package com.doinner.csys.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;


public class WordPdfUtils {

    static Logger logger = LoggerFactory.getLogger(WordPdfUtils.class);

    private WordPdfUtils(){
        throw new AssertionError("Utility class must not be instantiated");
    }

    public static XWPFDocument readWords(InputStream inputStream){
        try{
            XWPFDocument document = new XWPFDocument(inputStream);
            return document;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static XWPFDocument readWords(MultipartFile file){
        try{
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            return document;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

//    public static void readWords(MultipartFile file) throws Exception{
//        XWPFDocument document = new XWPFDocument(file.getInputStream());
//        List<XWPFParagraph> paragraphs = document.getParagraphs();
//        List<XWPFTable> tables = document.getTables();
//        String content = doCommonContents(paragraphs);
//        System.out.println(content);
//        for(XWPFTable table:tables){
//            List<XWPFTableRow> trs = table.getRows();
//            for(XWPFTableRow tr:trs){
//                List<XWPFTableCell> cells = tr.getTableCells();
//                for(XWPFTableCell cell:cells){
//                    System.out.println(cell.getText());
//                }
//            }
//            System.out.println(table.getText());
//        }
//        System.out.println("  ------------  ");
//    }

    private static String doCommonContents(List<XWPFParagraph> paragraphs){
        String result = "";
        for(XWPFParagraph item:paragraphs){
            for(XWPFRun run:item.getRuns()){
                result += run.text();
            }
        }
        return result;
    }

    private static List<List<XWPFParagraph>> getStudy(List<XWPFParagraph> studyList){
        List<List<XWPFParagraph>> result = Lists.newArrayList();

        int i = 1;
        while(i>0){
            List<XWPFParagraph> list = getStudySub(studyList,i);
            if(CollectionUtils.isEmpty(list)){
                break;
            }else{
                result.add(list);
                i++;
            }
        }

        return result;
    }

    private static List<XWPFParagraph> getStudySub(List<XWPFParagraph> studyList, int i){
        List<XWPFParagraph> result = Lists.newArrayList();

        Iterator it = studyList.iterator();
        while(it.hasNext()){
            XWPFParagraph study = (XWPFParagraph)it.next();
            List<XWPFRun> runs = study.getRuns();
            if(CollectionUtils.isEmpty(runs)){
                continue;
            }
            if(runs.get(0).getText(0).contains("知识单元" + (i+1))){
                break;
            }
            result.add(study);
            it.remove();
        }

        return result;
    }


    public static void exportWord(HttpServletResponse response){
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun nameRun = paragraph.createRun();
        nameRun.setText("执笔人:");
        nameRun.setFontSize(14);
        nameRun.setBold(true);
        XWPFRun nameRun0 = paragraph.createRun();
        nameRun0.setText("张三，李四");
        nameRun0.setFontSize(14);

        XWPFParagraph paragraph1 = document.createParagraph();
        XWPFRun nameRun1 = paragraph1.createRun();
        nameRun1.setText("审阅学院:");
        nameRun1.setFontSize(14);
        nameRun1.setBold(true);
        XWPFRun nameRun11 = paragraph1.createRun();
        nameRun11.setText("理学院");
        nameRun11.setFontSize(14);

        XWPFParagraph paragraph2 = document.createParagraph();
        XWPFRun nameRun2 = paragraph2.createRun();
        nameRun2.setText("适用对象:");
        nameRun2.setFontSize(14);
        nameRun2.setBold(true);
        XWPFRun nameRun22 = paragraph2.createRun();
        nameRun22.setText("XXXXXX");
        nameRun22.setFontSize(14);

        XWPFParagraph paragraph3 = document.createParagraph();
        XWPFRun nameRun3 = paragraph3.createRun();
        nameRun3.setText("施教学院:");
        nameRun3.setFontSize(14);
        nameRun3.setBold(true);
        XWPFRun nameRun33 = paragraph3.createRun();
        nameRun33.setText("XXXXXX");
        nameRun33.setFontSize(14);

        XWPFParagraph paragraph4 = document.createParagraph();
        XWPFRun nameRun4 = paragraph4.createRun();
        nameRun4.setText("预修课程:");
        nameRun4.setFontSize(14);
        nameRun4.setBold(true);
        XWPFRun nameRun44 = paragraph4.createRun();
        nameRun44.setText("课程1，课程2");
        nameRun44.setFontSize(14);

        XWPFParagraph paragraph5 = document.createParagraph();
        XWPFRun nameRun5 = paragraph5.createRun();
        nameRun5.setText("实施地点:");
        nameRun5.setFontSize(14);
        nameRun5.setBold(true);
        XWPFRun nameRun55 = paragraph5.createRun();
        nameRun55.setText("XXXXXX");
        nameRun55.setFontSize(14);

        XWPFParagraph paragraphKcxz = document.createParagraph();
        XWPFRun nameRunKcxz = paragraphKcxz.createRun();
        nameRunKcxz.setText("一、任务背景与目标");
        nameRunKcxz.setFontSize(18);
        nameRunKcxz.setBold(true);

        XWPFParagraph paragraphKcmb = document.createParagraph();
        XWPFRun nameRunKcmb = paragraphKcmb.createRun();
        nameRunKcmb.setText("（一）任务背景描述");
        nameRunKcmb.setFontSize(18);
        nameRunKcmb.setBold(true);
        XWPFParagraph paragraphKcmbContent = document.createParagraph();
        XWPFRun nameRunKcmbContent = paragraphKcmbContent.createRun();
        nameRunKcmbContent.setText("1.正文（五号，宋体）（说明本课程实验整体的实验任务背景）");
        nameRunKcmbContent.setFontSize(14);

        XWPFParagraph paragraphsjmb = document.createParagraph();
        XWPFRun nameRunsjmb = paragraphsjmb.createRun();
        nameRunsjmb.setText("（二）实践目标");
        nameRunsjmb.setFontSize(18);
        nameRunsjmb.setBold(true);
        XWPFParagraph paragraphsjmbContent = document.createParagraph();
        XWPFRun nameRunsjmbContent = paragraphsjmbContent.createRun();
        nameRunsjmbContent.setText("正文（五号，宋体）（说明本课程实验技术目标）");
        nameRunsjmbContent.setFontSize(14);

        XWPFParagraph paragraphKczsmb = document.createParagraph();
        XWPFRun nameRunKczsmb = paragraphKczsmb.createRun();
        nameRunKczsmb.setText("1.知识目标");
        nameRunKczsmb.setFontSize(18);
        nameRunKczsmb.setBold(true);
        XWPFParagraph paragraphKczsmbContent = document.createParagraph();
        XWPFRun nameRunKczsmbContent = paragraphKczsmbContent.createRun();
        nameRunKczsmbContent.setText("1、具备并能应用与本专业相关的技术知识与推理能力");
        nameRunKczsmbContent.setFontSize(14);

        XWPFParagraph paragraphKcnlmb = document.createParagraph();
        XWPFRun nameRunKcnlmb = paragraphKcnlmb.createRun();
        nameRunKcnlmb.setText("2.能力目标");
        nameRunKcnlmb.setFontSize(18);
        nameRunKcnlmb.setBold(true);
        XWPFParagraph paragraphKcnlmbContent = document.createParagraph();
        XWPFRun nameRunKcnlmbContent = paragraphKcnlmbContent.createRun();
        nameRunKcnlmbContent.setText("1、树立“以算制胜”的理念");
        nameRunKcnlmbContent.setFontSize(14);

        XWPFParagraph paragraphKcszmb = document.createParagraph();
        XWPFRun nameRunKcszmb = paragraphKcszmb.createRun();
        nameRunKcszmb.setText("3.思政目标");
        nameRunKcszmb.setFontSize(18);
        nameRunKcszmb.setBold(true);
        XWPFParagraph paragraphKcszmbContent = document.createParagraph();
        XWPFRun nameRunKcszmbContent = paragraphKcszmbContent.createRun();
        nameRunKcszmbContent.setText("1、树立“以算制胜”的理念");
        nameRunKcszmbContent.setFontSize(14);

        XWPFParagraph paragraphJxff = document.createParagraph();
        XWPFRun nameRunJxff = paragraphJxff.createRun();
        nameRunJxff.setText("二、主要内容和要求");
        nameRunJxff.setFontSize(18);
        nameRunJxff.setBold(true);
        XWPFParagraph paragraphJxffContent = document.createParagraph();
        XWPFRun nameRunJxffContent = paragraphJxffContent.createRun();
        nameRunJxffContent.setText("详细说明实践环节的主要内容和要求。实践环节内容要符合实践环节的目的和任务，并注明每项内容的具体要求和训练的能力点（罗列训练的三级能力指标）。");
        nameRunJxffContent.setFontSize(14);

        XWPFParagraph paragraphXxnrsj = document.createParagraph();
        XWPFRun nameRunXxnrsj = paragraphXxnrsj.createRun();
        nameRunXxnrsj.setText("三、实践环节单位概况及与实践环节内容和要求的关联情况");
        nameRunXxnrsj.setFontSize(18);
        nameRunJxff.setBold(true);
        XWPFParagraph paragraphXxnrsjContent = document.createParagraph();
        XWPFRun nameRunXxnrsjContent = paragraphXxnrsjContent.createRun();
        nameRunXxnrsjContent.setText("说明实践环节单位概况及与本专业实践环节内容和要求的关联情况，在校内进行的实践环节可不填写此项。");
        nameRunXxnrsjContent.setFontSize(14);

        XWPFParagraph paragraphSxhj = document.createParagraph();
        XWPFRun nameRunSxhj = paragraphSxhj.createRun();
        nameRunSxhj.setText("四、实践方式");
        nameRunSxhj.setFontSize(18);
        nameRunJxff.setBold(true);
        XWPFParagraph paragraphSxhjContent1 = document.createParagraph();
        XWPFRun nameRunSxhjContent1 = paragraphSxhjContent1.createRun();
        nameRunSxhjContent1.setText("实践方式");
        nameRunSxhjContent1.setFontSize(14);

        XWPFParagraph paragraphSjdd = document.createParagraph();
        XWPFRun nameRunSjdd = paragraphSjdd.createRun();
        nameRunSjdd.setText("五、时间及地点安排");
        nameRunSjdd.setFontSize(18);
        nameRunSjdd.setBold(true);
        XWPFParagraph paragraphSjddContent = document.createParagraph();
        XWPFRun nameRunSjddContent = paragraphSjddContent.createRun();
        nameRunSjddContent.setText("实践地点");
        nameRunSjddContent.setFontSize(14);

        XWPFParagraph paragraphKhpj = document.createParagraph();
        XWPFRun nameRunKhpj = paragraphKhpj.createRun();
        nameRunKhpj.setText("六、考核与评价");
        nameRunKhpj.setFontSize(18);
        nameRunKhpj.setBold(true);
        XWPFParagraph paragraphKhfs = document.createParagraph();
        XWPFRun nameRunKhfs = paragraphKhfs.createRun();
        nameRunKhfs.setText("考核方式:");
        nameRunKhfs.setFontSize(14);
        nameRunKhfs.setBold(true);
        XWPFRun nameRunKhfsContent = paragraphKhfs.createRun();
        nameRunKhfsContent.setText("考试/考查");
        nameRunKhfsContent.setFontSize(14);

        XWPFParagraph paragraphZzfs = document.createParagraph();
        XWPFRun nameRunZzfs = paragraphZzfs.createRun();
        nameRunZzfs.setText("组织方式:");
        nameRunZzfs.setFontSize(14);
        nameRunZzfs.setBold(true);
        XWPFRun nameRunZzfsContent = paragraphZzfs.createRun();
        nameRunZzfsContent.setText("笔试/口试/现场测试，开卷/闭卷");
        nameRunZzfsContent.setFontSize(14);

        XWPFParagraph paragraphCjpd = document.createParagraph();
        XWPFRun nameRunCjpd = paragraphCjpd.createRun();
        nameRunCjpd.setText("成绩评定:");
        nameRunCjpd.setFontSize(14);
        nameRunCjpd.setBold(true);
        XWPFRun nameRunCjpdContent = paragraphCjpd.createRun();
        nameRunCjpdContent.setText("百分制/五级制（优秀、良好、中等、及格、不及格）/两级制（合格、不合格）");
        nameRunCjpdContent.setFontSize(14);

        XWPFParagraph paragraphJfbz = document.createParagraph();
        XWPFRun nameRunJfbz = paragraphJfbz.createRun();
        nameRunJfbz.setText("记分标准:");
        nameRunJfbz.setFontSize(14);
        nameRunJfbz.setBold(true);
        XWPFRun nameRunJfbzContent = paragraphJfbz.createRun();
        nameRunJfbzContent.setText("课程考试占40%，综合实践占60%（含平时作业、实践与在线学习成绩）。");
        nameRunJfbzContent.setFontSize(14);

        XWPFParagraph paragraphJccks = document.createParagraph();
        XWPFRun nameRunJccks = paragraphJccks.createRun();
        nameRunJccks.setText("七、有关要求");
        nameRunJccks.setFontSize(18);
        nameRunJfbz.setBold(true);
        XWPFParagraph paragraphJcContent = document.createParagraph();
        XWPFRun nameRunJcContent = paragraphJcContent.createRun();
        nameRunJcContent.setText("有关要求");
        nameRunJcContent.setFontSize(14);

        try {
            OutputStream outputStream = response.getOutputStream();
            String fileName = new String("教学计划模板.docx".getBytes(StandardCharsets.UTF_8),"iso-8859-1");
            response.setHeader("Content-disposition","attachment;filename="+fileName);
            document.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
