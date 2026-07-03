package com.doinner.csys.entity.csys;


import com.doinner.csys.constant.ConstantTrainingScheme;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.entity.csys.model.*;
import com.doinner.csys.utils.TreeBuilderUtils;
import com.doinner.csys.utils.WordUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 培养方案文档生成器 - 基于TrainingPlanModel生成培养方案Word文档
 *
 * 包含四个一级标题：
 * 1. 培养目标
 * 2. 毕业要求
 * 3. 修业时间与学时学分
 * 4. 教学训练体系与安排
 */
public class
TrainingPlanGenerator {

    private  String[] term={"第一学年秋季学期","第一学年春季学期","第二学年秋季学期","第二学年春季学期","第三学年秋季学期","第三学年春季学期","第四学年秋季学期","第四学年春季学期","第五学年秋季学期","第五学年春季学期"};
    /**
     * 生成培养方案文档
     *
     * @param model      培养方案数据模型
     * @throws IOException 生成异常
     */
    public InputStream generate(TrainingPlanModel model) throws IOException {
        XWPFDocument document = new XWPFDocument();

        try {
            // 1. 创建标题
            WordUtil.createTitle(document, model.getTrainingPlanName());

            // 2. 培养目标
            generateTrainingTarget(model, document);

            // 3. 毕业要求
            generateStandardGraduations(model, document);

            // 4. 修业时间与学时学分
            generateDurationAndCredits(model, document);

            // 5. 教学训练体系与安排（课程信息）
            generateCourseArrangements(model, document);

            // 输出文档
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            outputStream.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            document.close();
        }
    }

    public void generate(TrainingPlanModel model, String outputPath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        try {
            // 1. 创建标题
            WordUtil.createTitle(document, model.getTrainingPlanName());

            // 2. 培养目标
            generateTrainingTarget(model, document);

            // 3. 毕业要求
            generateStandardGraduations(model, document);

            // 4. 修业时间与学时学分
            generateDurationAndCredits(model, document);

            // 5. 教学训练体系与安排（课程信息）
            generateCourseArrangements(model, document);

            // 输出文档
            FileOutputStream out = new FileOutputStream(outputPath);
            document.write(out);
            out.close();

        } finally {
            document.close();
        }
    }

    /**
     * 生成培养目标部分
     */
    private void generateTrainingTarget(TrainingPlanModel model, XWPFDocument document) {
        TrainingTargetModel target = model.getTrainingTarget();
        if(ObjectUtils.isEmpty(target)){
            return;
        }
        // 一级标题
        WordUtil.createHeading(document, target.getFirstLevelTitle(), 1);

        // 一级内容
        WordUtil.createParagraph(document, target.getFirstLevelContent(), null);

        // 二级标题1
        WordUtil.createHeading(document, target.getSecondLevelTitle1(), 2);
        WordUtil.createParagraph(document, target.getSecondLevelContent1(), null);

        // 二级标题2
        WordUtil.createHeading(document, target.getSecondLevelTitle2(), 2);
        WordUtil.createParagraph(document, target.getSecondLevelContent2(), null);
    }

    /**
     * 生成毕业要求部分
     */
    private void generateStandardGraduations(TrainingPlanModel model, XWPFDocument document) {
        // 一级标题
        WordUtil.createHeading(document, "二、毕业要求", 1);

        // 毕业要求内容说明
        WordUtil.createParagraph(document, model.getStandardGraduationContent(), null);

        List<StandardGraduation> standardGraduations = TreeBuilderUtils.buildRootTree(model.getStandardGraduations());
        if(ObjectUtils.isEmpty(standardGraduations)){
            return;
        }
        // 遍历一级标题并处理子结构
        for (StandardGraduation standardGraduation : standardGraduations) {
            processFirstLevel(standardGraduation, document);
        }
    }

    /**
     * 处理一级标题及其子结构
     */
    private void processFirstLevel(StandardGraduation graduation, XWPFDocument document) {
        WordUtil.createHeading(document, graduation.getName(), 2);
        List<StandardGraduation> children = (List<StandardGraduation>) (graduation.getChildren());
        if (ObjectUtils.isNotEmpty(children)) {
            children.forEach(child -> processSecondLevel(child, document));
        }
    }

    /**
     * 处理二级标题及其子结构
     */
    private void processSecondLevel(StandardGraduation child, XWPFDocument document) {
        WordUtil.createHeading(document, child.getName(), 3);
        List<StandardGraduation> childrenList = (List<StandardGraduation>) (child.getChildren());
        if (ObjectUtils.isNotEmpty(childrenList)) {
            String content = buildContentString(childrenList);
            WordUtil.createParagraph(document, content, null);
        }
    }

    /**
     * 拼接子节点内容字符串
     */
    private String buildContentString(List<StandardGraduation> nodes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (StandardGraduation node : nodes) {
            stringBuilder.append(WordUtil.addEndDot(node.getName(),","));
        }
        return WordUtil.addEndDot(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString(),"。");
    }


    /**
     * 根据父项ID查找子项
     */
    private List<StandardGraduation> findChildrenById(List<StandardGraduation> items, Long parentId) {
        List<StandardGraduation> children = new ArrayList<>();
        if (parentId == null) {
            return children;
        }
        for (StandardGraduation item : items) {
            if (parentId.equals(item.getParentId())) {
                children.add(item);
            }
        }
        return children;
    }

    /**
     * 生成修业时间与学时学分部分
     */
    private void generateDurationAndCredits(TrainingPlanModel model, XWPFDocument document) {
        DurationAndCreditsModel dac = model.getDurationAndCredits();

        // 一级标题
        WordUtil.createHeading(document, "三、修业时间与学时学分", 1);

        // （一）修业时间安排
        WordUtil.createHeading(document, dac.getFirstLevelTitle1(), 2);
        WordUtil.createParagraph(document, dac.getFirstLevelContent1(), null);

        // （二）学时学分要求
        WordUtil.createHeading(document, dac.getFirstLevelTitle2(), 2);
        WordUtil.createParagraph(document, dac.getFirstLevelContent2(), null);

        //学时学分要求表格
        generateDurationAndCreditsTable(dac, document);

        // （三）学分冲抵机制
        WordUtil.createHeading(document, dac.getFirstLevelTitle3(), 2);
        WordUtil.createParagraph(document, dac.getFirstLevelContent3(), null);


    }

    /**
     * 生成修业时间与学时学分表格
     */
    private XWPFTable generateDurationAndCreditsTable(DurationAndCreditsModel model, XWPFDocument document) {
        // 这里可以根据需要生成具体的表格
        // 计算总行数：表头6行 + 数据行 + 小计2行+合计1行
        int totalRows = 8+model.getDataSize();
        int totalCols = 9; // 原始8列，删除第6列和第11列后实际13列

        CellWidth cellWidth = new CellWidth(9);
        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 创建表格
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行 3列
        setCellText(table.getRow(0).getCell(0), "课程体系", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);
        WordUtil.mergeCellsHorizontal(table, 0, 0, 2);
        WordUtil.mergeCellsHorizontal(table, 1, 0, 2);
        WordUtil.mergeCellsHorizontal(table, 2, 0, 2);



        // 列1：学时学分要求，跨行6列
        setCellText(table.getRow(0).getCell(1), "学时学分要求", true, cellWidth.getCellWidth(6));
        WordUtil.mergeCellsHorizontal(table, 0, 1, 6);

        // ========== 表头第2行（行号1） ==========
        // 列2：必修，跨行2行
        setCellText(table.getRow(1).getCell(1), "必修", true,  cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 1, 2);

        // 列3：选修，跨行2行
        setCellText(table.getRow(1).getCell(2), "选修", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 2,3);

        // 列3：小计，跨行2行
        setCellText(table.getRow(1).getCell(3), "小计", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 3, 4);
        // ========== 表头第2行（行号2） ==========
        setCellText(table.getRow(2).getCell(1), "学时", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(2), "学分", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(3), "学时", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(4), "学分", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(5), "学时", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(6), "学分", true, cellWidth.getCellWidth(1));
        // deleteCol(table,0,2,totalCols);
        // deleteCol(table,1,4,totalCols);
        // deleteCol(table,2,7,totalCols);
        // ========== 内容 ==========
        int dataRow=3;
        CountModel countModel = new CountModel();
        setCellText(getCell(table, dataRow,0), "公共基础\n课程\n", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 3, 3+model.getGeneralCourses().size());
        //排序
        List<CreditsDetailModel> detailModelList = model.getGeneralCourses().parallelStream()
                .sorted(Comparator.comparing(CreditsDetailModel::getModelNameSort)
                        .thenComparing(CreditsDetailModel::getChildrenNameModelSort))
                .collect(Collectors.toList());
        HashMap<String, int[]> mergeRowMap = new HashMap<>();
        for (int i = 0; i < detailModelList.size(); i++) {
            int firstCell=1;
            CreditsDetailModel creditsDetailModel = model.getGeneralCourses().get(i);
            if(ObjectUtils.isEmpty(creditsDetailModel.getChildrenNameModelName())||creditsDetailModel.getChildrenNameModelName().equals("-1")){
                //只有一级
                setCellText(getCell(table, dataRow,firstCell), creditsDetailModel.getModelName(), true, cellWidth.getCellWidth(2));
                WordUtil.mergeCellsHorizontal(table, dataRow, 1,2);
                // deleteCol(table,dataRow,8,totalCols);
            }else{
                //存在子集
                setCellText(getCell(table, dataRow,firstCell), creditsDetailModel.getModelName(), true, cellWidth.getCellWidth(1));
                firstCell++;
                setCellText(getCell(table, dataRow,firstCell), creditsDetailModel.getChildrenNameModelName(), true, cellWidth.getCellWidth(1));
                if(!mergeRowMap.containsKey(creditsDetailModel.getModelName())){
                    int[] rowArr=new int[2];
                    rowArr[0]=dataRow;
                    rowArr[1]=dataRow;
                    mergeRowMap.put(creditsDetailModel.getModelName(), rowArr);
                }else{
                    int[] rowArr = mergeRowMap.get(creditsDetailModel.getModelName());
                    rowArr[1]=dataRow;
                    mergeRowMap.put(creditsDetailModel.getModelName(), rowArr);
                }
            }
            firstCell++;
            setCreditsAndHours(creditsDetailModel,firstCell,table,dataRow,countModel,cellWidth);
            dataRow++;
        }
        //合并行
        mergeRowMap.forEach((k,rowArr)->{
            WordUtil.mergeCellsVertical(table,1,rowArr[0],rowArr[1]);
        });

        Map<String, CreditsDetailModel> disciplineMajorCourseMap = model.getDisciplineMajorCourseMap();
        setCellText(getCell(table, dataRow,0), "学科专业\n课程\n", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, dataRow, dataRow+1);
        // deleteCol(table,dataRow,8,totalCols);

        // ========== 内容 第六行：8 ==========
        setCellText(getCell(table, dataRow,1), DictContent.DISCIPLINE_COURSE_NAME, true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, dataRow, 1,2);
        CreditsDetailModel disciplineDetail = disciplineMajorCourseMap.get(DictContent.DISCIPLINE_COURSE_NAME);
        setCreditsAndHours(disciplineDetail, 2,table, dataRow,countModel, cellWidth);
        // deleteCol(table,dataRow,8,totalCols);
        dataRow++;
        // ========== 内容 第七行：9 ==========
        setCellText(getCell(table, dataRow,1), DictContent.MAJOR_COURSE_NAME, true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, dataRow, 1,2);
        CreditsDetailModel majorDetail = disciplineMajorCourseMap.get(DictContent.MAJOR_COURSE_NAME);
        setCreditsAndHours(majorDetail,2, table, dataRow,countModel, cellWidth);
        // deleteCol(table,dataRow,8,totalCols);
        dataRow++;
        //小计
        setCellText(getCell(table, dataRow,0), "小计", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, dataRow, 0, 2);
        setCellText(getCell(table, dataRow,1), countModel.getRequireHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,2), countModel.getRequireCredit()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,3), countModel.getOptionalHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,4), countModel.getOptionalCredit()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,5), countModel.getTotalHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,6), countModel.getTotalCredit()+"", true, cellWidth.getCellWidth(1));
        // deleteCol(table,dataRow,7,totalCols);
        dataRow++;
        //=========表2================
        setCellText(getCell(table, dataRow,0), "实践训练体系", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsVertical(table, 0, dataRow, dataRow+2);
        WordUtil.mergeCellsHorizontal(table, dataRow, 0, 2);
        WordUtil.mergeCellsHorizontal(table, dataRow+1, 0, 2);
        WordUtil.mergeCellsHorizontal(table, dataRow+2, 0, 2);
        // 列1：学时学分要求，跨行6列
        setCellText(getCell(table, dataRow,1), "学时学分要求", true, cellWidth.getCellWidth(6));
        WordUtil.mergeCellsHorizontal(table, dataRow, 1, 6);
        // deleteCol(table,dataRow,2,totalCols);
        dataRow++;
        // ========== 表头第2行（行号1） ==========
        // 列2：必修，跨行2行
        setCellText(getCell(table, dataRow,1), "必修", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, dataRow, 1, 2);
        // 列3：选修，跨行2行
        setCellText(getCell(table, dataRow,2), "选修", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, dataRow, 2, 3);
        // 列3：小计，跨行2行
        setCellText(getCell(table, dataRow,3), "小计", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, dataRow, 3, 4);
        // deleteCol(table,dataRow,4,totalCols);
        dataRow++;

        setCellText(getCell(table, dataRow,1), "学时", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,2), "学分", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,3), "学时", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,4), "学分", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,5), "学时", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,6), "学分", true, cellWidth.getCellWidth(1));
        // deleteCol(table,dataRow,7,totalCols);
        dataRow++;
        //小计
        setCellText(getCell(table, dataRow,0), "小计", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, dataRow, 0, 2);
        CreditsDetailModel trainingProjectCourse = model.getTrainingProjectCourses();
        //实践训练一共就一行 直接用countModel去加 算出合计
        setCreditsAndHours(trainingProjectCourse, 1,table, dataRow,countModel, cellWidth);
        // deleteCol(table,dataRow,7,totalCols);
        dataRow++;
        //合计
        setCellText(getCell(table, dataRow,0), "合计", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, dataRow, 0, 2); setCellText(getCell(table, dataRow,1), countModel.getRequireHours()+"", true, "25%");
        setCellText(getCell(table, dataRow,1), countModel.getRequireHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,2), countModel.getRequireCredit()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,3), countModel.getOptionalHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,4), countModel.getOptionalCredit()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,5), countModel.getTotalHours()+"", true, cellWidth.getCellWidth(1));
        setCellText(getCell(table, dataRow,6), countModel.getTotalCredit()+"", true, cellWidth.getCellWidth(1));
        // deleteCol(table,dataRow,7,totalCols);
        return table;
    }

    private static XWPFTableCell getCell(XWPFTable table, int dataRow,int cell) {
        if(ObjectUtils.isEmpty(table.getRow(dataRow))) {
            table.createRow().createCell();
        }
        return table.getRow(dataRow).getCell(cell);
    }

    private void setCreditsAndHours(CreditsDetailModel creditsDetailModel, int firstCell, XWPFTable table, int dataRow , CountModel countModel, CellWidth cellWidth) {
        if(ObjectUtils.isEmpty(creditsDetailModel)){
            return;
        }
        setCellText(getCell(table, dataRow,firstCell), creditsDetailModel.getRequiredHours() + "", true, cellWidth.getCellWidth(1));
        countModel.setRequireHours(countModel.getRequireHours()+creditsDetailModel.getRequiredHours());

        setCellText(getCell(table, dataRow,firstCell+1), creditsDetailModel.getRequiredCredits() + "", true, cellWidth.getCellWidth(1));
        countModel.setRequireCredit(countModel.getRequireCredit()+creditsDetailModel.getRequiredCredits());

        setCellText(getCell(table, dataRow,firstCell+2), creditsDetailModel.getOptionalHours() + "", true, cellWidth.getCellWidth(1));
        countModel.setOptionalHours(countModel.getOptionalHours()+creditsDetailModel.getOptionalHours());

        setCellText(getCell(table, dataRow,firstCell+3), creditsDetailModel.getOptionalCredits() + "", true, cellWidth.getCellWidth(1));
        countModel.setOptionalCredit(countModel.getOptionalCredit()+creditsDetailModel.getOptionalCredits());

        setCellText(getCell(table, dataRow,firstCell+4), creditsDetailModel.getTotalHours() + "", true, cellWidth.getCellWidth(1));
        countModel.setTotalHours(countModel.getTotalHours()+creditsDetailModel.getTotalHours());

        setCellText(getCell(table, dataRow,firstCell+5), creditsDetailModel.getTotalCredits() + "", true, cellWidth.getCellWidth(1));
        countModel.setTotalCredit(countModel.getTotalCredit()+creditsDetailModel.getTotalCredits());

    }

    /**
     * 生成教学训练体系与安排（课程信息表格）
     */
    private void generateCourseArrangements(TrainingPlanModel model, XWPFDocument document) {
        // 一级标题
        WordUtil.createHeading(document, "四、教学训练体系与安排", 1);
        WordUtil.createParagraph(document,"通识课程任选课程安排表见附录1、专业任选课程安排表见附录2、其他课程和实践训练安排如下：",null);
        //二级标题
        WordUtil.createHeading(document, "（一）课程安排", 2);

        // 公共基础课程教学安排
        if (model.getGeneralCourses() != null && !model.getGeneralCourses().isEmpty()) {
            XWPFTable table = generateCourseTable(document, "1.公共基础课程教学安排", model.getGeneralCourses());
            WordUtil.createParagraph(document, "", null); // 空行
        }
        // 添加备注
        XWPFParagraph remarkParagraph = document.createParagraph();
        XWPFRun remarkRun = remarkParagraph.createRun();
        remarkRun.setText("备注：1.B代表必修课程、X代表限选课程、R代表任选课程；S代表考试，C代表考查（下同）。\n 2.《大学英语》免修规则：请军政基础教育学院补充。");
        remarkRun.setFontSize(10);

        // 学科基础课程
        if (model.getDisciplineCourses() != null && !model.getDisciplineCourses().isEmpty()) {
            XWPFTable table = disciplineCoursesTable(document, "2.学科基础课程教学安排", model.getDisciplineCourses());
            WordUtil.createParagraph(document, "", null); // 空行
        }

        // 专业课程
        if (model.getMajorCourses() != null && !model.getMajorCourses().isEmpty()) {
            XWPFTable table = majorCoursesTable(document, "3.专业课程教学安排", model.getMajorCourses());
            WordUtil.createParagraph(document, "", null); // 空行
        }
        //二级标题
        WordUtil.createHeading(document, "（二）实践训练安排", 2);
        //实践项目训练课程
        if (model.getTrainingSubjectCourses() != null && !model.getTrainingSubjectCourses().isEmpty()) {
            XWPFTable table = practicalProjectCourseTable(document, "1.实践训练课目与安排", model.getTrainingSubjectCourses());
            WordUtil.createParagraph(document, "", null); // 空行
        }
        //2.实践项目与安排
        if (model.getPracticalProjectCourse() != null && !model.getPracticalProjectCourse().isEmpty()) {
            XWPFTable table = trainingSubjectCoursesTable(document, "2.实践项目与安排", model.getPracticalProjectCourse());
            WordUtil.createParagraph(document, "", null); // 空行
        }

    }



    /**
     * 学期安排列数。父类为四年制 8 列（第一学年秋季~第四学年春季）；
     * 第五学年子类重写为 10 列（追加第五学年秋/春）。
     * 供 {@link #setTermCheckmarks} / {@link #setTotalRow} 决定填充多少个学期单元格。
     */
    protected int termColumnCount() {
        return 8;
    }

    /**
     * 生成课程教学安排表格 - 严格按照TableCreateGenerator中的表头结构
     *
     * 表格结构：
     * - 列数：16列（删除第6列和第11列后实际为13列）
     * - 行数：表头3行 + 模块行 + 小计1行
     *
     * 表头结构（3行）：
     * 行0：课程模块 | 课程名称 | 修读要求 | 考核方式 | 学时安排(跨3列) | 学期安排(跨8列)
     * 行1：[ Module行跨3行 ] | | | | 小计 | 讲授 | 实践 | 第一学年(跨2列) | 第二学年(跨2列) | 第三学年(跨2列) | 第四学年(跨2列)
     * 行2：[ Module行跨3行 ] | | | | | | | 秋 | 春 | 秋 | 春 | 秋 | 春 | 秋 | 春
     *
     * 删除第6列（空列）和第11列（空列），所以实际使用13列
     */
    protected XWPFTable generateCourseTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 17; // 模块3+名称1+学分1+修读1+考核1+学时3+学期8 = 18单位，模块占3列故物理17列(原16+学分1)
        CellWidth cellWidth = new CellWidth(19);

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);

        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 生成表头（3行）
        generateCourseHeader(table,cellWidth);


        int dataRowStart = 3;

        Map<String, Map<String, List<TrainingSchemeCourseModel>>> couseMap = TrainingSchemeCourseModel.groupCourses(courses);
        // 处理每一门课程，确定其所属模块
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        CountModel countModel = new CountModel();
        couseMap.forEach((modelName,courseFourLevelMap) -> {
            int moduleRowStart = dataRow.get();
            AtomicInteger moduleRowEnd = new AtomicInteger(moduleRowStart);
            courseFourLevelMap.forEach((fourLevelName,courseModels) -> {
                int fourMoudleStart = dataRow.get();
                int fourMoudleEnd = fourMoudleStart + courseModels.size();
                int initCell=1;
                XWPFTableRow row = table.getRow(fourMoudleStart);
                //判断科学文化下的基础科学等子模块
                if(DictContent.FOUR_LEVEL_NAME_NULL .equals(fourLevelName)){
                    for (int i = fourMoudleStart; i < fourMoudleEnd; i++) {
                        WordUtil.mergeCellsHorizontal(table, i, 0,  1);
                    }
                }else{
                    initCell=2;
                    XWPFTableCell cell1 = row.getCell(1);
                    WordUtil.setCellText(cell1, fourLevelName, false,  cellWidth.getCellWidth(1));
                    WordUtil.mergeCellsVertical(table, 1, fourMoudleStart,  fourMoudleStart+courseModels.size() - 1);
                }
                for (int i = 0; i < courseModels.size(); i++) {
                    TrainingSchemeCourseModel course = courseModels.get(i);
                    // 设置课程数据行
                    int dataRowIndex = fourMoudleStart + i;
                    setCourseDataRow(table, dataRowIndex, course,initCell,countModel,cellWidth,2);
                }

                // 前进到下一个模块的起始行
                dataRow.set(fourMoudleEnd);
                // 更新模块结束行
                moduleRowEnd.set(fourMoudleEnd);
            });
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtil.setCellText(cell1, modelName, false,  cellWidth.getCellWidth(1));
            WordUtil.mergeCellsVertical(table, 0, moduleRowStart,  moduleRowEnd.get() - 1);

        });
        // 设置小计行（学时三列起始=7，学期起始=10）
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow,countModel,6,9,cellWidth);
        // deleteCol(table,totalRow,15,16);

        return table;
    }

    protected XWPFTable disciplineCoursesTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 15; // 名称1+学分1+修读1+考核1+学时3+学期8 = 15列

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);

        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(15);

        // 生成表头（3行）
        disciplineCoursesHeader(table,cellWidth);


        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        CountModel countModel = new CountModel();
        for (int i = 0; i < courses.size(); i++) {
            TrainingSchemeCourseModel course = courses.get(i);
            // 设置课程数据行
            int dataRowIndex = dataRowStart + i;
            setCourseDataRow(table, dataRowIndex, course,0,countModel, cellWidth,1);
        }

        // 设置小计行（学时三列起始=4，学期起始=7）
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow,countModel,4,7, cellWidth);

        return table;
    }

    protected XWPFTable majorCoursesTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);
        Map<String, List<TrainingSchemeCourseModel>> couseMap = courses.stream().collect(Collectors.groupingBy(course -> course.getSubMajorName()==null?"**专业方向":course.getSubMajorName()));

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows +couseMap.size();
        int totalCols = 16; // 专业方向1+名称1+学分1+修读1+考核1+学时3+学期8 = 16列

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);

        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(16);
        // 生成表头（3行）
        majorCoursesHeader(table,cellWidth);


        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        //todo 专业方向字段
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        couseMap.forEach((majorName,courseModels) -> {
            int moduleRowStart = dataRow.get();
            int moduleRowEnd = dataRow.get()+courseModels.size();
            CountModel countModel = new CountModel();
            for (int i = 0; i < courseModels.size(); i++) {
                TrainingSchemeCourseModel course = courseModels.get(i);
                // 设置课程数据行
                int dataRowIndex = moduleRowStart + i;
                setCourseDataRow(table, dataRowIndex, course,1,countModel, cellWidth,1);
            }
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtil.setCellText(cell1, majorName, false, "1134");
            WordUtil.mergeCellsVertical(table, 0, moduleRowStart,  moduleRowEnd - 1);
            // 设置小计行（学时三列起始=5，学期起始=8）
            setTotalRow(table, moduleRowEnd,countModel,5,8, cellWidth);
            dataRow.set(moduleRowEnd+1);
        });
        return table;
    }



    protected XWPFTable practicalProjectCourseTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头1行 + 数据行+小计
        int dataRows = courses.size();
        int totalRows =  dataRows + 1;
        int totalCols = 6; // 原始6

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);

        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(6);
        // 生成表头（1行）
        practicalProjectCourseHeader(table,cellWidth);
        int dataRowStart = 1;

        Map<String, List<TrainingSchemeCourseModel>> courseMap = courses.stream().collect(Collectors.groupingBy(c -> c.getTrainingCourseModelName() == null ? "" : c.getTrainingCourseModelName()));
        // 处理每一门课程，确定其所属模块
        AtomicInteger moduleRowStart = new AtomicInteger(dataRowStart);
        CountModel countModel = new CountModel();
        courseMap.forEach((modelName,courseModels) -> {
            int moduleRowEnd = moduleRowStart.get()+courseModels.size();
            for (int i = 0; i < courseModels.size(); i++) {
                TrainingSchemeCourseModel course = courseModels.get(i);
                // 设置课程数据行
                int dataRowIndex = moduleRowStart.get() + i;
                XWPFTableRow row = table.getRow(dataRowIndex);
                //课程名称
                XWPFTableCell cell1 = row.getCell(1);
                WordUtil.setCellText(cell1, course.getName(), false, cellWidth.getCellWidth(1));
                //修读要求
                XWPFTableCell cell2 = row.getCell(2);
                String attrText = getAttrText(course.getCourseAttr());
                WordUtil.setCellText(cell2, attrText, false, cellWidth.getCellWidth(1));
                //时间安排
                XWPFTableCell cell3 = row.getCell(3);
                String timeWeek = course.getTimeWeek() == null ? "" : course.getTimeWeek().toString();
                String timeWeekText = timeWeek + (course.getUnit() == null ? "周" : course.getUnit());
                WordUtil.setCellText(cell3, timeWeekText, false, cellWidth.getCellWidth(1));
                //学期安排
                XWPFTableCell cell4 = row.getCell(4);
                StringBuilder opentermStr=new StringBuilder();
                for (Integer openTerm : course.getOpenTerm()) {
                    if(ObjectUtils.isNotEmpty(openTerm)){
                        opentermStr.append(term[openTerm-1]+",");
                    }
                }
                if(ObjectUtils.isNotEmpty(opentermStr)){
                    WordUtil.setCellText(cell4,opentermStr.deleteCharAt(opentermStr.length()-1).toString(), false, cellWidth.getCellWidth(1));
                }
                //支撑课程或实践训练课目
                XWPFTableCell cell5 = row.getCell(5);
                WordUtil.setCellText(cell5, course.getRemark(), false, cellWidth.getCellWidth(1));
            }
            //设置模块行
            XWPFTableRow row = table.getRow(moduleRowStart.get());
            XWPFTableCell cell1 = row.getCell(0);
            WordUtil.setCellText(cell1, modelName, false, cellWidth.getCellWidth(1));
            WordUtil.mergeCellsVertical(table, 0, moduleRowStart.get(),  moduleRowEnd - 1);
            moduleRowStart.set(moduleRowEnd);
        });
        return table;
    }

    protected XWPFTable trainingSubjectCoursesTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头1行 + 数据行
        int dataRows = courses.size();
        int totalRows =  dataRows + 1;
        int totalCols = 7; // 专业方向 + 项目层级 + 项目名称 + 修读要求 + 时间安排 + 学期安排 + 支撑课程

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(totalCols);
        // 生成表头（1行）
        trainingSubjectCoursesHeader(table,cellWidth);


        int dataRowStart = 1;

        // 两层分组：外层专业方向(subMajorName) -> 内层项目层级(projectLevelName)，使用 LinkedHashMap 保持输入顺序
        Map<String, Map<String, List<TrainingSchemeCourseModel>>> subMajorMap = courses.stream().collect(
                Collectors.groupingBy(
                        c -> c.getSubMajorName() == null ? "" : c.getSubMajorName(),
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                c -> c.getProjectLevelName() == null ? "**" : c.getProjectLevelName(),
                                LinkedHashMap::new,
                                Collectors.toList())));
        // 处理每一门课程，确定其所属模块
        AtomicInteger moduleRowStart = new AtomicInteger(dataRowStart);
        subMajorMap.forEach((subMajorName, levelMap) -> {
            int subMajorStart = moduleRowStart.get();
            levelMap.forEach((levelName, courseModels) -> {
                int levelStart = moduleRowStart.get();
                int levelEnd = levelStart + courseModels.size();
                for (int i = 0; i < courseModels.size(); i++) {
                    TrainingSchemeCourseModel course = courseModels.get(i);
                    // 设置课程数据行
                    int dataRowIndex = levelStart + i;
                    XWPFTableRow row = table.getRow(dataRowIndex);
                    //项目名称
                    XWPFTableCell cell1 = row.getCell(2);
                    WordUtil.setCellText(cell1, course.getName(), false, cellWidth.getCellWidth(1));
                    //修读要求
                    XWPFTableCell cell2 = row.getCell(3);
                    String attrText = getAttrText(course.getCourseAttr());
                    WordUtil.setCellText(cell2, attrText, false, cellWidth.getCellWidth(1));
                    //时间安排
                    XWPFTableCell cell3 = row.getCell(4);
                    String timeWeek = course.getTimeWeek() == null ? "" : course.getTimeWeek().toString();
                    String timeWeekText = timeWeek + (course.getUnit() == null ? "周" : course.getUnit());
                    WordUtil.setCellText(cell3, timeWeekText, false, cellWidth.getCellWidth(1));
                    //学期安排
                    XWPFTableCell cell4 = row.getCell(5);
                    StringBuilder opentermStr=new StringBuilder();
                    for (Integer openTerm : course.getOpenTerm()) {
                        if(ObjectUtils.isNotEmpty(openTerm)){
                            opentermStr.append(term[openTerm-1]+",");
                        }
                    }
                    if(ObjectUtils.isNotEmpty(opentermStr)){
                        WordUtil.setCellText(cell4,opentermStr.deleteCharAt(opentermStr.length()-1).toString(), false, cellWidth.getCellWidth(1));
                    }
                    //支撑课程或实践训练课目
                    XWPFTableCell cell5 = row.getCell(6);
                    WordUtil.setCellText(cell5, course.getSupportingCourseNames(), false, cellWidth.getCellWidth(1));
                }
                //设置项目层级列(col1)合并
                XWPFTableRow levelRow = table.getRow(levelStart);
                WordUtil.setCellText(levelRow.getCell(1), levelName, false, cellWidth.getCellWidth(1));
                WordUtil.mergeCellsVertical(table, 1, levelStart, levelEnd - 1);
                moduleRowStart.set(levelEnd);
            });
            //设置专业方向列(col0)合并，字段为空显示为空
            int subMajorEnd = moduleRowStart.get();
            if (subMajorEnd > subMajorStart) {
                XWPFTableRow subMajorRow = table.getRow(subMajorStart);
                WordUtil.setCellText(subMajorRow.getCell(0), subMajorName == null ? "" : subMajorName, false, cellWidth.getCellWidth(1));
                WordUtil.mergeCellsVertical(table, 0, subMajorStart, subMajorEnd - 1);
            }
        });

        // 设置小计行
