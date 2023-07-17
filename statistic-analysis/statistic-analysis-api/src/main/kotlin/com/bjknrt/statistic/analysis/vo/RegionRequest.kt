package com.bjknrt.statistic.analysis.vo

import java.util.Objects
import com.bjknrt.statistic.analysis.vo.CrowdType
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
 * RegionRequest
 * @param code  
 * @param crowdTypeList  人群分类
 */
data class RegionRequest(

    @field:Valid
    @field:JsonProperty("code") val code: java.math.BigInteger? = null,

    @field:Valid
    @field:JsonProperty("crowdTypeList") val crowdTypeList: kotlin.collections.List<CrowdType>? = null
) {

}

