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
 * MedicationOrderInner
 * @param drugId  药品编号
 * @param frequency  频次-每天多少次
 * @param singleDose  单次剂量
 * @param doseUnit  单次剂量单位
 */
data class MedicationOrderInner(

    @field:JsonProperty("drug_id", required = true) val drugId: kotlin.Long,

    @field:JsonProperty("frequency", required = true) val frequency: kotlin.Int,

    @field:JsonProperty("single_dose", required = true) val singleDose: java.math.BigDecimal,
) {

}

