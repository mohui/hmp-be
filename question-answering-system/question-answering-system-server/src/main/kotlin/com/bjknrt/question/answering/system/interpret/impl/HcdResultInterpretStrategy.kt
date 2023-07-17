package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.google.common.collect.ImmutableMap
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class HcdResultInterpretStrategy() : ResultInterpretStrategy {
    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        val result = ImmutableMap.of(
            "低位", "建议：\n" +
                    "1. 充分认识高血压的危害；\n" +
                    "2. 经常测量血压，定期体检；\n" +
                    "3. 学习掌握高血压疾病的有关知识；\n" +
                    "4. 如果一年后血压还不能正常，就应即刻启动药物治疗。",
            "中危", "建议：\n" +
                    "1. 积极开展生活方式调整治疗；\n" +
                    "2. 经常测量血压，定期体检；\n" +
                    "3. 学习掌握高血压疾病的相关知识；\n" +
                    "4. 如果经过6个月生活方式调整治疗无效，应即刻启动药物方案。",
            "高危", "建议：\n" +
                    "1. 将血压长期控制在“达标”水平；\n" +
                    "2. 尽可能选用对靶器官有明确保护作用的抗高血压药物，一般需要多种药物联合治疗，要遵医嘱用药；\n" +
                    "3. 生活方式调整治疗与药物治疗同等重要, 要纠正不良生活习惯；\n" +
                    "4. 要养成自测血压的习惯，且需定期体检；\n" +
                    "5. 积极治疗并发症；\n" +
                    "6. 掌握更多疾病知识，学会最基本的急救方法。"
        )
        //获取填写的血压值
        val pressure = interpretMatter.optionList.filter { op ->
            op.optionId == BigInteger.valueOf(190101) || op.optionId == BigInteger.valueOf(190201)
        }
        //根据血压得到分类
        val category = listOf(pressure.first { it.optionId == BigInteger.valueOf(190101) }.optionValue.toInt().let {
            if (it < 160) 1
            else if (it in 160 until 180) 2
            else 3
        }, pressure.first { it.optionId == BigInteger.valueOf(190201) }.optionValue.toInt().let {
            if (it < 100) 1
            else if (it in 100 until 110) 2
            else 3
        }).max()
        //根据分类与其他危险因素病史得到最终标签
        val resultQ2 = interpretMatter.optionList.filter { op ->
            op.questionsId == BigInteger.valueOf(1903) &&
                    op.optionId != BigInteger.valueOf(190308)
        }.size
        val resultQ3_4 = interpretMatter.optionList.filter { op ->
            (op.questionsId == BigInteger.valueOf(1904) &&
                    op.optionId != BigInteger.valueOf(190405)) ||
                    (op.questionsId == BigInteger.valueOf(1905) &&
                            op.optionId != BigInteger.valueOf(190507))
        }.size
        val tag = if (category == 1 && resultQ2 == 0 && resultQ3_4 == 0) "低位"
        else
            if (category == 3 || resultQ2 >= 3 || resultQ3_4 >= 1) "高危"
            else "中危"
        return InterpretResult(tag, result[tag])
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.examinationPaperCode == "HCD"
    }

    override fun getOrder(): Int {
        return 4
    }
}
