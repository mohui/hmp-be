package com.bjknrt.medication.guide.vo

import java.util.Objects
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
 * @param id  药品编号
 * @param name  药品名称
 * @param strength  规格
 * @param doseageForm  剂型
 * @param manufactor  厂家
 * @param doseUnit  剂量单位
 */
data class DrugList200ResponseInner(

    @field:JsonProperty("id", required = true) val id: kotlin.Long,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("strength", required = true) val strength: kotlin.String,

    @field:JsonProperty("doseageForm", required = true) val doseageForm: kotlin.String,

    @field:JsonProperty("manufactor", required = true) val manufactor: kotlin.String,

    @field:JsonProperty("doseUnit") val doseUnit: kotlin.String? = null,
) {

}

