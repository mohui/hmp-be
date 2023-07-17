package com.bjknrt.health.scheme.service.health.impl

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.vo.Frequency
import com.bjknrt.health.scheme.vo.FrequencyNumUnit
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.health.scheme.vo.TimeUnit
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HealthManageCerebralStrokeServiceImpl(
    val healthSchemeManageService: HealthSchemeManageService,
    healthPlanClient: HealthPlanApi,
    medicationRemindClient: MedicationRemindApi
) : AbstractHealthManageService(
    healthSchemeManageService,
    healthPlanClient,
    medicationRemindClient
) {
    companion object {
        //脑卒中服务-血压测量计划名称、描述、频次
        const val CEREBRAL_STROKE_BLOOD_PRESSURE_PLAN_NAME = "测血压"
        const val CEREBRAL_STROKE_BLOOD_PRESSURE_MEASURE_DESC = "1周1次"
        val CEREBRAL_STROKE_BLOOD_PRESSURE_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-出院随访计划名称、描述、频次
        const val LEAVE_HOSPITAL_VISIT_PLAN_NAME = "联系医生完成出院随访"
        val LEAVE_HOSPITAL_VISIT_PLAN_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-康复训练计划名称、描述、频次
        const val REHABILITATION_TRAINING_PLAN_NAME = "尽快完成康复训练评估"
        const val REHABILITATION_TRAINING_PLAN_DESC = "1天1次"
        val REHABILITATION_TRAINING_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-脑卒中随访计划名称、描述、频次
        const val CEREBRAL_STROKE_VISIT_MONTH_DAY_FORMAT = "MM.dd"
        const val CEREBRAL_STROKE_VISIT_FREQUENCY_NUM = 1
        const val CEREBRAL_STROKE_VISIT_LAST_DAY_15 = -15L
        const val CEREBRAL_STROKE_VISIT_PLAN_NAME = "完成脑卒中随访"
        val CEREBRAL_STROKE_VISIT_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 15,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-脑卒中行为习惯随访计划名称、描述、频次
        const val CEREBRAL_STROKE_BEHAVIOR_VISIT_MONTH_DAY_FORMAT = "MM.dd"
        const val CEREBRAL_STROKE_BEHAVIOR_VISIT_FREQUENCY_NUM = 1
        const val CEREBRAL_STROKE_BEHAVIOR_VISIT_LAST_DAY_15 = -15L
        const val CEREBRAL_STROKE_BEHAVIOR_VISIT_PLAN_NAME = "尽快完成行为习惯随访"
        val CEREBRAL_STROKE_BEHAVIOR_VISIT_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 15,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-mRS随访计划名称、描述、频次
        const val CEREBRAL_STROKE_MRS_VISIT_MONTH_DAY_FORMAT = "MM.dd"
        const val CEREBRAL_STROKE_MRS_VISIT_FREQUENCY_NUM = 1
        const val CEREBRAL_STROKE_MRS_VISIT_LAST_DAY_15 = -15L
        const val CEREBRAL_STROKE_MRS_VISIT_PLAN_NAME = "完成mRS随访"
        val CEREBRAL_STROKE_MRS_VISIT_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 15,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-Barthel随访计划名称、描述、频次
        const val CEREBRAL_STROKE_BARTHEL_VISIT_MONTH_DAY_FORMAT = "MM.dd"
        const val CEREBRAL_STROKE_BARTHEL_VISIT_FREQUENCY_NUM = 1
        const val CEREBRAL_STROKE_BARTHEL_VISIT_LAST_DAY_15 = -15L
        const val CEREBRAL_STROKE_BARTHEL_VISIT_PLAN_NAME = "完成改良Barthel评估表"
        val CEREBRAL_STROKE_BARTHEL_VISIT_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 15,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //脑卒中服务-EQ-5D随访计划名称、描述、频次
        const val CEREBRAL_STROKE_EQ5D_VISIT_MONTH_DAY_FORMAT = "MM.dd"
        const val CEREBRAL_STROKE_EQ5D_VISIT_FREQUENCY_NUM = 1
        const val CEREBRAL_STROKE_EQ5D_VISIT_LAST_DAY_15 = -15L
        const val CEREBRAL_STROKE_EQ5D_VISIT_PLAN_NAME = "完成EQ-5D评估表"
        val CEREBRAL_STROKE_EQ5D_VISIT_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 15,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )
    }

    override fun saveHealthPlan(healthManage: HsHealthSchemeManagementInfo): List<HsHsmHealthPlan> {
        val healthManageId = healthManage.knId
        val startDateTime = healthManage.knStartDate.atStartOfDay()
        val patientId = healthManage.knPatientId
        //计划集合
        val planList = mutableListOf<HealthPlanDTO>()

        //新建血压测量计划
        this.addBloodPressureMeasurePlan(startDateTime).let {
            planList.addAll(it)
        }

        //新建出院随访计划
        this.addLeaveHospitalVisitPlan(startDateTime.plusDays(-1)).let {
            planList.addAll(it)
        }

        //新建提醒康复训练计划
        this.addRehabilitationTrainingPlan(startDateTime).let {
            planList.addAll(it)
        }

        return this.saveHealthPlanAndRemindPlan(
            patientId = patientId,
            healthManageId = healthManageId,
            healthPlans = planList,
            drugPlans = listOf()
        ) ?: listOf()
    }

    /**
     * 添加血压测量计划
     * @param startDateTime 健康计划开始时间
     */
    fun addBloodPressureMeasurePlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_BLOOD_PRESSURE_PLAN_NAME,
            type = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            desc = CEREBRAL_STROKE_BLOOD_PRESSURE_MEASURE_DESC,
            cycleStartTime = startDateTime,
            cycleEndTime = null,
            frequencys = CEREBRAL_STROKE_BLOOD_PRESSURE_FREQUENCY
        )
        return listOf(params)
    }

    /**
     * 添加出院随访计划
     * @param startDateTime 健康计划开始时间
     */
    fun addLeaveHospitalVisitPlan(
        startDateTime: LocalDateTime?
    ): List<HealthPlanDTO> {
        val params = HealthPlanDTO(
            name = LEAVE_HOSPITAL_VISIT_PLAN_NAME,
            type = HealthPlanType.LEAVE_HOSPITAL_VISIT,
            desc = null,
            cycleStartTime = startDateTime,
            cycleEndTime = null,
            clockDisplay = false,
            frequencys = LEAVE_HOSPITAL_VISIT_PLAN_FREQUENCY
        )
        return listOf(params)
    }

    /**
     * 添加康复训练计划
     * @param startDateTime 计划开始时间
     */
    fun addRehabilitationTrainingPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        val params = HealthPlanDTO(
            name = REHABILITATION_TRAINING_PLAN_NAME,
            type = HealthPlanType.REHABILITATION_TRAINING_REMIND,
            subName = "",
            desc = REHABILITATION_TRAINING_PLAN_DESC,
            cycleStartTime = startDateTime,
            cycleEndTime = null,
            group = null,
            clockDisplay = false,
            frequencys = REHABILITATION_TRAINING_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 创建脑卒中随访计划
     * @param startDate 开始时间
     * @param monthNum 月数
     */
    fun addCerebralStrokeVisitPlan(
        startDate: LocalDate,
        monthNum: Long
    ): List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(CEREBRAL_STROKE_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            CEREBRAL_STROKE_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            CEREBRAL_STROKE_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_VISIT_PLAN_NAME,
            type = HealthPlanType.CEREBRAL_STROKE,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${CEREBRAL_STROKE_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = CEREBRAL_STROKE_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 创建脑卒中行为习惯随访计划
     * @param startDate 开始时间
     * @param monthNum 月数
     */
    fun addCerebralStrokeBehaviorVisitPlan(
        startDate: LocalDate,
        monthNum: Long
    ): List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(CEREBRAL_STROKE_BEHAVIOR_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            CEREBRAL_STROKE_BEHAVIOR_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            CEREBRAL_STROKE_BEHAVIOR_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_BEHAVIOR_VISIT_PLAN_NAME,
            type = HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${CEREBRAL_STROKE_BEHAVIOR_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = CEREBRAL_STROKE_BEHAVIOR_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 创建mRS随访计划
     * @param startDate 开始时间
     * @param monthNum 月数
     */
    fun addMrsVisitPlan(
        startDate: LocalDate,
        monthNum: Long
    ): List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(CEREBRAL_STROKE_MRS_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            CEREBRAL_STROKE_MRS_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            CEREBRAL_STROKE_MRS_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_MRS_VISIT_PLAN_NAME,
            type = HealthPlanType.MRS,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${CEREBRAL_STROKE_MRS_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = CEREBRAL_STROKE_MRS_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 创建Barthel随访计划
     * @param startDate 开始时间
     * @param monthNum 月数
     */
    fun addBarthelVisitPlan(
        startDate: LocalDate,
        monthNum: Long
    ): List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(CEREBRAL_STROKE_BARTHEL_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            CEREBRAL_STROKE_BARTHEL_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            CEREBRAL_STROKE_BARTHEL_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_BARTHEL_VISIT_PLAN_NAME,
            type = HealthPlanType.BARTHEL,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${CEREBRAL_STROKE_BARTHEL_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = CEREBRAL_STROKE_BARTHEL_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 创建Eq5D随访计划
     * @param startDate 开始时间
     * @param monthNum 月数
     */
    fun addEq5DVisitPlan(
        startDate: LocalDate,
        monthNum: Long
    ): List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(CEREBRAL_STROKE_EQ5D_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            CEREBRAL_STROKE_EQ5D_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            CEREBRAL_STROKE_EQ5D_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = CEREBRAL_STROKE_EQ5D_VISIT_PLAN_NAME,
            type = HealthPlanType.EQ_5_D,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${CEREBRAL_STROKE_EQ5D_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = CEREBRAL_STROKE_EQ5D_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * 同时创建所有周期计划的入口方法
     * @param patientId 患者Id
     * @param healthManage 方案
     * @param cycleStartDate 周期开始时间
     * @param monthNum 月数
     */
    fun addCerebralStrokeCyclePlan(
        patientId: Id,
        healthManage: HsHealthSchemeManagementInfo,
        cycleStartDate: LocalDate,
        monthNum: Long
    ): List<HsHsmHealthPlan> {
        val healthPlans = mutableListOf<HealthPlanDTO>()
        this.addCerebralStrokeVisitPlan(cycleStartDate, monthNum)
            .let { planList -> healthPlans.addAll(planList) }
        this.addCerebralStrokeBehaviorVisitPlan(cycleStartDate, monthNum)
            .let { planList -> healthPlans.addAll(planList) }
        this.addMrsVisitPlan(cycleStartDate, monthNum)
            .let { planList -> healthPlans.addAll(planList) }
        this.addBarthelVisitPlan(cycleStartDate, monthNum)
            .let { planList -> healthPlans.addAll(planList) }
        this.addEq5DVisitPlan(cycleStartDate, monthNum)
            .let { planList -> healthPlans.addAll(planList) }

        val cyclePlanList = this.saveHealthPlanAndRemindPlan(
            patientId = patientId,
            healthManageId = healthManage.knId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        ) ?: listOf()
        //发布计划事件
        publishSaveHealthPlanEvent(healthManage, cyclePlanList)
        return cyclePlanList
    }
}
