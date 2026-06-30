package com.doinner.csys.utils;

import com.doinner.common.security.utils.DictUtils;
import com.doinner.csys.constant.AttributeEnum;
import com.doinner.csys.constant.CollegeEnum;
import com.doinner.csys.constant.PropertyEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class GenerateCourseCodeUtils {

    /**
     * 获取课程编号前缀信息
     * @param collectName
     * @param propertyName
     * @param attributeCode
     * @return
     */
    public static String getPrefixCode(String collectName,String propertyName,String attributeCode){
        String collectCode = CollegeEnum.getCodeByName(collectName);
        String propertyCode = PropertyEnum.getCodeByName(propertyName);
        // String attributeCode = AttributeEnum.getCodeByName(attributeName);
        int year = LocalDate.now().getYear();
        String s = String.valueOf(year % 100);
        if (StringUtils.isBlank(attributeCode)){
            attributeCode = "1";
        }
        String prefixCode = collectCode+"-"+propertyCode+"-"+attributeCode+"-"+s+"-";
        return prefixCode;
    }

    /**
     * 获取课程编号前缀信息
     * * 版本（**）+培训层次(*)+开课单位(**)
     * @param collectName
     * @param collectName
     * @param educationLevel
     * @return
     */
    public static String getPrefixCode_new(String version,String collectName,String educationLevel){
        //版本
        String versionCode="";
        if(ObjectUtils.isNotEmpty(version)){
            versionCode=version.substring(version.length()-2);
        }
        //开课单位
        String collectCode = CollegeEnum.getCodeByName(collectName);
        if (StringUtils.isNotBlank(educationLevel)){
            if ("1".equals(educationLevel)){
                educationLevel = "JG";
            }else if ("2".equals(educationLevel)){
                educationLevel = "DB";
            }else if ("3".equals(educationLevel)){
                educationLevel = "SG";
            }else if ("4".equals(educationLevel)){
                educationLevel = "JD";
            }else if ("5".equals(educationLevel)){
                educationLevel = "JS";
            }else if ("6".equals(educationLevel)){
                educationLevel = "DS";
            } else if ("7".equals(educationLevel)){
                educationLevel = "YL";
            } else if ("8".equals(educationLevel)){
                educationLevel = "JY";
            }
        }
        return versionCode+"-"+educationLevel+"-"+collectCode+"-";
    }

    /**
     *  生成课程编号
     *  prefixCode 生成的前缀 number 根据查询模糊匹配最大的值
     * @param prefixCode
     * @param number
     * @return
     */
    public static String getCourseCode(String prefixCode,Integer number){
        number++;
        String format = String.format("%04d", number);
        return prefixCode + format;
    }


    public static void main(String[] args) {
        String prefixCode = getPrefixCode("外国语学院", "专业大类课", "限选");
        String courseCode = getCourseCode(prefixCode, 65);
        System.out.println(courseCode);
    }
}
