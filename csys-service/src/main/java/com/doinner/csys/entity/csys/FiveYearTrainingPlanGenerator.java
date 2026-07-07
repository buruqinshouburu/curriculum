package com.doinner.csys.entity.csys;

import com.doinner.csys.entity.csys.model.CellWidth;
import com.doinner.csys.entity.csys.model.CountModel;
import com.doinner.csys.entity.csys.model.DictContent;
import com.doinner.csys.entity.csys.model.TrainingSchemeCourseModel;
import com.doinner.csys.utils.WordUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 第五学年培养方案文档生成器。
 *
 * 适用对象（培养层次 educationLevel）为 7（英烈子女班）或 8（生长军官学员及英烈子女班）时，
 * 由服务层 {@code TrainingServiceImpl.createTrainingPlanGenerator} 选择本子类实例化；
 * 其他值仍使用父类 {@link TrainingPlanGenerator}（四年制，原样）。
 *
 * 实现方式：父类建表与数据填充逻辑保持原样不动，本子类只针对三张课程表
 * （公共基础 / 学科基础 / 专业课程）重写表头与建表方法，把"学期安排"由 8 列扩为 10 列，
 * 追加"第五学年 秋/春"两列，并按现有方式把第五学年学时填入对应位置。
 * 数据行/小计行的学期填充复用父类 {@link #setCourseDataRow}/{@link #setTotalRow}，
 * 通过 {@link #termColumnCount()} 返回 10 使其填写 10 个学期单元格。
 *
 * poi 合并单元格后 cell 索引会塌缩（如合并 7,8 后只剩一个，下一个取 8），
 * 因此第五学年两列沿用与原表头相同的"塌缩后索引"写法，避免错位。
 */
public class FiveYearTrainingPlanGenerator extends TrainingPlanGenerator {

    /** 五年制：学期安排 10 列（第一~第五学年，每学年秋/春） */
    @Override
    protected int termColumnCount() {
        return 10;
    }

    // ============================ 公共基础课程教学安排 ============================

    @Override
    protected XWPFTable generateCourseTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 19; // 模块3+名称1+修读1+考核1+学分1+学时3+学期10 = 20单位，模块占3列故物理19列(原18+学分1)
        CellWidth cellWidth = new CellWidth(21);

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");

        // 生成表头（3行）——第五学年版
        generateCourseHeader(table, cellWidth);

        int dataRowStart = 3;

        Map<String, Map<String, List<TrainingSchemeCourseModel>>> couseMap = TrainingSchemeCourseModel.groupCourses(courses);
        // 处理每一门课程，确定其所属模块
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        CountModel countModel = new CountModel();
        couseMap.forEach((modelName, courseFourLevelMap) -> {
            int moduleRowStart = dataRow.get();
            AtomicInteger moduleRowEnd = new AtomicInteger(moduleRowStart);
            courseFourLevelMap.forEach((fourLevelName, courseModels) -> {
                int fourMoudleStart = dataRow.get();
                int fourMoudleEnd = fourMoudleStart + courseModels.size();
                int initCell = 1;
                XWPFTableRow row = table.getRow(fourMoudleStart);
                //判断科学文化下的基础科学等子模块
                if (DictContent.FOUR_LEVEL_NAME_NULL.equals(fourLevelName)) {
                    for (int i = fourMoudleStart; i < fourMoudleEnd; i++) {
                        WordUtil.mergeCellsHorizontal(table, i, 0, 1);
                    }
                } else {
                    initCell = 2;
                    XWPFTableCell cell1 = row.getCell(1);
                    WordUtil.setCellText(cell1, fourLevelName, false, cellWidth.getCellWidth(1));
                    WordUtil.mergeCellsVertical(table, 1, fourMoudleStart, fourMoudleStart + courseModels.size() - 1);
                }
                for (int i = 0; i < courseModels.size(); i++) {
                    TrainingSchemeCourseModel course = courseModels.get(i);
                    // 设置课程数据行（学期列数由 termColumnCount()=10 决定）
                    int dataRowIndex = fourMoudleStart + i;
                    setCourseDataRow(table, dataRowIndex, course, initCell, countModel, cellWidth,2);
                }

                // 前进到下一个模块的起始行
                dataRow.set(fourMoudleEnd);
                // 更新模块结束行
                moduleRowEnd.set(fourMoudleEnd);
            });
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtil.setCellText(cell1, modelName, false, cellWidth.getCellWidth(1));
            WordUtil.mergeCellsVertical(table, 0, moduleRowStart, moduleRowEnd.get() - 1);

        });
        // 设置小计行（学时三列起始=7，学期起始=10）
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow, countModel, 6, 9, cellWidth);

        return table;
    }

    /**
     * 公共基础课程表头（3行）——在原四年制表头基础上追加"第五学年 秋/春"两列。
     * poi 水平合并后单元格索引塌缩，故各学年表头取塌缩后位置（第一学年7、第二学年8、第三学年9、第四学年10、第五学年11）。
     */
    @Override
    protected void generateCourseHeader(XWPFTable table, CellWidth cellWidth) {
        // 列布局(19列,塌缩后索引)：模块gs3(0)|名称(1)|修读(2)|考核(3)|学分(4)|学时gs3(5)|学期gs10(6)
        // ========== 表头第1行（行号0） ==========
        // 课程模块，跨3行
        setCellText(table.getRow(0).getCell(0), "课程模块", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);
        WordUtil.mergeCellsHorizontal(table, 0, 0, 1);
        WordUtil.mergeCellsHorizontal(table, 1, 0, 1);
        WordUtil.mergeCellsHorizontal(table, 2, 0, 1);

        // 课程名称，跨3行
        setCellText(table.getRow(0).getCell(1), "课程名称", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 修读要求，跨3行
        setCellText(table.getRow(0).getCell(2), "修读\n要求", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 考核方式，跨3行
        setCellText(table.getRow(0).getCell(3), "考核\n方式", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        // 学分，跨3行（位于考核方式之后）
        setCellText(table.getRow(0).getCell(4), "学分", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 0, 2);

        // 学时安排，跨3列
        setCellText(table.getRow(0).getCell(5), "学时安排", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 7);

        // 学期安排：跨10列（5个学年，每个学年2个学期）塌缩后从6起
        setCellText(table.getRow(0).getCell(6), "学期安排", true, cellWidth.getCellWidth(10));
        WordUtil.mergeCellsHorizontal(table, 0, 6, 15);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(5), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);
        setCellText(table.getRow(1).getCell(7), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 7, 1, 2);

        // 第一~第五学年，每学年跨2列（塌缩后依次 8/9/10/11/12）
        setCellText(table.getRow(1).getCell(8), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);
        setCellText(table.getRow(1).getCell(11), "第四学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 11, 12);
        setCellText(table.getRow(1).getCell(12), "第五学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 12, 13);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替，共10列，塌缩后依次 8~17）
        setCellText(table.getRow(2).getCell(8), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(15), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(16), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(17), "春", true, cellWidth.getCellWidth(1));
    }

    // ============================ 学科基础课程教学安排 ============================

    @Override
    protected XWPFTable disciplineCoursesTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);

        // 计算总行数：表头3行 + 数据行 + 小计1行
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + 1;
        int totalCols = 17; // 名称1+修读1+考核1+学分1+学时3+学期10 = 17列

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(17);

        // 生成表头（3行）——第五学年版
        disciplineCoursesHeader(table, cellWidth);

        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        CountModel countModel = new CountModel();
        for (int i = 0; i < courses.size(); i++) {
            TrainingSchemeCourseModel course = courses.get(i);
            // 设置课程数据行
            int dataRowIndex = dataRowStart + i;
            setCourseDataRow(table, dataRowIndex, course, 0, countModel, cellWidth,1);
        }

        // 设置小计行（学时三列起始=4，学期起始=7）
        int totalRow = totalRows - 1;
        setTotalRow(table, totalRow, countModel, 4, 7, cellWidth);

        return table;
    }

    /**
     * 学科基础课程表头（3行）——在原四年制表头基础上追加"第五学年 秋/春"两列。
     * 该表无"课程模块"列，学期从列6开始，学年表头塌缩后依次 6/7/8/9/10。
     */
    @Override
    protected void disciplineCoursesHeader(XWPFTable table, CellWidth cellWidth) {
        // 列布局(17列)：名称(0) | 修读(1) | 考核(2) | 学分(3) | 学时(4-6) | 学期(7-16,10列)
        // ========== 表头第1行（行号0） ==========
        setCellText(table.getRow(0).getCell(0), "课程名称", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);

        // 修读要求，跨3行
        setCellText(table.getRow(0).getCell(1), "修读\n要求", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 考核方式，跨3行
        setCellText(table.getRow(0).getCell(2), "考核\n方式", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 学分，跨3行（位于考核方式之后）
        setCellText(table.getRow(0).getCell(3), "学分", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        // 学时安排，跨3列
        setCellText(table.getRow(0).getCell(4), "学时安排", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 4, 6);

        // 学期安排：跨10列（5个学年）塌缩后从5起
        setCellText(table.getRow(0).getCell(5), "学期安排", true, cellWidth.getCellWidth(10));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 14);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(4), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 1, 2);
        setCellText(table.getRow(1).getCell(5), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);

        // 第一~第五学年，每学年跨2列（塌缩后依次 7/8/9/10/11）
        setCellText(table.getRow(1).getCell(7), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 7, 8);
        setCellText(table.getRow(1).getCell(8), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第四学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);
        setCellText(table.getRow(1).getCell(11), "第五学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 11, 12);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替，共10列）
        setCellText(table.getRow(2).getCell(7), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(8), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(15), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(16), "春", true, cellWidth.getCellWidth(1));
    }

    // ============================ 专业课程教学安排 ============================

    @Override
    protected XWPFTable majorCoursesTable(XWPFDocument document, String title, List<TrainingSchemeCourseModel> courses) {
        // 创建表格标题
        WordUtil.createHeading(document, title, 3);
        Map<String, List<TrainingSchemeCourseModel>> couseMap = courses.stream().collect(Collectors.groupingBy(course -> course.getSubMajorName() == null ? "**专业方向" : course.getSubMajorName()));

        // 计算总行数：表头3行 + 数据行 + 小计1行（每个专业方向一个小计）
        int dataRows = courses.size();
        int totalRows = 3 + dataRows + couseMap.size();
        int totalCols = 18; // 专业方向1+名称1+修读1+考核1+学分1+学时3+学期10 = 18列

        XWPFTable table = document.createTable(totalRows, totalCols);
        WordUtil.initTableGrid(table,totalCols,1000);
        table.setWidthType(TableWidthType.PCT);
        table.setWidth("100%");
        CellWidth cellWidth = new CellWidth(18);
        // 生成表头（3行）——第五学年版
        majorCoursesHeader(table, cellWidth);

        int dataRowStart = 3;

        // 处理每一门课程，确定其所属模块
        AtomicInteger dataRow = new AtomicInteger(dataRowStart);
        couseMap.forEach((majorName, courseModels) -> {
            int moduleRowStart = dataRow.get();
            int moduleRowEnd = dataRow.get() + courseModels.size();
            CountModel countModel = new CountModel();
            for (int i = 0; i < courseModels.size(); i++) {
                TrainingSchemeCourseModel course = courseModels.get(i);
                // 设置课程数据行
                int dataRowIndex = moduleRowStart + i;
                setCourseDataRow(table, dataRowIndex, course, 1, countModel, cellWidth,1);
            }
            XWPFTableRow row = table.getRow(moduleRowStart);
            XWPFTableCell cell1 = row.getCell(0);
            WordUtil.setCellText(cell1, majorName, false, "1134");
            WordUtil.mergeCellsVertical(table, 0, moduleRowStart, moduleRowEnd - 1);
            // 设置小计行（学时三列起始=5，学期起始=8）
            setTotalRow(table, moduleRowEnd, countModel, 5, 8, cellWidth);
            dataRow.set(moduleRowEnd + 1);
        });
        return table;
    }

    /**
     * 专业课程表头（3行）——在原四年制表头基础上追加"第五学年 秋/春"两列。
     * 学期从列7开始，学年表头塌缩后依次 7/8/9/10/11。
     */
    @Override
    protected void majorCoursesHeader(XWPFTable table, CellWidth cellWidth) {
        // 列布局(18列)：专业方向(0) | 名称(1) | 修读(2) | 考核(3) | 学分(4) | 学时(5-7) | 学期(8-17,10列)
        // ========== 表头第1行（行号0） ==========
        setCellText(table.getRow(0).getCell(0), "专业方向", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 0, 0, 2);

        setCellText(table.getRow(0).getCell(1), "课程名称", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 1, 0, 2);

        // 修读要求，跨3行
        setCellText(table.getRow(0).getCell(2), "修读\n要求", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 2, 0, 2);

        // 考核方式，跨3行
        setCellText(table.getRow(0).getCell(3), "考核\n方式", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 3, 0, 2);

        // 学分，跨3行（位于考核方式之后）
        setCellText(table.getRow(0).getCell(4), "学分", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 4, 0, 2);

        // 学时安排，跨3列
        setCellText(table.getRow(0).getCell(5), "学时安排", true, cellWidth.getCellWidth(3));
        WordUtil.mergeCellsHorizontal(table, 0, 5, 7);

        // 学期安排：跨10列（5个学年）塌缩后从6起
        setCellText(table.getRow(0).getCell(6), "学期安排", true, cellWidth.getCellWidth(10));
        WordUtil.mergeCellsHorizontal(table, 0, 6, 15);

        // ========== 表头第2行（行号1） ==========
        // 学时子项
        setCellText(table.getRow(1).getCell(5), "小计", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 5, 1, 2);
        setCellText(table.getRow(1).getCell(6), "讲授", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 6, 1, 2);
        setCellText(table.getRow(1).getCell(7), "实践", true, cellWidth.getCellWidth(1));
        WordUtil.mergeCellsVertical(table, 7, 1, 2);

        // 第一~第五学年，每学年跨2列（塌缩后依次 8/9/10/11/12）
        setCellText(table.getRow(1).getCell(8), "第一学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 8, 9);
        setCellText(table.getRow(1).getCell(9), "第二学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 9, 10);
        setCellText(table.getRow(1).getCell(10), "第三学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 10, 11);
        setCellText(table.getRow(1).getCell(11), "第四学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 11, 12);
        setCellText(table.getRow(1).getCell(12), "第五学年", true, cellWidth.getCellWidth(2));
        WordUtil.mergeCellsHorizontal(table, 1, 12, 13);

        // ========== 表头第3行（行号2） ==========
        // 学期子项（秋、春交替，共10列）
        setCellText(table.getRow(2).getCell(8), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(9), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(10), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(11), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(12), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(13), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(14), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(15), "春", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(16), "秋", true, cellWidth.getCellWidth(1));
        setCellText(table.getRow(2).getCell(17), "春", true, cellWidth.getCellWidth(1));
    }
}
