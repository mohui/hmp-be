package com.bjknrt.health.scheme.service.health.impl

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HealthManageDiabetesServiceImpl(
    healthSchemeManageService: HealthSchemeManageService,
    healthPlanClient: HealthPlanApi,
    medicationRemindClient: MedicationRemindApi
) : AbstractHealthManageService(
    healthSchemeManageService,
    healthPlanClient,
    medicationRemindClient
) {

    companion object {

        //糖尿病服务-血糖计划名称Map（糖尿病包）
        const val DIABETES_FASTING_BLOOD_SUGAR_PLAN_NAME = "空腹血糖（早餐前）"
        const val DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_NAME = "餐前血糖（中、晚）"
        const val DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_NAME = "餐后2小时血糖（早、中、晚）"

        //糖尿病服务-空腹血糖计划描述Map（糖尿病包）
        val DIABETES_FASTING_BLOOD_SUGAR_PLAN_DESC_MSG_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "一周2天 1天1次",
            ManageStage.STABLE_STAGE to "一周1天 1天1次",
            ManageStage.METAPHASE_STABLE_STAGE to "一周1天 一天1次"
        )

        //糖尿病服务-餐前血糖计划描述Map（糖尿病包）
        val DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "一周2天 1天1次",
            ManageStage.STABLE_STAGE to "一周1天 1天1次",
            ManageStage.METAPHASE_STABLE_STAGE to "一周1天 1天1次"
        )

        //糖尿病服务-餐后2h血糖计划描述Map（糖尿病包）
        val DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "一周2天 1天1次",
            ManageStage.STABLE_STAGE to "一周2天 1天1次",
            ManageStage.METAPHASE_STABLE_STAGE to "一周1天 一天1次"
        )

        //糖尿病服务-糖尿病随访
        const val DIABETES_MONTH_DAY_FORMAT = "MM月dd日"
        const val DIABETES_VISIT_FREQUENCY_NUM = 1
        const val DIABETES_VISIT_IS_STANDARD = 14L
        const val DIABETES_VISIT_NOT_STANDARD = 7L
        const val DIABETES_VISIT_LAST_DAY_2 = -2L
        const val DIABETES_VISIT_LAST_DAY_3 = -3L
        const val DIABETES_VISIT_PLAN_NAME = "完成糖尿病随访"
        val DIABETES_VISIT_PLAN_FREQUENCIES_2_DAYS_1_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 2,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            ),
            Frequency(
                frequencyTime = 2,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )
        val DIABETES_VISIT_PLAN_FREQUENCIES_3_DAYS_1_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 3,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            ),
            Frequency(
                frequencyTime = 3,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //糖尿病服务-行为习惯随访
        const val DIABETES_BEHAVIOR_VISIT_LAST_DAY = -2L
        const val DIABETES_BEHAVIOR_VISIT_PLAN_NAME = "完成行为习惯随访"
        const val DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM = 1
        val DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 2,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            ),
            Frequency(
                frequencyTime = 2,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //糖尿病服务：阶段和打卡次数的映射map
        val DIABETES_MANAGEMENT_STAGE_TO_CLOCK_NUM_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 1,
            ManageStage.STABLE_STAGE to 2,
            ManageStage.METAPHASE_STABLE_STAGE to 4
        )
    }

    override fun getEndDate(startDate: LocalDate, manageStage: ManageStage?): LocalDate? {
        return manageStage?.let {
            when (manageStage) {
                ManageStage.INITIAL_STAGE -> startDate.plusDays(SEVEN_DAY)
                ManageStage.STABLE_STAGE -> startDate.plusDays(FOURTEEN_DAY)
                ManageStage.METAPHASE_STABLE_STAGE -> startDate.plusDays(TWENTY_EIGHT_DAY)
                else -> startDate.plusDays(SEVEN_DAY)
            }
        }
    }

    override fun getManageStageIsNull(): ManageStage? {
        return ManageStage.INITIAL_STAGE
    }

    override fun saveHealthPlan(
        healthManage: HsHealthSchemeManagementInfo
    ): List<HsHsmHealthPlan> {
        //个性化的健康计划
        val endDate = healthManage.knEndDate
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-end-date-is-null"))
        val healthPlans = mutableListOf<HealthPlanDTO>()
        val startDate = healthManage.knStartDate.atStartOfDay()
        val managementStage = healthManage.knManagementStage
        val manageId = healthManage.knId
        val patientId = healthManage.knPatientId

        //创建血糖计划
        this.addBloodSugarPlan(
            startDate,
            endDate.atStartOfDay(),
            managementStage?.let { stage -> ManageStage.valueOf(stage) }
        )?.let { bloodSugarList ->
            healthPlans.addAll(bloodSugarList)
        }

        //创建糖尿病随访计划
        this.addDiabetesVisitPlan(
            false,
            startDate,
            endDate.atStartOfDay()
        )?.let { diabetesVisitList ->
            healthPlans.addAll(diabetesVisitList)
        }

        //创建行为习惯随访计划
        this.addBehaviorVisitPlan(
            endDate.atStartOfDay()
        )?.let { behaviorVisitList ->
            healthPlans.addAll(behaviorVisitList)
        }
        return this.addHealthPlan(
            patientId = patientId,
            healthManageId =  manageId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )?: listOf()

    }

    fun addBloodSugarPlan(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        managementStage: ManageStage?
    ): List<HealthPlanDTO>? {
        val hsmHealthPlans = mutableListOf<HealthPlanDTO>()
        //添加空腹血糖计划
        this.addFastingBloodSugarPlan(startDate, endDate, managementStage).let {
            hsmHealthPlans.addAll(it)
        }
        //添加餐前血糖计划
        this.addBeforeMealBloodSugarPlan(startDate, endDate, managementStage).let {
            hsmHealthPlans.addAll(it)
        }
        //添加餐后2h血糖计划
        this.addAfterMealBloodSugarPlan(startDate, endDate, managementStage).let {
            hsmHealthPlans.addAll(it)
        }
        return hsmHealthPlans
    }


    fun addFastingBloodSugarPlan(
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        managementStage: ManageStage?
    ): List<HealthPlanDTO> {
        val planName = DIABETES_FASTING_BLOOD_SUGAR_PLAN_NAME
        val healthParams = HealthPlanDTO(
            name = planName,
            type = HealthPlanType.FASTING_BLOOD_GLUCOSE,
            desc = DIABETES_FASTING_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
            cycleStartTime = startDateTime,
            cycleEndTime = endDateTime,
            frequencys = DIABETES_FASTING_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
        )
        return listOf(healthParams)
    }

    fun addBeforeMealBloodSugarPlan(
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        managementStage: ManageStage?
    ): List<HealthPlanDTO> {
        val planName = DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_NAME
        val healthParams = HealthPlanDTO(
            name = planName,
            type = HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE,
            desc = DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
            cycleStartTime = startDateTime,
            cycleEndTime = endDateTime,
            frequencys = DIABETES_BEFORE_LUNCH_OR_DINNER_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
        )
        return listOf(healthParams)
    }

    private fun addAfterMealBloodSugarPlan(
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        managementStage: ManageStage?
    ): List<HealthPlanDTO> {
        val planName = DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_NAME
        val healthParams = HealthPlanDTO(
            name = planName,
            type = HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
            desc = DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
            cycleStartTime = startDateTime,
            cycleEndTime = endDateTime,
            frequencys = DIABETES_AFTER_MEAL_ANY_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
        )
        return listOf(healthParams)
    }

    /**
     * 新增"完成糖尿病随访"的提醒计划
     * 血压达标：1次/2周,第12-14天   计划提醒“完成糖尿病随访”    频率：0/1
     * 血压不达标：1次/周,第6-7天      计划提醒“完成糖尿病随访”    频率：0/1
     *
     * @param isStandard 是否达标
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     */
    fun addDiabetesVisitPlan(
        isStandard: Boolean,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HealthPlanDTO>? {
        //计划的周期
        val intervalDay = if (isStandard) DIABETES_VISIT_IS_STANDARD else DIABETES_VISIT_NOT_STANDARD
        //计划提醒的时间区间
        val diabetesVisitLastDay = if (isStandard) DIABETES_VISIT_LAST_DAY_3 else DIABETES_VISIT_LAST_DAY_2
        //计划提醒的频次
        val diabetesVisitFrequencies =
            if (isStandard) DIABETES_VISIT_PLAN_FREQUENCIES_3_DAYS_1_SEQUENCE else DIABETES_VISIT_PLAN_FREQUENCIES_2_DAYS_1_SEQUENCE
        //拼接计划提醒的参数
        var tempStartDateTime = startDateTime
        val list = mutableListOf<HealthPlanDTO>()
        do {
            val tempEndDateTime = tempStartDateTime.plusDays(intervalDay)
            tempStartDateTime = tempEndDateTime.plusDays(diabetesVisitLastDay)
            val startFormat = LocalDateTimeUtil.format(
                tempStartDateTime,
                DIABETES_MONTH_DAY_FORMAT
            )
            val endFormat = LocalDateTimeUtil.format(
                tempEndDateTime.plusDays(-1),
                DIABETES_MONTH_DAY_FORMAT
            )

            val healthParams = HealthPlanDTO(
                name = DIABETES_VISIT_PLAN_NAME,
                type = HealthPlanType.ONLINE_DIABETES,
                desc = "${startFormat}-${endFormat},完成${DIABETES_VISIT_FREQUENCY_NUM}次",
                cycleStartTime = tempStartDateTime,
                cycleEndTime = tempEndDateTime,
                frequencys = diabetesVisitFrequencies
            )
            tempStartDateTime = tempEndDateTime
            list.add(healthParams)
        } while (tempStartDateTime < endDateTime)
        return list
    }

    /**
     * 新增"完成行为习惯随访"的提醒计划
     * 每个阶段结束前2天，计划提醒“完成行为习惯随访”    频率：0/1
     *
     * @param endDateTime 结束时间
     */
    fun addBehaviorVisitPlan(
        endDateTime: LocalDateTime
    ): List<HealthPlanDTO>? {
        val tempStartDateTime = endDateTime.plusDays(DIABETES_BEHAVIOR_VISIT_LAST_DAY)
        val startFormat = LocalDateTimeUtil.format(
            tempStartDateTime,
            DIABETES_MONTH_DAY_FORMAT
        )
        val endFormat = LocalDateTimeUtil.format(
            endDateTime.plusDays(-1),
            DIABETES_MONTH_DAY_FORMAT
        )
        val healthParams = HealthPlanDTO(
            name = DIABETES_BEHAVIOR_VISIT_PLAN_NAME,
            type = HealthPlanType.DIABETES_BEHAVIOR_VISIT,
            desc = "${startFormat}-${endFormat},完成${DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM}次",
            cycleStartTime = tempStartDateTime,
            cycleEndTime = endDateTime,
            frequencys = DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCIES
        )
        return listOf(healthParams)
    }

}
