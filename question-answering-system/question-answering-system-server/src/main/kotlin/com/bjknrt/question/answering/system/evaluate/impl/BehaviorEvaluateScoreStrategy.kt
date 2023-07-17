package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import com.bjknrt.question.answering.system.util.getBmiValue
import com.bjknrt.question.answering.system.util.waistlineIsStandard
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class BehaviorEvaluateScoreStrategy(
    val patientInfoRpcService: PatientApi,
) : EvaluateScoreStrategy {
    val optionCodeList = listOf(
        "BEHAVIOR_140003_1",
        "BEHAVIOR_140003_2",
        "BEHAVIOR_140004_1",
        "BEHAVIOR_140004_2",
        "BEHAVIOR_140005_1",
        "BEHAVIOR_140005_2",
        "BEHAVIOR_140006_1",
        "BEHAVIOR_140006_2",
    )

    val heightOptionCode = "BEHAVIOR_140001_1"
    val weightOptionCode = "BEHAVIOR_140001_2"
    val waistlineOptionCode = "BEHAVIOR_140002_1"

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {

        //匹配上述定义的code编码，符合则累加value值
        val totalScore = this.getSumScore(options)

        //第一题：根据BMI值计算得分，累加到总分中
        val bmiScore = getBmiScore(options)

        //第二题：根据腰围计算得分，累加到总分中
        val waistlineScore = getWaistline(options)

        return totalScore + bmiScore + waistlineScore
    }

    fun getWaistline(options: Collection<EvaluateScoreOption>): BigDecimal {
        val patientInfo = patientInfoRpcService.getPatientInfo(AppSecurityUtil.currentUserIdWithDefault())
        val waistline = options.firstOrNull { it.optionCode == waistlineOptionCode }?.optionValue?.toDouble()
        if (waistlineIsStandard(patientInfo.gender, waistline)) {
            return BigDecimal.valueOf(5)
        }
        return BigDecimal.ZERO
    }

    fun getBmiScore(options: Collection<EvaluateScoreOption>): BigDecimal {
        val optionMap = options.associate { it.optionCode to it.optionValue }
        if (optionMap.containsKey(heightOptionCode) && optionMap.containsKey(weightOptionCode)) {
            val bmiValue = getBmiValue(
                optionMap[heightOptionCode]?.toBigDecimal(),
                optionMap[weightOptionCode]?.toBigDecimal()
            )
            if (bmiValue < BigDecimal.valueOf(24)) {
                return BigDecimal.valueOf(5)
            }
        }
        return BigDecimal.ZERO
    }

    fun getSumScore(options: Collection<EvaluateScoreOption>): BigDecimal {
        return options.filter { optionCodeList.contains(it.optionCode) }
            .mapNotNull {
                it.optionScore
            }.takeIf { it.isNotEmpty() }
            ?.let {
                it.reduce { acc, item -> acc + item }
            } ?: BigDecimal.ZERO
    }

    override fun getCode(): String {
        return "BEHAVIOR"
    }
}
