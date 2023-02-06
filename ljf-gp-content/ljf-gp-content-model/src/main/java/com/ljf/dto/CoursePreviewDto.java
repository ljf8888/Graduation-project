package com.ljf.dto;

import lombok.Data;

import java.util.List;

/*
 * @Classname CoursePreviewDto
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:41
 * @Created by 李炯飞
 **/
@Data
public class CoursePreviewDto {

    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息暂时不加...

}
