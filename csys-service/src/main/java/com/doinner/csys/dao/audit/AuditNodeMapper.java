package com.doinner.csys.dao.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.entity.audit.AuditNode;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuditNodeMapper extends BaseMapper<AuditNode> {

    @Select("SELECT * FROM audit_node WHERE flow_id = #{flowId} ORDER BY node_order ASC")
    List<AuditNode> selectByFlowId(Long flowId);

    @Select("SELECT * FROM audit_node WHERE flow_id = #{flowId} AND node_key = #{nodeKey}")
    AuditNode selectByFlowIdAndNodeKey(@Param("flowId")Long flowId, @Param("nodeKey")String nodeKey);

    @Select("SELECT * FROM audit_node WHERE flow_id = #{flowId} AND node_order > #{nodeOrder} ORDER BY node_order ASC LIMIT 1")
    AuditNode selectNextNode(@Param("flowId") Long flowId, @Param("nodeOrder")Integer nodeOrder);


    @Select("SELECT * FROM audit_node WHERE id = #{id}")
    AuditNode selectById(Long id);

    @Update("UPDATE audit_node SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateNodeStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);

    @Delete("delete from audit_node where flow_id=#{flowId} ")
    void deleteByFlowId(Long flowId);
}