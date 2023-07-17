package com.bjknrt.question.answering.system.api

import com.bjknrt.question.answering.system.vo.AnswerDetailResponse
import com.bjknrt.question.answering.system.vo.EvaluateResultRequest
import com.bjknrt.question.answering.system.vo.EvaluateResultResponse
import com.bjknrt.question.answering.system.vo.LastEvaluateResultRequest
import com.bjknrt.question.answering.system.vo.PageEvaluateResultRequest
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

@KatoClient(name = "\${app.hmp-question-answering-system.kato-server-name:\${spring.application.name}}", contextId = "AnswerApi")
@Validated
interface AnswerApi {


    /**
     * 保存个人评估结果
     * 
     *
     * @param evaluateResultRequest
     * @return EvaluateResultResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answer/evaluateResult"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun evaluateResult(@Valid evaluateResultRequest: EvaluateResultRequest): EvaluateResultResponse


    /**
     * 查询个人评估详情
     * 
     *
     * @param body
     * @return AnswerDetailResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answer/detail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getAnswerDetail(@Valid body: java.math.BigInteger): AnswerDetailResponse


    /**
     * 最新的评估结果
     * 
     *
     * @param lastEvaluateResultRequest
     * @return EvaluateResultResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answer/last"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastAnswerResult(@Valid lastEvaluateResultRequest: LastEvaluateResultRequest): EvaluateResultResponse


    /**
     * 分页查询评估结果
     * 
     *
     * @param pageEvaluateResultRequest
     * @return PagedResult<EvaluateResultResponse>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answer/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getPageAnswerResult(@Valid pageEvaluateResultRequest: PageEvaluateResultRequest): PagedResult<EvaluateResultResponse>
}
