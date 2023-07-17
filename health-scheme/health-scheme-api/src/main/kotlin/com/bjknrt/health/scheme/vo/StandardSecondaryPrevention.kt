package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
* 标准用药-是否标准二级预防用药-单药抗血小板:SINGLE_DRUG_ANTI_PLATELET，双联抗血小板:DOUBLE_COUPLET_ANTI_PLATELET，抗凝抗血小板+抗凝:ANTICOAGULANT_ANTI_PLATELET，未标准用药:NOT_STANDARD_MEDICATION
* Values: SINGLE_DRUG_ANTI_PLATELET,DOUBLE_COUPLET_ANTI_PLATELET,ANTICOAGULANT_ANTI_PLATELET,NOT_STANDARD_MEDICATION
*/
enum class StandardSecondaryPrevention(val value: kotlin.String) {

    /**
     * 单药抗血小板
     */
    @JsonProperty("SINGLE_DRUG_ANTI_PLATELET") SINGLE_DRUG_ANTI_PLATELET("SINGLE_DRUG_ANTI_PLATELET"),
    
    /**
     * 双联抗血小板
     */
    @JsonProperty("DOUBLE_COUPLET_ANTI_PLATELET") DOUBLE_COUPLET_ANTI_PLATELET("DOUBLE_COUPLET_ANTI_PLATELET"),
    
    /**
     * 抗凝抗血小板+抗凝
     */
    @JsonProperty("ANTICOAGULANT_ANTI_PLATELET") ANTICOAGULANT_ANTI_PLATELET("ANTICOAGULANT_ANTI_PLATELET"),
    
    /**
     * 未标准用药
     */
    @JsonProperty("NOT_STANDARD_MEDICATION") NOT_STANDARD_MEDICATION("NOT_STANDARD_MEDICATION")
    
}

