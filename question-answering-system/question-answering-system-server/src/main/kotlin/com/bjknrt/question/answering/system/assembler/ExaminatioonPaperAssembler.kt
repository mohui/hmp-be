package com.bjknrt.question.answering.system.assembler

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasOption
import com.bjknrt.question.answering.system.QasQuestions
import com.bjknrt.question.answering.system.QasQuestionsImage
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.vo.*

fun qasExaminationPaperToExaminationPaper(paper: QasExaminationPaper): ExaminationPaper {
    return ExaminationPaper(
        paper.id,
        paper.examinationPaperTitle,
        paper.examinationPaperDesc,
        paper.examinationPaperReference,
        paper.evaluationTime,
        paper.examinationPaperCode,
        paper.examinationPaperTip
    )
}

fun qasQuestionsToQuestions(questions: QasQuestions, options: List<QuestionsOptions>, images: List<QuestionImageInfo>): Questions {
    return Questions(
        id = questions.id,
        questionsTitle = questions.questionsTitle,
        questionsType = QuestionsType.valueOf(questions.questionsType),
        questionsSort = questions.questionsSort,
        images = images,
        options = options,
        questionsGroupTitle = questions.questionsGroupTitle,
        questionsTip = questions.questionsTip,
        matchRegExp = questions.matchRegExp,
        matchFailMsg = questions.matchFailMsg,
        isRepeatAnswer = questions.isRepeatAnswer
    )
}

fun qasOptionToOption(qasOption: QasOption): QuestionsOptions {
    return QuestionsOptions(
        qasOption.questionsId,
        qasOption.id,
        qasOption.optionValue,
        qasOption.optionLabel,
        qasOption.optionTip,
        qasOption.optionSort,
        OptionType.valueOf(qasOption.optionType),
        qasOption.isDynamic,
        qasOption.isAutoCommit,
        qasOption.optionCode,
        qasOption.optionScore,
        qasOption.optionRule,
        qasOption.forwardTo
    )
}

fun questionsOptionsToEvaluateScoreOption(option: EvaluateResultQuestionsOption): EvaluateScoreOption {
    return EvaluateScoreOption(
        option.questionsId,
        option.optionCode,
        option.optionValue,
        option.optionScore,
    )
}

fun questionsImageToImageInfo(questionImage: QasQuestionsImage): QuestionImageInfo {
    return QuestionImageInfo(
        id = questionImage.id,
        questionsId = questionImage.questionsId,
        imageUrl = questionImage.imageUrl
    )
}
