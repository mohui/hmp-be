/* dpm_doctor_info */
CREATE TABLE `dpm_doctor_info`
(
    `kn_id`              bigint(20) unsigned NOT NULL COMMENT '主键',
    `kn_name`            varchar(50)         NOT NULL COMMENT '姓名',
    `kn_gender`          varchar(16)         NOT NULL COMMENT '性别（MAN-男，WOMAN-女）',
    `kn_phone`           varchar(11)         NOT NULL COMMENT '联系电话',
    `kn_dept_name`       varchar(20)         NOT NULL COMMENT '科室名称',
    `kn_hospital_id`     bigint(20) unsigned NOT NULL COMMENT '医院Id',
    `kn_hospital_name`   varchar(20)         NOT NULL COMMENT '医院名称',
    `kn_address`         varchar(255)        NOT NULL COMMENT '详细地址',
    `kn_auth_id`         bigint(20) unsigned NOT NULL COMMENT '权限中心返回Id',
    `kn_health_plan_num` bigint(20)          NOT NULL DEFAULT '0' COMMENT '健康方案数量',
    `kn_created_by`      bigint(20) unsigned NOT NULL COMMENT '创建人id',
    `kn_created_at`      datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`      bigint(20) unsigned NOT NULL COMMENT '修改人id',
    `kn_updated_at`      datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `kn_del`             tinyint(1)          NOT NULL DEFAULT '0' COMMENT '删除状态 （1-是，0-否）',
    `kn_doctor_level`    varchar(50)         NOT NULL DEFAULT 'RESIDENT_PHYSICIAN' COMMENT '等级职称：住院医师 RESIDENT_PHYSICIAN，主治医生 IN_CHARGE_PHYSICIAN,主任医师 CHIEF_PHYSICIAN，副主任医师 ASSOCIATE_CHIEF_PHYSICIAN，初级护士 JUNIOR_NURSE, 初级护师 JUNIOR_SENIORNURSE ，主管护师 CHARGE_SENIORNURSE,主任护师 CHIEF_SENIORNURSE，副主任医师 ASSOCIATE_CHIEF_SENIORNURSE',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='医生信息表';

/* dpm_doctor_patient */
CREATE TABLE `dpm_doctor_patient`
(
    `kn_id`                   bigint(20) unsigned NOT NULL COMMENT '主键',
    `kn_patient_id`           bigint(20) unsigned NOT NULL COMMENT '患者Id(患者信息表主键)',
    `kn_doctor_id`            bigint(20) unsigned NOT NULL COMMENT '医生Id(医生信息表主键)',
    `kn_bind_doctor_datetime` datetime(3)                  DEFAULT NULL COMMENT '绑定医生时间 (医生信息表主键)',
    `kn_status`               varchar(16)         NOT NULL DEFAULT 'START_ASSESS' COMMENT '患者状态 （NONE-无，START_ASSESS-开始评估，SUCCESS-完成）',
    `kn_message_status`       varchar(16)         NOT NULL DEFAULT 'NONE' COMMENT '留言类型 （READ-已读，UNREAD-未读，NONE-无）',
    `kn_message_num`          int(11)             NOT NULL DEFAULT '0' COMMENT '留言数量 ',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='医生患者关系表';


/* dpm_patient_info */
CREATE TABLE `dpm_patient_info`
(
    `kn_id`                          bigint(20) unsigned NOT NULL COMMENT '主键',
    `kn_name`                        varchar(50)         NOT NULL COMMENT '姓名',
    `kn_gender`                      varchar(16)         NOT NULL COMMENT '性别（MAN-男，WOMAN-女）',
    `kn_phone`                       varchar(11)         NOT NULL COMMENT '联系电话',
    `kn_birthday`                    datetime            NOT NULL COMMENT '出生日期',
    `kn_id_card`                     varchar(18)         NOT NULL COMMENT '身份证号',
    `kn_provincial_code`             varchar(20)                  DEFAULT NULL COMMENT '省编码',
    `kn_municipal_code`              varchar(20)                  DEFAULT NULL COMMENT '市编码',
    `kn_county_code`                 varchar(20)                  DEFAULT NULL COMMENT '县编码',
    `kn_address`                     varchar(255)                 DEFAULT NULL COMMENT '详细地址',
    `kn_medical_history`             varchar(255)                 DEFAULT NULL COMMENT '疾病史，多选逗号分割（HYPERTENSION-高血压,DIABETES-糖尿病,ACUTE_CORONARY_DISEASE-冠心病,CEREBRAL_STROKE-脑卒中,COPD-慢阻肺,DYSLIPIDEMIA-血脂异常）',
    `kn_family_history`              varchar(255)                 DEFAULT NULL COMMENT '家族史，多选逗号分割（HYPERTENSION-高血压,DIABETES-糖尿病,ACUTE_CORONARY_DISEASE-冠心病,CEREBRAL_STROKE-脑卒中,COPD-慢阻肺）',
    `kn_symptom`                     varchar(255)                 DEFAULT NULL COMMENT '症状，多选逗号分割（DIZZY-头晕，头疼症状，CHEST_PAIN-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解，CHRONIC_COUGH-呼吸困难或慢性咳嗽，WEIGHT_LOSS-多饮、多尿、多食、不明原因体重下降，GIDDINESS-一过性黑蒙、眩晕）',
    `kn_smoking`                     tinyint(1)                   DEFAULT NULL COMMENT '是否吸烟 （1-是，0-否）',
    `kn_drink_wine`                  tinyint(1)                   DEFAULT NULL COMMENT '是否饮酒 （1-是，0-否）',
    `kn_sports`                      tinyint(1)                   DEFAULT NULL COMMENT '是否运动 （1-是，0-否）',
    `kn_salt_intake`                 decimal(10, 2)               DEFAULT NULL COMMENT '每天盐的摄入量（单位 g）',
    `kn_copd_disease_tag`            varchar(16)                  DEFAULT NULL COMMENT '慢阻肺病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    `kn_cerebral_stroke_disease_tag` varchar(16)                  DEFAULT NULL COMMENT '脑卒中病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    `kn_acute_coronary_disease_tag`  varchar(16)                  DEFAULT NULL COMMENT '冠心病病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    `kn_diabetes_disease_tag`        varchar(16)                  DEFAULT NULL COMMENT '糖尿病病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    `kn_hypertension_disease_tag`    varchar(16)                  DEFAULT NULL COMMENT '高血压病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    `kn_synthesis_disease_tag`       varchar(16)                  DEFAULT NULL COMMENT '五病综合标签 （HIGH-高危,LOW-低危）',
    `kn_auth_id`                     bigint(20) unsigned NOT NULL COMMENT '权限中心返回Id',
    `kn_created_by`                  bigint(20) unsigned NOT NULL COMMENT '创建人id',
    `kn_created_at`                  datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `kn_updated_by`                  bigint(20) unsigned NOT NULL COMMENT '修改人id',
    `kn_updated_at`                  datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    `kn_del`                         tinyint(1)          NOT NULL DEFAULT '0' COMMENT '删除状态 （1-是，0-否）',
    `kn_township_code`               varchar(16)                  DEFAULT NULL COMMENT '乡镇编码',
    `kn_region_address`              varchar(255)                 DEFAULT NULL COMMENT '行政区域地址',
    PRIMARY KEY (`kn_id`) /*T![clustered_index] CLUSTERED */
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='患者信息表';
