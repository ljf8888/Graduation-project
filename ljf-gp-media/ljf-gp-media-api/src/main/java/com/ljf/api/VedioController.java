package com.ljf.api;

import com.ljf.dto.UploadFileParamsDto;
import com.ljf.model.RestResponse;
import com.ljf.service.VedioService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname VedioController
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/18 0:06
 * @Created by 李炯飞
 */
@RestController
public class VedioController {
    @Autowired
    VedioService vedioService;
    /*
     * @description:文件上传前检查文件
     * @author 李炯飞
     * @date: 2023/2/18 0:10
     * @param: [fileMd5]
     * @return: RestResponse<Boolean>
     **/
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(
            @RequestParam("fileMd5") String fileMd5
    ) throws Exception {
        return vedioService.checkFile(fileMd5);
    }

    /*
     * @description:分块文件上传前的检测
     * @author 李炯飞
     * @date: 2023/2/18 0:27
     * @param: [fileMd5, chunk]
     * @return: com.ljf.model.RestResponse<java.lang.Boolean>
     **/

    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) throws Exception {
        return vedioService.checkChunk(fileMd5,chunk);
    }
    /*
     * @description:上传分块文件
     * @author 李炯飞
     * @date: 2023/2/18 0:39
     * @param: [file, fileMd5, chunk]
     * @return: com.ljf.model.RestResponse
     **/
    @ApiOperation(value = "")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        return vedioService.uploadChunk(fileMd5,chunk,file.getBytes());

    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType("001002");//视频
        uploadFileParamsDto.setTags("课程视频");
        return vedioService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);

    }

}
