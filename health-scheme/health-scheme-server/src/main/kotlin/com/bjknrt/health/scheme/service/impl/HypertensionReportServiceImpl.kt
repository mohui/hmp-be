package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.service.HypertensionReport
import com.bjknrt.health.scheme.service.HypertensionReportService
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.service.VisitService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageHypertensionServiceImpl
import com.bjknrt.health.scheme.util.*
import com.bjknrt.health.scheme.vo.DrugCompliance
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.ManageStage
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
import java.math.RoundingMode
import java.time.LocalDateTime


@Service
class HypertensionReportServiceImpl(
    val visitService: VisitService,
    val healthPlanClient: HealthPlanApi,
    val questionsAnswerClient: AnswerHistoryApi,
    val hsStageReportTable: HsStageReportTable,
    val hsStageStatisticsTable: HsStageStatisticsTable,
    val hsStageReportStatisticsDetailTable: HsStageStatisticsDetailTable,
    val healthSchemeManageService: HealthSchemeManageService,
    val standardVerificationService: StandardVerificationService,
    val clockInRateUtils: ClockInRateUtils,
    val healthManageService: HealthManageHypertensionServiceImpl,
) : HypertensionReportService {

    companion object {
        //高血压行为习惯随访
        val BEHAVIOR_EXAMINATION_PAPER_CODE = "BEHAVIOR"

        //血压测量标准次数
        val BLOOD_PRESSURE_MEASUREMENT_STANDARD_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 14,
            ManageStage.STABLE_STAGE to 14,
            ManageStage.METAPHASE_STABLE_STAGE to 12,
            ManageStage.SECULAR_STABLE_STAGE to 12,
        )

        const val REPORT_FAIL_MSG_NOT_STANDARD = "阶段内血压次数未达标"
        const val REPORT_FAIL_MSG_NOT_BEHAVIOR = "阶段内随访未完成"

        const val PULSE_PRESSURE_DIFF_HIGH_MSG_TITLE = "您的脉压差高于正常范围。"
        const val PULSE_PRESSURE_DIFF_HIGH_MSG_CONTENT =
            "脉压差过高可加速动脉粥样硬化，进而引发心脑血管疾病，建议您咨询专业医生。"

        const val MEDICATION_REGULARLY_MSG_TITLE = "您本阶段内服药不规律"
        const val MEDICATION_REGULARLY_MSG_CONTENT =
            "自行更改高血压用药或停药可能会导致血压升高或波动，长期以往易增加心肌梗死、脑梗死、肾功能不全的发生风险，因此建议您按时线下随访、严格遵从医嘱服药。"
    }

    @Transactional
    override fun generateReport(hypertensionReport: HypertensionReport) {
        //生成提醒查看报告的计划
        healthManageService.addReminderViewReportPlan(
            patientId = hypertensionReport.patientId,
            healthManageId = hypertensionReport.healthSchemeManagementInfoId,
        )


        //血压缺失，次数不达标
        if (!hypertensionReport.isStandard) {
            this.saveStageReport(hypertensionReport, null, REPORT_FAIL_MSG_NOT_STANDARD)
            return
        }
        // 行为习惯评估记录
        val behaviorAnswerRecord = this.getLastEvaluateRecord(
            BEHAVIOR_EXAMINATION_PAPER_CODE,
            hypertensionReport.patientId,
            hypertensionReport.startDateTime,
            hypertensionReport.endDateTime
        )
        if (behaviorAnswerRecord == null) {
            this.saveStageReport(hypertensionReport, null, REPORT_FAIL_MSG_NOT_BEHAVIOR)
            return
        }
        //异常数据提醒消息
        val abnormalDataAlertMsgList = abnormalDataAlertMsgToList(behaviorAnswerRecord.resultsMsg)
        //行为习惯随访得分
        val behaviorScore = behaviorAnswerRecord.totalScore ?: BigDecimal.ZERO
        //报告总得分
        val totalScore = behaviorScore + BigDecimal(getOtherScore(hypertensionReport, abnormalDataAlertMsgList))
        // 2.生成报告主表
        val report = this.saveStageReport(hypertensionReport, totalScore)

        // 3.保存统计数据
        this.saveStatistics(
            hypertensionReport,
            totalScore,
            report,
            abnormalDataAlertMsgListToString(abnormalDataAlertMsgList)
        )

    }

    /**
     * 保存报告统计数据
     * @param hypertensionReport 报告生成请求参数
     * @param report 报告
     */
    private fun saveStatistics(
        hypertensionReport: HypertensionReport,
        score: BigDecimal,
        report: HsStageReport,
        abnormalDataAlertMsg: String
    ) {


        this.saveStageReportStatisticsDetail(hypertensionReport.bloodPressureList, report.id)

        this.saveStageReportStatistics(
            hypertensionReport.patientId,
            hypertensionReport.healthSchemeManagementInfoId,
            hypertensionReport.age,
            hypertensionReport.bloodPressureList,
            score,
            report.id,
            hypertensionReport.managementStage,
            abnormalDataAlertMsg
        )
    }

    /**
     * 保存报告统计数据
     * @param patientId 患者Id
     * @param healthSchemeManagementInfoId 健康方案管理id
     * @param age 年龄
     * @param bloodPressureList 血压集合
     * @param totalScore 报告得分
     * @param reportId 报告Id
     * @param managementStage 高血压阶段
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStageReportStatistics(
        patientId: BigInteger,
        healthSchemeManagementInfoId: BigInteger,
        age: Int,
        bloodPressureList: List<BloodPressure>,
        totalScore: BigDecimal,
        reportId: BigInteger,
        managementStage: String,
        abnormalDataAlertMsg: String
    ) {
        //本次测量次数
        val actualMeasureNumber = bloodPressureList.size

        //本次建议测量次数
        val expectedNumber = BLOOD_PRESSURE_MEASUREMENT_STANDARD_MAP[ManageStage.valueOf(managementStage)] ?: 0
        //本次完成率 = 实际总的血压测量次数/系统建议本阶段内的血压测量次数
        val bloodPressureFillRate = formatRate(getRate(actualMeasureNumber, expectedNumber))

        val valueList =
            bloodPressureList.map { BloodPressureValue(it.systolicBloodPressure, it.diastolicBloodPressure) }
        //本次达标测量次数
        val standardMeasureNumber = getBloodPressureStandardNumber(age, valueList)
        //本次达标率
        val bloodPressureStandardRate = getRate(standardMeasureNumber, actualMeasureNumber)
        //本次血压平均值是否达标
        val isBloodPressureAvgStandard = getBloodPressureAvgStandard(age, valueList)

        //本次超收缩压上限的次数（ 年龄＜65岁者,收缩压≥140mmHg的次数；年龄≥65岁者，收缩压≥150mmHg的次数 ）
        val sbpUpperLimitNum = getSystolicBloodPressureUpperLimitNum(age, valueList)
        //本次超舒张压下限的次数（ 舒张压≥90mmHg ）
        val dbpLowerLimitNum = getDiastolicBloodPressureLowerLimitNum(valueList)
        //本次低血压次数（ 收缩压＜90mmHg 或 舒张压＜60mmHg ）
        val lowBloodPressureNum = getLowBloodPressureNum(valueList)

        val systolicBloodPressureList = bloodPressureList.map { it.systolicBloodPressure }
        val diastolicBloodPressureList = bloodPressureList.map { it.diastolicBloodPressure }

        // 本次收缩压平均值
        val systolicBloodPressureAvg = standardVerificationService.getBloodPressureAvg(systolicBloodPressureList)
        // 本次舒张压平均值
        val diastolicBloodPressureAvg = standardVerificationService.getBloodPressureAvg(diastolicBloodPressureList)
        // 本次收缩压标准差
        val systolicBloodPressureStandardDeviation = getBloodPressureStandardDeviation(systolicBloodPressureList)
        // 本次舒张压标准差
        val diastolicBloodPressureStandardDeviation = getBloodPressureStandardDeviation(diastolicBloodPressureList)

        //本次达标率与上次达标率的差
        var standardRateDeviationValue: BigDecimal? = null
        // 本次测量(上传)次数与上次测量(上传)次数的差
        var actualMeasureNumberDeviationValue: Int? = null
        // 本次达标次数与上次达标次数的差
        var standardMeasureNumberDeviationValue: Int? = null
        // 本次收缩压平均值与上次收缩压平均值的差值
        var systolicBloodPressureAvgDeviationValue: BigDecimal? = null
        // 本次舒张压平均值与上次舒张压平均值的差值
        var diastolicBloodPressureAvgDeviationValue: BigDecimal? = null
        // 本次血压标准差与上次血压标准差的差值
        var systolicBloodPressureStandardDeviationValue: BigDecimal? = null
        // 本次舒张压标准差与上次舒张压标准差的差值
        var diastolicBloodPressureStandardDeviationValue: BigDecimal? = null
        // 本次报告得分与上次报告得分的差值
        var scoreDeviationValue: BigDecimal? = null

        // 上次的数据
        this.getLastHealthSchemeManagementInfo(patientId, healthSchemeManagementInfoId)
            ?.let { lastHealthSchemeManagementInfo ->

                if (HealthManageType.valueOf(lastHealthSchemeManagementInfo.knHealthManageType) == HealthManageType.HYPERTENSION) {
                    this.getLastStageReport(lastHealthSchemeManagementInfo.knId)?.let { lastStageReport ->
                        //上次的统计数据
                        this.getLastStageStatistics(lastStageReport.id)?.let { lastStageStatistics ->
                            //本次本次达标率与上次本次达标率的差
                            standardRateDeviationValue =
                                bloodPressureStandardRate - lastStageStatistics.bloodPressureStandardRate
                            // 本次测量(上传)次数与上次测量(上传)次数的差
                            actualMeasureNumberDeviationValue =
                                actualMeasureNumber - lastStageStatistics.actualMeasureNumber
                            // 本次达标次数与上次达标次数的差
                            standardMeasureNumberDeviationValue =
                                standardMeasureNumber - lastStageStatistics.standardMeasureNumber
                            // 本次收缩压平均值与上次收缩压平均值的差值
                            systolicBloodPressureAvgDeviationValue =
                                systolicBloodPressureAvg - lastStageStatistics.systolicBloodPressureAvg
                            // 本次舒张压平均值与上次舒张压平均值的差值
                            diastolicBloodPressureAvgDeviationValue =
                                diastolicBloodPressureAvg - lastStageStatistics.diastolicBloodPressureAvg
                            // 本次血压标准差与上次血压标准差的差值
                            systolicBloodPressureStandardDeviationValue =
                                systolicBloodPressureStandardDeviation - lastStageStatistics.systolicBloodPressureStandardDeviation
                            // 本次舒张压标准差与上次舒张压标准差的差值
                            diastolicBloodPressureStandardDeviationValue =
                                diastolicBloodPressureStandardDeviation - lastStageStatistics.diastolicBloodPressureStandardDeviation
                        }

                        // 本次报告得分与上次报告得分的差值
                        scoreDeviationValue = lastStageReport.reportScore?.let { totalScore - it }
                    }
                }
            }

        val stageStatistics = HsStageStatistics.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(reportId)
            .setActualMeasureNumber(actualMeasureNumber)
            .setStandardMeasureNumber(standardMeasureNumber)
            .setSystolicBloodPressureAvg(systolicBloodPressureAvg)
            .setDiastolicBloodPressureAvg(diastolicBloodPressureAvg)
            .setSystolicBloodPressureStandardDeviation(systolicBloodPressureStandardDeviation)
            .setDiastolicBloodPressureStandardDeviation(diastolicBloodPressureStandardDeviation)
            .setIsBloodPressureAvgStandard(isBloodPressureAvgStandard)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setCreatedAt(LocalDateTime.now())
            .setActualMeasureNumberDeviationValue(actualMeasureNumberDeviationValue)
            .setStandardMeasureNumberDeviationValue(standardMeasureNumberDeviationValue)
            .setSystolicBloodPressureAvgDeviationValue(systolicBloodPressureAvgDeviationValue)
            .setDiastolicBloodPressureAvgDeviationValue(diastolicBloodPressureAvgDeviationValue)
            .setSystolicBloodPressureStandardDeviationValue(systolicBloodPressureStandardDeviationValue)
            .setDiastolicBloodPressureStandardDeviationValue(diastolicBloodPressureStandardDeviationValue)
            .setScoreDeviationValue(scoreDeviationValue)
            .setBloodPressureFillRate(bloodPressureFillRate)
            .setBloodPressureStandardRate(bloodPressureStandardRate)
            .setStandardRateDeviationValue(standardRateDeviationValue)
            .setLowBloodPressureNum(lowBloodPressureNum)
            .setAbnormalDataAlertMsg(abnormalDataAlertMsg)
            .setSystolicBloodPressureUpperLimitNum(sbpUpperLimitNum)
            .setDiastolicBloodPressureLowerLimitNum(dbpLowerLimitNum)
            .build()
        hsStageStatisticsTable.save(stageStatistics)
    }

    /**
     * 获取上次阶段统计数据
     * @param id 阶段报告Id
     * @return 上次阶段统计数据
     */
    private fun getLastStageStatistics(id: BigInteger): HsStageStatistics? {
        return hsStageStatisticsTable.select()
            .where(HsStageStatisticsTable.StageReportId eq id)
            .findOne()
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
     * @param healthSchemeManagementInfoId 患者Id
     * @return  上次健康方案管理信息
     */
    private fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        healthSchemeManagementInfoId: BigInteger
    ): HsHealthSchemeManagementInfo? {
        return healthSchemeManageService.getLastHealthSchemeManagementInfo(patientId, healthSchemeManagementInfoId)
    }


    /**
     * 保存报告统计明细数据
     * @param bloodPressureList 血压集合
     * @param reportId 报告Id
     */
    private fun saveStageReportStatisticsDetail(
        bloodPressureList: List<BloodPressure>,
        reportId: BigInteger
    ) {
        //血压数据为空时不保存
        if (bloodPressureList.isNotEmpty()) {
            val diastolicBloodPressureMax = bloodPressureList.maxOf { it.diastolicBloodPressure }
            val systolicBloodPressureMax = bloodPressureList.maxOf { it.systolicBloodPressure }

            for (bloodPressure in bloodPressureList) {
                val diastolicValue = bloodPressure.diastolicBloodPressure
                val systolicValue = bloodPressure.systolicBloodPressure
                val measureDatetime = bloodPressure.measureDatetime
                val detail = HsStageStatisticsDetail.builder()
                    .setId(AppIdUtil.nextId())
                    .setStageReportId(reportId)
                    .setSystolicBloodPressure(systolicValue)
                    .setDiastolicBloodPressure(diastolicValue)
                    .setIsSystolicBloodPressureMax(systolicValue.toDouble() == systolicBloodPressureMax.toDouble())
                    .setIsDiastolicBloodPressureMax(diastolicValue.toDouble() == diastolicBloodPressureMax.toDouble())
                    .setMeasureDatetime(measureDatetime)
                    .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                    .setCreatedAt(LocalDateTime.now())
                    .build()
                hsStageReportStatisticsDetailTable.save(detail)
            }
        }
    }

    /**
     * 保存报告主表数据
     * @param hypertensionReport 报告生成请求参数
     * @param score 得分
     * @param failMsg 失败原因
     * @return 报告Id
     */
    private fun saveStageReport(
        hypertensionReport: HypertensionReport,
        score: BigDecimal?,
        failMsg: String? = null
    ): HsStageReport {
        val report = HsStageReport.builder()
            .setId(AppIdUtil.nextId())
            .setHealthSchemeManagementInfoId(hypertensionReport.healthSchemeManagementInfoId)
            .setPatientId(hypertensionReport.patientId)
            .setPatientName(hypertensionReport.patientName)
            .setReportName(hypertensionReport.reportName)
            .setReportStartDatetime(hypertensionReport.startDateTime)
            .setReportEndDatetime(hypertensionReport.endDateTime)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setStageReportType(StageReportType.HYPERTENSION.name)
            .setFailMsg(failMsg)
            .setAge(hypertensionReport.age)
            .setReportScore(score)
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsStageReportTable.insertWithoutNull(report)
        return report
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
     * 获取其他得分
     * @param hypertensionReport 高血压报告请求参数
     * @return  行为习惯评估得分
     */
    private fun getOtherScore(
        hypertensionReport: HypertensionReport,
        abnormalDataAlertMsgList: MutableList<AbnormalDataAlertMsg>
    ): Int {

        // 1.血压达标率得分
        val bloodPressureScore = this.getBloodPressureStandardRateScore(
            hypertensionReport.age,
            hypertensionReport.bloodPressureList,
        )

        // 2.脉压得分
        val avgPulsePressureScore = this.getAvgPulsePressureScore(hypertensionReport.bloodPressureList)

        // 3.饮食打卡率得分
        var dietPunchRateScore = 0

        // 4.有氧运动打卡率得分
        var cardioPunchRateScore = 0

        // 5.抗阻运动打卡率得分
        var resistanceExerciseRateScore = 0

        val planFrequencyValueList = hypertensionReport.planFrequencyValue
        val startDateTime = hypertensionReport.startDateTime
        val endDateTime = hypertensionReport.endDateTime
        if (planFrequencyValueList.isNotEmpty()) {
            //阶段
            val managementStage = ManageStage.valueOf(hypertensionReport.managementStage)
            //健康计划
            val groupPlanMap = healthPlanClient.idGetList(planFrequencyValueList.map { it.planId })
                .filter { it.type == HealthPlanType.DIET_PLAN || it.type == HealthPlanType.EXERCISE_PROGRAM }
                .groupBy { it.group ?: "" }

            //实际打卡次数
            var expectedClockInNumber = CLOCK_IN_DIET_MAP[managementStage] ?: 0
            // 饮食打卡率得分
            dietPunchRateScore = clockInRateUtils.getDietPunchRateScore(
                this.getPlanList(groupPlanMap, DIET).map { it.id },
                expectedClockInNumber,
                startDateTime,
                endDateTime
            )

            // 有氧运动打卡率得分
            var planList = this.getPlanList(groupPlanMap, AEROBIC_EXERCISE)
            expectedClockInNumber = getExpectedClockInNumber(CLOCK_IN_AEROBIC_EXERCISE_MAP[managementStage], planList)
            cardioPunchRateScore = clockInRateUtils.getCardioPunchRateScore(
                planList.map { it.id },
                expectedClockInNumber,
                startDateTime,
                endDateTime
            )

            // 抗阻运动打卡率得分
            planList = this.getPlanList(groupPlanMap, RESISTANCE_EXERCISE)
            expectedClockInNumber =
                getExpectedClockInNumber(CLOCK_IN_RESISTANCE_EXERCISE_MAP[managementStage], planList)
            resistanceExerciseRateScore = clockInRateUtils.getResistanceExerciseRateScore(
                planList.map { it.id },
                expectedClockInNumber,
                startDateTime,
                endDateTime
            )

        }

        //高血压随访记录
        val hypertensionVisitList = visitService.getHypertensionVisitList(
            hypertensionReport.patientId,
            hypertensionReport.startDateTime,
            endDateTime
        )

        // 6.是否按照医嘱规律服药得分
        var regularMedicationScore = 0
        // 7.症状得分
        var symptomScore = 0
        if (hypertensionVisitList.isNotEmpty()) {
            regularMedicationScore = this.getRegularMedicationScore(hypertensionVisitList)

            symptomScore = this.getSymptomScore(hypertensionVisitList)
        }

        if (avgPulsePressureScore == 0) {
            abnormalDataAlertMsgList.add(
                0, AbnormalDataAlertMsg(
                    PULSE_PRESSURE_DIFF_HIGH_MSG_TITLE,
                    PULSE_PRESSURE_DIFF_HIGH_MSG_CONTENT
                )
            )
        }
        if (regularMedicationScore == 0) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(
                    MEDICATION_REGULARLY_MSG_TITLE,
                    MEDICATION_REGULARLY_MSG_CONTENT
                )
            )
        }

        val otherScore = bloodPressureScore + avgPulsePressureScore + dietPunchRateScore + cardioPunchRateScore + resistanceExerciseRateScore + regularMedicationScore + symptomScore

        LOGGER.info(
            "患者:{},方案:{},周期:{}~{}的{},系统计算分值总分值:{},血压达标率得分:{},脉压得分:{},饮食打卡率得分:{},有氧运动打卡率得分:{},抗阻运动打卡率得分:{},规律服药得分:{},症状得分:{}",
            hypertensionReport.patientId,
            hypertensionReport.healthSchemeManagementInfoId,
            hypertensionReport.startDateTime,
            hypertensionReport.endDateTime,
            hypertensionReport.reportName,
            otherScore,
            bloodPressureScore,avgPulsePressureScore,dietPunchRateScore,cardioPunchRateScore,resistanceExerciseRateScore,regularMedicationScore,symptomScore
        )

        return otherScore

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
     * 根据分组获取健康计划集合
     */
    private fun getPlanList(groupMap: Map<String, List<HealthPlanMain>>, key: String): List<HealthPlanMain> {
        return groupMap[key] ?: listOf()
    }


    /**
     * 血压达标率得分
     * 达标率小于25% (5分）
     * 达标率大于等于25%，达标率小于50%（10分）
     * 达标率大于等于50%，达标率小于75%（15分）
     * 达标率大于75% （20分）
     * @param age 高血压报告请求参数
     * @param bloodPressureList 血压集合
     * @return  血压得分
     */
    fun getBloodPressureStandardRateScore(
        age: Int,
        bloodPressureList: List<BloodPressure>,
    ): Int {
        if (bloodPressureList.isEmpty()) {
            return 5
        }
        val list = bloodPressureList.map { BloodPressureValue(it.systolicBloodPressure, it.diastolicBloodPressure) }
        val standardNumber = getBloodPressureStandardNumber(age, list)

        return when (getRate(BigDecimal(standardNumber), BigDecimal(list.size))) {
            in 0 until 25 -> 5
            in 25 until 50 -> 10
            in 50 until 75 -> 15
            else -> 20
        }
    }

    /**
     * 获取规律服药得分
     *  本阶段内填写过的高血压随访，并且是否按照医嘱规律服药题的答案是"规律或不服药" (12分)
     *  未填写过高血压随访或是否按照医嘱规律服药题的答案填写过"间断" (0分)
     * @param hypertensionVisitList 高血压随访随访记录
     */
    private fun getRegularMedicationScore(hypertensionVisitList: List<HsHtnVisit>): Int {

        //是否包含间断
        val isInterrupt = hypertensionVisitList
            .mapNotNull { it.drugCompliance }
            .stream()
            .anyMatch { DrugCompliance.valueOf(it) == DrugCompliance.GAP }
        return if (isInterrupt) 0 else 12
    }

    /**
     * 脉压得分
     * 报告周期内的平均脉压差都达标得10分
     * 平均脉压差（收缩压-舒张压）在20~60mmHg范围之间达标
     *
     * @param bloodPressureList 血压集合
     * @return  脉压得分
     */
    private fun getAvgPulsePressureScore(
        bloodPressureList: List<BloodPressure>
    ): Int {
        if (bloodPressureList.isEmpty()) {
            return 0
        }
        val pulsePressure =
            (getAvg(bloodPressureList.map { it.systolicBloodPressure }) - getAvg(bloodPressureList.map { it.diastolicBloodPressure }))
                .setScale(0, RoundingMode.HALF_UP).toInt()
        return if (pulsePressure in 20..60) 10 else 0
    }

    /**
     * 症状得分
     * 本阶段内填写的高血压随访中《是否有如下不适症状？》问题的答案包含
     * 头痛头晕、恶心呕吐、眼花耳鸣、呼吸困难或慢性咳嗽、心悸胸闷、鼻出血不止、四肢发麻或下肢水肿、一过性黑朦、体力劳动或精神紧张时出现胸痛症状
     *  出现0个症状（20分）
     *  出现1个症状 (15分）
     *  出现2个症状（10分）
     *  出现大于等于3个症状(5分）
     * @param hypertensionVisitList 高血压随访记录
     * @return  症状得分
     */
    private fun getSymptomScore(hypertensionVisitList: List<HsHtnVisit>): Int {

        val num = hypertensionVisitList
            .map {
                var num = 0
                if (it.isSymptomDizzinessHeadache == true) {
                    num++
                }
                if (it.isSymptomNauseaVomiting == true) {
                    num++
                }
                if (it.isSymptomBlurredTinnitus == true) {
                    num++
                }
                if (it.isSymptomBreathing == true) {
                    num++
                }
                if (it.isSymptomPalpitationsToc == true) {
                    num++
                }
                if (it.isSymptomNumbness == true) {
                    num++
                }
                if (it.isSymptomLowerExtremityEdema == true) {
                    num++
                }
                if (it.symptomOther != null) {
                    num++
                }
                num
            }
            .maxOf { it }

        return when (num) {
            0 -> 20
            1 -> 15
            2 -> 10
            else -> 5
        }
    }
}
