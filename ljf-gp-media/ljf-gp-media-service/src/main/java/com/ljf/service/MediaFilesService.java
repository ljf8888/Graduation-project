package com.ljf.service;

import com.ljf.dto.UploadFileParamsDto;
import com.ljf.dto.UploadFileResultDto;
import com.ljf.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Classname MediaFilesService
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/15 14:48
 * @Created by 李炯飞
 */
public interface MediaFilesService {
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    /* @description:将文件信息入库
    * @author 李炯飞
    * @date: 2023/2/16 0:24
    * @param: [companyId, fileId, uploadFileParamsDto, bucket, objectName]
    * @return: com.ljf.po.MediaFiles
    **/
   @Transactional
   MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);
}
