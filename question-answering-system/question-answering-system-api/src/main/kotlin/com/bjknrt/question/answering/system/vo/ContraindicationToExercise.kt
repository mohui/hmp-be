package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param code  选项下标
 * @param name  下标对应内容
 */
data class ContraindicationToExercise(

    @field:JsonProperty("code", required = true) val code: kotlin.Int,

    @field:JsonProperty("name") val name: kotlin.String? = null
) {

}

