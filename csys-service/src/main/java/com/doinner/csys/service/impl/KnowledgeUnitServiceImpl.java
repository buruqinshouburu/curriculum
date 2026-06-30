package com.doinner.csys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.constant.CourseConstant;
import com.doinner.csys.constant.DomainExceptionConstant;
import com.doinner.csys.dao.CourseKnowledgePointMapper;
import com.doinner.csys.dao.CourseKnowledgeUnitMapper;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseRefKnowledgeUnitMapper;
import com.doinner.csys.domain.SourceDomain;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.entity.csys.po.CourseKnowledgePoint;
import com.doinner.csys.entity.csys.po.CourseKnowledgeUnit;
import com.doinner.csys.entity.csys.po.CourseRefKnowledgeUnit;
import com.doinner.csys.io.utils.ExcelUtils;
import com.doinner.csys.service.IKnowledgeUnitService;
import com.doinner.csys.utils.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识单元服务实现类
 * 新增：前端传全部数据（id都为null），统一保存
 * 修改：前端传全部数据（包含id），后端根据id判断是新增还是更新
 * 查询：根据课程Id进行查询
 */
@Service
public class KnowledgeUnitServiceImpl implements IKnowledgeUnitService {
    @Autowired
    private  CourseKnowledgeUnitMapper knowledgeUnitMapper;
    @Autowired
    private  CourseKnowledgePointMapper knowledgePointMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseRefKnowledgeUnitMapper courseRefKnowledgeUnitMapper;


