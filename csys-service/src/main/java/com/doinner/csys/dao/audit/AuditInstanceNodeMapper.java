package com.doinner.csys.dao.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.audit.AuditInstanceNode;
import com.doinner.csys.entity.audit.AuditNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AuditInstanceNodeMapper extends BaseMapper<AuditInstanceNode> {

    @Select("SELECT * FROM audit_instance_node WHERE instance_id = #{instanceId} ORDER BY node_order ASC")
    List<AuditInstanceNode> selectByInstanceId(Long instanceId);

    @Select("SELECT * FROM audit_instance_node WHERE instance_id = #{instanceId} AND node_status = 0 ORDER BY node_order ASC LIMIT 1")
    AuditInstanceNode selectPendingNode(Long instanceId);

    @Select("SELECT * FROM audit_instance_node WHERE auditor_id = #{auditorId} AND node_status = 0 ")
    List<AuditInstanceNode> selectTodoByAuditor(String auditorId);

    @Update("UPDATE audit_instance_node SET node_status = #{nodeStatus}, audit_result = #{auditResult}, audit_opinion = #{auditOpinion}, audit_time = #{auditTime} WHERE id = #{id}")
    int updateNodeStatus(@Param("id") Long id,
                         @Param("nodeStatus") Integer nodeStatus,
                         @Param("auditResult") Integer auditResult,
                         @Param("auditOpinion") String auditOpinion,
                         @Param("auditTime") java.time.LocalDateTime auditTime);

    @Select("SELECT * FROM audit_instance_node WHERE auditor_id = #{auditorId} AND node_status in (1,2)")
    List<AuditInstanceNode> selectDoneByAuditor(String auditorId);

    @Select("SELECT * FROM audit_instance_node WHERE instance_id = #{instance_id} AND node_order > #{nodeOrder} ORDER BY node_order ASC LIMIT 1")
    AuditInstanceNode selectNextNode(@Param("instance_id") Long instance_id, @Param("nodeOrder")Integer nodeOrder);
}