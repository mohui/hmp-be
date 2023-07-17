package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Component

@Component
class SimpleThreeStageBalance : ResultInterpretStrategy {
    val sitOptionCodeMap = linkedMapOf(
        Pair("SIMPLE_THREE_STAGE_BALANCE_290001_29000101", "一"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290001_29000102", "二"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290001_29000103", "三")
    )
    val stationOptionCodeMap = linkedMapOf(
        Pair("SIMPLE_THREE_STAGE_BALANCE_290002_29000201", "一"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290002_29000202", "二"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290002_29000203", "三")
    )
    val walkOptionCodeMap = linkedMapOf(
        Pair("SIMPLE_THREE_STAGE_BALANCE_290003_29000301", "一"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290003_29000302", "二"),
        Pair("SIMPLE_THREE_STAGE_BALANCE_290003_29000303", "三")
    )

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        val sit = getOption(interpretMatter, sitOptionCodeMap)
        val station = getOption(interpretMatter, stationOptionCodeMap)
        val walk = getOption(interpretMatter, walkOptionCodeMap)
        val msg = "坐位：${sit}级\n" +
                "站位：${station}级\n" +
                "行走：${walk}级"
        return InterpretResult("", msg)
    }

    private fun getOption(interpretMatter: InterpretMatter, optionCodeMap: Map<String, String>): String {
        return interpretMatter.optionList.firstOrNull{ optionCodeMap.keys.contains(it.optionCode)}
            ?.let { optionCodeMap[it.optionCode] }
            ?: throw KatoException(AppSpringUtil.getMessage("question.option.error"))
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "SIMPLE_THREE_STAGE_BALANCE"
    }

    override fun getOrder(): Int {
        return 10
    }
}