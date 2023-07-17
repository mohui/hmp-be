package com.bjknrt.user.permission.centre.entity

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class RoleEntity(
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("code", required = true) val code: kotlin.String,

    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean,

) {
}