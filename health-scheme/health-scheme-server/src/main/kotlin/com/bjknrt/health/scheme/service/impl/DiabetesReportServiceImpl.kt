package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.bjknrt.health.scheme.service.BloodSugar
import com.bjknrt.health.scheme.service.DiabetesReport
import com.bjknrt.health.scheme.service.DiabetesReportService
import com.bjknrt.health.scheme.service.VisitService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl
import com.bjknrt.health.scheme.util.*
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

@Service
class DiabetesReportServiceImpl(
    val visitService: VisitService,
    val hsStageReportTable: HsStageReportTable,
    val hsDiabetesStageStatisticsDetailTable: HsDiabetesStageStatisticsDetailTable,
    val hsDiabetesStageStatisticsTable: HsDiabetesStageStatisticsTable,
    val healthSchemeManageService: HealthSchemeManageService,
    val questionsAnswerClient: AnswerHistoryApi,
    val indicatorClient: IndicatorApi,
    val healthPlanClient: HealthPlanApi,
    val clockInRateUtils: ClockInRateUtils,
    val healthManageService: HealthManageDiabetesServiceImpl
) : DiabetesReportService {
    companion object {
        //糖尿病行为习惯随访
        val DIABETES_BEHAVIOR_EXAMINATION_PAPER_CODE = "DIABETES_BEHAVIOR"

        const val REPORT_FAIL_MSG_NOT_STANDARD = "阶段内血糖次数未达标"
        const val REPORT_FAIL_MSG_NOT_BEHAVIOR = "阶段内随访未完成"
        val LOW_BLOOD_SUGAR_3_9 = BigDecimal(3.9)
        const val HBALC_7_0 = 7.0
        val HDC_1_0: BigDecimal = BigDecimal(1.0)
        val LDC_2_6: BigDecimal = BigDecimal(2.6)
        val TC_4_5: BigDecimal = BigDecimal(4.5)
        val TG_1_7: BigDecimal = BigDecimal(1.7)

        const val MONTHS_3 = 3L
        const val RATE_60 = 60
        const val AGE_60 = 60

        const val BLOOD_SUGAR_HIGH_MSG_TITLE = "您的餐后2小时血糖较餐前血糖的增幅较高"
        const val BLOOD_SUGAR_HIGH_MSG_CONTENT =
            "请您注意调整主食量、定时进餐，如经调整后餐后2小时血糖较餐前血糖的增幅仍 ≥ 5.0 mmol/L，建议您线下前往医院内分泌科就诊。"

        const val REGULAR_MEDICATION_MSG_TITLE = "您在本阶段内服药不规律"
        const val REGULAR_MEDICATION_MSG_CONTENT =
            "自行更改降糖药或自行停药可能会导致血糖升高或波动，甚至引起糖尿病急性并发症，建议您严格遵从医嘱服药、按时线下医院随访。"

        const val HBALC_MSG_TITLE = "您的糖化血红蛋白高于控制目标"
        const val HBALC_MSG_CONTENT =
            "糖化血红蛋白反映的是近2~3个月内的平均血糖水平，建议您改善生活方式，定时前往线下医院复诊。"

        var BLOOD_LIPIDS_MSG_TITLE = "您的%s异常"
        const val BLOOD_LIPIDS_MSG_CONTENT =
            "血脂异常是引起糖尿病血管病变的重要危险因素，可增加糖尿病并发症的发生风险、加快疾病进展。建议您改善生活方式，并定期前往线下医院复诊。"

        val SMOKING_MSG_TITLE = "您本阶段内有吸烟或吸二手烟"
        const val SMOKING_MSG_CONTENT =
            "吸烟不仅是导致癌症、呼吸系统和心脑血管系统疾病的重要危险因素，也与糖尿病及其并发症（肾功能不全、大血管病变、神经病变）的发生发展密切相关。戒烟能显著降低心血管疾病发生率，延缓糖尿病肾病的发展。建议您不要吸烟或使用其他烟草类产品，并尽量减少二手烟暴露。"

        val DIET_MSG_TITLE = "您在本阶段内的饮食管理欠佳"
        const val DIET_MSG_CONTENT =
            "科学健康的饮食习惯是糖尿病治疗过程中的重要部分，对控制血糖有很大益处，建议您对照个人健康方案积极进行膳食管理。"

        val SPORT_MSG_TITLE = "您在本阶段内的运动习惯有待改进"
        const val SPORT_MSG_CONTENT =
            "规律的有氧运动及抗阻运动有助于控制血糖、降低心血管疾病的发生风险。建议您对照健康方案养成规律运动的良好习惯。"

        val HYPOGLYCEMIA_MSG_TITLE = "您属于容易发生低血糖的人群"
        const val HYPOGLYCEMIA_MSG_CONTENT =
            "建议您平时注意多监测餐前血糖。如果多次出现低血糖，建议您及时前往医院内分泌科就诊。"

    }

    @Transactional
    override fun generateReport(diabetesReport: DiabetesReport) {
        //生成提醒查看报告的计划
        healthManageService.addReminderViewReportPlan(
            patientId = diabetesReport.patientId,
            healthManageId = diabetesReport.healthManageId,
        )

        //阶段内血糖次数未达标
        if (!diabetesReport.isStandard) {
            this.saveStageReport(diabetesReport, null, REPORT_FAIL_MSG_NOT_STANDARD)
            return
        }
        // 行为习惯评估记录
        val behaviorAnswerRecord = this.getLastEvaluateRecord(
            DIABETES_BEHAVIOR_EXAMINATION_PAPER_CODE,
            diabetesReport.patientId,
            diabetesReport.startDateTime,
            diabetesReport.endDateTime
        )
        //阶段内随访未完成
        if (behaviorAnswerRecord == null) {
            this.saveStageReport(diabetesReport, null, REPORT_FAIL_MSG_NOT_BEHAVIOR)
            return
        }
        //异常数据提醒消息
        val abnormalDataAlertMsgList = abnormalDataAlertMsgToList(behaviorAnswerRecord.resultsMsg)
        //行为习惯得分
        val behaviorScore = behaviorAnswerRecord.totalScore ?: BigDecimal.ZERO
        //其他系统计算得分
        val otherScore = getOtherScore(diabetesReport, abnormalDataAlertMsgList).toBigDecimal()
        //报告总得分
        val totalScore = behaviorScore + otherScore
        //生成报告主表
        val report = this.saveStageReport(diabetesReport, totalScore)
        //保存统计数据
        this.saveStatistics(
            diabetesReport,
            totalScore,
            report,
            abnormalDataAlertMsgListToString(abnormalDataAlertMsgList)
        )
    }


    /**
     * 保存统计数据
     * @param diabetesReport 高血压报告生成请求参数
     * @param totalScore 报告得分
     * @param report 阶段报告
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStatistics(
        diabetesReport: DiabetesReport,
        totalScore: BigDecimal,
        report: HsStageReport,
        abnormalDataAlertMsg: String
    ) {
        val bloodSugarList = diabetesReport.bloodSugarList

        //保存明细
        this.saveStatisticsDetail(bloodSugarList, report.id)

        //保存统计数据
        this.saveStageReportStatistics(
            diabetesReport.patientId,
            diabetesReport.healthManageId,
            bloodSugarList,
            totalScore,
            report.id,
            abnormalDataAlertMsg
        )
    }

    /**
     *
     * @param patientId 患者Id
     * @param healthManageId 健康方案Id
     * @param bloodSugarList 血糖集合
     * @param totalScore 报告得分
     * @param reportId 报告Id
     * @param abnormalDataAlertMsg 异常数据提醒消息
     */
    private fun saveStageReportStatistics(
        patientId: BigInteger,
        healthManageId: BigInteger,
        bloodSugarList: List<BloodSugar>,
        totalScore: BigDecimal,
        reportId: BigInteger,
        abnormalDataAlertMsg: String
    ) {
        //本次空腹血糖测量次数
        val fastingBloodSugarMeasureNumber = bloodSugarList.filter { it.fastingBloodSugar != null }.size

        //本次餐前血糖（中、晚餐前）血糖测量次数
        val beforeLunchBloodSugarMeasureNumber = bloodSugarList
            .filter { it.beforeLunchBloodSugar != null || it.beforeDinnerBloodSugar != null }
            .size

        //本次(早、午、晚)餐后2个小时血糖测量次数
        val afterMealBloodSugarBloodSugarMeasureNumber = bloodSugarList
            .filter { it.afterMealBloodSugar != null || it.afterLunchBloodSugar != null || it.afterDinnerBloodSugar != null }
            .size
        //本次低血糖次数
        val lowBloodSugarNumber = bloodSugarList
            .filter {
                it.fastingBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.randomBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.afterMealBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.beforeLunchBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.beforeDinnerBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.afterLunchBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.afterDinnerBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true ||
                        it.beforeSleepBloodSugar?.let { value -> value < LOW_BLOOD_SUGAR_3_9 } == true
            }
            .size

        //血糖达标率
        val bloodSugarStandardRate = getBloodSugarStandardRate(bloodSugarList)
        //本次血糖达标率与上次血糖达标率的差
        var bloodSugarStandardRateDeviationValue: BigDecimal? = null
        //本次报告得分与上次报告得分的差值
        var scoreDeviationValue: BigDecimal? = null
        // 上次的数据
        this.getLastHealthSchemeManagementInfo(patientId, healthManageId)
            ?.let { lastHealthSchemeManagementInfo ->

                if (HealthManageType.valueOf(lastHealthSchemeManagementInfo.knHealthManageType) == HealthManageType.DIABETES) {
                    //上次的阶段报告
                    this.getLastStageReport(lastHealthSchemeManagementInfo.knId)
                        ?.let { lastStageReport ->
                            //上次的统计数据
                            this.getLastStageStatistics(lastStageReport.id)?.let { lastStageStatistics ->
                                //本次血糖达标率与上次血糖达标率的差
                                bloodSugarStandardRateDeviationValue =
                                    bloodSugarStandardRate - lastStageStatistics.bloodSugarStandardRate
                            }

                            //本次报告得分与上次报告得分的差值
                            lastStageReport.reportScore?.let {
                                scoreDeviationValue = totalScore - it
                            }
                        }

                }
            }

        hsDiabetesStageStatisticsTable.save(
            HsDiabetesStageStatistics.builder()
                .setId(AppIdUtil.nextId())
                .setStageReportId(reportId)
                .setFastingBloodSugarMeasureNumber(fastingBloodSugarMeasureNumber)
                .setBeforeLunchBloodSugarMeasureNumber(beforeLunchBloodSugarMeasureNumber)
                .setAfterMealBloodSugarBloodSugarMeasureNumber(afterMealBloodSugarBloodSugarMeasureNumber)
                .setLowBloodSugarNumber(lowBloodSugarNumber)
                .setBloodSugarStandardRate(bloodSugarStandardRate)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setCreatedAt(LocalDateTime.now())
                .setAbnormalDataAlertMsg(abnormalDataAlertMsg)
                .setBloodSugarStandardRateDeviationValue(bloodSugarStandardRateDeviationValue)
                .setScoreDeviationValue(scoreDeviationValue)
                .build()
        )

    }

    /**
     * 获取上次阶段统计数据
     * @param id 阶段报告Id
     * @return 上次阶段统计数据
     */
    private fun getLastStageStatistics(id: BigInteger): HsDiabetesStageStatistics? {
        return hsDiabetesStageStatisticsTable.select()
            .where(HsDiabetesStageStatisticsTable.StageReportId eq id)
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
     * 保存统计明细数据
     * @param bloodSugarList 血糖集合
     * @param reportId 报告Id
     */
    private fun saveStatisticsDetail(bloodSugarList: List<BloodSugar>, reportId: BigInteger) {
        bloodSugarList.map {
            HsDiabetesStageStatisticsDetail.builder()
                .setId(AppIdUtil.nextId())
                .setStageReportId(reportId)
                .setMeasureDatetime(it.measureDatetime)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setCreatedAt(LocalDateTime.now())
                .setFastingBloodSugar(it.fastingBloodSugar)
                .setRandomBloodSugar(it.randomBloodSugar)
                .setBeforeLunchBloodSugar(it.beforeLunchBloodSugar)
                .setBeforeDinnerBloodSugar(it.beforeDinnerBloodSugar)
                .setAfterMealBloodSugar(it.afterMealBloodSugar)
                .setAfterLunchBloodSugar(it.afterLunchBloodSugar)
                .setAfterDinnerBloodSugar(it.afterDinnerBloodSugar)
                .setBeforeSleepBloodSugar(it.beforeSleepBloodSugar)
                .build()
        }.forEach {
            hsDiabetesStageStatisticsDetailTable.save(it)
        }
    }

    /**
     * 糖尿病计算系统得分
     * @param diabetesReport 糖尿病报告生成参数
     * @param abnormalDataAlertMsgList 异常数据提醒消息
     */
    private fun getOtherScore(
        diabetesReport: DiabetesReport,
        abnormalDataAlertMsgList: MutableList<AbnormalDataAlertMsg>,
    ): Int {
        //1.血糖达标率分数
        val bloodSugarStandardRateScore = getBloodSugarStandardRateScore(diabetesReport.bloodSugarList)

        //2.血糖变化分值
        val bloodSugarChangeScore = getBloodSugarChangeScore(diabetesReport.bloodSugarList)

        // 3.饮食打卡率得分
        var dietPunchRate = 0
        var dietPunchRateScore = 0

        // 4.有氧运动打卡率得分
        var cardioPunchRate = 0
        var cardioPunchRateScore = 0

        // 5.抗阻运动打卡率得分
        var resistanceExerciseRate = 0
        var resistanceExerciseRateScore = 0

        val planFrequencyValueList = diabetesReport.planFrequencyValue
        var startDateTime = diabetesReport.startDateTime
        val endDateTime = diabetesReport.endDateTime
        if (planFrequencyValueList.isNotEmpty()) {
            //阶段
            val managementStage = diabetesReport.manageStage
            //健康计划Id与频率Id对应Map
            //健康计划
            val groupPlanMap = healthPlanClient.idGetList(planFrequencyValueList.map { it.planId })
                .filter { it.type == HealthPlanType.DIET_PLAN || it.type == HealthPlanType.EXERCISE_PROGRAM }
                .groupBy { it.group ?: "" }

            //要求打卡次数
            var expectedClockInNumber = CLOCK_IN_DIET_MAP[managementStage] ?: 0
            //实际打卡次数
            var clockInNumber = clockInRateUtils.getClockInNumber(
                this.getPlanList(groupPlanMap, DIET).map { it.id },
                startDateTime,
                endDateTime
            )
            //饮食打卡率
            dietPunchRate = getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))
            // 饮食打卡率得分
            dietPunchRateScore = clockInRateUtils.getRateScore(dietPunchRate) {
                when (it) {
                    in 0 until 25 -> 0
                    in 25 until 75 -> 5
                    else -> 10
                }
            }


            // 有氧运动打卡率得分
            var planList = this.getPlanList(groupPlanMap, AEROBIC_EXERCISE)
            //要求打卡次数
            expectedClockInNumber = getExpectedClockInNumber(
                CLOCK_IN_AEROBIC_EXERCISE_MAP[managementStage],
                planList
            )
            //实际打卡次数
            clockInNumber = clockInRateUtils.getClockInNumber(
                planList.map { it.id },
                startDateTime,
                endDateTime
            )
            //有氧运动打卡率
            cardioPunchRate = getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))
            //有氧运动打卡率得分
            cardioPunchRateScore = clockInRateUtils.getRateScore(cardioPunchRate) {
                when (it) {
                    in 0 until 25 -> 0
                    in 25 until 75 -> 2
                    else -> 5
                }
            }


            // 抗阻运动打卡率得分
            planList = this.getPlanList(groupPlanMap, RESISTANCE_EXERCISE)
            //要求打卡次数
            expectedClockInNumber =
                getExpectedClockInNumber(
                    CLOCK_IN_RESISTANCE_EXERCISE_MAP[managementStage],
                    planList
                )
            //实际打卡次数
            clockInNumber = clockInRateUtils.getClockInNumber(
                planList.map { it.id },
                startDateTime,
                endDateTime
            )
            resistanceExerciseRate = getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))
            resistanceExerciseRateScore = clockInRateUtils.getRateScore(resistanceExerciseRate) {
                when (it) {
                    in 0 until 25 -> 0
                    in 25 until 75 -> 2
                    else -> 5
                }
            }
        }

        //糖尿病随访记录
        val visitList = visitService.getDiabetesVisitList(
            diabetesReport.patientId,
            startDateTime,
            endDateTime
        )

        // 6.是否按照医嘱规律服药得分
        var regularMedicationScore = 0
        // 7.症状得分
        var symptomScore = 0
        if (visitList.isNotEmpty()) {
            regularMedicationScore = this.getRegularMedicationScore(visitList)

            symptomScore = this.getSymptomScore(visitList)
        }

        //构建异常提示信息
        //本阶段餐后2h血糖较餐前血糖的增幅，是否≥5.0 mmol/L
        if (bloodSugarChangeScore == 0) {
            abnormalDataAlertMsgList.add(
                0,
                AbnormalDataAlertMsg(BLOOD_SUGAR_HIGH_MSG_TITLE, BLOOD_SUGAR_HIGH_MSG_CONTENT)
            )
        }
        //是否按照医嘱规律服药
        if (regularMedicationScore == 0) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(REGULAR_MEDICATION_MSG_TITLE, REGULAR_MEDICATION_MSG_CONTENT)
            )
        }
        //糖化血红蛋白≥7.0
        val hbA1c = visitList.sortedByDescending { it.hbA1cDate }.firstNotNullOfOrNull { it.hbA1c }
        if (hbA1c != null && hbA1c >= HBALC_7_0) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(HBALC_MSG_TITLE, HBALC_MSG_CONTENT)
            )
        }
        //血脂
        val bloodLipidsList = indicatorClient.bloodLipidsList(
            FindListParam(
                diabetesReport.startDateTime,
                endDateTime,
                diabetesReport.patientId
            )
        )
        val msgList = mutableListOf<String>()
        val hdc =
            bloodLipidsList.sortedByDescending { it.knCreatedAt }.firstNotNullOfOrNull { it.knHighDensityLipoprotein }
        val ldc =
            bloodLipidsList.sortedByDescending { it.knCreatedAt }.firstNotNullOfOrNull { it.knLowDensityLipoprotein }
        val tc =
            bloodLipidsList.sortedByDescending { it.knCreatedAt }.firstNotNullOfOrNull { it.knTotalCholesterol }
        val tg =
            bloodLipidsList.sortedByDescending { it.knCreatedAt }.firstNotNullOfOrNull { it.knTriglycerides }
        if (hdc != null && hdc <= HDC_1_0) {
            msgList.add("高密度脂蛋白胆固醇")
        }
        if (ldc != null && ldc >= LDC_2_6) {
            msgList.add("低密度脂蛋白胆固醇")
        }
        if (tc != null && tc >= TC_4_5) {
            msgList.add("总胆固醇")
        }
        if (tg != null && tg >= TG_1_7) {
            msgList.add("甘油三酯")
        }
        val bloodLipidsMsg = msgList.joinToString("、")
        if (bloodLipidsMsg.isNotEmpty()) {
            BLOOD_LIPIDS_MSG_TITLE = BLOOD_LIPIDS_MSG_TITLE.format(bloodLipidsMsg)
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(BLOOD_LIPIDS_MSG_TITLE, BLOOD_LIPIDS_MSG_CONTENT)
            )
        }

        //吸烟（数据有效期为近3个月内的）
        startDateTime = endDateTime.minusMonths(MONTHS_3)
        val smokeList = indicatorClient.smokeList(
            FindListParam(
                startDateTime,
                endDateTime,
                diabetesReport.patientId
            )
        )
        if (smokeList.map { it.knNum }.stream().anyMatch { it > 0 }) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(SMOKING_MSG_TITLE, SMOKING_MSG_CONTENT)
            )
        }

        //饮食（患者饮食打卡率＜60%时显示下列内容）
        if (dietPunchRate < RATE_60) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(DIET_MSG_TITLE, DIET_MSG_CONTENT)
            )
        }

        //运动（患者有氧运动或抗阻运动打卡率＜60%时【异常数据提醒】部分都显示下列内容）
        if (cardioPunchRate < RATE_60 || resistanceExerciseRate < RATE_60) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(SPORT_MSG_TITLE, SPORT_MSG_CONTENT)
            )
        }

        //低血糖风险：如患者年龄＞60岁，则添加正文
        if (diabetesReport.age > AGE_60) {
            abnormalDataAlertMsgList.add(
                AbnormalDataAlertMsg(HYPOGLYCEMIA_MSG_TITLE, HYPOGLYCEMIA_MSG_CONTENT)
            )
        }

        val otherScore =
            bloodSugarStandardRateScore + bloodSugarChangeScore + dietPunchRateScore + cardioPunchRateScore + resistanceExerciseRateScore + regularMedicationScore + symptomScore

        LOGGER.info(
            "患者:{},方案:{},周期:{}~{}的{},系统计算分值总分值:{},血糖达标率分数:{},血糖变化分值:{},饮食打卡率得分:{},有氧运动打卡率得分:{},抗阻运动打卡率得分:{},规律服药得分:{},症状得分:{}",
            diabetesReport.patientId,
            diabetesReport.healthManageId,
            diabetesReport.startDateTime,
            diabetesReport.endDateTime,
            diabetesReport.reportName,
            otherScore,
            bloodSugarStandardRateScore,
            bloodSugarChangeScore,
            dietPunchRateScore,
            cardioPunchRateScore,
            resistanceExerciseRateScore,
            regularMedicationScore,
            symptomScore
        )

        return otherScore
    }


    /**
     * 症状得分
     * 本阶段内填写的糖尿病随访中《是否有如下不适症状？》问题的答案包含
     * 多饮、多食、多尿、视力模糊、感染、手脚麻木、下肢浮肿、体重明显下降
     *  0个（15分）
     *  1个（10分）
     *  ≥2个（5分）
     * @param visitList 高血压随访记录
     * @return  症状得分
     */
    private fun getSymptomScore(visitList: List<HsT2dmVisit>): Int {

        val num = visitList
            .map {
                var num = 0
                if (it.isSymptomDrink == true) {
                    num++
                }
                if (it.isSymptomEat == true) {
                    num++
                }
                if (it.isSymptomDiuresis == true) {
                    num++
                }
                if (it.isSymptomBlurred == true) {
                    num++
                }
                if (it.isSymptomHandFeetNumbness == true) {
                    num++
                }
                if (it.isSymptomLowerExtremityEdema == true) {
                    num++
                }
                if (it.isSymptomWeightLossDiagnosed == true) {
                    num++
                }
                if (it.symptomOther != null) {
                    num++
                }
                num
            }
            .maxOf { it }

        return when (num) {
            0 -> 15
            1 -> 10
            else -> 5
        }
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
     * 获取规律服药得分
     *  本阶段内填写过的糖尿病随访，是否按照医嘱规律服药填写过“间断”，则为 0分，否则为 2分
     * @param visitList 随访随访记录
     */
    private fun getRegularMedicationScore(visitList: List<HsT2dmVisit>): Int {

        //是否包含间断
        val isInterrupt = visitList
            .mapNotNull { it.drugCompliance }
            .stream()
            .anyMatch { DrugCompliance.valueOf(it) == DrugCompliance.GAP }
        return if (isInterrupt) 0 else 2
    }

    /**
     * 餐后2h血糖较餐前血糖的增幅，是否≥5.0 mmol/L
     * 首先系统识别本阶段内(早、中、晚）餐前血糖内的最低值、(早、中、晚）餐后2h血糖内的最高值.
     * (早、中、晚)餐后2h血糖最高值 - (早、中、晚）餐前血糖最低值  = 餐后2h血糖较餐前血糖的增幅
     */
    private fun getBloodSugarChangeScore(bloodSugarList: List<BloodSugar>): Int {
        val fastingBloodSugarList = bloodSugarList.mapNotNull { it.fastingBloodSugar }
        val beforeLunchBloodSugarList = bloodSugarList.mapNotNull { it.beforeLunchBloodSugar }
        val beforeDinnerBloodSugarList = bloodSugarList.mapNotNull { it.beforeDinnerBloodSugar }
        //餐前血糖集合
        val beforeBloodSugarList = mutableListOf<BigDecimal>()
        beforeBloodSugarList.addAll(fastingBloodSugarList)
        beforeBloodSugarList.addAll(beforeLunchBloodSugarList)
        beforeBloodSugarList.addAll(beforeDinnerBloodSugarList)
        val minValue = beforeBloodSugarList.minOrNull() ?: return 10


        val afterMealBloodSugarList = bloodSugarList.mapNotNull { it.afterMealBloodSugar }
        val afterLunchBloodSugarList = bloodSugarList.mapNotNull { it.afterLunchBloodSugar }
        val afterDinnerBloodSugarList = bloodSugarList.mapNotNull { it.afterDinnerBloodSugar }
        //餐后血糖集合
        val afterBloodSugarList = mutableListOf<BigDecimal>()
        afterBloodSugarList.addAll(afterMealBloodSugarList)
        afterBloodSugarList.addAll(afterLunchBloodSugarList)
        afterBloodSugarList.addAll(afterDinnerBloodSugarList)

        val maxValue = afterBloodSugarList.maxOrNull() ?: return 10

        val bloodSugarChangeValue = maxValue - minValue

        return if (bloodSugarChangeValue >= BigDecimal(5.0)) 0 else 10
    }


    /**
     * 获取血糖值达标率分值（即本阶段内口空腹血糖+餐前血糖+餐后2h血糖达标次数/本阶段内空腹血糖+餐前血糖+餐后2h血糖的总测量次数）
     * 达标率＜25%（5分）
     * 25%≤达标率＜50%（10分）
     * 50%≤达标率＜75%（20分）
     * 75%≤达标率＜100%（35分）
     */
    private fun getBloodSugarStandardRateScore(bloodSugarList: List<BloodSugar>): Int {
        //达标率
        return when (getBloodSugarStandardRate(bloodSugarList).toInt()) {
            in 0 until 25 -> 5
            in 25 until 50 -> 10
            in 50 until 75 -> 20
            else -> 35
        }
    }


    /**
     * 计算血糖达标率
     * @param bloodSugarList 血糖数据
     */
    private fun getBloodSugarStandardRate(bloodSugarList: List<BloodSugar>): BigDecimal {
        //达标数量
        val standardNum = bloodSugarList
            .filter {
                it.fastingBloodSugar?.let { value -> isFastingBloodSugarStandard(value) } ?: false
                        || it.randomBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.afterMealBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.beforeLunchBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.beforeDinnerBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.afterLunchBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.afterDinnerBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
                        || it.beforeSleepBloodSugar?.let { value -> isBloodSugarStandard(value) } ?: false
            }
            .size
        return getRate(standardNum, bloodSugarList.size)
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
     * @param diabetesReport 报告生成请求参数
     * @param score 得分
     * @param failMsg 失败原因
     * @return 报告对象
     */
    private fun saveStageReport(
        diabetesReport: DiabetesReport,
        score: BigDecimal?,
        failMsg: String? = null
    ): HsStageReport {
        val reportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(reportId)
            .setHealthSchemeManagementInfoId(diabetesReport.healthManageId)
            .setPatientId(diabetesReport.patientId)
            .setPatientName(diabetesReport.patientName)
            .setReportName(diabetesReport.reportName)
            .setReportStartDatetime(diabetesReport.startDateTime)
            .setReportEndDatetime(diabetesReport.endDateTime)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setCreatedAt(LocalDateTime.now())
            .setReportScore(score)
            .setStageReportType(StageReportType.DIABETES.name)
            .setAge(diabetesReport.age)
            .setFailMsg(failMsg)
            .build()
        hsStageReportTable.insertWithoutNull(report)
        return report
    }
}
