package com.bjknrt.question.answering.system.util

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.pow

//被减数1
val FORMULA_COMMON_MINUEND: BigDecimal = BigDecimal.valueOf(1)

//公式一中的常量
//女性
const val FORMULA_ONE_WOMEN_P_S10_0_984 = 0.984

val FORMULA_ONE_WOMEN_P_MEAN_XB_191_923: BigDecimal = BigDecimal.valueOf(191.923)

val FORMULA_ONE_WOMEN_AGE_RELATION_46_143: BigDecimal = BigDecimal.valueOf(46.143)

val FORMULA_ONE_WOMEN_LDL_RELATION_0_127: BigDecimal = BigDecimal.valueOf(0.127)

val FORMULA_ONE_WOMEN_HDL_RELATION_0_531: BigDecimal = BigDecimal.valueOf(0.531)

val FORMULA_ONE_WOMEN_SDP_RELATION_37_045: BigDecimal = BigDecimal.valueOf(37.045)

val FORMULA_ONE_WOMEN_SMOKE_RELATION_0_091: BigDecimal = BigDecimal.valueOf(0.091)

val FORMULA_ONE_WOMEN_DIABETES_RELATION_0_518: BigDecimal = BigDecimal.valueOf(0.518)

val FORMULA_ONE_WOMEN_AGE_MULTI_SDP_RELATION_8_757: BigDecimal = BigDecimal.valueOf(8.757)

//男性
const val FORMULA_ONE_MAN_P_S10_0_971 = 0.971

val FORMULA_ONE_MAN_P_MEAN_XB_161_703: BigDecimal = BigDecimal.valueOf(161.703)

val FORMULA_ONE_MAN_AGE_RELATION_36_216: BigDecimal = BigDecimal.valueOf(36.216)

val FORMULA_ONE_MAN_LDL_RELATION_7_959: BigDecimal = BigDecimal.valueOf(7.959)

val FORMULA_ONE_MAN_HDL_RELATION_0_265: BigDecimal = BigDecimal.valueOf(0.265)

val FORMULA_ONE_MAN_SDP_RELATION_22_768: BigDecimal = BigDecimal.valueOf(22.768)

val FORMULA_ONE_MAN_SMOKE_RELATION_0_480: BigDecimal = BigDecimal.valueOf(0.480)

val FORMULA_ONE_MAN_DIABETES_RELATION_0_236: BigDecimal = BigDecimal.valueOf(0.236)

val FORMULA_ONE_MAN_AGE_MULTI_SDP_RELATION_4_828: BigDecimal = BigDecimal.valueOf(4.828)

val FORMULA_ONE_MAN_AGE_MULTI_LDL_RELATION_1_903: BigDecimal = BigDecimal.valueOf(1.903)

//公式二中的常量
//女性
const val FORMULA_TWO_WOMEN_P_S10_0_984 = 0.984

val FORMULA_TWO_WOMEN_P_MEAN_XB_192_086: BigDecimal = BigDecimal.valueOf(192.086)

val FORMULA_TWO_WOMEN_AGE_RELATION_45_905: BigDecimal = BigDecimal.valueOf(45.905)

val FORMULA_TWO_WOMEN_TDL_RELATION_0_392: BigDecimal = BigDecimal.valueOf(0.392)

val FORMULA_TWO_WOMEN_HDL_RELATION_0_603: BigDecimal = BigDecimal.valueOf(0.603)

val FORMULA_TWO_WOMEN_SDP_RELATION_36_897: BigDecimal = BigDecimal.valueOf(36.897)

val FORMULA_TWO_WOMEN_SMOKE_RELATION_0_090: BigDecimal = BigDecimal.valueOf(0.090)

val FORMULA_TWO_WOMEN_DIABETES_RELATION_0_510: BigDecimal = BigDecimal.valueOf(0.510)

val FORMULA_TWO_WOMEN_AGE_MULTI_SDP_RELATION_8_723: BigDecimal = BigDecimal.valueOf(8.723)

