package com.bjknrt.question.answering.system.expost.question.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.IndicatorEnum
import com.bjknrt.health.indicator.vo.SelectRecentlyValidPatientIndicatorParam
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.expost.question.QuestionPostProcessorsHandler
import com.bjknrt.question.answering.system.vo.Questions
import com.bjknrt.question.answering.system.vo.QuestionsOptions
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

@Component
class OptionValuePostProcessorsHandler(
    val indicatorRpcService: IndicatorApi,
    val patientInfoRpcService: PatientApi
) : QuestionPostProcessorsHandler {

    companion object {

        //问题选项Id集合
        val OPTION_ID_MAP = mapOf(

            //行为习惯随访-脑卒中
            BigInteger.valueOf(25000101) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(25000102) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(25000201) to IndicatorEnum.WAISTLINE,

            //行为习惯随访-高血压
            BigInteger.valueOf(14000101) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(14000102) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(14000201) to IndicatorEnum.WAISTLINE,

            //行为习惯随访-糖尿病
            BigInteger.valueOf(21000101) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(21000102) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(21000201) to IndicatorEnum.WAISTLINE,

            //行为习惯随访-慢阻肺
            BigInteger.valueOf(31000101) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(31000102) to IndicatorEnum.BODY_WEIGHT,

            //行为习惯随访-冠心病
            BigInteger.valueOf(32000101) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(32000102) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(32000201) to IndicatorEnum.WAISTLINE,

            //五病评估
            BigInteger.valueOf(3011) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(3021) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(3031) to IndicatorEnum.WAISTLINE,
            BigInteger.valueOf(3051) to IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE,
            BigInteger.valueOf(3061) to IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE,
            BigInteger.valueOf(3071) to IndicatorEnum.FASTING_BLOOD_SUGAR,
            BigInteger.valueOf(3081) to IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL,

            //ASCVD风险评估
            BigInteger.valueOf(20000401) to IndicatorEnum.BODY_HEIGHT,
            BigInteger.valueOf(20000402) to IndicatorEnum.BODY_WEIGHT,
            BigInteger.valueOf(20000301) to IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE,
            BigInteger.valueOf(20000302) to IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE,
            BigInteger.valueOf(20000201) to IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN,
            BigInteger.valueOf(20000202) to IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN,
            BigInteger.valueOf(20000203) to IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL,
            BigInteger.valueOf(20000204) to IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES,

            //糖尿病发病风险评估
            BigInteger.valueOf(180201) to IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE,
            BigInteger.valueOf(180401) to IndicatorEnum.BMI,
            BigInteger.valueOf(180501) to IndicatorEnum.WAISTLINE,
        )

        val OPTION_ID_PATIENT_MAP = mapOf(
            BigInteger.valueOf(28000101) to fun(patient: PatientInfoResponse): String = patient.age.toString(),
            BigInteger.valueOf(180101) to fun(patient: PatientInfoResponse): String = patient.age.toString()
        )
    }

    override fun execute(questionsOption: List<Questions>): List<Questions> {

        val patientId = AppSecurityUtil.currentUserIdWithDefault()
        //查询指标值
        val indicatorMap = indicatorRpcService.selectRecentlyValidIndicatorByType(
            SelectRecentlyValidPatientIndicatorParam(
                patientId,
                OPTION_ID_MAP.values.distinct().toList()
            )
        ).associate { it.key to (it._value ?: BigDecimal.ZERO) }

        var questionsList = questionsOption.map { this.transformQuestion(it, indicatorMap) }

        if (questionsOption.flatMap { it.options.map { option -> option.optionId } }
                .stream()
                .anyMatch { OPTION_ID_PATIENT_MAP.keys.contains(it) }
        ) {
            val patientInfo = patientInfoRpcService.getPatientInfo(patientId)
            questionsList = questionsList.map {
                it.copy(options = it.options.map { option ->
                    this.transformOption(option, patientInfo)
                })
            }
        }

        return questionsList
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return true
    }

    override fun getOrder(): Int {
        return 0
    }

    private fun transformQuestion(questions: Questions, indicatorMap: Map<IndicatorEnum, BigDecimal>): Questions {
        return questions.copy(options = questions.options.map { this.transformOption(it, indicatorMap) })
    }

    private fun transformOption(
        option: QuestionsOptions, indicatorMap: Map<IndicatorEnum, BigDecimal>
    ): QuestionsOptions {
        if (OPTION_ID_MAP.containsKey(option.optionId)) {
            return option.copy(optionValue = indicatorMap[OPTION_ID_MAP[option.optionId]]?.toString() ?: "")
        }
        return option
    }

    private fun transformOption(
        option: QuestionsOptions, patientInfo: PatientInfoResponse
    ): QuestionsOptions {
        if (OPTION_ID_PATIENT_MAP.containsKey(option.optionId)) {
            return option.copy(optionValue = OPTION_ID_PATIENT_MAP[option.optionId]?.let { it(patientInfo) } ?: "")
        }
        return option
    }
}
