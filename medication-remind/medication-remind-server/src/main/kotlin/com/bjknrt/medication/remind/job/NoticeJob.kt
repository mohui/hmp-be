package com.bjknrt.medication.remind.job

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.util.PageUtils
import me.danwi.sqlex.core.query.*
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.spring.annotations.Recurring
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class NoticeJob(val noticeService: NoticeService, val healthPlanService: HealthPlanService) {

    @Value("\${app.page.default.pageSize:5000}")
    var pageSize = 5000L

    /**
     * 每分钟
     */
    @Job(retries = 3, name = "定时提醒用药")
    @Recurring(id = "remind", interval = "PT1M")
    fun exec() {
        PageUtils.pageExec(
            { currentPage -> PagedResult.fromDbPaged(healthPlanService.pagePatientIds(currentPage, pageSize)) },
            { patientIds -> noticeService.remind(patientIds) }
        )
    }

    /**
     * 线下随访开始时间提醒
     */
    @Job(retries = 3, name = "线下随访开始时间")
    @Recurring(id = "offlineVisitStartDate", cron = "0 10 * * *")
    fun offlineVisitStartDate() {
        PageUtils.pageExec(
            { currentPage -> PagedResult.fromDbPaged(healthPlanService.pagePatientIds(currentPage, pageSize)) },
            { patientIds -> noticeService.offlineVisitStartDate(patientIds) }
        )
    }

    /**
     * 线下随访结束时间提醒
     */
    @Job(retries = 3, name = "线下随访结束时间")
    @Recurring(id = "offlineVisitEndDate", cron = "0 10 1 3,6,9,12 *")
    fun offlineVisitEndDate() {
        PageUtils.pageExec(
            { currentPage -> PagedResult.fromDbPaged(healthPlanService.pagePatientIds(currentPage, pageSize)) },
            { patientIds -> noticeService.offlineVisitEndDate(patientIds) }
        )
    }

    /**
     * 测量计划（血压、空腹血糖、餐前血糖、餐后2小时血糖、脉搏氧饱和度）
     */
    @Job(retries = 3, name = "测量计划")
    @Recurring(id = "measure", cron = "0 9 * * *")
    fun measure() {
        PageUtils.pageExec(
            { currentPage -> PagedResult.fromDbPaged(healthPlanService.pagePatientIds(currentPage, pageSize)) },
            { patientIds -> noticeService.measure(patientIds) }
        )
    }

    @Job(retries = 3, name = "线上随访开始时间")
    @Recurring(id = "onlineVisitStartDate", cron = "0 9 * * *")
    fun onlineVisitStartDate() {
        PageUtils.pageExec(
            { currentPage -> PagedResult.fromDbPaged(healthPlanService.pagePatientIds(currentPage, pageSize)) },
            { patientIds -> noticeService.onlineVisitStartDate(patientIds) }
        )
    }
}