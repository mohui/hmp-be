package com.bjknrt.question.answering.system.expost.evaluate

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import org.springframework.stereotype.Component

@Component
class EvaluatePostProcessorsHandlerFactory(
    processorsHandlerList: List<EvaluatePostProcessorsHandler>
) {
    private val handlerList = processorsHandlerList.sortedByDescending { it.getOrder() }

    private fun getProcessorsHandlerStrategy(examinationPaper: QasExaminationPaper): EvaluatePostProcessorsHandler? {
        return handlerList.firstOrNull { it.support(examinationPaper) }
    }

    fun execute(
        examinationPaper: QasExaminationPaper,
        answerRecord: QasQuestionsAnswerRecord,
        answerResultList: List<QasQuestionsAnswerResult>
    ) {
        this.getProcessorsHandlerStrategy(examinationPaper)?.execute(answerRecord, answerResultList)
    }
}
