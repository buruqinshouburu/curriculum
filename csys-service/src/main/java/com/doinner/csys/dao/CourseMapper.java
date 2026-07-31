package com.doinner.csys.dao;

import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程Mapper接口
 *
 * @author doinner
 * @date 2023-03-14
 */
public interface CourseMapper {

    /**
     * 查询课程
     *
     * @param id 课程主键
     * @return 课程
     */
    CourseVo selectCourseById(Long id);

    /**
     * 查询课程通过知识单元主键
     *
     * @param id 课程主键
     * @return 课程
     */
    CourseVo selectCourseByKnowledgeUnitId(Long id);

    /**
     * 查询课程
     *
     * @param ids 课程主键
     * @return 课程
     */
    List<Course> selectCoursesByIds(@Param("ids") List<Long> ids);

    /**
     * 按课程id集合查询, 支持自定义排序(用于构建课程体系列表的按字段排序)
     *
     * @param ids          课程主键集合
     * @param database_prop 排序字段(数据库列名)
     * @param order         排序方式 asc/desc
     * @return 课程集合
     */
    List<Course> selectCoursesByIdsWithSort(@Param("ids") List<Long> ids,
                                            @Param("database_prop") String database_prop,
                                            @Param("order") String order);

    /**
     * 查询课程列表
     *  注意这个是分页方法  不能关联表一对多关联查询  会导致分页问题
     * @param course 课程
     * @return 课程集合
     */
    List<Course> selectCourseList(Course course);

    List<Course> selectSchemeCourseList(Course course);



    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    int insertCourse(Course course);

    /**
     * 新增课程
     *
     * @param course 课程
     * @return 结果
     */
    int insertCourses(@Param("courseList") List<Course> course);

    /**
     * 修改课程
     *
     * @param course 课程
     * @return 结果
     */
    int updateCourse(Course course);

    /**
     * 删除课程
     *
     * @param id 课程主键
     * @return 结果
     */
    int deleteCourseById(Long id);

