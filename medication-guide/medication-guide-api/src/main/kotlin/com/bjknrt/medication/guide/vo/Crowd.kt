package com.bjknrt.medication.guide.vo

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
* 18岁及以下儿童-PEDIATRIC 老年人-GERIATRIC 男性-MALE 女性-FEMALE 孕妇-PREGNANCY 哺乳期妇女-LACTATION
* Values: PEDIATRIC,GERIATRIC,MALE,FEMALE,PREGNANCY,LACTATION
*/
enum class Crowd(val value: kotlin.String) {

    /**
     * 18岁及以下儿童
     */
    @JsonProperty("PEDIATRIC") PEDIATRIC("PEDIATRIC"),
    
    /**
     * 老年人
     */
    @JsonProperty("GERIATRIC") GERIATRIC("GERIATRIC"),
    
    /**
     * 男性
     */
    @JsonProperty("MALE") MALE("MALE"),
    
    /**
     * 女性
     */
    @JsonProperty("FEMALE") FEMALE("FEMALE"),
    
    /**
     * 孕妇
     */
    @JsonProperty("PREGNANCY") PREGNANCY("PREGNANCY"),
    
    /**
     * 哺乳期妇女
     */
    @JsonProperty("LACTATION") LACTATION("LACTATION")
    
}

