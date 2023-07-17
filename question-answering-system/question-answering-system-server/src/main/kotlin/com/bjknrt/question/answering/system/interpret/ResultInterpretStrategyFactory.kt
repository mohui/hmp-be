package com.bjknrt.question.answering.system.interpret

import com.bjknrt.question.answering.system.QasExaminationPaper
import org.springframework.stereotype.Component

@Component
class ResultInterpretStrategyFactory(
    resultInterpretList: List<ResultInterpretStrategy>
) {
    private val resultInterpretList = resultInterpretList.sortedByDescending { it.getOrder() }

    fun getResultInterpretStrategy(examinationPaper: QasExaminationPaper): ResultInterpretStrategy {
        return resultInterpretList.first{ it.support(examinationPaper) }
    }
}
