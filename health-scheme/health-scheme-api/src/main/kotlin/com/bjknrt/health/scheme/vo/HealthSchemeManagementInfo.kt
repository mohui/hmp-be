package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.scheme.vo.ManageStage
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
 * CurrentHealthSchemeManagementInfoResult
 * @param knId  
 * @param knStartDate  
 * @param knPatientId  
 * @param knHealthManageType  
 * @param knDiseaseExistsTag 健康方案病种患者标签,多个逗号隔开 
 * @param knManagementStage  
 * @param knEndDate  
 * @param knReportOutputDate  
 * @param knJobId 任务Id 
 */
data class HealthSchemeManagementInfo(
    
    @field:Valid
    @field:JsonProperty("KnId", required = true) val knId: Id,
    
    @field:Valid
    @field:JsonProperty("KnStartDate", required = true) val knStartDate: java.time.LocalDate,
    
    @field:Valid
    @field:JsonProperty("KnPatientId", required = true) val knPatientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("KnHealthManageType", required = true) val knHealthManageType: HealthManageType,
    
    @field:JsonProperty("KnDiseaseExistsTag", required = true) val knDiseaseExistsTag: kotlin.String,

    @field:Valid
    @field:JsonProperty("KnManagementStage") val knManagementStage: ManageStage? = null,

    @field:Valid
    @field:JsonProperty("KnEndDate") val knEndDate: java.time.LocalDate? = null,

    @field:Valid
    @field:JsonProperty("KnReportOutputDate") val knReportOutputDate: java.time.LocalDate? = null,

    @field:JsonProperty("KnJobId") val knJobId: kotlin.String? = null
) {

}

