package com.bjknrt.question.answering.system.service.impl

import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.*
import com.bjknrt.question.answering.system.assembler.qasExaminationPaperToExaminationPaper
import com.bjknrt.question.answering.system.assembler.qasOptionToOption
import com.bjknrt.question.answering.system.assembler.qasQuestionsToQuestions
import com.bjknrt.question.answering.system.assembler.questionsImageToImageInfo
import com.bjknrt.question.answering.system.expost.question.QuestionPostProcessorsHandlerFactory
import com.bjknrt.question.answering.system.service.ExaminationPaperService
import com.bjknrt.question.answering.system.vo.*
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.*

@Service
class ExaminationPaperServiceImpl(
    val qasQuestionsImageTable: QasQuestionsImageTable,
    val qasExaminationPaperTable: QasExaminationPaperTable,
    val qasQuestionsTable: QasQuestionsTable,
    val qasOptionTable: QasOptionTable,
    val questionPostProcessorsHandlerFactory: QuestionPostProcessorsHandlerFactory
) : ExaminationPaperService {

    override fun getPageExaminationPaper(pageExaminationPaperRequest: PageExaminationPaperRequest): PagedResult<ExaminationPaper> {
        val codeSet = pageExaminationPaperRequest.codeSet

        var condition = qasExaminationPaperTable.select()
            .where(QasExaminationPaperTable.IsDel eq false)
        if (codeSet.isEmpty().not()) {
            condition =
                condition.where(QasExaminationPaperTable.ExaminationPaperCode `in` (codeSet.map { it.arg }))
        }
        val pageInfo = condition.page(pageExaminationPaperRequest.pageSize, pageExaminationPaperRequest.pageNo)

        return PagedResult.Companion.fromDbPaged(pageInfo) { qasExaminationPaperToExaminationPaper(it) }
    }

    override fun getQuestionsOption(examinationPaperCode: String): List<Questions> {
        //查询问卷
        val examinationPaper = qasExaminationPaperTable.findByExaminationPaperCode(examinationPaperCode)
            ?: throw MsgException(AppSpringUtil.getMessage("examination.paper.not-found"))
        //查询问卷题目
        val questions = qasQuestionsTable.select()
            .where(QasQuestionsTable.ExaminationPaperId eq examinationPaper.id)
            .where(QasQuestionsTable.IsDel eq false)
            .order(QasQuestionsTable.QuestionsSort, Order.Asc)
            .order(QasQuestionsTable.QuestionsGroupTitle, Order.Asc)
            .find()
            ?: Collections.emptyList()

        var optionsMap = mapOf<BigInteger, List<QasOption>>()
        var questionsImageMap = mapOf<BigInteger, List<QuestionImageInfo>>()
        if (questions.isNotEmpty()) {
            //查询问卷题目的选项
            optionsMap = qasOptionTable.select()
                .where(QasOptionTable.QuestionsId.`in`(questions.map { it.id.arg }.toSet()))
                .where(QasOptionTable.IsDel eq false)
                .order(QasOptionTable.OptionSort, Order.Asc)
                .find()
                .groupBy { it.questionsId }

            questionsImageMap = qasQuestionsImageTable.select()
                .where(QasQuestionsImageTable.QuestionsId `in` questions.map { it.id.arg }.toSet())
                .order(QasQuestionsImageTable.Sort, Order.Asc)
                .find()
                .map { questionsImageToImageInfo(it) }
                .groupBy { it.questionsId }
        }

        val questionsList = questions.map {
            buildQuestions(
                it,
                optionsMap.getOrDefault(it.id, Collections.emptyList()),
                questionsImageMap.getOrDefault(it.id, Collections.emptyList())
            )
        }
        //过滤问题列表
      return  questionPostProcessorsHandlerFactory.execute(
            examinationPaper,
            questionsList
        )
    }

    override fun getQuestionImageInfo(imageRequest: ImageRequest): List<QuestionImageInfo> {
        return qasQuestionsImageTable.select()
            .where(QasQuestionsImageTable.QuestionsId eq imageRequest.questionsId)
            .find()
            .map { questionsImageToImageInfo(it) }
    }

    private fun buildQuestions(
        qasQuestions: QasQuestions,
        qasOption: List<QasOption>,
        images: List<QuestionImageInfo>
    ): Questions {
        val options = qasOption.map { qasOptionToOption(it) }
        return qasQuestionsToQuestions(qasQuestions, options, images)
    }


}
