package com.doinner.csys.dao.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.domain.vo.audit.AuditTaskVo;
import com.doinner.csys.entity.audit.AuditInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AuditInstanceMapper extends BaseMapper<AuditInstance> {

    @Select("SELECT * FROM audit_instance WHERE flow_code = #{flowCode} AND business_type = #{businessType} AND business_id = #{businessId} AND instance_status = 0 ORDER BY create_time DESC LIMIT 1")
    AuditInstance selectPendingInstance(@Param("flowCode") String flowCode,
                                        @Param("businessType") String businessType,
                                        @Param("businessId") Long businessId);

//    @Select("SELECT * FROM audit_instance WHERE submitter_id = #{submitterId} ORDER BY create_time DESC")
    List<AuditInstance> selectBySubmitter(@Param("auditTaskVo") AuditTaskVo auditTaskVo);

    @Update("UPDATE audit_instance SET instance_status = #{instanceStatus}, final_result = #{finalResult}, finish_time = #{finishTime} WHERE id = #{id}")
    int updateInstanceStatus(@Param("id") Long id,
                             @Param("instanceStatus") Integer instanceStatus,
                             @Param("finalResult") Integer finalResult,
                             @Param("finishTime") java.time.LocalDateTime finishTime);

    List<AuditInstance> getTodoList(@Param("auditTaskVo") AuditTaskVo auditTaskVo);

    List<AuditInstance> getDoneList(@Param("auditTaskVo")AuditTaskVo auditTaskVo);
}