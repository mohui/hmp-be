package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.bjknrt.doctor.patient.management.vo.CrowdType
import com.bjknrt.doctor.patient.management.vo.ServiceCode
import com.bjknrt.doctor.patient.management.vo.ToDoStatus
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
 * PatientPageRequest
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param doctorId  
 * @param keyword 手机号或姓名 
 * @param crowdTypeAnyMatch 人群分类任意满足 
 * @param status  
 * @param crowdTypeSet  病种名称
 * @param orgIdSet  机构ID
 * @param regionIdSet  区域code
 * @param servicePackageCodeSet  服务包编码
 * @param messageStatus 消息类型 
 */
data class PatientPageRequest(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: java.math.BigInteger? = null,

    @field:JsonProperty("keyword") val keyword: kotlin.String? = null,

    @field:JsonProperty("crowdTypeAnyMatch") val crowdTypeAnyMatch: kotlin.Boolean? = null,

    @field:Valid
    @field:JsonProperty("status") val status: ToDoStatus? = null,

    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("crowdTypeSet") val crowdTypeSet: kotlin.collections.Set<CrowdType>? = null,

    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("orgIdSet") val orgIdSet: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("regionIdSet") val regionIdSet: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("servicePackageCodeSet") val servicePackageCodeSet: kotlin.collections.Set<ServiceCode>? = null,

    @field:Valid
    @field:JsonProperty("messageStatus") val messageStatus: MessageStatus? = null
) {

}

