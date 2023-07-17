package com.bjknrt.question.answering.system.assembler

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.vo.AnswerRecord
import com.bjknrt.question.answering.system.vo.SaveAnswerRecordRequest
import java.time.LocalDateTime


fun buildQasQuestionsAnswerRecord(request: SaveAnswerRecordRequest): QasQuestionsAnswerRecord {
    return QasQuestionsAnswerRecord.builder()
        .setId(AppIdUtil.nextId())
        .setAnswerBy(request.answerBy)
        .setExaminationPaperId(request.examinationPaperId)
        .setExaminationPaperCode(request.examinationPaperCode)
        .setResultsTag(request.resultsTag)
        .setCreatedBy(request.createdBy)
        .setTotalScore(request.totalScore)
        .setResultsMsg(request.resultsMsg)
        .setCreatedAt(LocalDateTime.now())
        .build()
}

fun qasQuestionsAnswerRecordToAnswerRecord(
    record: QasQuestionsAnswerRecord,
    examinationPaper: QasExaminationPaper
): AnswerRecord {
    return AnswerRecord(
        id = record.id,
        answerBy = record.answerBy,
        examinationPaperId = record.examinationPaperId,
        examinationPaperCode = record.examinationPaperCode,
        examinationPaperTitle = examinationPaper.examinationPaperTitle,
        examinationPaperDesc = examinationPaper.examinationPaperDesc,
        examinationPaperTip = examinationPaper.examinationPaperTip,
        examinationPaperReference = examinationPaper.examinationPaperReference,
        totalScore = record.totalScore,
        createdBy = record.createdBy,
        createdAt = record.createdAt,
        resultsTag = record.resultsTag,
        resultsMsg = record.resultsMsg
    )
}
