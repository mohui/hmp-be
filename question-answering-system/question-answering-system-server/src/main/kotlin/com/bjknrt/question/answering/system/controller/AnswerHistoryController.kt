package com.bjknrt.question.answering.system.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.api.AnswerHistoryApi
import com.bjknrt.question.answering.system.service.AnswerHistoryService
import com.bjknrt.question.answering.system.vo.*
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.question.answering.system.api.AnswerHistoryController")
class AnswerHistoryController(
    val answerHistoryService: AnswerHistoryService
) : AppBaseController(), AnswerHistoryApi {
    override fun getLastAnswerRecordList(lastAnswerRecordListRequest: LastAnswerRecordListRequest): List<LastAnswerRecord> {
        return answerHistoryService.getLastAnswerRecordList(lastAnswerRecordListRequest)
    }

    override fun getAnswerRecordList(answerRecordListRequest: AnswerRecordListRequest): List<AnswerRecord> {
        return answerHistoryService.getAnswerRecordList(answerRecordListRequest)
    }

    override fun getLastAnswerDetailList(lastAnswerDetailRequest: LastAnswerDetailRequest): List<AnswerResultToQuestionsOption> {
        return answerHistoryService.getLastAnswerDetailList(lastAnswerDetailRequest)
    }

    override fun getPageAnswerRecord(pageAnswerRecordRequest: PageAnswerRecordRequest): PagedResult<AnswerRecord> {
        return answerHistoryService.getPageAnswerRecord(pageAnswerRecordRequest)
    }

    override fun saveAnswerRecord(saveAnswerRecordRequest: SaveAnswerRecordRequest) {
        answerHistoryService.saveAnswerRecord(saveAnswerRecordRequest)
    }

}
