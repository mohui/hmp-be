package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Component


@Component
class BrunnstromResultInterpretStrategy(
) : ResultInterpretStrategy {
    val optionCodeMap_1 = linkedMapOf(
        Pair("BRUNNSTROM_270001_27000101", "I"),
        Pair("BRUNNSTROM_270001_27000102", "II"),
        Pair("BRUNNSTROM_270001_27000103", "III"),
        Pair("BRUNNSTROM_270001_27000104", "IV"),
        Pair("BRUNNSTROM_270001_27000105", "V"),
        Pair("BRUNNSTROM_270001_27000106", "VI"),
    )
    val optionCodeMap_2 = linkedMapOf(
        Pair("BRUNNSTROM_270002_27000201", "I"),
        Pair("BRUNNSTROM_270002_27000202", "II"),
        Pair("BRUNNSTROM_270002_27000203", "III"),
        Pair("BRUNNSTROM_270002_27000204", "IV"),
        Pair("BRUNNSTROM_270002_27000205", "V"),
        Pair("BRUNNSTROM_270002_27000206", "VI"),
    )
    val optionCodeMap_3 = linkedMapOf(
        Pair("BRUNNSTROM_270003_27000301", "I"),
        Pair("BRUNNSTROM_270003_27000302", "II"),
        Pair("BRUNNSTROM_270003_27000303", "III"),
        Pair("BRUNNSTROM_270003_27000304", "IV"),
        Pair("BRUNNSTROM_270003_27000305", "V"),
        Pair("BRUNNSTROM_270003_27000306", "VI"),
    )
    val optionCodeMap_4 = linkedMapOf(
        Pair("BRUNNSTROM_270004_27000401", "I"),
        Pair("BRUNNSTROM_270004_27000402", "II"),
        Pair("BRUNNSTROM_270004_27000403", "III"),
        Pair("BRUNNSTROM_270004_27000404", "IV"),
        Pair("BRUNNSTROM_270004_27000405", "V"),
        Pair("BRUNNSTROM_270004_27000406", "VI"),
    )
    val optionCodeMap_5 = linkedMapOf(
        Pair("BRUNNSTROM_270005_27000501", "I"),
        Pair("BRUNNSTROM_270005_27000502", "II"),
        Pair("BRUNNSTROM_270005_27000503", "III"),
        Pair("BRUNNSTROM_270005_27000504", "IV"),
        Pair("BRUNNSTROM_270005_27000505", "V"),
        Pair("BRUNNSTROM_270005_27000506", "VI"),
    )
    val optionCodeMap_6 = linkedMapOf(
        Pair("BRUNNSTROM_270006_27000601", "I"),
        Pair("BRUNNSTROM_270006_27000602", "II"),
        Pair("BRUNNSTROM_270006_27000603", "III"),
        Pair("BRUNNSTROM_270006_27000604", "IV"),
        Pair("BRUNNSTROM_270006_27000605", "V"),
        Pair("BRUNNSTROM_270006_27000606", "VI"),
    )

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        //上肢-左
        val upperBodyLeft = getOption(interpretMatter, optionCodeMap_1)
        //上肢-右
        val upperBodyRight = getOption(interpretMatter, optionCodeMap_2)
        //手-左
        val handLeft = getOption(interpretMatter, optionCodeMap_3)
        //手-右
        val handRight = getOption(interpretMatter, optionCodeMap_4)
        //下肢-左
        val lowerBodyLeft = getOption(interpretMatter, optionCodeMap_5)
        //下肢-右
        val lowerBodyRight = getOption(interpretMatter, optionCodeMap_6)

        val msg = "上肢\n" +
                "左：${upperBodyLeft}期             右：${upperBodyRight}期\n" +
                "手\n" +
                "左：${handLeft}期             右：${handRight}期\n" +
                "下肢\n" +
                "左：${lowerBodyLeft}期             右：${lowerBodyRight}期"
        return InterpretResult("", msg)
    }

    private fun getOption(interpretMatter: InterpretMatter, optionCodeMap: Map<String, String>): String {
        return interpretMatter.optionList.firstOrNull { optionCodeMap.keys.contains(it.optionCode) }
            ?.let { optionCodeMap[it.optionCode] }
            ?: throw KatoException(AppSpringUtil.getMessage("question.option.error"))
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "BRUNNSTROM"
    }

    override fun getOrder(): Int {
        return 9
    }
}
