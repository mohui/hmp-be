package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
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
* 
* Values: RESIDENT_PHYSICIAN,IN_CHARGE_PHYSICIAN,CHIEF_PHYSICIAN,ASSOCIATE_CHIEF_PHYSICIAN,JUNIOR_NURSE,JUNIOR_SENIORNURSE,CHARGE_SENIORNURSE,CHIEF_SENIORNURSE,ASSOCIATE_CHIEF_SENIORNURSE,OTHER
*/
enum class DoctorLevel(val value: kotlin.String) {

    /**
     * 住院医师 
     */
    @JsonProperty("RESIDENT_PHYSICIAN") RESIDENT_PHYSICIAN("RESIDENT_PHYSICIAN"),
    
    /**
     * 主治医师
     */
    @JsonProperty("IN_CHARGE_PHYSICIAN") IN_CHARGE_PHYSICIAN("IN_CHARGE_PHYSICIAN"),
    
    /**
     * 主任医师
     */
    @JsonProperty("CHIEF_PHYSICIAN") CHIEF_PHYSICIAN("CHIEF_PHYSICIAN"),
    
    /**
     * 副主任医师
     */
    @JsonProperty("ASSOCIATE_CHIEF_PHYSICIAN") ASSOCIATE_CHIEF_PHYSICIAN("ASSOCIATE_CHIEF_PHYSICIAN"),
    
    /**
     * 初级护士
     */
    @JsonProperty("JUNIOR_NURSE") JUNIOR_NURSE("JUNIOR_NURSE"),
    
    /**
     * 初级护师
     */
    @JsonProperty("JUNIOR_SENIORNURSE") JUNIOR_SENIORNURSE("JUNIOR_SENIORNURSE"),
    
    /**
     * 主管护师
     */
    @JsonProperty("CHARGE_SENIORNURSE") CHARGE_SENIORNURSE("CHARGE_SENIORNURSE"),
    
    /**
     * 主任护师
     */
    @JsonProperty("CHIEF_SENIORNURSE") CHIEF_SENIORNURSE("CHIEF_SENIORNURSE"),
    
    /**
     * 副主任护师
     */
    @JsonProperty("ASSOCIATE_CHIEF_SENIORNURSE") ASSOCIATE_CHIEF_SENIORNURSE("ASSOCIATE_CHIEF_SENIORNURSE"),
    
    /**
     * 其他
     */
    @JsonProperty("OTHER") OTHER("OTHER")
    
}

