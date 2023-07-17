package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DefaultEvaluateScoreStrategy : EvaluateScoreStrategy {

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        return options.map { it.optionScore }
            .fold(BigDecimal.ZERO) { acc, item -> item?.let { acc + it } ?: BigDecimal.ZERO }
    }

    override fun getCode(): String {
        return "DEFAULT"
    }
}
