package com.bjknrt.health.scheme.vo

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
 * @param drugName  药品名称
 * @param timesPerDay  每日次数
 * @param unitsPerTime  每次用量
 * @param drugId  
 * @param drugManufacturers  药品厂家
 */
data class DrugsInner(

    @field:JsonProperty("drugName") val drugName: kotlin.String? = null,

    @field:JsonProperty("timesPerDay") val timesPerDay: kotlin.Int? = null,

    @field:JsonProperty("unitsPerTime") val unitsPerTime: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("drugId") val drugId: java.math.BigInteger? = null,

    @field:JsonProperty("drugManufacturers") val drugManufacturers: kotlin.String? = null
) {

}

