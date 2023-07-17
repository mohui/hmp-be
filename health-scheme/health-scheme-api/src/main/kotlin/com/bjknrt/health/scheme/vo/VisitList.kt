package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param name  
 * @param followDate  
 * @param signsSbp  血压 - 收缩压
 * @param signsDbp  血压 - 舒张压
 * @param signsHeight  身高
 * @param signsWeight  体重
 * @param signsHeartRate  心率
 * @param signsBim  体质指数
 * @param glu  空腹血糖
 * @param createdBy  
 */
data class VisitList(

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:Valid
    @field:JsonProperty("name") val name: VisitType? = null,

    @field:JsonProperty("followDate") val followDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("signsSbp") val signsSbp: java.math.BigDecimal? = null,

    @field:JsonProperty("signsDbp") val signsDbp: java.math.BigDecimal? = null,

    @field:JsonProperty("signsHeight") val signsHeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsWeight") val signsWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsHeartRate") val signsHeartRate: java.math.BigDecimal? = null,

    @field:JsonProperty("signsBim") val signsBim: java.math.BigDecimal? = null,

    @field:JsonProperty("glu") val glu: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: Id? = null
) {

}

