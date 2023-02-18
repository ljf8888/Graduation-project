package com.ljf.service.impl;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.ljf.dao.MediaFilesMapper;
import com.ljf.dto.UploadFileParamsDto;
import com.ljf.dto.UploadFileResultDto;
import com.ljf.exception.myselfException;
import com.ljf.po.MediaFiles;
import com.ljf.service.MediaFilesService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Classname MediaFilesServiceImpl
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/15 14:49
 * @Created by 李炯飞
 */
@Service
public class MediaFilesServiceImpl implements MediaFilesService {
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaFilesService mediafilesproxy;

    @Autowired
    MinioClient minioClient;

    //普通文件存储的桶
    @Value("${minio.bucket.files}")
    private String bucket_files;


    /*
     * @description:
     * @author 李炯飞
     * @date: 2023/2/16 0:12
     * @param: [companyId, uploadFileParamsDto, bytes, folder, objectName]
     * @return: com.ljf.dto.UploadFileResultDto
     **/
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {

        String fileMd5 = DigestUtils.md5Hex(bytes);

        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径 按年月日生成，
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder = folder + "/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();

        if (StringUtils.isEmpty(objectName)) {
            //如果objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }

        objectName = folder + objectName;
        try {

            addMediaFilesToMinIO(bytes, bucket_files, objectName);

            MediaFiles mediaFiles = mediafilesproxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
            //准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    /*
     * @description:根据日期拼接目录
     * @author 李炯飞
     * @date: 2023/2/16 0:12
     * @param: [date, year, month, day]
     * @return: java.lang.String
     **/
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    /*
     * @description:将文件上传到分布式文件系统
     * @author 李炯飞
     * @date: 2023/2/16 0:17
     * @param: [bytes, bucket, objectName]
     * @return: void
     **/
    public void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName) {

        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流

        if (objectName.indexOf(".") >= 0) {
            //取objectName中的扩展名
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }

        }


        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minio
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            myselfException.cast("上传文件到文件系统出错");
        }
    }

     /* @description:将文件信息入库
     * @author 李炯飞
     * @date: 2023/2/16 0:24
     * @param: [companyId, fileId, uploadFileParamsDto, bucket, objectName]
     * @return: com.ljf.po.MediaFiles
     **/
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //保存到数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();

            //封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);

            //获取扩展名
            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if(StringUtils.isNotEmpty(filename) && filename.indexOf(".")>=0){
                extension = filename.substring(filename.lastIndexOf("."));
            }
            //媒体类型
            String mimeType = getMimeTypeByextension(extension);
            //图片、mp4视频可以设置URL
            if(mimeType.indexOf("image")>=0 || mimeType.indexOf("mp4")>=0){
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }

            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");


            //插入文件表
            mediaFilesMapper.insert(mediaFiles);

            //对avi视频添加到待处理任务表
//            if(mimeType.equals("video/x-msvideo")){
//
//                MediaProcess mediaProcess = new MediaProcess();
//                BeanUtils.copyProperties(mediaFiles,mediaProcess);
//                //设置一个状态
//                mediaProcess.setStatus("1");//未处理
//                mediaProcessMapper.insert(mediaProcess);
//            }



        }
        return mediaFiles;
    }
    /*
     * @description:根据扩展名拿匹配的媒体类型
     * @author 李炯飞
     * @date: 2023/2/16 0:28
     * @param: [extension]
     * @return: java.lang.String
     **/
    private String getMimeTypeByextension(String extension){
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return  contentType;
    }

    //将文件上传到文件系统
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
        } catch (Exception e) {
            myselfException.cast("文件上传到文件系统失败");
        }
    }
}
