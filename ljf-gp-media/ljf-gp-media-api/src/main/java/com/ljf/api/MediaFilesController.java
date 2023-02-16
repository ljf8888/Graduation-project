package com.ljf.api;

import com.ljf.dto.UploadFileParamsDto;
import com.ljf.dto.UploadFileResultDto;
import com.ljf.exception.myselfException;
import com.ljf.service.MediaFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname MediaController
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/15 14:30
 * @Created by 李炯飞
 */
@RestController
public class MediaFilesController {
    @Autowired
    MediaFilesService mediaFilesService;

    @PostMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
                                      @RequestParam(value = "folder",required=false) String folder,
                                      @RequestParam(value= "objectName",required=false) String objectName){
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        String contentType = filedata.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        uploadFileParamsDto.setFileSize(filedata.getSize());//文件大小
        if (contentType.indexOf("image") >= 0) {
            //是个图片
            uploadFileParamsDto.setFileType("001001");
        } else {
            uploadFileParamsDto.setFileType("001003");
        }
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());//文件名称
        UploadFileResultDto uploadFileResultDto = null;
        try {
            uploadFileResultDto = mediaFilesService.uploadFile(companyId, uploadFileParamsDto, filedata.getBytes(), folder, objectName);
        } catch (Exception e) {
            myselfException.cast("上传文件过程中出错");
        }

        return uploadFileResultDto;
    }
}
