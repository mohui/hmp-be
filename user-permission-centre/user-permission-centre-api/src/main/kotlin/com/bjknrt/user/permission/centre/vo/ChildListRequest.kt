package com.bjknrt.user.permission.centre.vo

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
 * ChildListRequest
 * @param code  
 */
data class ChildListRequest(

    @field:Valid
    @field:JsonProperty("code") val code: java.math.BigInteger? = null
) {

}

