package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 性别（MAN-男，WOMAN-女）
* Values: MAN,WOMAN
*/
enum class Gender(val value: kotlin.String) {

    /**
     * 男
     */
    @JsonProperty("MAN") MAN("MAN"),

    /**
     * 女
     */
    @JsonProperty("WOMAN") WOMAN("WOMAN")

}

