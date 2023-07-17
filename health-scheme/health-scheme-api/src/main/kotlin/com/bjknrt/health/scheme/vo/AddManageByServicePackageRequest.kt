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
 * AddManageByServicePackageRequest
 * @param patientId  
 * @param startDate  
 * @param serviceCodeList  订阅的服务包编码集合
 */
data class AddManageByServicePackageRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDate,
    
    @field:JsonProperty("serviceCodeList", required = true) val serviceCodeList: kotlin.collections.List<kotlin.String>
) {

}

