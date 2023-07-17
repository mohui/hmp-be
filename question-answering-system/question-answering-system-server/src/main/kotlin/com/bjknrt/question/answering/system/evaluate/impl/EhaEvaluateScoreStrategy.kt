package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class EhaEvaluateScoreStrategy : EvaluateScoreStrategy {

    val optionCodeMap = mapOf(
        Pair("eha_1_b", BigDecimal.valueOf(1)),
        Pair("eha_1_d", BigDecimal.valueOf(1)),
        Pair("eha_2_c", BigDecimal.valueOf(1)),
        Pair("eha_2_d", BigDecimal.valueOf(1)),
        Pair("eha_3_c", BigDecimal.valueOf(1)),
        Pair("eha_3_d", BigDecimal.valueOf(1)),
        Pair("eha_4_b", BigDecimal.valueOf(1)),
        Pair("eha_4_c", BigDecimal.valueOf(1)),
        Pair("eha_4_d", BigDecimal.valueOf(1)),
        Pair("eha_5_d", BigDecimal.valueOf(1)),
        Pair("eha_5_e", BigDecimal.valueOf(1)),
        Pair("eha_6_a", BigDecimal.valueOf(1)),
        Pair("eha_6_b", BigDecimal.valueOf(1)),
        Pair("eha_6_c", BigDecimal.valueOf(1)),
    )

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        val totalScore = options
            .filter { optionCodeMap.keys.contains(it.optionCode?.lowercase(Locale.getDefault()) ?: "") }
            .map { optionCodeMap.getOrDefault(it.optionCode?.lowercase(Locale.getDefault()) ?: "", BigDecimal.ZERO) }
            .takeIf { it.isNotEmpty() }
            ?.let {
                it.reduce { acc, item -> acc + item }
            }
            ?: BigDecimal.ZERO

        return if (totalScore > BigDecimal.TEN) BigDecimal.TEN else totalScore
    }

    override fun getCode(): String {
        return "EHA"
    }
}
