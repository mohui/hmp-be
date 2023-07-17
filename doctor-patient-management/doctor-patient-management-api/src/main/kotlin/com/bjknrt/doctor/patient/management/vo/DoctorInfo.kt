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
 * 
 * @param id  
 * @param name 医生姓名 
 * @param gender  
 * @param phone 手机号 
 * @param deptName 科室名称 
 * @param hospitalId  
 * @param hospitalName 机构名称 
 * @param address 详细地址 
 * @param doctorLevel  
 */
data class DoctorInfo(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,
    
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:JsonProperty("deptName", required = true) val deptName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("hospitalId", required = true) val hospitalId: java.math.BigInteger,
    
    @field:JsonProperty("hospitalName", required = true) val hospitalName: kotlin.String,
    
    @field:JsonProperty("address", required = true) val address: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("doctorLevel", required = true) val doctorLevel: DoctorLevel
) {

}

