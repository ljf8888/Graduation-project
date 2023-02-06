package com.ljf.dto;

import com.ljf.po.CourseCategory;
import lombok.Data;

import java.util.List;

/*
 * @Classname CourseCategoryTreeDto
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:41
 * @Created by 李炯飞
 **/
 @Data
public class CourseCategoryTreeDto extends CourseCategory {
     //子分类
     List<CourseCategoryTreeDto> childrenTreeNodes;
}
