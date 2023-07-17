package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 家族史（HYPERTENSION-高血压,DIABETES-糖尿病,ACUTE_CORONARY_DISEASE-冠心病,CEREBRAL_STROKE-脑卒中,COPD-慢阻肺）
* Values: HYPERTENSION,DIABETES,ACUTE_CORONARY_DISEASE,CEREBRAL_STROKE,COPD
*/
enum class FamilyHistory(val value: kotlin.String) {

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
    @JsonProperty("COPD") COPD("COPD")

}

