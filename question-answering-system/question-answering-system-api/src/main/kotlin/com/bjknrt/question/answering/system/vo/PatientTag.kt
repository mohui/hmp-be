package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 高危标签（HIGH-高危,LOW-低危,EXISTS-患者）
* Values: HIGH,LOW,EXISTS
*/
enum class PatientTag(val value: kotlin.String) {

    /**
     * 高危
     */
    @JsonProperty("HIGH") HIGH("HIGH"),

    /**
     * 低危
     */
    @JsonProperty("LOW") LOW("LOW"),

    /**
     * 患者
     */
    @JsonProperty("EXISTS") EXISTS("EXISTS")

}

