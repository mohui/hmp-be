package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.BARTHEL_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.constant.EQ5D_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.constant.MRS_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.service.CerebralStrokeReport
import com.bjknrt.health.scheme.service.CerebralStrokeReportService
import com.bjknrt.health.scheme.service.VisitService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageCerebralStrokeServiceImpl
import com.bjknrt.health.scheme.util.*
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.StageReportType
import com.bjknrt.question.answering.system.api.AnswerHistoryApi
import com.bjknrt.question.answering.system.vo.LastAnswerRecord
import com.bjknrt.question.answering.system.vo.LastAnswerRecordListRequest
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class CerebralStrokeReportServiceImpl(
    val questionsAnswerClient: AnswerHistoryApi,
    val hsStageReportTable: HsStageReportTable,
    val hsCerebralStrokeStageStatisticsDetailTable: HsCerebralStrokeStageStatisticsDetailTable,
    val hsCerebralStrokeStageStatisticsTable: HsCerebralStrokeStageStatisticsTable,
    val healthSchemeManageService: HealthSchemeManageService,
    val visitService: VisitService,
    val healthManageService: HealthManageCerebralStrokeServiceImpl
) : CerebralStrokeReportService {

    companion object {
        const val CEREBRAL_STROKE_BEHAVIOR_EXAMINATION_PAPER_CODE = "CEREBRAL_STROKE_BEHAVIOR"
        const val REPORT_FAIL_MSG_NOT_BEHAVIOR = "阶段内行为习惯随访未完成"
        const val REPORT_FAIL_MSG_NOT_CEREBRAL_STROKE = "阶段内脑卒中随访未完成"
        const val BEFORE_DAY_30 = -30L
        const val IS_FALL_MSG_TITLE = "您本阶段内出现过跌倒"
        const val IS_FALL_MSG_CONTENT =
            "由于肌肉力量减退、关节灵活性降低等原因，脑卒中后容易出现跌倒，这会严重影响您的生活质量。建议您前往医院检查是否伴随其他损伤，同时建议您积极进行康复。"
        const val REGULAR_MEDICATION_MSG_TITLE = "您在本阶段内服药不规律"
        const val REGULAR_MEDICATION_MSG_CONTENT =
            "擅自停药或长期服药不规律很可能会导致您的病情不稳定，因此建议您严格按照医生的处方规律服药。"
    }

    @Transactional
    override fun generateReport(cerebralStrokeReport: CerebralStrokeReport) {
        //生成提醒查看报告的计划
        healthManageService.addReminderViewReportPlan(
            patientId = cerebralStrokeReport.patientId,
            healthManageId = cerebralStrokeReport.healthManageId,
        )

        //脑卒中随访
        val cerebralStrokeVisit = this.getCerebralStrokeVisit(
            cerebralStrokeReport.patientId,
            cerebralStrokeReport.startDateTime,
            cerebralStrokeReport.endDateTime
        )
        if (cerebralStrokeVisit == null) {
            this.saveStageReport(cerebralStrokeReport, null, REPORT_FAIL_MSG_NOT_CEREBRAL_STROKE)
            return
        }

        // 行为习惯评估记录
        val behaviorAnswerRecord = this.getLastEvaluateRecord(
            CEREBRAL_STROKE_BEHAVIOR_EXAMINATION_PAPER_CODE,
            cerebralStrokeReport.patientId,
            cerebralStrokeReport.startDateTime,
            cerebralStrokeReport.endDateTime
        )
        if (behaviorAnswerRecord == null) {
            this.saveStageReport(cerebralStrokeReport, null, REPORT_FAIL_MSG_NOT_BEHAVIOR)
            return
        }

        //异常数据提醒消息
        val abnormalDataAlertMsgList = abnormalDataAlertMsgToList(behaviorAnswerRecord.resultsMsg)

        //行为习惯随访得分
        val behaviorScore = behaviorAnswerRecord.totalScore ?: BigDecimal.ZERO
        //其他系统计算得分
        val otherScore = this.getOtherScore(
            cerebralStrokeReport,
            cerebralStrokeReport.healthManageId,
            abnormalDataAlertMsgList
        ).toBigDecimal()
        //报告总得分
        val score = behaviorScore + otherScore
        val totalScore = if (score < BigDecimal.ZERO) BigDecimal.ZERO else score
        // 2.生成报告主表
        val report = this.saveStageReport(cerebralStrokeReport, totalScore)

        // 3.保存统计数据
        this.saveStatistics(
            cerebralStrokeReport,
            totalScore,
            report,
            abnormalDataAlertMsgListToString(abnormalDataAlertMsgList)
        )
    }

    /**
     * 查询脑卒中随访
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     */
    private fun getCerebralStrokeVisit(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): HsCerebralStrokeVisit? {
        return visitService.getCerebralStrokeVisitList(patientId, startDateTime, endDateTime).firstOrNull()
    }

    /**
     * 保存报告主表数据
     * @param cerebralStrokeReport 报告生成请求参数
     * @param score 得分
     * @param failMsg 失败原因
     * @return 报告对象
     */
    private fun saveStageReport(
        cerebralStrokeReport: CerebralStrokeReport,
        score: BigDecimal?,
        failMsg: String? = null
    ): HsStageReport {
        val reportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(reportId)
            .setHealthSchemeManagementInfoId(cerebralStrokeReport.healthManageId)
            .setPatientId(cerebralStrokeReport.patientId)
            .setPatientName(cerebralStrokeReport.patientName)
            .setReportName(cerebralStrokeReport.reportName)
            .setReportStartDatetime(cerebralStrokeReport.startDateTime)
            .setReportEndDatetime(cerebralStrokeReport.endDateTime)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setReportScore(score)
            .setStageReportType(StageReportType.CEREBRAL_STROKE.name)
            .setAge(cerebralStrokeReport.age)
            .setCreatedAt(LocalDateTime.now())
            .setFailMsg(failMsg)
            .build()
        hsStageReportTable.insertWithoutNull(report)
        return report
    }

    /**
     * 保存统计数据
     * @param cerebralStrokeReport 高血压报告生成请求参数
     * @param totalScore 报告得分
     * @param report 阶段报告
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStatistics(
        cerebralStrokeReport: CerebralStrokeReport,
        totalScore: BigDecimal,
        report: HsStageReport,
        abnormalDataAlertMsg: String
    ) {
        //保存明细
        this.saveStatisticsDetail(cerebralStrokeReport.bloodPressureList, report.id)

        //保存统计数据
        this.saveStageReportStatistics(
            cerebralStrokeReport.patientId,
            cerebralStrokeReport.healthManageId,
            cerebralStrokeReport.startDateTime,
            cerebralStrokeReport.endDateTime,
            totalScore,
            report.id,
            abnormalDataAlertMsg
        )
    }

    /**
     * 保存报告统计数据
     * @param patientId 患者Id
     * @param healthManageId 健康方案Id
     * @param startDateTime 报告开始时间
     * @param endDateTime 报告结束时间
     * @param totalScore 报告得分
     * @param reportId 报告Id
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStageReportStatistics(
        patientId: BigInteger,
        healthManageId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        totalScore: BigDecimal,
        reportId: BigInteger,
        abnormalDataAlertMsg: String
    ) {
        //本次mrs评分
        val mrsScore =
            this.getLastEvaluateRecord(
                MRS_EXAMINATION_PAPER_CODE,
                patientId,
                startDateTime,
                endDateTime
            )?.totalScore ?: BigDecimal.ZERO
        //本次barthel评分
        val barthelScore =
            this.getLastEvaluateRecord(
                BARTHEL_EXAMINATION_PAPER_CODE,
                patientId,
                startDateTime,
                endDateTime
            )?.totalScore ?: BigDecimal.ZERO
        //本次eq5d评分
        val eq5dScore =
            this.getLastEvaluateRecord(
                EQ5D_EXAMINATION_PAPER_CODE,
                patientId,
                startDateTime,
                endDateTime
            )?.totalScore ?: BigDecimal.ZERO
        //本次mrs评分与上次mrs评分的差
        var mrsScoreDeviationValue: BigDecimal? = null
        //本次barthel评分与上次barthel评分的差
        var barthelScoreDeviationValue: BigDecimal? = null
        //本次eq5d评分与上次eq5d评分的差
        var eq5dScoreDeviationValue: BigDecimal? = null
        //本次报告得分与上次报告得分的差值
        var scoreDeviationValue: BigDecimal? = null
        // 上次的数据
        this.getLastHealthSchemeManagementInfo(patientId, healthManageId)
            ?.let { lastHealthSchemeManagementInfo ->

                if (HealthManageType.valueOf(lastHealthSchemeManagementInfo.knHealthManageType) == HealthManageType.CEREBRAL_STROKE) {
                    //上次的阶段报告
                    this.getLastStageReport(lastHealthSchemeManagementInfo.knId)
                        ?.let { lastStageReport ->
                            //上次的统计数据
                            this.getLastStageStatistics(lastStageReport.id)?.let { lastStageStatistics ->
                                //本次mrs评分与上次mrs评分的差
                                mrsScoreDeviationValue = mrsScore - lastStageStatistics.mrsScore
                                //本次barthel评分与上次barthel评分的差
                                barthelScoreDeviationValue = barthelScore - lastStageStatistics.barthelScore
                                //本次eq5d评分与上次eq5d评分的差
                                eq5dScoreDeviationValue = eq5dScore - lastStageStatistics.eq5dScore
                            }

                            //本次报告得分与上次报告得分的差值
                            lastStageReport.reportScore?.let {
                                scoreDeviationValue = totalScore - it
                            }
                        }
                }
            }
        val statistics = HsCerebralStrokeStageStatistics.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(reportId)
            .setMrsScore(mrsScore)
            .setBarthelScore(barthelScore)
            .setEq5dScore(eq5dScore)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setAbnormalDataAlertMsg(abnormalDataAlertMsg)
            .setBarthelScoreDeviationValue(barthelScoreDeviationValue)
            .setScoreDeviationValue(scoreDeviationValue)
            .setMrsScoreDeviationValue(mrsScoreDeviationValue)
            .setEq5dScoreDeviationValue(eq5dScoreDeviationValue)
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsCerebralStrokeStageStatisticsTable.insertWithoutNull(statistics)
    }

    /**
     * 获取问卷得分
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return  问卷记录
     */
    private fun getLastEvaluateRecord(
        code: String,
        patientId: BigInteger,
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
    ): LastAnswerRecord? {
        return questionsAnswerClient.getLastAnswerRecordList(
            LastAnswerRecordListRequest(
                examinationPaperCode = code,
                answerBy = patientId,
                needNum = 1,
                startDate = startDateTime,
                endDate = endDateTime
            )
        ).firstOrNull()
    }


    /**
     * 获取上次阶段统计数据
     * @param id 阶段报告Id
     * @return 上次阶段统计数据
     */
    private fun getLastStageStatistics(id: BigInteger): HsCerebralStrokeStageStatistics? {
        return hsCerebralStrokeStageStatisticsTable.select()
            .where(HsCerebralStrokeStageStatisticsTable.StageReportId eq id)
            .findOne()
    }


    /**
     * 获取上次阶段报告数据
     * @param healthManageId 健康方案管理信息id
     * @return  上次阶段报告数据
     */
    private fun getLastStageReport(healthManageId: BigInteger): HsStageReport? {
        return hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq healthManageId)
            .findOne()
    }

    /**
     * 获取上次健康方案管理信息
     * @param patientId 患者Id
     * @param healthManageId 患者Id
     * @return  上次健康方案管理信息
     */
    private fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        healthManageId: BigInteger
    ): HsHealthSchemeManagementInfo? {
        return healthSchemeManageService.getLastHealthSchemeManagementInfo(patientId, healthManageId)
    }

    /**
     * 保存报告统计明细数据
     * @param bloodPressureList 血压集合
     * @param reportId 报告Id
     */
    private fun saveStatisticsDetail(bloodPressureList: List<BloodPressure>, reportId: BigInteger) {
        //血压数据为空时不保存
        if (bloodPressureList.isNotEmpty()) {
            val diastolicBloodPressureMax = bloodPressureList.maxOf { it.diastolicBloodPressure }
            val systolicBloodPressureMax = bloodPressureList.maxOf { it.systolicBloodPressure }

            for (bloodPressure in bloodPressureList) {
                val diastolicValue = bloodPressure.diastolicBloodPressure
                val systolicValue = bloodPressure.systolicBloodPressure
                val measureDatetime = bloodPressure.measureDatetime
                val detail = HsCerebralStrokeStageStatisticsDetail.builder().setId(AppIdUtil.nextId())
                    .setStageReportId(reportId)
                    .setSystolicBloodPressure(systolicValue)
                    .setDiastolicBloodPressure(diastolicValue)
                    .setIsSystolicBloodPressureMax(systolicValue.toDouble() == systolicBloodPressureMax.toDouble())
                    .setIsDiastolicBloodPressureMax(diastolicValue.toDouble() == diastolicBloodPressureMax.toDouble())
                    .setMeasureDatetime(measureDatetime)
                    .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                    .setCreatedAt(LocalDateTime.now())
                    .build()
                hsCerebralStrokeStageStatisticsDetailTable.insertWithoutNull(detail)
            }
        }
    }


    /**
     * 计算系统得分
     * @param cerebralStrokeReport 报告生成参数
     * @param healthManageId 健康方案Id
     * @param abnormalDataAlertMsgList 异常数据提醒消息
     */
    private fun getOtherScore(
        cerebralStrokeReport: CerebralStrokeReport,
        healthManageId: BigInteger,
        abnormalDataAlertMsgList: MutableList<AbnormalDataAlertMsg>
    ): Int {
        val patientId = cerebralStrokeReport.patientId
        val startDateTime = cerebralStrokeReport.startDateTime
        val endDateTime = cerebralStrokeReport.endDateTime


        //血压值的达标率
        val bloodPressureStrandRateScore =
            this.getBloodPressureStrandRateScore(cerebralStrokeReport.age, cerebralStrokeReport.bloodPressureList)

        //mRS评分本次与上次mRS评分相比
        val currentMrsScore = this.getLastEvaluateRecord(
            MRS_EXAMINATION_PAPER_CODE,
            patientId,
            startDateTime,
            endDateTime
        )?.totalScore
        val lastMrsScore = this.getLastEvaluateRecord(
            MRS_EXAMINATION_PAPER_CODE,
            patientId,
            null,
            startDateTime.plusDays(BEFORE_DAY_30)
        )?.totalScore
        val mrsScore = this.getMrsScore(currentMrsScore, lastMrsScore)

        //barthel评分本次与上次barthel评分相比
        val currentBarthelScore = this.getLastEvaluateRecord(
            BARTHEL_EXAMINATION_PAPER_CODE,
            patientId,
            startDateTime,
            endDateTime
        )?.totalScore
        val lastBarthelScoreScore = this.getLastEvaluateRecord(
            BARTHEL_EXAMINATION_PAPER_CODE,
            patientId,
            null,
            startDateTime.plusDays(BEFORE_DAY_30)
        )?.totalScore
        val barthelScore = this.getBarthelScore(currentBarthelScore, lastBarthelScoreScore)
        //eq5d评分本次与上次eq5d评分相比
        val currentEq5dScore = this.getLastEvaluateRecord(
            EQ5D_EXAMINATION_PAPER_CODE,
            patientId,
            startDateTime,
            endDateTime
        )?.totalScore
        val lastEq5dScoreScore = this.getLastEvaluateRecord(
            EQ5D_EXAMINATION_PAPER_CODE,
            patientId,
            null,
            startDateTime.plusDays(BEFORE_DAY_30)
        )?.totalScore
        val eq5dScore = this.getEq5dScore(currentEq5dScore, lastEq5dScoreScore)

        //常规康复分数
        var normalRecoverScore = 0
        //智能康复分数
        var intelligentRecoverScore = 0
        //疼痛分数
        var painCauseScore = 0
        //跌倒分数
        var fallScore = 0
        //规律服药分数
        var regularMedicationScore = 0
        this.getCerebralStrokeVisit(patientId, startDateTime, endDateTime)?.let { visit ->

            this.getLastHealthSchemeManagementInfo(patientId, healthManageId)?.let { lastHealthManage ->
                if (HealthManageType.valueOf(lastHealthManage.knHealthManageType) == HealthManageType.CEREBRAL_STROKE) {

                    //查询上个健康方案开始时间到本次健康方案开始时间之间的脑卒中随访，找最后一条
                    this.getCerebralStrokeVisit(patientId, lastHealthManage.knStartDate.atStartOfDay(), startDateTime)
                        ?.let { lastVisit ->
                            //常规康复分数
                            normalRecoverScore =
                                this.getNormalRecoverScore(visit.normalRecoverTime, lastVisit.normalRecoverTime)
                            //智能康复分数
                            intelligentRecoverScore = this.getIntelligentRecoverScore(
                                visit.intelligentRecoverTime,
                                lastVisit.intelligentRecoverTime
                            )
                        }
                }
            }

            //疼痛分数
            painCauseScore =
                this.getPainCauseScore(visit.isPainCauseArthrosis || visit.isPainCauseHead || visit.isPainCauseShoulder || visit.isPainCauseNerve)
            //跌倒分数
            fallScore = this.getFallScore(visit.isFall)
            //规律服药分数
            regularMedicationScore = this.getRegularMedicationScore(visit.isRegularMedication)

            //自上次随访以来是否跌倒过,若答案为“是”
            if (visit.isFall) {
                abnormalDataAlertMsgList.add(
                    AbnormalDataAlertMsg(IS_FALL_MSG_TITLE, IS_FALL_MSG_CONTENT)
                )
            }
            //是否遵从医嘱按时规律服药,若答案为“不规律”
            if (!visit.isRegularMedication) {
                abnormalDataAlertMsgList.add(
                    AbnormalDataAlertMsg(REGULAR_MEDICATION_MSG_TITLE, REGULAR_MEDICATION_MSG_CONTENT)
                )
            }
        }

        val otherScore = bloodPressureStrandRateScore + mrsScore + barthelScore + eq5dScore + normalRecoverScore + intelligentRecoverScore + painCauseScore + fallScore + regularMedicationScore

        LOGGER.info(
            "患者:{},方案:{},周期:{}~{}的{},系统计算分值总分值:{},血压达标率得分:{},mRS评分得分:{},barthel评分得分:{},eq5d评分得分:{},常规康复得分:{},智能康复得分:{},疼痛得分:{},跌倒得分:{},规律服药得分:{}",
            cerebralStrokeReport.patientId,
            cerebralStrokeReport.healthManageId,
            cerebralStrokeReport.startDateTime,
            cerebralStrokeReport.endDateTime,
            cerebralStrokeReport.reportName,
            otherScore,
            bloodPressureStrandRateScore,
            mrsScore,
            barthelScore,
            eq5dScore,
            normalRecoverScore,
            intelligentRecoverScore,
            painCauseScore,
            fallScore,
            regularMedicationScore
        )

        return otherScore
    }

    /**
     * 智能康复项目的训练时长与上个阶段最新的随访相比（ 训练时长（h/周））首次无对比，则按照无变化得5分，如本次未随访，缺失对比数据，则按照0分处理
     * 升高（10分）
     * 无变化（5分）
     * 下降（0分）
     */
    private fun getIntelligentRecoverScore(
        currentIntelligentRecoverTime: BigDecimal,
        lastIntelligentRecoverTime: BigDecimal
    ): Int {
        return if (currentIntelligentRecoverTime > lastIntelligentRecoverTime) {
            10
        } else if (currentIntelligentRecoverTime < lastIntelligentRecoverTime) {
            0
        } else {
            5
        }
    }

    /**
     * 常规康复项目的训练时长与上个阶段最新的随访相比（ 训练时长（h/周））首次无对比，则按照无变化得5分，如本次未随访，缺失对比数据，则按照0分处理
     * 升高（10分）
     * 无变化（5分）
     * 下降（0分）
     */
    private fun getNormalRecoverScore(currentNormalRecoverTime: BigDecimal, lastNormalRecoverTime: BigDecimal): Int {
        return if (currentNormalRecoverTime > lastNormalRecoverTime) {
            10
        } else if (currentNormalRecoverTime < lastNormalRecoverTime) {
            0
        } else {
            5
        }
    }

    /**
     * 是否遵从医嘱按时规律服药   来源：脑卒中随访
     * 规律（5分）
     * 不规律（0分）
     */
    private fun getRegularMedicationScore(regularMedication: Boolean): Int {
        return if (regularMedication) 5 else 0
    }


    /**
     * 自上次随访以来是否跌倒过（报告得分不低于0分）
     * 是（-5分）扣5分
     * 否（0分）
     */
    private fun getFallScore(fall: Boolean): Int {
        return if (fall) -5 else 0
    }

    /**
     * 是否存在疼痛？
     * 是（0分）
     * 否（5分）
     */
    private fun getPainCauseScore(painCause: Boolean): Int {
        return if (painCause) 0 else 5
    }

    /**
     * EQ-5D评分本次与上次EQ-5D评分相比（数据有效期：需是距离本次评分至少30天以前中的最近的一次）
     * 升高（10分）
     * 无变化（5分）
     * 下降（0分）
     */
    private fun getEq5dScore(currentEq5dScore: BigDecimal?, lastEq5dScoreScore: BigDecimal?): Int {
        //本次eq5d评分没有得0分
        currentEq5dScore ?: return 0

        //上次eq5d评分没有得0分
        lastEq5dScoreScore ?: return 0

        return if (currentEq5dScore > lastEq5dScoreScore) {
            10
        } else if (currentEq5dScore < lastEq5dScoreScore) {
            0
        } else {
            5
        }

    }

    /**
     * 改良Barthel评分本次与上次改良Barthel评分相比（数据有效期：需是距离本次评分至少30天以前中的最近的一次）
     *   升高（10分）
     *  无变化（5分）
     *  下降（0分）
     */
    private fun getBarthelScore(currentBarthelScore: BigDecimal?, lastBarthelScoreScore: BigDecimal?): Int {
        //本次Barthel评分没有得0分
        currentBarthelScore ?: return 0

        //上次Barthel评分没有得0分
        lastBarthelScoreScore ?: return 0

        return if (currentBarthelScore > lastBarthelScoreScore) {
            10
        } else if (currentBarthelScore < lastBarthelScoreScore) {
            0
        } else {
            5
        }
    }

    /**
     * mRS评分本次与上次mRS评分相比（数据有效期：需是距离本次评分至少30天以前中的最近的一次）
     * 升高（0分）
     * 无变化（5分）
     * 下降（10分）
     */
    private fun getMrsScore(currentMrsScore: BigDecimal?, lastMrsScore: BigDecimal?): Int {
        //本次mRS评分没有得0分
        currentMrsScore ?: return 0

        //上次mRS评分没有得0分
        lastMrsScore ?: return 0

        return if (currentMrsScore > lastMrsScore) {
            0
        } else if (currentMrsScore < lastMrsScore) {
            10
        } else {
            5
        }
    }

    /**
     * 血压值的达标率分值
     * 达标率=0（0分）
     * 0＜达标率≤50%（5分）
     * 达标率＞50%（10分）
     * @param age 年龄
     * @param bloodPressureList 血压数据
     */
    private fun getBloodPressureStrandRateScore(age: Int, bloodPressureList: List<BloodPressure>): Int {
        val strandNumber = getBloodPressureStandardNumber(
            age,
            bloodPressureList.map { BloodPressureValue(it.systolicBloodPressure, it.diastolicBloodPressure) }
        )

        return when (getRate(strandNumber, bloodPressureList.size).toInt()) {
            in 0..0 -> 0
            in 1..50 -> 5
            else -> 10
        }
    }
}
