package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.bjknrt.health.scheme.service.CopdReport
import com.bjknrt.health.scheme.service.CopdReportService
import com.bjknrt.health.scheme.service.PulseOxygenSaturation
import com.bjknrt.health.scheme.service.VisitService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageCopdServiceImpl
import com.bjknrt.health.scheme.util.ClockInRateUtils
import com.bjknrt.health.scheme.util.abnormalDataAlertMsgListToString
import com.bjknrt.health.scheme.util.abnormalDataAlertMsgToList
import com.bjknrt.health.scheme.vo.DrugCompliance
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.StageReportType
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.HealthPlanMain
import com.bjknrt.medication.remind.vo.HealthPlanType
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
import java.time.temporal.ChronoUnit

@Service
class CopdReportServiceImpl(
    val questionsAnswerClient: AnswerHistoryApi,
    val hsStageReportTable: HsStageReportTable,
    val hsCopdStageStatisticsTable: HsCopdStageStatisticsTable,
    val hsCopdStageStatisticsDetailTable: HsCopdStageStatisticsDetailTable,
    val visitService: VisitService,
    val healthPlanClient: HealthPlanApi,
    val clockInRateUtils: ClockInRateUtils,
    val healthSchemeManageService: HealthSchemeManageService,
    val healthManageService: HealthManageCopdServiceImpl
) : CopdReportService {
    companion object {
        //慢阻肺行为习惯随访
        const val COPD_BEHAVIOR_EXAMINATION_PAPER_CODE = "COPD_BEHAVIOR"

        const val REPORT_FAIL_MSG_NOT_VISIT = "阶段内慢阻肺线上随访未完成"
        const val REPORT_FAIL_MSG_NOT_BEHAVIOR = "阶段内行为习惯随访未完成"

        const val RESPIRATORY_MSG_TITLE = "您本阶段内进行呼吸操训练的次数较少"
        const val RESPIRATORY_MSG_CONTENT =
            "呼吸操可以增加慢阻肺患者的呼吸肌耐力和力量，改善呼吸症状。建议您多了解呼吸训练的相关知识，努力做到每周至少2次、至少维持8周"

        const val DIET_MSG_TITLE = "您本阶段内的饮食管理不佳"
        const val DIET_MSG_CONTENT =
            "慢阻肺患者容易出现营养不良，因此我们建议您合理膳食、保持营养均衡，BMI尽量保持在18.5~23.9 kg/m²之间。"


    }

    @Transactional
    override fun generateReport(copdReport: CopdReport) {
        //生成提醒查看报告的计划
        healthManageService.addReminderViewReportPlan(
            patientId = copdReport.patientId,
            healthManageId = copdReport.healthManageId,
        )

        // 慢阻肺线上随访
        val copdVisitList = visitService.getCopdVisitList(
            copdReport.patientId,
            copdReport.startDateTime,
            copdReport.endDateTime
        )
        val copdVisit = copdVisitList.firstOrNull()
        //阶段内慢阻肺线上随访未完成
        if (copdVisit == null) {
            this.saveStageReport(copdReport, null, REPORT_FAIL_MSG_NOT_VISIT)
            return
        }

        // 行为习惯评估记录
        val behaviorAnswerRecord = this.getLastEvaluateRecord(
            COPD_BEHAVIOR_EXAMINATION_PAPER_CODE,
            copdReport.patientId,
            copdReport.startDateTime,
            copdReport.endDateTime
        )
        //阶段内行为习惯评估未完成
        if (behaviorAnswerRecord == null) {
            this.saveStageReport(copdReport, null, REPORT_FAIL_MSG_NOT_BEHAVIOR)
            return
        }

        //异常数据提醒消息
        val abnormalDataAlertMsgList = abnormalDataAlertMsgToList(behaviorAnswerRecord.resultsMsg)
        //行为习惯得分
        val behaviorScore = behaviorAnswerRecord.totalScore ?: BigDecimal.ZERO
        //其他系统计算得分
        val otherScore = getOtherScore(copdReport, copdVisitList, abnormalDataAlertMsgList).toBigDecimal()
        //报告总得分
        val totalScore = behaviorScore + otherScore
        //生成报告主表
        val report = this.saveStageReport(copdReport, totalScore)
        //保存统计数据
        this.saveStatistics(copdReport, totalScore, report, abnormalDataAlertMsgListToString(abnormalDataAlertMsgList))
    }


    /**
     * 保存统计数据
     * @param copdReport 高血压报告生成请求参数
     * @param totalScore 报告得分
     * @param report 阶段报告
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStatistics(
        copdReport: CopdReport,
        totalScore: BigDecimal,
        report: HsStageReport,
        abnormalDataAlertMsg: String
    ) {
        val pulseOxygenSaturationList = copdReport.pulseOxygenSaturationList

        //保存明细
        this.saveStatisticsDetail(pulseOxygenSaturationList, report.id)

        //保存统计数据
        this.saveStageReportStatistics(
            copdReport.patientId,
            copdReport.healthManageId,
            totalScore,
            report.id,
            abnormalDataAlertMsg
        )
    }

    /**
     * 保存统计数据
     * @param patientId 患者Id
     * @param healthManageId 健康方案Id
     * @param totalScore 报告得分
     * @param reportId 报告Id
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStageReportStatistics(
        patientId: BigInteger,
        healthManageId: BigInteger,
        totalScore: BigDecimal,
        reportId: BigInteger,
        abnormalDataAlertMsg: String
    ) {

        //本次报告得分与上次报告得分的差值
        var scoreDeviationValue: BigDecimal? = null
        // 上次的数据
        this.getLastHealthSchemeManagementInfo(patientId, healthManageId)
            ?.let { lastHealthSchemeManagementInfo ->

                if (HealthManageType.valueOf(lastHealthSchemeManagementInfo.knHealthManageType) == HealthManageType.COPD) {
                    //上次的阶段报告
                    val lastStageReport: HsStageReport? = this.getLastStageReport(lastHealthSchemeManagementInfo.knId)

                    //本次报告得分与上次报告得分的差值
                    lastStageReport?.reportScore?.let {
                        scoreDeviationValue = totalScore - it
                    }
                }
            }

        hsCopdStageStatisticsTable.save(
            HsCopdStageStatistics.builder()
                .setId(AppIdUtil.nextId())
                .setStageReportId(reportId)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setCreatedAt(LocalDateTime.now())
                .setAbnormalDataAlertMsg(abnormalDataAlertMsg)
                .setScoreDeviationValue(scoreDeviationValue)
                .build()
        )

    }


    /**
     * 获取上次阶段报告数据
     * @param healthSchemeManagementInfoId 健康方案管理信息id
     * @return  上次阶段报告数据
     */
    private fun getLastStageReport(healthSchemeManagementInfoId: BigInteger): HsStageReport? {
        return hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq healthSchemeManagementInfoId)
            .findOne()
    }

    /**
     * 获取上次健康方案管理信息
     * @param patientId 患者Id
     * @param healthSchemeManagementInfoId 健康方案Id
     * @return  上次健康方案管理信息
     */
    private fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        healthSchemeManagementInfoId: BigInteger
    ): HsHealthSchemeManagementInfo? {
        return healthSchemeManageService.getLastHealthSchemeManagementInfo(patientId, healthSchemeManagementInfoId)
    }

    /**
     * 保存统计明细数据
     * @param pulseOxygenSaturationList 脉搏氧饱和度集合
     * @param reportId 报告Id
     */
    private fun saveStatisticsDetail(pulseOxygenSaturationList: List<PulseOxygenSaturation>, reportId: BigInteger) {
        pulseOxygenSaturationList.map {
            HsCopdStageStatisticsDetail.builder()
                .setId(AppIdUtil.nextId())
                .setStageReportId(reportId)
                .setPulseOxygenSaturation(it.pulseOxygenSaturation)
                .setMeasureDatetime(it.measureDatetime)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setCreatedAt(LocalDateTime.now())
                .build()
        }.forEach {
            hsCopdStageStatisticsDetailTable.save(it)
        }
    }

    /**
     * 慢阻肺计算系统得分
     * @param copdReport 报告生成参数
     * @param visitList 慢阻肺随访数据
     * @param abnormalDataAlertMsgList 异常数据提醒消息
     */
    private fun getOtherScore(
        copdReport: CopdReport,
        visitList: List<HsCopdVisit>,
        abnormalDataAlertMsgList: MutableList<AbnormalDataAlertMsg>
    ): Int {
        val patientId = copdReport.patientId

        // 上阶段慢阻肺随访记录
        val lastHealthManage = this.getLastHealthSchemeManagementInfo(patientId, copdReport.healthManageId)
        var lastVisitList = listOf<HsCopdVisit>()
        if (
            lastHealthManage != null
            && HealthManageType.valueOf(lastHealthManage.knHealthManageType) == HealthManageType.COPD
            && copdReport.startDateTime.isAfter(lastHealthManage.knStartDate.atStartOfDay())
            )
        {
            lastVisitList = visitService.getCopdVisitList(
                patientId,
                copdReport.startDateTime.minusMonths(COPD_INTERVAL_MONTH),
                copdReport.startDateTime
            )

        }

        // 吸烟得分
        val lastSmokingNum = lastVisitList.firstOrNull()?.lifeCigarettesPerDay ?: 0
        val currentSmokingNum = visitList.firstOrNull()?.lifeCigarettesPerDay ?: 0
        val smokingScore = this.getSmokingScore(lastSmokingNum, currentSmokingNum)

        // 是否按照医嘱规律服药得分
        var regularMedicationScore = 0
        // 症状得分
        var symptomScore = 0
        if (visitList.isNotEmpty()) {
            regularMedicationScore = this.getRegularMedicationScore(visitList)

            symptomScore = this.getSymptomScore(visitList)
        }

        // 饮食打卡率得分
        var dietPunchRateScore = 0

        // 呼吸操打卡率得分
        var respiratoryRateScore = 0

        // 有氧运动打卡率得分
        var cardioPunchRateScore = 0

        // 抗阻运动打卡率得分
        var resistanceExerciseRateScore = 0

        // 每日科普打卡率得分
        var popularScienceRateScore = 0

        val planFrequencyValueList = copdReport.planFrequencyValue
        val startDateTime = copdReport.startDateTime
        val endDateTime = copdReport.endDateTime
        if (planFrequencyValueList.isNotEmpty()) {
            //健康计划Id与频率Id对应Map
            val planFrequencyIdMap = planFrequencyValueList.associate { it.planId to it.frequencyId }
            //健康计划
            val groupPlanMap = healthPlanClient.idGetList(planFrequencyValueList.map { it.planId })
                .filter { it.type == HealthPlanType.DIET_PLAN || it.type == HealthPlanType.EXERCISE_PROGRAM || it.type == HealthPlanType.SCIENCE_POPULARIZATION_PLAN }
                .groupBy { it.group ?: "" }


            val betweenWeeks = 4
            //饮食期望打卡次数
            val dietExpectedClockInNumber = betweenWeeks
            //呼吸操期望打卡次数
            val respiratoryExpectedClockInNumber = betweenWeeks * 2

            //有氧运动打卡标准次数
            val clockInAerobicExerciseMap = mapOf(
                "步行" to betweenWeeks * 5,
                "慢跑" to betweenWeeks * 5,
                "骑自行车" to betweenWeeks * 5,
                "球类（乒乓球、羽毛球、网球）" to betweenWeeks * 5,
                "健身操" to betweenWeeks * 5,
                "游泳" to betweenWeeks * 3,
            )

            //抗阻运动打卡标准次数
            val clockInResistanceExerciseMap = mapOf(
                "握手训练" to betweenWeeks * 2,
            )

            // 饮食打卡率得分
            dietPunchRateScore = clockInRateUtils.getDietPunchRateScore(
                this.getPlanList(groupPlanMap, DIET).map { it.id },
                dietExpectedClockInNumber,
                startDateTime,
                endDateTime
            )

            // 呼吸操打卡率得分
            respiratoryRateScore = clockInRateUtils.getRespiratoryRateScore(
                this.getPlanList(groupPlanMap, BREATHING_EXERCISE).map { it.id },
                respiratoryExpectedClockInNumber,
                startDateTime,
                endDateTime
            )

            // 有氧运动打卡率得分
            var planList = this.getPlanList(groupPlanMap, AEROBIC_EXERCISE)
            val aerobicExpectedClockInNumber = getExpectedClockInNumber(
                clockInAerobicExerciseMap,
                planList
            )
            cardioPunchRateScore = clockInRateUtils.getCardioPunchRateScore(
                planList.map { it.id },
                aerobicExpectedClockInNumber,
                startDateTime,
                endDateTime
            )

            // 抗阻运动打卡率得分
            planList = this.getPlanList(groupPlanMap, RESISTANCE_EXERCISE)
            val resistanceExpectedClockInNumber =
                getExpectedClockInNumber(
                    clockInResistanceExerciseMap,
                    planList
                )
            resistanceExerciseRateScore = clockInRateUtils.getResistanceExerciseRateScore(
                planList.map { it.id },
                resistanceExpectedClockInNumber,
                startDateTime,
                endDateTime
            )

            //每日一科普打卡率得分
            planList = this.getPlanList(groupPlanMap, SCIENCE_POPULARIZATION)
            popularScienceRateScore = clockInRateUtils.getPopularScienceRateScore(
                planList.map { it.id },
                ChronoUnit.DAYS.between(copdReport.startDateTime, copdReport.endDateTime).toInt(),
                startDateTime,
                endDateTime
            )
        }

        // 饮食打卡率选择“打卡率 ＜ 25%”，饮食打卡率得分为0,则添加正文
        if (dietPunchRateScore == 0) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(
                    DIET_MSG_TITLE,
                    DIET_MSG_CONTENT
                )
            )
        }
        // 呼吸操打卡率 ＜ 25%，呼吸操打卡率得分为0,则添加正文
        if (respiratoryRateScore == 0) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(
                    RESPIRATORY_MSG_TITLE,
                    RESPIRATORY_MSG_CONTENT
                )
            )
        }
        val otherScore =
            smokingScore + regularMedicationScore + symptomScore + dietPunchRateScore + respiratoryRateScore + cardioPunchRateScore + resistanceExerciseRateScore + popularScienceRateScore

        LOGGER.info(
            "患者:{},方案:{},周期:{}~{}的{},系统计算分值总分值:{},吸烟得分:{},规律服药得分:{},症状得分:{},饮食打卡率得分:{},呼吸操打卡率得分:{},有氧运动打卡率得分:{},抗阻运动打卡率得分:{},每日科普打卡率得分:{}",
            copdReport.patientId,
            copdReport.healthManageId,
            copdReport.startDateTime,
            copdReport.endDateTime,
            copdReport.reportName,
            otherScore,
            smokingScore,
            regularMedicationScore,
            symptomScore,
            dietPunchRateScore,
            respiratoryRateScore,
            cardioPunchRateScore,
            resistanceExerciseRateScore,
            popularScienceRateScore
        )

        return otherScore
    }

    /**
     * 吸烟得分
     * 吸烟，而且也没有比以前少吸（0分）
     * 吸烟，但比以前少（5分）
     * 已戒烟或一直不吸烟（10分）
     * @param lastSmokingNum 上次吸烟数量
     * @param currentSmokingNum 当前吸烟数量
     */
    private fun getSmokingScore(lastSmokingNum: Int, currentSmokingNum: Int): Int {
        if (currentSmokingNum == 0) {
            return 10
        }
        if (currentSmokingNum < lastSmokingNum) {
            return 5
        }
        return 0
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
     * 保存报告主表数据
     * @param copdReport 报告生成请求参数
     * @param score 得分
     * @param failMsg 失败原因
     * @return 报告对象
     */
    private fun saveStageReport(
        copdReport: CopdReport,
        score: BigDecimal?,
        failMsg: String? = null
    ): HsStageReport {
        val reportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(reportId)
            .setHealthSchemeManagementInfoId(copdReport.healthManageId)
            .setPatientId(copdReport.patientId)
            .setPatientName(copdReport.patientName)
            .setReportName(copdReport.reportName)
            .setReportStartDatetime(copdReport.startDateTime)
            .setReportEndDatetime(copdReport.endDateTime)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setStageReportType(StageReportType.COPD.name)
            .setReportScore(score)
            .setAge(copdReport.age)
            .setFailMsg(failMsg)
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsStageReportTable.insertWithoutNull(report)
        return report
    }

    /**
     * 根据分组获取健康计划集合
     */
    private fun getPlanList(groupMap: Map<String, List<HealthPlanMain>>, key: String): List<HealthPlanMain> {
        return groupMap[key] ?: listOf()
    }

    /**
     * 获取期望的打卡次数
     */
    private fun getExpectedClockInNumber(map: Map<String, Int>?, planList: List<HealthPlanMain>): Int {
        if (map == null) {
            return 0
        }
        var num = 0
        val planNameList = planList.map { it.name }
        for (key in map.keys) {
            if (planNameList.contains(key)) {
                num += map.getOrDefault(key, 0)
            }
        }
        return num
    }

    /**
     * 获取规律服药得分
     *  本阶段内填写过的慢阻肺随访，是否按照医嘱规律服药填写过“间断”，则为 0分，否则为 15分
     * @param visitList 随访随访记录
     */
    private fun getRegularMedicationScore(visitList: List<HsCopdVisit>): Int {

        //是否包含间断
        val isInterrupt = visitList
            .map { it.drugCompliance }
            .stream()
            .anyMatch { DrugCompliance.valueOf(it) == DrugCompliance.GAP }
        return if (isInterrupt) 0 else 15
    }


    /**
     * 症状得分
     * 本阶段内填写的慢阻肺随访中《是否有如下不适症状？》问题的答案包含
     * 咳嗽、咳痰比平时多、咳脓痰、食欲不振、 腹胀、活动后呼吸困难、下肢或全身浮肿
     *  0个（10分）
     *  1个（5分）
     *  ≥2个（0分）
     * @param visitList 高血压随访记录
     * @return  症状得分
     */
    private fun getSymptomScore(visitList: List<HsCopdVisit>): Int {

        val num = visitList
            .map {
                var num = 0
                if (it.isSymptomCough) {
                    num++
                }
                if (it.isSymptomPurulentSputum) {
                    num++
                }
                if (it.isSymptomInappetence) {
                    num++
                }
                if (it.isSymptomAbdominalDistention) {
                    num++
                }
                if (it.isSymptomBreathing) {
                    num++
                }
                if (it.isSymptomSystemicEdema) {
                    num++
                }
                if (it.symptomOther != null) {
                    num++
                }
                num
            }
            .maxOf { it }

        return when (num) {
            0 -> 10
            1 -> 5
            else -> 0
        }
    }
}
