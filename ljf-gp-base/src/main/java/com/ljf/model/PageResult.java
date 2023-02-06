package com.ljf.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/*
 * @Classname PageResult
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:56
 * @Created by 李炯飞
 **/
 @Data
 @ToString
public class PageResult<T> {
  // 数据列表
  private List<T> items;

  //总记录数
  private long counts;

  //当前页码
  private long page;

  //每页记录数
  private long pageSize;

  public PageResult(List<T> items, long counts, long page, long pageSize) {
   this.items = items;
   this.counts = counts;
   this.page = page;
   this.pageSize = pageSize;
  }

  public PageResult(){

  }

 }
