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
* 身份标签
* Values: REGION_ADMIN,ORG_ADMIN,DOCTOR,NURSE,PATIENT
*/
enum class IdentityTag(val value: kotlin.String) {

    /**
     * 区域管理员
     */
    @JsonProperty("REGION_ADMIN") REGION_ADMIN("REGION_ADMIN"),
    
    /**
     * 机构管理员
     */
    @JsonProperty("ORG_ADMIN") ORG_ADMIN("ORG_ADMIN"),
    
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

