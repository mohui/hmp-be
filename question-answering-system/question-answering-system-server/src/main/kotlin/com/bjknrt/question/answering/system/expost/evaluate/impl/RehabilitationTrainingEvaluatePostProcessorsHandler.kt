package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandler
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class RehabilitationTrainingEvaluatePostProcessorsHandler(
    val healthPlanManageRpcService: ManageApi,
    val clockInRpcService: ClockInApi
) : EvaluatePostProcessorsHandler {

    companion object {
        const val CODE = "REHABILITATION_TRAINING"

        const val ROUTINE_TRAINING_PREFIX = "常规训练-"
        const val INTELLIGENCE_TRAINING_PREFIX = "智能训练-"

        const val ROUTINE_TRAINING = "完成常规训练"
        const val INTELLIGENCE_TRAINING = "完成智能训练"

        const val QUESTIONS_1_OPTION_1 = "REHABILITATION_TRAINING_260001_26000101"
        const val QUESTIONS_1_OPTION_2 = "REHABILITATION_TRAINING_260001_26000102"
        const val QUESTIONS_1_OPTION_3 = "REHABILITATION_TRAINING_260001_26000103"
        const val QUESTIONS_1_OPTION_4 = "REHABILITATION_TRAINING_260001_26000104"
        const val QUESTIONS_1_OPTION_5 = "REHABILITATION_TRAINING_260001_26000105"

        const val QUESTIONS_2_OPTION_1 = "REHABILITATION_TRAINING_260002_26000201"
        const val QUESTIONS_2_OPTION_2 = "REHABILITATION_TRAINING_260002_26000202"
        const val QUESTIONS_2_OPTION_3 = "REHABILITATION_TRAINING_260002_26000203"
        const val QUESTIONS_2_OPTION_4 = "REHABILITATION_TRAINING_260002_26000204"
        const val QUESTIONS_2_OPTION_5 = "REHABILITATION_TRAINING_260002_26000205"

        //频率：1天2次
        val ONE_DAY_TWO_SEQUENCE = listOf(
            Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 2
            )
        )
    }

    override fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>) {
        val answerBy = answerRecord.answerBy
        // 提醒康复训练计划不删除, 打卡,打卡完成后不显示
        this.clockRehabilitationTrainingRemind(answerBy)

        // 删除智能训练, 常规训练
        this.removeHealthPlanRemind(
            patientId = answerBy,
            healthPlanTypes = listOf(
                HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT
            )
        )

        val healthPlans: MutableList<FrequencyHealthParams> = mutableListOf()
        //创建常规训练计划（根据选项创建计划）
        this.createRoutineTrainRemindPlan(answerBy, answerResultList).let {
            healthPlans.addAll(it)
        }
        //创建智能训练计划（根据选项创建计划）
        this.createIntelligenceTrainRemindPlan(answerBy, answerResultList).let {
            healthPlans.addAll(it)
        }

        healthPlans.takeIf { it.isNotEmpty() }?.let {
            // 创建新的常规训练计划, 智能训练
            healthPlanManageRpcService.addHealthPlan(
                AddHealthPlanParam(
                    healthPlans = it,
                    drugPlans = listOf()
                )
            )
        }
    }

    private fun clockRehabilitationTrainingRemind(answerBy: BigInteger) {
        // 根据患者ID和健康计划类型打卡
        clockInRpcService.saveClockIn(
            ClockInRequest(
                patientId = answerBy,
                healthPlanType = HealthPlanType.REHABILITATION_TRAINING_REMIND,
                currentDateTime = LocalDateTime.now()
            )
        )
    }

    private fun createIntelligenceTrainRemindPlan(
        answerBy: BigInteger,
        answerResultList: List<QasQuestionsAnswerResult>
    ):List<FrequencyHealthParams> {

        val optionCodeList = answerResultList.map { it.optionCode }

        val frequencyList: MutableList<FrequencyHealthParams> = mutableListOf()

        val startDate = LocalDate.now().atStartOfDay()

        if (optionCodeList.contains(QUESTIONS_2_OPTION_1)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = INTELLIGENCE_TRAINING_PREFIX + "脑机接口",
                    type = HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = INTELLIGENCE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_2_OPTION_2)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = INTELLIGENCE_TRAINING_PREFIX + "康复机器人",
                    type = HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = INTELLIGENCE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_2_OPTION_3)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = INTELLIGENCE_TRAINING_PREFIX + "平衡反馈训练",
                    type = HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = INTELLIGENCE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_2_OPTION_4)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = INTELLIGENCE_TRAINING_PREFIX + "虚拟现实",
                    type = HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = INTELLIGENCE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_2_OPTION_5)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = INTELLIGENCE_TRAINING_PREFIX + "其他智能设备辅助下训练",
                    type = HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = INTELLIGENCE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        return frequencyList
    }

    private fun createRoutineTrainRemindPlan(
        answerBy: BigInteger,
        answerResultList: List<QasQuestionsAnswerResult>
    ):List<FrequencyHealthParams> {
        val optionCodeList = answerResultList.map { it.optionCode }

        val frequencyList: MutableList<FrequencyHealthParams> = mutableListOf()

        val startDate = LocalDate.now().atStartOfDay()

        if (optionCodeList.contains(QUESTIONS_1_OPTION_1)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = ROUTINE_TRAINING_PREFIX + "运动疗法",
                    type = HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = ROUTINE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_1_OPTION_2)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = ROUTINE_TRAINING_PREFIX + "作业疗法",
                    type = HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = ROUTINE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_1_OPTION_3)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = ROUTINE_TRAINING_PREFIX + "认知训练",
                    type = HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = ROUTINE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_1_OPTION_4)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = ROUTINE_TRAINING_PREFIX + "言语训练",
                    type = HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = ROUTINE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        if (optionCodeList.contains(QUESTIONS_1_OPTION_5)) {
            frequencyList.add(
                FrequencyHealthParams(
                    name = ROUTINE_TRAINING_PREFIX + "吞咽训练",
                    type = HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    patientId = answerBy,
                    subName = "",
                    desc = "每天2次",
                    cycleStartTime = startDate,
                    cycleEndTime = null,
                    group = ROUTINE_TRAINING,
                    frequencys = ONE_DAY_TWO_SEQUENCE
                )
            )
        }
        return frequencyList
    }

    private fun removeHealthPlanRemind(patientId: BigInteger, healthPlanTypes: List<HealthPlanType>) {
        // 先删除常规训练, 智能训练
        healthPlanManageRpcService.delHealthPlanByPatientIdAndTypes(
            DelHealthPlanByPatientIdAndTypes(
                patientId = patientId,
                types = healthPlanTypes
            )
        )
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.examinationPaperCode == CODE
    }

    override fun getOrder(): Int {
        return 7
    }
}
