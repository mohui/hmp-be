create table ol_operator_log
(
    kn_id              bigint unsigned                          not null comment '唯一主键',
    kn_created_at      datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    kn_login_name      varchar(50)                              not null comment '登录名',
    kn_created_by      bigint unsigned                          not null comment '登录人ID',
    kn_ip              varchar(50)                              not null comment '终端IP',
    kn_operator_model  varchar(100)                             not null comment '操作模块',
    kn_operator_action varchar(100)                             not null comment '操作行为',
    kn_sys             varchar(100)                             not null comment '操作服务',
    kn_time_millis     bigint                                   not null comment '用时(毫秒)',
    kn_org_id          bigint unsigned                          null comment '机构ID',
    kn_context         varchar(50)                              null comment '操作内容',
    PRIMARY KEY (`kn_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='操作日志表';