package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * HealthServiceActivationCode
 * @param activationCode  激活码
 * @param status  
 * @param createdByName  创建人
 * @param createdAt  
 * @param healthServices  服务包列表
 * @param forbiddenByName  禁用人
 * @param forbiddenAt  
 * @param purchasedByName  购买人
 * @param purchaserPhone  购买人手机号
 * @param purchaseRemarks  购买备注
 */
data class HealthServiceActivationCode(
    
    @field:JsonProperty("activationCode", required = true) val activationCode: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("status", required = true) val status: HealthServiceActivationCodeStatus,
    
    @field:JsonProperty("createdByName", required = true) val createdByName: kotlin.String,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("healthServices", required = true) val healthServices: kotlin.collections.List<HealthServiceGetActivationCodePost200ResponseHealthServicesInner>,

    @field:JsonProperty("forbiddenByName") val forbiddenByName: kotlin.String? = null,

    @field:JsonProperty("forbiddenAt") val forbiddenAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("purchasedByName") val purchasedByName: kotlin.String? = null,

    @field:JsonProperty("purchaserPhone") val purchaserPhone: kotlin.String? = null,

    @field:JsonProperty("purchaseRemarks") val purchaseRemarks: kotlin.String? = null
) {

}

