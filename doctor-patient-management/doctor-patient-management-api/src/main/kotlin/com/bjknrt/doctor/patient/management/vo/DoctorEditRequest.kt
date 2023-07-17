package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.bjknrt.doctor.patient.management.vo.DoctorLevel
import com.bjknrt.doctor.patient.management.vo.Gender
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
 * DoctorEditRequest
 * @param id  
 * @param name 姓名 
 * @param gender  
 * @param deptName 科室名称 
 * @param address 地址 
 * @param phone 手机号 
 * @param doctorLevel  
 */
data class DoctorEditRequest(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,
    
    @field:JsonProperty("deptName", required = true) val deptName: kotlin.String,
    
    @field:JsonProperty("address", required = true) val address: kotlin.String,
    
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("doctorLevel", required = true) val doctorLevel: DoctorLevel
) {

}

