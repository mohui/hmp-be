package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasOptionTable
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandler
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SportEvaluatePostProcessorsHandler(
    val healthPlanManageRpcService: ManageApi,
    val clockInRpcService: ClockInApi,
    val examinationRpcService: ExaminationApi,
    val optionTable: QasOptionTable
) : EvaluatePostProcessorsHandler {

    companion object {
        const val SPORT_CODE = "SPORT"

        //推送禁忌的题目1
        val GO_ON_QUESTION_ID = BigInteger.valueOf(150001)
        val GO_ON_QUESTION_OPTION_NULL_ID = BigInteger.valueOf(15000107)

        //题目2-有氧运动 题目3-抗阻运动 运动频率：1周5天
        const val AEROBIC_EXERCISE = "有氧运动"
        val AEROBIC_EXERCISE_QUESTION_ID = BigInteger.valueOf(150002)
        val AEROBIC_EXERCISE_QUESTION_OPTION_NULL_ID = BigInteger.valueOf(15000215)
        const val RESISTANCE_EXERCISE = "抗阻运动"
        val RESISTANCE_EXERCISE_QUESTION_ID = BigInteger.valueOf(150003)
        val RESISTANCE_EXERCISE_QUESTION_OPTION_NULL_ID = BigInteger.valueOf(15000307)
        val ONE_WEEKS_FIVE_DAYS_FREQUENCY = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 5,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 5
            ),
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 5,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 5
            ),
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        // (完成运动情况反馈) 4周1次
        val EXERCISE_PROGRAM_FOUR_WEEKS_ONE_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 4,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 1
            )
        )

        val OPTION_CODE_SPORT = mapOf(
            Pair("SPORT_150002_01", "步行"),
            Pair("SPORT_150002_02", "慢跑"),
            Pair("SPORT_150002_03", "骑自行车"),
            Pair("SPORT_150002_04", "球类"),
            Pair("SPORT_150002_05", "健身操"),
            Pair("SPORT_150002_06", "游泳"),
            Pair("SPORT_150002_07", "太极拳"),
            Pair("SPORT_150002_08", "八段锦"),
            Pair("SPORT_150002_09", "广场舞"),
            Pair("SPORT_150002_10", "呼啦圈"),
            Pair("SPORT_150002_11", "瑜伽"),
            Pair("SPORT_150002_12", "跳绳"),
            Pair("SPORT_150002_13", "踢键子"),
            Pair("SPORT_150002_14", "广播体操"),
            Pair("SPORT_150003_01", "拉力绳"),
            Pair("SPORT_150003_02", "弹力带"),
            Pair("SPORT_150003_03", "俯卧撑"),
            Pair("SPORT_150003_04", "仰卧起坐"),
            Pair("SPORT_150003_05", "平板支撑"),
            Pair("SPORT_150003_06", "哑铃")
        )
    }

    override fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>) {
        val answerBy = answerRecord.answerBy
        val healthPlans: MutableList<FrequencyHealthParams> = mutableListOf()
        // 先打卡尽快完成运动评估, 在删除。如果仅打卡隐藏, 可能会有很多"尽快完成运动评估"并且永远不显示的会打卡
        clockNotEvaluateSportRemindPlan(answerBy)
        //删除未进行评估(运动计划)的运动计划
        removeNotEvaluateSportRemindPlan(answerBy)

        //创建新的运动提醒计划
        createSportRemindPlan(answerRecord, answerResultList).let {
            healthPlans.addAll(it)
        }

        //创建运动提醒计划调整提醒计划
        createSportRemindAdjustPlan(answerBy).let {
            healthPlans.addAll(it)
        }

        healthPlanManageRpcService.addHealthPlan(
            AddHealthPlanParam(
                healthPlans = healthPlans,
                drugPlans = listOf()
            )
        )
    }


    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.examinationPaperCode == SPORT_CODE
    }

    override fun getOrder(): Int {
        return 0
    }

    private fun createSportRemindPlan(
        answerRecord: QasQuestionsAnswerRecord,
        answerResultList: List<QasQuestionsAnswerResult>
    ):List<FrequencyHealthParams> {
        val patientId = answerRecord.answerBy
        val recordId = answerRecord.id
        //选择第一题不需要创建任何运动计划，会同步运动禁忌数据
        val sportTabooList = mutableListOf<ExaminationPaperOption>()
        if (answerResultList
                .stream()
                .anyMatch { it.questionsId == GO_ON_QUESTION_ID && it.optionId != GO_ON_QUESTION_OPTION_NULL_ID }
        ) {
            val optionIdList: List<BigInteger> = answerResultList.map { it.optionId }
            val optionIdMap: Map<BigInteger, BigInteger> = answerResultList.associate { it.optionId to it.id }
            optionTable.select()
                .where(QasOptionTable.Id `in` optionIdList.map { it.arg })
                .where(QasOptionTable.IsDel eq false)
                .find()
                .forEach { option ->
                    optionIdMap[option.id]?.let { answerResultId ->
                        sportTabooList.add(
                            ExaminationPaperOption(
                                knAnswerRecordId = recordId,
                                knAnswerResultId = answerResultId,
                                knQuestionsId = option.questionsId,
                                knOptionId = option.id,
                                knOptionLabel = option.optionLabel
                            )
                        )
                    }
                }
        }

        val frequencyList: MutableList<FrequencyHealthParams> = mutableListOf()

        val startDate = LocalDate.now().atStartOfDay()

        answerResultList
            .filter {
                (it.questionsId == AEROBIC_EXERCISE_QUESTION_ID && it.optionId != AEROBIC_EXERCISE_QUESTION_OPTION_NULL_ID)
                        || (it.questionsId == RESISTANCE_EXERCISE_QUESTION_ID && it.optionId != RESISTANCE_EXERCISE_QUESTION_OPTION_NULL_ID)
            }
            .forEach {
                //运动名称
                val name = OPTION_CODE_SPORT.getOrDefault(it.optionCode, "")
                //运动分组及频次
                val group: String?
                if (it.questionsId == AEROBIC_EXERCISE_QUESTION_ID) {
                    group = AEROBIC_EXERCISE
                } else {
                    group = RESISTANCE_EXERCISE
                }
                frequencyList.add(
                    FrequencyHealthParams(
                        name = name,
                        type = HealthPlanType.EXERCISE_PROGRAM,
                        patientId = patientId,
                        subName = "",
                        desc = "每周5次，每次30分钟",
                        cycleStartTime = startDate,
                        cycleEndTime = null,
                        group = group,
                        frequencys = ONE_WEEKS_FIVE_DAYS_FREQUENCY
                    )
                )
            }

        //同步运动禁忌
        examinationRpcService.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = SPORT_CODE,
                knExaminationPaperOptionList = sportTabooList
            )
        )
        return frequencyList
    }

    private fun createSportRemindAdjustPlan(
        patientId: BigInteger,
    ): List<FrequencyHealthParams> {
        // 创建运动调整提醒(运动计划)
        val frequencyHealthParams = FrequencyHealthParams(
            name = "完成运动情况反馈",
            type = HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND,
            patientId = patientId,
            subName = "",
            desc = "",
            cycleStartTime = LocalDate.now().plusDays(28).atStartOfDay(),
            cycleEndTime = null,
            clockDisplay = false,
            frequencys = EXERCISE_PROGRAM_FOUR_WEEKS_ONE_SEQUENCE
        )
        return listOf(frequencyHealthParams)
    }

    // 打卡
    private fun clockNotEvaluateSportRemindPlan(patientId: BigInteger) {
        clockInRpcService.saveClockIn(
            ClockInRequest(
                patientId = patientId,
                healthPlanType = HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                currentDateTime = LocalDateTime.now()
            )
        )
    }

    private fun removeNotEvaluateSportRemindPlan(patientId: BigInteger) {
        //删除未进行评估(运动计划)的运动计划, 删除运动调整提醒(运动计划), 运动方案
        healthPlanManageRpcService.delHealthPlanByPatientIdAndTypes(
            DelHealthPlanByPatientIdAndTypes(
                patientId = patientId,
                types = listOf(
                    HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                    HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND,
                    HealthPlanType.EXERCISE_PROGRAM
                )
            )
        )
    }
}
