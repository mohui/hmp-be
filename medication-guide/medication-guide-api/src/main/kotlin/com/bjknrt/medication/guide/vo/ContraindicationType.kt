package com.bjknrt.medication.guide.vo

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
* 
* Values: GERIATRIC,DRUGDISEASE,INDICATION,DRUGDRUG,INGREDIENT,DOSELIMIT
*/
enum class ContraindicationType(val value: kotlin.String) {

    /**
     * 老年用药禁忌
     */
    @JsonProperty("GERIATRIC") GERIATRIC("GERIATRIC"),
    
    /**
     * 药品禁忌
     */
    @JsonProperty("DRUGDISEASE") DRUGDISEASE("DRUGDISEASE"),
    
    /**
     * 非适应症用药
     */
    @JsonProperty("INDICATION") INDICATION("INDICATION"),
    
    /**
     * 药品间禁忌
     */
    @JsonProperty("DRUGDRUG") DRUGDRUG("DRUGDRUG"),
    
    /**
     * 成分重复
     */
    @JsonProperty("INGREDIENT") INGREDIENT("INGREDIENT"),
    
    /**
     * 推荐用量
     */
    @JsonProperty("DOSELIMIT") DOSELIMIT("DOSELIMIT")
    
}

