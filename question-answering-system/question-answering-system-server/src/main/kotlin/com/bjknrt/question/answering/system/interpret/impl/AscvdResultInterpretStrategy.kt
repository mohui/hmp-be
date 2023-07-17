package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import org.springframework.stereotype.Component


@Component
class AscvdResultInterpretStrategy(
) : ResultInterpretStrategy {
    val optionCodeMap = linkedMapOf(
        Pair("ASCVD_110001_01", "ASCVD发病危险—极高危"),
        Pair("ASCVD_110002_01", "ASCVD发病危险—高危"),
        Pair("ASCVD_110003_01", "ASCVD发病危险—高危"),
        Pair("ASCVD_110005_01", "ASCVD 10年发病危险—低危"),
        Pair("ASCVD_110005_04", "ASCVD 10年发病危险—高危"),
        Pair("ASCVD_110006_01", "ASCVD 10年发病危险—低危"),
        Pair("ASCVD_110007_02", "ASCVD 10年发病危险—低危"),
        Pair("ASCVD_110008_01", "ASCVD 10年发病危险—高危"),
        Pair("ASCVD_110009_02", "ASCVD 10年发病危险—低危"),
        Pair("ASCVD_110010_02", "ASCVD 10年发病危险—低危"),
        Pair("ASCVD_110011_02", "ASCVD 10年发病危险—中危"),
        Pair("ASCVD_110012_01", "10年发病危险—中危\n余生危险—高危"),
        Pair("ASCVD_110012_02", "10年发病危险—中危\n余生危险—非高危"),
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
        return examinationPaper.strategyCode == "ASCVD"
    }

    override fun getOrder(): Int {
        return 2
    }
}
