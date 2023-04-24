package com.ljf.service.impl;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.ljf.dao.MediaFilesMapper;
import com.ljf.dto.UploadFileParamsDto;
import com.ljf.exception.myselfException;
import com.ljf.model.RestResponse;
import com.ljf.po.MediaFiles;
import com.ljf.service.VedioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;

/**
 * @Classname VedioserviceImpl
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/18 0:17
 * @Created by 李炯飞
 */
@Service
public class VedioserviceImpl implements VedioService {
    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesMapper mediaFilesMapper;


    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //在文件表存在，并且在文件系统存在，此文件才存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles==null){
            return RestResponse.success(false);
        }
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if(inputStream==null){
                //文件不存在
                return RestResponse.success(false);
            }
        }catch (Exception e){
            //文件不存在
            return RestResponse.success(false);
        }
        //文件已存在
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunk) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        //查询文件系统分块文件是否存在
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("video").object(chunkFilePath).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if(inputStream==null){
                //文件不存在
                return RestResponse.success(false);
            }
        }catch (Exception e){
            //文件不存在
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            addMediaFilesToMinIO(bytes, "video", chunkFilePath);
            //上传成功
            return RestResponse.success(true);
        } catch (Exception e) {

            return RestResponse.validfail(false,"上传分块失败");
        }
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //下载分块
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);

        //得到合并后文件的扩展名
        String filename = uploadFileParamsDto.getFilename();
        //扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        File tempMergeFile = null;
        try {
            try {
                //创建一个临时文件作为合并文件
                tempMergeFile = File.createTempFile("'merge'", extension);
            } catch (IOException e) {
                myselfException.cast("创建临时合并文件出错");
            }

            //创建合并文件的流对象
            try( RandomAccessFile raf_write  =new RandomAccessFile(tempMergeFile, "rw")) {
                byte[] b = new byte[1024];
                for (File file : chunkFiles) {
                    //读取分块文件的流对象
                    try(RandomAccessFile raf_read = new RandomAccessFile(file, "r");) {
                        int len = -1;
                        while ((len = raf_read.read(b)) != -1) {
                            //向合并文件写数据
                            raf_write.write(b, 0, len);
                        }
                    }

                }
            } catch (IOException e) {
                myselfException.cast("合并文件过程出错");
            }


            //校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    myselfException.cast("合并文件校验不通过");
                }
            } catch (IOException e) {
                myselfException.cast("合并文件校验出错");
            }


            //拿到合并文件在minio的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);
            //将合并后的文件上传到文件系统
            addMediaFilesToMinIO(tempMergeFile.getAbsolutePath(), "video", mergeFilePath);

            //将文件信息入库保存
            uploadFileParamsDto.setFileSize(tempMergeFile.length());//合并文件的大小
            addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, "video", mergeFilePath);

            return RestResponse.success(true);
        }finally {
            //删除临时分块文件
            if(chunkFiles!=null){
                for (File chunkFile : chunkFiles) {
                    if(chunkFile.exists()){
                        chunkFile.delete();
                    }
                }
            }
            //删除合并的临时文件
            if(tempMergeFile!=null){
                tempMergeFile.delete();
            }


        }
    }



    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件数组
        File[] chunkFiles = new File[chunkTotal];
        //开始下载
        for (int i = 0; i < chunkTotal; i++) {
            //分块文件的路径
            String chunkFilePath = chunkFileFolderPath + i;
            //分块文件
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk", null);
            } catch (IOException e) {
                e.printStackTrace();
                myselfException.cast("创建分块临时文件出错"+e.getMessage());
            }

            //下载分块文件
            downloadFileFromMinIO(chunkFile, "video", chunkFilePath);
            chunkFiles[i] = chunkFile;

        }

        return chunkFiles;
    }

    //根据桶和文件路径从minio下载文件
    public File downloadFileFromMinIO(File file,String bucket,String objectName){

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        try(
                InputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream =new FileOutputStream(file);
        ) {
            IOUtils.copy(inputStream,outputStream);
            return file;
        }catch (Exception e){
            e.printStackTrace();
            myselfException.cast("查询分块文件出错");
        }
        return null;
    }


    private String getFilePathByMd5(String fileMd5, String extension) {
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +extension;
    }


    /*
     * @description:得到分块文件的目录
     * @author 李炯飞
     * @date: 2023/2/18 0:35
     * @param: [fileMd5]
     * @return: java.lang.String
     **/
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
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

    private String getMimeTypeByextension(String extension) {
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
}
