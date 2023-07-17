package com.bjknrt.article.service.vo

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
 * SaveReadRecordParam
 * @param readerId  
 * @param articleId  
 */
data class SaveReadRecordParam(
    
    @field:Valid
    @field:JsonProperty("readerId", required = true) val readerId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("articleId", required = true) val articleId: java.math.BigInteger
) {

}

