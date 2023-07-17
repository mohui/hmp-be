package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class Eq5dEvaluateScoreStrategy : EvaluateScoreStrategy {

    companion object {
        val SCORE = BigDecimal(0.152)
    }

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        val total = options.map { it.optionScore }
            .fold(BigDecimal.ZERO) { acc, item -> item?.let { acc + it } ?: BigDecimal.ZERO }

        return if (total == BigDecimal.ZERO) BigDecimal.ONE else BigDecimal.ONE - SCORE - total
    }

    override fun getCode(): String {
        return "EQ5D"
    }
}
