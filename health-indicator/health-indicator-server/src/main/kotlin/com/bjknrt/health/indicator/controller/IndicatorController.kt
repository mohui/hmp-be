package com.bjknrt.health.indicator.controller

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.*
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.config.IndicatorExecutorConfig.Companion.INDICATOR_ASYNC_TASK_EXECUTOR
import com.bjknrt.health.indicator.dao.IndicatorDao
import com.bjknrt.health.indicator.event.AddIndicatorParam
import com.bjknrt.health.indicator.event.SaveIndicatorEvent
import com.bjknrt.health.indicator.transfer.*
import com.bjknrt.health.indicator.utils.*
import com.bjknrt.health.indicator.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.concurrent.*
import javax.annotation.Resource

@RestController("com.bjknrt.health.indicator.api.IndicatorController")
class IndicatorController(
    val bmiTable: HiBmiTable,
    val bloodLipidsTable: HiBloodLipidsTable,
    val bloodPressureTable: HiBloodPressureTable,
    val bloodSugarTable: HiBloodSugarTable,
    val bodyHeightTable: HiBodyHeightTable,
    val bodyWeightTable: HiBodyWeightTable,
    val drinkingTable: HiDrinkingTable,
    val heartRateTable: HiHeartRateTable,
    val pulseTable: HiPulseTable,
    val smokeTable: HiSmokeTable,
    val waistlineTable: HiWaistlineTable,
    val indicatorDao: IndicatorDao,
    val bodyTemperatureTable: HiBodyTemperatureTable,
    val pulseOximetryTable: HiPulseOximetryTable,
    val eventPublisher: ApplicationEventPublisher
) : AppBaseController(), IndicatorApi {

    @Resource(name = INDICATOR_ASYNC_TASK_EXECUTOR)
    lateinit var executor: ThreadPoolTaskExecutor

    companion object {
        val drinkEnumValueList = listOf(
            IndicatorEnum.DRINKING_BEER.name,
            IndicatorEnum.DRINKING_WHITE_SPIRIT.name,
            IndicatorEnum.DRINKING_WINE.name,
            IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT.name
        )
    }

    @Transactional
    override fun addBMI(addBMIParam: AddBMIParam) {
        val now = LocalDateTime.now()
        val patientId = addBMIParam.knPatientId
        val hiBmi = HiBmi.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnBmi(addBMIParam.knBmi.toDouble())
            .setFromTag(addBMIParam.fromTag.name)
            .setKnMeasureAt(addBMIParam.knMeasureAt ?: now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnCreatedAt(now)
            .build()
        bmiTable.insert(hiBmi)
        //发布保存指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiBmi.knId,
                    knPatientId = patientId,
                    indicatorName = BMI_NAME,
                    knCreatedAt = now,
                    extend1 = addBMIParam.knBmi
                )
            )
        )
    }

    @Transactional
    override fun addBloodLipids(addBloodLipidsParam: AddBloodLipidsParam) {
        leastOneNotNull(
            addBloodLipidsParam.knTotalCholesterol,
            addBloodLipidsParam.knHighDensityLipoprotein,
            addBloodLipidsParam.knLowDensityLipoprotein,
            addBloodLipidsParam.knTriglycerides
        ) {
            val patientId = addBloodLipidsParam.knPatientId
            val now = LocalDateTime.now()
            val hiBloodLipids = HiBloodLipids.builder()
                .setKnId(AppIdUtil.nextId())
                .setKnPatientId(patientId)
                .setFromTag(addBloodLipidsParam.fromTag.name)
                .setKnCreatedAt(now)
                .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setKnMeasureAt(addBloodLipidsParam.knMeasureAt ?: now)
                .setKnTotalCholesterol(addBloodLipidsParam.knTotalCholesterol?.toDouble())
                .setKnTriglycerides(addBloodLipidsParam.knTriglycerides?.toDouble())
                .setKnLowDensityLipoprotein(addBloodLipidsParam.knLowDensityLipoprotein?.toDouble())
                .setKnHighDensityLipoprotein(addBloodLipidsParam.knHighDensityLipoprotein?.toDouble())
                .build()
            bloodLipidsTable.insert(hiBloodLipids)
            //发布保存血脂事件
            eventPublisher.publishEvent(
                SaveIndicatorEvent(
                    this,
                    AddIndicatorParam(
                        knId = hiBloodLipids.knId,
                        knPatientId = patientId,
                        indicatorName = BLOOD_LIPID_NAME,
                        knCreatedAt = now,
                        extend1 = addBloodLipidsParam.knTotalCholesterol,
                        extend2 = addBloodLipidsParam.knTriglycerides,
                        extend3 = addBloodLipidsParam.knLowDensityLipoprotein,
                        extend4 = addBloodLipidsParam.knHighDensityLipoprotein,
                    )
                )
            )
        } ?: throw KatoException(AppSpringUtil.getMessage("bloodLipids.param.not-empty"))
    }

    @Transactional
    override fun addBloodPressure(addBloodPressureParam: AddBloodPressureParam) {
        val now = LocalDateTime.now()
        val patientId = addBloodPressureParam.knPatientId
        val hiBloodPressure = HiBloodPressure.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnSystolicBloodPressure(addBloodPressureParam.knSystolicBloodPressure.toDouble())
            .setKnDiastolicBloodPressure(addBloodPressureParam.knDiastolicBloodPressure.toDouble())
            .setFromTag(addBloodPressureParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addBloodPressureParam.knMeasureAt ?: now)
            .build()
        bloodPressureTable.insert(hiBloodPressure)
        //发布保存血压指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiBloodPressure.knId,
                    knPatientId = patientId,
                    indicatorName = BLOOD_PRESSURE_NAME,
                    knCreatedAt = now,
                    knMeasureAt = hiBloodPressure.knMeasureAt,
                    extend1 = addBloodPressureParam.knSystolicBloodPressure,
                    extend2 = addBloodPressureParam.knDiastolicBloodPressure
                )
            )
        )
    }

    @Transactional
    override fun addBloodSugar(addBloodSugarParam: AddBloodSugarParam) {
        val now = LocalDateTime.now()
        val id = AppIdUtil.nextId()
        val patientId = addBloodSugarParam.knPatientId
        onlyOneNotNull(
            addBloodSugarParam.knFastingBloodSandalwood,
            addBloodSugarParam.knBeforeLunchBloodSugar,
            addBloodSugarParam.knBeforeDinnerBloodSugar,
            addBloodSugarParam.knAfterMealBloodSugar,
            addBloodSugarParam.knAfterLunchBloodSugar,
            addBloodSugarParam.knAfterDinnerBloodSugar,
            addBloodSugarParam.knRandomBloodSugar,
            addBloodSugarParam.knBeforeSleepBloodSugar
        ) {
            val hiBloodSugar =
                HiBloodSugar.builder()
                    .setKnId(id)
                    .setKnPatientId(patientId)
                    .setFromTag(addBloodSugarParam.fromTag.name)
                    .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                    .setKnCreatedAt(now)
                    .setKnFastingBloodSandalwood(addBloodSugarParam.knFastingBloodSandalwood?.toDouble())
                    .setKnBeforeLunchBloodSugar(addBloodSugarParam.knBeforeLunchBloodSugar?.toDouble())
                    .setKnBeforeDinnerBloodSugar(addBloodSugarParam.knBeforeDinnerBloodSugar?.toDouble())
                    .setKnRandomBloodSugar(addBloodSugarParam.knRandomBloodSugar?.toDouble())
                    .setKnAfterMealBloodSugar(addBloodSugarParam.knAfterMealBloodSugar?.toDouble())
                    .setKnAfterLunchBloodSugar(addBloodSugarParam.knAfterLunchBloodSugar?.toDouble())
                    .setKnAfterDinnerBloodSugar(addBloodSugarParam.knAfterDinnerBloodSugar?.toDouble())
                    .setKnBeforeSleepBloodSugar(addBloodSugarParam.knBeforeSleepBloodSugar?.toDouble())
                    .setKnMeasureAt(addBloodSugarParam.knMeasureAt ?: now)
                    .build()
            bloodSugarTable.insert(hiBloodSugar)
            //发布保存血糖指标事件
            eventPublisher.publishEvent(
                SaveIndicatorEvent(
                    this,
                    AddIndicatorParam(
                        knId = hiBloodSugar.knId,
                        knPatientId = patientId,
                        indicatorName = BLOOD_SUGAR_NAME,
                        knCreatedAt = now,
                        extend1 = addBloodSugarParam.knFastingBloodSandalwood,
                        extend2 = addBloodSugarParam.knBeforeLunchBloodSugar,
                        extend3 = addBloodSugarParam.knBeforeDinnerBloodSugar,
                        extend4 = addBloodSugarParam.knAfterMealBloodSugar,
                        extend5 = addBloodSugarParam.knAfterLunchBloodSugar,
                        extend6 = addBloodSugarParam.knAfterDinnerBloodSugar,
                        extend7 = addBloodSugarParam.knRandomBloodSugar,
                        extend8 = addBloodSugarParam.knBeforeSleepBloodSugar
                    )
                )
            )
        } ?: throw KatoException(AppSpringUtil.getMessage("bloodSugar.param.not-correct"))

        //发布保存空腹血糖指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = id,
                    knPatientId = patientId,
                    indicatorName = FASTING_BLOOD_SUGAR_NAME,
                    knCreatedAt = now,
                    knMeasureAt = addBloodSugarParam.knMeasureAt ?: now,
                    extend1 = addBloodSugarParam.knFastingBloodSandalwood
                )
            )
        )

        //发布保存餐前指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = id,
                    knPatientId = patientId,
                    indicatorName = BEFORE_LUNCH_BLOOD_SUGAR_NAME,
                    knCreatedAt = now,
                    knMeasureAt = addBloodSugarParam.knMeasureAt ?: now,
                    extend1 = addBloodSugarParam.knBeforeLunchBloodSugar,
                    extend2 = addBloodSugarParam.knBeforeDinnerBloodSugar
                )
            )
        )

        //发布保存餐后2h指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = id,
                    knPatientId = patientId,
                    indicatorName = AFTER_MEAL_BLOOD_SUGAR_NAME,
                    knCreatedAt = now,
                    knMeasureAt = addBloodSugarParam.knMeasureAt ?: now,
                    extend1 = addBloodSugarParam.knAfterMealBloodSugar,
                    extend2 = addBloodSugarParam.knAfterLunchBloodSugar,
                    extend3 = addBloodSugarParam.knAfterDinnerBloodSugar
                )
            )
        )

    }

    @Transactional
    override fun addBodyHeight(addBodyHeightParam: AddBodyHeightParam) {
        val (patientId, now) = this.onlyAddBodyHeight(addBodyHeightParam)

        //判断是否有最新体重指标，有则计算BMI
        this.selectRecentlyValidIndicatorByType(
            SelectRecentlyValidPatientIndicatorParam
                (
                patientId = patientId,
                indicatorList = listOf(IndicatorEnum.BODY_WEIGHT)
            )
        ).takeIf { it.isNotEmpty() }
            ?.let {
                //计算bmi
                val bmiValue = getBmiValue(addBodyHeightParam.knBodyHeight, it[0]._value)
                //保存bmi
                this.addBMI(
                    AddBMIParam(
                        knPatientId = patientId,
                        knBmi = bmiValue,
                        fromTag = addBodyHeightParam.fromTag,
                        addBodyHeightParam.knMeasureAt ?: now
                    )
                )
            }
    }

    @Transactional
    override fun addBodyWeight(addBodyWeightParam: AddBodyWeightParam) {
        val (patientId, now) = this.onlyAddBodyWeight(addBodyWeightParam)

        //判断是否有最新身高指标，有则计算BMI
        this.selectRecentlyValidIndicatorByType(
            SelectRecentlyValidPatientIndicatorParam
                (
                patientId = patientId,
                indicatorList = listOf(IndicatorEnum.BODY_HEIGHT)
            )
        ).takeIf { it.isNotEmpty() }
            ?.let {
                //计算bmi
                val bmiValue = getBmiValue(it[0]._value, addBodyWeightParam.knBodyWeight)
                //保存bmi
                this.addBMI(
                    AddBMIParam(
                        knPatientId = patientId,
                        knBmi = bmiValue,
                        fromTag = addBodyWeightParam.fromTag,
                        addBodyWeightParam.knMeasureAt ?: now
                    )
                )
            }
    }

    private fun onlyAddBodyHeight(addBodyHeightParam: AddBodyHeightParam): Pair<Id, LocalDateTime> {
        val patientId = addBodyHeightParam.knPatientId
        val now = LocalDateTime.now()
        val hiBodyHeight = HiBodyHeight.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnBodyHeight(addBodyHeightParam.knBodyHeight.toDouble())
            .setFromTag(addBodyHeightParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addBodyHeightParam.knMeasureAt ?: now)
            .build()
        bodyHeightTable.insert(hiBodyHeight)
        return Pair(patientId, now)
    }

    private fun onlyAddBodyWeight(addBodyWeightParam: AddBodyWeightParam): Pair<Id, LocalDateTime> {
        val patientId = addBodyWeightParam.knPatientId
        val now = LocalDateTime.now()
        val hiBodyWeight = HiBodyWeight.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnBodyWeight(addBodyWeightParam.knBodyWeight.toDouble())
            .setFromTag(addBodyWeightParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addBodyWeightParam.knMeasureAt ?: now)
            .build()
        bodyWeightTable.insert(hiBodyWeight)
        return Pair(patientId, now)
    }

    @Transactional
    override fun addDrinking(addDrinkingParam: AddDrinkingParam) {
        val now = LocalDateTime.now()
        val hiDrinking = HiDrinking.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(addDrinkingParam.knPatientId)
            .setKnBeer(addDrinkingParam.knBeer.toDouble())
            .setKnWhiteSpirit(addDrinkingParam.knWhiteSpirit.toDouble())
            .setKnWine(addDrinkingParam.knWine.toDouble())
            .setFromTag(addDrinkingParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addDrinkingParam.knMeasureAt ?: now)
            .setKnYellowRiceSpirit(addDrinkingParam.knYellowRichSpirit.toDouble())
            .setKnTotalAlcohol(
                getTotalDrink(
                    addDrinkingParam.knWhiteSpirit,
                    addDrinkingParam.knBeer,
                    addDrinkingParam.knWine,
                    addDrinkingParam.knYellowRichSpirit
                )
            )
            .build()
        drinkingTable.insert(hiDrinking)
    }

    @Transactional
    override fun addHeartRate(addHeartRateParam: AddHeartRateParam) {
        val now = LocalDateTime.now()
        val patientId = addHeartRateParam.knPatientId
        val hiHeartRate = HiHeartRate.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnHeartRate(addHeartRateParam.knHeartRate)
            .setFromTag(addHeartRateParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addHeartRateParam.knMeasureAt ?: now)
            .build()
        heartRateTable.insert(hiHeartRate)

        //发布保存心率指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiHeartRate.knId,
                    knPatientId = patientId,
                    indicatorName = HEART_RATE_NAME,
                    knCreatedAt = now,
                    extend1 = addHeartRateParam.knHeartRate.toBigDecimal()
                )
            )
        )
    }

    @Transactional
    override fun addPulse(addPulseParam: AddPulseParam) {
        val now = LocalDateTime.now()
        val patientId = addPulseParam.knPatientId
        val hiPulse = HiPulse.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnPulse(addPulseParam.knPulse)
            .setFromTag(addPulseParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addPulseParam.knMeasureAt ?: now)
            .build()
        pulseTable.insert(hiPulse)
        //发布保存脉搏指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiPulse.knId,
                    knPatientId = patientId,
                    indicatorName = PULSE_NAME,
                    knCreatedAt = now,
                    extend1 = hiPulse.knPulse.toBigDecimal(),
                )
            )
        )
    }

    override fun addPulseOximetry(addPulseOximetryParam: AddPulseOximetryParam) {
        val now = LocalDateTime.now()
        val hiPulseOximetry = HiPulseOximetry.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(addPulseOximetryParam.knPatientId)
            .setKnPulseOximetry(addPulseOximetryParam.knPulseOximetry)
            .setFromTag(addPulseOximetryParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addPulseOximetryParam.knMeasureAt ?: now)
            .build()
        pulseOximetryTable.insert(hiPulseOximetry)

        //发布脉搏氧饱和度指标事件
        eventPublisher.publishEvent(SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    hiPulseOximetry.knId,
                    addPulseOximetryParam.knPatientId,
                    indicatorName = PULSE_OXYGEN_NAME,
                    now,
                    knMeasureAt = addPulseOximetryParam.knMeasureAt ?: now,
                    extend1 = addPulseOximetryParam.knPulseOximetry
                )
            )
        )
    }

    @Transactional
    override fun addSmoke(addSmokeParam: AddSmokeParam) {
        val now = LocalDateTime.now()
        val hiSmoke = HiSmoke.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(addSmokeParam.knPatientId)
            .setKnNum(addSmokeParam.knNum)
            .setFromTag(addSmokeParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addSmokeParam.knMeasureAt ?: now)
            .build()
        smokeTable.insert(hiSmoke)
    }

    @Transactional
    override fun addWaistline(addWaistlineParam: AddWaistlineParam) {
        val now = LocalDateTime.now()
        val patientId = addWaistlineParam.knPatientId
        val hiWaistline = HiWaistline.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnWaistline(addWaistlineParam.knWaistline.toDouble())
            .setFromTag(addWaistlineParam.fromTag.name)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnMeasureAt(addWaistlineParam.knMeasureAt ?: now)
            .build()
        //插入腰围指标
        waistlineTable.insert(hiWaistline)
        //发布腰围指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiWaistline.knId,
                    knPatientId = patientId,
                    indicatorName = WAISTLINE_NAME,
                    knCreatedAt = now,
                    extend1 = addWaistlineParam.knWaistline
                )
            )
        )
    }

    @Transactional
    override fun addBodyTemperature(addBodyTemperatureParam: AddBodyTemperatureParam) {
        val now = LocalDateTime.now()
        val patientId = addBodyTemperatureParam.knPatientId
        val hiBodyTemperature = HiBodyTemperature.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPatientId(patientId)
            .setKnCelsius(addBodyTemperatureParam.knCelsius.toDouble())
            .setFromTag(addBodyTemperatureParam.fromTag.name)
            .setKnMeasureAt(addBodyTemperatureParam.knMeasureAt ?: now)
            .setKnCreatedAt(now)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .build()
        bodyTemperatureTable.insert(hiBodyTemperature)

        //发布体温指标事件
        eventPublisher.publishEvent(
            SaveIndicatorEvent(
                this,
                AddIndicatorParam(
                    knId = hiBodyTemperature.knId,
                    knPatientId = patientId,
                    indicatorName = BODY_TEMPERATURE_NAME,
                    knCreatedAt = now,
                    extend1 = addBodyTemperatureParam.knCelsius
                )
            )
        )
    }

    override fun bMIList(findListParam: FindListParam): List<BMIResult> {
        val hiBmiList = bmiTable.select()
            .where(HiBmiTable.KnPatientId eq findListParam.patientId)
            .where(HiBmiTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBmiTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBmiTable.KnMeasureAt, Order.Desc)
            .order(HiBmiTable.KnId, Order.Desc)
            .find()
        return toBMIListResponse(hiBmiList)
    }

    override fun bMIPageList(findPageListParam: FindPageListParam): BMIPageListResult {
        val hiBmiPageList = bmiTable.select()
            .where(HiBmiTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBmiTable.KnMeasureAt, Order.Desc)
            .order(HiBmiTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BMIPageListResult(
            hiBmiPageList.totalPage,
            hiBmiPageList.pageSize,
            hiBmiPageList.pageNo,
            hiBmiPageList.total,
            toBMIListResponse(hiBmiPageList.data)
        )
    }

    @Transactional
    override fun batchAddIndicator(batchIndicator: BatchIndicator) {
        val patientId = batchIndicator.patientId
        val fromTag = batchIndicator.fromTag
        val bmi = batchIndicator.knBmi
        val bodyHeight = batchIndicator.knBodyHeight
        val bodyWeight = batchIndicator.knBodyWeight

        //判断Bmi是否存在
        var isExistBmi = false
        bmi?.let { isExistBmi = true }

        //判断身高和体重是否同时存在
        var isExistHeightAndWeight = false
        if (bodyHeight != null && bodyWeight != null)
            isExistHeightAndWeight = true

        //身高
        try {
            batchIndicator.knBodyHeight?.let {
                val addBodyHeightParam =
                    AddBodyHeightParam(patientId, it, fromTag)
                if (isExistBmi || isExistHeightAndWeight)
                    this.onlyAddBodyHeight(addBodyHeightParam)
                else
                    this.addBodyHeight(addBodyHeightParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步身高指标异常，患者：$patientId", e)
        }
        //体重
        try {
            batchIndicator.knBodyWeight?.let {
                val addBodyWeightParam =
                    AddBodyWeightParam(patientId, it, fromTag)
                if (isExistBmi)
                    this.onlyAddBodyWeight(addBodyWeightParam)
                else
                    this.addBodyWeight(addBodyWeightParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步体重指标异常，患者：$patientId", e)
        }
        //Bmi
        try {
            batchIndicator.knBmi?.let {
                val addBMIParam = AddBMIParam(patientId, it, fromTag)
                this.addBMI(addBMIParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步bmi指标异常，患者：$patientId", e)
        }
        //腰围
        try {
            batchIndicator.knWaistline?.let {
                val addWaistlineParam =
                    AddWaistlineParam(patientId, it, fromTag)
                this.addWaistline(addWaistlineParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步腰围指标异常，患者：$patientId", e)
        }
        //血压
        try {
            val systolicBloodPressure = batchIndicator.knSystolicBloodPressure
            val diastolicBloodPressure = batchIndicator.knDiastolicBloodPressure
            if (systolicBloodPressure != null && diastolicBloodPressure != null) {
                val addBloodPressureParam = AddBloodPressureParam(
                    knPatientId = patientId,
                    knSystolicBloodPressure = systolicBloodPressure,
                    knDiastolicBloodPressure = diastolicBloodPressure,
                    fromTag
                )
                this.addBloodPressure(addBloodPressureParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步血压指标异常，患者：$patientId", e)
        }
        //血糖
        try {
            batchIndicator.knFastingBloodSandalwood?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knFastingBloodSandalwood = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knBeforeLunchBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knBeforeLunchBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knBeforeDinnerBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knBeforeDinnerBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knAfterMealBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knAfterMealBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knAfterLunchBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knAfterLunchBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knAfterDinnerBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knAfterDinnerBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knRandomBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knRandomBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
            batchIndicator.knBeforeSleepBloodSugar?.let {
                val addBloodSugarParam = AddBloodSugarParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knBeforeSleepBloodSugar = it
                )
                this.addBloodSugar(addBloodSugarParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步血糖指标异常，患者：$patientId", e)
        }
        //血脂
        try {
            val totalCholesterol = batchIndicator.knTotalCholesterol
            val triglycerides = batchIndicator.knTriglycerides
            val lowDensityLipoprotein = batchIndicator.knLowDensityLipoprotein
            val highDensityLipoprotein = batchIndicator.knHighDensityLipoprotein
            leastOneNotNull(
                totalCholesterol,
                triglycerides,
                lowDensityLipoprotein,
                highDensityLipoprotein
            ) {
                val addBloodLipidsParam = AddBloodLipidsParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knTotalCholesterol = totalCholesterol,
                    knTriglycerides = triglycerides,
                    knLowDensityLipoprotein = lowDensityLipoprotein,
                    knHighDensityLipoprotein = highDensityLipoprotein
                )
                this.addBloodLipids(addBloodLipidsParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步血脂指标异常，患者：$patientId", e)
        }
        //饮酒
        try {
            val whiteSpirit = batchIndicator.knWhiteSpirit
            val beer = batchIndicator.knBeer
            val wine = batchIndicator.knWine
            val yellowRiceSpirit = batchIndicator.knYellowRiceSpirit
            leastOneNotNull(
                whiteSpirit,
                beer,
                wine,
                yellowRiceSpirit
            ) {
                val addDrinkingParam = AddDrinkingParam(
                    knPatientId = patientId,
                    fromTag = fromTag,
                    knWhiteSpirit = whiteSpirit ?: BigDecimal.ZERO,
                    knBeer = beer ?: BigDecimal.ZERO,
                    knWine = wine ?: BigDecimal.ZERO,
                    knYellowRichSpirit = yellowRiceSpirit ?: BigDecimal.ZERO
                )
                this.addDrinking(addDrinkingParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步饮酒指标异常，患者：$patientId", e)
        }
        //吸烟
        try {
            batchIndicator.knNum?.let {
                val addSmokeParam = AddSmokeParam(patientId, it, fromTag)
                this.addSmoke(addSmokeParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步抽烟指标异常，患者：$patientId", e)
        }
        //脉搏氧饱和度
        try {
            batchIndicator.knPulseOximetry?.let {
                val addPulseOximetryParam =
                    AddPulseOximetryParam(patientId, it, fromTag)
                this.addPulseOximetry(addPulseOximetryParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步脉搏氧饱和度指标异常，患者：$patientId", e)
        }
        //心率
        try {
            batchIndicator.knHeartRate?.let {
                val addHeartRateParam =
                    AddHeartRateParam(patientId, it, fromTag)
                this.addHeartRate(addHeartRateParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步心率指标异常，患者：$patientId", e)
        }
        //脉搏
        try {
            batchIndicator.knPulse?.let {
                val addPulseParam =
                    AddPulseParam(patientId, it, fromTag)
                this.addPulse(addPulseParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步脉搏指标异常，患者：$patientId", e)
        }
        //体温
        try {
            batchIndicator.knCelsius?.let {
                val addBodyTemperatureParam =
                    AddBodyTemperatureParam(patientId, it, fromTag)
                this.addBodyTemperature(addBodyTemperatureParam)
            }
        } catch (e: Exception) {
            LOGGER.warn("批量添加接口同步体温指标异常，患者：$patientId", e)
        }

    }

    override fun bloodLipidsList(findListParam: FindListParam): List<BloodLipidsResult> {
        val hiBloodLipidList = bloodLipidsTable.select()
            .where(HiBloodLipidsTable.KnPatientId eq findListParam.patientId)
            .where(HiBloodLipidsTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBloodLipidsTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
            .order(HiBloodLipidsTable.KnId, Order.Desc)
            .find()
        return toBloodLipidsListResponse(hiBloodLipidList)
    }

    override fun bloodLipidsPageList(findPageListParam: FindPageListParam): BloodLipidsPageListResult {
        val hiBloodLipidPageList = bloodLipidsTable.select()
            .where(HiBloodLipidsTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
            .order(HiBloodLipidsTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BloodLipidsPageListResult(
            hiBloodLipidPageList.totalPage,
            hiBloodLipidPageList.pageSize,
            hiBloodLipidPageList.pageNo,
            hiBloodLipidPageList.total,
            toBloodLipidsListResponse(hiBloodLipidPageList.data)
        )
    }

    override fun bloodPressureList(findListParam: FindListParam): List<BloodPressureResult> {
        val hiBloodPressureList = bloodPressureTable.select()
            .where(HiBloodPressureTable.KnPatientId eq findListParam.patientId)
            .where(HiBloodPressureTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBloodPressureTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBloodPressureTable.KnMeasureAt, Order.Desc)
            .order(HiBloodPressureTable.KnId, Order.Desc)
            .find()
        return toBloodPressureListResponse(hiBloodPressureList)
    }

    override fun bloodPressurePageList(findPageListParam: FindPageListParam): BloodPressurePageListResult {
        val hiBloodPressurePageList = bloodPressureTable.select()
            .where(HiBloodPressureTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBloodPressureTable.KnMeasureAt, Order.Desc)
            .order(HiBloodPressureTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BloodPressurePageListResult(
            hiBloodPressurePageList.total,
            hiBloodPressurePageList.pageSize,
            hiBloodPressurePageList.pageNo,
            hiBloodPressurePageList.total,
            toBloodPressureListResponse(hiBloodPressurePageList.data)
        )
    }

    override fun bloodSugarList(findListParam: FindListParam): List<BloodSugarResult> {
        val hiBloodSugarList = bloodSugarTable.select()
            .where(HiBloodSugarTable.KnPatientId eq findListParam.patientId)
            .where(HiBloodSugarTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBloodSugarTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
            .order(HiBloodSugarTable.KnId, Order.Desc)
            .find()
        return toBloodSugarListResponse(hiBloodSugarList)
    }

    override fun bloodSugarPageList(findPageListParam: FindPageListParam): BloodSugarPageListResult {
        val hiBloodSugarPageList = bloodSugarTable.select()
            .where(HiBloodSugarTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
            .order(HiBloodSugarTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BloodSugarPageListResult(
            hiBloodSugarPageList.totalPage,
            hiBloodSugarPageList.pageSize,
            hiBloodSugarPageList.pageNo,
            hiBloodSugarPageList.total,
            toBloodSugarListResponse(hiBloodSugarPageList.data)
        )
    }

    override fun bodyHeightList(findListParam: FindListParam): List<BodyHeightResult> {
        val hiBodyHeightList = bodyHeightTable.select()
            .where(HiBodyHeightTable.KnPatientId eq findListParam.patientId)
            .where(HiBodyHeightTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBodyHeightTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBodyHeightTable.KnMeasureAt, Order.Desc)
            .order(HiBodyHeightTable.KnId, Order.Desc)
            .find()
        return toBodyHeightListResponse(hiBodyHeightList)
    }

    override fun bodyHeightPageList(findPageListParam: FindPageListParam): BodyHeightPageListResult {
        val hiBodyHeightPageList = bodyHeightTable.select()
            .where(HiBodyHeightTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBodyHeightTable.KnMeasureAt, Order.Desc)
            .order(HiBodyHeightTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BodyHeightPageListResult(
            hiBodyHeightPageList.totalPage,
            hiBodyHeightPageList.pageSize,
            hiBodyHeightPageList.pageNo,
            hiBodyHeightPageList.total,
            toBodyHeightListResponse(hiBodyHeightPageList.data)
        )
    }

    override fun bodyTemperatureList(findListParam: FindListParam): List<BodyTemperatureResult> {
        val hiBodyTemperatureList = bodyTemperatureTable.select()
            .where(HiBodyTemperatureTable.KnPatientId eq findListParam.patientId)
            .where(HiBodyTemperatureTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBodyTemperatureTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBodyTemperatureTable.KnMeasureAt, Order.Desc)
            .order(HiBodyTemperatureTable.KnId, Order.Desc)
            .find()
        return toBodyTemperatureListResponse(hiBodyTemperatureList)
    }

    override fun bodyTemperaturePageList(findPageListParam: FindPageListParam): BodyTemperaturePageListResult {
        val hiBodyTemperaturePageList = bodyTemperatureTable.select()
            .where(HiBodyTemperatureTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBodyTemperatureTable.KnMeasureAt, Order.Desc)
            .order(HiBodyTemperatureTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BodyTemperaturePageListResult(
            hiBodyTemperaturePageList.totalPage,
            hiBodyTemperaturePageList.pageSize,
            hiBodyTemperaturePageList.pageNo,
            hiBodyTemperaturePageList.total,
            toBodyTemperatureListResponse(hiBodyTemperaturePageList.data)
        )
    }

    override fun bodyWeightList(findListParam: FindListParam): List<BodyWeightResult> {
        val hiBodyWeightList = bodyWeightTable.select()
            .where(HiBodyWeightTable.KnPatientId eq findListParam.patientId)
            .where(HiBodyWeightTable.KnMeasureAt gte findListParam.startTime)
            .where(HiBodyWeightTable.KnMeasureAt lt findListParam.endTime)
            .order(HiBodyWeightTable.KnMeasureAt, Order.Desc)
            .order(HiBodyWeightTable.KnId, Order.Desc)
            .find()
        return toBodyWeightListResponse(hiBodyWeightList)
    }

    override fun bodyWeightPageList(findPageListParam: FindPageListParam): BWPageListResult {
        val hiBodyWeightPageList = bodyWeightTable.select()
            .where(HiBodyWeightTable.KnPatientId eq findPageListParam.patientId)
            .order(HiBodyWeightTable.KnMeasureAt, Order.Desc)
            .order(HiBodyWeightTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return BWPageListResult(
            hiBodyWeightPageList.totalPage,
            hiBodyWeightPageList.pageSize,
            hiBodyWeightPageList.pageNo,
            hiBodyWeightPageList.total,
            toBodyWeightListResponse(hiBodyWeightPageList.data)
        )
    }

    override fun drinkingList(findListParam: FindListParam): List<DrinkingResult> {
        val hiDrinkingList = drinkingTable.select()
            .where(HiDrinkingTable.KnPatientId eq findListParam.patientId)
            .where(HiDrinkingTable.KnMeasureAt gte findListParam.startTime)
            .where(HiDrinkingTable.KnMeasureAt lt findListParam.endTime)
            .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
            .order(HiDrinkingTable.KnId, Order.Desc)
            .find()
        return toDrinkingListResponse(hiDrinkingList)
    }

    override fun drinkingPageList(findPageListParam: FindPageListParam): DrinkingPageListResult {
        val hiDrinkingPageList = drinkingTable.select()
            .where(HiDrinkingTable.KnPatientId eq findPageListParam.patientId)
            .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
            .order(HiDrinkingTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return DrinkingPageListResult(
            hiDrinkingPageList.totalPage,
            hiDrinkingPageList.pageSize,
            hiDrinkingPageList.pageNo,
            hiDrinkingPageList.total,
            toDrinkingListResponse(hiDrinkingPageList.data)
        )
    }

    override fun heartRateList(findListParam: FindListParam): List<HeartRateResult> {
        val hiHeartRateList = heartRateTable.select()
            .where(HiHeartRateTable.KnPatientId eq findListParam.patientId)
            .where(HiHeartRateTable.KnMeasureAt gte findListParam.startTime)
            .where(HiHeartRateTable.KnMeasureAt lt findListParam.endTime)
            .order(HiHeartRateTable.KnMeasureAt, Order.Desc)
            .order(HiHeartRateTable.KnId, Order.Desc)
            .find()
        return toHeartRateListResponse(hiHeartRateList)
    }

    override fun heartRatePageList(findPageListParam: FindPageListParam): HeartRatePageListResult {
        val hiHeartRatePageList = heartRateTable.select()
            .where(HiHeartRateTable.KnPatientId eq findPageListParam.patientId)
            .order(HiHeartRateTable.KnMeasureAt, Order.Desc)
            .order(HiHeartRateTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return HeartRatePageListResult(
            hiHeartRatePageList.totalPage,
            hiHeartRatePageList.pageSize,
            hiHeartRatePageList.pageNo,
            hiHeartRatePageList.total,
            toHeartRateListResponse(hiHeartRatePageList.data)
        )
    }

    override fun pulseList(findListParam: FindListParam): List<PulseResult> {
        val hiPulseList = pulseTable.select()
            .where(HiPulseTable.KnPatientId eq findListParam.patientId)
            .where(HiPulseTable.KnMeasureAt gte findListParam.startTime)
            .where(HiPulseTable.KnMeasureAt lt findListParam.endTime)
            .order(HiPulseTable.KnMeasureAt, Order.Desc)
            .order(HiPulseTable.KnId, Order.Desc)
            .find()
        return toPulseListResponse(hiPulseList)
    }

    override fun pulsePageList(findPageListParam: FindPageListParam): PulsePageListResult {
        val hiPulsePageList = pulseTable.select()
            .where(HiPulseTable.KnPatientId eq findPageListParam.patientId)
            .order(HiPulseTable.KnMeasureAt, Order.Desc)
            .order(HiPulseTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return PulsePageListResult(
            hiPulsePageList.totalPage,
            hiPulsePageList.pageSize,
            hiPulsePageList.pageNo,
            hiPulsePageList.total,
            toPulseListResponse(hiPulsePageList.data)
        )
    }

    override fun pulseOximetryList(findListParam: FindListParam): List<PulseOximetryResult> {
        val hiPulseOximetryList = pulseOximetryTable.select()
            .where(HiPulseOximetryTable.KnPatientId eq findListParam.patientId)
            .where(HiPulseOximetryTable.KnMeasureAt gte findListParam.startTime)
            .where(HiPulseOximetryTable.KnMeasureAt lt findListParam.endTime)
            .order(HiPulseOximetryTable.KnMeasureAt, Order.Desc)
            .order(HiPulseOximetryTable.KnId, Order.Desc)
            .find()
        return toPulseOximetryListResponse(hiPulseOximetryList)
    }

    override fun pulseOximetryPageList(findPageListParam: FindPageListParam): PagedResult<PulseOximetryResult> {
        val hiPulseOximetryPagedResult = pulseOximetryTable.select()
            .where(HiPulseOximetryTable.KnPatientId eq findPageListParam.patientId)
            .order(HiPulseOximetryTable.KnMeasureAt, Order.Desc)
            .order(HiPulseOximetryTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return PagedResult.fromDbPaged(hiPulseOximetryPagedResult) {
            PulseOximetryResult(
                knId = it.knId,
                knCreatedAt = it.knCreatedAt,
                knPulseOximetry = it.knPulseOximetry,
                knMeasureAt = it.knMeasureAt
            )
        }
    }

    override fun recentlyValidIndicatorPageListByEnumType(indicatorEnumTypeParam: IndicatorEnumTypeParam): PagedResult<PatientRecentlyValidIndicatorResultInner> {
        return when (indicatorEnumTypeParam.indicatorEnumType) {
            IndicatorEnum.BODY_HEIGHT -> {
                val pagedResult = bodyHeightTable.select()
                    .where(HiBodyHeightTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBodyHeightTable.KnBodyHeight.isNotNull)
                    .order(HiBodyHeightTable.KnMeasureAt, Order.Desc)
                    .order(HiBodyHeightTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BODY_HEIGHT,
                        _value = it.knBodyHeight.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BODY_WEIGHT -> {
                val pagedResult = bodyWeightTable.select()
                    .where(HiBodyWeightTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBodyWeightTable.KnBodyWeight.isNotNull)
                    .order(HiBodyWeightTable.KnMeasureAt, Order.Desc)
                    .order(HiBodyWeightTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BODY_WEIGHT,
                        _value = it.knBodyWeight.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BMI -> {
                val pagedResult = bmiTable.select()
                    .where(HiBmiTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBmiTable.KnBmi.isNotNull)
                    .order(HiBmiTable.KnMeasureAt, Order.Desc)
                    .order(HiBmiTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BMI,
                        _value = it.knBmi.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.WAISTLINE -> {
                val pagedResult = waistlineTable.select()
                    .where(HiWaistlineTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiWaistlineTable.KnWaistline.isNotNull)
                    .order(HiWaistlineTable.KnMeasureAt, Order.Desc)
                    .order(HiWaistlineTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.WAISTLINE,
                        _value = it.knWaistline.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BODY_TEMPERATURE -> {
                val pagedResult = bodyTemperatureTable.select()
                    .where(HiBodyTemperatureTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBodyTemperatureTable.KnCelsius.isNotNull)
                    .order(HiBodyTemperatureTable.KnMeasureAt, Order.Desc)
                    .order(HiBodyTemperatureTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BODY_TEMPERATURE,
                        _value = it.knCelsius.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE -> {
                val pagedResult = bloodPressureTable.select()
                    .where(HiBloodPressureTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodPressureTable.KnSystolicBloodPressure.isNotNull)
                    .order(HiBloodPressureTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodPressureTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE,
                        _value = it.knSystolicBloodPressure.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE -> {
                val pagedResult = bloodPressureTable.select()
                    .where(HiBloodPressureTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodPressureTable.KnDiastolicBloodPressure.isNotNull)
                    .order(HiBloodPressureTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodPressureTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE,
                        _value = it.knDiastolicBloodPressure.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.FASTING_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnFastingBloodSandalwood.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.FASTING_BLOOD_SUGAR,
                        _value = it.knFastingBloodSandalwood?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnBeforeLunchBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR,
                        _value = it.knBeforeLunchBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnBeforeDinnerBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR,
                        _value = it.knBeforeDinnerBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.RANDOM_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnRandomBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.RANDOM_BLOOD_SUGAR,
                        _value = it.knRandomBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnAfterMealBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR,
                        _value = it.knAfterMealBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnAfterLunchBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR,
                        _value = it.knAfterLunchBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnAfterDinnerBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR,
                        _value = it.knAfterDinnerBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR -> {
                val pagedResult = bloodSugarTable.select()
                    .where(HiBloodSugarTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodSugarTable.KnBeforeSleepBloodSugar.isNotNull)
                    .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodSugarTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR,
                        _value = it.knBeforeSleepBloodSugar?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL -> {
                val pagedResult = bloodLipidsTable.select()
                    .where(HiBloodLipidsTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodLipidsTable.KnTotalCholesterol.isNotNull)
                    .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodLipidsTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL,
                        _value = it.knTotalCholesterol?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES -> {
                val pagedResult = bloodLipidsTable.select()
                    .where(HiBloodLipidsTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodLipidsTable.KnTriglycerides.isNotNull)
                    .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodLipidsTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES,
                        _value = it.knTriglycerides?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN -> {
                val pagedResult = bloodLipidsTable.select()
                    .where(HiBloodLipidsTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodLipidsTable.KnLowDensityLipoprotein.isNotNull)
                    .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodLipidsTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN,
                        _value = it.knLowDensityLipoprotein?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN -> {
                val pagedResult = bloodLipidsTable.select()
                    .where(HiBloodLipidsTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiBloodLipidsTable.KnHighDensityLipoprotein.isNotNull)
                    .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
                    .order(HiBloodLipidsTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN,
                        _value = it.knHighDensityLipoprotein?.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.HEART_RATE -> {
                val pagedResult = heartRateTable.select()
                    .where(HiHeartRateTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiHeartRateTable.KnHeartRate.isNotNull)
                    .order(HiHeartRateTable.KnMeasureAt, Order.Desc)
                    .order(HiHeartRateTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.HEART_RATE,
                        _value = it.knHeartRate.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.PULSE -> {
                val pagedResult = pulseTable.select()
                    .where(HiPulseTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiPulseTable.KnPulse.isNotNull)
                    .order(HiPulseTable.KnMeasureAt, Order.Desc)
                    .order(HiPulseTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.PULSE,
                        _value = it.knPulse.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.SMOKE -> {
                val pagedResult = smokeTable.select()
                    .where(HiSmokeTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiSmokeTable.KnNum.isNotNull)
                    .order(HiSmokeTable.KnMeasureAt, Order.Desc)
                    .order(HiSmokeTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.SMOKE,
                        _value = it.knNum.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.DRINKING_BEER -> {
                val pagedResult = drinkingTable.select()
                    .where(HiDrinkingTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiDrinkingTable.KnBeer.isNotNull)
                    .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
                    .order(HiDrinkingTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DRINKING_BEER,
                        _value = it.knBeer.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.DRINKING_WHITE_SPIRIT -> {
                val pagedResult = drinkingTable.select()
                    .where(HiDrinkingTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiDrinkingTable.KnWhiteSpirit.isNotNull)
                    .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
                    .order(HiDrinkingTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DRINKING_WHITE_SPIRIT,
                        _value = it.knWhiteSpirit.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.DRINKING_WINE -> {
                val pagedResult = drinkingTable.select()
                    .where(HiDrinkingTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiDrinkingTable.KnWine.isNotNull)
                    .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
                    .order(HiDrinkingTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DRINKING_WINE,
                        _value = it.knWine.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT -> {
                val pagedResult = drinkingTable.select()
                    .where(HiDrinkingTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiDrinkingTable.KnYellowRiceSpirit.isNotNull)
                    .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
                    .order(HiDrinkingTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT,
                        _value = it.knYellowRiceSpirit.toBigDecimal(),
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }

            IndicatorEnum.PULSE_OXIMETRY -> {
                val pagedResult = pulseOximetryTable.select()
                    .where(HiPulseOximetryTable.KnPatientId eq indicatorEnumTypeParam.patientId)
                    .where(HiPulseOximetryTable.KnPulseOximetry.isNotNull)
                    .order(HiPulseOximetryTable.KnMeasureAt, Order.Desc)
                    .order(HiPulseOximetryTable.KnId, Order.Desc)
                    .page(indicatorEnumTypeParam.pageSize, indicatorEnumTypeParam.pageNo)
                PagedResult.fromDbPaged(pagedResult) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.PULSE_OXIMETRY,
                        _value = it.knPulseOximetry,
                        knCreatedAt = it.knCreatedAt,
                        knMeasureAt = it.knMeasureAt
                    )
                }
            }
            //这里获取的是当天的饮酒量
            IndicatorEnum.DRINKING_DAILY_ALCOHOL -> {
                val beginOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now())
                val endOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(1))
                val dailyDrinking =
                    indicatorDao.findDailyDrinking(beginOfDay, endOfDay, indicatorEnumTypeParam.patientId)

                PagedResult.fromDbPaged(
                    me.danwi.sqlex.core.type.PagedResult(
                        10,
                        1,
                        dailyDrinking.size.toLong(),
                        dailyDrinking
                    )
                ) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.DRINKING_DAILY_ALCOHOL,
                        _value = it.taValue.toBigDecimal(),
                        knCreatedAt = it.measureDate.atStartOfDay(),
                        knMeasureAt = it.measureDate.atStartOfDay()
                    )
                }
            }
            //这里获取的是当天的吸烟量
            IndicatorEnum.SMOKE_DAILY_QUANTITY -> {
                val beginOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now())
                val endOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(1))
                val dailySmoke =
                    indicatorDao.findDailySmoke(beginOfDay, endOfDay, indicatorEnumTypeParam.patientId)
                PagedResult.fromDbPaged(
                    me.danwi.sqlex.core.type.PagedResult(
                        10,
                        1,
                        dailySmoke.size.toLong(),
                        dailySmoke
                    )
                ) {
                    PatientRecentlyValidIndicatorResultInner(
                        key = IndicatorEnum.SMOKE_DAILY_QUANTITY,
                        _value = it.numValue,
                        knCreatedAt = it.measureDate.atStartOfDay(),
                        knMeasureAt = it.measureDate.atStartOfDay()
                    )
                }
            }
        }
    }

    override fun selectAnyIndicatorListForDpm(findListParam: FindListParam): PatientIndicatorListResult {
        val bodyHeightList = bodyHeightList(findListParam)
        val bodyWeightList = bodyWeightList(findListParam)
        val waistLineList = waistLineList(findListParam)
        val bloodSugarList = bloodSugarList(findListParam)
        val bloodPressureList = bloodPressureList(findListParam)
        val bloodLipidsList = bloodLipidsList(findListParam)
        val smokeList = smokeList(findListParam)

        return PatientIndicatorListResult(
            bodyHeightList,
            bodyWeightList,
            waistLineList,
            bloodSugarList,
            bloodPressureList,
            bloodLipidsList,
            smokeList
        )
    }


    override fun selectIndicatorbyPatientId(selectPatientIndicatorParam: SelectPatientIndicatorParam): PatientIndicatorResult {
        val patientId = selectPatientIndicatorParam.patientId
        val bodyHeightFuture: CompletableFuture<HiBodyHeight?> = CompletableFuture.supplyAsync({
            bodyHeightTable.select()
                .where(HiBodyHeightTable.KnPatientId eq patientId)
                .order(HiBodyHeightTable.KnMeasureAt, Order.Desc)
                .order(HiBodyHeightTable.KnId, Order.Desc)
                .findOne()

        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "身高"), it)
        }

        val bodyWeightFuture: CompletableFuture<HiBodyWeight?> = CompletableFuture.supplyAsync({
            bodyWeightTable.select()
                .where(HiBodyWeightTable.KnPatientId eq patientId)
                .order(HiBodyWeightTable.KnMeasureAt, Order.Desc)
                .order(HiBodyWeightTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "体重"), it)
        }

        val bmiFuture: CompletableFuture<HiBmi?> = CompletableFuture.supplyAsync({
            bmiTable.select()
                .where(HiBmiTable.KnPatientId eq patientId)
                .order(HiBmiTable.KnMeasureAt, Order.Desc)
                .order(HiBmiTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "BMI"), it)
        }

        val waistlineFuture: CompletableFuture<HiWaistline?> = CompletableFuture.supplyAsync({
            waistlineTable.select()
                .where(HiWaistlineTable.KnPatientId eq patientId)
                .order(HiWaistlineTable.KnMeasureAt, Order.Desc)
                .order(HiWaistlineTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "腰围"), it)
        }

        val bloodSugarFuture: CompletableFuture<HiBloodSugar?> = CompletableFuture.supplyAsync({
            bloodSugarTable.select()
                .where(HiBloodSugarTable.KnPatientId eq patientId)
                .order(HiBloodSugarTable.KnMeasureAt, Order.Desc)
                .order(HiBloodSugarTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "血糖"), it)
        }

        val bloodPressureFuture: CompletableFuture<HiBloodPressure?> = CompletableFuture.supplyAsync({
            bloodPressureTable.select()
                .where(HiBloodPressureTable.KnPatientId eq patientId)
                .order(HiBloodPressureTable.KnMeasureAt, Order.Desc)
                .order(HiBloodPressureTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "血压"), it)
        }

        val bloodLipidsFuture: CompletableFuture<HiBloodLipids?> = CompletableFuture.supplyAsync({
            bloodLipidsTable.select()
                .where(HiBloodLipidsTable.KnPatientId eq patientId)
                .order(HiBloodLipidsTable.KnMeasureAt, Order.Desc)
                .order(HiBloodLipidsTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "血脂"), it)
        }

        val smokeFuture: CompletableFuture<HiSmoke?> = CompletableFuture.supplyAsync({
            smokeTable.select()
                .where(HiSmokeTable.KnPatientId eq patientId)
                .order(HiSmokeTable.KnMeasureAt, Order.Desc)
                .order(HiSmokeTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "吸烟"), it)
        }

        val drinkFuture: CompletableFuture<HiDrinking?> = CompletableFuture.supplyAsync({
            drinkingTable.select()
                .where(HiDrinkingTable.KnPatientId eq patientId)
                .order(HiDrinkingTable.KnMeasureAt, Order.Desc)
                .order(HiDrinkingTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "饮酒"), it)
        }

        val pulseOximetryFuture: CompletableFuture<HiPulseOximetry?> = CompletableFuture.supplyAsync({
            pulseOximetryTable.select()
                .where(HiPulseOximetryTable.KnPatientId eq patientId)
                .order(HiPulseOximetryTable.KnMeasureAt, Order.Desc)
                .order(HiPulseOximetryTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "脉搏氧饱和度"), it)
        }

        val heartRateFuture: CompletableFuture<HiHeartRate?> = CompletableFuture.supplyAsync({
            heartRateTable.select()
                .where(HiHeartRateTable.KnPatientId eq patientId)
                .order(HiHeartRateTable.KnMeasureAt, Order.Desc)
                .order(HiHeartRateTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "心率"), it)
        }

        val pulseFuture: CompletableFuture<HiPulse?> = CompletableFuture.supplyAsync({
            pulseTable.select()
                .where(HiPulseTable.KnPatientId eq patientId)
                .order(HiPulseTable.KnMeasureAt, Order.Desc)
                .order(HiPulseTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "脉搏"), it)
        }

        val bodyTemperatureFuture: CompletableFuture<HiBodyTemperature?> = CompletableFuture.supplyAsync({
            bodyTemperatureTable.select()
                .where(HiBodyTemperatureTable.KnPatientId eq patientId)
                .order(HiBodyTemperatureTable.KnMeasureAt, Order.Desc)
                .order(HiBodyTemperatureTable.KnId, Order.Desc)
                .findOne()
        }, executor).exceptionally {
            throw KatoException(AppSpringUtil.getMessage("indicator.async.execute.select-exception", "体温"), it)
        }

        //等待上面所有异步线程执行完以后，才会运行allOf回调函数
        val completableFuture: CompletableFuture<PatientIndicatorResult> =
            CompletableFuture.allOf(
                bodyHeightFuture,
                bodyWeightFuture,
                bmiFuture,
                waistlineFuture,
                bloodSugarFuture,
                bloodPressureFuture,
                bloodLipidsFuture,
                smokeFuture,
                drinkFuture,
                pulseOximetryFuture,
                heartRateFuture,
                pulseFuture,
                bodyTemperatureFuture
            ).thenApply {
                val bodyHeight = bodyHeightFuture.get()
                val bodyWeight = bodyWeightFuture.get()
                val bmi = bmiFuture.get()
                val waistline = waistlineFuture.get()
                val bloodSugar = bloodSugarFuture.get()
                val bloodPressure = bloodPressureFuture.get()
                val bloodLipids = bloodLipidsFuture.get()
                val smoke = smokeFuture.get()
                val drink = drinkFuture.get()
                val pulseOximetry = pulseOximetryFuture.get()
                val heartRate = heartRateFuture.get()
                val pulse = pulseFuture.get()
                val bodyTemperature = bodyTemperatureFuture.get()
                PatientIndicatorResult(
                    patientId = patientId,
                    knBodyHeight = bodyHeight?.knBodyHeight?.toBigDecimal(),
                    knBodyWeight = bodyWeight?.knBodyWeight?.toBigDecimal(),
                    knBmi = bmi?.knBmi?.toBigDecimal(),
                    knWaistline = waistline?.knWaistline?.toBigDecimal(),
                    knDiastolicBloodPressure = bloodPressure?.knDiastolicBloodPressure?.toBigDecimal(),
                    knSystolicBloodPressure = bloodPressure?.knSystolicBloodPressure?.toBigDecimal(),
                    knFastingBloodSandalwood = bloodSugar?.knFastingBloodSandalwood?.toBigDecimal(),
                    knBeforeLunchBloodSugar = bloodSugar?.knBeforeLunchBloodSugar?.toBigDecimal(),
                    knBeforeDinnerBloodSugar = bloodSugar?.knBeforeDinnerBloodSugar?.toBigDecimal(),
                    knRandomBloodSugar = bloodSugar?.knRandomBloodSugar?.toBigDecimal(),
                    knAfterMealBloodSugar = bloodSugar?.knAfterMealBloodSugar?.toBigDecimal(),
                    knAfterLunchBloodSugar = bloodSugar?.knAfterLunchBloodSugar?.toBigDecimal(),
                    knAfterDinnerBloodSugar = bloodSugar?.knAfterDinnerBloodSugar?.toBigDecimal(),
                    knBeforeSleepBloodSugar = bloodSugar?.knBeforeSleepBloodSugar?.toBigDecimal(),
                    knTotalCholesterol = bloodLipids?.knTotalCholesterol?.toBigDecimal(),
                    knTriglycerides = bloodLipids?.knTriglycerides?.toBigDecimal(),
                    knHighDensityLipoprotein = bloodLipids?.knHighDensityLipoprotein?.toBigDecimal(),
                    knLowDensityLipoprotein = bloodLipids?.knLowDensityLipoprotein?.toBigDecimal(),
                    knNum = smoke?.knNum ?: 0,
                    knWhiteSpirit = drink?.knWhiteSpirit?.toBigDecimal(),
                    knBeer = drink?.knBeer?.toBigDecimal(),
                    knWine = drink?.knWine?.toBigDecimal(),
                    knYellowRiceSpirit = drink?.knYellowRiceSpirit?.toBigDecimal(),
                    knPulseOximetry = pulseOximetry?.knPulseOximetry,
                    knHeartRate = heartRate?.knHeartRate,
                    knPulse = pulse?.knPulse,
                    knCelsius = bodyTemperature?.knCelsius?.toBigDecimal()
                )
            }
        return completableFuture.get()
    }

    override fun selectLeastIndicatorMeasureAtByPatientId(body: BigInteger): MeasureAt {
        val leastMeasureAt: LocalDateTime? = indicatorDao.selectLeastIndicatorMeasureAtListByPatientId(body)
        return leastMeasureAt?.let {
            MeasureAt(it)
        } ?: MeasureAt()
    }

    override fun selectRecentlyValidIndicatorByType(selectRecentlyValidPatientIndicatorParam: SelectRecentlyValidPatientIndicatorParam): List<PatientRecentlyValidIndicatorResultInner> {

        val indicatorResultInners = mutableListOf<PatientRecentlyValidIndicatorResultInner>()

        var drinking: HiDrinking? = null
        if (CollUtil.containsAny(
                selectRecentlyValidPatientIndicatorParam.indicatorList.map { it.name },
                drinkEnumValueList
            )
        ) {
            drinking = drinkingTable.select()
                .where(HiDrinkingTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                .order(HiDrinkingTable.KnId, Order.Desc)
                .findOne()
        }

        selectRecentlyValidPatientIndicatorParam.indicatorList.forEach {
            when (it.name) {
                IndicatorEnum.BODY_HEIGHT.name -> {
                    bodyHeightTable.select()
                        .where(HiBodyHeightTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiBodyHeightTable.KnId, Order.Desc)
                        .findOne()?.let { bh ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BODY_HEIGHT,
                                _value = bh.knBodyHeight.toBigDecimal(),
                                knCreatedAt = bh.knCreatedAt,
                                knMeasureAt = bh.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BODY_WEIGHT.name -> {
                    bodyWeightTable.select()
                        .where(HiBodyWeightTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiBodyWeightTable.KnId, Order.Desc)
                        .findOne()?.let { bw ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BODY_WEIGHT,
                                _value = bw.knBodyWeight.toBigDecimal(),
                                knCreatedAt = bw.knCreatedAt,
                                knMeasureAt = bw.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BMI.name -> {
                    bmiTable.select()
                        .where(HiBmiTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiBmiTable.KnId, Order.Desc)
                        .findOne()?.let { bmi ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BMI,
                                _value = bmi.knBmi.toBigDecimal(),
                                knCreatedAt = bmi.knCreatedAt,
                                knMeasureAt = bmi.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.WAISTLINE.name -> {
                    waistlineTable.select()
                        .where(HiWaistlineTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiWaistlineTable.KnId, Order.Desc)
                        .findOne()?.let { wl ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.WAISTLINE,
                                _value = wl.knWaistline.toBigDecimal(),
                                knCreatedAt = wl.knCreatedAt,
                                knMeasureAt = wl.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BODY_TEMPERATURE.name -> {
                    bodyTemperatureTable.select()
                        .where(HiBodyTemperatureTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiBodyTemperatureTable.KnId, Order.Desc)
                        .findOne()?.let { bt ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BODY_TEMPERATURE,
                                _value = bt.knCelsius.toBigDecimal(),
                                knCreatedAt = bt.knCreatedAt,
                                knMeasureAt = bt.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE.name -> {
                    bloodPressureTable.select()
                        .where(HiBloodPressureTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodPressureTable.KnSystolicBloodPressure.isNotNull)
                        .order(HiBloodPressureTable.KnId, Order.Desc)
                        .findOne()?.let { sbp ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE,
                                _value = sbp.knSystolicBloodPressure.toBigDecimal(),
                                knCreatedAt = sbp.knCreatedAt,
                                knMeasureAt = sbp.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE.name -> {
                    bloodPressureTable.select()
                        .where(HiBloodPressureTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodPressureTable.KnDiastolicBloodPressure.isNotNull)
                        .order(HiBloodPressureTable.KnId, Order.Desc)
                        .findOne()?.let { dbp ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE,
                                _value = dbp.knDiastolicBloodPressure.toBigDecimal(),
                                knCreatedAt = dbp.knCreatedAt,
                                knMeasureAt = dbp.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.FASTING_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnFastingBloodSandalwood.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { fbs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.FASTING_BLOOD_SUGAR,
                                _value = fbs.knFastingBloodSandalwood?.toBigDecimal(),
                                knCreatedAt = fbs.knCreatedAt,
                                knMeasureAt = fbs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnBeforeLunchBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { blBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR,
                                _value = blBs.knBeforeLunchBloodSugar?.toBigDecimal(),
                                knCreatedAt = blBs.knCreatedAt,
                                knMeasureAt = blBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnBeforeDinnerBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { blBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR,
                                _value = blBs.knBeforeDinnerBloodSugar?.toBigDecimal(),
                                knCreatedAt = blBs.knCreatedAt,
                                knMeasureAt = blBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.RANDOM_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnRandomBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { rbs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.RANDOM_BLOOD_SUGAR,
                                _value = rbs.knRandomBloodSugar?.toBigDecimal(),
                                knCreatedAt = rbs.knCreatedAt,
                                knMeasureAt = rbs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnAfterMealBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { amBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR,
                                _value = amBs.knAfterMealBloodSugar?.toBigDecimal(),
                                knCreatedAt = amBs.knCreatedAt,
                                knMeasureAt = amBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnAfterLunchBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { amBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR,
                                _value = amBs.knAfterLunchBloodSugar?.toBigDecimal(),
                                knCreatedAt = amBs.knCreatedAt,
                                knMeasureAt = amBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnAfterDinnerBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { amBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR,
                                _value = amBs.knAfterDinnerBloodSugar?.toBigDecimal(),
                                knCreatedAt = amBs.knCreatedAt,
                                knMeasureAt = amBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR.name -> {
                    bloodSugarTable.select()
                        .where(HiBloodSugarTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodSugarTable.KnBeforeSleepBloodSugar.isNotNull)
                        .order(HiBloodSugarTable.KnId, Order.Desc)
                        .findOne()?.let { amBs ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR,
                                _value = amBs.knBeforeSleepBloodSugar?.toBigDecimal(),
                                knCreatedAt = amBs.knCreatedAt,
                                knMeasureAt = amBs.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL.name -> {
                    bloodLipidsTable.select()
                        .where(HiBloodLipidsTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodLipidsTable.KnTotalCholesterol.isNotNull)
                        .order(HiBloodLipidsTable.KnId, Order.Desc)
                        .findOne()?.let { tcBl ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL,
                                _value = tcBl.knTotalCholesterol?.toBigDecimal(),
                                knCreatedAt = tcBl.knCreatedAt,
                                knMeasureAt = tcBl.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES.name -> {
                    bloodLipidsTable.select()
                        .where(HiBloodLipidsTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodLipidsTable.KnTriglycerides.isNotNull)
                        .order(HiBloodLipidsTable.KnId, Order.Desc)
                        .findOne()?.let { tbl ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES,
                                _value = tbl.knTriglycerides?.toBigDecimal(),
                                knCreatedAt = tbl.knCreatedAt,
                                knMeasureAt = tbl.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN.name -> {
                    bloodLipidsTable.select()
                        .where(HiBloodLipidsTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodLipidsTable.KnLowDensityLipoprotein.isNotNull)
                        .order(HiBloodLipidsTable.KnId, Order.Desc)
                        .findOne()?.let { ldlBl ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN,
                                _value = ldlBl.knLowDensityLipoprotein?.toBigDecimal(),
                                knCreatedAt = ldlBl.knCreatedAt,
                                knMeasureAt = ldlBl.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN.name -> {
                    bloodLipidsTable.select()
                        .where(HiBloodLipidsTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .where(HiBloodLipidsTable.KnHighDensityLipoprotein.isNotNull)
                        .order(HiBloodLipidsTable.KnId, Order.Desc)
                        .findOne()?.let { hdlBl ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN,
                                _value = hdlBl.knHighDensityLipoprotein?.toBigDecimal(),
                                knCreatedAt = hdlBl.knCreatedAt,
                                knMeasureAt = hdlBl.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.HEART_RATE.name -> {
                    heartRateTable.select()
                        .where(HiHeartRateTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiHeartRateTable.KnId, Order.Desc)
                        .findOne()?.let { hr ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.HEART_RATE,
                                _value = hr.knHeartRate.toBigDecimal(),
                                knCreatedAt = hr.knCreatedAt,
                                knMeasureAt = hr.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.PULSE.name -> {
                    pulseTable.select()
                        .where(HiPulseTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiPulseTable.KnId, Order.Desc)
                        .findOne()?.let { pulse ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.PULSE,
                                _value = pulse.knPulse.toBigDecimal(),
                                knCreatedAt = pulse.knCreatedAt,
                                knMeasureAt = pulse.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.SMOKE.name -> {
                    smokeTable.select()
                        .where(HiSmokeTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiSmokeTable.KnId, Order.Desc)
                        .findOne()?.let { smoke ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.SMOKE,
                                _value = smoke.knNum.toBigDecimal(),
                                knCreatedAt = smoke.knCreatedAt,
                                knMeasureAt = smoke.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.DRINKING_BEER.name -> {
                    drinking?.let { drink ->
                        val inner = PatientRecentlyValidIndicatorResultInner(
                            key = IndicatorEnum.DRINKING_BEER,
                            _value = drink.knBeer.toBigDecimal(),
                            knCreatedAt = drinking.knCreatedAt,
                            knMeasureAt = drink.knMeasureAt
                        )
                        indicatorResultInners.add(inner)
                    }
                }

                IndicatorEnum.DRINKING_WHITE_SPIRIT.name -> {
                    drinking?.let { drink ->
                        val inner = PatientRecentlyValidIndicatorResultInner(
                            key = IndicatorEnum.DRINKING_WHITE_SPIRIT,
                            _value = drink.knWhiteSpirit.toBigDecimal(),
                            knCreatedAt = drink.knCreatedAt,
                            knMeasureAt = drink.knMeasureAt
                        )
                        indicatorResultInners.add(inner)
                    }
                }

                IndicatorEnum.DRINKING_WINE.name -> {
                    drinking?.let { drink ->
                        val inner = PatientRecentlyValidIndicatorResultInner(
                            key = IndicatorEnum.DRINKING_WINE,
                            _value = drink.knWine.toBigDecimal(),
                            knCreatedAt = drink.knCreatedAt,
                            knMeasureAt = drink.knMeasureAt
                        )
                        indicatorResultInners.add(inner)
                    }
                }

                IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT.name -> {
                    drinking?.let { drink ->
                        val inner = PatientRecentlyValidIndicatorResultInner(
                            key = IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT,
                            _value = drink.knYellowRiceSpirit.toBigDecimal(),
                            knCreatedAt = drink.knCreatedAt,
                            knMeasureAt = drink.knMeasureAt
                        )
                        indicatorResultInners.add(inner)
                    }
                }
                //这里获取的是当天的饮酒量
                IndicatorEnum.DRINKING_DAILY_ALCOHOL.name -> {
                    val beginOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now())
                    val endOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(1))
                    indicatorDao.findDailyDrinking(
                        beginOfDay,
                        endOfDay,
                        selectRecentlyValidPatientIndicatorParam.patientId
                    ).takeIf { dailyDrinks -> dailyDrinks.isNotEmpty() }
                        ?.first()
                        ?.let { drink ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.DRINKING_DAILY_ALCOHOL,
                                _value = drink.taValue.toBigDecimal(),
                                knCreatedAt = drink.measureDate.atStartOfDay(),
                                knMeasureAt = drink.measureDate.atStartOfDay()
                            )
                            indicatorResultInners.add(inner)
                        }
                }
                //这里获取的是当天的吸烟量
                IndicatorEnum.SMOKE_DAILY_QUANTITY.name -> {
                    val beginOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now())
                    val endOfDay = LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(1))
                    indicatorDao.findDailySmoke(
                        beginOfDay,
                        endOfDay,
                        selectRecentlyValidPatientIndicatorParam.patientId
                    ).takeIf { dailySmokes -> dailySmokes.isNotEmpty() }
                        ?.first()
                        ?.let { smoke ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.SMOKE_DAILY_QUANTITY,
                                _value = smoke.numValue,
                                knCreatedAt = smoke.measureDate.atStartOfDay(),
                                knMeasureAt = smoke.measureDate.atStartOfDay()
                            )
                            indicatorResultInners.add(inner)
                        }
                }

                IndicatorEnum.PULSE_OXIMETRY.name -> {
                    pulseOximetryTable.select()
                        .where(HiPulseOximetryTable.KnPatientId eq selectRecentlyValidPatientIndicatorParam.patientId.arg)
                        .order(HiPulseOximetryTable.KnId, Order.Desc)
                        .findOne()?.let { pulseOximetry ->
                            val inner = PatientRecentlyValidIndicatorResultInner(
                                key = IndicatorEnum.PULSE_OXIMETRY,
                                _value = pulseOximetry.knPulseOximetry,
                                knCreatedAt = pulseOximetry.knCreatedAt,
                                knMeasureAt = pulseOximetry.knMeasureAt
                            )
                            indicatorResultInners.add(inner)
                        }
                }
            }
        }
        return indicatorResultInners
    }

    override fun smokeList(findListParam: FindListParam): List<SmokeResult> {
        val hiSmokeList = smokeTable.select()
            .where(HiSmokeTable.KnPatientId eq findListParam.patientId)
            .where(HiSmokeTable.KnMeasureAt gte findListParam.startTime)
            .where(HiSmokeTable.KnMeasureAt lt findListParam.endTime)
            .order(HiSmokeTable.KnMeasureAt, Order.Desc)
            .order(HiSmokeTable.KnId, Order.Desc)
            .find()
        return toSmokeListResponse(hiSmokeList)
    }

    override fun smokePageList(findPageListParam: FindPageListParam): SmokePageListResult {
        val hiSmokePageList = smokeTable.select()
            .where(HiSmokeTable.KnPatientId eq findPageListParam.patientId)
            .order(HiSmokeTable.KnMeasureAt, Order.Desc)
            .order(HiSmokeTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return SmokePageListResult(
            hiSmokePageList.totalPage,
            hiSmokePageList.pageSize,
            hiSmokePageList.pageNo,
            hiSmokePageList.total,
            toSmokeListResponse(hiSmokePageList.data)
        )
    }

    override fun waistLineList(findListParam: FindListParam): List<WaistLineResult> {
        val hiWaistlineList = waistlineTable.select()
            .where(HiWaistlineTable.KnPatientId eq findListParam.patientId)
            .where(HiWaistlineTable.KnMeasureAt gte findListParam.startTime)
            .where(HiWaistlineTable.KnMeasureAt lt findListParam.endTime)
            .order(HiWaistlineTable.KnMeasureAt, Order.Desc)
            .order(HiWaistlineTable.KnId, Order.Desc)
            .find()
        return toWaistlineListResponse(hiWaistlineList)
    }

    override fun waistLinePageList(findPageListParam: FindPageListParam): WaistLinePageListResult {
        val hiWaistlinePageList = waistlineTable.select()
            .where(HiWaistlineTable.KnPatientId eq findPageListParam.patientId)
            .order(HiWaistlineTable.KnMeasureAt, Order.Desc)
            .order(HiWaistlineTable.KnId, Order.Desc)
            .page(findPageListParam.pageSize, findPageListParam.pageNo)
        return WaistLinePageListResult(
            hiWaistlinePageList.totalPage,
            hiWaistlinePageList.pageSize,
            hiWaistlinePageList.pageNo,
            hiWaistlinePageList.total,
            toWaistlineListResponse(hiWaistlinePageList.data)
        )
    }
}
