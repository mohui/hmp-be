package com.bjknrt.question.answering.system.api

import com.bjknrt.question.answering.system.vo.ImageRequest
import com.bjknrt.question.answering.system.vo.PageExaminationPaperRequest
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.vo.QuestionImageInfo
import com.bjknrt.question.answering.system.vo.Questions
import com.bjknrt.question.answering.system.vo.ExaminationPaper
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import kotlin.collections.List
import kotlin.collections.Map

@KatoClient(name = "\${app.hmp-question-answering-system.kato-server-name:\${spring.application.name}}", contextId = "ExaminationPaperApi")
@Validated
interface ExaminationPaperApi {


    /**
     * 分页查询问卷列表
     * 
     *
     * @param pageExaminationPaperRequest
     * @return PagedResult<ExaminationPaper>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/examinationPaper/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getPageExaminationPaper(@Valid pageExaminationPaperRequest: PageExaminationPaperRequest): PagedResult<ExaminationPaper>


    /**
     * 查询题干图片
     * 
     *
     * @param imageRequest
     * @return List<QuestionImageInfo>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/examinationPaper/questionImageInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getQuestionImageInfo(@Valid imageRequest: ImageRequest): List<QuestionImageInfo>


    /**
     * 查询问卷问题和选项
     * 
     *
     * @param body
     * @return List<Questions>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/examinationPaper/option"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getQuestionsOption(@Valid body: kotlin.String): List<Questions>
}
