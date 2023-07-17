package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
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
 * ValidOrgRegionPermissionResult
 * @param isRun  是否执行
 * @param orgIdSet  行政区域ID
 * @param regionIdSet  组织ID
 */
data class ValidOrgRegionPermissionResult(
    
    @field:JsonProperty("isRun", required = true) val isRun: kotlin.Boolean,

    @field:Valid
    @field:JsonProperty("orgIdSet") val orgIdSet: kotlin.collections.List<Id>? = null,

    @field:Valid
    @field:JsonProperty("regionIdSet") val regionIdSet: kotlin.collections.List<Id>? = null
) {

}

