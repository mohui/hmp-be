package com.bjknrt.question.answering.system.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.api.ExaminationPaperApi
import com.bjknrt.question.answering.system.service.ExaminationPaperService
import com.bjknrt.question.answering.system.vo.*
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.question.answering.system.api.ExaminationPaperController")
class ExaminationPaperController(
    val examinationPaperService: ExaminationPaperService
) : AppBaseController(), ExaminationPaperApi {

    override fun getPageExaminationPaper(pageExaminationPaperRequest: PageExaminationPaperRequest): PagedResult<ExaminationPaper> {
        return examinationPaperService.getPageExaminationPaper(pageExaminationPaperRequest)
    }

    override fun getQuestionImageInfo(imageRequest: ImageRequest): List<QuestionImageInfo> {
       return examinationPaperService.getQuestionImageInfo(imageRequest)
    }

    override fun getQuestionsOption(body: String): List<Questions> {
        return examinationPaperService.getQuestionsOption(body)
    }

}
