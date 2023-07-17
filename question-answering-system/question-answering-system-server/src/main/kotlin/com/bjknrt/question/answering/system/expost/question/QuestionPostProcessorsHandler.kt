package com.bjknrt.question.answering.system.expost.question

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.vo.Questions

/**
 * 查询问题和选项后的后置处理程序
 */
interface QuestionPostProcessorsHandler {
    /**
     * 执行后置处理器逻辑
     * @param  questionsOption 题目选项
     */
    fun execute(questionsOption: List<Questions>): List<Questions>

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
