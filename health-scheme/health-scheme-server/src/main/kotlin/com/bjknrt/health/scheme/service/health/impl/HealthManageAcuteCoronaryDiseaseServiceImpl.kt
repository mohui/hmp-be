package com.bjknrt.health.scheme.service.health.impl

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.framework.api.exception.MsgException
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
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HealthManageAcuteCoronaryDiseaseServiceImpl(
    healthSchemeManageService: HealthSchemeManageService,
    healthPlanClient: HealthPlanApi,
    medicationRemindClient: MedicationRemindApi
) : AbstractHealthManageService(
    healthSchemeManageService,
    healthPlanClient,
    medicationRemindClient
) {
    companion object {
        //血压测量计划
        val MANAGEMENT_STAGE_TO_BLOOD_PRESSURE_PLAN_NAME_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "测早晚血压",
            ManageStage.STABLE_STAGE to "测一天内最高血压",
            ManageStage.METAPHASE_STABLE_STAGE to "测一天内最高血压",
            ManageStage.SECULAR_STABLE_STAGE to "测一天内最高血压"
        )
        val BLOOD_PRESSURE_MEASURE_DESC_MSG_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "每天2次",
            ManageStage.STABLE_STAGE to "每天1次",
            ManageStage.METAPHASE_STABLE_STAGE to "一周3天 一天1次",
            ManageStage.SECULAR_STABLE_STAGE to "一周1次",
        )

        //冠心病随访
        const val MONTH_DAY_FORMAT = "MM月dd日"
        const val ACUTE_CORONARY_DISEASE_VISIT_FREQUENCY_NUM = 1
        const val ACUTE_CORONARY_DISEASE_VISIT_IS_STANDARD = 14L
        const val ACUTE_CORONARY_DISEASE_VISIT_NOT_STANDARD = 7L
        const val ACUTE_CORONARY_DISEASE_VISIT_LAST_DAY_2 = -2L
        const val ACUTE_CORONARY_DISEASE_VISIT_LAST_DAY_3 = -3L
        const val ACUTE_CORONARY_DISEASE_VISIT_PLAN_NAME = "完成冠心病随访"
        val ACUTE_CORONARY_DISEASE_VISIT_FREQUENCIES_2_DAYS_1_SEQUENCE = listOf(
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
        val ACUTE_CORONARY_DISEASE_VISIT_FREQUENCIES_3_DAYS_1_SEQUENCE = listOf(
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

        //冠心病行为习惯随访
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_LAST_DAY = -2L
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_NAME = "完成行为习惯随访"
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM = 1
        val ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_FREQUENCIES = listOf(
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
    }

    override fun getEndDate(startDate: LocalDate, manageStage: ManageStage?): LocalDate? {
        return manageStage?.let {
            when (manageStage) {
                ManageStage.INITIAL_STAGE -> startDate.plusDays(SEVEN_DAY)
                ManageStage.STABLE_STAGE -> startDate.plusDays(FOURTEEN_DAY)
                ManageStage.METAPHASE_STABLE_STAGE -> startDate.plusDays(TWENTY_EIGHT_DAY)
                ManageStage.SECULAR_STABLE_STAGE -> startDate.plusDays(EIGHTY_FOUR_DAY)
                else -> startDate.plusDays(SEVEN_DAY)
            }
        }
    }

    override fun getManageStageIsNull(): ManageStage? {
        return ManageStage.INITIAL_STAGE
    }

    override fun saveHealthPlan(healthManage: HsHealthSchemeManagementInfo): List<HsHsmHealthPlan> {
        val endDate = healthManage.knEndDate
            ?: throw MsgException(AppSpringUtil.getMessage("health-manage-scheme.stage-end-date-is-null"))
        val healthPlans = mutableListOf<HealthPlanDTO>()
        val startDate = healthManage.knStartDate.atStartOfDay()
        val managementStage = healthManage.knManagementStage
        val manageId = healthManage.knId
        val patientId = healthManage.knPatientId

        //冠心病血压测量计划
        this.addBloodPressureMeasurePlan(
            startDate,
            endDate.atStartOfDay(),
            managementStage?.let { stage -> ManageStage.valueOf(stage) }
        ).let { bloodPressureList ->
            healthPlans.addAll(bloodPressureList)
        }

        //冠心病随访计划
        this.addAcuteCoronaryDiseaseOnlineVisitPlan(
            false,
            startDate,
            endDate.atStartOfDay()
        )?.let { acuteCoronaryDiseaseOnlineVisitList ->
            healthPlans.addAll(acuteCoronaryDiseaseOnlineVisitList)
        }

        //冠心病行为习惯随访计划
        this.addAcuteCoronaryDiseaseBehaviorVisitPlan(
            endDate.atStartOfDay()
        ).let { behaviorVisitPlanList ->
            healthPlans.addAll(behaviorVisitPlanList)
        }
        return this.addHealthPlan(
            patientId = patientId,
            healthManageId = manageId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )?: listOf()
    }

    /**
     * 新增"填写冠心病的血压测量"的提醒计划
     * 阶段一：7天4天，1天2次
     * 阶段二：14天10天，一天1次
     * 阶段三：4周4周，1周3天，一天1次
     * 阶段四：12周8周，1周1天，一天1次
     *
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param schemeManagementId 健康管理方案Id
     * @param managementStage 当前的阶段
     * @param patientId 患者ID
     */
    fun addBloodPressureMeasurePlan(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        managementStage: ManageStage?,
    ): List<HealthPlanDTO> {
        val param = HealthPlanDTO(
            name = MANAGEMENT_STAGE_TO_BLOOD_PRESSURE_PLAN_NAME_MAP[managementStage] ?: "",
            type = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            desc = BLOOD_PRESSURE_MEASURE_DESC_MSG_MAP[managementStage],
            cycleStartTime = startDateTime,
            cycleEndTime = endDateTime,
            frequencys = BLOOD_PRESSURE_FREQUENCY_MAP[managementStage]
        )
        return listOf(param)
    }

    /**
     * 新增"填写冠心病随访"的提醒计划
     * 血压达标：1次/2周,第12-14天   计划提醒“填写冠心病随访”    频率：0/1
     * 血压不达标：1次/周,第6-7天      计划提醒“填写冠心病随访”    频率：0/1
     *
     * @param isStandard 是否达标
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param schemeManagementId 健康管理方案Id
     * @param patientId 患者ID
     */
    fun addAcuteCoronaryDiseaseOnlineVisitPlan(
        isStandard: Boolean,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HealthPlanDTO>? {
        //计划的周期
        val intervalDay =
            if (isStandard) ACUTE_CORONARY_DISEASE_VISIT_IS_STANDARD else ACUTE_CORONARY_DISEASE_VISIT_NOT_STANDARD
        //计划提醒的时间区间
        val acuteCoronaryDiseaseVisitLastDay =
            if (isStandard) ACUTE_CORONARY_DISEASE_VISIT_LAST_DAY_3 else ACUTE_CORONARY_DISEASE_VISIT_LAST_DAY_2
        //计划提醒的频次
        val hypertensionOnlineVisitFrequencies =
            if (isStandard) ACUTE_CORONARY_DISEASE_VISIT_FREQUENCIES_3_DAYS_1_SEQUENCE else ACUTE_CORONARY_DISEASE_VISIT_FREQUENCIES_2_DAYS_1_SEQUENCE
        //拼接计划提醒的参数
        var tempStartDateTime = startDateTime
        val list = mutableListOf<HealthPlanDTO>()
        do {
            val tempEndDateTime = tempStartDateTime.plusDays(intervalDay)
            tempStartDateTime = tempEndDateTime.plusDays(acuteCoronaryDiseaseVisitLastDay)
            val startFormat = LocalDateTimeUtil.format(
                tempStartDateTime,
                MONTH_DAY_FORMAT
            )
            val endFormat = LocalDateTimeUtil.format(
                tempEndDateTime.plusDays(-1),
                MONTH_DAY_FORMAT
            )
            val param = HealthPlanDTO(
                name = ACUTE_CORONARY_DISEASE_VISIT_PLAN_NAME,
                type = HealthPlanType.ONLINE_ACUTE_CORONARY_DISEASE,
                desc = "${startFormat}-${endFormat},完成${ACUTE_CORONARY_DISEASE_VISIT_FREQUENCY_NUM}次",
                cycleStartTime = tempStartDateTime,
                cycleEndTime = tempEndDateTime,
                frequencys = hypertensionOnlineVisitFrequencies
            )
            tempStartDateTime = tempEndDateTime
            list.add(param)
        } while (tempStartDateTime < endDateTime)
        return list
    }

    /**
     * 新增"冠心病行为习惯随访"的提醒计划
     * 每个阶段结束前2天，计划提醒“完成行为习惯随访”    频率：0/1
     *
     * @param endDateTime 结束时间
     * @param schemeManagementId 健康管理方案Id
     * @param patientId 患者ID
     */
    fun addAcuteCoronaryDiseaseBehaviorVisitPlan(
        endDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        val tempStartDateTime = endDateTime.plusDays(ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_LAST_DAY)
        val startFormat = LocalDateTimeUtil.format(
            tempStartDateTime,
            MONTH_DAY_FORMAT
        )
        val endFormat = LocalDateTimeUtil.format(
            endDateTime.plusDays(-1),
            MONTH_DAY_FORMAT
        )
        val param = HealthPlanDTO(
            name = ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_NAME,
            type = HealthPlanType.ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT,
            desc = "${startFormat}-${endFormat},完成${ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM}次",
            cycleStartTime = tempStartDateTime,
            cycleEndTime = endDateTime,
            frequencys = ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT_PLAN_FREQUENCIES
        )
        return listOf(param)
    }
}
