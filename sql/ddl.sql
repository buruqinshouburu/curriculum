-- auto-generated definition
create table audit_flow
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    code                varchar(64)                        not null comment '流程编码，区分不同流程类型',
    name                varchar(128)                       not null comment '流程名称',
    description         varchar(512)                       null comment '流程描述',
    target_table        varchar(64)                        not null comment '目标业务表名',
    target_id_field     varchar(64)                        not null comment '目标表主键字段名',
    target_status_field varchar(64)                        not null comment '目标表状态字段名',
    status              tinyint  default 1                 not null comment '流程状态 0-禁用 1-启用',
    version             int      default 1                 not null comment '版本号',
    create_by           varchar(64)                        null comment '创建人',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by           varchar(64)                        null comment '更新人',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_code
        unique (code)
)
    comment '审核流程表' engine = InnoDB;

-- auto-generated definition
create table audit_instance
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    flow_code        varchar(64)                        not null comment '流程编码',
    business_type    varchar(64)                        not null comment '业务类型',
    business_id      bigint                             not null comment '业务数据ID',
    business_title   varchar(256)                       null comment '业务标题',
    submitter_id     varchar(64)                        not null comment '送审人ID',
    submitter_name   varchar(128)                       not null comment '送审人姓名',
    submit_time      datetime default CURRENT_TIMESTAMP not null comment '送审时间',
    current_node_id  bigint                             null comment '当前节点ID',
    current_node_key varchar(64)                        null comment '当前节点标识',
    instance_status  tinyint  default 0                 not null comment '实例状态 0-审核中 1-审核通过 2-审核驳回 3-已撤销',
    final_result     tinyint                            null comment '最终结果 0-驳回 1-通过',
    finish_time      datetime                           null comment '完成时间',
    remark           varchar(512)                       null comment '备注',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '审核实例表' engine = InnoDB;

create index idx_business
    on audit_instance (business_type, business_id);

create index idx_flow_code
    on audit_instance (flow_code);

create index idx_status
    on audit_instance (instance_status);

create index idx_submitter
    on audit_instance (submitter_id);

-- auto-generated definition
create table audit_instance_node
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    instance_id   bigint                             not null comment '审核实例ID',
    flow_id       bigint                             not null comment '流程ID',
    node_id       bigint                             not null comment '节点ID',
    node_key      varchar(64)                        not null comment '节点标识',
    node_name     varchar(128)                       not null comment '节点名称',
    node_order    int                                not null comment '节点顺序',
    role_code     varchar(64)                        not null comment '审核角色编码',
    auditor_id    varchar(64)                        null comment '审核人ID',
    auditor_name  varchar(128)                       null comment '审核人姓名',
    node_status   tinyint  default 0                 not null comment '节点状态 0-待审核 1-已通过 2-已驳回 3-已跳过',
    audit_result  tinyint                            null comment '审核结果 0-驳回 1-通过',
    audit_opinion varchar(512)                       null comment '审核意见',
    audit_time    datetime                           null comment '审核时间',
    receive_time  datetime                           null comment '接收时间',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '审核实例节点表' engine = InnoDB;

create index idx_auditor
    on audit_instance_node (auditor_id, node_status);

create index idx_instance_id
    on audit_instance_node (instance_id);

create index idx_node
    on audit_instance_node (node_id, node_status);

-- auto-generated definition
create table audit_node
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    flow_id       bigint                             not null comment '流程ID',
    node_key      varchar(64)                        not null comment '节点标识',
    node_name     varchar(128)                       not null comment '节点名称',
    node_order    int                                not null comment '节点顺序',
    role_code     varchar(64)                        not null comment '审核角色编码',
    role_name     varchar(128)                       not null comment '审核角色名称',
    is_auto_pass  tinyint  default 0                 not null comment '是否自动通过 0-否 1-是',
    timeout_hours int                                null comment '审核超时时间（小时）',
    status        tinyint  default 1                 not null comment '节点状态 0-禁用 1-启用',
    create_by     varchar(64)                        null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by     varchar(64)                        null comment '更新人',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '审核节点表' engine = InnoDB;

create index idx_flow_id
    on audit_node (flow_id);

