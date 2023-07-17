package com.bjknrt.question.answering.system.expost.evaluate

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult

/**
 * 评估后置处理程序
 */
interface EvaluatePostProcessorsHandler {
    /**
     * 执行后置处理器逻辑
     * @param  answerRecord 答题记录
     * @param  answerResultList 答题结果集合
     */
    fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>)

    /**
     * 判断是否支持后置处理器逻辑
     * @param examinationPaper 问卷信息
     * @return 支持true,不支持false
     */
    fun support(examinationPaper: QasExaminationPaper): Boolean

    /**
     * 后置处理器排序字段，会以降序排列，数值越大，匹配时越靠前，匹配成功后取第一个作为最终执行的处理器
     */
    fun getOrder(): Int
}
