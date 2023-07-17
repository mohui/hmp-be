package com.bjknrt.question.answering.system.util

import com.bjknrt.doctor.patient.management.vo.Gender
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 获取BMI的值
 * @param height 身高 （cm）
 * @param weight 体重 （kg）
 */
fun getBmiValue(height: BigDecimal?, weight: BigDecimal?): BigDecimal {
    if (height == null || weight == null) {
        return BigDecimal.ZERO
    }
    val heightSquare = (height.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).pow(2))
    return weight.divide(heightSquare, 3, RoundingMode.HALF_EVEN)
}

/**
 * 血脂数据单位换算(1mmol/L = 38.6mg/dl)
 * @param dlValue 血脂指标值（mmol/L）
 * @return 换算后的血脂指标值（mg/dl）
 */
fun convertDlValue(dlValue: BigDecimal?): BigDecimal {
    if (dlValue == null) {
        return BigDecimal.ZERO
    }
    return dlValue.multiply(BigDecimal.valueOf(38.6))
}

/**
 * @description: 判断字符串是否为合法的数字，包括正负小数和整数
 * @param: 字符串
 * @return： 字符串是否是数字
 */
fun isNumeric(str: String?): Boolean {
    if (str == null)
        return false
    return str.matches(Regex("(^[+-]?[0-9]+)|(^[+-]?[0-9]+\\.[0-9]+)"))
}

fun waistlineIsStandard(gender: Gender, waistline: Double?): Boolean {
    return (waistline != null
            && ((gender == Gender.MAN && waistline < 90)
            || (gender == Gender.WOMAN && waistline < 85)))
}