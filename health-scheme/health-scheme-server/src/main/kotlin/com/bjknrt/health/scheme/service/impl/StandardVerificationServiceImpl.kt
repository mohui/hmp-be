package com.bjknrt.health.scheme.service.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BloodSugarResult
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.indicator.vo.PatientIndicatorListResult
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.HsHsmHealthPlanTable
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.transfer.buildRemindFrequency
import com.bjknrt.health.scheme.util.*
import com.bjknrt.health.scheme.vo.Frequency
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.health.scheme.vo.ManageStage
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.FrequencyGetClockInParam
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class StandardVerificationServiceImpl(
    val hsHsmHealthPlanTable: HsHsmHealthPlanTable,
    val indicatorClient: IndicatorApi,
    val patientClient: PatientApi,
    val healthPlanClient: HealthPlanApi,
    val clockInRateUtils: ClockInRateUtils
) : StandardVerificationService {
    companion object {

        //糖尿病服务：阶段和打卡次数的映射map
        val DIABETES_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 1,
            ManageStage.STABLE_STAGE to 2,
            ManageStage.METAPHASE_STABLE_STAGE to 4
        )

        //高血压服务：阶段和打卡次数的映射map
        val HYPERTENSION_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 4,
            ManageStage.STABLE_STAGE to 10,
            ManageStage.METAPHASE_STABLE_STAGE to 4,
            ManageStage.SECULAR_STABLE_STAGE to 8
        )

        //冠心病服务：阶段和打卡次数的映射map
        val ACUTE_CORONARY_DISEASE_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 4,
            ManageStage.STABLE_STAGE to 10,
            ManageStage.METAPHASE_STABLE_STAGE to 4,
            ManageStage.SECULAR_STABLE_STAGE to 8
        )

    }

    //血压指标集合平均值是否达标
    override fun isBloodPressureStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        //获取患者信息
        val patientInfo = patientClient.getPatientInfo(patientId)
        val bloodPressureValueList =
            this.getRemoteBloodPressureValueList(manageId, patientId).takeIf { it.isNotEmpty() } ?: return false
        //基于上面的患者信息和BloodPressureValue集合，判断是否达标
        return getBloodPressureAvgStandard(patientInfo.age, bloodPressureValueList)
    }

    //血压指标集合平均值是否达标
    override fun isAcuteCoronaryDiseaseBloodPressureStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        val bloodPressureValueList =
            this.getRemoteBloodPressureValueList(manageId, patientId).takeIf { it.isNotEmpty() } ?: return false
        //根据BloodPressureValue集合，判断是否达标
        return getBloodPressureAvgStandard(bloodPressureValueList)
    }

    private fun getRemoteBloodPressureValueList(
        manageId: BigInteger,
        patientId: BigInteger
    ): List<BloodPressureValue> {
        //查询血压测量计划周期下的远程指标集合
        val bloodPressureList =
            patientPlanIndicatorListResult(manageId, patientId, HealthPlanType.BLOOD_PRESSURE_MEASUREMENT)
                ?: return emptyList()

        //过滤出远程血压指标集合，并转换为BloodPressureValue集合
        return bloodPressureList.bloodPressureListResult.map { bpr ->
            BloodPressureValue(
                bpr.knSystolicBloodPressure,
                bpr.knDiastolicBloodPressure
            )
        }
    }

    override fun lastTimeAllBloodSugarIsStandard(healthManage: HsHealthSchemeManagementInfo): Boolean {
        //判断：“（空腹血糖值＜7.0 mmol/L）” 且 “（餐后2h血糖＜10.0mmol/L或无数据）且（随机血糖＜10.0mmol/L或无数据）”
        //1、过滤出最近一次空腹血糖非空指标数据，判断是否达标
        val fastBloodSugarResultIsStandard =
            lastTimeFastingBloodSugarIsStandard(healthManage.knId, healthManage.knPatientId)

        //如果空腹血糖不达标，直接返回不达标
        if (!fastBloodSugarResultIsStandard) return false

        //2、过滤出最近一次餐后2h血糖或随机血糖至少一个非空的指标数据，根据此规则（餐后2h血糖＜10.0mmol/L或无数据）且（随机血糖＜10.0mmol/L或无数据）”，判断是否达标
        return lastTimeMealTwoHourBloodSugarIsStandard(healthManage.knId, healthManage.knPatientId)
    }

    override fun patientPlanIndicatorListResult(
        manageId: BigInteger,
        patientId: BigInteger,
        planType: HealthPlanType
    ): PatientIndicatorListResult? {
        //获取健康计划
        return hsHsmHealthPlanTable.select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq manageId)
            .where(HsHsmHealthPlanTable.KnPlanType eq planType.name)
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .findOne()?.let { plan ->
                //健康计划开始时间和结束时间段内的指标数据
                plan.knEndDate?.let { endDate ->
                    val findListParam = FindListParam(
                        plan.knStartDate,
                        endDate,
                        patientId
                    )
                    indicatorClient.selectAnyIndicatorListForDpm(findListParam)
                }
            }
    }

    override fun patientPlanBloodSugarIndicatorListResult(
        manageId: BigInteger,
        patientId: BigInteger,
        planType: HealthPlanType
    ): List<BloodSugarResult>? {
        //获取健康计划
        return hsHsmHealthPlanTable.select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq manageId)
            .where(HsHsmHealthPlanTable.KnPlanType eq planType.name)
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .findOne()?.let { plan ->
                //健康计划开始时间和结束时间段内的指标数据
                plan.knEndDate?.let { endDate ->
                    val findListParam = FindListParam(
                        plan.knStartDate,
                        endDate,
                        patientId
                    )
                    indicatorClient.bloodSugarList(findListParam)
                }
            }
    }

    override fun lastTimeFastingBloodSugarIsStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        val indicatorListForDpm =
            patientPlanIndicatorListResult(manageId, patientId, HealthPlanType.FASTING_BLOOD_GLUCOSE)

        //过滤出最近的一次非空的空腹血糖指标数据
        val fastingBloodSandalwood =
            indicatorListForDpm?.bloodSugarListResult
                ?.sortedByDescending { it.knId }
                ?.find { it.knFastingBloodSandalwood != null }
                ?.knFastingBloodSandalwood

        return fastingBloodSandalwood?.let {
            it < BigDecimal.valueOf(7)
        } ?: false
    }

    override fun lastTimeMealTwoHourBloodSugarIsStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        val indicatorListForDpm =
            patientPlanIndicatorListResult(manageId, patientId, HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE)

        //过滤出最近的一次餐后2h血糖/随机血糖至少有一个非空的指标，判断是否达标
        val bloodSugar = indicatorListForDpm?.bloodSugarListResult
            ?.sortedByDescending { it.knId }
            ?.find { it.knAfterMealBloodSugar != null || it.knRandomBloodSugar != null }

        return bloodSugar?.let { it ->
            it.knAfterMealBloodSugar?.let { afterMeal ->
                it.knRandomBloodSugar?.let { random ->
                    //两个都有值（如果两个都有值，必须都满足才达标）
                    afterMeal < BigDecimal.valueOf(10) && random < BigDecimal.valueOf(10)
                } ?: (afterMeal < BigDecimal.valueOf(10)) //只有餐后2h血糖有值

            } ?: it.knRandomBloodSugar?.let { random -> random < BigDecimal.valueOf(10) } //只有随机血糖有值

        } ?: false
    }

    override fun patientManageIndicatorListResult(managementInfo: HsHealthSchemeManagementInfo): PatientIndicatorListResult {
        val startDate = managementInfo.knStartDate
        managementInfo.knEndDate?.let { endDate ->
            val findListParam = FindListParam(
                startDate.atStartOfDay(),
                endDate.atStartOfDay(),
                managementInfo.knPatientId
            )
            return indicatorClient.selectAnyIndicatorListForDpm(findListParam)
        } ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-start-date-is-null"))

    }

    override fun isHypertensionBloodPressureHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean {
        //根据计划获取打卡次数
        val num = clockInRateUtils.getClockNumber(hsmHealthPlan.knForeignPlanId, healthManageStartDate, healthManageEndDate)
        //根据当前阶段数，判断对应打卡次数是否达标
        return HYPERTENSION_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP[manageStage]?.let { num >= it } ?: false
    }

    override fun isDiabetesFastingBloodSugarHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean {
        //根据计划获取打卡次数
        val num = clockInRateUtils.getClockNumber(hsmHealthPlan.knForeignPlanId, healthManageStartDate, healthManageEndDate)
        //根据阶段数，判断对应打卡次数是否达标
        return DIABETES_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP[manageStage]?.let { num >= it } ?: false
    }

    override fun isDiabetesBeforeMealBloodSugarHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean {
        //根据计划获取打卡次数
        val num = clockInRateUtils.getClockNumber(hsmHealthPlan.knForeignPlanId, healthManageStartDate, healthManageEndDate)
        //根据阶段数，判断对应打卡次数是否达标
        return DIABETES_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP[manageStage]?.let { num >= it } ?: false
    }

    override fun isDiabetesAfterMealBloodSugarHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean {
        //根据计划获取打卡次数
        val num = clockInRateUtils.getClockNumber(hsmHealthPlan.knForeignPlanId, healthManageStartDate, healthManageEndDate)
        //根据当前阶段数，判断对应打卡信息是否达标
        return DIABETES_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP[manageStage]?.let { num >= it } ?: false
    }

    override fun isDiabetesBloodSugarValueStandard(manageId: BigInteger, patientId: BigInteger): Boolean {
        //一、空腹血糖计划周期下的血糖指标集合
        val isFastingBloodSugarStandard = isDiabetesFastingBloodSugarStandard(manageId, patientId)

        if (!isFastingBloodSugarStandard) return false

        //二、餐前血糖计划周期下的血糖指标集合
        val isBeforeMealBloodSugarStandard = isDiabetesBeforeMealBloodSugarStandard(manageId, patientId)

        if (!isBeforeMealBloodSugarStandard) return false

        //三、餐后血糖计划周期下的血糖指标值集合
        return isDiabetesAfterMealBloodSugarStandard(manageId, patientId)

    }

    private fun isDiabetesAfterMealBloodSugarStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        return patientPlanBloodSugarIndicatorListResult(
            manageId,
            patientId,
            HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE
        )?.let {
            //1-1、过滤出有效餐后2h血糖（早餐）指标集合
            val afterMealBloodSugarList = it.mapNotNull { sugar -> sugar.knAfterMealBloodSugar }
            //1-2、计算是否达标
            val afterMealSugarValueIsStandard = diabetesBloodSugarValueIsStandard(
                afterMealBloodSugarList,
                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE
            )

            //2、这里不达标直接返回
            if (!afterMealSugarValueIsStandard) return false

            //3-1、过滤出有效餐后2h血糖(午餐)指标集合
            val afterLunchBloodSugarList = it.mapNotNull { sugar -> sugar.knAfterLunchBloodSugar }
            //3-2、计算是否达标
            val afterLunchSugarValueIsStandard = diabetesBloodSugarValueIsStandard(
                afterLunchBloodSugarList,
                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE
            )
            //4、这里不达标直接返回
            if (!afterLunchSugarValueIsStandard) return false

            //5-1、过滤出有效餐后2h血糖(晚餐)指标集合
            val afterDinnerBloodSugarList = it.mapNotNull { sugar -> sugar.knAfterDinnerBloodSugar }
            //5-2、计算是否达标
            return diabetesBloodSugarValueIsStandard(
                afterDinnerBloodSugarList,
                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE
            )
        } ?: false
    }

    private fun isDiabetesBeforeMealBloodSugarStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        return patientPlanBloodSugarIndicatorListResult(
            manageId,
            patientId,
            HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE
        )?.let {
            //1-1、过滤出有效餐前血糖(午餐)指标集合
            val beforeLunchBloodSugarList = it.mapNotNull { sugar -> sugar.knBeforeLunchBloodSugar }
            //1-2、计算是否达标
            val beforeLunchSugarValueIsStandard = diabetesBloodSugarValueIsStandard(
                beforeLunchBloodSugarList,
                HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE
            )

            //3、这里不达标直接返回
            if (!beforeLunchSugarValueIsStandard) return false

            //4-1、过滤出有效餐前血糖(晚餐)指标集合
            val beforeDinnerBloodSugarList = it.mapNotNull { sugar -> sugar.knBeforeDinnerBloodSugar }
            //4-2、计算是否达标
            return diabetesBloodSugarValueIsStandard(
                beforeDinnerBloodSugarList,
                HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE
            )

        } ?: false
    }

    private fun isDiabetesFastingBloodSugarStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        return patientPlanBloodSugarIndicatorListResult(
            manageId,
            patientId,
            HealthPlanType.FASTING_BLOOD_GLUCOSE
        )?.let {
            //过滤出有效的空腹血糖(餐前早餐)指标集合
            val fastingBloodSugarList = it.mapNotNull { sugar -> sugar.knFastingBloodSandalwood }
            //计算是否达标
            return diabetesBloodSugarValueIsStandard(fastingBloodSugarList, HealthPlanType.FASTING_BLOOD_GLUCOSE)
        } ?: false
    }

    private fun diabetesBloodSugarValueIsStandard(
        bloodSugarValueList: List<BigDecimal>,
        planType: HealthPlanType
    ): Boolean {
        //判断所有有效的指标是否全部符合
        return bloodSugarValueList.size == when (planType) {
            HealthPlanType.FASTING_BLOOD_GLUCOSE -> {
                //计算出所有符合条件的空腹血糖（午餐）指标的次数
                bloodSugarValueList.filter { isFastingBloodSugarStandard(it) }.size
            }

            HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE -> {
                //计算出所有符合条件的餐前血糖（午、晚）指标的次数
                bloodSugarValueList.filter { isBloodSugarStandard(it) }.size
            }

            HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE -> {
                //计算出所有符合条件的餐前血糖（午、晚）指标的次数
                bloodSugarValueList.filter { isBloodSugarStandard(it) }.size
            }

            else -> {
                -1
            }
        }
    }

    override fun isAcuteCoronaryDiseaseBloodPressureHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean {
        //根据计划获取打卡次数
        val num = clockInRateUtils.getClockNumber(hsmHealthPlan.knForeignPlanId, healthManageStartDate, healthManageEndDate)
        //根据当前阶段数，判断对应打卡次数是否达标
        return ACUTE_CORONARY_DISEASE_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP[manageStage]?.let { num >= it } ?: false
    }

    override fun smokeNum(indicatorListForDpm: PatientIndicatorListResult): Int {
        val smokeNum = indicatorListForDpm.smokeListResult.map { it.knNum }.fold(0) { acc, i ->
            acc + i
        }
        return smokeNum
    }

    override fun getBloodPressureAvg(bloodPressureList: List<BigDecimal>): BigDecimal {
        if (bloodPressureList.isEmpty()) {
            return BigDecimal.ZERO
        }
        return bloodPressureList.fold(BigDecimal.ZERO) { acc, element -> acc + element }
            .divide(BigDecimal(bloodPressureList.size), RoundingMode.HALF_UP)
    }

    override fun bloodSugarIsStandard(isFirstWeek: Boolean, healthManage: HsHealthSchemeManagementInfo): Boolean {
        return if (!isFirstWeek) {
            //患者最近一次的三种血糖最近一次数值是否达标
            lastTimeAllBloodSugarIsStandard(healthManage)
        } else {
            false
        }
    }

    //最近一次血压指标是否达标
    override fun lastTimeBloodPressureIsStandard(
        isFirstWeek: Boolean,
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean {
        return if (!isFirstWeek) {
            //获取患者信息
            val patientInfo = patientClient.getPatientInfo(patientId)

            //根据患者ID查询此计划周期下的血压指标的集合
            val indicatorListResult =
                patientPlanIndicatorListResult(manageId, patientId, HealthPlanType.BLOOD_PRESSURE_MEASUREMENT)

            return indicatorListResult?.bloodPressureListResult?.firstOrNull()?.let {
                isBloodPressureStandard(
                    patientInfo.age,
                    it.knSystolicBloodPressure.toDouble(),
                    it.knDiastolicBloodPressure.toDouble()
                )
            } ?: false
        } else false
    }

    override fun getRemoteHealthPlanByPlanIdAndFrequency(
        foreignPlanId: BigInteger,
        frequency: Frequency,
        now: LocalDateTime
    ): Int {
        val remindFrequency = buildRemindFrequency(frequency)
        //通过健康计划Id获取获取打卡天数
        val frequencyGetClockInParam = FrequencyGetClockInParam(foreignPlanId, now, remindFrequency)
        return healthPlanClient.frequencyGetClockIn(frequencyGetClockInParam)
    }

    override fun getBloodPressureIndicatorListResult(
        patientId: BigInteger,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<BloodPressure> {
        val param = FindListParam(startDate, endDate, patientId)
        return indicatorClient.bloodPressureList(param).map {
            BloodPressure(
                systolicBloodPressure = it.knSystolicBloodPressure,
                diastolicBloodPressure = it.knDiastolicBloodPressure,
                measureDatetime = it.knMeasureAt
            )
        }
    }
}
