package com.bjknrt.health.scheme.service.health.impl

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.constant.COPD_INTERVAL_MONTH
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
class HealthManageCopdServiceImpl(
    healthSchemeManageService: HealthSchemeManageService,
    healthPlanClient: HealthPlanApi,
    medicationRemindClient: MedicationRemindApi
) : AbstractHealthManageService(
    healthSchemeManageService,
    healthPlanClient,
    medicationRemindClient
) {
    companion object {
        //慢阻肺服务-血压测量计划名称、描述、频次
        const val COPD_BLOOD_PRESSURE_PLAN_NAME = "测量血压"
        const val COPD_BLOOD_PRESSURE_MEASURE_DESC = "每月完成1次"
        val COPD_BLOOD_PRESSURE_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-接种流感疫苗计划名称、描述、频次
        const val COPD_INFLUENZA_VACCINATION_PLAN_DESC = "每年完成1次"
        const val COPD_INFLUENZA_VACCINATION_PLAN_NAME = "完成流感疫苗接种"
        val COPD_INFLUENZA_VACCINATION_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-接种肺炎球菌疫苗计划名称、描述、频次
        const val COPD_PNEUMOCOCCAL_VACCINATION_PLAN_DESC = "每5年完成1次"
        const val COPD_PNEUMOCOCCAL_VACCINATION_PLAN_NAME = "完成肺炎球菌疫苗接种"
        val COPD_PNEUMOCOCCAL_VACCINATION_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 5,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-脉搏氧饱和度计划名称、描述、频次
        const val COPD_PULSE_OXYGEN_SATURATION_PLAN_DESC = "每周完成1次"
        const val COPD_PULSE_OXYGEN_SATURATION_PLAN_NAME = "测量脉搏氧饱和度"
        val COPD_PULSE_OXYGEN_SATURATION_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-肺功能检查计划名称、描述、频次
        const val COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_DESC = "每上、下半年各完成1次"
        const val COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_NAME = "完成肺功能检查"
        val COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 6,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-呼吸科体检计划名称、描述、频次
        const val COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_DESC = "每年完成1次"
        const val COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_NAME = "前去呼吸科进行体检"
        val COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-Zung氏焦虑自评量表计划名称、描述、频次
        const val COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_DESC = "每年完成1次"
        const val COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_NAME = "完成Zung氏焦虑自评量表"
        val COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-Zung氏抑郁自评量表计划名称、描述、频次
        const val COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_DESC = "每年完成1次"
        const val COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_NAME = "完成Zung氏抑郁自评量表"
        val COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-慢阻肺行为习惯随访计划名称、描述、频次
        const val COPD_BEHAVIOR_VISIT_PLAN_DESC = "每月完成1次"
        const val COPD_BEHAVIOR_VISIT_PLAN_NAME = "完成慢阻肺行为习惯随访"
        val COPD_BEHAVIOR_VISIT_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        //慢阻肺服务-线上慢阻肺随访计划名称、描述、频次
        const val COPD_ONLINE_VISIT_PLAN_DESC = "每月完成1次"
        const val COPD_ONLINE_VISIT_PLAN_NAME = "完成慢阻肺随访"
        val COPD_ONLINE_VISIT_PLAN_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )
    }

    override fun getReportDate(startDate: LocalDate, endDate: LocalDate?): LocalDate? {
        return startDate.plusMonths(COPD_INTERVAL_MONTH)
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

        //添加慢阻肺行为习惯随访计划
        this.addCopdBehaviorVisitPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加线上慢阻肺随访计划
        this.addOnlineCopdVisitPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加接种流感疫苗计划
        this.addInfluenzaVaccinationPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加接种肺炎球菌疫苗计划
        this.addPneumococcalVaccinationPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加脉搏氧饱和度计划
        this.addPulseOxygenSaturationPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加肺功能检查计划
        this.addPulmonaryFunctionExaminationPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加呼吸科体检计划
        this.addRespiratoryDepartmentExaminationPlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加Zung氏焦虑自评量表计划
        this.addZungSelfRatingAnxietyScalePlan(startDateTime).let {
            planList.addAll(it)
        }

        //添加Zung氏抑郁自评量表计划
        this.addZungSelfRatingDepressionScalePlan(startDateTime).let {
            planList.addAll(it)
        }

        // 调用根据type批量添加健康计划, 保存关联关系
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
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = startDateTime,
            healthPlanName = COPD_BLOOD_PRESSURE_PLAN_NAME,
            healthPlanType = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            healthPlanDesc = COPD_BLOOD_PRESSURE_MEASURE_DESC,
            frequencies = COPD_BLOOD_PRESSURE_FREQUENCY,
            displayTime = startDateTime
        )
    }

    /**
     * 添加慢阻肺行为习惯随访计划
     * @param startDateTime 健康计划开始时间
     */
    fun addCopdBehaviorVisitPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = startDateTime,
            healthPlanName = COPD_BEHAVIOR_VISIT_PLAN_NAME,
            healthPlanType = HealthPlanType.COPD_BEHAVIOR_VISIT,
            healthPlanDesc = COPD_BEHAVIOR_VISIT_PLAN_DESC,
            frequencies = COPD_BEHAVIOR_VISIT_PLAN_SEQUENCE,
            displayTime = startDateTime
        )
    }

    /**
     * 添加线上慢阻肺随访计划
     * @param startDateTime 健康计划开始时间
     */
    fun addOnlineCopdVisitPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = startDateTime,
            healthPlanName = COPD_ONLINE_VISIT_PLAN_NAME,
            healthPlanType = HealthPlanType.ONLINE_COPD,
            healthPlanDesc = COPD_ONLINE_VISIT_PLAN_DESC,
            frequencies = COPD_ONLINE_VISIT_PLAN_SEQUENCE,
            displayTime = startDateTime
        )
    }

    /**
     * 添加接种流感疫苗计划
     * @param startDateTime 健康计划开始时间
     */
    fun addInfluenzaVaccinationPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_INFLUENZA_VACCINATION_PLAN_NAME,
            healthPlanType = HealthPlanType.INFLUENZA_VACCINATION,
            healthPlanDesc = COPD_INFLUENZA_VACCINATION_PLAN_DESC,
            frequencies = COPD_INFLUENZA_VACCINATION_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     * 添加接种肺炎球菌疫苗计划
     * @param startDateTime 健康计划开始时间
     */
    fun addPneumococcalVaccinationPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_PNEUMOCOCCAL_VACCINATION_PLAN_NAME,
            healthPlanType = HealthPlanType.PNEUMOCOCCAL_VACCINATION,
            healthPlanDesc = COPD_PNEUMOCOCCAL_VACCINATION_PLAN_DESC,
            frequencies = COPD_PNEUMOCOCCAL_VACCINATION_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     * 添加脉搏氧饱和度计划
     * @param startDateTime 健康计划开始时间
     */
    fun addPulseOxygenSaturationPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = startDateTime,
            healthPlanName = COPD_PULSE_OXYGEN_SATURATION_PLAN_NAME,
            healthPlanType = HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN,
            healthPlanDesc = COPD_PULSE_OXYGEN_SATURATION_PLAN_DESC,
            frequencies = COPD_PULSE_OXYGEN_SATURATION_PLAN_SEQUENCE,
            displayTime = startDateTime
        )
    }

    /**
     * 添加肺功能检查计划
     * @param startDateTime 健康计划开始时间
     */
    fun addPulmonaryFunctionExaminationPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_NAME,
            healthPlanType = HealthPlanType.PULMONARY_FUNCTION_EXAMINATION_PLAN,
            healthPlanDesc = COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_DESC,
            frequencies = COPD_PULMONARY_FUNCTION_EXAMINATION_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     * 添加呼吸科体检计划
     * @param startDateTime 健康计划开始时间
     */
    fun addRespiratoryDepartmentExaminationPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_NAME,
            healthPlanType = HealthPlanType.RESPIRATORY_DEPARTMENT_EXAMINATION,
            healthPlanDesc = COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_DESC,
            frequencies = COPD_RESPIRATORY_DEPARTMENT_EXAMINATION_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     * 添加Zung氏焦虑自评量表计划
     * @param startDateTime 健康计划开始时间
     */
    fun addZungSelfRatingAnxietyScalePlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_NAME,
            healthPlanType = HealthPlanType.ZUNG_SELF_RATING_ANXIETY_SCALE,
            healthPlanDesc = COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_DESC,
            frequencies = COPD_ZUNG_SELF_RATING_ANXIETY_SCALE_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     * 添加Zung氏抑郁自评量表计划
     * @param startDateTime 健康计划开始时间
     */
    fun addZungSelfRatingDepressionScalePlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        return this.saveCopdHealthPlanAndRemindPlan(
            startDateTime = LocalDate.of(startDateTime.year, 1, 1).atStartOfDay(),
            healthPlanName = COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_NAME,
            healthPlanType = HealthPlanType.ZUNG_SELF_RATING_DEPRESSION_SCALE,
            healthPlanDesc = COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_DESC,
            frequencies = COPD_ZUNG_SELF_RATING_DEPRESSION_SCALE_PLAN_SEQUENCE,
            clockDisplay = false,
            displayTime = startDateTime
        )
    }

    /**
     *
     *保存慢阻肺相关计划的通用方法
     * @param startDateTime 健康计划开始时间
     * @param healthPlanName 健康计划名称
     * @param healthPlanType 健康计划类型
     * @param healthPlanDesc 健康计划描述
     * @param frequencies 健康计划频率
     * @param clockDisplay 打卡后是否显示（true：显示、false：不显示、默认不传会设置为true）
     */
    private fun saveCopdHealthPlanAndRemindPlan(
        startDateTime: LocalDateTime,
        healthPlanName: String,
        healthPlanType: HealthPlanType,
        healthPlanDesc: String,
        frequencies: List<Frequency>,
        clockDisplay: Boolean? = null,
        displayTime: LocalDateTime
    ): List<HealthPlanDTO> {
        // 拼接参数
        val param = HealthPlanDTO(
            name = healthPlanName,
            type = healthPlanType,
            subName = null,
            desc = healthPlanDesc,
            externalKey = null,
            cycleStartTime = startDateTime,
            cycleEndTime = null,
            displayTime = displayTime,
            frequencys = frequencies,
            clockDisplay = clockDisplay
        )
        return listOf(param)
    }
}