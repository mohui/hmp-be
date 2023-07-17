package com.bjknrt.doctor.patient.management.vo

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
* 症状（DIZZY-头晕，头疼症状，CHEST_PAIN-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解，CHRONIC_COUGH-呼吸困难或慢性咳嗽，WEIGHT_LOSS-多饮、多尿、多食、不明原因体重下降，GIDDINESS-一过性黑蒙、眩晕）
* Values: DIZZY,CHEST_PAIN,CHRONIC_COUGH,WEIGHT_LOSS,GIDDINESS
*/
enum class PatientSymptom(val value: kotlin.String) {

    /**
     * 头晕，头疼症状
     */
    @JsonProperty("DIZZY") DIZZY("DIZZY"),
    
    /**
     * 体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解
     */
    @JsonProperty("CHEST_PAIN") CHEST_PAIN("CHEST_PAIN"),
    
    /**
     * 呼吸困难或慢性咳嗽
     */
    @JsonProperty("CHRONIC_COUGH") CHRONIC_COUGH("CHRONIC_COUGH"),
    
    /**
     * 多饮、多尿、多食、不明原因体重下降
     */
    @JsonProperty("WEIGHT_LOSS") WEIGHT_LOSS("WEIGHT_LOSS"),
    
    /**
     * 一过性黑蒙、眩晕
     */
    @JsonProperty("GIDDINESS") GIDDINESS("GIDDINESS")
    
}

