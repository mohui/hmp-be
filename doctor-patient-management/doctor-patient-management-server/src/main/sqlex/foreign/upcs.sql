/* undo_log */
CREATE TABLE `undo_log` (
                            `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
                            `xid` varchar(128) NOT NULL COMMENT 'global transaction id',
                            `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
                            `rollback_info` longblob NOT NULL COMMENT 'rollback info',
                            `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
                            `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
                            `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='AT transaction mode undo table';

/* upcs_health_management_item */
CREATE TABLE `upcs_health_management_item` (
                                               `health_management_item_id` bigint(20) unsigned NOT NULL COMMENT '项目id',
                                               `health_management_item_code` varchar(10) NOT NULL COMMENT '项目code',
                                               `health_management_item_name` varchar(10) NOT NULL COMMENT '项目名称',
                                               `created_by` bigint(20) NOT NULL COMMENT '创建人',
                                               `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                               PRIMARY KEY (`health_management_item_id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='服务包项目列表';

/* upcs_health_service */
CREATE TABLE `upcs_health_service` (
                                       `health_service_id` bigint(20) unsigned NOT NULL COMMENT '服务包id',
                                       `health_service_code` varchar(10) NOT NULL COMMENT '服务包code',
                                       `health_service_name` varchar(10) NOT NULL COMMENT '服务包名称',
                                       `during_time` smallint(6) DEFAULT NULL COMMENT '生效时长-月',
                                       `created_by` bigint(20) NOT NULL COMMENT '创建人',
                                       `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                       PRIMARY KEY (`health_service_id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='服务包列表';

/* upcs_health_service_activation_code */
CREATE TABLE `upcs_health_service_activation_code` (
                                                       `activation_code` varchar(30) NOT NULL COMMENT '激活码',
                                                       `health_service_id` bigint(20) unsigned NOT NULL COMMENT '服务包id',
                                                       `usage_times` int(11) NOT NULL DEFAULT '1' COMMENT '激活码使用次数',
                                                       `created_by` bigint(20) NOT NULL COMMENT '创建人',
                                                       `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                                       PRIMARY KEY (`activation_code`) /*T![clustered_index] NONCLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='服务包激活码列表';

/* upcs_health_service_item */
CREATE TABLE `upcs_health_service_item` (
                                            `health_service_id` bigint(20) unsigned NOT NULL COMMENT '服务包id',
                                            `health_management_item_id` bigint(20) unsigned NOT NULL COMMENT '项目id',
                                            `created_by` bigint(20) NOT NULL COMMENT '创建人',
                                            `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                            PRIMARY KEY (`health_service_id`,`health_management_item_id`) /*T![clustered_index] NONCLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='服务包项目关系表';

/* upcs_org */
CREATE TABLE `upcs_org` (
                            `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                            `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                            `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                            `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                            `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                            `kn_id` bigint(20) unsigned NOT NULL COMMENT '主键',
                            `kn_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                            `kn_sort` int(11) NOT NULL DEFAULT '100' COMMENT '排序',
                            `kn_region_code` bigint(20) unsigned NOT NULL COMMENT '行政区域编码',
                            `kn_org_level` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织级别(基层卫生院 substrate_hygiene_hospital,社区卫生服务中心 community_service_center,乡镇医院	towns_hospital,乡镇	towns,乡镇民营医院 towns_private_hospital,乡镇卫生院	towns_hygiene_hospital,县域医院	county_hospital,市级医疗机构	city_medical_institution,市级综合医院	city_composite_hospital,一级	level_one,一级综合医院	level_one_composite_hospital,一级专科医院	level_one_specialty_hospital,一级甲等	level_one_frist,一级乙等	level_one_second,二级	level_two,二级医院	level_two_hospital,二级综合医院	level_two_composite_hospital,二级专科医院	level_two_specialty_hospital,二级甲等	level_two_frist,二级乙等	level_two_second,三级	level_three,三级医院	level_three_hospital,三级综合医院	level_three_composite_hospital,三级专科医院	level_three_specialty_hospital,三级甲等	level_three_frist,三级乙等	level_three_second)',
                            PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织表';

/* upcs_patient_health_service */
CREATE TABLE `upcs_patient_health_service` (
                                               `patient_id` bigint(20) unsigned NOT NULL COMMENT '患者id',
                                               `health_service_id` bigint(20) unsigned NOT NULL COMMENT '服务包id',
                                               `activation_code` varchar(30) DEFAULT NULL COMMENT '激活码',
                                               `expire_date` datetime(3) DEFAULT NULL COMMENT '到期时间',
                                               `created_by` bigint(20) NOT NULL COMMENT '创建人',
                                               `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='患者签约服务包关系表';

/* upcs_region */
CREATE TABLE `upcs_region` (
                               `kn_code` bigint(20) unsigned NOT NULL,
                               `kn_parent_code` bigint(20) unsigned DEFAULT NULL,
                               `kn_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
                               `kn_level_code` int(11) NOT NULL DEFAULT '1',
                               PRIMARY KEY (`kn_code`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='行政区分代码表';

/* upcs_role */
CREATE TABLE `upcs_role` (
                             `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                             `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                             `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                             `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                             `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                             `is_used` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态, true:可用,false: 不可用',
                             `kn_id` bigint(20) unsigned NOT NULL COMMENT '主键',
                             `kn_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                             `kn_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码',
                             PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

/* upcs_role_permission */
CREATE TABLE `upcs_role_permission` (
                                        `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                                        `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                        `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                                        `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                        `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                        `kn_id` bigint(20) unsigned NOT NULL COMMENT '主键',
                                        `kn_role_id` bigint(20) unsigned NOT NULL COMMENT '角色id',
                                        `kn_permission_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限code',
                                        PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                                        KEY `idx_role_id` (`kn_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';

/* upcs_user */
CREATE TABLE `upcs_user` (
                             `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                             `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                             `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                             `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                             `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                             `kn_id` bigint(20) unsigned NOT NULL COMMENT '主键',
                             `kn_login_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录名',
                             `kn_login_password` varchar(256) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（微信绑定用户可能为空）',
                             `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
                             `kn_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                             `kn_phone` varchar(16) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号',
                             `kn_email` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                             `kn_birthday` datetime DEFAULT NULL COMMENT '出生日期',
                             `kn_gender` varchar(8) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNKNOWN' COMMENT '手机号',
                             `kn_profile_pic` varchar(1024) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
                             `kn_id_card` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身份证号',
                             `kn_extends` varchar(1024) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展数据',
                             `kn_address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
                             PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                             KEY `idx_login_name` (`kn_login_name`),
                             KEY `idx_phone` (`kn_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

/* upcs_user_identity_tag */
CREATE TABLE `upcs_user_identity_tag` (
                                          `kn_id` bigint(20) unsigned NOT NULL COMMENT '唯一标识',
                                          `kn_user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
                                          `kn_identify_tag` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份标签（区域管理员-REGION_ADMIN,机构管理员-ORG_ADMIN,医生-DOCTOR 护士-NURSE,患者-PATIENT）',
                                          `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                          `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                                          `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                          `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                                          `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                          PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                                          KEY `idx_user_id` (`kn_user_id`),
                                          KEY `idx_org_id` (`kn_identify_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户身份标签表';

/* upcs_user_org */
CREATE TABLE `upcs_user_org` (
                                 `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                                 `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                 `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                                 `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                 `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                 `kn_id` bigint(20) unsigned NOT NULL COMMENT '唯一标识',
                                 `kn_user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
                                 `kn_org_id` bigint(20) unsigned NOT NULL COMMENT '组织id',
                                 PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                                 KEY `idx_user_id` (`kn_user_id`),
                                 KEY `idx_org_id` (`kn_org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户组织表';

/* upcs_user_region */
CREATE TABLE `upcs_user_region` (
                                    `kn_id` bigint(20) unsigned NOT NULL COMMENT '唯一标识',
                                    `kn_user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
                                    `kn_region_code` bigint(20) unsigned NOT NULL COMMENT '行政区域id',
                                    `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                    `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                                    `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                    `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                                    `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                                    KEY `idx_user_id` (`kn_user_id`),
                                    KEY `idx_org_id` (`kn_region_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户行政区域表';

/* upcs_user_role */
CREATE TABLE `upcs_user_role` (
                                  `kn_created_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人id',
                                  `kn_created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                  `kn_updated_by` bigint(20) unsigned DEFAULT NULL COMMENT '修改人id',
                                  `kn_updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                  `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                  `kn_id` bigint(20) unsigned NOT NULL COMMENT '唯一标识',
                                  `kn_user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
                                  `kn_role_id` bigint(20) unsigned NOT NULL COMMENT '角色id',
                                  PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */,
                                  KEY `idx_user_id` (`kn_user_id`),
                                  KEY `idx_role_id` (`kn_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色表';
