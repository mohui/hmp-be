package com.bjknrt.question.answering.system.evaluate

import java.math.BigDecimal

/**
 * 评估分值接口
 */
interface EvaluateScoreStrategy {

    /**
     * 评估分值逻辑
     * @return 最终分值
     */
    fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal?

    /**
     * 评估分值策略唯一编码
     */
    fun getCode(): String
}
