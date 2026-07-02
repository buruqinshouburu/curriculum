package com.doinner.csys.dao;

import com.doinner.csys.domain.CourseInvokeDeleteLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 调用课程删除日志Mapper接口
 *
 * @author doinner
 */
public interface CourseInvokeDeleteLogMapper {

    /**
     * 批量新增调用课程删除日志
     *
     * @param list 删除日志集合
     * @return 结果
     */
    int insertBatch(@Param("list") List<CourseInvokeDeleteLog> list);
}
