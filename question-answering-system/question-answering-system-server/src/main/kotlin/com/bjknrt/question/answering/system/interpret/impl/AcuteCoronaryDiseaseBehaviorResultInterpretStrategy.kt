package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.entity.AbnormalDataAlertMsg
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.bjknrt.question.answering.system.util.getBmiValue
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
class AcuteCoronaryDiseaseBehaviorResultInterpretStrategy : ResultInterpretStrategy {
    companion object {

        const val EXAMINATION_PAPER_CODE = "ACUTE_CORONARY_DISEASE_BEHAVIOR"

        //用来判断的选项编码
        //第一题选项（BMI）
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_1 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_1"
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_2 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_2"

        //第二题
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_2 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_2"

        //第三题
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_2 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_2"
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_3 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_3"

        //第四题
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_2 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_2"
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_3 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_3"

        //第五题
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_2 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_2"
        const val ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_3 = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_3"

        //第1题，若答案为“BMI ≥ 24 kg/m²”,则添加正文
        const val MSG_TAG_BMI_TITLE = "您的体质指数（BMI）超过了24 kg/m²"
        const val MSG_TAG_BMI_CONTENT =
            "BMI ≥ 24 kg/m²可能会增加心血管疾病的发生风险，合理的体重管理对血压、血糖、血脂等都具有改善作用。因此建议您3~6个月内减轻5%~10%的体重。"

        //患者填写第2题，若答案为“否”，则添加正文：
        const val MSG_TAG_2_TITLE = "您的腰围偏大"
        const val MSG_TAG_2_CONTENT =
            "男性腰围≥90cm、女性腰围≥85cm可能会增加心血管疾病的发生危险。因此建议您通过平衡膳食、合理运动减小腰围。"

        //患者填写第3题，若答案为“偶尔”或“经常”，则添加正文
        const val MSG_TAG_3_TITLE = "您在本阶段内夜间睡眠欠佳"
        const val MSG_TAG_3_CONTENT =
            "夜间睡眠差的患者夜间血压通常较高，全身得不到充分休息，容易导致心、脑、肾等器官受损。推荐您前往小程序“个人评估”模块选择“失眠严重程度指数量表”进行测评，必要时线下前往正规医院精神科、心理科等科室寻找医生帮助。"

        //患者填写第4、5题，若答案为“偶尔”或“经常”，则添加正文
        const val MSG_TAG_4_5_TITLE = "您在本阶段内精神状态欠佳"
        const val MSG_TAG_4_5_CONTENT =
            "人在紧张、压抑、焦虑等状态下，血压容易升高，心血管病发生风险增加。推荐您前往“个人评估”模块选择相应量表进行自我评估，必要时可线下前往正规医院精神科、心理科等科室寻找医生帮助。"

    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        //准备页面已经选择或填写的选项数据
        val optionMap = interpretMatter.optionList.associate { it.optionCode to it.optionValue }

        //第1题：获取页面填写的BMI的值
        val height = getOptionValue(ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_1, optionMap)
        val weight = getOptionValue(ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_2, optionMap)
        val bmiValue = getBmiValue(height, weight)

        //第2题，若答案为“经常”
        val isWaistlineOften = optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_2)

        //第3题，若答案为“偶尔”或“经常”，
        val isBreatheOften =
            optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_2) || optionMap.containsKey(
                ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_3
            )

        //第4、5题，若答案为“偶尔”或“经常”
        val isEmotionOften =
            optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_2) ||
                    optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_3) ||
                    optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_2) ||
                    optionMap.containsKey(ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_3)

        //根据上述计算结果，进行拼接消息
        val msg = getResultMsg(bmiValue, isWaistlineOften, isBreatheOften, isEmotionOften)
        return InterpretResult("", msg)
    }


    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == EXAMINATION_PAPER_CODE
    }

    override fun getOrder(): Int {
        return 8
    }

    /**
     * 获取选项的值，没有返回0
     * @param optionCode 选项编码
     * @param optionValueMap 选项值Map
     * @return 选项的值，没有返回0
     */
    private fun getOptionValue(optionCode: String, optionValueMap: Map<String?, String>): BigDecimal {
        if (optionValueMap.containsKey(optionCode)) {
            val value = optionValueMap[optionCode]
            return if (value.isNullOrEmpty()) BigDecimal.ZERO else BigDecimal(value)
        }
        return BigDecimal.ZERO
    }

    /**
     * 获取结果解读消息
     * @return 结果拼接字符串
     */
    private fun getResultMsg(
        bmiValue: BigDecimal,
        isWaistlineOften: Boolean,
        isBreatheOften: Boolean,
        isEmotionOften: Boolean,
    ): String {
        val list = mutableListOf<AbnormalDataAlertMsg>()

        if (bmiValue >= BigDecimal.valueOf(24)) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_BMI_TITLE,
                    MSG_TAG_BMI_CONTENT
                )
            )
        }
        if (isWaistlineOften) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_2_TITLE,
                    MSG_TAG_2_CONTENT
                )
            )
        }
        if (isBreatheOften) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_3_TITLE,
                    MSG_TAG_3_CONTENT
                )
            )
        }
        if (isEmotionOften) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_4_5_TITLE,
                    MSG_TAG_4_5_CONTENT
                )
            )
        }
        return ObjectMapper().writeValueAsString(list)
    }

}
