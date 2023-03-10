package com.ljf.api;

import com.ljf.dto.AddCourseDto;
import com.ljf.dto.CourseBaseInfoDto;
import com.ljf.dto.CourseCategoryTreeDto;
import com.ljf.dto.QueryCourseParamsDto;
import com.ljf.exception.ValidationGroups;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;
import com.ljf.service.CourseBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname CourseBaseInfoController
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:48
 * @Created by 李炯飞
 */
@RequestMapping("/content")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseService courseBaseService;
    
    /*
     * @description:
     * @author 李炯飞
     * @date: 2023/2/3 16:30
     * @param: [params, queryCourseParamsDto]
     * @return: com.ljf.model.PageResult<com.ljf.po.CourseBase>
     **/
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        PageResult<CourseBase> courseBasePageResult = courseBaseService.queryCourseBaseList(params, queryCourseParamsDto);
        return  courseBasePageResult;
    }
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:18
     * @param: []
     * @return: java.util.List<com.ljf.dto.CourseCategoryTreeDto>
     **/
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseBaseService.queryTreeNodes();
    }

    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:22
     * @param: [addCourseDto]
     * @return: com.ljf.dto.CourseBaseInfoDto
     **/
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto){
        Long companyId = 22L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

}
