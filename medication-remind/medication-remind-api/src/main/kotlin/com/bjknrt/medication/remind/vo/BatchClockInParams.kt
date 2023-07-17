package com.bjknrt.medication.remind.vo

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
 * batchClockInParams
 * @param ids  要打卡的ID
 * @param clockAt  
 */
data class BatchClockInParams(
    
    @field:Valid
    @field:JsonProperty("ids", required = true) val ids: kotlin.collections.List<Id>,

    @field:JsonProperty("clockAt") val clockAt: java.time.LocalDateTime? = null
) {

}

