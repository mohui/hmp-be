package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
*
* Values: HTN,T2DM,TEMP,DNT,CHD,COPD,NONE
*/
enum class ServiceCode(val value: kotlin.String) {

    /**
     *
     */
    @JsonProperty("htn") HTN("htn"),

    /**
     *
     */
    @JsonProperty("t2dm") T2DM("t2dm"),

    /**
     *
     */
    @JsonProperty("temp") TEMP("temp"),

    /**
     *
     */
    @JsonProperty("dnt") DNT("dnt"),

    /**
     *
     */
    @JsonProperty("chd") CHD("chd"),

    /**
     *
     */
    @JsonProperty("copd") COPD("copd"),

    /**
     *
     */
    @JsonProperty("none") NONE("none")

}

