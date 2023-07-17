package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 性别（MAN-男，WOMAN-女,UNKNOWN-未知）
* Values: MAN,WOMAN,UNKNOWN
*/
enum class Gender(val value: kotlin.String) {

    /**
     * 男
     */
    @JsonProperty("MAN") MAN("MAN"),

    /**
     * 女
     */
    @JsonProperty("WOMAN") WOMAN("WOMAN"),

    /**
     * 未知
     */
    @JsonProperty("UNKNOWN") UNKNOWN("UNKNOWN")

}

