package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 疾病史（HYPERTENSION-高血压,DIABETES-糖尿病,ACUTE_CORONARY_DISEASE-冠心病,CEREBRAL_STROKE-脑卒中,COPD-慢阻肺,DYSLIPIDEMIA-血脂异常）
* Values: HYPERTENSION,DIABETES,ACUTE_CORONARY_DISEASE,CEREBRAL_STROKE,COPD,DYSLIPIDEMIA,DIABETIC_NEPHROPATHY,DIABETIC_RETINOPATHY,DIABETIC_FOOT
*/
enum class MedicalHistory(val value: kotlin.String) {

    /**
     * 高血压
     */
    @JsonProperty("HYPERTENSION") HYPERTENSION("HYPERTENSION"),

    /**
     * 糖尿病
     */
    @JsonProperty("DIABETES") DIABETES("DIABETES"),

    /**
     * 冠心病
     */
    @JsonProperty("ACUTE_CORONARY_DISEASE") ACUTE_CORONARY_DISEASE("ACUTE_CORONARY_DISEASE"),

    /**
     * 脑卒中
     */
    @JsonProperty("CEREBRAL_STROKE") CEREBRAL_STROKE("CEREBRAL_STROKE"),

    /**
     * 慢阻肺
     */
    @JsonProperty("COPD") COPD("COPD"),

    /**
     * 血脂异常
     */
    @JsonProperty("DYSLIPIDEMIA") DYSLIPIDEMIA("DYSLIPIDEMIA"),

    /**
     * 糖尿病肾病
     */
    @JsonProperty("DIABETIC_NEPHROPATHY") DIABETIC_NEPHROPATHY("DIABETIC_NEPHROPATHY"),

    /**
     * 糖尿病视网膜病变
     */
    @JsonProperty("DIABETIC_RETINOPATHY") DIABETIC_RETINOPATHY("DIABETIC_RETINOPATHY"),

    /**
     * 糖尿病足
     */
    @JsonProperty("DIABETIC_FOOT") DIABETIC_FOOT("DIABETIC_FOOT")

}

