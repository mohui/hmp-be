package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class NoneEvaluateScoreStrategy : EvaluateScoreStrategy {

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        return null
    }

    override fun getCode(): String {
        return "NONE"
    }
}
