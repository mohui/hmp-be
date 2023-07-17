package com.bjknrt.health.indicator.utils

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