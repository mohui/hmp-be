package com.bjknrt.question.answering.system.interpret

import com.bjknrt.question.answering.system.QasExaminationPaper

interface ResultInterpretStrategy {

    /**
     * 获取结果解读策略
     * @param  interpretMatter 解决解读的条件参数
     * @return 对应的结果解读
     */
    fun getInterpret(interpretMatter: InterpretMatter): InterpretResult

    /**
     * 判断是否支持结果解读
     */
    fun support(examinationPaper: QasExaminationPaper): Boolean

    /**
     * 结果解读策略排序字段，会以降序排列，数值越大，匹配时越靠前，匹配成功后取第一个作为最终的结果解读策略
     */
    fun getOrder(): Int
}
