package com.ljf.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
 * @Classname BindTeachplanMediaDto
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:40
 * @Created by 李炯飞
 **/
 @Data
 @ApiModel(value="BindTeachplanMediaDto", description="教学计划-媒资绑定提交数据")
 public class BindTeachplanMediaDto {

  @ApiModelProperty(value = "媒资文件id", required = true)
  private String mediaId;

  @ApiModelProperty(value = "媒资文件名称", required = true)
  private String fileName;

  @ApiModelProperty(value = "课程计划标识", required = true)
  private Long teachplanId;


 }
