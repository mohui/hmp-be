package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ReportPageRequest
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param patientId  
 * @param healthManageType  
 */
data class ReportPageRequest(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("healthManageType") val healthManageType: HealthManageType? = null
) {

}

