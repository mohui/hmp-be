package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
*
* Values: HIGH,OTHER
*/
enum class RiskLevel(val value: kotlin.String) {

    /**
     * 高危
     */
    @JsonProperty("HIGH") HIGH("HIGH"),

    /**
     * 中低危
     */
    @JsonProperty("OTHER") OTHER("OTHER")

}

