package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param healthServices  服务包列表
 * @param purchasedByName  购买人
 * @param purchaserPhone  购买人手机号
 * @param purchaseRemarks  购买备注
 */
data class HealthServiceAddActivationCodePostRequest(
    
    @field:Valid
    @field:JsonProperty("healthServices", required = true) val healthServices: kotlin.collections.List<HealthServiceGetActivationCodePost200ResponseHealthServicesInner>,

    @field:JsonProperty("purchasedByName") val purchasedByName: kotlin.String? = null,

    @field:JsonProperty("purchaserPhone") val purchaserPhone: kotlin.String? = null,

    @field:JsonProperty("purchaseRemarks") val purchaseRemarks: kotlin.String? = null
) {

}

