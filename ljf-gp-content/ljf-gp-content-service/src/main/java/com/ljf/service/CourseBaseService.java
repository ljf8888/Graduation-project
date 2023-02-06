package com.ljf.service;

import com.ljf.dto.QueryCourseParamsDto;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;

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

}
