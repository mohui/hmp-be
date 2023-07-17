package com.bjknrt.question.answering.system.interpret.impl

import cn.hutool.core.collection.CollUtil
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.entity.AbnormalDataAlertMsg
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.bjknrt.question.answering.system.util.getBmiValue
import com.bjknrt.question.answering.system.util.waistlineIsStandard
import com.bjknrt.security.client.AppSecurityUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
class BehaviorResultInterpretStrategy(
    val patientInfoRpcService: PatientApi,
) : ResultInterpretStrategy {
    companion object {

        const val EXAMINATION_PAPER_CODE = "BEHAVIOR"

        //用来判断的选项编码
        //第一题选项（BMI）
        const val BEHAVIOR_140001_1 = "BEHAVIOR_140001_1"
        const val BEHAVIOR_140001_2 = "BEHAVIOR_140001_2"

        //第二题填写（腰围）
        const val BEHAVIOR_140002_1 = "BEHAVIOR_140002_1"

        //第三题选项
        val BEHAVIOR_140003 = listOf(
            "BEHAVIOR_140003_2",
            "BEHAVIOR_140003_3"
        )

        //第四题和第五题选项
        val BEHAVIOR_140004_5 = listOf(
            "BEHAVIOR_140004_2",
            "BEHAVIOR_140004_3",
            "BEHAVIOR_140005_2",
            "BEHAVIOR_140005_3"
        )

        //第1题，若答案为“BMI ≥ 24 kg/m²”,则添加正文
        const val MSG_TAG_BMI_TITLE = "您的体质指数（BMI）超过了24 kg/m²"
        const val MSG_TAG_BMI_CONTENT =
            "BMI ≥ 24 kg/m²可能会增加您的心血管疾病发生风险，合理的体重管理可以改善血压，对血糖、血脂等也具有改善作用。因此建议您在3~6个月内减轻5%~10%的体重。"

        //第2题，若答案为“否”，则添加正文
        const val MSG_TAG_WAISTLINE_TITLE = "您的腰围偏大"
        const val MSG_TAG_WAISTLINE_CONTENT =
            "男性腰围≥90cm、女性腰围≥85cm可能会增加心血管疾病的发生危险，因此建议您通过合理的膳食和运动减小腰围。"

        //第3题，若答案为“偶尔”或“经常”，则添加正文：
        const val MSG_TAG_SLEEP_HARD_TITLE = "您在本阶段内夜间睡眠欠佳"
        const val MSG_TAG_SLEEP_HARD_CONTENT =
            "夜间睡眠差的患者夜间血压通常较高，全身得不到充分休息，容易导致心、脑、肾等器官受损。推荐您前往小程序“个人评估”模块选择“失眠严重程度指数量表”进行测评，必要时线下前往正规医院精神科、心理科等科室寻找医生帮助。"

        //第4或5题，若答案为“偶尔”或“经常”，则添加正文
        const val MSG_TAG_SAD_OR_ANXIETY_TITLE = "您在本阶段内精神状态欠佳"
        const val MSG_TAG_SAD_OR_ANXIETY_CONTENT =
            "人在紧张、压抑、焦虑等状态下，血压容易升高，心血管病发生风险增加。推荐您前往“个人评估”模块选择相应量表进行自我评估，必要时可线下前往正规医院精神科、心理科等科室找医生帮助。"
    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        //获取提患者者信息
        val patient = patientInfoRpcService.getPatientInfo(AppSecurityUtil.currentUserIdWithDefault())
        //准备页面已经选择或填写的选项数据
        val optionMap = interpretMatter.optionList.associate { it.optionCode to it.optionValue }
        val optionCodeList = interpretMatter.optionList.map { it.optionCode }

        //第1题：获取页面填写的BMI的值
        val height = getOptionValue(BEHAVIOR_140001_1, optionMap)
        val weight = getOptionValue(BEHAVIOR_140001_2, optionMap)
        val bmiValue = getBmiValue(height, weight)
        val waistline =
            interpretMatter.optionList.firstOrNull { it.optionCode == BEHAVIOR_140002_1 }?.optionValue?.toDouble()

        //第2/3/4/5题：判断页面是否选择此选项
        val notWaistlineStandard = !waistlineIsStandard(patient.gender, waistline)
        val isSleepHard = CollUtil.containsAny(optionCodeList, BEHAVIOR_140003)
        val isSadOrAnxiety = CollUtil.containsAny(optionCodeList, BEHAVIOR_140004_5)

        //根据上述计算结果，进行拼接消息
        val msg = getResultMsg(
            bmiValue,
            notWaistlineStandard,
            isSleepHard,
            isSadOrAnxiety
        )
        return InterpretResult("", msg)
    }


    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == EXAMINATION_PAPER_CODE
    }

    override fun getOrder(): Int {
        return 6
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
     * @param bmiValue  BMI值
     * @param notWaistlineStandard  是否不在腰围标准范围内
     * @param isSleepHard  是否睡眠困难
     * @param isSadOrAnxiety 是否情绪低落或者焦虑
     * @return 结果拼接字符串
     */
    private fun getResultMsg(
        bmiValue: BigDecimal,
        notWaistlineStandard: Boolean,
        isSleepHard: Boolean,
        isSadOrAnxiety: Boolean,
    ): String {
        val list = mutableListOf<AbnormalDataAlertMsg>()

        if (bmiValue >= BigDecimal.valueOf(24)) {
            list.add(
                AbnormalDataAlertMsg(MSG_TAG_BMI_TITLE, MSG_TAG_BMI_CONTENT)
            )
        }
        if (notWaistlineStandard) {
            list.add(
                AbnormalDataAlertMsg(MSG_TAG_WAISTLINE_TITLE, MSG_TAG_WAISTLINE_CONTENT)
            )
        }

        if (isSleepHard) {
            list.add(
                AbnormalDataAlertMsg(MSG_TAG_SLEEP_HARD_TITLE, MSG_TAG_SLEEP_HARD_CONTENT)
            )
        }

        if (isSadOrAnxiety) {
            list.add(
                AbnormalDataAlertMsg(MSG_TAG_SAD_OR_ANXIETY_TITLE, MSG_TAG_SAD_OR_ANXIETY_CONTENT)
            )
        }
        return ObjectMapper().writeValueAsString(list)
    }

}