    @Override
    public List<CourseKnowledgeUnitVo> listByCourseId(Long courseId) {
        List<CourseRefKnowledgeUnit> courseRefKnowledgeUnits = courseRefKnowledgeUnitMapper.selectByCourseId(courseId);
        if(ObjectUtils.isEmpty(courseRefKnowledgeUnits)){
            return null;
        }
        // 查询知识单元列表（按sort排序）
        List<CourseKnowledgeUnit> units = knowledgeUnitMapper.selectByIds(courseRefKnowledgeUnits.stream().map(c->c.getCourseUnitId()).collect(Collectors.toList()));

        if (units.isEmpty()) {
            return new ArrayList<>();
        }
        units.sort(Comparator.comparing(CourseKnowledgeUnit::getSort));
        // 获取所有知识单元ID
        List<Long> unitIds = units.stream()
                .map(CourseKnowledgeUnit::getId)
                .collect(Collectors.toList());

        // 批量查询知识点（按unitId和sort排序）
        List<CourseKnowledgePoint> points = knowledgePointMapper.selectByIds(unitIds);

        // 按知识单元ID分组知识点
        Map<Long, List<CourseKnowledgePoint>> pointsMap = points.stream()
                .collect(Collectors.groupingBy(CourseKnowledgePoint::getUnitId));

        // 转换为DTO
        return units.stream()
                .map(unit -> {
                    CourseKnowledgeUnitVo dto = new CourseKnowledgeUnitVo();
                    BeanUtils.copyProperties(unit, dto);
                    // 设置知识点列表
                    List<CourseKnowledgePoint> unitPoints = pointsMap.getOrDefault(unit.getId(), new ArrayList<>());
                    List<CourseKnowledgePointVo> pointVos = unitPoints.stream()
                            .map(point -> {
                                CourseKnowledgePointVo pointVo = new CourseKnowledgePointVo();
                                BeanUtils.copyProperties(point, pointVo);
                                return pointVo;
                            })
                            .collect(Collectors.toList());
                    dto.setKnowledgePoints(pointVos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveBatch(CourseKnowledgeVo courseKnowledgeVo) {
        //查看是否具有修改权限
        CourseVo courseVo = courseMapper.selectCourseById(courseKnowledgeVo.getCourseId());
        UserUtils.checkDataPermission(courseVo);
        //清空现有知识单元知识点
        List<CourseKnowledgeUnit> courseKnowledgeUnits = knowledgeUnitMapper.selectByCourseId(courseKnowledgeVo.getCourseId());
        List<Long> unitIds = courseKnowledgeUnits.stream().map(c -> c.getId()).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(unitIds)){
            courseRefKnowledgeUnitMapper.deleteByCourseId(courseKnowledgeVo.getCourseId());
            knowledgePointMapper.deleteByUnitIds(unitIds);
            knowledgeUnitMapper.deleteBatchIds(unitIds);
        }
        //重新添加
        saveCourseKnowLedge(courseKnowledgeVo);
    }

    private void saveCourseKnowLedge(CourseKnowledgeVo courseKnowledgeVo) {
        for (CourseKnowledgeUnitVo courseKnowledgeUnitVo : courseKnowledgeVo.getUnitList()) {
            CourseKnowledgeUnit courseKnowledgeUnit = new CourseKnowledgeUnit();
            BeanUtils.copyProperties(courseKnowledgeUnitVo, courseKnowledgeUnit);
            UserUtils.reflash(courseKnowledgeUnit);
            knowledgeUnitMapper.insert(courseKnowledgeUnit);
            saveCourseKnowledgeRef(courseKnowledgeVo, courseKnowledgeUnit);
            saveKnowledgePoint(courseKnowledgeUnitVo, courseKnowledgeUnit);
        }
        //更新绑定状态
        courseMapper.updateBuildStatus(courseKnowledgeVo.getCourseId(), CourseConstant.CUR_STATUS_YES);
    }

    private void saveKnowledgePoint(CourseKnowledgeUnitVo courseKnowledgeUnitVo, CourseKnowledgeUnit courseKnowledgeUnit) {
        List<CourseKnowledgePoint> pointList = new ArrayList<>();
        int pointSort=1;
        for (CourseKnowledgePointVo knowledgePoint : courseKnowledgeUnitVo.getKnowledgePoints()) {
            CourseKnowledgePoint courseKnowledgePoint = new CourseKnowledgePoint();
            BeanUtils.copyProperties(knowledgePoint, courseKnowledgePoint);
            courseKnowledgePoint.setUnitId(courseKnowledgeUnit.getId());
            if(ObjectUtils.isEmpty(courseKnowledgePoint.getSort())){
                courseKnowledgePoint.setSort(pointSort++);
            }
            UserUtils.reflash(courseKnowledgePoint);
            pointList.add(courseKnowledgePoint);
        }
        knowledgePointMapper.insertBatch(pointList);
    }

    private void saveCourseKnowledgeRef(CourseKnowledgeVo courseKnowledgeVo, CourseKnowledgeUnit courseKnowledgeUnit) {
        CourseRefKnowledgeUnit courseRefKnowledgeUnit = new CourseRefKnowledgeUnit();
        courseRefKnowledgeUnit.setCourseUnitId(courseKnowledgeUnit.getId());
        courseRefKnowledgeUnit.setCourseId(courseKnowledgeVo.getCourseId());
        courseRefKnowledgeUnitMapper.insert(courseRefKnowledgeUnit);
    }

    @Override
    public void exportTemplate(HttpServletResponse response) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("template/knowledge/CourseKnowLedge.xlsx");
        ExcelUtils.exportTemplate(inputStream,response);
    }

    @Override
    @Transactional
    public Message exportInTemplate(MultipartFile file, Long courseId, Integer type) {
        //查看是否有导入权限 非课程创建人无法导入
        CourseVo courseVo = courseMapper.selectCourseById(courseId);
        UserUtils.checkDataPermission(courseVo);
        //导入
        List<TreeTableVo> treeTableVos = ExcelUtils.readTreeTable(file,   0 , 1);
        if (CollectionUtils.isNotEmpty(treeTableVos)){
            List<CourseKnowledgeTreeVo> treeVos = treeTableVos.parallelStream().map(TreeTableVo::toCourseKnowledgeTreeVo)
                    .filter(k->ObjectUtils.isNotEmpty(k.getName())).collect(Collectors.toList());
            if(ObjectUtils.isEmpty(treeVos)){
                return Message.error(DomainExceptionConstant.DATA_IMPORT_TEMPLATE_ERROR);
            }
            CourseKnowledgeVo courseKnowledgeVo = new CourseKnowledgeVo();
            courseKnowledgeVo.setCourseId(courseId);
            List<CourseKnowledgeUnitVo> courseKnowledgeUnitVos = new ArrayList<>();
            int unitSort=1;
            //查询现有知识体系的序号
            for (CourseKnowledgeTreeVo treeVo : treeVos) {
                CourseKnowledgeUnitVo knowledgeUnitVo = new CourseKnowledgeUnitVo();
                knowledgeUnitVo.setName(treeVo.getName());
                knowledgeUnitVo.setSort(unitSort);
                unitSort++;
                setCourseKnowledgePoint(treeVo, knowledgeUnitVo);
                courseKnowledgeUnitVos.add(knowledgeUnitVo);
            }
            courseKnowledgeVo.setUnitList(courseKnowledgeUnitVos);
            if(type==1){
                //追加
                List<CourseKnowledgeUnit> courseKnowledgeUnits = knowledgeUnitMapper.selectByCourseId(courseId);
                if(ObjectUtils.isNotEmpty(courseKnowledgeUnits)){
                    unitSort=courseKnowledgeUnits.get(courseKnowledgeUnits.size()-1).getSort()+1;
                }
                for (CourseKnowledgeUnitVo courseKnowledgeUnitVo : courseKnowledgeVo.getUnitList()) {
                    courseKnowledgeUnitVo.setSort(unitSort++);
                }
                saveCourseKnowLedge(courseKnowledgeVo);
            }else{
                //覆盖
                saveBatch(courseKnowledgeVo);
            }
        }
        return Message.success();
    }

    private  void setCourseKnowledgePoint(CourseKnowledgeTreeVo treeVo, CourseKnowledgeUnitVo knowledgeUnitVo) {
        if(ObjectUtils.isNotEmpty(treeVo.getChildren())){
            ArrayList<CourseKnowledgePointVo> courseKnowledgePointVos = new ArrayList<>();
            int pointSort=1;
            for (CourseKnowledgeTreeVo child : treeVo.getChildren()) {
                CourseKnowledgePointVo courseKnowledgePointVo = new CourseKnowledgePointVo();
                courseKnowledgePointVo.setName(child.getName());
                courseKnowledgePointVo.setSort(pointSort);
                pointSort++;
                courseKnowledgePointVos.add(courseKnowledgePointVo);
            }
            knowledgeUnitVo.setKnowledgePoints(courseKnowledgePointVos);
        }
    }
}
