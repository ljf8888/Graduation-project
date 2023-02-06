package com.ljf.dto;

import lombok.Data;

/*
 * @Classname QueryCourseParamsDto
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:41
 * @Created by 李炯飞
 **/
@Data
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
