package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id
 * @param user
 * @param drug  药品名称
 * @param time Time 时间
 */
data class NotifyDrugPostRequestInner(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("user", required = true) val user: Id,

    @field:JsonProperty("drug", required = true) val drug: kotlin.String,

    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime
) {

}

