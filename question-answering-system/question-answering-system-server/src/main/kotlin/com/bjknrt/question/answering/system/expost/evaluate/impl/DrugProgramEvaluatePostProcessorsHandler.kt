package com.bjknrt.question.answering.system.expost.evaluate.impl

import cn.hutool.core.date.DateUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandler
import com.bjknrt.question.answering.system.interpret.impl.DrugProgramInterpretStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DrugProgramEvaluatePostProcessorsHandler(
    val healthPlanManageRpcService: ManageApi,
    val clockInRpcService: ClockInApi,
    val examinationRpcService: ExaminationApi,
) : EvaluatePostProcessorsHandler {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper()
        const val DRUG_PROGRAM_CODE = "DRUG_PROGRAM"

        // 高血压问题1: 目前是否服用药问题
        val HYPERTENSION_QUESTIONS_ID_1 = BigInteger.valueOf(330001)
        // 高血压问题2
        val HYPERTENSION_QUESTIONS_ID_2 = BigInteger.valueOf(330002)
        // 高血压问题1选项的1: 是
        val HYPERTENSION_QUESTIONS_1_OPTION_ID_1 = BigInteger.valueOf(33000101)
        // 高血压问题2的选项1
        const val HYPERTENSION_QUESTIONS_2_OPTION_1 = "HYPERTENSION_DRUG_PROGRAM_330002_1"
        const val HYPERTENSION_QUESTIONS_2_OPTION_2 = "HYPERTENSION_DRUG_PROGRAM_330002_2"
        const val HYPERTENSION_QUESTIONS_2_OPTION_3 = "HYPERTENSION_DRUG_PROGRAM_330002_3"
        const val HYPERTENSION_QUESTIONS_2_OPTION_4 = "HYPERTENSION_DRUG_PROGRAM_330002_4"
        // 高血压问题3的选项1: 是
        const val HYPERTENSION_QUESTIONS_3_OPTION_1 = "HYPERTENSION_DRUG_PROGRAM_330003_1"

        // 糖尿病问题1: 目前是否服用药问题
        val DIABETES_QUESTIONS_ID_1 = BigInteger.valueOf(340001)
        // 糖尿病问题2
        val DIABETES_QUESTIONS_ID_2 = BigInteger.valueOf(340002)
        // 糖尿病问题1选项的1: 是
        val DIABETES_QUESTIONS_1_OPTION_ID_1 = BigInteger.valueOf(34000101)
        // 糖尿病问题2的选项1
        const val DIABETES_QUESTIONS_2_OPTION_1 = "DIABETES_DRUG_PROGRAM_340002_1"
        const val DIABETES_QUESTIONS_2_OPTION_2 = "DIABETES_DRUG_PROGRAM_340002_2"
        const val DIABETES_QUESTIONS_2_OPTION_3 = "DIABETES_DRUG_PROGRAM_340002_3"
        const val DIABETES_QUESTIONS_2_OPTION_4 = "DIABETES_DRUG_PROGRAM_340002_4"
        // 糖尿病问题3的选项1: 是
        const val DIABETES_QUESTIONS_3_OPTION_1 = "DIABETES_DRUG_PROGRAM_340003_1"
    }

    override fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>) {
        val answerBy = answerRecord.answerBy

        // 用药提醒健康计划type
        var drugPlanType: HealthPlanType? = null
        // 未进行用药评估type
        var notEvaluatedType: HealthPlanType? = null
        // 创建高血压用药提醒
        if (answerResultList.stream().anyMatch { it.questionsId == HYPERTENSION_QUESTIONS_ID_1 }) {
            drugPlanType = HealthPlanType.HYPERTENSION_DRUG_PROGRAM
            // 未进行用药评估(高血压)
            notEvaluatedType = HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED
        } else if (answerResultList.stream().anyMatch { it.questionsId == DIABETES_QUESTIONS_ID_1 }) {
            drugPlanType = HealthPlanType.DIABETES_DRUG_PROGRAM
            // 未进行用药评估(糖尿病)
            notEvaluatedType = HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED
        }

        if (notEvaluatedType == null || drugPlanType == null) {
            throw KatoException(AppSpringUtil.getMessage("question.option.error"))
        }

        // 打卡未进行用药评估
        clockDrugProgramNotEvaluatedRemindPlan(answerBy, notEvaluatedType)
        // 删除未进行用药评估, 用药提醒计划
        deleteDrugProgramNotEvaluatedRemindPlan(answerBy, listOf(notEvaluatedType, drugPlanType))
        // 创建用药提醒
        createDrugRemindPlan(drugPlanType, answerRecord, answerResultList)
        // 同步患者当前方案的问卷选项
        saveSyncCurrentSchemeExaminationAdapter(answerRecord, answerResultList)
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == DRUG_PROGRAM_CODE
    }

    override fun getOrder(): Int {
        return 14
    }

    // 打卡未进行用药评估
    private fun clockDrugProgramNotEvaluatedRemindPlan(patientId: BigInteger, notEvaluatedType: HealthPlanType) {
        clockInRpcService.saveClockIn(
            ClockInRequest(
                patientId = patientId,
                healthPlanType = notEvaluatedType,
                currentDateTime = LocalDateTime.now()
            )
        )
    }

    // 删除未进行用药评估, 用药提醒计划
    private fun deleteDrugProgramNotEvaluatedRemindPlan(patientId: BigInteger, healthPlanTypes:List<HealthPlanType>) {
        healthPlanManageRpcService.delHealthPlanByPatientIdAndTypes(
            DelHealthPlanByPatientIdAndTypes(
                patientId = patientId,
                types = healthPlanTypes,
            )
        )
    }

    /**
     * 创建用药提醒
     */
    private fun createDrugRemindPlan(
        drugPlanType: HealthPlanType,
        answerRecord: QasQuestionsAnswerRecord,
        answerResultList: List<QasQuestionsAnswerResult>
    ) {
        // 患者ID
        val patientId = answerRecord.answerBy
        // 定义用药提醒类型数组
        var drugPlans: List<AddDrugRemindParams> = listOf()
        val startDate = LocalDate.now().atStartOfDay()
        // 创建高血压用药提醒
        if (drugPlanType == HealthPlanType.HYPERTENSION_DRUG_PROGRAM) {
            drugPlans = createHypertensionDrugRemindPlan(
                startDate,
                patientId,
                drugPlanType,
                answerResultList
            )
        } else if (drugPlanType == HealthPlanType.DIABETES_DRUG_PROGRAM) {
            // 创建糖尿病健康计划
            drugPlans = createDiabetesDrugRemindPlan(
                startDate,
                patientId,
                drugPlanType,
                answerResultList
            )
        }

        drugPlans.takeIf { it.isNotEmpty() }?.let {
            healthPlanManageRpcService.addHealthPlan(
                AddHealthPlanParam(
                    healthPlans = listOf(),
                    drugPlans = it
                )
            )
        }
    }

    /**
     * 创建高血压用药提醒计划
     */
    private fun createHypertensionDrugRemindPlan(
        cycleStartTime: LocalDateTime,
        patientId: BigInteger,
        type: HealthPlanType,
        answerResultList: List<QasQuestionsAnswerResult>
    ): List<AddDrugRemindParams> {
        // 定义用药提醒类型数组
        val drugPlans = mutableListOf<AddDrugRemindParams>()
        // 如果第一题选择了是: 创建用药提醒. 否: 发送一个空的创建用药提醒(删除),
        if (
            answerResultList.stream().anyMatch {
                it.questionsId == HYPERTENSION_QUESTIONS_ID_1 && it.optionId == HYPERTENSION_QUESTIONS_1_OPTION_ID_1
            }
        ) {
            // 查找第三题选项 是, 查找到, 选择了是, 是可以推送. 查找不到, 就是选择了否, 不推送
            val isUsed = answerResultList.find { it.optionCode == HYPERTENSION_QUESTIONS_3_OPTION_1 } != null

            // 筛选出所有的第二题, 即拼接用药提醒
            val option2Details = answerResultList.filter { it.questionsId == HYPERTENSION_QUESTIONS_ID_2 }

            // 以同一问题回答次数key, 第二题相同key的其他记录list为value
            val option2DetailsMap: Map<Int, List<QasQuestionsAnswerResult>> =
                option2Details.groupBy { it.questionsAnsweredCount }
            // 根据填的药, 拼接提醒
            option2DetailsMap.forEach { item ->
                // 药品名称
                var drugName: String? = null
                // 剂量
                var subName: String? = null
                // 时间
                var time: java.time.LocalTime? = null
                // 频率, 周一到周日
                var frequencys: List<Week>? = null
                // 频率
                for (forIt in item.value) {
                    if (forIt.optionCode == HYPERTENSION_QUESTIONS_2_OPTION_1) drugName = forIt.optionValue
                    if (forIt.optionCode == HYPERTENSION_QUESTIONS_2_OPTION_2) subName = forIt.optionValue
                    if (forIt.optionCode == HYPERTENSION_QUESTIONS_2_OPTION_3) {
                        // 把字符串数组转换为数组
                        val paramWeeks: Array<String> =
                            objectMapper.readValue(forIt.optionValue, arrayOf<String>()::class.java)
                        frequencys = paramWeeks.map { Week.valueOf(it) }
                    }
                    if (forIt.optionCode == HYPERTENSION_QUESTIONS_2_OPTION_4)
                        time = DateUtil.parseTime(forIt.optionValue).toLocalDateTime().toLocalTime()
                }
                drugPlans.add(
                    AddDrugRemindParams(
                        patientId = patientId,
                        drugName = drugName ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        isUsed = isUsed,
                        time = time ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        frequencys = frequencys ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        type = type,
                        cycleStartTime = cycleStartTime,
                        cycleEndTime = null,
                        subName = subName
                    )
                )
            }
        }
        return drugPlans;
    }

    /**
     * 创建糖尿病用药提醒计划
     * @param cycleStartTime: 开始时间
     * @param patientId: 患者ID
     */
    private fun createDiabetesDrugRemindPlan(
        cycleStartTime: LocalDateTime,
        patientId: BigInteger,
        type: HealthPlanType,
        answerResultList: List<QasQuestionsAnswerResult>
    ): List<AddDrugRemindParams> {
        // 定义用药提醒类型数组
        val drugPlans = mutableListOf<AddDrugRemindParams>()
        // 如果第一题选择了是: 创建用药提醒. 否: 发送一个空的创建用药提醒(删除),
        if (
            answerResultList.stream().anyMatch {
                it.questionsId == DIABETES_QUESTIONS_ID_1 && it.optionId == DIABETES_QUESTIONS_1_OPTION_ID_1
            }
        ) {
            // 查找第三题选项 是, 查找到, 选择了是, 是可以推送. 查找不到, 就是选择了否, 不推送
            val isUsed = answerResultList.find { it.optionCode == DIABETES_QUESTIONS_3_OPTION_1 } != null

            // 筛选出所有的第二题, 即拼接用药提醒
            val option2Details = answerResultList.filter { it.questionsId == DIABETES_QUESTIONS_ID_2 }

            // 以同一问题回答次数key, 第二题相同key的其他记录list为value
            val option2DetailsMap: Map<Int, List<QasQuestionsAnswerResult>> =
                option2Details.groupBy { it.questionsAnsweredCount }

            // 根据填的药, 拼接提醒
            option2DetailsMap.forEach { item ->
                // 药品名称
                var drugName: String? = null
                // 剂量
                var subName: String? = null
                // 时间
                var time: java.time.LocalTime? = null
                // 频率, 周一到周日
                var frequencys: List<Week>? = null
                // 频率
                for (forIt in item.value) {

                    if (forIt.optionCode == DIABETES_QUESTIONS_2_OPTION_1) drugName = forIt.optionValue
                    if (forIt.optionCode == DIABETES_QUESTIONS_2_OPTION_2) subName = forIt.optionValue
                    if (forIt.optionCode == DIABETES_QUESTIONS_2_OPTION_3) {
                        // 把字符串数组转换为数组
                        val paramWeeks: Array<String> =
                            objectMapper.readValue(forIt.optionValue, arrayOf<String>()::class.java)
                        frequencys = paramWeeks.map { Week.valueOf(it) }

                    }
                    if (forIt.optionCode == DIABETES_QUESTIONS_2_OPTION_4)
                        time = DateUtil.parseTime(forIt.optionValue).toLocalDateTime().toLocalTime()
                }
                drugPlans.add(
                    AddDrugRemindParams(
                        patientId = patientId,
                        drugName = drugName ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        isUsed = isUsed,
                        time = time ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        frequencys = frequencys ?: throw KatoException(AppSpringUtil.getMessage("question.option.error")),
                        type = type,
                        cycleStartTime = cycleStartTime,
                        cycleEndTime = null,
                        subName = subName
                    )
                )
            }
        }
        return drugPlans
    }

    /**
     * 同步患者当前方案的问卷选项
     */
    private fun saveSyncCurrentSchemeExaminationAdapter(
        answerRecord: QasQuestionsAnswerRecord,
        answerResultList: List<QasQuestionsAnswerResult>
    ) {
        val optionCodeMap = DrugProgramInterpretStrategy.optionCodeMap
        // 患者ID
        val patientId = answerRecord.answerBy
        // 答题记录ID
        val recordId = answerRecord.id
        // 问卷唯一标识
        val examinationPaperCode = answerRecord.examinationPaperCode
        // 答题记录
        val examinationPaperOptionList = mutableListOf<ExaminationPaperOption>()

        answerResultList.forEach { resultModel ->
            if (optionCodeMap[resultModel.optionCode] != null) {
                examinationPaperOptionList.add(
                    ExaminationPaperOption(
                        knAnswerRecordId = recordId,
                        knAnswerResultId = resultModel.id,
                        knQuestionsId = resultModel.questionsId,
                        knOptionId = resultModel.optionId,
                        knOptionLabel = resultModel.optionValue,
                        knMessage = optionCodeMap[resultModel.optionCode]
                    )
                )
            }
        }
        // 同步患者当前方案的问卷选项
        examinationRpcService.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = examinationPaperCode,
                knExaminationPaperOptionList = examinationPaperOptionList
            )
        )


    }
}