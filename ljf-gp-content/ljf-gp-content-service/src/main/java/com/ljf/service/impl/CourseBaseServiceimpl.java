package com.ljf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljf.dao.*;
import com.ljf.dto.*;
import com.ljf.exception.myselfException;
import com.ljf.messageSDK.model.po.MqMessage;
import com.ljf.messageSDK.service.MqMessageService;
import com.ljf.model.PageParams;
import com.ljf.model.PageResult;
import com.ljf.po.*;
import com.ljf.service.CourseBaseService;
import com.ljf.service.TvdetailsService;
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
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    CourseBaseDao courseBaseDao;

    @Autowired
    CoursePublishDao coursePublishDao;

    @Autowired
    CourseMarketDao courseMarketDao;

    @Autowired
    CourseCategoryDao courseCategoryDao;

    @Autowired
    TvdetailsService tvdetailsService;


    /**
     * @description 根据课程id查询课程信息，包括基本信息和营销信息
     * @param courseId
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author Mr.M
     * @date 2022/10/8 16:10
     */
    
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
                myselfException.cast("课程为收费价格不能为空且必须大于0");

            }
        }
        //向数据库插入课程营销表
        int insert1 = courseMarketDao.insert(courseMarket);
        //插入成功返回1
//        int insert1 = courseMarketMapper.insert(courseMarket);
//        if(insert<1 && insert1<1){
        //只要有一个插入不成功抛出异常
        if(insert<1 || insert1<1){
            throw new myselfException("创建课程过程中出错");
        }
        return getCourseBaseInfo(courseId);
    }

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

        //基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        //教学计划
        List<TeachplanDto> teachplayTree = new TvdetailsServiceimpl().findTeachplanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplayTree);
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBase courseBase = courseBaseDao.selectById(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交不允许再次提交
        if ("202003".equals(auditStatus)) {
            myselfException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            myselfException.cast("不允许提交其它机构的课程。");
        }

        //课程图片是否填写
        if (org.apache.commons.lang3.StringUtils.isEmpty(courseBase.getPic())) {
            myselfException.cast("提交失败，请上传课程图片");
        }

        //查询课程计划信息
        List<TeachplanDto> teachplanTree = tvdetailsService.findTeachplanTree(courseId);

        //封装数据，基本信息、营销信息、课程计划信息、师资信息
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //查询基本信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        if (teachplanTree!=null){
            String teachplanTreeJson = JSON.toJSONString(teachplanTree);
            coursePublishPre.setTeachplan(teachplanTreeJson);
        }

        //课程营销信息
        CourseMarket courseMarket = courseMarketDao.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);

        //课程预发布表初始审核状态
        coursePublishPre.setStatus("202003");

        CoursePublishPre coursePublishPre1 = coursePublishDao.selectById(courseId);
        if (coursePublishPre1 == null) {
            coursePublishDao.insert(coursePublishPre);
        } else {
            coursePublishDao.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseDao.updateById(courseBase);
    }

    @Override
    public void publish(Long companyId, Long courseId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishDao.selectById(courseId);
        if(coursePublishPre == null){
            myselfException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            myselfException.cast("不允许提交其它机构的课程。");
        }


        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            myselfException.cast("操作失败，课程审核通过方可发布。");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishDao.deleteById(courseId);
    }

    @Override
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
    //保存课程发布信息
    private void saveCoursePublish(Long courseId) {

        //课程发布信息来源于预发布表
        CoursePublishPre coursePublishPre = coursePublishDao.selectById(courseId);

        CoursePublish coursePublish = new CoursePublish();
        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");//已发布

        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseDao.selectById(courseId);
        courseBase.setStatus("203002");//已发布
        courseBaseDao.updateById(courseBase);
    }

    //保存消息表
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage == null){
            myselfException.cast("添加消息记录失败");
        }
    }

}
