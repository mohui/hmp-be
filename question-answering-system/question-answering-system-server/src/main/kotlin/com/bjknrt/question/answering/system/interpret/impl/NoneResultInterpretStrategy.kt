package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import org.springframework.stereotype.Component

@Component
class NoneResultInterpretStrategy : ResultInterpretStrategy {

    companion object {
        val codeList = listOf("NONE", "DEFAULT_NONE")
        val interpretResult = InterpretResult("", "")
    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        return interpretResult
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return codeList.any { it == examinationPaper.strategyCode }
    }

    override fun getOrder(): Int {
        return 1
    }


}
