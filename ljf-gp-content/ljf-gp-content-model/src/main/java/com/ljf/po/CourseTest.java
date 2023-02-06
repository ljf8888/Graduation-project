package com.ljf.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * @Classname CourseTest
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:17
 * @Created by 李炯飞
 **/
@Data
@TableName("course_test")
public class CourseTest implements Serializable {

 private Long id;
 private String name;
 private Float price;
 private String qq;
 private String wechat;
 private String phone;
 private String description;
 private LocalDateTime createdate;

}
