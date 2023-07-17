package com.bjknrt.health.indicator.vo

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
 * @param measureDate
 * @param beer
 * @param wine
 * @param whiteSpirit
 * @param yellowRichSpirit
 * @param totalAlcohol
 */
data class DrinkingStatistic(

    @field:Valid
    @field:JsonProperty("measureDate", required = true) val measureDate: java.time.LocalDate,

    @field:JsonProperty("beer", required = true) val beer: java.math.BigDecimal,

    @field:JsonProperty("wine", required = true) val wine: java.math.BigDecimal,

    @field:JsonProperty("whiteSpirit", required = true) val whiteSpirit: java.math.BigDecimal,

    @field:JsonProperty("yellowRichSpirit", required = true) val yellowRichSpirit: java.math.BigDecimal,

    @field:JsonProperty("totalAlcohol", required = true) val totalAlcohol: java.math.BigDecimal
) {

}

