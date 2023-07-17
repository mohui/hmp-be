package com.bjknrt.user.permission.centre.vo

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
* Values: ADMIN,REGION_ADMIN,DOCTOR,NURSE,PATIENT
*/
enum class RoleCode(val value: kotlin.String) {

    /**
     * 管理员
     */
    @JsonProperty("ADMIN") ADMIN("ADMIN"),
    
    /**
     * 行政管理员
     */
    @JsonProperty("REGION_ADMIN") REGION_ADMIN("REGION_ADMIN"),
    
    /**
     * 医生
     */
    @JsonProperty("DOCTOR") DOCTOR("DOCTOR"),
    
    /**
     * 护士
     */
    @JsonProperty("NURSE") NURSE("NURSE"),
    
    /**
     * 患者
     */
    @JsonProperty("PATIENT") PATIENT("PATIENT")
    
}