    int deleteFile(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCourseByIds(List<Long> ids);


    /**
     * 查询课程和课程关联对象
     *
     * @param id
     * @return
     */
    CourseVo selectCourseAndRelevanceById(Long id);

    /**
     * 查询课程和课程关联对象
     * @param ids
     * @return
     */
    List<CourseVo> selectCourseAndRelevanceByIds(@Param("ids") List<Long> ids);

    List<CourseVo> selectCourseAndRelevanceList(Course course);

    List<Long> selectCourseCount(Long schemeId);
    List<Long> selectUnitCount(@Param("courseIds")List<Long> courseIds);
    List<Long> selectPointCount(@Param("pointIds")List<Long> pointIds);

    List<Course> selectCourseListByNameIn(@Param("courseNames")List<String> courseNames);

    void updateFileById(@Param("fileId") String fileId,@Param("fileName")String fileName,@Param("id")Long id);

    /**
     * 查询课程教学计划生成文档文件ID（用于生成前清理旧文件）。
     *
     * @param id 课程主键
     * @return plan_file_id，无则 null
     */
    String selectPlanFileId(@Param("id") Long id);

    /**
     * 回写课程教学计划生成文档文件信息（plan_file_id/plan_file_name/plan_download_url/plan_preview_url）。
     *
     * @param planFileId     生成文档文件ID
     * @param planFileName   生成文档文件名
     * @param planDownloadUrl 下载地址
     * @param planPreviewUrl  预览地址
     * @param id             课程主键
     */
    void updatePlanFileById(@Param("planFileId") String planFileId,
                            @Param("planFileName") String planFileName,
                            @Param("planDownloadUrl") String planDownloadUrl,
                            @Param("planPreviewUrl") String planPreviewUrl,
                            @Param("id") Long id);


    void updateStatusByIds(@Param("ids") List<Long> ids);

    /**
     * 查询课程
     *
     * @param ids 课程主键
     * @return 课程
     */
    List<CourseIdAndName> selectCoursesIdAndNameByIds(@Param("ids") List<Long> ids);

    List<TrainingSchemeCourseScheduleRankingVo> courseRanking();

    List<TrainingSchemeCourseScheduleRankingVo> courseSelectStatistics(@Param("courseName")String courseName,@Param("types")List<Integer> types);

    List<String> selectCodeByCodes(@Param("codeList") List<String> codeList);

    Long countCourse();
    Long countScheme();

    /**
     * 学院课程统计
     * @return
     */
    List<CurriculumVo> countCollegeCourse();
    /**
     * 学院培养方案统计
     * @return
     */
    List<CollegeProgramVo> countCollegeScheme();
    /**
     * 培养方案类别统计
     * @return
     */
    List<TypeProgramVo> countCategory();
    /**
     * 学期排课统计
     * @return
     */
    List<CurriculumSelectionVo> countSemesterScheduling();

    List<Long> selectUnitBySchemeId(@Param("schemeId") Long schemeId);

    Long selectCourseByCodeAndNotId(@Param("code") String code,@Param("id")Long id);



    List<CourseAndSpecializedVo> courseAndSpecializedStatistics();


    List<Course> selectCourseBeforeAndAfterList(@Param("beforeCourseId") String beforeCourseId,@Param("afterCourseId") String afterCourseId);

    List<Course> selectCoursesByCodeLike(String code);

    List<Map<String,Long>> selectKnowledgeNum(@Param("courseIds")List<Long> courseIds, @Param("majorId") Long majorId);

    List<CourseVo> selectCourseByGraduationIds(@Param("ids") List<Long> ids);

    List<ExcelRelationshipVo> selectCourseRefGraduation(@Param("graduationIds") List<Long> graduationIds, @Param("courseIds") List<Long> courseIds);

    Integer selectGenerateCourse(@Param("version")String version);

    List<Long> selectOverQuoteCourse(@Param("version")String version);

    List<OverQuoteCourseInfo> selectQuoteCourseInfo(@Param("ids") List<Long> ids);

    List<Long> selectQuotedCourse(@Param("courseTemplateVo") CourseTemplateVo courseTemplateVo);

    void insertCourseList(@Param("courseList") List<Course> courseList);

    Long selectByNameCount(Course course);


    List<String> selectByNamesCount(@Param("courseNames") List<String> courseNames,@Param("version")String version,@Param("type")String type);

    List<TrainingSchemeCourseVo> selectCourseKnowledgeByCourseIds(@Param("courseIds")List<Long> courseIds);

    List<Course> selectCourseBySourceId(Long id);

    /**
     * 查询学期安排字典为历史值(6=贯穿4年 / 7=多学期排课)的课程id集合。
     * 检测范围：课程表 t_csys_course.semester_Schedule 在 ('6','7')，
     * 或其关联表 t_csys_course_ref_schedule 存在 semester_Schedule 在 ('6','7') 的行。
     */
    List<Long> selectLegacyScheduleCourseIds();

    /**
     * 查询缺课程编号(code 为空)的源库课程(source_id 为空)，供统一刷新编号。
     * 返回 id/type/version/collegeId/educationLevel(+ collegeName 经 sys_dept join)。
     */
    List<Course> selectCoursesWithoutCode();

    /**
     * 仅回写课程编号(刷新用，避免全字段 updateCourse 副作用)。
     */
    int updateCourseCode(@Param("id") Long id, @Param("code") String code);

    int removeCourseByIds(@Param("ids") List<Long> ids);

    void updateEnableFlag(@Param("id") Long courseId,@Param("enableFlag") Integer enableFlag);

    void updateBindStatusById(@Param("id") Long courseId, @Param("bind_status") Integer courseBindStatusFalse);

    List<CourseKnowledgeViewVo> selectCourseKnowledgeList(Long trainingSchemeId);

    List<TrainingSchemeCourseVo> selectCourseGraduationByCourseIds(@Param("courseIds")List<Long> courseIds,@Param("type") String type);

    void updateBuildStatus(@Param("id") Long id, @Param("status") Integer status);

    List<HashMap<String,Object>> selectCourseSourceHoursByTargetCourseIds(@Param("ids") List<Long> courseIds);

    List<HashMap<String, Object>> selectCourseAcademicStatus(@Param("schemeIds")List<Long> schemeIds);

    /**
     * 选择性更新调用课程(课程调用场景): 除修读要求、课程模块、专业方向、考核方式、
     * 学年安排、学期安排、id 外, 其余字段按非空更新。
     *
     * @param course 调用课程(CourseVo)
     * @return 结果
     */
    int updateInvokeCourse(CourseVo course);
}
