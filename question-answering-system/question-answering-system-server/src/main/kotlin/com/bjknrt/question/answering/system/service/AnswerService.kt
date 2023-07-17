package com.bjknrt.question.answering.system.service

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.vo.*
import java.math.BigInteger

interface AnswerService {
    fun saveEvaluateResult(evaluateResultRequest: EvaluateResultRequest): EvaluateResultResponse
    fun getLastAnswerResult(lastEvaluateResultRequest: LastEvaluateResultRequest): EvaluateResultResponse
    fun getPageAnswerResult(pageEvaluateResultRequest: PageEvaluateResultRequest): PagedResult<EvaluateResultResponse>
    fun getAnswerDetail(answerRecordId: BigInteger): AnswerDetailResponse
}