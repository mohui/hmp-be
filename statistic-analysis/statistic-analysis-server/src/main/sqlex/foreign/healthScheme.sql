create table hs_t2dm_visit
(
    id                               bigint unsigned                     not null
        primary key,
    patient_id                       bigint unsigned                     not null comment '患者id',
    patient_name                     varchar(255)                        not null comment '患者姓名(手录)',
    doctor_id                        bigint unsigned                     null comment '随访医生id',
    doctor_name                      varchar(255)                        null comment '随访医生姓名(手录)',
    follow_way                       varchar(16)                         null comment '随访方式. SELF. 患者自填. OUTPATIENT. 门诊; FAMILY. 家庭; PHONE. 电话;ONLINE.线上',
    follow_date                      datetime                            null comment '随访日期.',
    is_symptom_none                  tinyint(1)                          null comment '无症状',
    is_symptom_drink                 tinyint(1)                          null comment '多饮',
    is_symptom_eat                   tinyint(1)                          null comment '多食',
    is_symptom_diuresis              tinyint(1)                          null comment '多尿',
    is_symptom_blurred               tinyint(1)                          null comment '视力模糊',
    is_symptom_infection             tinyint(1)                          null comment '感染',
    is_symptom_hand_feet_numbness    tinyint(1)                          null comment '手脚麻木',
    is_symptom_lower_extremity_edema tinyint(1)                          null comment '下肢浮肿',
    is_symptom_weight_loss_diagnosed tinyint(1)                          null comment '体重明显下降',
    symptom_other                    varchar(255)                        null comment '其它',
    signs_sbp                        double unsigned                     null comment '血压 - 收缩压. 单位: mmHg',
    signs_dbp                        double unsigned                     null comment '血压 - 舒张压. 单位: mmHg',
    signs_height                     double unsigned                     null comment '身高. 单位: cm',
    signs_weight                     double unsigned                     null comment '体重. 单位: kg',
    signs_bim                        double unsigned                     null comment '体质指数. 单位: kg/m²',
    is_signs_adp_normal              tinyint(1)                          null comment '足背动脉搏动: 触及正常',
    is_signs_adp_recede              tinyint(1)                          null comment '足背动脉搏动: 减弱-双侧',
    is_signs_adp_recede_left         tinyint(1)                          null comment '足背动脉搏动: 减弱-左侧',
    is_signs_adp_recede_right        tinyint(1)                          null comment '足背动脉搏动: 减弱-右侧',
    is_signs_adp_vanish              tinyint(1)                          null comment '足背动脉搏动: 消失-双侧',
    is_signs_adp_vanish_left         tinyint(1)                          null comment '足背动脉搏动: 消失-左侧',
    is_signs_adp_vanish_right        tinyint(1)                          null comment '足背动脉搏动: 消失-右侧',
    signs_other                      varchar(255)                        null comment '体征 - 其他',
    life_cigarettes_per_day          tinyint unsigned                    null comment '日吸烟量. 单位: 支',
    life_alcohol_per_day             double unsigned                     null comment '日饮酒量. 单位: 两',
    life_sport_per_week              double unsigned                     null comment '每周运动次数. 单位: 次',
    life_sport_per_time              double unsigned                     null comment '每次运动时间. 单位: 分钟',
    life_food_situation              double unsigned                     null comment '主食. 单位: 克/天',
    life_mental_adjustment           varchar(255)                        null comment '心理调整. GOOD: 良好; ORDINARY: 一般; BAD: 差',
    life_follow_medical_advice       varchar(255)                        null comment '遵医行为. GOOD: 良好; ORDINARY: 一般; BAD: 差',
    glu                              double                              null comment '空腹血糖. 单位: mmol/L',
    HbA1c                            double                              null comment '糖化血糖蛋白值. 单位: %',
    HbA1c_date                       datetime                            null comment '糖化血糖蛋白检查日期',
    assist_other                     varchar(255)                        null comment '辅助检查其他检查',
    drug_compliance                  varchar(255)                        null comment '服药依从性. LAW: 规律; GAP: 间断; NO: 不服药',
    is_adr                           tinyint(1)                          null comment '有无药物不良反应',
    adr_desc                         varchar(255)                        null comment '药物不良反应描述',
    rhg                              varchar(255)                        null comment '低血糖反应. NO: 无; OCCASIONALLY: 偶尔; FREQUENTLY: 频繁',
    visit_class                      varchar(255)                        null comment '此次随访分类. SATISFACTORY: 控制满意; UNSATISFACTORY: 控制不满意; UNTOWARD_EFFECT: 不良反应; COMPLICATION: 并发症',
    insulin_name                     varchar(255)                        null comment '胰岛素名称',
    insulin_desc                     varchar(255)                        null comment '胰岛素用法用量',
    is_has_referral                  tinyint(1)                          null comment '是否转诊',
    referral_reason                  varchar(255)                        null comment '转诊原因',
    referral_agencies                varchar(255)                        null comment '转诊机构及科别',
    next_visit                       datetime                            not null comment '下次随访日期',
    created_by                       bigint unsigned                     not null comment '创建人',
    created_at                       timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_by                       bigint unsigned                     not null comment '更新人',
    updated_at                       timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    recommend_weight                 double unsigned                     null comment '建议体重. 单位: kg',
    recommend_bim                    double unsigned                     null comment '建议体质指数',
    recommend_cigarettes_per         tinyint unsigned                    null comment '建议日吸烟量',
    recommend_alcohol_per            double unsigned                     null comment '建议饮酒量',
    recommend_sport_per_week         tinyint unsigned                    null comment '建议每周运动次数 单位: 次',
    recommend_sport_per_time         double unsigned                     null comment '建议每次运动时间. 单位: 分钟'
) comment '2型糖尿病随访表';