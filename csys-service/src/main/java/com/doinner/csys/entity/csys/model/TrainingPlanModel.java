package com.doinner.csys.entity.csys.model;

import com.doinner.csys.constant.ConstantTrainingScheme;
import com.doinner.csys.domain.Course;
import com.doinner.csys.domain.StandardCultivationTarget;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.domain.TeachingProgrammeInstanceExtract;
import com.doinner.csys.domain.vo.TrainingSchemeCourseScheduleVo;
import com.doinner.csys.domain.vo.TrainingSchemeVo;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrainingPlanModel {
    public TrainingPlanModel() {
    }

    //培养方案名称
    private String trainingPlanName;
    //培养目标
    private TrainingTargetModel trainingTarget;
    //修业时间与学时学分
    private DurationAndCreditsModel durationAndCredits;
    //毕业要求内容
    private String standardGraduationContent = "具有学籍的本科学员，在修业年限内完成本培养方案规定的教学训练，通过各项考核、达成以下毕业要求，依据国防科技大学《高等教育生长军官学员、军士职业技术教育学员学籍管理规定实施细则（暂行）》，颁发毕业证书；依据《国防科技大学学位工作细则（暂行）》，对符合学位授予条件的毕业学员，授予X学士学位。";
    //毕业要求
    private List<StandardGraduation> standardGraduations;
    //公共基础课程
    private List<TrainingSchemeCourseModel> generalCourses;
    //学科基础课程
    private List<TrainingSchemeCourseModel> disciplineCourses;
    //专业课程
    private List<TrainingSchemeCourseModel> majorCourses;
    //实践项目与安排
    private List<TrainingSchemeCourseModel> trainingSubjectCourses;
    //训练课项目与安排
    private List<TrainingSchemeCourseModel> practicalProjectCourse;
    //军士职业技术教育课程（军事职业教育父模块下的政治理论/军事基础/任职基础/任职岗位4个子模块）
    private List<TrainingSchemeCourseModel> ncoCourses;

    public String getTrainingPlanName() {
        return trainingPlanName;
    }

    public void setTrainingPlanName(String trainingPlanName) {
        this.trainingPlanName = trainingPlanName;
    }

    public TrainingTargetModel getTrainingTarget() {
        return trainingTarget;
    }

    public void setTrainingTarget(List<StandardCultivationTarget> standardCultivationTargetList, TrainingSchemeVo trainingSchemeVo) {
        TrainingTargetModel trainingTargetModel = new TrainingTargetModel();
        if (ObjectUtils.isNotEmpty(standardCultivationTargetList)) {
            // 存储培养目标
            StandardCultivationTarget parentTarget = standardCultivationTargetList.stream().filter(s -> s.getParentId() == -1).collect(Collectors.toList()).get(0);
            List<StandardCultivationTarget> otherTarget = standardCultivationTargetList.stream().filter(s -> s.getParentId() != -1).collect(Collectors.toList());
            trainingTargetModel.setSecondLevelContent1(parentTarget.getRemark());
            trainingTargetModel.setSecondLevelContent2(otherTarget);
            StringBuilder subMajorNames = new StringBuilder();
            if(ObjectUtils.isNotEmpty(trainingSchemeVo.getSubMajorNames())) {
                for (String subMajorName : trainingSchemeVo.getSubMajorNames()) {
                    subMajorNames.append(subMajorName).append(",");
                }
                subMajorNames.deleteCharAt(subMajorNames.length() - 1);
            }else{
                subMajorNames.append("****,****,****");
            }
            trainingTargetModel.setFirstLevelContent(trainingSchemeVo.getEducationLevelName(),subMajorNames.toString());
        }
        this.trainingTarget = trainingTargetModel;

    }

    public List<StandardGraduation> getStandardGraduations() {
        return standardGraduations;
    }

    public void setStandardGraduations(List<StandardGraduation> standardGraduations) {
        this.standardGraduations = standardGraduations;
    }

    public List<TrainingSchemeCourseModel> getGeneralCourses() {
        return generalCourses;
    }


    public List<TrainingSchemeCourseModel> getDisciplineCourses() {
        return disciplineCourses;
    }


    public List<TrainingSchemeCourseModel> getMajorCourses() {
        return majorCourses;
    }


    public List<TrainingSchemeCourseModel> getTrainingSubjectCourses() {
        return trainingSubjectCourses;
    }

    public List<TrainingSchemeCourseModel> getPracticalProjectCourse() {
        return practicalProjectCourse;
    }

    public List<TrainingSchemeCourseModel> getNcoCourses() {
        return ncoCourses;
    }

    public void setNcoCourses(List<TrainingSchemeCourseModel> ncoCourses) {
        this.ncoCourses = ncoCourses;
    }

    public String getStandardGraduationContent() {
        return standardGraduationContent;
    }

    public void setStandardGraduationContent(String standardGraduationContent) {
        this.standardGraduationContent = standardGraduationContent;
    }

    public DurationAndCreditsModel getDurationAndCredits() {
        return durationAndCredits;
    }

    /*public void setDurationAndCredits(List<TeachingProgrammeInstanceExtract> teachingProgrammeInstanceExtracts) {
        if(ObjectUtils.isNotEmpty(teachingProgrammeInstanceExtracts)){
            TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract = teachingProgrammeInstanceExtracts.get(0);
            DurationAndCreditsModel durationAndCreditsModel = new DurationAndCreditsModel();
            BeanUtils.copyProperties(teachingProgrammeInstanceExtract,durationAndCreditsModel);
            this.durationAndCredits=durationAndCreditsModel;
        }else{
            this.durationAndCredits=new DurationAndCreditsModel();
        }
    }*/

    public void setDurationAndCredits() {
        DurationAndCreditsModel durationAndCreditsModel = new DurationAndCreditsModel();
        //设置公共基础课程
        setGeneralCourseHoursAndCredits(durationAndCreditsModel);
        //设置军士职业技术教育课程（军事职业教育4个子模块，学时学分表按子模块汇总）
        setNcoCourseHoursAndCredits(durationAndCreditsModel);
        //设置学科基础课程
        setDisciplineCourse(durationAndCreditsModel);
        //设置专业课程
        setMajorCourse(durationAndCreditsModel);
        //设置实践课程
        setProjectTraining(durationAndCreditsModel);
        this.durationAndCredits = durationAndCreditsModel;
    }

    private  void setProjectTraining( DurationAndCreditsModel durationAndCreditsModel) {
        List<TrainingSchemeCourseModel> allPracticeCourseList = new ArrayList<>();
        allPracticeCourseList.addAll(trainingSubjectCourses);
        allPracticeCourseList.addAll(practicalProjectCourse);
        if(ObjectUtils.isNotEmpty(allPracticeCourseList)) {
            CreditsDetailModel creditsDetailModel = setCourseDetailModel(durationAndCreditsModel, allPracticeCourseList);
            durationAndCreditsModel.setTrainingProjectCourses(creditsDetailModel);
        }
    }

    private void setMajorCourse(DurationAndCreditsModel durationAndCreditsModel) {
        if(ObjectUtils.isNotEmpty(majorCourses)) {
            CreditsDetailModel creditsDetailModel = setCourseDetailModel(durationAndCreditsModel,majorCourses);
            durationAndCreditsModel.setDisciplineMajorCourse(creditsDetailModel,DictContent.MAJOR_COURSE_NAME);
        }
    }

    private void setDisciplineCourse(DurationAndCreditsModel durationAndCreditsModel) {
        if(ObjectUtils.isNotEmpty(disciplineCourses)) {
            CreditsDetailModel creditsDetailModel = setCourseDetailModel(durationAndCreditsModel, disciplineCourses);
            durationAndCreditsModel.setDisciplineMajorCourse(creditsDetailModel,DictContent.DISCIPLINE_COURSE_NAME);
        }
    }

    private  void setGeneralCourseHoursAndCredits(DurationAndCreditsModel durationAndCreditsModel) {
        Map<String, Map<String, List<TrainingSchemeCourseModel>>> generalCourseMap = TrainingSchemeCourseModel.groupCourses(generalCourses);
        generalCourseMap.forEach((ModelName, courseMap) -> {
            courseMap.forEach((childrenModelName, courseModelList) -> {
                if (ObjectUtils.isNotEmpty(courseModelList)) {
                    CreditsDetailModel creditsDetailModel = setCourseDetailModel(durationAndCreditsModel,courseModelList);
                    durationAndCreditsModel.setGeneralCourse(creditsDetailModel);
                }
            });
        });
    }

    /**
     * 设置军士职业技术教育课程学时学分：按4个子模块（courseModeChildrenName）汇总，
     * 每个 CreditsDetailModel 追加到 durationAndCreditsModel.generalCourses，
     * 供军士学时学分表按子模块行读取。
     */
    private void setNcoCourseHoursAndCredits(DurationAndCreditsModel durationAndCreditsModel) {
        Map<String, Map<String, List<TrainingSchemeCourseModel>>> ncoCourseMap = TrainingSchemeCourseModel.groupCourses(ncoCourses);
        ncoCourseMap.forEach((ModelName, courseMap) -> {
            courseMap.forEach((childrenModelName, courseModelList) -> {
                if (ObjectUtils.isNotEmpty(courseModelList)) {
                    CreditsDetailModel creditsDetailModel = setCourseDetailModel(durationAndCreditsModel, courseModelList);
                    durationAndCreditsModel.setGeneralCourse(creditsDetailModel);
                }
            });
        });
    }

    @NotNull
    private  CreditsDetailModel setCourseDetailModel(DurationAndCreditsModel durationAndCreditsModel, List<TrainingSchemeCourseModel> courseModelList) {
        TrainingSchemeCourseModel baseCourse = courseModelList.get(0);
        CreditsDetailModel creditsDetailModel = new CreditsDetailModel();
        creditsDetailModel.setModelName(baseCourse.getCourseModeChildrenName());
        creditsDetailModel.setChildrenNameModelName(baseCourse.getCourseModeFourLevelName());
        creditsDetailModel.setModelNameSort(baseCourse.getChildrenModelSort());
        creditsDetailModel.setChildrenNameModelSort(baseCourse.getCourseModeFourLevelSort());

        AtomicDouble requiredHour = new AtomicDouble(0);
        AtomicDouble optionalHour = new AtomicDouble(0);
        AtomicDouble requireCredits = new AtomicDouble(0);
        AtomicDouble optionalCredits = new AtomicDouble(0);
        AtomicDouble projectTime = new AtomicDouble(0);
        AtomicDouble requireTime = new AtomicDouble(0);
        AtomicDouble optionalTime = new AtomicDouble(0);
        for (TrainingSchemeCourseModel course : courseModelList) {
            double hours = course.getHours() == null ? 0 : course.getHours();
            double credits = course.getCredits()==null?0:course.getCredits();
            double timeWeek = course.getTimeWeek()==null?0:course.getTimeWeek();
            if (ConstantTrainingScheme.COMPULSORY_COURSE.equals(course.getCourseAttr())) {
                //必修
                requiredHour.set(requiredHour.get() + hours);
                requireCredits.set(requireCredits.get() + credits);
                requireTime.set(requireTime.get()+timeWeek);
            } else {
                //选修 任选
                optionalHour.set(optionalHour.get() + hours);
                optionalCredits.set(optionalCredits.get() + credits);
                optionalTime.set(optionalTime.get()+timeWeek);
            }
        }
        projectTime.set(optionalTime.get()+requireTime.get());
        creditsDetailModel.setRequiredHours(requiredHour.get());
        creditsDetailModel.setOptionalHours(optionalHour.get());
        creditsDetailModel.setRequiredCredits(requireCredits.get());
        creditsDetailModel.setOptionalCredits(optionalCredits.get());
        creditsDetailModel.setTotalHours(requiredHour.get() + optionalHour.get());
        creditsDetailModel.setTotalCredits(requireCredits.get() + optionalCredits.get());
        //记录数据总数
        durationAndCreditsModel.setDataSize(durationAndCreditsModel.getDataSize()+1);
        //统计总数 学时
        durationAndCreditsModel.setRequiredHour(durationAndCreditsModel.getRequiredHour()+creditsDetailModel.getRequiredHours());
        durationAndCreditsModel.setOptionalHour(durationAndCreditsModel.getOptionalHour()+creditsDetailModel.getOptionalHours());
        durationAndCreditsModel.setTotalHour(durationAndCreditsModel.getTotalHour()+creditsDetailModel.getTotalHours());
        //学分
        durationAndCreditsModel.setRequiredCredit(durationAndCreditsModel.getRequiredCredit()+creditsDetailModel.getRequiredCredits());
        durationAndCreditsModel.setOptionalCredit(durationAndCreditsModel.getOptionalCredit()+creditsDetailModel.getOptionalCredits());
        durationAndCreditsModel.setTotalCredit(durationAndCreditsModel.getTotalCredit()+creditsDetailModel.getTotalCredits());
        //实践周次
        durationAndCreditsModel.setRequireTime(durationAndCreditsModel.getRequireTime()+requireTime.get());
        durationAndCreditsModel.setOptionalTime(durationAndCreditsModel.getOptionalTime()+optionalTime.get());
        durationAndCreditsModel.setProjectTime(durationAndCreditsModel.getProjectTime()+projectTime.get());
        return creditsDetailModel;
    }

    public TrainingPlanModel(List<StandardGraduation> standardGraduations,
                             List<StandardCultivationTarget> standardCultivationTargetList,
                             List<TeachingProgrammeInstanceExtract> teachingProgrammeInstanceExtracts,
                             List<Course> courses,
                             List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos,
                             TrainingSchemeVo trainingSchemeVo) {
        setTrainingPlanName(trainingSchemeVo.getPlanName());
        setTrainingTarget(standardCultivationTargetList,trainingSchemeVo);
        setStandardGraduations(standardGraduations);
        //设置课程
        setCourseModel(courses, trainingSchemeCourseScheduleVos);
        //根据课程设置学分
        //setDurationAndCredits();
    }

    private void setCourseModel(List<Course> courseList, List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos) {
        Map<Long, List<TrainingSchemeCourseScheduleVo>> courseScheduleMap = trainingSchemeCourseScheduleVos.stream().collect(Collectors.groupingBy(TrainingSchemeCourseScheduleVo::getCourseId));
        Map<String, List<Course>> courseTypeMap = courseList.stream().collect(Collectors.groupingBy(Course::getType));
        //课程类型包含通识课，专业大类，专业方向
        List<TrainingSchemeCourseModel> generalCourseList = new ArrayList<>();
        List<TrainingSchemeCourseModel> disciplineCourseList = new ArrayList<>();
        List<TrainingSchemeCourseModel> majorCourseList = new ArrayList<>();
        //实践项目训练课程
        List<TrainingSchemeCourseModel> trainingSubjectCourseList = new ArrayList<>();
        //训练科目
        List<TrainingSchemeCourseModel> practicalProjectCourse = new ArrayList<>();
        //军士职业技术教育课程（军事职业教育父模块下的4个子模块）
        List<TrainingSchemeCourseModel> ncoCourseList = new ArrayList<>();
        courseTypeMap.forEach((type, courses) -> {
            switch (type) {
                case ConstantTrainingScheme.TRAINING_TYPE:
                    for (Course course : courses) {
                        setCourse(trainingSubjectCourseList, course, courseScheduleMap.get(course.getId()));
                    }
                    break;
                case ConstantTrainingScheme.PRACTICAL_TYPE:
                    for (Course course : courses) {
                        setCourse(practicalProjectCourse, course, courseScheduleMap.get(course.getId()));
                    }
                    break;
                default:
                    for (Course course : courses) {
                        switch (course.getCourseModule()) {
                            case DictContent.GENERAL_EDUCATION_COURSES_SCHEDULE:
                                //公共基础课程教学安排
                                setCourse(generalCourseList, course, courseScheduleMap.get(course.getId()));
                                break;
                            case DictContent.DISCIPLINE_CORE_COURSES_SCHEDULE:
                                //学科基础课程教学安排
                                setCourse(disciplineCourseList, course, courseScheduleMap.get(course.getId()));
                                break;
                            case DictContent.MAJOR_COURSES_SCHEDULE:
                                //专业课程教学安排
                                setCourse(majorCourseList, course, courseScheduleMap.get(course.getId()));
                                break;
                            case DictContent.MILITARY_VOCATIONAL_EDUCATION:
                                //军士职业技术教育：军事职业教育父模块
                                setCourse(ncoCourseList, course, courseScheduleMap.get(course.getId()));
                                break;
                            default:
                                //兼容 courseModule 存的是4个子模块ID的情况（按子模块ID归类）
                                if (isNcoSubModule(course.getCourseModule())) {
                                    setCourse(ncoCourseList, course, courseScheduleMap.get(course.getId()));
                                }
                                break;
                        }
                    }
                    break;
            }
        });

        this.generalCourses = generalCourseList;
        this.disciplineCourses = disciplineCourseList;
        this.majorCourses = majorCourseList;
        //实践项目训练课程
        this.trainingSubjectCourses = trainingSubjectCourseList;
        //训练科目
        this.practicalProjectCourse = practicalProjectCourse;
        //军士职业技术教育课程
        this.ncoCourses = ncoCourseList;
    }

    /**
     * 判断 courseModule 是否为军士4个子模块之一。
     */
    private boolean isNcoSubModule(String courseModule) {
        return DictContent.POLITICAL_THEORY_NCO.equals(courseModule)
                || DictContent.MILITARY_FOUNDATION_NCO.equals(courseModule)
                || DictContent.POSITION_FOUNDATION.equals(courseModule)
                || DictContent.DUTY_POSITION.equals(courseModule);
    }

    private void setCourse(List<TrainingSchemeCourseModel> courseModeList, Course course, List<TrainingSchemeCourseScheduleVo> trainingSchemeCourseScheduleVos) {
        TrainingSchemeCourseModel courseModel = new TrainingSchemeCourseModel();
        courseModel.setName(course.getName());
        courseModel.setExaMethod(course.getExaMethod());
        courseModel.setMajorId(course.getMajorId());
        //专业方向：透传 subMajorId，后续在 translateDict 中查询专业方向名称
        courseModel.setSubmajorId(course.getSubMajorId());
        courseModel.setCourseModelId(course.getCourseModule());
        courseModel.setCourseModeChildrenId(course.getCourseModuleChildren());
        courseModel.setRemark(course.getRemark());
        courseModel.setCourseAttr(course.getCourseAttr());
        if(ObjectUtils.isEmpty(course.getHours())) {
            double teachHours = course.getTeachHours() == null ? 0.0 : course.getTeachHours();
            double practiceHours = course.getPracticeHours() == null ? 0.0 : course.getPracticeHours();
            courseModel.setHours(teachHours+practiceHours);
        }else{
            courseModel.setHours(course.getHours());
        }
        courseModel.setCredits(course.getCredit());
        //实践类课程
        courseModel.setProjectLevelId(course.getProgramLevel());
        courseModel.setTimeWeek(course.getTimeWeek());
        courseModel.setUnit(course.getUnit());
        String supportCourseIds= Stream.of(course.getBeforeCourseId(),course.getAfterCourseId())
                .filter(Objects::nonNull).filter(s->!s.isBlank()).collect(Collectors.joining(","));
        courseModel.setSupportingCourseIds(supportCourseIds);
        //训练课目
        courseModel.setTrainingCourseModelId(course.getLocation());

        Double teachHours = 0.0;
        Double practiceHours = 0.0;
        ArrayList<Integer> openTerms = new ArrayList<>();
        //各学期拆分学时（讲授+实践），同一学期可能有多条排课记录需累加
        Map<Integer, Double> termHoursMap = new LinkedHashMap<>();
        if (ObjectUtils.isNotEmpty(trainingSchemeCourseScheduleVos)) {
            for (TrainingSchemeCourseScheduleVo courseSchedule : trainingSchemeCourseScheduleVos) {
                if (ObjectUtils.isNotEmpty(courseSchedule.getTeachHours())) {
                    teachHours += courseSchedule.getTeachHours();
                }
                if (ObjectUtils.isNotEmpty(courseSchedule.getPracticeHours())) {
                    practiceHours += courseSchedule.getPracticeHours();
                }
                //courseModel.setCourseAttr(courseSchedule.getCourseAttr());
                openTerms.add(courseSchedule.getTerm());

                //按学期累计拆分学时（讲授+实践）
                Integer term = courseSchedule.getTerm();
                if (term != null) {
                    double tHours = courseSchedule.getTeachHours() == null ? 0.0 : courseSchedule.getTeachHours();
                    double pHours = courseSchedule.getPracticeHours() == null ? 0.0 : courseSchedule.getPracticeHours();
                    termHoursMap.merge(term, tHours + pHours, Double::sum);
                }
            }
        }

        courseModel.setOpenTerm(openTerms);
        courseModel.setTermHoursMap(termHoursMap);
        courseModel.setTeachHours(teachHours == 0.0 ? course.getTeachHours() : teachHours);
        courseModel.setPracticeHours(practiceHours == 0.0 ? course.getPracticeHours() : practiceHours);

        courseModeList.add(courseModel);
    }

}
