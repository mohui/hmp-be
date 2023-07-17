package com.bjknrt.health.scheme.job

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.HsHsmHealthPlanTable
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.job.event.SaveHealthPlanEvent
import com.bjknrt.health.scheme.service.CerebralStrokeReport
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageCerebralStrokeServiceImpl
import com.bjknrt.health.scheme.service.impl.CerebralStrokeReportServiceImpl
import com.bjknrt.health.scheme.vo.HealthPlanType
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.JobScheduler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Component
class CerebralStrokeHealthPlanJob(
    val healthSchemeManageService: HealthSchemeManageService,
    val hsHsmHealthPlanTable: HsHsmHealthPlanTable,
    val hsCerebralStrokeServiceImpl: HealthManageCerebralStrokeServiceImpl,
    val cerebralStrokeReportServiceImpl: CerebralStrokeReportServiceImpl,
    val patientClient: PatientApi,
    val standardVerificationService: StandardVerificationService,
    val jobScheduler: JobScheduler
) {

    companion object {
        //报告名称
        private const val CEREBRAL_STROKE_CYCLE_STAGE_REPORT = "脑卒中阶段性报告"

        //脑卒中周期计划的类型
        val CEREBRAL_STROKE_PLAN_TYPES = listOf(
            HealthPlanType.CEREBRAL_STROKE,
            HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
            HealthPlanType.MRS,
            HealthPlanType.BARTHEL,
            HealthPlanType.EQ_5_D
        )
    }

    /**
     * 脑卒中计划比较特殊, 需要先找医生做出院随访才会生成脑卒中方案计划
     * 脑卒中方案不会删除, 如果计划时间到了, 只会删除脑卒中健康计划, 然后创建新的脑卒中健康计划,更新健康方案表里的job执行时间
     */
    @EventListener(condition = "#root.event.healthManage.knHealthManageType=='CEREBRAL_STROKE'")
    @Transactional
    fun listener(event: SaveHealthPlanEvent) {
        val managementInfo = event.healthManage
        //过滤出新创建的周期计划
        val healthPlanList = event.healthPlanList
            .filter { plan -> CEREBRAL_STROKE_PLAN_TYPES.contains(HealthPlanType.valueOf(plan.knPlanType)) }
        if (healthPlanList.isNotEmpty()) {
            // 健康方案ID
            val healthManageInfoId = managementInfo.knId.toString()
            // 患者ID
            val patientId = managementInfo.knPatientId.toString()
            // 方案结束时间
            val runTime = healthPlanList[0].knEndDate

            // 表示创建一个定时任务，在指定的时间执行 cerebralStrokeCyclePlanJobHandler() 方法。
            val jobId = jobScheduler.schedule(runTime) {
                cerebralStrokeCyclePlanJobHandler(
                    manageId = healthManageInfoId,
                    patientId = patientId,
                    jobContext = JobContext.Null
                )
            }

            //更新周期计划的jobId
            hsHsmHealthPlanTable.update().setKnJobId(jobId.toString())
                .where(HsHsmHealthPlanTable.KnId `in` healthPlanList.map { plan -> plan.knId.arg })
                .execute()
            //更新健康方案中的字段（阶段报告的输出时间，来源于周期计划的结束时间）
            managementInfo.knReportOutputDate = healthPlanList[0].knEndDate?.toLocalDate()
            healthSchemeManageService.updateHealthManage(managementInfo)
        }
    }

    /**
     * 定时执行计划任务
     *
     * @param manageId: 健康计划ID
     */
    @Job(retries = 3, name = "健康方案-脑卒中-%0-CEREBRAL_STROKE-%1")
    @Transactional
    fun cerebralStrokeCyclePlanJobHandler(manageId: String, patientId: String, jobContext: JobContext) {
        val jobId = jobContext.jobId.toString()
        val healthManageInfoId = manageId.toBigInteger()
        // 获取健康方案详情
        val managementInfo = healthSchemeManageService.getHealthSchemeManagementInfo(healthManageInfoId)
        // 获取jobID
        managementInfo?.let {
            //1、查询上一个周期的计划开始时间和结束时间
            val planInfo = getHealthPlanInfo(
                planTypeList= CEREBRAL_STROKE_PLAN_TYPES,
                healthManageInfoId = healthManageInfoId,
                jobId = jobId
            )
            val planEndDate =
                planInfo?.knEndDate ?: throw KatoException(AppSpringUtil.getMessage("health-plan-end-date-is-null"))

            //2、创建上个周期的阶段报告
            val cycleStartDate = this.calPreCycleStartDate(planEndDate)
            this.createCerebralStrokeGenerateReport(cycleStartDate, planEndDate, it)

            //3、创建新周期的计划并发布
            hsCerebralStrokeServiceImpl.addCerebralStrokeCyclePlan(
                it.knPatientId,
                it,
                planEndDate.toLocalDate(),
                THREE_MONTH
            )
        } ?: throw KatoException(AppSpringUtil.getMessage("health-plan.job-health-plan-is-null", healthManageInfoId))
    }

    private fun calPreCycleStartDate(planEndDate: LocalDateTime): LocalDateTime {
        return planEndDate.minusMonths(THREE_MONTH)
    }

    private fun createCerebralStrokeGenerateReport(
        cycleStartDate: LocalDateTime,
        cycleEndDate: LocalDateTime,
        manageInfo: HsHealthSchemeManagementInfo
    ) {
        //2-2、获取患者信息及周期内的指标集合
        val patientInfo = patientClient.getPatientInfo(manageInfo.knPatientId)
        val bloodPressureList =
            standardVerificationService.getBloodPressureIndicatorListResult(
                manageInfo.knPatientId,
                cycleStartDate,
                cycleEndDate
            )
        //2-3、创建脑卒中阶段报告
        cerebralStrokeReportServiceImpl.generateReport(
            CerebralStrokeReport(
                patientInfo.id,
                patientInfo.name,
                patientInfo.age,
                manageInfo.knId,
                cycleStartDate,
                cycleEndDate,
                CEREBRAL_STROKE_CYCLE_STAGE_REPORT,
                bloodPressureList
            )
        )
    }

    /**
     * 根据健康计划类型, 健康方案ID, 任务jobId获取对应的健康计划信息
     *
     * @param planTypeList 计划类型
     * @param healthManageInfoId 方案id
     * @param planTypeList 任务id
     */
    private fun getHealthPlanInfo(planTypeList: List<HealthPlanType>, healthManageInfoId: BigInteger, jobId: String): HsHsmHealthPlan? {

        //根据管理id、任务id、计划类型获取对应的健康健康计划信息
        return hsHsmHealthPlanTable.select().where(HsHsmHealthPlanTable.KnSchemeManagementId eq healthManageInfoId.arg)
            .where(HsHsmHealthPlanTable.KnJobId eq jobId.arg)
            .where(HsHsmHealthPlanTable.KnPlanType `in` planTypeList.map { plan -> plan.name.arg })
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .order(HsHsmHealthPlanTable.KnId, Order.Desc)
            .findOne()
    }
}
