package com.bjknrt.user.permission.centre.enum

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * Values: ADMIN,REGION_ADMIN,DOCTOR,NURSE,PATIENT
 */
enum class RoleCodeEnum(val value: kotlin.String) {

    /**
     * 管理员
     */
    @JsonProperty("ADMIN") ADMIN("ADMIN"),

    /**
     * 行政管理员
     */
    @JsonProperty("REGION_ADMIN") REGION_ADMIN("REGION_ADMIN"),

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

