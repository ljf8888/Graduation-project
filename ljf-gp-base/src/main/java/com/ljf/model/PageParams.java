package com.ljf.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname PageParams
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 14:53
 * @Created by 李炯飞
 */
@Data

public class PageParams {
    //当前页码默认值
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    //每页记录数默认值
    public static final long DEFAULT_PAGE_SIZE = 10L;

    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = DEFAULT_PAGE_CURRENT;

    //每页记录数默认值
    private Long pageSize = DEFAULT_PAGE_SIZE;

    public PageParams(){

    }

    public PageParams(long pageNo,long pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
