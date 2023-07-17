package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class EssenEvaluateScoreStrategy : EvaluateScoreStrategy {


    val ageOptionCode = "ESSEN_280001_28000101"


    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {

        val ageScore = options.firstOrNull { ageOptionCode == it.optionCode }?.let {
            val age = it.optionValue.toInt()
            if (age in 65..74) {
                return@let 1
            } else if (age >= 75) {
                return@let 2
            } else {
                return@let 0
            }
        } ?: 0


        val totalScore = options.filter { ageOptionCode != it.optionCode }
            .mapNotNull {
                it.optionScore
            }.takeIf { it.isNotEmpty() }
            ?.let {
                it.reduce { acc, item -> acc + item }
            } ?: BigDecimal.ZERO



        return totalScore + ageScore.toBigDecimal()
    }

    override fun getCode(): String {
        return "ESSEN"
    }
}
