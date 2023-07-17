package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 五病综合标签（HIGH-高危,LOW-低危）
* Values: HIGH,LOW
*/
enum class PatientSynthesisTag(val value: kotlin.String) {

    /**
     * 高危
     */
    @JsonProperty("HIGH") HIGH("HIGH"),

    /**
     * 低危
     */
    @JsonProperty("LOW") LOW("LOW")

}

