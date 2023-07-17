package com.bjknrt.doctor.patient.management.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DoctorAddRequest
 * @param authId
 * @param name 姓名
 * @param gender
 * @param deptName 科室名称
 * @param address 地址
 * @param phone 手机号
 * @param doctorLevel
 * @param hospitalId
 * @param hospitalName
 */
data class DoctorAddRequest(

    @field:Valid
    @field:JsonProperty("authId", required = true) val authId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("deptName", required = true) val deptName: kotlin.String,

    @field:JsonProperty("address", required = true) val address: kotlin.String,

    @field:JsonProperty("phone", required = true) val phone: kotlin.String,

    @field:Valid
    @field:JsonProperty("doctorLevel", required = true) val doctorLevel: DoctorLevel,

    @field:Valid
    @field:JsonProperty("hospitalId", required = true) val hospitalId: Id,

    @field:JsonProperty("hospitalName", required = true) val hospitalName: kotlin.String
) {

}

