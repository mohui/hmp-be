package com.bjknrt.health.scheme.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class ExaminationCodeEnum(val value: kotlin.String) {
    /**
     * 运动评估编码
     */
    @JsonProperty("SPORT")
    SPORT("运动评估问卷编码"),

    /**
     * 用药评估-高血压
     */
    @JsonProperty("HYPERTENSION_DRUG_PROGRAM")
    HYPERTENSION_DRUG_PROGRAM("用药评估-高血压"),

    /**
     * 用药评估-糖尿病
     */
    @JsonProperty("DIABETES_DRUG_PROGRAM")
    DIABETES_DRUG_PROGRAM("用药评估-糖尿病"),

    /**
     * 饮食评估-高血压
     */
    @JsonProperty("DIET_EVALUATE_HYPERTENSION")
    DIET_EVALUATE_HYPERTENSION("饮食评估-高血压"),

    /**
     * 饮食评估-糖尿病
     */
    @JsonProperty("DIET_EVALUATE_DIABETES")
    DIET_EVALUATE_DIABETES("饮食评估-糖尿病")
}