//男性
const val FORMULA_TWO_MAN_P_S10_0_971 = 0.971

val FORMULA_TWO_MAN_P_MEAN_XB_191_613: BigDecimal = BigDecimal.valueOf(191.613)

val FORMULA_TWO_MAN_AGE_RELATION_43_321: BigDecimal = BigDecimal.valueOf(43.321)

val FORMULA_TWO_MAN_TDL_RELATION_13_022: BigDecimal = BigDecimal.valueOf(13.022)

val FORMULA_TWO_MAN_HDL_RELATION_0_411: BigDecimal = BigDecimal.valueOf(0.411)

val FORMULA_TWO_MAN_SDP_RELATION_22_684: BigDecimal = BigDecimal.valueOf(22.684)

val FORMULA_TWO_MAN_SMOKE_RELATION_0_474: BigDecimal = BigDecimal.valueOf(0.474)

val FORMULA_TWO_MAN_DIABETES_RELATION_0_223: BigDecimal = BigDecimal.valueOf(0.223)

val FORMULA_TWO_MAN_AGE_MULTI_SDP_RELATION_4_812: BigDecimal = BigDecimal.valueOf(4.812)

val FORMULA_TWO_MAN_AGE_MULTI_TDL_RELATION_3_075: BigDecimal = BigDecimal.valueOf(3.075)

/**
 * @description: 公式一的女性计算：获取的IndexXB
 * @param： age 年龄
 * @param： isSmoking 是否抽烟
 * @param： isDiabetes 是否患有糖尿病
 * @param： sbpValue 收缩压
 * @param：hdlValue（高密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @param：ldlValue（低密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @return: indexXB值
 */
fun getFormulaOneWomanIndexXB(
    age: Int,
    isSmoking: Boolean,
    isDiabetes: Boolean,
    sbpValue: BigDecimal,
    hdlValue: BigDecimal,
    ldlValue: BigDecimal
): BigDecimal {
    val isSmoke = if (isSmoking) BigDecimal.ONE else BigDecimal.ZERO
    val isDiabetesPatient = if (isDiabetes) BigDecimal.ONE else BigDecimal.ZERO
    val calAge: BigDecimal = FORMULA_ONE_WOMEN_AGE_RELATION_46_143.multiply(ln(age.toDouble()).toBigDecimal())
    val calLdlValue: BigDecimal =
        FORMULA_ONE_WOMEN_LDL_RELATION_0_127.multiply(ln(convertDlValue(ldlValue).toDouble()).toBigDecimal())
    val calHdlValue: BigDecimal =
        FORMULA_ONE_WOMEN_HDL_RELATION_0_531.multiply(ln(convertDlValue(hdlValue).toDouble()).toBigDecimal())
    val calSbpValue: BigDecimal = FORMULA_ONE_WOMEN_SDP_RELATION_37_045.multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calSmoke: BigDecimal = FORMULA_ONE_WOMEN_SMOKE_RELATION_0_091.multiply(isSmoke)
    val calDiabetes: BigDecimal = FORMULA_ONE_WOMEN_DIABETES_RELATION_0_518.multiply(isDiabetesPatient)
    val calAgeMultiSdp: BigDecimal =
        FORMULA_ONE_WOMEN_AGE_MULTI_SDP_RELATION_8_757.multiply(ln(age.toDouble()).toBigDecimal())
            .multiply(ln(sbpValue.toDouble()).toBigDecimal())
    return calAge.add(calLdlValue).subtract(calHdlValue).add(calSbpValue).add(calSmoke).add(calDiabetes)
        .subtract(calAgeMultiSdp)
}

/**
 * @description: 公式一的男性计算：获取的IndexXB
 * @param： age 年龄
 * @param： isSmoking 是否抽烟
 * @param： isDiabetes 是否患有糖尿病
 * @param： sbpValue 收缩压
 * @param：hdlValue（高密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @param：ldlValue（低密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @return: indexXB值
 */
