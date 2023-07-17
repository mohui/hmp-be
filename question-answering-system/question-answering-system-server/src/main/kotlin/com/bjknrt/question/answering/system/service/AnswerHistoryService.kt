package com.bjknrt.question.answering.system.service

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.vo.*

interface AnswerHistoryService {

    fun saveAnswerRecord(saveAnswerRecordRequest: SaveAnswerRecordRequest)
    fun getPageAnswerRecord(pageAnswerRecordRequest: PageAnswerRecordRequest): PagedResult<AnswerRecord>
    fun getAnswerRecordList(answerRecordListRequest: AnswerRecordListRequest): List<AnswerRecord>
    fun getLastAnswerRecordList(lastAnswerRecordListRequest: LastAnswerRecordListRequest): List<LastAnswerRecord>
    fun getLastAnswerDetailList(lastAnswerDetailRequest: LastAnswerDetailRequest): List<AnswerResultToQuestionsOption>

}
