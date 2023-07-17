package com.bjknrt.health.indicator.api

import com.bjknrt.health.indicator.vo.AddBMIParam
import com.bjknrt.health.indicator.vo.AddBloodLipidsParam
import com.bjknrt.health.indicator.vo.AddBloodPressureParam
import com.bjknrt.health.indicator.vo.AddBloodSugarParam
import com.bjknrt.health.indicator.vo.AddBodyHeightParam
import com.bjknrt.health.indicator.vo.AddBodyTemperatureParam
import com.bjknrt.health.indicator.vo.AddBodyWeightParam
import com.bjknrt.health.indicator.vo.AddDrinkingParam
import com.bjknrt.health.indicator.vo.AddHeartRateParam
import com.bjknrt.health.indicator.vo.AddPulseOximetryParam
import com.bjknrt.health.indicator.vo.AddPulseParam
import com.bjknrt.health.indicator.vo.AddSmokeParam
import com.bjknrt.health.indicator.vo.AddWaistlineParam
import com.bjknrt.health.indicator.vo.BMIPageListResult
import com.bjknrt.health.indicator.vo.BMIResult
import com.bjknrt.health.indicator.vo.BWPageListResult
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.BloodLipidsPageListResult
import com.bjknrt.health.indicator.vo.BloodLipidsResult
import com.bjknrt.health.indicator.vo.BloodPressurePageListResult
import com.bjknrt.health.indicator.vo.BloodPressureResult
import com.bjknrt.health.indicator.vo.BloodSugarPageListResult
import com.bjknrt.health.indicator.vo.BloodSugarResult
import com.bjknrt.health.indicator.vo.BodyHeightPageListResult
import com.bjknrt.health.indicator.vo.BodyHeightResult
import com.bjknrt.health.indicator.vo.BodyTemperaturePageListResult
import com.bjknrt.health.indicator.vo.BodyTemperatureResult
import com.bjknrt.health.indicator.vo.BodyWeightResult
import com.bjknrt.health.indicator.vo.DrinkingPageListResult
import com.bjknrt.health.indicator.vo.DrinkingResult
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.indicator.vo.FindPageListParam
import com.bjknrt.health.indicator.vo.HeartRatePageListResult
import com.bjknrt.health.indicator.vo.HeartRateResult
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.indicator.vo.IndicatorEnumTypeParam
import com.bjknrt.health.indicator.vo.MeasureAt
import com.bjknrt.health.indicator.vo.PatientIndicatorListResult
import com.bjknrt.health.indicator.vo.PatientIndicatorResult
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.health.indicator.vo.PatientRecentlyValidIndicatorResultInner
import com.bjknrt.health.indicator.vo.PulseOximetryResult
import com.bjknrt.health.indicator.vo.PulsePageListResult
import com.bjknrt.health.indicator.vo.PulseResult
import com.bjknrt.health.indicator.vo.SelectPatientIndicatorParam
import com.bjknrt.health.indicator.vo.SelectRecentlyValidPatientIndicatorParam
import com.bjknrt.health.indicator.vo.SmokePageListResult
import com.bjknrt.health.indicator.vo.SmokeResult
import com.bjknrt.health.indicator.vo.WaistLinePageListResult
import com.bjknrt.health.indicator.vo.WaistLineResult

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import me.danwi.kato.client.KatoClient
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

@KatoClient(name = "\${app.hmp-health-indicator.kato-server-name:\${spring.application.name}}", contextId = "IndicatorApi")
@Validated
interface IndicatorApi {


