package com.bjknrt.question.answering.system.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.api.AnswerApi
import com.bjknrt.question.answering.system.service.AnswerService
import com.bjknrt.question.answering.system.vo.*
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.question.answering.system.api.AnswerController")
class AnswerController(
    val answerService: AnswerService,
) : AppBaseController(), AnswerApi {

    override fun evaluateResult(evaluateResultRequest: EvaluateResultRequest): EvaluateResultResponse {
        return answerService.saveEvaluateResult(evaluateResultRequest)
    }

    override fun getAnswerDetail(body: BigInteger): AnswerDetailResponse {
       return answerService.getAnswerDetail(body)
    }

    override fun getLastAnswerResult(lastEvaluateResultRequest: LastEvaluateResultRequest): EvaluateResultResponse {
        return answerService.getLastAnswerResult(lastEvaluateResultRequest)
    }

    override fun getPageAnswerResult(pageEvaluateResultRequest: PageEvaluateResultRequest): PagedResult<EvaluateResultResponse> {
        return answerService.getPageAnswerResult(pageEvaluateResultRequest)
    }


}