package com.bjknrt.statistic.analysis.vo

import java.util.Objects
import com.bjknrt.statistic.analysis.vo.DoctorLevel
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
 * OrgHospitalDoctorPatientNum
 * @param id
 * @param name  医生姓名
 * @param level
 * @param num
 */
data class OrgHospitalDoctorPatientNum(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("level", required = true) val level: DoctorLevel,

    @field:JsonProperty("num", required = true) val num: kotlin.Long
) {

}

