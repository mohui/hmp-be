package com.bjknrt.question.answering.system.assembler

import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.vo.AnswerResultToQuestionsOption

fun answerResultToQuestionsOption(answerResult: QasQuestionsAnswerResult): AnswerResultToQuestionsOption {
    return AnswerResultToQuestionsOption(
        questionsId = answerResult.questionsId,
        optionId = answerResult.optionId,
        optionValue = answerResult.optionValue,
        questionsAnsweredCount = answerResult.questionsAnsweredCount,
        optionCode = answerResult.optionCode,
        optionScore = answerResult.optionScore,
    )
}
