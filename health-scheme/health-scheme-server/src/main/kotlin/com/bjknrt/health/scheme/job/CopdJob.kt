package com.bjknrt.health.scheme.job

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.scheme.constant.COPD_INTERVAL_MONTH
import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import com.bjknrt.health.scheme.job.event.SaveHealthManageEvent
import com.bjknrt.health.scheme.service.CopdReport
import com.bjknrt.health.scheme.service.CopdReportService
import com.bjknrt.health.scheme.service.PulseOxygenSaturation
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl
import com.bjknrt.health.scheme.util.getFrequencyIds
import me.danwi.kato.common.exception.KatoException
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.JobScheduler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Component
class CopdJob(
    val copdReportService: CopdReportService,
    val healthSchemeManageService: HealthSchemeManageService,
    val healthManageService: HealthManageDiabetesServiceImpl,
    val patientClient: PatientApi,
    val indicatorClient: IndicatorApi,
    val jobScheduler: JobScheduler
) {

    companion object {
        const val COPD_STAGE_REPORT = "慢阻肺阶段性报告"
    }
    @EventListener(condition = "#root.event.healthManage.knHealthManageType=='COPD'")
    @Transactional
    fun listener(event: SaveHealthManageEvent) {
        val healthManage = event.healthManage

        // 健康方案ID
        val healthManageInfoId = healthManage.knId.toString()
        // 患者ID
        val patientId = healthManage.knPatientId.toString()
        // 事件执行时间
        val runTime = healthManage.knReportOutputDate?.atStartOfDay()

        // 表示创建一个定时任务，在指定的时间执行 copdHealthManageJobHandler() 方法。
        val jobId = jobScheduler.schedule(runTime) {
            copdHealthManageJobHandler(
                manageId = healthManageInfoId,
                patientId = patientId,
                jobContext = JobContext.Null
            )
        }

        //更新jobID
        healthManage.knJobId = jobId.toString()
        healthSchemeManageService.updateHealthManage(healthManage)
    }

    /**
     * 定时执行计划任务
     *
     * @param manageId: 健康计划ID
     */
    @Job(retries = 3, name = "健康方案-慢阻肺-%0-COPD-%1")
    @Transactional
    fun copdHealthManageJobHandler(manageId: String, patientId: String, jobContext: JobContext) {
        val healthManageInfoId = manageId.toBigInteger()
        //根据管理ID获取健康管理方案信息
        val healthManage = healthSchemeManageService.getHealthSchemeManagementInfo(healthManageInfoId) ?: return
        //获取患者信息
        val patientInfo = patientClient.getPatientInfo(healthManage.knPatientId)

        //生成阶段报告sdd
        //阶段报告生成时间DSD
        val reportEndDate = healthManage.knReportOutputDate?.atStartOfDay()
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.stage-report-date-is-null"))

        //阶段报告生成时间减去间隔时间就是本阶段的开始时间
        val reportStartDate = reportEndDate.minusMonths(COPD_INTERVAL_MONTH)

        this.addStageReport(healthManage.knId, reportStartDate, reportEndDate, patientInfo)

        //更新出报告的时间
        //计算新的报告输出时间
        val newReportOutputDate = reportEndDate.plusMonths(COPD_INTERVAL_MONTH).toLocalDate()
        //更新健康方案
        healthManage.knReportOutputDate = newReportOutputDate
        healthSchemeManageService.updateHealthManage(healthManage)

        //发布事件创建下一次生成报告的任务
        healthManageService.publishSaveHealthManageEvent(healthManage)
    }


    private fun addStageReport(
        healthManageId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        patientInfo: PatientInfoResponse,
    ) {
        val patientId = patientInfo.id

        //健康管理信息关联的健康计划信息
        val healthPlanList = healthSchemeManageService.getHealthPlanList(healthManageId)
        //提醒计划的Id和频率Id
        val frequencyValueList = healthPlanList.map { plan ->
            PlanFrequencyValue(
                plan.knForeignPlanId,
                plan.knPlanType,
                getFrequencyIds(plan.knForeignPlanFrequencyIds).firstOrNull()
            )
        }

        //脉搏氧饱和度
        val pulseOxygenSaturationList = indicatorClient.pulseOximetryList(
            FindListParam(startDateTime, endDateTime, patientId)
        ).map {
            PulseOxygenSaturation(it.knPulseOximetry, it.knMeasureAt)
        }

        val copdReport = CopdReport(
            patientId = patientId,
            patientName = patientInfo.name,
            age = patientInfo.age,
            healthManageId = healthManageId,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            reportName = COPD_STAGE_REPORT,
            pulseOxygenSaturationList = pulseOxygenSaturationList,
            planFrequencyValue = frequencyValueList
        )
        copdReportService.generateReport(copdReport)
    }
}
