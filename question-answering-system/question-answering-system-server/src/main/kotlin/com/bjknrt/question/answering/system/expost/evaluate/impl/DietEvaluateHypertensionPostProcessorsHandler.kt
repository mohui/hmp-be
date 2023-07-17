package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 饮食评估后置处理逻辑
 * 推送到健康方案项目
 */
@Component
class DietEvaluateHypertensionPostProcessorsHandler(
    val clockInRpcService: ClockInApi,
    val healthPlanManageRpcService: ManageApi,
    val examinationRpcService: ExaminationApi
) : EvaluatePostProcessorsHandler {

    protected val examinationPaperCode = "DIET_EVALUATE_HYPERTENSION"
    protected val healthPlanType = HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION

    //定义的每个饮食评估的选项解读内容
    protected val optionCodeMap = mapOf(
        Pair(
            "DIET_EVALUATE_350001_1",
            "清淡饮食有利于健康，请继续保持。正常成人每日食盐摄入量不超过5g，烹调油摄入量不超过30g，多吃蔬菜水果，少吃富含饱和脂肪酸的食物。"
        ),
        Pair(
            "DIET_EVALUATE_350001_2",
            "限制盐的摄入，每日摄入量不超过5g。减少烹调用盐及含钠高的调味品，如味精、鸡精、酱油。避免或减少含钠盐量较高的加工食品的摄入，如咸菜、火腿、各类炒货、腌制品、烟熏食品、奶酪、虾皮等。"
        ),
        Pair(
            "DIET_EVALUATE_350001_3",
            "限制脂肪摄入量，每日烹调油控制在30克以下，少吃富含饱和脂肪酸的食物（猪油、牛油、鸡油、奶油等）、动物内脏（脑、肝脏、肾脏）、富含胆固醇的食物（肥肉、鱼子、蛋黄、动物内脏、蟹黄、鱿鱼等）。"
        ),
        Pair(
            "DIET_EVALUATE_350002_1",
            "腌制食品和加工食品，如咸菜、火腿、各类炒货、腌制品、烟熏食品、奶酪、虾皮等通常含钠盐量较高，建议您少吃或不吃。增加富钾食物（新鲜蔬菜、水果和豆类）的摄入量可降低血压。"
        ),
        Pair(
            "DIET_EVALUATE_350003_1",
            "在外就餐时食物中的油、盐含量相对较高，请尽量减少在外就餐次数，或以清淡、少盐、少油食物为主。"
        ),
        Pair(
            "DIET_EVALUATE_350004_1",
            "酒只能提供热能，几乎不含其它营养素，且饮酒不利于血压控制。为了您的健康，请继续保持“滴酒不沾”。"
        ),
        Pair(
            "DIET_EVALUATE_350004_2",
            "饮酒后会导致血压出现明显波动，不利于血压控制，也会增加心血管疾病的风险，高血压患者不建议饮酒。"
        ),
        Pair(
            "DIET_EVALUATE_350004_3",
            "酒精会明显影响药物的吸收、分布和药效。另外，饮酒后会导致血压出现明显波动，不利于血压控制，也会增加心血管疾病的风险。高血压患者不建议饮酒。"
        ),
        Pair("DIET_EVALUATE_350005_1", "吸烟是一种不健康行为，是心血管病和癌症的主要危险因素之一。建议您尽快戒烟。"),
        Pair(
            "DIET_EVALUATE_350006_1",
            "男性每天水的适宜摄入量为1700ml，女性为1500ml，您的水摄入量充足。建议饮水的适宜温度在10-40℃。在进行身体活动时，要注意身体活动前、中和后水分的摄入，可分别喝100~200ml，以保持良好的水合状态。"
        ),
        Pair(
            "DIET_EVALUATE_350006_2",
            "您每日喝水量适宜，有利于您的身体健康。建议饮水的适宜温度在10~40℃。在进行身体活动时，要注意身体活动前、中和后水分的摄入，可分别喝100~200ml，以保持良好的水合状态。"
        ),
        Pair(
            "DIET_EVALUATE_350006_3",
            "男性每天水的适宜摄入量为1700ml，女性为1500ml，您的水摄入量不足。为了您的健康，应主动喝水、少量多次。可以在一天的任意时间，每次1杯，每杯约200ml。"
        )
    )

    override fun getOrder(): Int {
        return 11
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.examinationPaperCode == examinationPaperCode
    }

    override fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>) {
        //打卡
        clockInRpcService.saveClockIn(
            ClockInRequest(
                patientId = answerRecord.answerBy,
                healthPlanType = healthPlanType,
                currentDateTime = LocalDateTime.now()
            )
        )
        //删除未进行评估(饮食)的饮食计划
        healthPlanManageRpcService.delHealthPlanByPatientIdAndTypes(
            DelHealthPlanByPatientIdAndTypes(
                patientId = answerRecord.answerBy,
                types = listOf(healthPlanType),
            )
        )
        //推送健康方案
        examinationRpcService.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = answerRecord.answerBy,
                knExaminationPaperCode = examinationPaperCode,
                knExaminationPaperOptionList = answerResultList.map {
                    ExaminationPaperOption(
                        knAnswerRecordId = answerRecord.id,
                        knAnswerResultId = it.id,
                        knQuestionsId = it.questionsId,
                        knOptionId = it.optionId,
                        knMessage = optionCodeMap.getOrDefault(it.optionCode, "")
                    )
                }
            )
        )
    }
}