package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 身份标签
* Values: REGION_ADMIN,ORG_ADMIN,DOCTOR,NURSE,PATIENT
*/
enum class IdentityTag(val value: kotlin.String) {

    /**
     * 区域管理员
     */
    @JsonProperty("REGION_ADMIN") REGION_ADMIN("REGION_ADMIN"),

    /**
     * 机构管理员
     */
    @JsonProperty("ORG_ADMIN") ORG_ADMIN("ORG_ADMIN"),

    /**
     * 医生
     */
    @JsonProperty("DOCTOR") DOCTOR("DOCTOR"),

    /**
     * 护士
     */
    @JsonProperty("NURSE") NURSE("NURSE"),

    /**
     * 患者
     */
    @JsonProperty("PATIENT") PATIENT("PATIENT")

}

