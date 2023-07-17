package com.bjknrt.question.answering.system.interpret

import com.bjknrt.question.answering.system.vo.EvaluateResultQuestionsOption
import java.math.BigDecimal
import java.math.BigInteger

class InterpretMatter(
    val examinationPaperId: BigInteger,
    val score: BigDecimal?,
    val optionList: List<EvaluateResultQuestionsOption>
)
