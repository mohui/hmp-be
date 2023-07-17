package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.bjknrt.doctor.patient.management.vo.HealthManageType
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
 * ServicePackageInfo
 * @param id
 * @param code  服务包编码
 * @param name  服务包名称
 * @param isUsed  是否正在使用
 * @param healthManageType
 */
data class ServicePackageInfo(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("code", required = true) val code: kotlin.String,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean,

    @field:Valid
    @field:JsonProperty("healthManageType", required = true) val healthManageType: HealthManageType
) {

}

