package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import org.springframework.stereotype.Component

@Component
class DrugProgramInterpretStrategy: ResultInterpretStrategy {
    companion object {
        val optionCodeMap = linkedMapOf(
            Pair("HYPERTENSION_DRUG_PROGRAM_330001_1", "遵医嘱服药。"),
            Pair("DIABETES_DRUG_PROGRAM_340001_1", "遵医嘱服药或使用胰岛素。"),
            Pair(
                "DIABETES_DRUG_PROGRAM_340001_2",
                "糖尿病的营养治疗和运动治疗是控制血糖的基本措施。在饮食和运动不能使血糖控制达标时应及时采用包括口服药治疗在内的药物治疗。\n" +
                        "请在医生的指导下规范用药，控制血糖达标。"
            )
        )
    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        val option = interpretMatter.optionList.find {
            optionCodeMap.contains(it.optionCode)
        }
        option?.let {
            return InterpretResult("", optionCodeMap.getOrDefault(it.optionCode, ""))
        }
        return InterpretResult("", "")
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "DRUG_PROGRAM"
    }

    override fun getOrder(): Int {
        return 16
    }
}