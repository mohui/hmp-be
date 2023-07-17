package com.bjknrt.health.scheme.vo

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
* 服药依从性. LAW: 规律; GAP: 间断; NO: 不服药
* Values: LAW,GAP,NO
*/
enum class DrugCompliance(val value: kotlin.String) {

    /**
     * 规律
     */
    @JsonProperty("LAW") LAW("LAW"),
    
    /**
     * 间断
     */
    @JsonProperty("GAP") GAP("GAP"),
    
    /**
     * 不服药
     */
    @JsonProperty("NO") NO("NO")
    
}

