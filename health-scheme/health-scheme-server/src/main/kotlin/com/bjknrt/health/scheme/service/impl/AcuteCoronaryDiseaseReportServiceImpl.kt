package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.service.AcuteCoronaryDiseaseReport
import com.bjknrt.health.scheme.service.AcuteCoronaryDiseaseReportService
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.service.VisitService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageAcuteCoronaryDiseaseServiceImpl
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
import java.time.LocalDateTime

@Service
class AcuteCoronaryDiseaseReportServiceImpl(
    val questionsAnswerClient: AnswerHistoryApi,
    val hsStageReportTable: HsStageReportTable,
    val hsAcuteCoronaryDiseaseStageStatisticsTable: HsAcuteCoronaryDiseaseStageStatisticsTable,
    val hsAcuteCoronaryDiseaseStageStatisticsDetailTable: HsAcuteCoronaryDiseaseStageStatisticsDetailTable,
    val visitService: VisitService,
    val healthPlanClient: HealthPlanApi,
    val healthSchemeManageService: HealthSchemeManageService,
    val healthManageService: HealthManageAcuteCoronaryDiseaseServiceImpl,
    val standardVerificationService: StandardVerificationService,
    val clockInRateUtils: ClockInRateUtils,
) : AcuteCoronaryDiseaseReportService {
    companion object {
        //冠心病行为习惯随访
        const val ACD_BEHAVIOR_EXAMINATION_PAPER_CODE = "ACUTE_CORONARY_DISEASE_BEHAVIOR"

        //血压测量标准次数
        val BLOOD_PRESSURE_MEASUREMENT_STANDARD_MAP = mapOf(
            ManageStage.INITIAL_STAGE to 14,
            ManageStage.STABLE_STAGE to 14,
            ManageStage.METAPHASE_STABLE_STAGE to 12,
            ManageStage.SECULAR_STABLE_STAGE to 12,
        )

        const val REPORT_FAIL_MSG_NOT_STANDARD = "阶段内血压次数未达标"
        const val REPORT_FAIL_MSG_NOT_BEHAVIOR = "阶段内行为习惯随访未完成"

        const val ABNORMAL_DATA_ALERT_MSG_8_TITLE = "您在本阶段可能出现过加重的症状"
        const val ABNORMAL_DATA_ALERT_MSG_8_CONTENT =
            "出现心慌、胸口处压榨性疼痛或憋闷感或紧缩感、颈部或喉咙感觉发紧、头晕、恶心等症状，这提示您的病情可能出现了变化，建议您及时前往线下医院就诊，以免耽误病情。"
        const val ABNORMAL_DATA_ALERT_MSG_9_TITLE = "您在本阶段可能出现过抗凝或抗血小板药物引起的不良反应"
        const val ABNORMAL_DATA_ALERT_MSG_9_CONTENT =
            "用药期间出现鼻出血、牙龈出血、多次黑褐色大便等异常出血情况，这可能是您正在服用的抗血小板或抗凝药物引起的不良反应，请您及时前往线下医院就诊，如有必要可进行凝血功能检查，遵从医生的建议进行治疗。"
        const val ABNORMAL_DATA_ALERT_MSG_10_TITLE = "您在本阶段可能出现过降脂药物引起的不良反应"
        const val ABNORMAL_DATA_ALERT_MSG_10_CONTENT =
            "您目前正在使用阿托伐他汀等降脂药，那么肌肉痛或肌肉无力、眼白或皮肤发黄等可能是使用这些药物引起的不良反应。建议您及时前往线下医院就诊，必要时进行肝酶、肌酶等检查，遵从医生的建议进行治疗。"
    }

    @Transactional
    override fun generateReport(acdReport: AcuteCoronaryDiseaseReport) {
        //生成提醒查看报告的计划
        healthManageService.addReminderViewReportPlan(
            patientId = acdReport.patientId,
            healthManageId = acdReport.healthManageId,
        )


        //阶段内血压测量次数未完成
        if (!acdReport.isStandard) {
            this.saveStageReport(acdReport, null, REPORT_FAIL_MSG_NOT_STANDARD)
            return
        }

        // 行为习惯评估记录
        val behaviorAnswerRecord = this.getLastEvaluateRecord(
            ACD_BEHAVIOR_EXAMINATION_PAPER_CODE,
            acdReport.patientId,
            acdReport.startDateTime,
            acdReport.endDateTime
        )
        //阶段内行为习惯评估未完成
        if (behaviorAnswerRecord == null) {
            this.saveStageReport(acdReport, null, REPORT_FAIL_MSG_NOT_BEHAVIOR)
            return
        }

        //异常数据提醒消息
        val abnormalDataAlertMsgList = abnormalDataAlertMsgToList(behaviorAnswerRecord.resultsMsg)

        //行为习惯得分
        val behaviorScore = behaviorAnswerRecord.totalScore ?: BigDecimal.ZERO
        //其他系统计算得分
        val otherScore = getOtherScore(acdReport, abnormalDataAlertMsgList).toBigDecimal()
        //报告总得分
        val totalScore = behaviorScore + otherScore
        //生成报告主表
        val report = this.saveStageReport(acdReport, totalScore)
        //保存统计数据
        this.saveStatistics(acdReport, totalScore, report, abnormalDataAlertMsgListToString(abnormalDataAlertMsgList))
    }


    /**
     * 保存统计数据
     * @param acdReport 高血压报告生成请求参数
     * @param totalScore 报告得分
     * @param report 阶段报告
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStatistics(
        acdReport: AcuteCoronaryDiseaseReport,
        totalScore: BigDecimal,
        report: HsStageReport,
        abnormalDataAlertMsg: String
    ) {
        val bloodPressureList = acdReport.bloodPressureList

        //保存明细
        this.saveStatisticsDetail(bloodPressureList, report.id)

        //保存统计数据
        this.saveStageReportStatistics(
            acdReport.patientId,
            acdReport.healthManageId,
            acdReport.managementStage,
            totalScore,
            report.id,
            abnormalDataAlertMsg,
            acdReport.bloodPressureList
        )
    }

    /**
     * 保存统计数据
     * @param patientId 患者Id
     * @param healthManageId 健康方案Id
     * @param managementStage 健康方案阶段
     * @param totalScore 报告得分
     * @param reportId 报告Id
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStageReportStatistics(
        patientId: BigInteger,
        healthManageId: BigInteger,
        managementStage: String,
        totalScore: BigDecimal,
        reportId: BigInteger,
        abnormalDataAlertMsg: String,
        bloodPressureList: List<BloodPressure>
    ) {
        //本次测量次数
        val actualMeasureNumber = bloodPressureList.size

        //本次建议测量次数
        val expectedNumber =
            BLOOD_PRESSURE_MEASUREMENT_STANDARD_MAP[ManageStage.valueOf(managementStage)]
                ?: 0
        //本次完成率 = 实际总的血压测量次数/系统建议本阶段内的血压测量次数
        val bloodPressureFillRate = formatRate(getRate(actualMeasureNumber, expectedNumber))

        val valueList =
            bloodPressureList.map { BloodPressureValue(it.systolicBloodPressure, it.diastolicBloodPressure) }
        //本次达标测量次数
        val standardMeasureNumber = getBloodPressureStandardNumber(valueList)
        //本次达标率
        val bloodPressureStandardRate = getRate(standardMeasureNumber, actualMeasureNumber)
        //本次血压平均值是否达标
        val isBloodPressureAvgStandard = getBloodPressureAvgStandard(valueList)

        //本次超收缩压上限的次数（ 收缩压≥140mmHg的次数 ）
        val sbpUpperLimitNum = getSystolicBloodPressureUpperLimitNum(valueList)
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
        this.getLastHealthSchemeManagementInfo(patientId, healthManageId)
            ?.let { lastHealthSchemeManagementInfo ->

                if (HealthManageType.valueOf(lastHealthSchemeManagementInfo.knHealthManageType) == HealthManageType.ACUTE_CORONARY_DISEASE) {

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
        val stageStatistics = HsAcuteCoronaryDiseaseStageStatistics.builder()
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
            .setAbnormalDataAlertMsg(abnormalDataAlertMsg)
            .setActualMeasureNumberDeviationValue(actualMeasureNumberDeviationValue)
            .setDiastolicBloodPressureAvgDeviationValue(diastolicBloodPressureAvgDeviationValue)
            .setSystolicBloodPressureStandardDeviationValue(systolicBloodPressureStandardDeviationValue)
            .setStandardMeasureNumberDeviationValue(standardMeasureNumberDeviationValue)
            .setSystolicBloodPressureAvgDeviationValue(systolicBloodPressureAvgDeviationValue)
            .setDiastolicBloodPressureStandardDeviationValue(diastolicBloodPressureStandardDeviationValue)
            .setScoreDeviationValue(scoreDeviationValue)
            .setBloodPressureFillRate(bloodPressureFillRate)
            .setBloodPressureStandardRate(bloodPressureStandardRate)
            .setStandardRateDeviationValue(standardRateDeviationValue)
            .setSystolicBloodPressureUpperLimitNum(sbpUpperLimitNum)
            .setDiastolicBloodPressureLowerLimitNum(dbpLowerLimitNum)
            .setLowBloodPressureNum(lowBloodPressureNum)
            .build()
        hsAcuteCoronaryDiseaseStageStatisticsTable.save(stageStatistics)

    }

    /**
     * 获取上次阶段统计数据
     * @param id 阶段报告Id
     * @return 上次阶段统计数据
     */
    private fun getLastStageStatistics(id: BigInteger): HsAcuteCoronaryDiseaseStageStatistics? {
        return hsAcuteCoronaryDiseaseStageStatisticsTable.select()
            .where(HsAcuteCoronaryDiseaseStageStatisticsTable.StageReportId eq id)
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
     * @param bloodPressureList 血压集合
     * @param reportId 报告Id
     */
    private fun saveStatisticsDetail(bloodPressureList: List<BloodPressure>, reportId: BigInteger) {
        bloodPressureList.map {
            HsAcuteCoronaryDiseaseStageStatisticsDetail.builder()
                .setId(AppIdUtil.nextId())
                .setStageReportId(reportId)
                .setSystolicBloodPressure(it.systolicBloodPressure)
                .setDiastolicBloodPressure(it.diastolicBloodPressure)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setCreatedAt(LocalDateTime.now())
                .setMeasureDatetime(it.measureDatetime)
                .build()
        }.forEach {
            hsAcuteCoronaryDiseaseStageStatisticsDetailTable.save(it)
        }
    }

    /**
     * 冠心病计算系统得分
     * @param acdReport 报告生成参数
     * @param abnormalDataAlertMsgList 异常数据提醒消息
     */
    private fun getOtherScore(
        acdReport: AcuteCoronaryDiseaseReport,
        abnormalDataAlertMsgList: MutableList<AbnormalDataAlertMsg>
    ): Int {
        // 血压达标率得分
        val bloodPressureScore = this.getBloodPressureStandardRateScore(
            acdReport.bloodPressureList,
        )

        val visitList = visitService.getAcuteCoronaryDiseaseVisitList(
            acdReport.patientId,
            acdReport.startDateTime,
            acdReport.endDateTime
        )

        // 吸烟得分
        val smokingScore = this.getSmokingScore(visitList)

        // 饮酒得分
        val drinkingScore = this.getDrinkingScore(visitList)

        // 饮食打卡率得分
        var dietPunchRateScore = 0

        // 有氧运动打卡率得分
        var cardioPunchRateScore = 0

        // 抗阻运动打卡率得分
        var resistanceExerciseRateScore = 0
        val planFrequencyValueList = acdReport.planFrequencyValue
        val startDateTime = acdReport.startDateTime
        val endDateTime = acdReport.endDateTime
        if (planFrequencyValueList.isNotEmpty()) {
            //阶段
            val managementStage = ManageStage.valueOf(acdReport.managementStage)
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
        // 是否按照医嘱规律服药得分
        var regularMedicationScore = 0
        //异常出血得分
        var abnormalBleedingScore = 0
        // 症状得分
        var symptomScore = 0
        // 疼痛得分
        var painScore = 0

        if (visitList.isNotEmpty()) {
            regularMedicationScore = this.getRegularMedicationScore(visitList)

            symptomScore = this.getSymptomScore(visitList)

            painScore = this.getPainScore(visitList)

            abnormalBleedingScore = this.getAbnormalBleedingScore(visitList)
        }
        //冠心病随访中《您近期服药过程中是否有过以下不良反应？》选择“无”、“其他”以外的任何选项，则添加正文：
        if (visitList.stream()
                .anyMatch { it.isDrugMyalgia == true || it.isDrugMuscleWeakness == true || it.isDrugSkinYellowness == true }
        ) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(
                   ABNORMAL_DATA_ALERT_MSG_10_TITLE,
                   ABNORMAL_DATA_ALERT_MSG_10_CONTENT
                )
            )
        }
        //冠心病随访中《您近期服药过程中是否发生过以下出血情况？》选择“无”、“其他”以外的任何选项，则添加正文：
        if (visitList.stream()
                .anyMatch { it.isBleedNose == true || it.isBleedGums == true || it.isBleedShit == true }
        ) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(
                    ABNORMAL_DATA_ALERT_MSG_9_TITLE,
                    ABNORMAL_DATA_ALERT_MSG_9_CONTENT
                )
            )
        }
        //冠心病随访中《本阶段是否有以下不适症状？》选择“无”、“其他”以外的任何选项，则添加正文：
        if (visitList.stream()
                .anyMatch { it.isSymptomPains || it.isSymptomDizziness || it.isSymptomNausea || it.isSymptomPalpitationsToc || it.isSymptomThroatTight }
        ) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(
                    ABNORMAL_DATA_ALERT_MSG_8_TITLE,
                    ABNORMAL_DATA_ALERT_MSG_8_CONTENT
                )
            )
        }

        val otherScore = bloodPressureScore + smokingScore + drinkingScore + dietPunchRateScore + cardioPunchRateScore + resistanceExerciseRateScore + regularMedicationScore + symptomScore + painScore + abnormalBleedingScore

        LOGGER.info(
            "患者:{},方案:{},周期:{}~{}的{},系统计算分值总分值:{},血压达标率得分:{},吸烟得分:{},饮酒得分:{},饮食打卡率得分:{},有氧运动打卡率得分:{},抗阻运动打卡率得分:{},规律服药得分:{},症状得分:{},疼痛得分:{},异常出血得分:{}",
            acdReport.patientId,
            acdReport.healthManageId,
            acdReport.startDateTime,
            acdReport.endDateTime,
            acdReport.reportName,
            otherScore,
            bloodPressureScore,
            smokingScore,
            drinkingScore,
            dietPunchRateScore,
            cardioPunchRateScore,
            resistanceExerciseRateScore,
            regularMedicationScore,
            symptomScore,
            painScore,
            abnormalBleedingScore
        )

        return otherScore
    }


    /**
     * 获取问卷记录
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
     * @param acdReport 报告生成请求参数
     * @param score 得分
     * @param failMsg 失败原因
     * @return 报告对象
     */
    private fun saveStageReport(
        acdReport: AcuteCoronaryDiseaseReport,
        score: BigDecimal?,
        failMsg: String? = null
    ): HsStageReport {
        val reportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(reportId)
            .setHealthSchemeManagementInfoId(acdReport.healthManageId)
            .setPatientId(acdReport.patientId)
            .setPatientName(acdReport.patientName)
            .setReportName(acdReport.reportName)
            .setReportStartDatetime(acdReport.startDateTime)
            .setReportEndDatetime(acdReport.endDateTime)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setCreatedAt(LocalDateTime.now())
            .setAge(acdReport.age)
            .setStageReportType(StageReportType.ACUTE_CORONARY_DISEASE.name)
            .setReportScore(score)
            .setFailMsg(failMsg)
            .build()
        hsStageReportTable.insertWithoutNull(report)
        return report
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
     * @param bloodPressureList 血压集合
     * @return  血压得分
     */
    fun getBloodPressureStandardRateScore(
        bloodPressureList: List<BloodPressure>,
    ): Int {
        if (bloodPressureList.isEmpty()) {
            return 5
        }
        val list = bloodPressureList.map { BloodPressureValue(it.systolicBloodPressure, it.diastolicBloodPressure) }
        val standardNumber = getBloodPressureStandardNumber(list)

        return when (getRate(BigDecimal(standardNumber), BigDecimal(list.size))) {
            in 0 until 25 -> 5
            in 25 until 50 -> 10
            in 50 until 75 -> 15
            else -> 20
        }
    }

    /**
     * 本阶段内是否吸烟
     *  是（0分）
     *  否（5分）
     * @return  吸烟得分
     */
    private fun getSmokingScore(visitList: List<HsAcuteCoronaryVisit>): Int {
        return if (visitList.stream().anyMatch { it.lifeCigarettesPerDay > 0 }) 0 else 5
    }

    /**
     * 本阶段内是否饮酒
     *  是（0分）
     *  否（5分）
     * @return  饮酒得分
     */
    private fun getDrinkingScore(visitList: List<HsAcuteCoronaryVisit>): Int {
        return if (visitList.stream().anyMatch { it.lifeAlcoholPerDay > 0 }) 0 else 5
    }

    /**
     * 获取规律服药得分
     *  本阶段内填写过的冠心病随访，并且是否按照医嘱规律服药题的答案是"规律或不服药" (8分)
     *  未填写过冠心病随访或是否按照医嘱规律服药题的答案填写过"间断" (0分)
     * @param visitList 随访随访记录
     */
    private fun getRegularMedicationScore(visitList: List<HsAcuteCoronaryVisit>): Int {
        //是否包含间断
        val isInterrupt = visitList
            .map { it.drugCompliance }
            .stream()
            .anyMatch { DrugCompliance.valueOf(it) == DrugCompliance.GAP }
        return if (isInterrupt) 0 else 8
    }

    /**
     * 症状得分
     * 本阶段内填写的高血压随访中《是否有如下不适症状？》问题的答案包含
     * 心慌、胸口处压榨性疼痛或憋闷感或紧缩感、颈部或喉咙感觉发紧、头晕、恶心
     *  出现0个症状 (10分）
     *  出现1个症状（5分）
     *  出现大于等于2个症状(0分）
     * @param visitList 随访记录
     * @return  症状得分
     */
    private fun getSymptomScore(visitList: List<HsAcuteCoronaryVisit>): Int {

        var num = 0

        visitList.forEach {
            if (it.isSymptomPains) {
                num++
            }
            if (it.isSymptomDizziness) {
                num++
            }
            if (it.isSymptomNausea) {
                num++
            }
            if (it.isSymptomPalpitationsToc) {
                num++
            }
            if (it.isSymptomThroatTight) {
                num++
            }
            if (!it.symptomOther.isNullOrEmpty()) {
                num++
            }
        }

        return when (num) {
            0 -> 10
            1 -> 5
            else -> 0
        }
    }

    /**
     * 疼痛得分
     * 本阶段内是否出现过肌肉痛、肌肉无力、眼白或皮肤发黄？
     * 是（0分）
     * 否（5分）
     * @param visitList 随访记录
     * @return  疼痛得分
     */
    private fun getPainScore(visitList: List<HsAcuteCoronaryVisit>): Int {
        return if (visitList.stream()
                .anyMatch { it.isDrugMyalgia == true || it.isDrugMuscleWeakness == true || it.isDrugSkinYellowness == true }
        ) 0 else 5
    }

    /**
     * 本阶段内是否发生过鼻出血、牙龈出血、多次黑褐色大便或其他异常出血情况？
     * 是（0分）
     * 否（5分）
     * @param visitList 随访记录
     * @return  异常出血得分
     */
    private fun getAbnormalBleedingScore(visitList: List<HsAcuteCoronaryVisit>): Int {
        return if (visitList.stream()
                .anyMatch { it.isBleedNose == true || it.isBleedGums == true || it.isBleedShit == true || !it.bleedOther.isNullOrBlank() }
        ) 0 else 5
    }

}
