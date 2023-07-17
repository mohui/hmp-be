/* upcs_org */
CREATE TABLE `upcs_org`
(
    `kn_created_by`  bigint(20) unsigned                             DEFAULT NULL COMMENT '创建人id',
    `kn_created_at`  datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`  bigint(20) unsigned                             DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at`  datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`         tinyint(1)                             NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_id`          bigint(20) unsigned                    NOT NULL COMMENT '主键',
    `kn_name`        varchar(64) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '名称',
    `kn_sort`        int(11)                                NOT NULL DEFAULT '100' COMMENT '排序',
    `kn_region_code` bigint(20) unsigned                    NOT NULL COMMENT '行政区域编码',
    `kn_org_level`   varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织级别(基层卫生院 substrate_hygiene_hospital,社区卫生服务中心 community_service_center,乡镇医院	towns_hospital,乡镇	towns,乡镇民营医院 towns_private_hospital,乡镇卫生院	towns_hygiene_hospital,县域医院	county_hospital,市级医疗机构	city_medical_institution,市级综合医院	city_composite_hospital,一级	level_one,一级综合医院	level_one_composite_hospital,一级专科医院	level_one_specialty_hospital,一级甲等	level_one_frist,一级乙等	level_one_second,二级	level_two,二级医院	level_two_hospital,二级综合医院	level_two_composite_hospital,二级专科医院	level_two_specialty_hospital,二级甲等	level_two_frist,二级乙等	level_two_second,三级	level_three,三级医院	level_three_hospital,三级综合医院	level_three_composite_hospital,三级专科医院	level_three_specialty_hospital,三级甲等	level_three_frist,三级乙等	level_three_second)',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='组织表';

/* upcs_region */
CREATE TABLE `upcs_region`
(
    `kn_code`        bigint(20) unsigned                    NOT NULL,
    `kn_parent_code` bigint(20) unsigned                             DEFAULT NULL,
    `kn_name`        varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
    `kn_level_code`  int(11)                                NOT NULL DEFAULT '1',
    PRIMARY KEY (`kn_code`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='行政区分代码表';

/* upcs_role */
CREATE TABLE `upcs_role`
(
    `kn_created_by` bigint(20) unsigned                             DEFAULT NULL COMMENT '创建人id',
    `kn_created_at` datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by` bigint(20) unsigned                             DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at` datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`        tinyint(1)                             NOT NULL DEFAULT '0' COMMENT '是否删除',
    `is_used`       tinyint(1)                             NOT NULL DEFAULT '1' COMMENT '状态, true:可用,false: 不可用',
    `kn_id`         bigint(20) unsigned                    NOT NULL COMMENT '主键',
    `kn_name`       varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
    `kn_code`       varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色表';

/* upcs_role_permission */
CREATE TABLE `upcs_role_permission`
(
    `kn_created_by`      bigint(20) unsigned                             DEFAULT NULL COMMENT '创建人id',
    `kn_created_at`      datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`      bigint(20) unsigned                             DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at`      datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`             tinyint(1)                             NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_id`              bigint(20) unsigned                    NOT NULL COMMENT '主键',
    `kn_role_id`         bigint(20) unsigned                    NOT NULL COMMENT '角色id',
    `kn_permission_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限code',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_role_id` (`kn_role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色权限表';

/* upcs_user */
CREATE TABLE `upcs_user`
(
    `kn_created_by`     bigint(20) unsigned                              DEFAULT NULL COMMENT '创建人id',
    `kn_created_at`     datetime(3)                             NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`     bigint(20) unsigned                              DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at`     datetime(3)                             NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`            tinyint(1)                              NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_id`             bigint(20) unsigned                     NOT NULL COMMENT '主键',
    `kn_login_name`     varchar(64) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '登录名',
    `kn_login_password` varchar(256) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（微信绑定用户可能为空）',
    `is_enabled`        tinyint(1)                              NOT NULL DEFAULT '1' COMMENT '是否启用',
    `kn_name`           varchar(64) COLLATE utf8mb4_general_ci           DEFAULT NULL COMMENT '昵称',
    `kn_phone`          varchar(16) COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `kn_email`          varchar(256) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '邮箱',
    `kn_birthday`       datetime                                         DEFAULT NULL COMMENT '出生日期',
    `kn_gender`         varchar(8) COLLATE utf8mb4_general_ci   NOT NULL DEFAULT 'UNKNOWN' COMMENT '手机号',
    `kn_profile_pic`    varchar(1024) COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '头像',
    `kn_id_card`        varchar(32) COLLATE utf8mb4_general_ci           DEFAULT NULL COMMENT '身份证号',
    `kn_extends`        varchar(1024) COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '扩展数据',
    `kn_address`        varchar(255) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_login_name` (`kn_login_name`),
    KEY `idx_phone` (`kn_phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户表';

/* upcs_user_identity_tag */
CREATE TABLE `upcs_user_identity_tag`
(
    `kn_id`           bigint(20) unsigned                    NOT NULL COMMENT '唯一标识',
    `kn_user_id`      bigint(20) unsigned                    NOT NULL COMMENT '用户id',
    `kn_identify_tag` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份标签（区域管理员-REGION_ADMIN,机构管理员-ORG_ADMIN,医生-DOCTOR 护士-NURSE,患者-PATIENT）',
    `is_del`          tinyint(1)                             NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_created_by`   bigint(20) unsigned                             DEFAULT NULL COMMENT '创建人id',
    `kn_created_at`   datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`   bigint(20) unsigned                             DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at`   datetime(3)                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_user_id` (`kn_user_id`),
    KEY `idx_org_id` (`kn_identify_tag`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户身份标签表';

/* upcs_user_org */
CREATE TABLE `upcs_user_org`
(
    `kn_created_by` bigint(20) unsigned          DEFAULT NULL COMMENT '创建人id',
    `kn_created_at` datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by` bigint(20) unsigned          DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at` datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`        tinyint(1)          NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_id`         bigint(20) unsigned NOT NULL COMMENT '唯一标识',
    `kn_user_id`    bigint(20) unsigned NOT NULL COMMENT '用户id',
    `kn_org_id`     bigint(20) unsigned NOT NULL COMMENT '组织id',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_user_id` (`kn_user_id`),
    KEY `idx_org_id` (`kn_org_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户组织表';

/* upcs_user_region */
CREATE TABLE `upcs_user_region`
(
    `kn_id`          bigint(20) unsigned NOT NULL COMMENT '唯一标识',
    `kn_user_id`     bigint(20) unsigned NOT NULL COMMENT '用户id',
    `kn_region_code` bigint(20) unsigned NOT NULL COMMENT '行政区域id',
    `is_del`         tinyint(1)          NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_created_by`  bigint(20) unsigned          DEFAULT NULL COMMENT '创建人id',
    `kn_created_at`  datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`  bigint(20) unsigned          DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at`  datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_user_id` (`kn_user_id`),
    KEY `idx_org_id` (`kn_region_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户行政区域表';

/* upcs_user_role */
CREATE TABLE `upcs_user_role`
(
    `kn_created_by` bigint(20) unsigned          DEFAULT NULL COMMENT '创建人id',
    `kn_created_at` datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by` bigint(20) unsigned          DEFAULT NULL COMMENT '修改人id',
    `kn_updated_at` datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `is_del`        tinyint(1)          NOT NULL DEFAULT '0' COMMENT '是否删除',
    `kn_id`         bigint(20) unsigned NOT NULL COMMENT '唯一标识',
    `kn_user_id`    bigint(20) unsigned NOT NULL COMMENT '用户id',
    `kn_role_id`    bigint(20) unsigned NOT NULL COMMENT '角色id',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
    KEY `idx_user_id` (`kn_user_id`),
    KEY `idx_role_id` (`kn_role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户角色表';


INSERT INTO upcs_user (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`,
                       `kn_id`, `kn_login_name`, `kn_login_password`, `is_enabled`, `kn_name`,
                       `kn_phone`, `kn_email`, `kn_birthday`, `kn_gender`, `kn_profile_pic`, `kn_id_card`, `kn_extends`)
VALUES (0, current_timestamp(3), 0, current_timestamp(3), 0,
        20221114001, '测试医生', '$2a$04$pvsUnnWV4K1EjpW3g5VVT.WplbTPsSai12ze8UmIt7d8EV6hYQXau', 1, '测试医生', '',
        NULL, NULL,
        'MAN', NULL, NULL, NULL);
INSERT INTO upcs_user_identity_tag (`kn_id`, `kn_user_id`, `kn_identify_tag`, `is_del`, `kn_created_by`,
                                    `kn_created_at`, `kn_updated_by`, `kn_updated_at`)
VALUES (20221114001, 20221114001, 'DOCTOR', 0, 0, current_timestamp(3),
        0, current_timestamp(3));


INSERT INTO upcs_user (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`,
                       `kn_id`, `kn_login_name`, `kn_login_password`, `is_enabled`, `kn_name`,
                       `kn_phone`, `kn_email`, `kn_birthday`, `kn_gender`, `kn_profile_pic`, `kn_id_card`, `kn_extends`)
VALUES (0, current_timestamp(3), 0, current_timestamp(3), 0,
        20221114002, '测试医生2', '$2a$04$pvsUnnWV4K1EjpW3g5VVT.WplbTPsSai12ze8UmIt7d8EV6hYQXau', 1, '测试医生', '',
        NULL, NULL,
        'MAN', NULL, NULL, NULL);
INSERT INTO upcs_user_identity_tag (`kn_id`, `kn_user_id`, `kn_identify_tag`, `is_del`, `kn_created_by`,
                                    `kn_created_at`, `kn_updated_by`, `kn_updated_at`)
VALUES (20221114002, 20221114002, 'DOCTOR', 0, 0, current_timestamp(3),
        0, current_timestamp(3));

INSERT INTO upcs_user (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`,
                       `kn_id`, `kn_login_name`, `kn_login_password`, `is_enabled`, `kn_name`,
                       `kn_phone`, `kn_email`, `kn_birthday`, `kn_gender`, `kn_profile_pic`, `kn_id_card`, `kn_extends`)
VALUES (0, current_timestamp(3), 0, current_timestamp(3), 0,
        20221114003, '测试医生3', '$2a$04$pvsUnnWV4K1EjpW3g5VVT.WplbTPsSai12ze8UmIt7d8EV6hYQXau', 1, '测试医生', '',
        NULL, NULL,
        'MAN', NULL, NULL, NULL);
INSERT INTO upcs_user_identity_tag (`kn_id`, `kn_user_id`, `kn_identify_tag`, `is_del`, `kn_created_by`,
                                    `kn_created_at`, `kn_updated_by`, `kn_updated_at`)
VALUES (20221114003, 20221114003, 'DOCTOR', 0, 0, current_timestamp(3),
        0, current_timestamp(3));

INSERT INTO `upcs_region` (`kn_code`, `kn_parent_code`, `kn_name`, `kn_level_code`)
VALUES (110000000000, NULL, '北京市', 1);
INSERT INTO `upcs_region` (`kn_code`, `kn_parent_code`, `kn_name`, `kn_level_code`)
VALUES (110100000000, 110000000000, '市辖区', 2);
INSERT INTO `upcs_region` (`kn_code`, `kn_parent_code`, `kn_name`, `kn_level_code`)
VALUES (110101000000, 110100000000, '东城区', 3);

INSERT INTO `upcs_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`, `kn_id`,
                        `kn_name`, `kn_sort`, `kn_region_code`, `kn_org_level`)
VALUES (NULL, '2022-08-31 16:07:46.516', NULL, '2022-08-31 16:07:46.516', 0, 1, '海安市人民医院', 100, 320685000000,
        'community_service_center');
INSERT INTO `upcs_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`, `kn_id`,
                        `kn_name`, `kn_sort`, `kn_region_code`, `kn_org_level`)
VALUES (NULL, '2022-08-31 16:07:46.516', NULL, '2022-08-31 16:07:46.516', 0, 2, '海安市白甸镇卫生院', 100, 320685000000,
        'towns_hygiene_hospital');
INSERT INTO `upcs_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`, `kn_id`,
                        `kn_name`, `kn_sort`, `kn_region_code`, `kn_org_level`)
VALUES (NULL, '2022-08-31 16:07:46.516', NULL, '2022-08-31 16:07:46.516', 0, 3, '海安市墩头镇中心卫生院', 100,
        320685000000, 'towns_hygiene_hospital');
INSERT INTO `upcs_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`, `kn_id`,
                        `kn_name`, `kn_sort`, `kn_region_code`, `kn_org_level`)
VALUES (NULL, '2022-08-31 16:07:46.516', NULL, '2022-08-31 16:07:46.516', 0, 4, '海安双楼医院', 100, 320685000000,
        'community_service_center');
INSERT INTO `upcs_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `is_del`, `kn_id`,
                        `kn_name`, `kn_sort`, `kn_region_code`, `kn_org_level`)
VALUES (NULL, '2022-08-31 16:07:46.516', NULL, '2022-09-09 17:37:02.398', 0, 5, '雅周镇中心卫生院', 100, 320685000000,
        'towns_hygiene_hospital');

INSERT INTO `upcs_user_region` (`kn_id`, `kn_user_id`, `kn_region_code`, `is_del`,
                                `kn_created_by`, `kn_created_at`, `kn_updated_by`,
                                `kn_updated_at`)
VALUES (1567076401561993217, 20221114001, 110000000000, 0, 1565602119472381952, '2022-09-06 17:05:03.738',
        1565602119472381952, '2022-11-07 18:28:20.351'),
       (1567076401561993218, 20221114002, 110000000000, 0, 1565602119472381952, '2022-09-06 17:05:03.738',
        1565602119472381952, '2022-11-07 18:28:20.351');

INSERT INTO `upcs_user_org` (`kn_created_by`, `kn_created_at`, `kn_updated_by`,
                             `kn_updated_at`, `is_del`, `kn_id`, `kn_user_id`, `kn_org_id`)
VALUES (1561592453696323584, '2022-08-22 14:06:08.676', 1561592453696323584, '2022-08-22 15:38:40.813', 0,
        1561595557657444352, 20221114001, 1);

/* upcs_patient_health_service */
CREATE TABLE `upcs_patient_health_service`
(
    `patient_id`        bigint(20) unsigned NOT NULL COMMENT '患者id',
    `health_service_id` bigint(20) unsigned NOT NULL COMMENT '服务包id',
    `activation_code`   varchar(30)                  DEFAULT NULL COMMENT '激活码',
    `expire_date`       datetime(3)                  DEFAULT NULL COMMENT '到期时间',
    `created_by`        bigint(20)          NOT NULL COMMENT '创建人',
    `created_at`        datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='患者签约服务包关系表';


/* upcs_health_service */
CREATE TABLE `upcs_health_service`
(
    `health_service_id`   bigint(20) unsigned NOT NULL COMMENT '服务包id',
    `health_service_code` varchar(10)         NOT NULL COMMENT '服务包code',
    `health_service_name` varchar(10)         NOT NULL COMMENT '服务包名称',
    `during_time`         smallint(6)                  DEFAULT NULL COMMENT '生效时长-月',
    `created_by`          bigint(20)          NOT NULL COMMENT '创建人',
    `created_at`          datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    PRIMARY KEY (`health_service_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='服务包列表';


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

INSERT INTO `hs_health_scheme_management_info` (`kn_id`, `kn_management_stage`, `kn_start_date`,
                                                                    `kn_end_date`, `kn_report_output_date`,
                                                                    `kn_created_by`, `kn_created_at`, `is_del`,
                                                                    `kn_patient_id`, `kn_job_id`,
                                                                    `kn_health_manage_type`, `kn_disease_exists_tag`)
VALUES (1, null, '1990-01-01', null, '2011-09-17', 0, '1990-01-01', 0,
        1015, NULL, 'DIABETES', 'DIABETES');
