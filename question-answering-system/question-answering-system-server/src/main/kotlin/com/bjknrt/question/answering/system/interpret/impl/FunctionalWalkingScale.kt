package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Component

@Component
class FunctionalWalkingScale  : ResultInterpretStrategy {

    val optionCodeMap = linkedMapOf(
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000101", "0级"),
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000102", "1级"),
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000103", "2级"),
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000104", "3级"),
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000105", "4级"),
        Pair("FUNCTIONAL_WALKING_SCALE_300001_30000106", "5级")
    )

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        val msg = interpretMatter.optionList.firstOrNull{ optionCodeMap.keys.contains(it.optionCode)}
            ?.let { optionCodeMap[it.optionCode] }
            ?: throw KatoException(AppSpringUtil.getMessage("question.option.error"))

        return InterpretResult("", msg)
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "FUNCTIONAL_WALKING_SCALE"
    }

    override fun getOrder(): Int {
        return 11
    }
}