package com.bjknrt.question.answering.system.service.impl

import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.*
import com.bjknrt.question.answering.system.assembler.answerResultToQuestionsOption
import com.bjknrt.question.answering.system.assembler.qasExaminationPaperToExaminationPaper
import com.bjknrt.question.answering.system.assembler.questionsOptionsToEvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategyFactory
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandlerFactory
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategyFactory
import com.bjknrt.question.answering.system.service.AnswerService
import com.bjknrt.question.answering.system.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

@Service
class AnswerServiceImpl(
    val qasExaminationPaperTable: QasExaminationPaperTable,
    val questionsAnswerRecordTable: QasQuestionsAnswerRecordTable,
    val questionsAnswerResultTable: QasQuestionsAnswerResultTable,
    val evaluateScoreStrategyFactory: EvaluateScoreStrategyFactory,
    val resultInterpretStrategyFactory: ResultInterpretStrategyFactory,
    val postProcessorsHandlerFactory: EvaluatePostProcessorsHandlerFactory
) : AnswerService {

    @Transactional
    override fun saveEvaluateResult(evaluateResultRequest: EvaluateResultRequest): EvaluateResultResponse {
        //问卷Id
        val examinationPaperId = evaluateResultRequest.examinationPaperId
        //问卷问题选项
        val questionsOption = evaluateResultRequest.questionsOption
        //查询问卷信息
        val examinationPaper = qasExaminationPaperTable.findById(examinationPaperId)
            ?: throw MsgException(AppSpringUtil.getMessage("examination.paper.not-found"))
        //问卷编码
        val paperCode = examinationPaper.examinationPaperCode

        // 策略编码
        val strategyCode = examinationPaper.strategyCode

        //1.计算总分
        val totalScore = evaluateScoreHandler(strategyCode, questionsOption)

        //2.查询问卷的结果解答
        val interpret = resultInterpretStrategyFactory
            .getResultInterpretStrategy(examinationPaper)
            .getInterpret(InterpretMatter(examinationPaperId, totalScore, questionsOption))

        //3.保存问卷的答题记录
        val answerRecord =
            buildAnswerRecord(
                examinationPaperId,
                paperCode,
                totalScore,
                interpret.resultsTag,
                interpret.resultsMsg,
                evaluateResultRequest.answerBy
            )
        questionsAnswerRecordTable.save(answerRecord)

        //4.保存问卷的答题结果
        val answerMap = mutableMapOf<String, Int>();
        val answerResultList = questionsOption.map { it ->
            val mapKey = "${it.questionsId}${it.optionId}"
            // 计算问卷重复答题次数
            val questionsAnsweredCount = answerMap[mapKey]?.let {
                it + 1
            }?: 1
            answerMap.put(mapKey, questionsAnsweredCount)

            buildAnswerResult(answerRecord.id, it, questionsAnsweredCount)
        }
        answerResultList.forEach { questionsAnswerResultTable.save(it) }

        //5.后置事件
        postProcessorsHandlerFactory.execute(examinationPaper, answerRecord, answerResultList)

        //返回结果
        return EvaluateResultResponse(
            answerRecord.id, interpret.resultsTag, answerRecord.createdAt, totalScore, interpret.resultsMsg
        )
    }

    override fun getLastAnswerResult(lastEvaluateResultRequest: LastEvaluateResultRequest): EvaluateResultResponse {
        //查询最新的问答记录
        val record = questionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.AnswerBy eq lastEvaluateResultRequest.patientId)
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperId eq lastEvaluateResultRequest.examinationPaperId)
            .order(QasQuestionsAnswerRecordTable.CreatedAt, Order.Desc).findOne()
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("answer.record.not-found"))
        //返回结果
        return buildEvaluateResultResponse(record)
    }

    override fun getPageAnswerResult(pageEvaluateResultRequest: PageEvaluateResultRequest): PagedResult<EvaluateResultResponse> {
        //查询最新的问答记录
        val pageInfo = questionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.AnswerBy eq pageEvaluateResultRequest.patientId)
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperId eq pageEvaluateResultRequest.examinationPaperId)
            .order(QasQuestionsAnswerRecordTable.CreatedAt, Order.Desc)
            .page(pageEvaluateResultRequest.pageSize, pageEvaluateResultRequest.pageNo)

        //返回所有的分页结果
        return PagedResult.fromDbPaged(pageInfo) { buildEvaluateResultResponse(it) }
    }

    override fun getAnswerDetail(answerRecordId: BigInteger): AnswerDetailResponse {
        //查询问卷评估记录
        val answerRecord = questionsAnswerRecordTable.findById(answerRecordId)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("answer.record.not-found"))

        //查询本地问卷保存的选项
        val optionList = questionsAnswerResultTable.select()
            .where(QasQuestionsAnswerResultTable.QuestionsAnswerRecordId eq answerRecordId).find()
            ?.map { answerResultToQuestionsOption(it) } ?: Collections.emptyList()

        //构建个人评估结果
        val evaluateResult = EvaluateResultResponse(
            answerRecord.id,
            answerRecord.resultsTag,
            answerRecord.createdAt,
            answerRecord.totalScore,
            answerRecord.resultsMsg
        )
        //查询问卷信息
        val examinationPaper = qasExaminationPaperTable.findById(answerRecord.examinationPaperId)
            ?.let { qasExaminationPaperToExaminationPaper(it) }
            ?: throw MsgException(AppSpringUtil.getMessage("examination.paper.not-found"))
        //返回结果
        return AnswerDetailResponse(
            evaluateResult,
            optionList,
            examinationPaper,
        )
    }

    private fun evaluateScoreHandler(code: String, questionsOption: List<EvaluateResultQuestionsOption>): BigDecimal? {
        val optionList = questionsOption.map { questionsOptionsToEvaluateScoreOption(it) }
        return evaluateScoreStrategyFactory.getEvaluateScoreStrategy(code).evaluate(optionList)
    }

    private fun buildEvaluateResultResponse(
        record: QasQuestionsAnswerRecord
    ): EvaluateResultResponse {
        return EvaluateResultResponse(
            record.id, record.resultsTag, record.createdAt, record.totalScore, record.resultsMsg,
        )
    }


    private fun buildAnswerRecord(
        examinationPaperId: BigInteger,
        examinationPaperCode: String,
        totalScore: BigDecimal?,
        resultsTag: String,
        resultsMsg: String?,
        answerBy: BigInteger
    ): QasQuestionsAnswerRecord {
        return QasQuestionsAnswerRecord.builder()
            .setId(AppIdUtil.nextId())
            .setAnswerBy(answerBy)
            .setExaminationPaperId(examinationPaperId)
            .setExaminationPaperCode(examinationPaperCode)
            .setResultsTag(resultsTag)
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setCreatedAt(LocalDateTime.now())
            .setResultsMsg(resultsMsg)
            .setTotalScore(totalScore)
            .build()
    }


    private fun buildAnswerResult(
        questionsAnswerRecordId: BigInteger,
        option: EvaluateResultQuestionsOption,
        questionsAnsweredCount: Int
    ): QasQuestionsAnswerResult {
        return QasQuestionsAnswerResult.builder()
            .setId(AppIdUtil.nextId())
            .setQuestionsAnswerRecordId(questionsAnswerRecordId)
            .setQuestionsId(option.questionsId)
            .setOptionId(option.optionId)
            .setOptionValue(option.optionValue)
            .setOptionCode(option.optionCode)
            .setOptionScore(option.optionScore)
            .setQuestionsAnsweredCount(questionsAnsweredCount)
            .build()
    }

}
