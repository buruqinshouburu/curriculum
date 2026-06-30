package com.doinner.csys.dao.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doinner.csys.domain.vo.audit.AuditFlowVo;
import com.doinner.csys.entity.audit.AuditFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditFlowMapper extends BaseMapper<AuditFlow> {

    @Select("SELECT * FROM audit_flow WHERE code = #{code}")
    AuditFlow selectByCode(String code);

    List<AuditFlow> getAuditFlowList(@Param("auditFlow") AuditFlowVo auditFlowVo);
}