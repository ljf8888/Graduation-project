package com.ljf.service;

import com.ljf.dto.*;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;

import java.util.List;

/**
 * @Classname CourseBaseService
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 16:31
 * @Created by 李炯飞
 */
public interface CourseBaseService {
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/3 17:12
     * @param: [params, queryCourseParamsDto]
     * @return: com.ljf.model.PageResult<com.ljf.po.CourseBase>
     **/
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto);
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:22
     * @param: []
     * @return: java.util.List<com.ljf.dto.CourseCategoryTreeDto>
     **/
    List<CourseCategoryTreeDto> queryTreeNodes();
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:22
     * @param: [companyId, addCourseDto]
     * @return: com.ljf.dto.CourseBaseInfoDto
     **/
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    void commitAudit(Long companyId, Long courseId);

    void publish(Long companyId, Long courseId);

    CourseBaseInfoDto getCourseBaseInfo(Long courseId);
}
