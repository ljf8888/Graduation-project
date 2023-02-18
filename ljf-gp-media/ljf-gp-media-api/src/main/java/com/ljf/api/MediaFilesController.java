package com.ljf.api;

import com.ljf.dto.QueryMediaParamsDto;
import com.ljf.dto.UploadFileParamsDto;
import com.ljf.dto.UploadFileResultDto;
import com.ljf.exception.myselfException;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.model.RestResponse;
import com.ljf.po.MediaFiles;
import com.ljf.service.MediaFilesService;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping(value = "/upload/coursefile1", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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
    /*
     * @description:
     * @author 李炯飞
     * @date: 2023/2/18 16:41
     * @param:
     * @return:
     **/
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {

        Long companyId = 1232141425L;
        return mediaFilesService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }
    /*
     * @description:
     * @author 李炯飞
     * @date: 2023/2/18 16:48
     * @param: [mediaId]
     * @return: RestResponse<String>
     **/
    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        //调用service查询文件的url

        MediaFiles mediaFiles = mediaFilesService.getFileById(mediaId);
        return RestResponse.success(mediaFiles.getUrl());
    }
}
