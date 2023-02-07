package com.ljf.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljf.dto.CourseCategoryTreeDto;
import com.ljf.dto.QueryCourseParamsDto;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Classname CourseBaseDao
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 16:30
 * @Created by 李炯飞
 */
@Mapper
public interface CourseBaseDao extends BaseMapper<CourseBase> {
    public List<CourseCategoryTreeDto> selectTreeNodes();
}
