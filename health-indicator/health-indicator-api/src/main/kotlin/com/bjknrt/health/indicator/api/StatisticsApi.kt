package com.bjknrt.health.indicator.api

import com.bjknrt.health.indicator.vo.BloodLipidsStatistic
import com.bjknrt.health.indicator.vo.BloodPressureStatistic
import com.bjknrt.health.indicator.vo.BloodSugarStatistic
import com.bjknrt.health.indicator.vo.BmiStatistic
import com.bjknrt.health.indicator.vo.DrinkingStatistic
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.indicator.vo.HeartRateStatistic
import com.bjknrt.health.indicator.vo.HeightStatistic
import com.bjknrt.health.indicator.vo.PulseStatistic
import com.bjknrt.health.indicator.vo.SmokeStatistic
import com.bjknrt.health.indicator.vo.StatisticPulseOximetry
import com.bjknrt.health.indicator.vo.TemperatureStatistic
import com.bjknrt.health.indicator.vo.WaistlineStatistic
import com.bjknrt.health.indicator.vo.WeightStatistic
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import kotlin.collections.List
import kotlin.collections.Map

@KatoClient(name = "\${app.hmp-health-indicator.kato-server-name:\${spring.application.name}}", contextId = "StatisticsApi")
@Validated
interface StatisticsApi {


    /**
     * 体质指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BmiStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bMIList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bMIList(@Valid findListParam: FindListParam): List<BmiStatistic>


    /**
     * 血脂指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodLipidsStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bloodLipidsList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodLipidsList(@Valid findListParam: FindListParam): List<BloodLipidsStatistic>


    /**
     * 血压指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodPressureStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bloodPressureList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodPressureList(@Valid findListParam: FindListParam): List<BloodPressureStatistic>


    /**
     * 血糖指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodSugarStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bloodSugarList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodSugarList(@Valid findListParam: FindListParam): List<BloodSugarStatistic>


    /**
     * 身高指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<HeightStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bodyHeightList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyHeightList(@Valid findListParam: FindListParam): List<HeightStatistic>


    /**
     * 体温指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<TemperatureStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bodyTemperatureList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyTemperatureList(@Valid findListParam: FindListParam): List<TemperatureStatistic>


    /**
     * 体重指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<WeightStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/bodyWeightList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyWeightList(@Valid findListParam: FindListParam): List<WeightStatistic>


    /**
     * 日饮酒指标列表（患者端）(FE v1.10)
     * 
     *
     * @param findListParam
     * @return List<DrinkingStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/drinkingList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun drinkingList(@Valid findListParam: FindListParam): List<DrinkingStatistic>


    /**
     * 心率指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<HeartRateStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/heartRateList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun heartRateList(@Valid findListParam: FindListParam): List<HeartRateStatistic>


    /**
     * 脉搏指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<PulseStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/pulseList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulseList(@Valid findListParam: FindListParam): List<PulseStatistic>


    /**
     * 脉搏氧饱和度指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<StatisticPulseOximetry>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/pulseOximetryList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulseOximetryList(@Valid findListParam: FindListParam): List<StatisticPulseOximetry>


    /**
     * 日吸烟指标列表（患者端）(FE v1.10)
     * 
     *
     * @param findListParam
     * @return List<SmokeStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/smokeList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun smokeList(@Valid findListParam: FindListParam): List<SmokeStatistic>


    /**
     * 腰围指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<WaistlineStatistic>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statistics/waistLineList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun waistLineList(@Valid findListParam: FindListParam): List<WaistlineStatistic>
}
