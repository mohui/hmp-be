package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasInterpretationOfResults
import com.bjknrt.question.answering.system.QasInterpretationOfResultsTable
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.bjknrt.question.answering.system.interpret.impl.NoneResultInterpretStrategy.Companion.interpretResult
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

@Component
class EssenResultInterpretStrategy(
    val interpretationOfResults: QasInterpretationOfResultsTable
) : ResultInterpretStrategy {
    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        return getInterpretationOfResults(interpretMatter.examinationPaperId, interpretMatter.score)
            ?.let {
                InterpretResult(
                    it.resultsTag,
                    it.resultsMsg
                )
            } ?: interpretResult
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "ESSEN"
    }

    override fun getOrder(): Int {
        return 11
    }


    private fun getInterpretationOfResults(
        examinationPaperId: BigInteger, totalScore: BigDecimal?
    ): QasInterpretationOfResults? {
        return totalScore?.let {
            interpretationOfResults.select()
                .where(QasInterpretationOfResultsTable.ExaminationPaperId eq examinationPaperId)
                .where(QasInterpretationOfResultsTable.IsDel eq false)
                .find().filter { it.minValue <= totalScore }.firstOrNull { it.maxValue >= totalScore }
        }
    }
}
