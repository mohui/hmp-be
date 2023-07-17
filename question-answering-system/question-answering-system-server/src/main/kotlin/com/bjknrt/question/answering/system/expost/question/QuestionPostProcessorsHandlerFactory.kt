package com.bjknrt.question.answering.system.expost.question

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.vo.Questions
import org.springframework.stereotype.Component

@Component
class QuestionPostProcessorsHandlerFactory(
    processorsHandlerList: List<QuestionPostProcessorsHandler>
) {
    private val handlerList = processorsHandlerList.sortedByDescending { it.getOrder() }

    private fun getProcessorsHandlerStrategy(examinationPaper: QasExaminationPaper): List<QuestionPostProcessorsHandler> {
        return handlerList.filter { it.support(examinationPaper) }
    }

    fun execute(
        examinationPaper: QasExaminationPaper,
        questionsOption: List<Questions>
    ): List<Questions> {
        var questions = questionsOption
        this.getProcessorsHandlerStrategy(examinationPaper).forEach { questions = it.execute(questions) }
        return questions
    }
}
