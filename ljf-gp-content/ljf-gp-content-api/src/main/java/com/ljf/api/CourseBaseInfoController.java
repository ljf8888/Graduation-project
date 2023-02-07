package com.ljf.api;

import com.ljf.dto.CourseCategoryTreeDto;
import com.ljf.dto.QueryCourseParamsDto;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;
import com.ljf.service.CourseBaseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseBaseService.queryTreeNodes();
    }

}
