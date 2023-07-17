package com.bjknrt.health.scheme.job

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.vo.PatientIndicatorListResult
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.HsManageDetailExaminationAdapter
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import com.bjknrt.health.scheme.enums.ExaminationCodeEnum
import com.bjknrt.health.scheme.job.event.SaveHealthManageEvent
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.service.HypertensionReport
import com.bjknrt.health.scheme.service.HypertensionReportService
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageHypertensionServiceImpl
import com.bjknrt.health.scheme.util.getFrequencyIds
import com.bjknrt.health.scheme.vo.*
import me.danwi.kato.common.exception.KatoException
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.JobScheduler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HypertensionJob(
    val healthSchemeManageService: HealthSchemeManageService,
    val healthManageHypertensionServiceImpl: HealthManageHypertensionServiceImpl,
    val hypertensionReportService: HypertensionReportService,
    val standardVerificationService: StandardVerificationService,
    val examinationService: ExaminationService,
    val patientClient: PatientApi,
    val jobScheduler: JobScheduler
) {

    companion object {
        //报告名称
        private const val HYPERTENSION_STAGE_REPORT = "高血压阶段性报告"

        private val SWITCHING_STAGE_PLAN_TYPE_LIST = listOf(
            //健康科普
            HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name,
            //饮食
            HealthPlanType.DIET_PLAN.name,
            //未进行评估(运动计划)
            HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name,
            //运动
            HealthPlanType.EXERCISE_PROGRAM.name,
            //运动调整提醒(运动计划)
            HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND.name,
            //高血压(线下随访)
            HealthPlanType.OFFLINE_HYPERTENSION.name,
            //糖尿病(线下随访)
            HealthPlanType.OFFLINE_DIABETES.name,
            //冠心病(线下随访)
            HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE.name,
            //脑卒中(线下随访)
            HealthPlanType.OFFLINE_CEREBRAL_STROKE.name,
            //慢阻肺(线下随访)
            HealthPlanType.OFFLINE_COPD.name,
            // 高血压未进行用药评估
            HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED.name,
            // 高血压用药计划
            HealthPlanType.HYPERTENSION_DRUG_PROGRAM.name,
            //未进行评估(饮食)
            HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name
        )

        // 评估结果type
        val HYPERTENSION_EXAMINATION_CODE_LIST = setOf(
            // 运动评估问卷编码
            ExaminationCodeEnum.SPORT,
            // 用药评估-高血压
            ExaminationCodeEnum.HYPERTENSION_DRUG_PROGRAM,
            // 饮食评估-高血压
            ExaminationCodeEnum.DIET_EVALUATE_HYPERTENSION
        )
    }

    @EventListener(condition = "#root.event.healthManage.knHealthManageType=='HYPERTENSION'")
    @Transactional
    fun listener(event: SaveHealthManageEvent) {
        val healthManage = event.healthManage
        // 健康方案ID
        val healthManageInfoId = healthManage.knId.toString()
        // 患者ID
        val patientId = healthManage.knPatientId.toString()
        // 事件执行时间
        val runTime = healthManage.knEndDate?.atStartOfDay()

        // 表示创建一个定时任务，在指定的时间执行 hypertensionHealthManageJobHandler() 方法。
        val jobId = jobScheduler.schedule(runTime) {
            hypertensionHealthManageJobHandler(
                manageId = healthManageInfoId,
                patientId = patientId,
                jobContext = JobContext.Null
            )
        }

        //更新健康方案的jobID
        healthManage.knJobId = jobId.toString()
        healthSchemeManageService.updateHealthManage(healthManage)
    }

    /**
     * 定时执行计划任务
     *
     * @param manageId: 健康计划ID
     */
    @Job(retries = 3, name = "健康方案-高血压-%0-HYPERTENSION-%1")
    @Transactional
    fun hypertensionHealthManageJobHandler(manageId: String, patientId: String, jobContext: JobContext) {
        val healthManageInfoId = manageId.toBigInteger()
        //根据 管理方案ID 获取健康管理方案信息
        val managementInfo = healthSchemeManageService.getHealthSchemeManagementInfo(healthManageInfoId)

        managementInfo?.let {

            val healthManageEndDate = it.knEndDate?.atStartOfDay()
                ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-end-date-is-null"))

            val managementStage = it.knManagementStage?.let { stage -> ManageStage.valueOf(stage) }
                ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))

            //上个阶段的结束时间是当前新方案的开始时间
            val endDate = it.knEndDate
                ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-end-date-is-null"))

            //获取患者信息
            val patientInfo = patientClient.getPatientInfo(it.knPatientId)

            //获取患者的指标集合
            val indicatorListForDpm = standardVerificationService.patientManageIndicatorListResult(it)

            // 查询问卷适配集合
            val manageDetailExaminationAdapter = examinationService.queryCurrentSchemeExaminationAdapterList(
                ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                    knPatientId = it.knPatientId,
                    knHealthManageSchemeId = healthManageInfoId,
                    knExaminationPaperCodeSet = HYPERTENSION_EXAMINATION_CODE_LIST
                )
            )

            //健康管理信息关联的健康计划信息
            val healthPlanList = healthSchemeManageService.getHealthPlanList(healthManageInfoId)

            //获取血压测量计划，判断健康计划是否达标
            val hsmHealthPlan: HsHsmHealthPlan = healthPlanList
                .first { plan -> plan.knPlanType == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name }

            //再调用远程血压计划判断是否达标
            val isHealthPlanStandard = standardVerificationService.isHypertensionBloodPressureHealthPlanStandard(
                it.knStartDate.atStartOfDay(),
                healthManageEndDate,
                hsmHealthPlan,
                managementStage
            )

            //血压指标是否达标
            val isBloodPressureStandard =
                standardVerificationService.isBloodPressureStandard(it.knId, it.knPatientId)

            val isStandard = isHealthPlanStandard && isBloodPressureStandard

            //根据当前阶段是否达标，获取下一个阶段
            val nextStage = getNextStage(managementStage, isStandard)

            //创建下一个阶段的健康管理方案
            dynamicAddStageHealthManage(
                managementInfo = it,
                manageStage = nextStage,
                isStandard = isStandard,
                isHealthPlanStandard = isHealthPlanStandard,
                startDate = endDate,
                indicatorListResult = indicatorListForDpm,
                patientInfo = patientInfo,
                healthPlanList = healthPlanList,
                manageDetailExaminationAdapter = manageDetailExaminationAdapter
            )
        }?: throw KatoException(AppSpringUtil.getMessage("health-plan.job-health-plan-is-null", healthManageInfoId))
    }

    private fun dynamicAddStageHealthManage(
        managementInfo: HsHealthSchemeManagementInfo,
        manageStage: ManageStage,
        isStandard: Boolean,
        isHealthPlanStandard: Boolean,
        startDate: LocalDate,
        indicatorListResult: PatientIndicatorListResult,
        patientInfo: PatientInfoResponse,
        healthPlanList: List<HsHsmHealthPlan>,
        manageDetailExaminationAdapter: List<HsManageDetailExaminationAdapter>
    ) {
        //生成阶段报告
        addStageReport(managementInfo, patientInfo, indicatorListResult, isHealthPlanStandard, healthPlanList)

        //切换健康管理方案，健康计划
        switchingStageHypertension(patientInfo, manageStage, isStandard, startDate, healthPlanList, manageDetailExaminationAdapter)
    }

    fun getNextStage(manageStage: ManageStage, judgeStandard: Boolean): ManageStage {
        if (judgeStandard) { //达标
            //如果是第四阶段，返回本阶段
            return if (manageStage.name == ManageStage.SECULAR_STABLE_STAGE.name) {
                manageStage
            } else {
                //否则返回下一阶段
                val manageStages = ManageStage.values()
                manageStages[manageStage.ordinal + 1]
            }

        } else { //不达标
            //如果是第四阶段，返回上一阶段
            return if (manageStage.name == ManageStage.SECULAR_STABLE_STAGE.name) {
                val manageStages = ManageStage.values()
                manageStages[manageStage.ordinal - 1]
            } else {
                //否则返回本阶段
                manageStage
            }
        }
    }

    /**
     * 切换健康方案
     * @param patientInfo 患者信息
     * @param manageStage 要切换到的阶段
     * @param isStandard 是否达标
     * @param startDate 健康方案开始时间
     * @param healthPlanList 健康方案对应的健康计划
     */
    private fun switchingStageHypertension(
        patientInfo: PatientInfoResponse,
        manageStage: ManageStage,
        isStandard: Boolean,
        startDate: LocalDate,
        healthPlanList: List<HsHsmHealthPlan>,
        manageDetailExaminationAdapter: List<HsManageDetailExaminationAdapter>
    ) {
        //1.创建新的健康管理方案
        val healthManage = healthManageHypertensionServiceImpl.saveHealthManage(
            patientInfo.id,
            HealthManageType.HYPERTENSION,
            startDate,
            manageStage,
            null
        )
        //发布新建健康方案事件
        healthManageHypertensionServiceImpl.publishSaveHealthManageEvent(healthManage)

        //2.合并上个阶段的科普计划、饮食计划、运动计划、线下随访计划到本阶段
        healthPlanList.filter {
            SWITCHING_STAGE_PLAN_TYPE_LIST.contains(it.knPlanType)
        }.map {
            it.knId = AppIdUtil.nextId()
            it.knSchemeManagementId = healthManage.knId
            it
        }.forEach {
            healthSchemeManageService.saveHealthPlan(it)
        }

        //3、job回调：创建3个健康计划 + 发布创建定时任务的事件
        val endDate = healthManage.knEndDate
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-end-date-is-null"))
        // 创建健康计划
        val healthPlans = mutableListOf<HealthPlanDTO>()
        //3-1、高血压测量计划
        healthManageHypertensionServiceImpl.addBloodPressureMeasurePlan(
            startDate.atStartOfDay(),
            endDate.atStartOfDay(),
            manageStage
        ).let {
            healthPlans.addAll(it)
        }
        //3-2、高血压随访计划
        healthManageHypertensionServiceImpl.addHypertensionOnlineVisit(
            isStandard,
            startDate.atStartOfDay(),
            endDate.atStartOfDay()
        )?.let {
            healthPlans.addAll(it)
        }
        //3-3、行为习惯随访计划
        healthManageHypertensionServiceImpl.addBehaviorVisitPlan(
            endDate.atStartOfDay()
        ).let {
            healthPlans.addAll(it)
        }

        val hsHsmHealthPlans = healthManageHypertensionServiceImpl.addHealthPlan(
            patientId = healthManage.knPatientId,
            healthManageId = healthManage.knId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )?: listOf()

        // 合并上个阶段的用药评估结果, 饮食评估结果, 运动禁忌到本阶段
        val manageDetailExaminationAdapterListParam = manageDetailExaminationAdapter
            .map { st ->
                HsManageDetailExaminationAdapter.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnPatientId(st.knPatientId)
                    .setKnHealthManageSchemeId(healthManage.knId)
                    .setKnExaminationPaperCode(st.knExaminationPaperCode)
                    .setKnAnswerRecordId(st.knAnswerRecordId)
                    .setKnAnswerResultId(st.knAnswerResultId)
                    .setKnQuestionsId(st.knQuestionsId)
                    .setKnOptionId(st.knOptionId)
                    .setKnCreatedBy(st.knCreatedBy)
                    .setIsDel(false)
                    .setKnCreatedAt(LocalDateTime.now())
                    .setKnOptionLabel(st.knOptionLabel)
                    .setKnMessage(st.knMessage)
                    .build()
            }
        examinationService.insertSchemeExaminationAdapter(manageDetailExaminationAdapterListParam)

        //3-4、发布事件(创建每个计划对应的定时任务)
        healthManageHypertensionServiceImpl.publishSaveHealthPlanEvent(healthManage, hsHsmHealthPlans)
    }

    private fun addStageReport(
        managementInfo: HsHealthSchemeManagementInfo,
        patientInfo: PatientInfoResponse,
        indicatorListResult: PatientIndicatorListResult,
        isHealthPlanStandard: Boolean,
        healthPlanList: List<HsHsmHealthPlan>
    ) {
        val startDate = managementInfo.knStartDate
        val endDate = managementInfo.knEndDate
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-start-date-is-null"))
        val managementStage = managementInfo.knManagementStage
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))

        val valueList = indicatorListResult.bloodPressureListResult.map {
            BloodPressure(
                it.knSystolicBloodPressure,
                it.knDiastolicBloodPressure,
                it.knMeasureAt
            )
        }

        //提醒计划的Id和频率Id
        val frequencyValueList = healthPlanList.map { plan ->
            PlanFrequencyValue(
                plan.knForeignPlanId,
                plan.knPlanType,
                getFrequencyIds(plan.knForeignPlanFrequencyIds).firstOrNull()
            )
        }

        val hypertensionReport = HypertensionReport(
            managementInfo.knPatientId,
            patientInfo.name,
            managementInfo.knId,
            Gender.valueOf(patientInfo.gender.name),
            patientInfo.age,
            isHealthPlanStandard,
            startDate.atStartOfDay(),
            endDate.atStartOfDay(),
            valueList,
            standardVerificationService.smokeNum(indicatorListResult),
            patientInfo.height ?: BigDecimal.ZERO,
            patientInfo.weight ?: BigDecimal.ZERO,
            patientInfo.waistline ?: BigDecimal.ZERO,
            HYPERTENSION_STAGE_REPORT,
            frequencyValueList,
            managementStage
        )
        hypertensionReportService.generateReport(hypertensionReport)
    }
}
