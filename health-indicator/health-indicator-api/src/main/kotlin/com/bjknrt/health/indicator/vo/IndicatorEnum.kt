package com.bjknrt.health.indicator.vo

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
* Values: BODY_HEIGHT,BODY_WEIGHT,BMI,WAISTLINE,BODY_TEMPERATURE,SYSTOLIC_BLOOD_PRESSURE,DIASTOLIC_BLOOD_PRESSURE,FASTING_BLOOD_SUGAR,BEFORE_LUNCH_BLOOD_SUGAR,BEFORE_DINNER_BLOOD_SUGAR,RANDOM_BLOOD_SUGAR,AFTER_MEAL_BLOOD_SUGAR,AFTER_LUNCH_BLOOD_SUGAR,AFTER_DINNER_BLOOD_SUGAR,BEFORE_SLEEP_BLOOD_SUGAR,BLOOD_LIPIDS_TOTAL_CHOLESTEROL,BLOOD_LIPIDS_TRIGLYCERIDES,BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN,BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN,HEART_RATE,PULSE,SMOKE,DRINKING_BEER,DRINKING_WHITE_SPIRIT,DRINKING_WINE,DRINKING_YELLOW_RICE_SPIRIT,PULSE_OXIMETRY,DRINKING_DAILY_ALCOHOL,SMOKE_DAILY_QUANTITY
*/
enum class IndicatorEnum(val value: kotlin.String) {

    /**
     * 身高
     */
    @JsonProperty("BODY_HEIGHT") BODY_HEIGHT("BODY_HEIGHT"),
    
    /**
     * 体重
     */
    @JsonProperty("BODY_WEIGHT") BODY_WEIGHT("BODY_WEIGHT"),
    
    /**
     * BMI
     */
    @JsonProperty("BMI") BMI("BMI"),
    
    /**
     * 腰围
     */
    @JsonProperty("WAISTLINE") WAISTLINE("WAISTLINE"),
    
    /**
     * 体温
     */
    @JsonProperty("BODY_TEMPERATURE") BODY_TEMPERATURE("BODY_TEMPERATURE"),
    
    /**
     * 收缩压
     */
    @JsonProperty("SYSTOLIC_BLOOD_PRESSURE") SYSTOLIC_BLOOD_PRESSURE("SYSTOLIC_BLOOD_PRESSURE"),
    
    /**
     * 舒张压
     */
    @JsonProperty("DIASTOLIC_BLOOD_PRESSURE") DIASTOLIC_BLOOD_PRESSURE("DIASTOLIC_BLOOD_PRESSURE"),
    
    /**
     * 空腹血糖（餐前血糖的早餐）
     */
    @JsonProperty("FASTING_BLOOD_SUGAR") FASTING_BLOOD_SUGAR("FASTING_BLOOD_SUGAR"),
    
    /**
     * 餐前血糖（午餐）
     */
    @JsonProperty("BEFORE_LUNCH_BLOOD_SUGAR") BEFORE_LUNCH_BLOOD_SUGAR("BEFORE_LUNCH_BLOOD_SUGAR"),
    
    /**
     * 餐前血糖（晚餐）
     */
    @JsonProperty("BEFORE_DINNER_BLOOD_SUGAR") BEFORE_DINNER_BLOOD_SUGAR("BEFORE_DINNER_BLOOD_SUGAR"),
    
    /**
     * 随机血糖
     */
    @JsonProperty("RANDOM_BLOOD_SUGAR") RANDOM_BLOOD_SUGAR("RANDOM_BLOOD_SUGAR"),
    
    /**
     * 餐后2h血糖（早餐）
     */
    @JsonProperty("AFTER_MEAL_BLOOD_SUGAR") AFTER_MEAL_BLOOD_SUGAR("AFTER_MEAL_BLOOD_SUGAR"),
    
    /**
     * 餐后2h血糖（午餐）
     */
    @JsonProperty("AFTER_LUNCH_BLOOD_SUGAR") AFTER_LUNCH_BLOOD_SUGAR("AFTER_LUNCH_BLOOD_SUGAR"),
    
    /**
     * 餐后2h血糖（晚餐）
     */
    @JsonProperty("AFTER_DINNER_BLOOD_SUGAR") AFTER_DINNER_BLOOD_SUGAR("AFTER_DINNER_BLOOD_SUGAR"),
    
    /**
     * 睡前血糖
     */
    @JsonProperty("BEFORE_SLEEP_BLOOD_SUGAR") BEFORE_SLEEP_BLOOD_SUGAR("BEFORE_SLEEP_BLOOD_SUGAR"),
    
    /**
     * 总胆固醇
     */
    @JsonProperty("BLOOD_LIPIDS_TOTAL_CHOLESTEROL") BLOOD_LIPIDS_TOTAL_CHOLESTEROL("BLOOD_LIPIDS_TOTAL_CHOLESTEROL"),
    
    /**
     * 甘油三酯
     */
    @JsonProperty("BLOOD_LIPIDS_TRIGLYCERIDES") BLOOD_LIPIDS_TRIGLYCERIDES("BLOOD_LIPIDS_TRIGLYCERIDES"),
    
    /**
     * 低密度脂蛋白
     */
    @JsonProperty("BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN") BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN("BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN"),
    
    /**
     * 高密度脂蛋白
     */
    @JsonProperty("BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN") BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN("BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN"),
    
    /**
     * 心率
     */
    @JsonProperty("HEART_RATE") HEART_RATE("HEART_RATE"),
    
    /**
     * 脉搏
     */
    @JsonProperty("PULSE") PULSE("PULSE"),
    
    /**
     * 抽烟
     */
    @JsonProperty("SMOKE") SMOKE("SMOKE"),
    
    /**
     * 啤酒
     */
    @JsonProperty("DRINKING_BEER") DRINKING_BEER("DRINKING_BEER"),
    
    /**
     * 白酒
     */
    @JsonProperty("DRINKING_WHITE_SPIRIT") DRINKING_WHITE_SPIRIT("DRINKING_WHITE_SPIRIT"),
    
    /**
     * 红酒
     */
    @JsonProperty("DRINKING_WINE") DRINKING_WINE("DRINKING_WINE"),
    
    /**
     * 黄酒
     */
    @JsonProperty("DRINKING_YELLOW_RICE_SPIRIT") DRINKING_YELLOW_RICE_SPIRIT("DRINKING_YELLOW_RICE_SPIRIT"),
    
    /**
     * 脉搏氧饱和度
     */
    @JsonProperty("PULSE_OXIMETRY") PULSE_OXIMETRY("PULSE_OXIMETRY"),
    
    /**
     * 日饮酒量
     */
    @JsonProperty("DRINKING_DAILY_ALCOHOL") DRINKING_DAILY_ALCOHOL("DRINKING_DAILY_ALCOHOL"),
    
    /**
     * 日吸烟量
     */
    @JsonProperty("SMOKE_DAILY_QUANTITY") SMOKE_DAILY_QUANTITY("SMOKE_DAILY_QUANTITY")
    
}

