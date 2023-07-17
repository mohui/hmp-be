package com.bjknrt.framework.api.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Min

data class Page(

    @field:Min(0)
    @field:JsonProperty("pageNo", required = true) val pageNo: Long,

    @field:Min(1)
    @field:JsonProperty("pageSize", required = true) val pageSize: Long
)