fun getFormulaOneManIndexXB(
    age: Int,
    isSmoking: Boolean,
    isDiabetes: Boolean,
    sbpValue: BigDecimal,
    hdlValue: BigDecimal,
    ldlValue: BigDecimal
): BigDecimal {
    val isSmoke = if (isSmoking) BigDecimal.ONE else BigDecimal.ZERO
    val isDiabetesPatient = if (isDiabetes) BigDecimal.ONE else BigDecimal.ZERO
    val calAge: BigDecimal = FORMULA_ONE_MAN_AGE_RELATION_36_216.multiply(ln(age.toDouble()).toBigDecimal())
    val calLdlValue: BigDecimal =
        FORMULA_ONE_MAN_LDL_RELATION_7_959.multiply(ln(convertDlValue(ldlValue).toDouble()).toBigDecimal())
    val calHdlValue: BigDecimal =
        FORMULA_ONE_MAN_HDL_RELATION_0_265.multiply(ln(convertDlValue(hdlValue).toDouble()).toBigDecimal())
    val calSbpValue: BigDecimal = FORMULA_ONE_MAN_SDP_RELATION_22_768.multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calSmoke: BigDecimal = FORMULA_ONE_MAN_SMOKE_RELATION_0_480.multiply(isSmoke)
    val calDiabetes: BigDecimal = FORMULA_ONE_MAN_DIABETES_RELATION_0_236.multiply(isDiabetesPatient)
    val calAgeMultiSdp: BigDecimal =
        FORMULA_ONE_MAN_AGE_MULTI_SDP_RELATION_4_828.multiply(ln(age.toDouble()).toBigDecimal())
            .multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calAgeMultiLdl = FORMULA_ONE_MAN_AGE_MULTI_LDL_RELATION_1_903.multiply(ln(age.toDouble()).toBigDecimal())
        .multiply(ln(ldlValue.toDouble()).toBigDecimal())
    return calAge.add(calLdlValue).subtract(calHdlValue).add(calSbpValue).add(calSmoke).add(calDiabetes)
        .subtract(calAgeMultiSdp).subtract(calAgeMultiLdl)
}

/**
 * @description: 公式二的女性计算：获取的IndexXB
 * @param： age 年龄
 * @param： isSmoking 是否抽烟
 * @param： isDiabetes 是否患有糖尿病
 * @param： sbpValue 收缩压
 * @param：hdlValue（高密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @param：tcValue（总胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @return: indexXB值
 */
fun getFormulaTwoWomanIndexXB(
    age: Int,
    isSmoking: Boolean,
    isDiabetes: Boolean,
    sbpValue: BigDecimal,
    hdlValue: BigDecimal,
    tcValue: BigDecimal
): BigDecimal {
    val isSmoke = if (isSmoking) BigDecimal.ONE else BigDecimal.ZERO
    val isDiabetesPatient = if (isDiabetes) BigDecimal.ONE else BigDecimal.ZERO
    val calAge: BigDecimal = FORMULA_TWO_WOMEN_AGE_RELATION_45_905.multiply(ln(age.toDouble()).toBigDecimal())
    val calTdlValue: BigDecimal =
        FORMULA_TWO_WOMEN_TDL_RELATION_0_392.multiply(ln(convertDlValue(tcValue).toDouble()).toBigDecimal())
    val calHdlValue: BigDecimal =
        FORMULA_TWO_WOMEN_HDL_RELATION_0_603.multiply(ln(convertDlValue(hdlValue).toDouble()).toBigDecimal())
    val calSbpValue: BigDecimal = FORMULA_TWO_WOMEN_SDP_RELATION_36_897.multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calSmoke: BigDecimal = FORMULA_TWO_WOMEN_SMOKE_RELATION_0_090.multiply(isSmoke)
    val calDiabetes: BigDecimal = FORMULA_TWO_WOMEN_DIABETES_RELATION_0_510.multiply(isDiabetesPatient)
    val calAgeMultiSdp: BigDecimal =
        FORMULA_TWO_WOMEN_AGE_MULTI_SDP_RELATION_8_723.multiply(ln(age.toDouble()).toBigDecimal())
            .multiply(ln(sbpValue.toDouble()).toBigDecimal())
    return calAge.add(calTdlValue).subtract(calHdlValue).add(calSbpValue).add(calSmoke).add(calDiabetes)
        .subtract(calAgeMultiSdp)
}

