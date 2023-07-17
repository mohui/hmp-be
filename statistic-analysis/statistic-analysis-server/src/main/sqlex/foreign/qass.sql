create table qas_questions_answer_record
(
    id                     bigint unsigned                          not null comment '主键'
        primary key,
    answer_by              bigint unsigned                          not null comment '答题人id',
    examination_paper_id   bigint unsigned                          not null comment '问卷Id',
    examination_paper_code varchar(50)                              not null comment '问卷唯一标识',
    results_tag            varchar(128)                             not null comment '结果标签',
    results_msg            varchar(1024)                            null comment '结果解读内容',
    total_score            decimal(10, 2)                           not null comment '问卷得分',
    created_by             bigint unsigned                          not null comment '创建人id',
    created_at             datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间'
) comment '问卷答题记录表';

create table qas_disease_evaluate
(
    id                                 bigint unsigned                          not null comment '主键'
        primary key,
    patient_id                         bigint unsigned                          not null comment '患者Id',
    synthesis_disease_tag              varchar(16)                              not null comment '五病综合标签 （HIGH-高危,LOW-低危）',
    hypertension_disease_tag           varchar(16)                              not null comment '高血压病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    diabetes_disease_tag               varchar(16)                              not null comment '糖尿病病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    acute_coronary_disease_tag         varchar(16)                              not null comment '冠心病病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    cerebral_stroke_disease_tag        varchar(16)                              not null comment '脑卒中病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    copd_disease_tag                   varchar(16)                              not null comment '慢阻肺病种标签 （EXISTS-患者，HIGH-高危,LOW-低危）',
    patient_height                     decimal(10, 2)                           not null comment '身高（单位 cm）',
    patient_weight                     decimal(10, 2)                           not null comment '体重（单位 kg）',
    patient_waistline                  decimal(10, 2)                           not null comment '腰围（单位 cm）',
    is_smoking                         tinyint(1)                               not null comment '是否吸烟 （1-是，0-否）',
    systolic_pressure                  decimal(10, 2)                           not null comment '收缩压（单位 mmHg）',
    diastolic_blood_pressure           decimal(10, 2)                           not null comment '舒张压（单位 mmHg）',
    fasting_blood_sugar                decimal(10, 2)                           not null comment '空腹血糖（单位 mmol/L）',
    serum_tch                          decimal(10, 2)                           not null comment '血清总胆固醇（单位 mmol/L）',
    is_pmh_essential_hypertension      tinyint(1)                               not null comment '既往病史-原发性高血压 （1-是，0-否）',
    is_pmh_type_two_diabetes           tinyint(1)                               not null comment '既往病史-2型糖尿病 （1-是，0-否）',
    is_pmh_cerebral_infarction         tinyint(1)                               not null comment '既往病史-缺血性脑卒中（脑梗死） （1-是，0-否）',
    is_pmh_coronary_heart_disease      tinyint(1)                               not null comment '既往病史-冠心病 （1-是，0-否）',
    is_pmh_copd                        tinyint(1)                               not null comment '既往病史-慢阻肺 （1-是，0-否）',
    is_pmh_dyslipidemia_hyperlipidemia tinyint(1)                               not null comment '既往病史-血脂异常（高脂血症) （1-是，0-否）',
    is_fh_essential_hypertension       tinyint(1)                               not null comment '家族史-原发性高血压 （1-是，0-否）',
    is_fh_type_two_diabetes            tinyint(1)                               not null comment '家族史-2型糖尿病 （1-是，0-否）',
    is_fh_cerebral_infarction          tinyint(1)                               not null comment '家族史-缺血性脑卒中（脑梗死） （1-是，0-否）',
    is_fh_coronary_heart_disease       tinyint(1)                               not null comment '家族史-冠心病 （1-是，0-否）',
    is_fh_copd                         tinyint(1)                               not null comment '家族史-慢阻肺 （1-是，0-否）',
    is_symptom_dizzy                   tinyint(1)                               not null comment '症状-头晕，头疼症状 （1-是，0-否）',
    is_symptom_chest_pain              tinyint(1)                               not null comment '症状-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解  （1-是，0-否）',
    is_symptom_chronic_cough           tinyint(1)                               not null comment '症状-呼吸困难或慢性咳嗽  （1-是，0-否）',
    is_symptom_weight_loss             tinyint(1)                               not null comment '症状-多饮、多尿、多食、不明原因体重下降   （1-是，0-否）',
    is_symptom_giddiness               tinyint(1)                               not null comment '症状-一过性黑曚、眩晕 （1-是，0-否）',
    is_symptom_none                    tinyint(1)                               not null comment '症状-无以上症状',
    created_by                         bigint unsigned                          not null comment '创建人id',
    created_at                         datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    is_pmh_diabetic_nephropathy        tinyint(1)  default 0                    not null comment '既往病史-糖尿病肾病（1-是，0-否）',
    is_pmh_diabetic_retinopathy        tinyint(1)  default 0                    not null comment '既往病史-糖尿病视网膜病变（1-是，0-否）',
    is_pmh_diabetic_foot               tinyint(1)  default 0                    not null comment '既往病史-糖尿病足（1-是，0-否）'
) comment '五病综合评估表' charset = utf8mb4;



