package com.bjknrt.question.answering.system.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-question-answering-system.kato-server-name:\${spring.application.name}}", contextId = "AnswerHistoryApi")
@Validated
interface AnswerHistoryApi {


    /**
     * 查询答题记录
     *
     *
     * @param answerRecordListRequest
     * @return List<AnswerRecord>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answerHistory/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getAnswerRecordList(@Valid answerRecordListRequest: AnswerRecordListRequest): List<AnswerRecord>


    /**
     * 查询指定时间段内的答题的选项
     *
     *
     * @param lastAnswerDetailRequest
     * @return List<AnswerResultToQuestionsOption>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answerHistory/detail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastAnswerDetailList(@Valid lastAnswerDetailRequest: LastAnswerDetailRequest): List<AnswerResultToQuestionsOption>


    /**
     * 查询指定时间段内的答题记录
     *
     *
     * @param lastAnswerRecordListRequest
     * @return List<LastAnswerRecord>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answerHistory/record"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastAnswerRecordList(@Valid lastAnswerRecordListRequest: LastAnswerRecordListRequest): List<LastAnswerRecord>


    /**
     * 分页查询答题记录列表
     *
     *
     * @param pageAnswerRecordRequest
     * @return PagedResult<AnswerRecord>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answerHistory/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getPageAnswerRecord(@Valid pageAnswerRecordRequest: PageAnswerRecordRequest): PagedResult<AnswerRecord>


    /**
     * 新增答题记录
     *
     *
     * @param saveAnswerRecordRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/answerHistory/save"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveAnswerRecord(@Valid saveAnswerRecordRequest: SaveAnswerRecordRequest): Unit
}