/**
 * @description: 公式二男性计算：获取的IndexXB
 * @param： age 年龄
 * @param： isSmoking 是否抽烟
 * @param： isDiabetes 是否患有糖尿病
 * @param： sbpValue 收缩压
 * @param：hdlValue（高密度脂蛋白胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @param：tcValue（总胆固醇mmol/L）换算公式为：1 mmol/L=38.6 mg/dl
 * @return: indexXB值
 */
fun getFormulaTwoManIndexXB(
    age: Int,
    isSmoking: Boolean,
    isDiabetes: Boolean,
    sbpValue: BigDecimal,
    hdlValue: BigDecimal,
    tcValue: BigDecimal
): BigDecimal {

    val isSmoke = if (isSmoking) BigDecimal.ONE else BigDecimal.ZERO
    val isDiabetesPatient = if (isDiabetes) BigDecimal.ONE else BigDecimal.ZERO
    val calAge: BigDecimal = FORMULA_TWO_MAN_AGE_RELATION_43_321.multiply(ln(age.toDouble()).toBigDecimal())
    val calTdlValue: BigDecimal =
        FORMULA_TWO_MAN_TDL_RELATION_13_022.multiply(ln(convertDlValue(tcValue).toDouble()).toBigDecimal())
    val calHdlValue: BigDecimal =
        FORMULA_TWO_MAN_HDL_RELATION_0_411.multiply(ln(convertDlValue(hdlValue).toDouble()).toBigDecimal())
    val calSbpValue: BigDecimal = FORMULA_TWO_MAN_SDP_RELATION_22_684.multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calSmoke: BigDecimal = FORMULA_TWO_MAN_SMOKE_RELATION_0_474.multiply(isSmoke)
    val calDiabetes: BigDecimal = FORMULA_TWO_MAN_DIABETES_RELATION_0_223.multiply(isDiabetesPatient)
    val calAgeMultiSdp: BigDecimal =
        FORMULA_TWO_MAN_AGE_MULTI_SDP_RELATION_4_812.multiply(ln(age.toDouble()).toBigDecimal())
            .multiply(ln(sbpValue.toDouble()).toBigDecimal())
    val calAgeMultiTdl: BigDecimal =
        FORMULA_TWO_MAN_AGE_MULTI_TDL_RELATION_3_075.multiply(ln(age.toDouble()).toBigDecimal())
            .multiply(ln(convertDlValue(hdlValue).toDouble()).toBigDecimal())
    return calAge.add(calTdlValue).subtract(calHdlValue).add(calSbpValue).add(calSmoke).add(calDiabetes)
        .subtract(calAgeMultiSdp).subtract(calAgeMultiTdl)
}


/**
 * @description: 获取P值
 * @param： s10 常量
 * @param： indexXB
 * @param： meanXB 常量
 * @return: P值
 */
fun getP(s10: Double, indexXB: BigDecimal, meanXB: BigDecimal): BigDecimal {
    //indexXB-MeanXB
    val indexSubMean = indexXB.subtract(meanXB).toDouble()
    //e^(indexXB-MeanXB)
    val ePow: Double = Math.E.pow(indexSubMean)
    //s10^e^(indexXB-MeanXB))
    val s10Pow: BigDecimal = s10.pow(ePow).toBigDecimal()
    //1-s10^e^(indexXB-MeanXB))
    return FORMULA_COMMON_MINUEND.subtract(s10Pow, MathContext(3, RoundingMode.HALF_EVEN))
}