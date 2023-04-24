package com.ljf.api;

import com.ljf.dto.*;
import com.ljf.exception.ValidationGroups;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;
import com.ljf.service.CourseBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
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

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 22L;
        courseBaseService.commitAudit(companyId, courseId);
    }
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId) {

        Long companyId = 22L;
        courseBaseService.publish(companyId,courseId);
    }

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {

        //查询数据
        CoursePreviewDto coursePreviewInfo = courseBaseService.getCoursePreviewInfo(courseId);


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("/templates/course_template.ftl");
        return modelAndView;
    }

}