//        int totalRow = totalRows - 1;
//        setTotalRow(table, totalRow);
//        // deleteCol(table,totalRow,5,6);

        return table;
    }

    /**
     * 填充通识课程表格表头（3行）
     */
    protected void generateCourseHeader(XWPFTable table, CellWidth cellWidth) {
        // 列布局(18列,塌缩后索引)：模块gs2(0) | 名称gs2(1) | 学分(2) | 修读(3) | 考核(4) | 学时gs3(5) | 学期gs8(6)
        // poi 水平合并后单元格索引塌缩，后续 getCell 取塌缩后位置(沿用原表头写法)
        // ========== 表头第1行（行号0） ==========
        // 列0：课程模块，跨行3行(模块表头gs3，沿用原塌缩后索引写法)
        setCellText(table.getRow(0).getCell(0), "课程模块", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);
        WordUtil.mergeCellsHorizontal(table, 0, 0, 1);
        WordUtil.mergeCellsHorizontal(table, 1, 0, 1);
        WordUtil.mergeCellsHorizontal(table, 2, 0, 1);

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 学分，跨3行
        setCellText(table.getRow(0).getCell(2), "学分", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 修读要求，跨3行
        setCellText(table.getRow(0).getCell(3), "修读\n要求", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        // 考核方式，跨3行
        setCellText(table.getRow(0).getCell(4), "考核\n方式", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 0, 2);

        // 学时安排，跨3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(5), "学时安排", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 7);

        // 学期安排，跨8列（4个学年，每个学年2个学期）
        setCellText(table.getRow(0).getCell(6), "学期安排", true, cellWidth.getCellWidth(8));
        WordUtil.mergeCellsHorizontal(table, 0, 6, 13);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(5), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);
        setCellText(table.getRow(1).getCell(7), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 7, 1, 2);

        // 第一~第四学年，每学年跨2列（塌缩后依次 8/9/10/11）
        setCellText(table.getRow(1).getCell(8), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);
        setCellText(table.getRow(1).getCell(11), "第四学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 11, 12);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替，塌缩后依次 8~15）
        setCellText(table.getRow(2).getCell(8), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "春", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "秋", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(15), "春", true, cellWidth.getCellWidth(1));
    }

    /**
     * 专业大类课程教学安排
     *
     * @param table
     * @param cellWidth
     */
    protected void disciplineCoursesHeader(XWPFTable table, CellWidth cellWidth) {
        // ========== 表头第1行（行号0） ==========

        // 列1：课程名称，跨行3行
        setCellText(table.getRow(0).getCell(0), "课程名称", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);

        // 学分，跨3行
        setCellText(table.getRow(0).getCell(1), "学分", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 修读要求，跨行3行
        setCellText(table.getRow(0).getCell(2), "修读\n要求", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 考核方式，跨行3行
        setCellText(table.getRow(0).getCell(3), "考核\n方式", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        // 学时安排，跨列3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(4), "学时安排", true,  cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 4, 6);

        // 学期安排，跨列8列（4个学年，每个学年2个学期）塌缩后从5起
        setCellText(table.getRow(0).getCell(5), "学期安排", true,  cellWidth.getCellWidth(8));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 12);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(4), "小计", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);

        // 第一~第四学年，每学年跨2列（塌缩后依次 7/8/9/10）
        setCellText(table.getRow(1).getCell(7), "第一学年", true,  cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 7, 8);
        setCellText(table.getRow(1).getCell(8), "第二学年", true,  cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第三学年", true,  cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第四学年", true,  cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替）
        setCellText(table.getRow(2).getCell(7), "秋", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(8), "春", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "秋", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "春", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "秋", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "春", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "秋", true,  cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "春", true,  cellWidth.getCellWidth(1));
    }
    /**
     * 专业方向课程教学安排
     *
     * @param table
     * @param cellWidth
     */
    protected void majorCoursesHeader(XWPFTable table, CellWidth cellWidth) {
        // 列布局(16列)：专业方向(0) | 名称(1) | 学分(2) | 修读(3) | 考核(4) | 学时(5-7) | 学期(8-15)
        // ========== 表头第1行（行号0） ==========
        setCellText(table.getRow(0).getCell(0), "专业方向", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);

        setCellText(table.getRow(0).getCell(1), "课程名称", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 学分，跨3行
        setCellText(table.getRow(0).getCell(2), "学分", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        setCellText(table.getRow(0).getCell(3), "修读\n要求", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        setCellText(table.getRow(0).getCell(4), "考核\n方式", true,  cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 0, 2);

        // 学时安排，跨列3列（小计、讲授、实践）
        setCellText(table.getRow(0).getCell(5), "学时安排", true,  cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 7);

        // 学期安排，跨列8列（4个学年，每个学年2个学期）塌缩后从6起
        setCellText(table.getRow(0).getCell(6), "学期安排", true, cellWidth.getCellWidth(8));
        WordUtil.mergeCellsHorizontal(table, 0, 6, 13);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(5), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);
        setCellText(table.getRow(1).getCell(7), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 7, 1, 2);

        // 第一~第四学年，每学年跨2列（塌缩后依次 8/9/10/11）
        setCellText(table.getRow(1).getCell(8), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);
        setCellText(table.getRow(1).getCell(11), "第四学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 11, 12);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替）
        setCellText(table.getRow(2).getCell(8), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(15), "春", true, cellWidth.getCellWidth(1));
    }

    private void practicalProjectCourseHeader(XWPFTable table, CellWidth cellWidth) {
        setCellText(table.getRow(0).getCell(0), "课目模块", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(1), "课目名称", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(2), "修读要求", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(3), "时间安排", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(4), "学期安排", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(5), "备注", true, cellWidth.getCellWidth(1));
    }

    private void trainingSubjectCoursesHeader(XWPFTable table, CellWidth cellWidth) {
        setCellText(table.getRow(0).getCell(0), "专业方向", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(1), "项目层级", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(2), "项目名称", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(3), "修读要求", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(4), "时间安排", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(5), "学期安排", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(0).getCell(6), "支撑课程或实践训练课目", true, cellWidth.getCellWidth(1));
    }

    /**
     * 删除指定行列的单元格（用于删除空列）
     */
    private void  deleteCol(XWPFTable table, int row, int startCol, int endCol) {
        XWPFTableRow currentRow = table.getRow(row);
        for (int col = endCol; col >= startCol; col--) {
            if (currentRow.getCell(col) != null) {
                currentRow.removeCell(col); // 删除指定列的冗余单元格
            }
        }
    }

    /**
     * 设置表格单元格内容 - 带所有属性
     */
    protected void setCellText(XWPFTableCell cell, String text, boolean isBold, String width) {
        if(ObjectUtils.isEmpty(cell)){
            return;
        }
        // 清空单元格默认段落
        while (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }
        XWPFParagraph paragraph = cell.addParagraph();
        // 水平居中
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        // 垂直居中
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        // 设置单元格宽度
        if (width != null && !width.isEmpty()) {
            cell.setWidth(width);
        }
        // 设置文本
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("宋体");
        run.setText(text);
        run.setFontSize(9);
        if (isBold) {
            run.setBold(true);
        }
    }
    /**
     * 设置课程数据行
     */
    protected void setCourseDataRow(XWPFTable table, int rowIndex, TrainingSchemeCourseModel course, int initCell, CountModel countModel, CellWidth cellWidth,Integer nameSpan) {
        setCourseDataRow(table, rowIndex, course, initCell, nameSpan, countModel, cellWidth);
    }

    /**
     * 设置课程数据行
     * @param nameSpan 课程名称占的物理列数(公共基础表为2，其余为1)。nameSpan>1时名称单元格横向合并。
     * 说明：poi 水平合并会移除被合并单元格使索引塌缩；nameSpan=2合并后正好抵消多占的1列，
     *       故无论 nameSpan 取1或2，后续单元格的"塌缩后索引"均为 initCell+1, initCell+2, ...
     */
    protected void setCourseDataRow(XWPFTable table, int rowIndex, TrainingSchemeCourseModel course, int initCell, int nameSpan, CountModel countModel, CellWidth cellWidth) {
        XWPFTableRow row = table.getRow(rowIndex);

        // 课程名称
        XWPFTableCell cell1 = row.getCell(initCell);
        WordUtil.setCellText(cell1, course.getName(), false,  cellWidth.getCellWidth(nameSpan));
//        if (nameSpan > 1) {
//            WordUtil.mergeCellsHorizontal(table, rowIndex, initCell, initCell + nameSpan - 1);
//        }

        // 学分（课程名称后，来源 course.getCredits()）
        XWPFTableCell cellCredits = row.getCell(initCell+1);
        WordUtil.setCellText(cellCredits, course.getCredits() != null ? course.getCredits().toString() : "", false,  cellWidth.getCellWidth(1));
        double m_credits = countModel.getTotalCredits() == null ? 0 : countModel.getTotalCredits();
        double c_credits = course.getCredits() == null ? 0 : course.getCredits();
        countModel.setTotalCredits(m_credits+c_credits);

        // 修读要求（B=必修，X=限选，R=任选）
        XWPFTableCell cell2 = row.getCell(initCell+2);
        String attrText = getAttrText(course.getCourseAttr());
        WordUtil.setCellText(cell2, attrText, false,  cellWidth.getCellWidth(1));

        // 考核方式（S=考试，C=考查）
        XWPFTableCell cell3 = row.getCell(initCell+3);
        String assessText = getAssessText(course.getExaMethod());
        WordUtil.setCellText(cell3, assessText, false,  cellWidth.getCellWidth(1));

        // 小计
        XWPFTableCell cell4 = row.getCell(initCell+4);
        WordUtil.setCellText(cell4, course.getHours() != null ? course.getHours().toString() : "", false,  cellWidth.getCellWidth(1));
        countModel.setTotalHours(countModel.getTotalHours()+course.getHours());

        // 讲授
        XWPFTableCell cell5 = row.getCell(initCell+5);
        WordUtil.setCellText(cell5, course.getTeachHours() != null ? course.getTeachHours().toString() : "", false,  cellWidth.getCellWidth(1));
        double m_hours = countModel.getTeachHours() == null ? 0 : countModel.getTeachHours();
        double c_hours = course.getTeachHours() == null ? 0 : course.getTeachHours();
        countModel.setTeachHours(m_hours+c_hours);

        // 实践
        XWPFTableCell cell6 = row.getCell(initCell+6);
        WordUtil.setCellText(cell6, course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false,  cellWidth.getCellWidth(1));
        double m_practiceHours = countModel.getPracticeHours() == null ? 0 : countModel.getPracticeHours();
        double c_practiceHours = course.getPracticeHours() == null ? 0 : course.getPracticeHours();
        countModel.setPracticeHours(m_practiceHours+c_practiceHours);

        // 学期安排（在开课学期填写对应学时）
        setTermCheckmarks(row, course,initCell+7,cellWidth,countModel);
    }

//    private void setGenerateCourseDataRow(XWPFTable table, int rowIndex, TrainingSchemeCourseModel course,int initCell) {
//        XWPFTableRow row = table.getRow(rowIndex);
//        //列0：课程模块
//
//        // 列1：课程名称
//        XWPFTableCell cell1 = row.getCell(initCell);
//        WordUtil.setCellText(cell1, course.getName(), false, "1985");
//
//        // 列2：修读要求（B=必修，X=限选，R=任选）
//        XWPFTableCell cell2 = row.getCell(initCell+1);
//        String attrText = getAttrText(course.getCourseAttrName());
//        WordUtil.setCellText(cell2, attrText, false, "1021");
//
//        // 列3：考核方式（S=考试，C=考查）
//        XWPFTableCell cell3 = row.getCell(initCell+2);
//        String assessText = getAssessText(course.getExaMethod());
//        WordUtil.setCellText(cell3, assessText, false, "1021");
//
//        // 列4：小计
//        XWPFTableCell cell4 = row.getCell(initCell+3);
//        WordUtil.setCellText(cell4, course.getHours() != null ? course.getHours().toString() : "", false, "851");
//
//        // 列5：讲授
//        XWPFTableCell cell5 = row.getCell(initCell+4);
//        WordUtil.setCellText(cell5, course.getTeachHours() != null ? course.getTeachHours().toString() : "", false, "851");
//
//        // 列6：实践
//        XWPFTableCell cell6 = row.getCell(initCell+5);
//        WordUtil.setCellText(cell6, course.getPracticeHours() != null ? course.getPracticeHours().toString() : "", false, "851");
//
//        // 列7-12：学期安排（根据semesterSchedule和springAutumn判断是否打勾）
//        setTermCheckmarks(row, course,initCell+6);
//    }

    /**
     * 设置学期安排：在开课学期填写该学期对应的拆分学时（讲授+实践），未开课学期留空
     * 并将各学期学时累加到countModel中，供小计行统计该列总学时
     * 拆分学时优先取course.termHoursMap；若无（数据缺失），则按开课学期数均分课程总学时
     */
    protected void setTermCheckmarks(XWPFTableRow row, TrainingSchemeCourseModel course, int initCell, CellWidth cellWidth, CountModel countModel) {
        List<Integer> openTermList = course.getOpenTerm();
        Map<Integer, Double> termHoursMap = course.getTermHoursMap();
        Double totalHours = course.getHours();
        int termCount = termColumnCount();

        // 默认值：termCount 个学期均留空
        String[] termValues = new String[termCount];
        Arrays.fill(termValues, "");
        if (ObjectUtils.isNotEmpty(openTermList)) {
            //过滤出有效的开课学期
            List<Integer> validTerms = openTermList.stream()
                    .filter(Objects::nonNull)
                    .filter(t -> t >= 1 && t <= termCount)
                    .collect(Collectors.toList());
            for (Integer openTerm : validTerms) {
                int idx = openTerm - 1;
                //优先使用按学期拆分的学时；若无拆分数据则按开课学期数均分总学时
                Double termHours = (termHoursMap != null) ? termHoursMap.get(openTerm) : null;
                if (termHours == null && totalHours != null && !validTerms.isEmpty()) {
                    termHours = totalHours / validTerms.size();
                }
                if (termHours != null) {
                    termValues[idx] = formatHours(termHours);
                    if (countModel != null) {
                        countModel.addTermHours(idx, termHours);
                    }
                }
            }
        }

        // 设置单元格（termCount 个学期对应列）
        for (int i = 0; i < termCount; i++) {
            WordUtil.setCellText(row.getCell(initCell + i), termValues[i], false, cellWidth.getCellWidth(1));
        }
    }

    /**
     * 格式化学时：整数显示为整型，否则保留有效小数
     */
    private String formatHours(Double hours) {
        if (hours == null) {
            return "";
        }
        double v = hours;
        if (v == Math.rint(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return new java.math.BigDecimal(v).stripTrailingZeros().toPlainString();
    }

    /**
     * 设置小计行
     *
     * @param dataCell       学时安排（小计/讲授/实践）三列的起始列
     * @param termStartCell  学期安排列的起始列，用于填写各学期总学时
     * 说明：小计行为全新行(无先前合并)，按物理列绝对索引填文本，最后再合并标签区。
     *       学分位于学时三列前一列(creditCol=dataCell-3)，修读/考核留空(默认空单元格)。
     *       "小计"标签合并 0..creditCol-1(覆盖模块+名称等前缀列)：
     *         公共基础 creditCol=3 → 合并0..2(模块+子模块+名称)
     *         学科基础 creditCol=1 → 不合并(仅列0填"小计")
     *         专业课程 creditCol=2 → 合并0..1(专业方向+名称)
     */
    protected void setTotalRow(XWPFTable table, int rowIndex, CountModel countModel, int dataCell, int termStartCell, CellWidth cellWidth) {
        XWPFTableRow row = table.getRow(rowIndex);
        int termCount = termColumnCount();
        int creditCol = dataCell - 3;

        // 1) 先按物理列绝对索引填文本
        // "小计"标签(宽度覆盖 0..creditCol-1)
        WordUtil.setCellText(row.getCell(0), "小  计", true, cellWidth.getCellWidth(creditCol));
        // 学分小计
        WordUtil.setCellText(row.getCell(creditCol), countModel.getTotalCredits()+"", true, cellWidth.getCellWidth(1));
        // 修读要求(creditCol+1)、考核方式(creditCol+2) 留空(默认空单元格，不填)
        // 学时小计/讲授/实践
        WordUtil.setCellText(row.getCell(dataCell), countModel.getTotalHours()+"", true, cellWidth.getCellWidth(1));
        WordUtil.setCellText(row.getCell(dataCell+1), countModel.getTeachHours()+"", true, cellWidth.getCellWidth(1));
        WordUtil.setCellText(row.getCell(dataCell+2), countModel.getPracticeHours()+"", true, cellWidth.getCellWidth(1));
        // 学期安排各列总学时
        Double[] termHours = countModel.getTermHours();
        for (int i = 0; i < termCount; i++) {
            WordUtil.setCellText(row.getCell(termStartCell + i), formatHours(termHours[i]), true, cellWidth.getCellWidth(1));
        }

        // 2) 最后合并"小计"标签区 0..creditCol-1(creditCol>1时才合并)
        if (creditCol > 1) {
            WordUtil.mergeCellsHorizontal(table, rowIndex, 0, creditCol - 1);
        }
    }

    /**
     * 获取修读要求文本（B=必修，X=限选，R=任选）
     */
    private String getAttrText(String attrName) {
        if (attrName == null) {
            return "";
        }
        // 根据课程属性名称获取对应代码
        if (attrName.contains(ConstantTrainingScheme.COMPULSORY_COURSE) ) {
            return "B";
        } else if (attrName.contains(ConstantTrainingScheme.ELECTIVE_COURSE) ) {
            return "X";
        } else if (attrName.contains(ConstantTrainingScheme.OPTIONAL_COURSE) ) {
            return "R";
        }
        return attrName;
    }

    /**
     * 获取考核方式文本（S=考试，C=考查）
     */
    private String getAssessText(String exaMethod) {
        if (exaMethod == null) {
            return "";
        }
        // 根据开课学期判断考核方式
        if (exaMethod.contains("1") || exaMethod.contains("S")) {
            return "S";
        } else if (exaMethod.contains("2") || exaMethod.contains("C")) {
            return "C";
        }
        return exaMethod;
    }


}
