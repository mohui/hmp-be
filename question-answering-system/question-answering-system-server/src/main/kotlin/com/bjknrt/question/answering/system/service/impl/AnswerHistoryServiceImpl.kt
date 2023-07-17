package com.bjknrt.question.answering.system.service.impl

import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasExaminationPaperTable
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecordTable
import com.bjknrt.question.answering.system.QasQuestionsAnswerResultTable
import com.bjknrt.question.answering.system.assembler.answerResultToQuestionsOption
import com.bjknrt.question.answering.system.assembler.buildQasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.assembler.qasQuestionsAnswerRecordToAnswerRecord
import com.bjknrt.question.answering.system.service.AnswerHistoryService
import com.bjknrt.question.answering.system.vo.*
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Suppress("UNREACHABLE_CODE")
@Service
class AnswerHistoryServiceImpl(
    val qasQuestionsAnswerResultTable: QasQuestionsAnswerResultTable,
    val qasQuestionsAnswerRecordTable: QasQuestionsAnswerRecordTable,
    val qasExaminationPaperTable: QasExaminationPaperTable
) : AnswerHistoryService {

    @Transactional
    override fun saveAnswerRecord(saveAnswerRecordRequest: SaveAnswerRecordRequest) {
        val record = buildQasQuestionsAnswerRecord(saveAnswerRecordRequest)
        qasQuestionsAnswerRecordTable.save(record)
    }

    override fun getPageAnswerRecord(pageAnswerRecordRequest: PageAnswerRecordRequest): PagedResult<AnswerRecord> {
        val condition = qasQuestionsAnswerRecordTable.select()
        pageAnswerRecordRequest.examinationPaperCodeList
            .takeIf { it.isNotEmpty() }
            ?.let { codeList ->
                condition.where(QasQuestionsAnswerRecordTable.ExaminationPaperCode `in` codeList.map { it.arg })
            }

        pageAnswerRecordRequest.answerBy?.let {
            condition.where(QasQuestionsAnswerRecordTable.AnswerBy eq it.arg)
        }

        pageAnswerRecordRequest.createdBy?.let {
            condition.where(QasQuestionsAnswerRecordTable.CreatedBy eq it.arg)
        }

        val pageInfo = condition
            .order(QasQuestionsAnswerRecordTable.CreatedAt, Order.Desc)
            .page(pageAnswerRecordRequest.pageSize, pageAnswerRecordRequest.pageNo)

        var examinationPaperMap = mapOf<BigInteger, QasExaminationPaper>()
        pageInfo.takeIf { it.data.size > 0 }?.let {
            examinationPaperMap = qasExaminationPaperTable.select()
                .where(QasExaminationPaperTable.IsDel eq false)
                .where(QasExaminationPaperTable.Id `in` pageInfo.data.map { it.examinationPaperId.arg })
                .find()
                .associateBy { it.id }
        }

        return PagedResult.Companion.fromDbPaged(pageInfo) {
            qasQuestionsAnswerRecordToAnswerRecord(
                it,
                examinationPaperMap[it.examinationPaperId]
                    ?: throw MsgException(AppSpringUtil.getMessage("examination.paper.not-found"))
            )

        }
    }

    override fun getAnswerRecordList(answerRecordListRequest: AnswerRecordListRequest): List<AnswerRecord> {
        val condition = qasQuestionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.AnswerBy eq answerRecordListRequest.answerBy.arg)
            .where(QasQuestionsAnswerRecordTable.CreatedAt gte answerRecordListRequest.startDate.arg)
            .where(QasQuestionsAnswerRecordTable.CreatedAt lt answerRecordListRequest.endDate.arg)

        answerRecordListRequest.examinationPaperCodeList
            .takeIf { it.isNotEmpty() }
            ?.let { codeList ->
                condition.where(QasQuestionsAnswerRecordTable.ExaminationPaperCode `in` codeList.map { it.arg })
            }

        val list = condition.find()

        var examinationPaperMap = mapOf<BigInteger, QasExaminationPaper>()
        list.takeIf { it.isNotEmpty() }?.let {
            examinationPaperMap = qasExaminationPaperTable.select()
                .where(QasExaminationPaperTable.IsDel eq false)
                .where(QasExaminationPaperTable.Id `in` list.map { it.examinationPaperId.arg })
                .find()
                .associateBy { it.id }
        }
        return list.map {
            qasQuestionsAnswerRecordToAnswerRecord(
                it,
                examinationPaperMap[it.examinationPaperId]
                    ?: throw MsgException(AppSpringUtil.getMessage("examination.paper.not-found"))
            )
        }
    }

    override fun getLastAnswerRecordList(lastAnswerRecordListRequest: LastAnswerRecordListRequest): List<LastAnswerRecord> {
        val condition = qasQuestionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperCode eq lastAnswerRecordListRequest.examinationPaperCode.arg)
            .where(QasQuestionsAnswerRecordTable.AnswerBy eq lastAnswerRecordListRequest.answerBy.arg)
            .order(QasQuestionsAnswerRecordTable.CreatedAt, Order.Desc)
            .take(lastAnswerRecordListRequest.needNum.toLong())

        lastAnswerRecordListRequest.startDate?.let {
            condition.where(QasQuestionsAnswerRecordTable.CreatedAt gte it.arg)
        }
        lastAnswerRecordListRequest.endDate?.let {
            condition.where(QasQuestionsAnswerRecordTable.CreatedAt lt it.arg)
        }
        return condition.find().map {
            LastAnswerRecord(
                id = it.id,
                answerBy = it.answerBy,
                resultsTag = it.resultsTag,
                resultsMsg = it.resultsMsg,
                totalScore = it.totalScore,
                createdBy = it.createdBy,
                createdAt = it.createdAt
            )
        }
    }

    override fun getLastAnswerDetailList(lastAnswerDetailRequest: LastAnswerDetailRequest): List<AnswerResultToQuestionsOption> {
        val condition = qasQuestionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.AnswerBy eq lastAnswerDetailRequest.answerBy.arg)
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperCode eq lastAnswerDetailRequest.examinationPaperCode.arg)
            .order(QasQuestionsAnswerRecordTable.CreatedAt, Order.Desc)

        lastAnswerDetailRequest.startDate?.let {
            condition.where(QasQuestionsAnswerRecordTable.CreatedAt gte it.arg)
        }
        lastAnswerDetailRequest.endDate?.let {
            condition.where(QasQuestionsAnswerRecordTable.CreatedAt lt it.arg)
        }

        return condition.findOne()?.let { record ->
            qasQuestionsAnswerResultTable.select()
                .where(QasQuestionsAnswerResultTable.QuestionsAnswerRecordId eq record.id)
                .find()
                .map {
                    answerResultToQuestionsOption(it)
                }
        } ?: emptyList()
    }
}
