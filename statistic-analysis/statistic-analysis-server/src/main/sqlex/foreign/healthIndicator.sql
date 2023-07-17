create table hi_blood_pressure
(
    kn_created_by               bigint unsigned                          null comment '创建人id',
    kn_created_at               datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    kn_id                       bigint unsigned                          not null comment '主键'
        primary key,
    kn_patient_id               bigint unsigned                          not null comment '病人id',
    kn_systolic_blood_pressure  double unsigned                          null comment '收缩压/mmHg',
    kn_diastolic_blood_pressure double unsigned                          null comment '舒张压/mmHg',
    from_tag                    varchar(64)                              not null comment '来源标识（PATIENT_SELF：患者本身，DOCTOR_VISIT：医生随访），后续会进行补充'
) comment '血压指标表';

create table hi_blood_sugar
(
    kn_created_by               bigint unsigned                          null comment '创建人id',
    kn_created_at               datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    kn_id                       bigint unsigned                          not null comment '主键'
        primary key,
    kn_patient_id               bigint unsigned                          not null comment '病人id',
    kn_fasting_blood_sandalwood double unsigned                          null comment '空腹血糖/mmol/L',
    kn_random_blood_sugar       double unsigned                          null comment '随机血糖/mmol/L',
    from_tag                    varchar(64)                              not null comment '来源标识（PATIENT_SELF：患者本身，DOCTOR_VISIT：医生随访），后续会进行补充',
    kn_after_meal_blood_sugar   double unsigned                          null comment '餐后2个小时血糖'
) comment '血糖指标表';

create table hi_bmi
(
    kn_created_by bigint unsigned                          null comment '创建人id',
    kn_created_at datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    kn_id         bigint unsigned                          not null comment '主键'
        primary key,
    kn_patient_id bigint unsigned                          not null comment '病人id',
    kn_bmi        double unsigned                          not null comment '体质指数. 单位: kg/m²',
    from_tag      varchar(64)                              not null comment '来源标识（PATIENT_SELF：患者本身，DOCTOR_VISIT：医生随访），后续会进行补充'
) comment 'BMI指标表';