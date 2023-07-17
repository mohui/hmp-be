package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.stereotype.Component

/**
 * 饮食评估后置处理逻辑
 * 推送到健康方案项目
 */
@Component
class DietEvaluateDiabetesPostProcessorsHandler(
    clockInRpcService: ClockInApi,
    healthPlanManageRpcService: ManageApi,
    examinationRpcService: ExaminationApi
) : DietEvaluateHypertensionPostProcessorsHandler(
    clockInRpcService,
    healthPlanManageRpcService,
    examinationRpcService
) {

    override val examinationPaperCode = "DIET_EVALUATE_DIABETES"

    override val healthPlanType = HealthPlanType.DIET_NOT_EVALUATED_DIABETES

    override val optionCodeMap = mapOf(
        Pair(
            "DIET_EVALUATE_360001_1",
            "清淡饮食有利于健康，请继续保持。正常成人每日食盐摄入量不超过5g，烹调油摄入量不超过30g，多吃蔬菜水果，少吃富含饱和脂肪酸的食物。"
        ),
        Pair(
            "DIET_EVALUATE_360001_2",
            "限制盐的摄入，每日摄入量不超过5g。减少烹调用盐及含钠高的调味品，如味精、鸡精、酱油。避免或减少含钠盐量较高的加工食品的摄入，如咸菜、火腿、各类炒货、腌制品、烟熏食品、奶酪、虾皮等。"
        ),
        Pair(
            "DIET_EVALUATE_360001_3",
            "限制脂肪摄入量，每日烹调油控制在30克以下，少吃富含饱和脂肪酸的食物（猪油、牛油、鸡油、奶油等）、动物内脏（脑、肝脏、肾脏）、富含胆固醇的食物（肥肉、鱼子、蛋黄、动物内脏、蟹黄、鱿鱼等）。"
        ),
        Pair(
            "DIET_EVALUATE_360001_4",
            "糖尿病患者可以适量吃甜食，但要注意甜食的种类，如要严格限制白糖、巧克力、甜点心等的摄入；可适量进食水果，原则是适量减少主食的摄入量。"
        ),
        Pair(
            "DIET_EVALUATE_360002_1",
            "吃粗杂粮后血糖升高速度比吃等量的精米精面慢，有利于血糖的平稳。建议您每日主食中添加一些薯类杂粮，如土豆、山药、地瓜等食物，同时适量减少米面等主食的摄入，或以燕麦、荞麦、高粱等粗粮面粉加入主食做杂粮饭或杂粮馒头。"
        ),
        Pair(
            "DIET_EVALUATE_360002_2",
            "吃粗杂粮后血糖升高速度比吃等量的精米精面慢，有利于血糖的平稳。建议您增加杂粮摄入量，每日主食中添加一些薯类杂粮，如土豆、山药、地瓜等食物，同时适量减少米面等主食的摄入，或以燕麦、荞麦、高粱等粗粮面粉加入主食做杂粮饭或杂粮馒头。"
        ),
        Pair("DIET_EVALUATE_360002_3", "吃粗杂粮后血糖升高速度比吃等量的精米精面慢，有利于血糖的平稳。"),
        Pair(
            "DIET_EVALUATE_360003_1",
            "酒只能提供热能，几乎不含其它营养素，且饮酒不利于血压控制。为了您的健康，请继续保持“滴酒不沾”。"
        ),
        Pair(
            "DIET_EVALUATE_360003_2",
            "饮酒后会导致血压出现明显波动，不利于血压控制，也会增加心血管疾病的风险，高血压患者不建议饮酒。"
        ),
        Pair(
            "DIET_EVALUATE_360003_3",
            "酒精会明显影响药物的吸收、分布和药效。另外，饮酒后会导致血压出现明显波动，不利于血压控制，也会增加心血管疾病的风险。高血压患者不建议饮酒。"
        ),
        Pair(
            "DIET_EVALUATE_360004_1",
            "坚果富含脂肪、维生素E以及膳食纤维。最好放在两餐之间空腹时作为加餐，控制食用数量，全天10粒左右。"
        ),
        Pair(
            "DIET_EVALUATE_360004_2",
            "膨化食品不适合糖尿病患者食用，虽然不甜，但热量高，也含有许多有害物质（如反式脂肪酸）。因此建议您少吃或不吃膨化食品。"
        ),
        Pair(
            "DIET_EVALUATE_360004_3",
            "糖果/甜糕点是含糖为主的零食，摄入后可直接升高血糖，糖尿病患者一般情况下不建议吃。"
        ),
        Pair("DIET_EVALUATE_360004_4", "腌制食品含盐高，建议您少吃或不吃腌制食品，减少盐的摄入。"),
        Pair("DIET_EVALUATE_360004_5", "不吃零食有利于您的血糖控制，请继续保持。"),
        Pair(
            "DIET_EVALUATE_360005_1",
            "男性每天水的适宜摄入量为1700ml，女性为1500ml，您的水摄入量充足。建议饮水的适宜温度在10-40℃。在进行身体活动时，要注意身体活动前、中和后水分的摄入，可分别喝100~200ml，以保持良好的水合状态。"
        ),
        Pair(
            "DIET_EVALUATE_360005_2",
            "您每日喝水量适宜，有利于您的身体健康。建议饮水的适宜温度在10~40℃。在进行身体活动时，要注意身体活动前、中和后水分的摄入，可分别喝100~200ml，以保持良好的水合状态。"
        ),
        Pair(
            "DIET_EVALUATE_360005_3",
            "男性每天水的适宜摄入量为1700ml，女性为1500ml，您的水摄入量不足。为了您的健康，应主动喝水、少量多次。可以在一天的任意时间，每次1杯，每杯约200ml。"
        ),
        Pair(
            "DIET_EVALUATE_360006_1",
            "完全吃饱容易导致能量摄入过多，日积月累会导致超重和肥胖。最好在感觉还欠几口的时候就放下筷子，控制能量摄入，有助于维持健康体重，防止血糖波动。"
        ),
        Pair("DIET_EVALUATE_360006_2", "每顿少吃一两口，可预防摄入过多而引起超重和肥胖。"),
        Pair("DIET_EVALUATE_360006_3", "建议每餐吃到8-9分饱，避免过度饥饿引起饱食中枢反应迟钝而导致进食过量。"),
        Pair("DIET_EVALUATE_360007_1", "规律用餐，每餐定时定量。"),
        Pair("DIET_EVALUATE_360007_2", "在外就餐时，少喝酒，清淡、少盐、少油食物为主。"),
        Pair(
            "DIET_EVALUATE_360007_3",
            "不规律的饮食不利于医生调整药物的剂量，还会引起低血糖的发生。请您根据自身血糖情况，在医生的指导下定时、定量，合理加餐。"
        )
    )

    override fun getOrder(): Int {
        return 12
    }
}