package com.ljf.dto;


import com.ljf.po.Teachplan;
import com.ljf.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/*
 * @Classname TeachplanDto
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:41
 * @Created by 李炯飞
 **/
@Data
public class TeachplanDto extends Teachplan {

   //课程计划关联的媒资信息
   TeachplanMedia teachplanMedia;

    //子目录
   List<TeachplanDto> teachPlanTreeNodes;
}
