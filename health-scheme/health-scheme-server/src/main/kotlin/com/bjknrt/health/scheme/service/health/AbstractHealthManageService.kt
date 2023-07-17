package com.bjknrt.health.scheme.service.health

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.entity.DrugPlanDTO
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.enums.DietPlanTypeEnum
import com.bjknrt.health.scheme.enums.DiseaseExistsTag
import com.bjknrt.health.scheme.job.event.SaveHealthManageEvent
import com.bjknrt.health.scheme.job.event.SaveHealthPlanEvent
import com.bjknrt.health.scheme.transfer.buildRemindFrequencyHealthAllParam
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import com.bjknrt.medication.remind.vo.TypesAndPatientParam
import org.jobrunr.jobs.JobId
import org.jobrunr.scheduling.JobScheduler
import org.jobrunr.storage.JobNotFoundException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import javax.annotation.Resource


abstract class AbstractHealthManageService(
    private val healthSchemeManageService: HealthSchemeManageService,
    private val healthPlanClient: HealthPlanApi,
    private val medicationRemindClient: MedicationRemindApi
) : HealthManageService, ApplicationEventPublisherAware {
    @Resource
    private lateinit var jobScheduler: JobScheduler

    private lateinit var eventPublisher: ApplicationEventPublisher

    companion object {
        const val OFFLINE_HYPERTENSION_PLAN_NAME = "完成高血压线下随访"
        const val OFFLINE_DIABETES_PLAN_NAME = "完成糖尿病线下随访"
        const val OFFLINE_ACUTE_CORONARY_DISEASE_PLAN_NAME = "完成冠心病线下随访"
        const val OFFLINE_CEREBRAL_STROKE_PLAN_NAME = "完成脑卒中线下随访"
        const val OFFLINE_COPD_PLAN_NAME = "完成慢阻肺线下随访"

        const val DIET_PLAN_GROUP = "饮食"
        const val DIET_PLAN_NAME = "完成本周饮食计划"
        const val DIET_PLAN_DESC = "1周1次"
        val DIET_PLAN_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        const val NOT_EVALUATE_SPORT_PLAN_NAME = "尽快完成运动评估"
        val NOT_EVALUATE_SPORT_PLAN_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        const val NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME = "尽快完成用药评估"
        val NOT_EVALUATE_DRUG_PROGRAM_PLAN_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        const val NOT_DIET_EVALUATE_NAME = "尽快完成饮食评估"
        val NOT_DIET_EVALUATE_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        // 科普计划
        val SCIENCE_POPULARIZATION_PLAN_FREQUENCIES = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )


        // 高血压/冠心病/脑卒中/糖尿病  每个季度提醒一次(1.1-3.30 / 4.1-6.30 / 7.1-9.30 / 10.1-12.31）
        val OFFLINE_VISIT_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 3,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        // 慢阻肺 上半年、下半年提醒一次（1.1-6.30/7.1-12.31）
        val OFFLINE_COPD_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 6,
                frequencyTimeUnit = TimeUnit.MONTHS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        const val REMINDER_VIEW_REPORT_PLAN_NAME = "您的阶段报告已经生成，请及时查看！"
        val REMINDER_VIEW_REPORT_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 10,
                frequencyTimeUnit = TimeUnit.YEARS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher
    }

    override fun saveHealthManage(
        patientId: BigInteger,
        healthManageType: HealthManageType,
        startDate: LocalDate,
        manageStage: ManageStage?,
        diseaseExistsTagList: List<DiseaseExistsTag>?
    ): HsHealthSchemeManagementInfo {
        //获取阶段
        val stage = getManageStage(manageStage)
        //获取结束时间
        val endDate = getEndDate(startDate, stage)
        //保存健康方案
        return healthSchemeManageService.saveHealthManage(
            patientId,
            healthManageType.name,
            startDate,
            endDate,
            getReportDate(startDate, endDate),
            stage?.name,
            diseaseExistsTagList
        )
    }

    override fun removeHealthManage(healthManageId: BigInteger, jobId: String?) {
        //删除健康方案
        healthSchemeManageService.deleteHealthManage(healthManageId)
        //删除回调任务
        jobId?.let {
            try {
                jobScheduler.delete(JobId.parse(it), "新建健康方案需要删除旧的健康方案")
            }  catch (e: JobNotFoundException) {
                LOGGER.warn("删除健康方案job任务异常", e)
            }

        } ?: run {
            healthSchemeManageService.getHealthSchemeManagementInfo(healthManageId)
                ?.knJobId
                ?.let {
                    try {
                        jobScheduler.delete(JobId.parse(it), "新建健康方案需要删除旧的健康方案")
                    }  catch (e: JobNotFoundException) {
                        LOGGER.warn("删除健康方案job任务异常", e)
                    }
                }
        }


        //删除提醒计划
        val healthPlanList = healthSchemeManageService.getHealthPlanList(healthManageId)
        //删除远程
        healthPlanList.map { it.knForeignPlanId }
            .takeIf { it.isNotEmpty() }
            ?.let { ids ->
                medicationRemindClient.batchIdDel(ids)
            }
        //删除本地
        healthSchemeManageService.deleteHealthPlan(healthManageId)
        //删除job任务
        healthPlanList.mapNotNull { it.knJobId }
            .takeIf { it.isNotEmpty() }
            ?.let { jobIdIdList ->
                jobIdIdList.forEach {
                    try {
                        jobScheduler.delete(JobId.parse(it), "新建健康方案需要删除旧的健康方案")
                    }  catch (e: JobNotFoundException) {
                        LOGGER.warn("删除健康方案job任务异常", e)
                    }
                }
            }
    }


    override fun publishSaveHealthManageEvent(
        healthManage: HsHealthSchemeManagementInfo
    ) {
        eventPublisher.publishEvent(SaveHealthManageEvent(this, healthManage))
    }

    override fun publishSaveHealthPlanEvent(
        healthManage: HsHealthSchemeManagementInfo,
        healthPlanList: List<HsHsmHealthPlan>
    ) {
        eventPublisher.publishEvent(SaveHealthPlanEvent(this, healthManage, healthPlanList))
    }

    /**
     * 获取健康方案的结束时间
     * @param startDate 健康方案的开始时间
     * @param manageStage 健康方案的阶段
     */
    protected open fun getEndDate(startDate: LocalDate, manageStage: ManageStage?): LocalDate? {
        return null
    }


    /**
     * 获取健康方案阶段
     * @param manageStage 健康方案阶段
     */
    private fun getManageStage(manageStage: ManageStage?): ManageStage? {
        if (manageStage != null) {
            return manageStage
        }
        return getManageStageIsNull()
    }

    /**
     * 获取当阶段为空的时候健康方案阶段的处理
     */
    protected open fun getManageStageIsNull(): ManageStage? {
        return null
    }

    /**
     * 获取健康方案报告的时间
     * @param startDate 健康方案的开始时间
     * @param endDate 健康方案的结束时间
     */
    protected open fun getReportDate(startDate: LocalDate, endDate: LocalDate?): LocalDate? {
        return endDate
    }

    /**
     * 先删除,后添加健康计划到本地和远程
     * @param healthManageId 健康方案Id
     * @param healthPlans 频率为几周几次的健康计划
     * @param drugPlans 频率为周几几点的健康计划
     */
    @Transactional
    override fun saveHealthPlanAndRemindPlan(
        patientId: BigInteger,
        healthManageId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>
    ): List<HsHsmHealthPlan>? {
        return saveHealthPlanAndRemindPlanService(
            patientId = patientId,
            healthManageId = healthManageId,
            healthPlans = healthPlans,
            drugPlans = drugPlans,
            isOnlyAdd = false
        )
    }

    /**
     * 仅添加健康计划到本地和远程(调用的远程是仅添加健康计划)
     * @param healthManageId 健康方案ID
     * @param healthPlans 频率为一周几次的健康计划
     * @param drugPlans 频率为周几几点的健康计划
     */
    @Transactional
    override fun addHealthPlan(
        patientId: BigInteger,
        healthManageId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>
    ): List<HsHsmHealthPlan>? {
        return saveHealthPlanAndRemindPlanService(
            patientId = patientId,
            healthManageId = healthManageId,
            healthPlans = healthPlans,
            drugPlans = drugPlans,
            isOnlyAdd = true
        )
    }

    private fun saveHealthPlanAndRemindPlanService(
        patientId: BigInteger,
        healthManageId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>,
        isOnlyAdd: Boolean
    ): List<HsHsmHealthPlan>? {
        if (healthPlans.isEmpty() && drugPlans.isEmpty()) {
            LOGGER.warn(AppSpringUtil.getMessage("logger.health-plan.remind-is-not-null"))
            return null
        }

        // 转换健康计划参数
        val remindFrequencyHealthAllParam = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = drugPlans
        )
        val resultList = if (isOnlyAdd) {
            healthPlanClient.batchAddHealthPlan(remindFrequencyHealthAllParam)
        } else {
            // 调用远程健康计划进行创建
            val deleteAndAddResults = healthPlanClient.upsertTypeFrequencyHealth(remindFrequencyHealthAllParam)

            // 根据type和健康方案ID删除本地
            val types = mutableSetOf<HealthPlanType>()
            deleteAndAddResults.healthPlans.forEach {
                types.add(com.bjknrt.health.scheme.vo.HealthPlanType.valueOf(it.type.name))
            }
            deleteAndAddResults.drugPlans.forEach {
                types.add(com.bjknrt.health.scheme.vo.HealthPlanType.valueOf(it.type.name))
            }
            //删除本地的健康计划
            healthSchemeManageService.delHealthPlanByHealthManageIdAndTypes(
                healthManageId = healthManageId,
                types = types.toList()
            )
            deleteAndAddResults
        }
        val healthPlanList = mutableListOf<HsHsmHealthPlan>()

        // 添加本地频率为几周几次的健康计划
        for (forIt in resultList.healthPlans) {
            val healthPlan = healthSchemeManageService.saveHealthPlan(
                healthManageId,
                forIt.type.name,
                forIt.cycleStartTime,
                forIt.cycleEndTime,
                forIt.id,
                forIt.frequencyIds,
            )
            healthPlanList.add(healthPlan)
        }
        // 添加本地频率为周几几点的健康计划
        for (forIt in resultList.drugPlans) {
            val healthPlan = healthSchemeManageService.saveHealthPlan(
                healthManageId,
                forIt.type.name,
                forIt.cycleStartTime,
                forIt.cycleEndTime,
                forIt.id,
                forIt.frequencyIds,
            )
            healthPlanList.add(healthPlan)
        }
        return healthPlanList
    }

    @Transactional
    override fun delHealthPlanByPatientIdAndHealthManageIdAndTypes(
        patientId: BigInteger,
        healthManageId: BigInteger,
        types: List<HealthPlanType>
    ) {
        // 删除远程
        medicationRemindClient.batchDeleteByTypeAndPatientId(listOf(TypesAndPatientParam(
            patientId = patientId,
            types = types.map { com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.name) }
        )))
        //删除本地
        healthSchemeManageService.delHealthPlanByHealthManageIdAndTypes(healthManageId, types)
    }

    override fun deleteHealthPlanAndRemindPlan(healthManageId: BigInteger, healthPlanTypes: List<HealthPlanType>) {
        //查询本地的健康计划集合
        val healthPlanList = healthSchemeManageService.getHealthPlanList(healthManageId, healthPlanTypes)

        if (healthPlanList.isNotEmpty()) {
            //删除远程健康计划
            medicationRemindClient.batchIdDel(healthPlanList.map { it.knForeignPlanId })
            //删除本地计划健康计划
            healthSchemeManageService.deleteHealthPlanList(healthPlanList.map { it.knId })
        }
    }

    /**
     * 保存通用的健康方案提醒计划
     */
    override fun saveCommentHealthPlan(healthManage: HsHealthSchemeManagementInfo) {
        val patientId = healthManage.knPatientId
        val healthManageId = healthManage.knId
        val startDateTime = healthManage.knStartDate.atStartOfDay()
        // 健康方案服务包类型
        val healthManageType = HealthManageType.valueOf(healthManage.knHealthManageType)
        val healthPlans = mutableListOf<HealthPlanDTO>()

        //添加科普计划
        this.addPopularSciencePlan(startDateTime).let {
            healthPlans.addAll(it)
        }

        //添加饮食计划
        this.addDietPlan(startDateTime, healthManageType)?.let {
            healthPlans.addAll(it)
        }

        //添加未进行评估的运动计划
        this.addNotEvaluateSportPlan(startDateTime).let {
            healthPlans.addAll(it)
        }

        // 添加未进行用药评估计划
        this.addNotEvaluateDrugProgramPlan(
            startDateTime,
            healthManageType
        )?.let {
            healthPlans.addAll(it)
        }

        //添加未进行评估的饮食计划(糖尿病、高血压服务包)
        if (healthManageType == HealthManageType.HYPERTENSION) {
            this.addDietNotEvaluatePlan(
                startDateTime,
                HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION
            ).let {
                healthPlans.addAll(it)
            }
        }
        if (healthManageType == HealthManageType.DIABETES) {
            this.addDietNotEvaluatePlan(
                startDateTime,
                HealthPlanType.DIET_NOT_EVALUATED_DIABETES
            ).let {
                healthPlans.addAll(it)
            }
        }

        //健康方案类型为脑卒中：不生成线下随访
        if (HealthManageType.CEREBRAL_STROKE != healthManageType) {
            //高血压线下随访
            if (healthManageType == HealthManageType.HYPERTENSION) {
                this.fiveDiseaseOfflineVisitPlan(
                    startDateTime,
                    OFFLINE_HYPERTENSION_PLAN_NAME,
                    HealthPlanType.OFFLINE_HYPERTENSION,
                    OFFLINE_VISIT_FREQUENCY
                ).let {
                    healthPlans.addAll(it)
                }
            }

            //糖尿病线下随访
            if (healthManageType == HealthManageType.DIABETES) {
                this.fiveDiseaseOfflineVisitPlan(
                    startDateTime,
                    OFFLINE_DIABETES_PLAN_NAME,
                    HealthPlanType.OFFLINE_DIABETES,
                    OFFLINE_VISIT_FREQUENCY
                ).let {
                    healthPlans.addAll(it)
                }
            }

            //冠心病线下随访
            if (healthManageType == HealthManageType.ACUTE_CORONARY_DISEASE) {
                this.fiveDiseaseOfflineVisitPlan(
                    startDateTime,
                    OFFLINE_ACUTE_CORONARY_DISEASE_PLAN_NAME,
                    HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
                    OFFLINE_VISIT_FREQUENCY
                ).let {
                    healthPlans.addAll(it)
                }
            }

            //慢阻肺线下随访
            if (healthManageType == HealthManageType.COPD) {
                this.fiveDiseaseOfflineVisitPlan(
                    startDateTime,
                    OFFLINE_COPD_PLAN_NAME,
                    HealthPlanType.OFFLINE_COPD,
                    OFFLINE_COPD_FREQUENCY
                ).let {
                    healthPlans.addAll(it)
                }
            }
        }

        // 调用根据type批量添加健康计划, 保存关联关系
        this.saveHealthPlanAndRemindPlan(
            patientId = patientId,
            healthManageId = healthManageId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )
    }

    /**
     * 科普计划
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     */
    private fun addPopularSciencePlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        // 拼接参数
        return listOf(
            HealthPlanDTO(
                name = "每日一科普",
                type = HealthPlanType.SCIENCE_POPULARIZATION_PLAN,
                subName = null,
                desc = "每日1篇",
                externalKey = null,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                group = "每日一科普",
                frequencys = SCIENCE_POPULARIZATION_PLAN_FREQUENCIES
            )
        )
    }

    /**
     * 饮食计划
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     * @param healthManageType 健康方案类型
     */
    private fun addDietPlan(
        startDateTime: LocalDateTime,
        healthManageType: HealthManageType
    ): List<HealthPlanDTO>? {
        // 根据健康方案确认文章type
        var type: DietPlanTypeEnum? = null
        // 高血压
        if (healthManageType == HealthManageType.HYPERTENSION) type = DietPlanTypeEnum.HYPERTENSION
        // 糖尿病
        if (healthManageType == HealthManageType.DIABETES) type = DietPlanTypeEnum.DIABETES
        // 慢阻肺
        if (healthManageType == HealthManageType.COPD) type = DietPlanTypeEnum.COPD
        // 冠心病 或 脑卒中
        if (healthManageType == HealthManageType.ACUTE_CORONARY_DISEASE || healthManageType == HealthManageType.CEREBRAL_STROKE)
            type = DietPlanTypeEnum.ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE

        // 拼接参数
        return type?.let {
            listOf(
                HealthPlanDTO(
                    name = DIET_PLAN_NAME,
                    type = HealthPlanType.DIET_PLAN,
                    subName = null,
                    desc = DIET_PLAN_DESC,
                    externalKey = type.name,
                    cycleStartTime = startDateTime,
                    cycleEndTime = null,
                    group = DIET_PLAN_GROUP,
                    frequencys = DIET_PLAN_FREQUENCIES
                )
            )
        }
    }

    /**
     * 未进行运动评估的运动健康计划
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     */
    private fun addNotEvaluateSportPlan(
        startDateTime: LocalDateTime
    ): List<HealthPlanDTO> {
        // 拼接参数
        return listOf(
            HealthPlanDTO(
                name = NOT_EVALUATE_SPORT_PLAN_NAME,
                type = HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_SPORT_PLAN_FREQUENCIES
            )
        )
    }

    /**
     * 未进行用药评估计划
     */
    private fun addNotEvaluateDrugProgramPlan(
        startDateTime: LocalDateTime,
        healthPlanType: HealthManageType
    ): List<HealthPlanDTO>? {
        val type = when (healthPlanType) {
            HealthManageType.HYPERTENSION -> HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED
            HealthManageType.DIABETES -> HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED
            else -> null
        }

        return type?.let {
            // 拼接参数
            listOf(
                HealthPlanDTO(
                    name = NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME,
                    type = it ,
                    subName = null,
                    desc = null,
                    externalKey = null,
                    cycleStartTime = startDateTime,
                    cycleEndTime = null,
                    clockDisplay = false,
                    frequencys = NOT_EVALUATE_DRUG_PROGRAM_PLAN_FREQUENCIES
                )
            )
        }
    }

    /**
     * 未进行饮食评估的运动健康计划
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     * @param type 饮食评估类型
     */
    private fun addDietNotEvaluatePlan(
        startDateTime: LocalDateTime,
        type: HealthPlanType
    ): List<HealthPlanDTO> {
        // 拼接参数
        return listOf(
            HealthPlanDTO(
                name = NOT_DIET_EVALUATE_NAME,
                type = type,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_DIET_EVALUATE_FREQUENCIES
            )
        )
    }

    /**
     * 五病线下随访计划创建逻辑
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     * @param healthName 健康计划名称
     * @param healthPlanType 健康计划类型
     * @param frequencies 健康计划频率
     */
    protected fun fiveDiseaseOfflineVisitPlan(
        startDateTime: LocalDateTime,
        healthName: String,
        healthPlanType: HealthPlanType,
        frequencies: List<Frequency>,
    ): List<HealthPlanDTO> {

        // 拼接参数
        return listOf(
            HealthPlanDTO(
                name = healthName,
                type = healthPlanType,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = LocalDate.of(LocalDate.now().year, 1, 1).atStartOfDay(),
                cycleEndTime = null,
                displayTime = startDateTime,
                clockDisplay = false,
                frequencys = frequencies
            )
        )
    }

    /**
     * 创建查看阶段报告的计划
     *
     * @param patientId  患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 健康计划开始时间
     */
    fun addReminderViewReportPlan(
        patientId: BigInteger,
        healthManageId: BigInteger,
        startDateTime: LocalDateTime = LocalDate.now().atStartOfDay()
    ) {

        // 拼接参数
        val healthPlans = listOf(
            HealthPlanDTO(
                name = REMINDER_VIEW_REPORT_PLAN_NAME,
                type = HealthPlanType.REMINDER_VIEW_REPORT,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                displayTime = startDateTime,
                frequencys = REMINDER_VIEW_REPORT_FREQUENCY
            )
        )
        this.addHealthPlan(
            patientId = patientId,
            healthManageId = healthManageId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )
    }

}
