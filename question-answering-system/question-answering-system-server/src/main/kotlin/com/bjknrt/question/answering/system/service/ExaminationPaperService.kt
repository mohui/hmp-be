package com.bjknrt.question.answering.system.service

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.question.answering.system.vo.*

interface ExaminationPaperService {
    fun getPageExaminationPaper(pageExaminationPaperRequest: PageExaminationPaperRequest): PagedResult<ExaminationPaper>
    fun getQuestionsOption(examinationPaperCode: String): List<Questions>
    fun getQuestionImageInfo(imageRequest: ImageRequest): List<QuestionImageInfo>
}
