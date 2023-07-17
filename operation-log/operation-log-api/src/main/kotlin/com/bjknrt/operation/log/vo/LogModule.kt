package com.bjknrt.operation.log.vo

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
* Values: HS,DPM,UPS,PATIENT_CLIENT,HEALTHY
*/
enum class LogModule(val value: kotlin.String) {

    /**
     * 健康方案模块
     */
    @JsonProperty("HS") HS("HS"),
    
    /**
     * 医患关系服务
     */
    @JsonProperty("DPM") DPM("DPM"),
    
    /**
     * 用户权限中心服务
     */
    @JsonProperty("UPS") UPS("UPS"),
    
    /**
     * 患者端
     */
    @JsonProperty("PATIENT_CLIENT") PATIENT_CLIENT("PATIENT_CLIENT"),
    
    /**
     * 健康科普
     */
    @JsonProperty("HEALTHY") HEALTHY("HEALTHY")
    
}

