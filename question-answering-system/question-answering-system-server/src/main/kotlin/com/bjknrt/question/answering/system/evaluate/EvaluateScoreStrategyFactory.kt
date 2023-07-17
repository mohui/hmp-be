package com.bjknrt.question.answering.system.evaluate

import org.springframework.stereotype.Component

@Component
class EvaluateScoreStrategyFactory(
    evaluateScoreStrategyList: List<EvaluateScoreStrategy>
) {

    private val evaluateScoreStrategyMap: Map<String, EvaluateScoreStrategy> =
        evaluateScoreStrategyList.associateBy { it.getCode() }

    fun getEvaluateScoreStrategy(code: String): EvaluateScoreStrategy {
        return evaluateScoreStrategyMap.getOrDefault(code, evaluateScoreStrategyMap.getValue("NONE"))
    }
}
