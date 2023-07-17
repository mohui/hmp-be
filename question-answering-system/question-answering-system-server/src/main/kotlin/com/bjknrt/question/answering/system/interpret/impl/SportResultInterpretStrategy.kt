package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import org.springframework.stereotype.Component


@Component
class SportResultInterpretStrategy(
) : ResultInterpretStrategy {
    val optionCodeMap = linkedMapOf(
        Pair("SPORT_150001_01", "为了您的安全，建议您向医生咨询运动方案。"),
        Pair("SPORT_150001_02", "为了您的安全，建议您向医生咨询运动方案。"),
        Pair("SPORT_150001_03", "为了您的安全，建议您向医生咨询运动方案。"),
        Pair("SPORT_150001_04", "为了您的安全，建议您向医生咨询运动方案。"),
        Pair("SPORT_150001_05", "为了您的安全，建议您向医生咨询运动方案。"),
        Pair("SPORT_150001_06", "为了您的安全，建议您向医生咨询运动方案。"),
    )

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        val option = interpretMatter.optionList.sortedBy { it.optionCode }.find {
            optionCodeMap.contains(it.optionCode)
        }
        option?.let {
            return InterpretResult(optionCodeMap.getOrDefault(it.optionCode, ""), "")
        }
        return InterpretResult("", "")
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "SPORT"
    }

    override fun getOrder(): Int {
        return 3
    }
}
