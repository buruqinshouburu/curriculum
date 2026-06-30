package com.doinner.csys.service.impl;

import com.doinner.csys.dao.*;
import com.doinner.csys.domain.*;
import com.doinner.csys.domain.vo.*;
import com.doinner.csys.service.KnowledgeCheckLogService;
import com.doinner.csys.utils.DuplicateCheckUtil;
import com.doinner.common.core.utils.PageUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class KnowledgeCheckLogServiceImpl implements KnowledgeCheckLogService {

    @Resource
    private KnowledgeCheckLogMapper knowledgeCheckLogMapper;
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private KnowledgeNoCheckLogMapper knowledgeNoCheckLogMapper;
    @Resource
    private KnowledgeChekTotalMapper knowledgeChekTotalMapper;
    @Resource
    private CourseRefKeUnitMapper courseRefKeUnitMapper;
    @Resource
    private TrainingSchemeRefCourseMapper trainingSchemeRefCourseMapper;

    @Resource
    private SourceDomainRefUnitMapper sourceDomainRefUnitMapper;
    @Resource
    private CourseRefKnowledgeUnitMapper courseRefKnowledgeUnitMapper;


    @Override
    public void knowledgePointCheck(List<Long> courseIds) {
        if (ObjectUtils.isEmpty(courseIds)) {
            return;
        }
        String key = getStringKey(courseIds);
        if (ObjectUtils.isEmpty(key)) {
            return;
        }
        if (!setNX(key)) {
            System.out.println("已经在使用");
            return;
        }
        try {
            long startTime = System.currentTimeMillis();
            //删除已有的查重数据
            knowledgeCheckLogMapper.deleteByCourseId(courseIds);
            knowledgeNoCheckLogMapper.deleteByCourseId(courseIds);
            knowledgeChekTotalMapper.deleteByCourseId(courseIds);
            List<KnowledgeCheckLogVo> knowLedgeCheckLogVoList = new ArrayList<>();
            //过滤没有知识单元的id
            List<KnowledgePointVoMaps> voList = knowledgePointMapper.checkCoursePoints(courseIds);
            List<Long> filterIds = voList.stream().map(KnowledgePointVoMaps::getcId).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(filterIds)) {
                return;
            }
            //获取id组合
            List<KnowledgeCheckLogVo> idCompose = getIdCompose(filterIds);
            //过滤已有的组合数据
//            List<KnowledgeCheckLogVo> filterList = filterIdCompose(idCompose, filterIds);
//            if (ObjectUtils.isEmpty(idCompose)) {
//                percentage(0, 1, 1, 3);
//            }
            //根据组合查询数据
            checkLogRe(idCompose, knowLedgeCheckLogVoList);
            if (ObjectUtils.isEmpty(idCompose)) {
                percentage(0, 1, 2, 3);
            }
            //写入数据库
            insetLogList(knowLedgeCheckLogVoList);
            if (ObjectUtils.isEmpty(knowLedgeCheckLogVoList)) {
                percentage(0, 1, 3, 3);
            }
            System.out.println(System.currentTimeMillis() - startTime);
            redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            redisTemplate.delete(key);
        }
    }


    @Override
    public KnowledgeChekTotalVo selectCheckPointLog(List<Long> courseIds) {
        KnowledgeChekTotalVo vo = getTotal(courseIds);
        PageUtils.startPage();
        if (ObjectUtils.isNotEmpty(courseIds)) {
            List<KnowledgeCheckLog> knowledgeCheckLogList = knowledgeCheckLogMapper.selectKnowledgeCheckLogListByCourses(courseIds);
            vo.setArtificialCheckResultList(PageDataTable.success(knowledgeCheckLogList));
        }
        return vo;
    }


    @Override
    public KnowledgeChekTotalVo selectCheckPointLogNoList(List<Long> courseIds) {
        return getTotal(courseIds);
    }


    @Override
    public List<KnowledgeCheckLog> selectCheckPointLogListBySchemeId(List<Long> courseIds) {
        if (ObjectUtils.isNotEmpty(courseIds)) {
            return knowledgeCheckLogMapper.selectKnowledgeCheckLogListByCourses(courseIds);
        }
        return List.of(new KnowledgeCheckLog());
    }

    @Override
    public void similePoint(KnowledgeCheckLog KnowledgeCheckLog) {
        knowledgeCheckLogMapper.updateKnowledgeCheckLog(KnowledgeCheckLog);
    }

    @Override
    public List<Long> getCourseIdsByCollegeId(Long collegeId) {
        CourseVo course = new CourseVo();
        course.setCollegeId(collegeId);
        List<Course> courseList = courseMapper.selectCourseList(course);
        return courseList.stream().map(Course::getId).collect(Collectors.toList());
    }

    //根据培养方案ID 获取课程
    @Override
    public List<Long> getCourseIdsBySchemeId(Long schemeId) {
        TrainingSchemeRefCourse trainingSchemeRefCourse = new TrainingSchemeRefCourse();
        trainingSchemeRefCourse.setSchemeId(schemeId);
        List<TrainingSchemeRefCourse> courseList = trainingSchemeRefCourseMapper.selectTrainingSchemeRefCourseList(trainingSchemeRefCourse);
        return courseList.stream().map(TrainingSchemeRefCourse::getCourseId).collect(Collectors.toList());
    }

    /**
     * 获取统计信息
     */
    public KnowledgeChekTotalVo getTotal(List<Long> courseIds) {
        if (ObjectUtils.isEmpty(courseIds)) {
            return null;
        }
        List<KnowledgeCheckLog> sPointList = knowledgeCheckLogMapper.countSPoint(courseIds);
        List<KnowledgeCheckLog> tPointList = knowledgeCheckLogMapper.countTPoint(courseIds);
        Set<String> strings = new CopyOnWriteArraySet<>();
        for (KnowledgeCheckLog svo : sPointList) {
            Long cId = svo.getSourceCourseId();
            Long uId = svo.getSourceUnitId();
            Long pId = svo.getSourcePointId();
            String point = cId + ":" + uId + ":" + pId;
            strings.add(point);
        }
        for (KnowledgeCheckLog tvo : tPointList) {
            Long cId = tvo.getTargetCourseId();
            Long uId = tvo.getTargetUnitId();
            Long pId = tvo.getTargetPointId();
            String point = cId + ":" + uId + ":" + pId;
            strings.add(point);
        }
        // todo 查询新的知识单元数据
        KnowledgeChekTotal chekTotal = knowledgeChekTotalMapper.selectTotalBySourceCourseIds(courseIds);
        KnowledgeChekTotalVo vo = new KnowledgeChekTotalVo();
        if (null != chekTotal) {
            vo.setTotalPointNum(chekTotal.getTotalPointNum());
            vo.setTotalUnitNum(chekTotal.getTotalUnitNum());
            vo.setTotalCurriculumNum(courseIds.size());
        }
        vo.setRepeatNum((long) strings.size());
        return vo;
    }


    /**
     * 查看相似记录 去除相似数据
     */
    private List<KnowledgeCheckLogVo> filterIdCompose(List<KnowledgeCheckLogVo> idCompose, List<Long> courseIds) {
        List<KnowledgeCheckLogVo> newIdCompose = new ArrayList<>();
        List<KnowledgeCheckLog> checkLogList = knowledgeCheckLogMapper.selectBySourceCourseIdList(courseIds);
        List<KnowledgeNoCheckLog> noCheckLogList = knowledgeNoCheckLogMapper.selectBySourceCourseIdList(courseIds);
        for (int i = 0; i < idCompose.size(); i++) {
            percentage(i + 1, idCompose.size(), 1, 3);
            KnowledgeCheckLogVo knowLedgeCheckLogVo = idCompose.get(i);
            Long sourceCourseId = knowLedgeCheckLogVo.getSourceCourseId();
            Long targetCourseId = knowLedgeCheckLogVo.getTargetCourseId();
            boolean exists = false;
            //查询是否有log
            for (KnowledgeCheckLog knowledgeCheckLog : checkLogList) {
                Long hasSCourseId = knowledgeCheckLog.getSourceCourseId();
                Long hasTCourseId = knowledgeCheckLog.getTargetCourseId();
                if (sourceCourseId.equals(hasSCourseId) && targetCourseId.equals(hasTCourseId)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                continue;
            }
            //查询是否在noLog
            for (KnowledgeNoCheckLog knowledgeNoCheckLog : noCheckLogList) {
                Long noSCourseId = knowledgeNoCheckLog.getSourceCourseId();
                Long noTCourseId = knowledgeNoCheckLog.getTargetCourseId();
                if (sourceCourseId.equals(noSCourseId) && targetCourseId.equals(noTCourseId)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                continue;
            }
            newIdCompose.add(knowLedgeCheckLogVo);
        }
        return newIdCompose;
    }

    /**
     * 统计知识领域下知识单元知识点数量
     */
    public synchronized void checkCourseTotal(Long courseId, List<KnowledgePointVo> pointVoList) {
        KnowledgeChekTotal courseTotal = knowledgeChekTotalMapper.selectBySourceCourseId(courseId);
        if (null == courseTotal) {
            addCourseTotal(courseId, pointVoList);
        }
    }


    /**
     * 添加统计课程
     */
    public void addCourseTotal(Long courseId, List<KnowledgePointVo> pointList) {
        List<Long> unitIds = courseRefKnowledgeUnitMapper.totalUnitByCourseId(courseId);
        KnowledgeChekTotal chekTotal = new KnowledgeChekTotal();
        chekTotal.setSourceCourseId(courseId);
        chekTotal.setTotalPointNum((long) pointList.size());
        chekTotal.setTotalUnitNum((long) unitIds.size());
        knowledgeChekTotalMapper.insertKnowledgeChekTotal(chekTotal);
    }

    /**
     * 添加统计课程
     */
    public void addTotal(Long courseId, List<KnowledgePointVo> pointList) {
        List<Long> unitIds = courseRefKeUnitMapper.totalUnitByCourseId(courseId);
        KnowledgeChekTotal chekTotal = new KnowledgeChekTotal();
        chekTotal.setSourceCourseId(courseId);
        chekTotal.setTotalPointNum((long) pointList.size());
        chekTotal.setTotalUnitNum((long) unitIds.size());
        knowledgeChekTotalMapper.insertKnowledgeChekTotal(chekTotal);
    }

    /**
     * 查重
     */
    private void getCheckLog(List<KnowledgePointVo> sourceList, List<KnowledgePointVo> targetList, List<KnowledgeCheckLogVo> knowLedgeCheckLogVoList, Long sourceId, Long targetId) {
        List<KnowledgeCheckLogVo> newList = new ArrayList<>();
        for (KnowledgePointVo knowledgePointVo : sourceList) {
            for (KnowledgePointVo pointVo : targetList) {
                String sName = knowledgePointVo.getName();
                String tName = pointVo.getName();
                double distancePercent = DuplicateCheckUtil.getDistancePercent(sName, tName);
                if (distancePercent > 0.9) {
                    newList.add(new KnowledgeCheckLogVo(knowledgePointVo, pointVo));
                }
            }
        }
        if (0 == newList.size()) {
            KnowledgeNoCheckLog knowledgeNoCheckLog = new KnowledgeNoCheckLog();
            knowledgeNoCheckLog.setSourceCourseId(sourceId);
            knowledgeNoCheckLog.setTargetCourseId(targetId);
            knowledgeNoCheckLogMapper.insertKnowledgeNoCheckLog(knowledgeNoCheckLog);
        } else {
            knowLedgeCheckLogVoList.addAll(newList);
        }

    }

    /**
     * 获取课程id组合
     */
    private static List<KnowledgeCheckLogVo> getIdCompose(List<Long> sourceDomainIds) {
        sourceDomainIds = sourceDomainIds.stream().sorted().collect(Collectors.toList());
        List<KnowledgeCheckLogVo> knowLedgeCheckLogVoList = new ArrayList<>();
        for (int i = 0; i < sourceDomainIds.size(); i++) {
            for (int o = i + 1; o < sourceDomainIds.size(); o++) {
                knowLedgeCheckLogVoList.add(new KnowledgeCheckLogVo(sourceDomainIds.get(i), sourceDomainIds.get(o)));
            }
        }
        return knowLedgeCheckLogVoList;
    }

    //写入数据库
    public void insetLogList(List<KnowledgeCheckLogVo> knowLedgeCheckLogVoList) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        KnowledgeCheckLogMapper mapper = sqlSession.getMapper(KnowledgeCheckLogMapper.class);
        for (int i = 0; i < knowLedgeCheckLogVoList.size(); i++) {
            mapper.insertKnowledgeCheckLog(knowLedgeCheckLogVoList.get(i));
            if (i % 200 == 0 || i == knowLedgeCheckLogVoList.size() - 1) {
                sqlSession.commit();
                sqlSession.clearCache();
            }
            percentage(i, knowLedgeCheckLogVoList.size(), 3, 3);
        }
        sqlSession.close();
    }

    /**
     * 查询log表去重
     */
    public void checkLogRe(List<KnowledgeCheckLogVo> filterIdCompose, List<KnowledgeCheckLogVo> knowLedgeCheckLogVoList) {
        //根据组合查询数据
        if (0 != filterIdCompose.size()) {
            Set<Long> set = new HashSet<>();
            for (KnowledgeCheckLogVo knowledgeCheckLogVo : filterIdCompose) {
                set.add(knowledgeCheckLogVo.getSourceCourseId());
                set.add(knowledgeCheckLogVo.getTargetCourseId());
            }
            List<Long> filterCourseIds = new ArrayList<>(set);
            //List<KnowledgePointVoMaps> voList = knowledgePointMapper.checkPoints(filterCourseIds);
            List<KnowledgePointVoMaps> voList = knowledgePointMapper.checkPointsByCourseIds(filterCourseIds);
            Map<Long, List<KnowledgePointVo>> pointVoMap = voList.stream().collect(Collectors.toMap(KnowledgePointVoMaps::getcId, KnowledgePointVoMaps::getKnowledgePointVoList));
            for (int i = 0; i < filterIdCompose.size(); i++) {
                percentage(i + 1, filterIdCompose.size(), 2, 3);
                KnowledgeCheckLogVo knowledgeCheckLogVo = filterIdCompose.get(i);
                Long sId = knowledgeCheckLogVo.getSourceCourseId();
                Long tId = knowledgeCheckLogVo.getTargetCourseId();
                List<KnowledgePointVo> sourceList = pointVoMap.get(sId);
                List<KnowledgePointVo> targetList = pointVoMap.get(tId);
                if (ObjectUtils.isNotEmpty(sourceList) && ObjectUtils.isNotEmpty(targetList)) {
                    //记录统计
                    checkCourseTotal(sId, sourceList);
                    checkCourseTotal(tId, targetList);
                    //去重
                    getCheckLog(sourceList, targetList, knowLedgeCheckLogVoList, sId, tId);
                }
            }
        }
    }

    /**
     * 查询重复知识点
     * @param courseIds
     * @return
     */
    @Override
    public List<KnowledgeCheckLog> selectKnowledgeCheckLogs(List<Long> courseIds) {
        ArrayList<KnowledgeCheckLog> knowledgeCheckLogs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(courseIds)) {
            knowledgeCheckLogs.addAll(knowledgeCheckLogMapper.selectKnowledgeCheckLogListByCourses(courseIds));
        }
        return knowledgeCheckLogs;
    }


    /**
     * 计算百分比
     */
    public static void percentage(int cont, int size, int step, int allStep) {
        double ratio = (double) (cont + 1) / size;
        BigDecimal b = new BigDecimal(ratio);
        int scale = (int) Math.log10(size) + 1;
        ratio = b.setScale(scale, RoundingMode.HALF_UP).doubleValue();
        ratio = ratio * 100;
        if (ratio % 2 == 0) {
            ratio = ratio * ((double) 1 / allStep);
            double a = (double) (step - 1) / allStep;
            ratio = ratio + a * 100;
            String message = "已经完成：" + String.format("%.2f", ratio) + "%";
            System.out.println(message);
        }
    }

    public static String getStringKey(List<Long> sourceDomainIds) {
        if (ObjectUtils.isNotEmpty(sourceDomainIds)) {
            sourceDomainIds = sourceDomainIds.stream().sorted().collect(Collectors.toList());
            StringBuilder sourceDomainIdsStr = new StringBuilder();
            for (Long courseId : sourceDomainIds) {
                sourceDomainIdsStr.append(courseId).append(":");
            }
            return DigestUtils.md5DigestAsHex(sourceDomainIdsStr.toString().getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    /**
     * redis 锁
     */
    public Boolean setNX(String key) {
        deleteKey(key);
        String value = "pointCheck";
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory is null");
        Boolean res = redisConnectionFactory.getConnection().setNX(key.getBytes(), value.getBytes());
        Assert.notNull(res, "setNX is null");
        if (res) {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, 3, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }


    /**
     * 防止锁死
     */
    public void deleteKey(String key) {
        Long l = redisTemplate.opsForValue().getOperations().getExpire(key);
        if (null != l) {
            if (l == -1) {
                redisTemplate.delete(key);
            }
        }
    }
}
