package com.ljf.dto;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ljf.po.Teachplan;
import com.ljf.po.TeachplanMedia;
import lombok.Data;

import java.time.LocalDateTime;
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

    TeachplanMedia teachplanMedia;
    //子目录
   List<TeachplanDto> teachPlanTreeNodes;
}
