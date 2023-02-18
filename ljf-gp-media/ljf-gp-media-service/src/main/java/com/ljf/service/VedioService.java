package com.ljf.service;

import com.ljf.dto.UploadFileParamsDto;
import com.ljf.model.RestResponse;

/**
 * @Classname VedioService
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/18 0:07
 * @Created by 李炯飞
 */
public interface VedioService {
    /*
     * @description:
     * @author 李炯飞
     * @date: 2023/2/18 0:13
     * @param: [fileMd5]
     * @return: com.ljf.model.RestResponse<java.lang.Boolean>
     **/
    RestResponse<Boolean> checkFile(String fileMd5);

    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes);

    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);
}
