package com.ljf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljf.dao.CourseBaseDao;
import com.ljf.dao.CourseCategoryDao;
import com.ljf.dao.CourseMarketDao;
import com.ljf.dto.AddCourseDto;
import com.ljf.dto.CourseBaseInfoDto;
import com.ljf.dto.CourseCategoryTreeDto;
import com.ljf.dto.QueryCourseParamsDto;
import com.ljf.exception.XueChengPlusException;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.CourseBase;
import com.ljf.po.CourseCategory;
import com.ljf.po.CourseMarket;
import com.ljf.service.CourseBaseService;
import jdk.nashorn.internal.runtime.Context;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Classname CourseBaseServiceimpl
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 17:10
 * @Created by 李炯飞
 */

@Service
public class CourseBaseServiceimpl implements CourseBaseService {

    @Autowired
    CourseBaseDao courseBaseDao;

    @Autowired
    CourseMarketDao courseMarketDao;

    @Autowired
    CourseCategoryDao courseCategoryDao;
    
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/3 17:14
     * @param: [params, queryCourseParamsDto]
     * @return: com.ljf.model.PageResult<com.ljf.po.CourseBase>
     **/
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //拼接查询条件
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());

        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());

        //根据课程发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //分页参数
        Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());


        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseDao.selectPage(page, queryWrapper);

        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();


        //准备返回数据 List<T> items, long counts, long page, long pageSize
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, params.getPageNo(), params.getPageSize());

        return courseBasePageResult;
    }
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:22
     * @param: []
     * @return: java.util.List<com.ljf.dto.CourseCategoryTreeDto>
     **/
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        //查询数据库得到的课程分类
        List<CourseCategoryTreeDto> courseCategoryTreeDtos =
                courseBaseDao.selectTreeNodes();
        System.out.println(courseCategoryTreeDtos+"aaa");
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();
        courseCategoryTreeDtos.stream().forEach(item->{
            mapTemp.put(item.getId(),item);
            //只将根节点的下级节点放入list-
            if(item.getParentid().equals("1")){
                categoryTreeDtos.add(item);
            }
            CourseCategoryTreeDto courseCategoryTreeDto =
                    mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new
                            ArrayList<CourseCategoryTreeDto>());
                }
//向节点的下级节点list加入节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
    /**
     * @description:
     * @author 李炯飞
     * @date: 2023/2/7 16:22
     * @param: [companyId, addCourseDto]
     * @return: com.ljf.dto.CourseBaseInfoDto
     **/
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //校验数据库合法性
        if(StringUtils.isBlank(dto.getName())){
            //抛出异常
//            throw  new RuntimeException("课程名称为空");
            XueChengPlusException.cast("课程名称为空");
//            XueChengPlusException.cast(CommonError.PARAMS_ERROR);
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }
        //向Course_base课程基本信息表表添加数据
        CourseBase courseBase = new CourseBase();
        //以上设置数据的方法可以拷贝,从源拷贝到目标
        BeanUtils.copyProperties(dto,courseBase);
        //设置机构id
        courseBase.setCompanyId(companyId);
        //创建时间
        courseBase.setCreateDate(LocalDateTime.now());
        //审核状态默认未提交
        courseBase.setAuditStatus("202002");
        //发布状态默认为未发布
        courseBase.setStatus("203001");
        //插入成功返回1
        int insert = courseBaseDao.insert(courseBase);
        //得到课程id
        Long courseId = courseBase.getId();

        //向数据库插入课程基本信息表，拿到课程的id
        //向课程营销表添加数据
        CourseMarket courseMarket = new CourseMarket();
        //两个对象的属性名一致，类型一样
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseId);
        //校验如果课程为收费，必须输入价格且大于0
        String charge = courseMarket.getCharge();
        if(charge.equals("201001")){
            if(courseMarket.getPrice()==null || courseMarket.getPrice().floatValue()<=0){
//                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
                XueChengPlusException.cast("课程为收费价格不能为空且必须大于0");

            }
        }
        //向数据库插入课程营销表
        int insert1 = courseMarketDao.insert(courseMarket);
        //插入成功返回1
//        int insert1 = courseMarketMapper.insert(courseMarket);
//        if(insert<1 && insert1<1){
        //只要有一个插入不成功抛出异常
        if(insert<1 || insert1<1){
            throw new XueChengPlusException("创建课程过程中出错");
        }
        return getCourseBaseInfo(courseId);
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        //课程基本信息
        CourseBase courseBase = courseBaseDao.selectById(courseId);
        //课程营销信息
        CourseMarket courseMarket = courseMarketDao.selectById(courseId);
        //组成要返回的数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //向分类的名称查询出来
        CourseCategory courseCategory = courseCategoryDao.selectById(courseBase.getMt());//一级分类
        courseBaseInfoDto.setMtName(courseCategory.getName());
        CourseCategory courseCategory2 = courseCategoryDao.selectById(courseBase.getSt());//二级分类
        courseBaseInfoDto.setStName(courseCategory2.getName());

        return courseBaseInfoDto;
    }


}
