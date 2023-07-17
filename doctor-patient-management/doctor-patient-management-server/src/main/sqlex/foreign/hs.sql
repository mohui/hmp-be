CREATE TABLE `hs_health_scheme_management_info`
(
    `kn_id`                 bigint(20) unsigned NOT NULL COMMENT '主键ID',
    `kn_management_stage`   varchar(255)                 DEFAULT NULL COMMENT '初始监测阶段:INITIAL_STAGE，短期稳定阶段:STABLE_STAGE，中期稳定阶段:METAPHASE_STABLE_STAGE，长期稳定阶段：SECULAR_STABLE_STAGE）',
    `kn_start_date`         date                NOT NULL COMMENT '阶段开始时间',
    `kn_end_date`           date                         DEFAULT NULL COMMENT '阶段结束时间',
    `kn_report_output_date` date                         DEFAULT NULL COMMENT '阶段性报告输出时间',
    `kn_created_by`         bigint(20) unsigned NOT NULL COMMENT '创建人',
    `kn_created_at`         datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_del`                tinyint(1)          NOT NULL DEFAULT '0' COMMENT '删除状态 （1-是，0-否）',
    `kn_patient_id`         bigint(20) unsigned NOT NULL COMMENT '患者ID',
    `kn_job_id`             int(11)                      DEFAULT NULL COMMENT '任务id',
    `kn_health_manage_type` varchar(255)        NOT NULL COMMENT '健康方案类型 SYNTHETIC-综合健康方案，HYPERTENSION-高血压健康方案',
    `kn_disease_exists_tag` varchar(255)        NOT NULL COMMENT '健康方案病种患者标签,多个逗号隔开  HYPERTENSION-高血压 DIABETES-糖尿病 ACUTE_CORONARY_DISEASE-冠心病 CEREBRAL_STROKE-脑卒中 COPD-慢阻肺',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='健康方案管理表(个性化)';
