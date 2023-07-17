package com.bjknrt.health.scheme.controller

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.api.ReportApi
import com.bjknrt.health.scheme.service.ClockInService
import com.bjknrt.health.scheme.service.health.HealthManageFacadeService
import com.bjknrt.health.scheme.util.abnormalDataAlertMsgToList
import com.bjknrt.health.scheme.vo.*
import me.danwi.sqlex.core.query.eq
import org.springframework.core.task.TaskExecutor
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.time.LocalDateTime

@RestController("com.bjknrt.health.scheme.api.ReportController")
class ReportController(
    val hsStageReportDao: HsStageReportDao,
    val hsStageReportTable: HsStageReportTable,
    val hsStageStatisticsTable: HsStageStatisticsTable,
    val hsStageReportStatisticsDetailTable: HsStageStatisticsDetailTable,
    val hsDiabetesStageStatisticsTable: HsDiabetesStageStatisticsTable,
    val hsDiabetesStageStatisticsDetailTable: HsDiabetesStageStatisticsDetailTable,
    val hsCerebralStrokeStageStatisticsTable: HsCerebralStrokeStageStatisticsTable,
    val hsCerebralStrokeStageStatisticsDetailTable: HsCerebralStrokeStageStatisticsDetailTable,
    val hsCopdStageStatisticsTable: HsCopdStageStatisticsTable,
    val hsCopdStageStatisticsDetailTable: HsCopdStageStatisticsDetailTable,
    val hsAcuteCoronaryDiseaseStageStatisticsTable: HsAcuteCoronaryDiseaseStageStatisticsTable,
    val hsAcuteCoronaryDiseaseStageStatisticsDetailTable: HsAcuteCoronaryDiseaseStageStatisticsDetailTable,
    val clockInService: ClockInService,
    val taskExecutor: TaskExecutor,
    val healthManageFacadeService: HealthManageFacadeService,
) : AppBaseController(), ReportApi {
    override fun getAcuteCoronaryDiseaseStageStatistics(body: BigInteger): StageStatisticsResponse {
        val report = hsStageReportTable.findById(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report.not-found"))

        return hsAcuteCoronaryDiseaseStageStatisticsTable.select()
            .where(HsAcuteCoronaryDiseaseStageStatisticsTable.StageReportId eq body)
            .findOne()
            ?.let {
                StageStatisticsResponse(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    actualMeasureNumber = it.actualMeasureNumber,
                    standardMeasureNumber = it.standardMeasureNumber,
                    systolicBloodPressureAvg = it.systolicBloodPressureAvg,
                    diastolicBloodPressureAvg = it.diastolicBloodPressureAvg,
                    systolicBloodPressureStandardDeviation = it.systolicBloodPressureStandardDeviation,
                    diastolicBloodPressureStandardDeviation = it.diastolicBloodPressureStandardDeviation,
                    bloodPressureFillRate = it.bloodPressureFillRate,
                    bloodPressureStandardRate = it.bloodPressureStandardRate,
                    systolicBloodPressureUpperLimitNum = it.systolicBloodPressureUpperLimitNum,
                    diastolicBloodPressureLowerLimitNum = it.diastolicBloodPressureLowerLimitNum,
                    lowBloodPressureNum = it.lowBloodPressureNum,
                    isBloodPressureAvgStandard = it.isBloodPressureAvgStandard,
                    createdAt = it.createdAt,
                    patientId = report.patientId,
                    patientName = report.patientName,
                    patientAge = report.age,
                    reportName = report.reportName,
                    reportStartDatetime = report.reportStartDatetime,
                    reportEndDatetime = report.reportEndDatetime,
                    abnormalDataAlertMsgList = this.splitAbnormalDataAlertMsg(it.abnormalDataAlertMsg),
                    actualMeasureNumberDeviationValue = it.actualMeasureNumberDeviationValue,
                    standardMeasureNumberDeviationValue = it.standardMeasureNumberDeviationValue,
                    systolicBloodPressureAvgDeviationValue = it.systolicBloodPressureAvgDeviationValue,
                    diastolicBloodPressureAvgDeviationValue = it.diastolicBloodPressureAvgDeviationValue,
                    systolicBloodPressureStandardDeviationValue = it.systolicBloodPressureStandardDeviationValue,
                    diastolicBloodPressureStandardDeviationValue = it.diastolicBloodPressureStandardDeviationValue,
                    scoreDeviationValue = it.scoreDeviationValue,
                    standardRateDeviationValue = it.standardRateDeviationValue,
                    reportScore = report.reportScore,

                    )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report-statistics.not-found"))
    }

    override fun getAcuteCoronaryDiseaseStageStatisticsDetailList(body: BigInteger): List<AcuteCoronaryDiseaseStageStatisticsDetail> {
        return hsAcuteCoronaryDiseaseStageStatisticsDetailTable.select()
            .where(HsAcuteCoronaryDiseaseStageStatisticsDetailTable.StageReportId eq body)
            .find()
            .map {
                AcuteCoronaryDiseaseStageStatisticsDetail(
                    it.id,
                    it.stageReportId,
                    it.systolicBloodPressure,
                    it.diastolicBloodPressure,
                    it.measureDatetime,
                    it.createdAt
                )
            }
            .sortedBy { it.measureDatetime }
    }

    override fun getCerebralStrokeStageStatistics(body: BigInteger): CerebralStrokeStageStatisticsResponse {
        val report = hsStageReportTable.findById(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report.not-found"))

        return hsCerebralStrokeStageStatisticsTable.select()
            .where(HsCerebralStrokeStageStatisticsTable.StageReportId eq body)
            .findOne()
            ?.let {
                CerebralStrokeStageStatisticsResponse(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    mrsScore = it.mrsScore,
                    barthelScore = it.barthelScore,
                    eq5dScore = it.eq5dScore,
                    createdAt = it.createdAt,
                    patientId = report.patientId,
                    patientName = report.patientName,
                    patientAge = report.age,
                    reportName = report.reportName,
                    reportStartDatetime = report.reportStartDatetime,
                    reportEndDatetime = report.reportEndDatetime,
                    abnormalDataAlertMsgList = this.splitAbnormalDataAlertMsg(it.abnormalDataAlertMsg),
                    mrsScoreDeviationValue = it.mrsScoreDeviationValue,
                    barthelScoreDeviationValue = it.barthelScoreDeviationValue,
                    eq5dScoreDeviationValue = it.eq5dScoreDeviationValue,
                    scoreDeviationValue = it.scoreDeviationValue,
                    reportScore = report.reportScore,
                )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report-statistics.not-found"))
    }

    override fun getCerebralStrokeStageStatisticsDetailList(body: BigInteger): List<StageStatisticsDetail> {
        return hsCerebralStrokeStageStatisticsDetailTable.select()
            .where(HsCerebralStrokeStageStatisticsDetailTable.StageReportId eq body)
            .find()
            .map {
                StageStatisticsDetail(
                    it.id,
                    it.stageReportId,
                    it.systolicBloodPressure,
                    it.diastolicBloodPressure,
                    it.isSystolicBloodPressureMax,
                    it.isDiastolicBloodPressureMax,
                    it.measureDatetime,
                    it.createdAt
                )
            }
            .sortedBy { it.measureDatetime }
    }

    override fun getCopdStageStatistics(body: BigInteger): CopdStageStatisticsResponse {
        val report = hsStageReportTable.findById(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report.not-found"))

        return hsCopdStageStatisticsTable.select()
            .where(HsCopdStageStatisticsTable.StageReportId eq body)
            .findOne()
            ?.let {
                CopdStageStatisticsResponse(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    createdAt = it.createdAt,
                    patientId = report.patientId,
                    patientName = report.patientName,
                    patientAge = report.age,
                    reportName = report.reportName,
                    reportStartDatetime = report.reportStartDatetime,
                    reportEndDatetime = report.reportEndDatetime,
                    abnormalDataAlertMsgList = this.splitAbnormalDataAlertMsg(it.abnormalDataAlertMsg),
                    scoreDeviationValue = it.scoreDeviationValue,
                    reportScore = report.reportScore,
                )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report-statistics.not-found"))
    }

    override fun getCopdStatisticsDetailList(body: BigInteger): List<CopdStatisticsDetail> {
        return hsCopdStageStatisticsDetailTable.select()
            .where(HsCopdStageStatisticsDetailTable.StageReportId eq body)
            .find()
            .map {
                CopdStatisticsDetail(
                    it.id,
                    it.stageReportId,
                    it.pulseOxygenSaturation,
                    it.measureDatetime,
                    it.createdAt
                )
            }
            .sortedBy { it.measureDatetime }
    }

    override fun getDiabetesStageStatistics(body: BigInteger): DiabetesStageStatisticsResponse {
        val report = hsStageReportTable.findById(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report.not-found"))

        return hsDiabetesStageStatisticsTable.select()
            .where(HsDiabetesStageStatisticsTable.StageReportId eq body)
            .findOne()
            ?.let {
                DiabetesStageStatisticsResponse(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    fastingBloodSugarMeasureNumber = it.fastingBloodSugarMeasureNumber,
                    beforeLunchBloodSugarMeasureNumber = it.beforeLunchBloodSugarMeasureNumber,
                    afterMealBloodSugarBloodSugarMeasureNumber = it.afterMealBloodSugarBloodSugarMeasureNumber,
                    lowBloodSugarNumber = it.lowBloodSugarNumber,
                    bloodSugarStandardRate = it.bloodSugarStandardRate,
                    createdAt = it.createdAt,
                    patientId = report.patientId,
                    patientName = report.patientName,
                    patientAge = report.age,
                    reportName = report.reportName,
                    reportStartDatetime = report.reportStartDatetime,
                    reportEndDatetime = report.reportEndDatetime,
                    abnormalDataAlertMsgList = this.splitAbnormalDataAlertMsg(it.abnormalDataAlertMsg),
                    bloodSugarStandardRateDeviationValue = it.bloodSugarStandardRateDeviationValue,
                    scoreDeviationValue = it.scoreDeviationValue,
                    reportScore = report.reportScore,
                )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report-statistics.not-found"))
    }

    override fun getDiabetesStageStatisticsDetailList(body: BigInteger): List<DiabetesStageStatisticsDetail> {
        return hsDiabetesStageStatisticsDetailTable.select()
            .where(HsDiabetesStageStatisticsDetailTable.StageReportId eq body)
            .find()
            .map {
                DiabetesStageStatisticsDetail(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    fastingBloodSugar = it.fastingBloodSugar,
                    randomBloodSugar = it.randomBloodSugar,
                    afterMealBloodSugar = it.afterMealBloodSugar,
                    beforeLunchBloodSugar = it.beforeLunchBloodSugar,
                    beforeDinnerBloodSugar = it.beforeDinnerBloodSugar,
                    afterLunchBloodSugar = it.afterLunchBloodSugar,
                    afterDinnerBloodSugar = it.afterDinnerBloodSugar,
                    beforeSleepBloodSugar = it.beforeSleepBloodSugar,
                    measureDatetime = it.measureDatetime,
                    createdAt = it.createdAt
                )
            }
            .sortedBy { it.measureDatetime }
    }

    override fun getReportPage(reportPageRequest: ReportPageRequest): PagedResult<StageReport> {

        //健康方案类型与阶段报告类型能对应上，可以使用健康方案类型查询阶段报告，综合健康方案无阶段报告
        val page = hsStageReportDao.findReportPage(
            reportPageRequest.patientId,
            reportPageRequest.healthManageType?.name,
            reportPageRequest.pageSize,
            reportPageRequest.pageNo
        )

        //提醒查看阶段报告计划打卡
        taskExecutor.execute {
            //打卡
            clockInService.saveClockIn(
                reportPageRequest.patientId,
                HealthPlanType.REMINDER_VIEW_REPORT,
                LocalDateTime.now()
            )
            //根据类型删除创建计划
            healthManageFacadeService.delHealthPlanByPatientIdAndTypes(
                DelHealthPlanByPatientIdAndTypes(
                    patientId = reportPageRequest.patientId,
                    types = listOf(HealthPlanType.REMINDER_VIEW_REPORT),
                )
            )

        }

        return PagedResult.fromDbPaged(page) {
            StageReport(
                it.id,
                it.healthSchemeManagementInfoId,
                it.patientId,
                it.patientName,
                it.age,
                it.reportName,
                it.reportStartDatetime,
                it.reportEndDatetime,
                it.createdAt,
                StageReportType.valueOf(it.stageReportType),
                it.reportScore,
                it.failMsg
            )
        }
    }

    override fun getStageStatistics(body: BigInteger): StageStatisticsResponse {
        val report = hsStageReportTable.findById(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report.not-found"))

        return hsStageStatisticsTable.select()
            .where(HsStageStatisticsTable.StageReportId eq body)
            .findOne()
            ?.let {
                StageStatisticsResponse(
                    id = it.id,
                    stageReportId = it.stageReportId,
                    actualMeasureNumber = it.actualMeasureNumber,
                    standardMeasureNumber = it.standardMeasureNumber,
                    systolicBloodPressureAvg = it.systolicBloodPressureAvg,
                    diastolicBloodPressureAvg = it.diastolicBloodPressureAvg,
                    systolicBloodPressureStandardDeviation = it.systolicBloodPressureStandardDeviation,
                    diastolicBloodPressureStandardDeviation = it.diastolicBloodPressureStandardDeviation,
                    bloodPressureFillRate = it.bloodPressureFillRate,
                    bloodPressureStandardRate = it.bloodPressureStandardRate,
                    systolicBloodPressureUpperLimitNum = it.systolicBloodPressureUpperLimitNum,
                    diastolicBloodPressureLowerLimitNum = it.diastolicBloodPressureLowerLimitNum,
                    lowBloodPressureNum = it.lowBloodPressureNum,
                    isBloodPressureAvgStandard = it.isBloodPressureAvgStandard,
                    createdAt = it.createdAt,
                    patientId = report.patientId,
                    patientName = report.patientName,
                    patientAge = report.age,
                    reportName = report.reportName,
                    reportStartDatetime = report.reportStartDatetime,
                    reportEndDatetime = report.reportEndDatetime,
                    abnormalDataAlertMsgList = this.splitAbnormalDataAlertMsg(it.abnormalDataAlertMsg),
                    actualMeasureNumberDeviationValue = it.actualMeasureNumberDeviationValue,
                    standardMeasureNumberDeviationValue = it.standardMeasureNumberDeviationValue,
                    systolicBloodPressureAvgDeviationValue = it.systolicBloodPressureAvgDeviationValue,
                    diastolicBloodPressureAvgDeviationValue = it.diastolicBloodPressureAvgDeviationValue,
                    systolicBloodPressureStandardDeviationValue = it.systolicBloodPressureStandardDeviationValue,
                    diastolicBloodPressureStandardDeviationValue = it.diastolicBloodPressureStandardDeviationValue,
                    scoreDeviationValue = it.scoreDeviationValue,
                    standardRateDeviationValue = it.standardRateDeviationValue,
                    reportScore = report.reportScore,

                    )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("stage-report-statistics.not-found"))
    }

    override fun getStageStatisticsDetailList(body: BigInteger): List<StageStatisticsDetail> {
        return hsStageReportStatisticsDetailTable.select()
            .where(HsStageStatisticsDetailTable.StageReportId eq body)
            .find()
            .map {
                StageStatisticsDetail(
                    it.id,
                    it.stageReportId,
                    it.systolicBloodPressure,
                    it.diastolicBloodPressure,
                    it.isSystolicBloodPressureMax,
                    it.isDiastolicBloodPressureMax,
                    it.measureDatetime,
                    it.createdAt
                )
            }
            .sortedBy { it.measureDatetime }
    }

    private fun splitAbnormalDataAlertMsg(abnormalDataAlertMsg: String?): List<AbnormalDataAlertMsgResult> {
        return try {
            abnormalDataAlertMsgToList(abnormalDataAlertMsg).map { AbnormalDataAlertMsgResult(it.title, it.content) }
        } catch (e: Exception) {
            LOGGER.error("异常数据提醒消息转换对象失败，不符合JSON格式", e);
            emptyList()
        }
    }
}
