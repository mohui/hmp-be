package com.bjknrt.question.answering.system.interpret.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.interpret.InterpretMatter
import com.bjknrt.question.answering.system.interpret.InterpretResult
import com.bjknrt.question.answering.system.interpret.ResultInterpretStrategy
import com.bjknrt.question.answering.system.util.*
import com.bjknrt.question.answering.system.vo.Gender
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
class ExpertAscvdResultInterpretStrategy(
    val patientInfoRpcService: PatientApi
) : ResultInterpretStrategy {
    companion object {
        //P值
        val P_VALUE_5: BigDecimal = BigDecimal.valueOf(5)
        val P_VALUE_10: BigDecimal = BigDecimal.valueOf(10)

        //收缩压
        val SBP_LIMIT_160: BigDecimal = BigDecimal.valueOf(160)

        //舒张压
        val DBP_LIMIT_100: BigDecimal = BigDecimal.valueOf(100)

        //BMI指数
        val BMI_LIMIT_25: BigDecimal = BigDecimal.valueOf(25)

        //高密度脂蛋白胆固醇(mmol/L)
        val HDL_LIMIT_1: BigDecimal = BigDecimal.valueOf(1)

        //非高密度脂蛋白胆固醇(mmol/L)
        val NO_HDL_LIMIT_5_2: BigDecimal = BigDecimal.valueOf(5.2)

        //血脂异常-总胆固醇(mmol/L)
        val DYSLIPIDEMIA_TC_LIMIT_6_2: BigDecimal = BigDecimal.valueOf(6.2)

        //血脂异常-高密度脂蛋白胆固醇(mmol/L)
        val DYSLIPIDEMIA_HDL_LIMIT_1: BigDecimal = BigDecimal.valueOf(1.0)

        //血脂异常-低密度脂蛋白胆固醇(mmol/L)
        val DYSLIPIDEMIA_LDL_LIMIT_4_1: BigDecimal = BigDecimal.valueOf(4.1)

        //血脂异常-甘油三酯(mmol/L)
        val DYSLIPIDEMIA_TG_LIMIT_2_3: BigDecimal = BigDecimal.valueOf(2.3)

        // 低密度脂蛋白胆固醇(mmol/L)
        val LDL_LIMIT_MIN_1_9: BigDecimal = BigDecimal.valueOf(1.9)

        // 低密度脂蛋白胆固醇(mmol/L)
        val LDL_LIMIT_MAX_4_9: BigDecimal = BigDecimal.valueOf(4.9)

        //标签级别
        const val TAG_LEVEL_LOW = "低危"
        const val TAG_LEVEL_CENTRE = "中危"
        const val TAG_LEVEL_HIGH = "高危"
        const val TAG_LEVEL_VERY_HIGH = "极高危"
        const val TAG_LEVEL_NOT_HIGH = "非高危"

        //条件满足的数量
        const val CONDITION_NUM_LIMIT = 2

        //年龄
        const val AGE_MIN = 35
        const val AGE_MAX = 54
        const val AGE_MAN = 40
        const val AGE_WOMAN = 50
        const val AGE_LIMIT = 40

        //您是否患有冠心病、缺血性卒中或其他外周动脉疾病病史选项编码
        const val OPTION_CODE_DISEASE = "EXPERT_ASCVD_20000101"

        //高密度脂蛋白胆固醇选项编码
        const val OPTION_CODE_HDL = "EXPERT_ASCVD_20000201"

        //低密度脂蛋白胆固醇选项编码
        const val OPTION_CODE_LDL = "EXPERT_ASCVD_20000202"

        //总胆固醇选项编码
        const val OPTION_CODE_TC = "EXPERT_ASCVD_20000203"

        //甘油三酯
        const val OPTION_CODE_TG = "EXPERT_ASCVD_20000204"

        //收缩压选项编码
        const val OPTION_CODE_SBP = "EXPERT_ASCVD_20000301"

        //舒张压选项编码
        const val OPTION_CODE_DBP = "EXPERT_ASCVD_20000302"

        //身高选项编码
        const val OPTION_CODE_HEIGHT = "EXPERT_ASCVD_20000401"

        //体重选项编码
        const val OPTION_CODE_WEIGHT = "EXPERT_ASCVD_20000402"

        //吸烟选项编码
        const val OPTION_CODE_IS_SMOKING = "EXPERT_ASCVD_20000501"

        //结果解读消息
        const val MSG_COMMENT =
            "血脂异常是动脉粥样硬化性心血管疾病（ASCVD）的核心危险因素，其中以低密度脂蛋白胆固醇（LDL-C）升高尤为关键，大量临床研究反复证实降低LDL-C水平可显著减少动脉粥样硬化性心血管疾病的发病及死亡风险。针对不同危险分层的人群，低密度脂蛋白胆固醇（LDL-C）的控制标准也不同。\n"

        //ASCVD发病危险：极高危
        const val MSG_TAG_FALL_ILL_VERY_HIGH =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜1.8 mmol/L（70 mg/dl） 范围内，并且每3~6个月测定1次血脂。\n"

        //ASCVD发病危险：高危  / ASCVD10年发病危险：高危
        const val MSG_TAG_FALL_ILL_OR_TEN_YEAR_FALL_ILL_HIGH =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜2.6 mmol/L（100 mg/dl）范围内，并且每3~6个月测定1次血脂。\n"

        //ASCVD 10年发病危险：中危
        const val MSG_TAG_TEN_YEAR_FALL_ILL_CENTER =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜3.4mmol/L（130 mg/dl）范围内，并且每年测定1次血脂。\n"

        //ASCVD10年发病危险：低危 / ASCVD10年发病危险：中危 + 余生危险：非高危
        //是否为40岁以上男性或50岁以上女性
        const val MSG_TAG_TEN_YEAR_FALL_ILL_LOW_OR_FALL_ILL_CENTER_AND_FUTURE_NOT_HIGH_YES =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜3.4mmol/L（130 mg/dl）范围内，并且每年检测1次血脂。\n"
        const val MSG_TAG_TEN_YEAR_FALL_ILL_LOW_OR_FALL_ILL_CENTER_AND_FUTURE_NOT_HIGH_NO =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜3.4mmol/L（130 mg/dl）范围内。\n"

        //ASCVD10年发病危险：中危 + 余生危险：高危
        const val MSG_TAG_TEN_YEAR_FALL_ILL_CENTER_AND_FUTURE_HIGH =
            "建议您将低密度脂蛋白胆固醇（LDL-C）控制在＜3.4mmol/L（130 mg/dl）范围内，并且戒烟限酒，将血压控制在合理范围内，BMI控制在18.5～24kg/m2，每半年到一年测定1次血脂。\n"

        //糖尿病患者
        const val MSG_DIABETES =
            "糖尿病可引起血脂异常，同时也可直接促进动脉粥样硬化性心血管疾病的发生。建议40岁及以上糖尿病患者血清的低密度脂蛋白胆固醇（LDL-C）水平控制在2.6 mmol/L（100 mg/dl）以下，高密度脂蛋白胆固醇（HDL-C）保持在1 mmol/L（38.6 mg/dl） 以上。\n"

        //高血压患者
        const val MSG_HYPERTENSION =
            "糖尿病可引起血脂异常，同时也可直接促进动脉粥样硬化性心血管疾病的发生。建议40岁及以上糖尿病患者血清的低密度脂蛋白胆固醇（LDL-C）水平控制在2.6 mmol/L（100 mg/dl）以下，高密度脂蛋白胆固醇（HDL-C）保持在1 mmol/L（38.6 mg/dl） 以上。\n"

        //吸烟患者
        const val MSG_SMOKING =
            "吸烟可使高密度脂蛋白胆固醇下降，引起血脂异常，同时吸烟也是动脉粥样硬化性心血管疾病的危险因素，增加动脉粥样硬化性心血管疾病的发病危险，因此建议您尽早戒烟，如有需要可在医生指导下进行戒烟。\n"

        //血脂异常患者
        const val MSG_DYSLIPIDEMIA =
            "血脂异常主要就是增加动脉粥样硬化性心血管疾病的发病危险，它可明显受到饮食及生活方式的影响，因此无论是否进行药物调脂治疗，建议您都务必坚持控制饮食和改善生活方式。饮食方面建议选择使用富含膳食纤维和低升糖指数的碳水化合物替代饱和脂肪酸，限制胆固醇的摄入，每日饮食应包含25~40g膳食纤维，每日摄入碳水化合物占总能量的50~65%并且以谷类、薯类和全谷物为主。同时建议您日常生活中保持中等强度锻炼，每天至少消耗200 kcal热量，远离烟草和保持理想体重（即BMI控制在18.5~23.9 kg/m²）。\n"
    }

    override fun getInterpret(interpretMatter: InterpretMatter): InterpretResult {
        //判断是否满足极高危情况，满足直接返回
        if (interpretMatter.optionList.stream().anyMatch { it.optionCode == OPTION_CODE_DISEASE }) {
            return InterpretResult("ASCVD发病危险:$TAG_LEVEL_VERY_HIGH ", MSG_COMMENT + MSG_TAG_FALL_ILL_VERY_HIGH)
        }


        val patientInfo = patientInfoRpcService.getPatientInfo(AppSecurityUtil.currentUserIdWithDefault())

        val optionValueMap = interpretMatter.optionList.associate { it.optionCode to it.optionValue }

        val tcValue = getOptionValue(OPTION_CODE_TC, optionValueMap)
        val hdlValue = getOptionValue(OPTION_CODE_HDL, optionValueMap)
        val ldlValue = getOptionValue(OPTION_CODE_LDL, optionValueMap)
        val tgValue = getOptionValue(OPTION_CODE_TG, optionValueMap)

        val sbpValue = getOptionValue(OPTION_CODE_SBP, optionValueMap)
        val dbpValue = getOptionValue(OPTION_CODE_DBP, optionValueMap)

        val height = getOptionValue(OPTION_CODE_HEIGHT, optionValueMap)
        val weight = getOptionValue(OPTION_CODE_WEIGHT, optionValueMap)
        val isSmoking = optionValueMap.containsKey(OPTION_CODE_IS_SMOKING)

        val bmiValue = getBmiValue(height, weight)

        val isDiabetes = patientInfo.diabetesDiseaseTag?.let { it == PatientTag.EXISTS } ?: false
        val isHypertension = patientInfo.hypertensionDiseaseTag?.let { it == PatientTag.EXISTS } ?: false
        val age = patientInfo.age
        val gender = Gender.valueOf(patientInfo.gender.value)

        //ASVCD发病危险
        val fallIllTag = getFallIllTag(age, isDiabetes, ldlValue)
        //高危直接返回
        if (fallIllTag != null && fallIllTag == TAG_LEVEL_HIGH) {
            return InterpretResult(
                "ASCVD发病危险:$TAG_LEVEL_HIGH ",
                MSG_COMMENT + MSG_TAG_FALL_ILL_OR_TEN_YEAR_FALL_ILL_HIGH
            )
        }
        //P值
        val pValue = getPValue(gender, age, isSmoking, isDiabetes, sbpValue, hdlValue, tcValue, optionValueMap)
        //动脉粥样硬化性心血管病10年发病危险
        val tenYearFallIllTag = getTenYearFallIllTag(pValue)
        //余生危险
        val futureTag = getFutureTag(pValue, age, bmiValue, sbpValue, dbpValue, tcValue, hdlValue, isSmoking)
        //获取消息
        val msg = getResultMsg(
            age,
            gender,
            fallIllTag,
            tenYearFallIllTag,
            futureTag,
            isDiabetes,
            isHypertension,
            isSmoking,
            tcValue,
            hdlValue,
            ldlValue,
            tgValue
        )
        //标签
        val tagBuilder = StringBuilder("动脉粥样硬化性心血管病10年发病危险:$tenYearFallIllTag")
        if (futureTag != null) {
            tagBuilder.append("  余生危险:$futureTag")
        }
        return InterpretResult(tagBuilder.toString(), msg)
    }


    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.strategyCode == "EXPERT_ASCVD"
    }

    override fun getOrder(): Int {
        return 5
    }

    /**
     * @description: 获取p值：当第2题填写了“低密度脂蛋白胆固醇”采用公式一，否则采用公式二
     * @param：性别
     * @param：年龄
     * @param：是否吸烟
     * @param：是否是糖尿病患者
     * @param：收缩压（mmHg）
     * @param：ldlValue（低密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
     * @param：hdlValue（高密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
     * @param：tcValue（总胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
     * @return P值
     */
    private fun getPValue(
        gender: Gender,
        age: Int,
        isSmoking: Boolean,
        isDiabetes: Boolean,
        sbpValue: BigDecimal,
        hdlValue: BigDecimal,
        tcValue: BigDecimal,
        optionValueMap: Map<String?, String>
    ): BigDecimal {
        val ldlValue = optionValueMap[OPTION_CODE_LDL]
        return if (isNumeric(ldlValue)) {
            val indexXB: BigDecimal
            when (gender) {
                Gender.WOMAN -> {//公式一女性
                    indexXB =
                        getFormulaOneWomanIndexXB(
                            age,
                            isSmoking,
                            isDiabetes,
                            sbpValue,
                            hdlValue,
                            ldlValue?.toBigDecimal() ?: BigDecimal.ZERO
                        )
                    getP(FORMULA_ONE_WOMEN_P_S10_0_984, indexXB, FORMULA_ONE_WOMEN_P_MEAN_XB_191_923)
                }
                Gender.MAN -> {//公式一男性
                    indexXB =
                        getFormulaOneManIndexXB(
                            age,
                            isSmoking,
                            isDiabetes,
                            sbpValue,
                            hdlValue,
                            ldlValue?.toBigDecimal() ?: BigDecimal.ZERO
                        )
                    getP(FORMULA_ONE_MAN_P_S10_0_971, indexXB, FORMULA_ONE_MAN_P_MEAN_XB_161_703)
                }
            }
        } else {
            val indexXB: BigDecimal
            when (gender) {
                Gender.WOMAN -> {//公式二女性
                    indexXB = getFormulaTwoWomanIndexXB(age, isSmoking, isDiabetes, sbpValue, hdlValue, tcValue)
                    getP(FORMULA_TWO_WOMEN_P_S10_0_984, indexXB, FORMULA_TWO_WOMEN_P_MEAN_XB_192_086)
                }
                Gender.MAN -> {//公式二男性
                    indexXB = getFormulaTwoManIndexXB(age, isSmoking, isDiabetes, sbpValue, hdlValue, tcValue)
                    getP(FORMULA_TWO_MAN_P_S10_0_971, indexXB, FORMULA_TWO_MAN_P_MEAN_XB_191_613)
                }
            }
        }
    }

    /**
     * 获取发病风险标签
     * 满足任一条件为高危
     * （1）年龄＞40岁且有糖尿病患者标签且1.8 mmol/L≤低密度脂蛋白胆固醇（LDL-C）＜4.9 mmol/L
     * （2）低密度脂蛋白胆固醇（LDL-C）≥4.9 mmol/L。
     * @param age 年龄
     * @param isDiabetes 是否糖尿病
     * @param ldlValue 低密度脂蛋白胆固醇（mmol/L）
     * @return 选项的值，没有返回0
     */
    private fun getFallIllTag(
        age: Int,
        isDiabetes: Boolean,
        ldlValue: BigDecimal,
    ): String? {
        if ((age > AGE_LIMIT && isDiabetes && ldlValue >= LDL_LIMIT_MIN_1_9 && ldlValue < LDL_LIMIT_MAX_4_9) ||
            (ldlValue >= LDL_LIMIT_MAX_4_9)
        ) {
            return TAG_LEVEL_HIGH
        }
        return null
    }

    /**
     * 获取10年发病风险标签
     * @param pValue P值
     * @return 选项的值，没有返回0
     */
    private fun getTenYearFallIllTag(pValue: BigDecimal): String {
        var fallIllTag = TAG_LEVEL_LOW

        if (pValue >= P_VALUE_5 && pValue < P_VALUE_10) {
            fallIllTag = TAG_LEVEL_CENTRE
        } else if (pValue >= P_VALUE_10) {
            fallIllTag = TAG_LEVEL_HIGH
        }
        return fallIllTag
    }

    /**
     * 获取余生风险标签
     * @param pValue P值
     * @param age 年龄
     * @param bmiValue BMI (kg/m2)
     * @param sbpValue 收缩压 (mmHg)
     * @param dbpValue 舒张压 (mmHg)
     * @param tcValue 总胆固醇（mmol/L）
     * @param hdlValue 高密度脂蛋白胆固醇（mmol/L）
     * @param isSmoking 是否吸烟
     */
    private fun getFutureTag(
        pValue: BigDecimal,
        age: Int,
        bmiValue: BigDecimal,
        sbpValue: BigDecimal,
        dbpValue: BigDecimal,
        tcValue: BigDecimal,
        hdlValue: BigDecimal,
        isSmoking: Boolean
    ): String? {
        var futureTag: String? = null

        if (pValue >= P_VALUE_5 && pValue < P_VALUE_10 && age >= AGE_MIN && age <= AGE_MAX) {
            var i = 0
            //1.收缩压 ≥ 160mmHg 或 舒张压 ≥ 100mmHg
            if (sbpValue >= SBP_LIMIT_160 || dbpValue >= DBP_LIMIT_100) {
                i++
            }
            //2.体重指数 ≥ 28 kg/m2
            if (bmiValue >= BMI_LIMIT_25) {
                i++
            }
            //3.高密度脂蛋白胆固醇（HDL-C）＜ 1mool/L (38.6 mg/dl)
            if (hdlValue < HDL_LIMIT_1) {
                i++
            }

            //总胆固醇
            //4.非-HDL-C ≥ 5.2mool/L(200.72mg/dl)
            if ((tcValue - hdlValue) >= NO_HDL_LIMIT_5_2) {
                i++
            }

            //5.吸烟
            if (isSmoking) {
                i++
            }

            futureTag = if (i >= CONDITION_NUM_LIMIT) TAG_LEVEL_HIGH else TAG_LEVEL_NOT_HIGH

        }

        return futureTag
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
     * @param age  年龄
     * @param gender  性别
     * @param fallIllTag  发病风险标签
     * @param tenYearFallIllTag 10年发病风险标签
     * @param futureTag 余生风险标签
     * @param isDiabetes 是否糖尿病患者
     * @param isHypertension 是否高血压患者
     * @param isSmoking 是否吸烟患者
     * @param tcValue 总胆固醇（mmol/L）
     * @param hdlValue 高密度脂蛋白胆固醇（mmol/L）
     * @param ldlValue 低密度脂蛋白胆固醇（mmol/L）
     * @param tgValue 甘油三脂（mmol/L）
     */
    private fun getResultMsg(
        age: Int,
        gender: Gender,
        fallIllTag: String?,
        tenYearFallIllTag: String?,
        futureTag: String?,
        isDiabetes: Boolean,
        isHypertension: Boolean,
        isSmoking: Boolean,
        tcValue: BigDecimal,
        hdlValue: BigDecimal,
        ldlValue: BigDecimal,
        tgValue: BigDecimal
    ): String {
        val builder = StringBuilder(MSG_COMMENT)


        //ASCVD发病危险：高危  / ASCVD10年发病危险：高危
        if (fallIllTag == TAG_LEVEL_HIGH || tenYearFallIllTag == TAG_LEVEL_HIGH) {
            builder.append(MSG_TAG_FALL_ILL_OR_TEN_YEAR_FALL_ILL_HIGH)
        }

        //ASCVD 10年发病危险：中危（≥55岁）
        if (tenYearFallIllTag == TAG_LEVEL_CENTRE) {
            builder.append(MSG_TAG_TEN_YEAR_FALL_ILL_CENTER)
        }

        //ASCVD10年发病危险：低危 / ASCVD10年发病危险：中危 + 余生危险：非高危
        if (tenYearFallIllTag == TAG_LEVEL_LOW ||
            (tenYearFallIllTag == TAG_LEVEL_CENTRE && futureTag == TAG_LEVEL_NOT_HIGH)
        ) {
            if ((gender == Gender.MAN && age > AGE_MAN) || (gender == Gender.WOMAN && age < AGE_WOMAN)) {
                builder.append(MSG_TAG_TEN_YEAR_FALL_ILL_LOW_OR_FALL_ILL_CENTER_AND_FUTURE_NOT_HIGH_YES)
            } else {
                builder.append(MSG_TAG_TEN_YEAR_FALL_ILL_LOW_OR_FALL_ILL_CENTER_AND_FUTURE_NOT_HIGH_NO)
            }
        }

        //ASCVD10年发病危险：中危 + 余生危险：高危
        if (tenYearFallIllTag == TAG_LEVEL_CENTRE && futureTag == TAG_LEVEL_HIGH) {
            builder.append(MSG_TAG_TEN_YEAR_FALL_ILL_CENTER_AND_FUTURE_HIGH)
        }

        //糖尿病患者
        if (isDiabetes) {
            builder.append(MSG_DIABETES)
        }

        //高血压患者：
        if (isHypertension) {
            builder.append(MSG_HYPERTENSION)
        }

        //吸烟患者
        if (isSmoking) {
            builder.append(MSG_SMOKING)
        }

        //血脂异常
        val isDyslipidemia = isDyslipidemia(tcValue, hdlValue, ldlValue, tgValue)
        if (isDyslipidemia) {
            builder.append(MSG_DYSLIPIDEMIA)
        }
        return builder.toString()
    }

    /**
     * 判断是否血脂异常
     * 血脂异常的判断逻辑,满足任一即异常：
     *    总胆固醇≥6.2mmol/L（或240mg/dl）
     *    甘油三酯≥2.3mmol/L（或200mg/dl）
     *    低密度脂蛋白胆固醇≥4.1mmol/L（或160mg/dl）
     *    高密度脂蛋白胆固醇＜1.0mmol/L（或40mg/dl）
     * @param tcValue 总胆固醇（mmol/L）
     * @param hdlValue 高密度脂蛋白胆固醇（mmol/L）
     * @param ldlValue 低密度脂蛋白胆固醇（mmol/L）
     * @param tgValue 甘油三脂（mmol/L）
     */
    private fun isDyslipidemia(
        tcValue: BigDecimal,
        hdlValue: BigDecimal,
        ldlValue: BigDecimal,
        tgValue: BigDecimal
    ): Boolean {
        if (tcValue >= DYSLIPIDEMIA_TC_LIMIT_6_2 || tgValue >= DYSLIPIDEMIA_TG_LIMIT_2_3 || ldlValue >= DYSLIPIDEMIA_LDL_LIMIT_4_1 || hdlValue < DYSLIPIDEMIA_HDL_LIMIT_1) {
            return true
        }
        return false
    }


}
