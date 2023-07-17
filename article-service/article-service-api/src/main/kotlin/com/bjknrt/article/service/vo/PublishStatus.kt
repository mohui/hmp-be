package com.bjknrt.article.service.vo

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
* 未发布：UN_PUBLISHED，已发布：PUBLISHED，已下架：OFF_SHELF
* Values: UN_PUBLISHED,PUBLISHED,OFF_SHELF
*/
enum class PublishStatus(val value: kotlin.String) {

    /**
     * 未发布，即存为草稿
     */
    @JsonProperty("UN_PUBLISHED") UN_PUBLISHED("UN_PUBLISHED"),
    
    /**
     * 已发布
     */
    @JsonProperty("PUBLISHED") PUBLISHED("PUBLISHED"),
    
    /**
     * 已下架
     */
    @JsonProperty("OFF_SHELF") OFF_SHELF("OFF_SHELF")
    
}

