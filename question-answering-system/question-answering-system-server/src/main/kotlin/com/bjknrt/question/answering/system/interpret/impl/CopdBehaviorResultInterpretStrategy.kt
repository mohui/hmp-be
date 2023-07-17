package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.entity.AbnormalDataAlertMsg
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component


@Component
class CopdBehaviorResultInterpretStrategy : ResultInterpretStrategy {
    companion object {

        const val EXAMINATION_PAPER_CODE = "COPD_BEHAVIOR"

        //用来判断的选项编码

        const val COPD_BEHAVIOR_310002_3 = "COPD_BEHAVIOR_310002_3"
        const val COPD_BEHAVIOR_310003_3 = "COPD_BEHAVIOR_250004_3"
        const val COPD_BEHAVIOR_310004_3 = "COPD_BEHAVIOR_250005_3"


        //患者填写第2题，若答案为“经常”，则添加正文：
        const val MSG_TAG_2_TITLE = "您本阶段内可能存在阻塞性睡眠呼吸暂停"
        const val MSG_TAG_2_CONTENT =
            "由于慢阻肺患者中约有20%~55%的人会发生阻塞性睡眠呼吸暂停，建议您前往“个人评估”模块选择“STOP‐bang 问卷”评估发生阻塞性睡眠呼吸暂停的风险。如您症状较严重，建议您及时前往呼吸科或耳鼻喉科就诊。"

        //患者填写第3、4题，若答案为“经常”，则添加正文：
        const val MSG_TAG_3_4_TITLE = "您本阶段内精神状态欠佳"
        const val MSG_TAG_3_4_CONTENT =
            "推荐您前往“个人评估”模块选择相应量表进行自我评估，必要时可线下前往正规医院精神科、心理科等科室寻找医生帮助。"

    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        //准备页面已经选择或填写的选项数据
        val optionMap = interpretMatter.optionList.associate { it.optionCode to it.optionValue }

        //第2题，若答案为“经常”
        val isBreatheOften = optionMap.containsKey(COPD_BEHAVIOR_310002_3)

        //第3、4题，若答案为“经常”
        val isEmotionOften =
            optionMap.containsKey(COPD_BEHAVIOR_310003_3) || optionMap.containsKey(COPD_BEHAVIOR_310004_3)

        //根据上述计算结果，进行拼接消息
        val msg = getResultMsg(isBreatheOften, isEmotionOften)
        return InterpretResult("", msg)
    }


    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == EXAMINATION_PAPER_CODE
    }

    override fun getOrder(): Int {
        return 8
    }

    /**
     * 获取结果解读消息
     * @return 结果拼接字符串
     */
    private fun getResultMsg(
        isBreatheOften: Boolean,
        isEmotionOften: Boolean,
    ): String {
        val list = mutableListOf<AbnormalDataAlertMsg>()


        if (isBreatheOften) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_2_TITLE,
                    MSG_TAG_2_CONTENT
                )
            )
        }
        if (isEmotionOften) {
            list.add(
                AbnormalDataAlertMsg(
                    MSG_TAG_3_4_TITLE,
                    MSG_TAG_3_4_CONTENT
                )
            )
        }
        return ObjectMapper().writeValueAsString(list)
    }

}