    /**
     * 添加体质指标
     * 
     *
     * @param addBMIParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBMI"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBMI(@Valid addBMIParam: AddBMIParam): Unit


    /**
     * 添加血脂指标
     * 
     *
     * @param addBloodLipidsParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBloodLipids"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBloodLipids(@Valid addBloodLipidsParam: AddBloodLipidsParam): Unit


    /**
     * 添加血压指标
     * 
     *
     * @param addBloodPressureParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBloodPressure"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBloodPressure(@Valid addBloodPressureParam: AddBloodPressureParam): Unit


    /**
     * 添加血糖指标
     * 
     *
     * @param addBloodSugarParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBloodSugar"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBloodSugar(@Valid addBloodSugarParam: AddBloodSugarParam): Unit


    /**
     * 添加身高指标
     * 
     *
     * @param addBodyHeightParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBodyHeight"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBodyHeight(@Valid addBodyHeightParam: AddBodyHeightParam): Unit


    /**
     * 添加体温指标
     * 
     *
     * @param addBodyTemperatureParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBodyTemperature"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBodyTemperature(@Valid addBodyTemperatureParam: AddBodyTemperatureParam): Unit


    /**
     * 添加体重指标
     * 
     *
     * @param addBodyWeightParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addBodyWeight"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBodyWeight(@Valid addBodyWeightParam: AddBodyWeightParam): Unit


    /**
     * 添加饮酒指标
     * 
     *
     * @param addDrinkingParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addDrinking"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addDrinking(@Valid addDrinkingParam: AddDrinkingParam): Unit


    /**
     * 添加心率指标
     * 
     *
     * @param addHeartRateParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addHeartRate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addHeartRate(@Valid addHeartRateParam: AddHeartRateParam): Unit


    /**
     * 添加脉搏指标
     * 
     *
     * @param addPulseParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addPulse"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addPulse(@Valid addPulseParam: AddPulseParam): Unit


    /**
     * 添加脉搏氧饱和度指标
     * 
     *
     * @param addPulseOximetryParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addPulseOximetry"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addPulseOximetry(@Valid addPulseOximetryParam: AddPulseOximetryParam): Unit


    /**
     * 添加吸烟指标
     * 
     *
     * @param addSmokeParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addSmoke"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addSmoke(@Valid addSmokeParam: AddSmokeParam): Unit


    /**
     * 添加腰围指标
     * 
     *
     * @param addWaistlineParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/addWaistline"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addWaistline(@Valid addWaistlineParam: AddWaistlineParam): Unit


    /**
     * 体质指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BMIResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bMIList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bMIList(@Valid findListParam: FindListParam): List<BMIResult>


    /**
     * 体质指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BMIPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bMIPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bMIPageList(@Valid findPageListParam: FindPageListParam): BMIPageListResult


    /**
     * 新增多个指标数据（医生端）
     * 
     *
     * @param batchIndicator
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/batchAddIndicator"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchAddIndicator(@Valid batchIndicator: BatchIndicator): Unit


    /**
     * 血脂指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodLipidsResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodLipidsList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodLipidsList(@Valid findListParam: FindListParam): List<BloodLipidsResult>


    /**
     * 血脂指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BloodLipidsPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodLipidsPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodLipidsPageList(@Valid findPageListParam: FindPageListParam): BloodLipidsPageListResult


    /**
     * 血压指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodPressureResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodPressureList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodPressureList(@Valid findListParam: FindListParam): List<BloodPressureResult>


    /**
     * 血压指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BloodPressurePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodPressurePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodPressurePageList(@Valid findPageListParam: FindPageListParam): BloodPressurePageListResult


    /**
     * 血糖指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BloodSugarResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodSugarList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodSugarList(@Valid findListParam: FindListParam): List<BloodSugarResult>


    /**
     * 血糖指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BloodSugarPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bloodSugarPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bloodSugarPageList(@Valid findPageListParam: FindPageListParam): BloodSugarPageListResult


    /**
     * 身高指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BodyHeightResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyHeightList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyHeightList(@Valid findListParam: FindListParam): List<BodyHeightResult>


    /**
     * 身高指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BodyHeightPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyHeightPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyHeightPageList(@Valid findPageListParam: FindPageListParam): BodyHeightPageListResult


    /**
     * 体温指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BodyTemperatureResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyTemperatureList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyTemperatureList(@Valid findListParam: FindListParam): List<BodyTemperatureResult>


    /**
     * 体温指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BodyTemperaturePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyTemperaturePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyTemperaturePageList(@Valid findPageListParam: FindPageListParam): BodyTemperaturePageListResult


    /**
     * 体重指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<BodyWeightResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyWeightList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyWeightList(@Valid findListParam: FindListParam): List<BodyWeightResult>


    /**
     * 体重指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return BWPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/bodyWeightPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bodyWeightPageList(@Valid findPageListParam: FindPageListParam): BWPageListResult


    /**
     * 饮酒指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<DrinkingResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/drinkingList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun drinkingList(@Valid findListParam: FindListParam): List<DrinkingResult>


    /**
     * 饮酒指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return DrinkingPageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/drinkingPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun drinkingPageList(@Valid findPageListParam: FindPageListParam): DrinkingPageListResult


    /**
     * 心率指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<HeartRateResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/heartRateList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun heartRateList(@Valid findListParam: FindListParam): List<HeartRateResult>


    /**
     * 心率指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return HeartRatePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/heartRatePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun heartRatePageList(@Valid findPageListParam: FindPageListParam): HeartRatePageListResult


    /**
     * 脉搏指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<PulseResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/pulseList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulseList(@Valid findListParam: FindListParam): List<PulseResult>


    /**
     * 脉搏氧饱和度指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<PulseOximetryResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/pulseOximetryList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulseOximetryList(@Valid findListParam: FindListParam): List<PulseOximetryResult>


    /**
     * 脉搏氧饱和度指标最新一页（医生端）
     * 
     *
     * @param findPageListParam
     * @return PagedResult<PulseOximetryResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/pulseOximetryPageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulseOximetryPageList(@Valid findPageListParam: FindPageListParam): PagedResult<PulseOximetryResult>


    /**
     * 脉搏指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return PulsePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/pulsePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pulsePageList(@Valid findPageListParam: FindPageListParam): PulsePageListResult


    /**
     * 根据患者ID和指标类型枚举，对患者特定指标分页查询(FE v1.7)
     * 
     *
     * @param indicatorEnumTypeParam
     * @return PagedResult<PatientRecentlyValidIndicatorResultInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/recentlyValidIndicatorPageListByEnumType"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun recentlyValidIndicatorPageListByEnumType(@Valid indicatorEnumTypeParam: IndicatorEnumTypeParam): PagedResult<PatientRecentlyValidIndicatorResultInner>


    /**
     * 根据患者ID查询多个指标数据的集合
     * 
     *
     * @param findListParam
     * @return PatientIndicatorListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/selectAnyIndicatorListForDpm"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun selectAnyIndicatorListForDpm(@Valid findListParam: FindListParam): PatientIndicatorListResult


    /**
     * 根据患者ID查询多个指标数据
     * 
     *
     * @param selectPatientIndicatorParam
     * @return PatientIndicatorResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/selectIndicatorByPatientId"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun selectIndicatorbyPatientId(@Valid selectPatientIndicatorParam: SelectPatientIndicatorParam): PatientIndicatorResult


    /**
     * 查询患者所有指标最后一次的测量时间(FE v1.6)
     * 
     *
     * @param body
     * @return MeasureAt
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/selectLeastIndicatorMeasureAtListByPatientId"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun selectLeastIndicatorMeasureAtByPatientId(@Valid body: java.math.BigInteger): MeasureAt


    /**
     * 查询此患者指定指标的最近一次有效数据
     * 
     *
     * @param selectRecentlyValidPatientIndicatorParam
     * @return List<PatientRecentlyValidIndicatorResultInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/selectRecentlyValidIndicatorByType"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun selectRecentlyValidIndicatorByType(@Valid selectRecentlyValidPatientIndicatorParam: SelectRecentlyValidPatientIndicatorParam): List<PatientRecentlyValidIndicatorResultInner>


    /**
     * 吸烟指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<SmokeResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/smokeList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun smokeList(@Valid findListParam: FindListParam): List<SmokeResult>


    /**
     * 吸烟指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return SmokePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/smokePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun smokePageList(@Valid findPageListParam: FindPageListParam): SmokePageListResult


    /**
     * 腰围指标列表（患者端）
     * 
     *
     * @param findListParam
     * @return List<WaistLineResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/waistLineList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun waistLineList(@Valid findListParam: FindListParam): List<WaistLineResult>


    /**
     * 腰围指标最新一页（医生端） 
     * 
     *
     * @param findPageListParam
     * @return WaistLinePageListResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/indicator/waistLinePageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun waistLinePageList(@Valid findPageListParam: FindPageListParam): WaistLinePageListResult
}
