package com.bjknrt.health.scheme.util

import java.math.BigDecimal
import java.math.RoundingMode

val ONE_HUNDRED = BigDecimal(100)

/**
 * 计算百分比 (整数)
 * @param number 分子
 * @param total  分母
 */
fun getRate(number: BigDecimal, total: BigDecimal): Int {
    if (total == BigDecimal.ZERO) {
        return 0
    }
    return number
        .multiply(ONE_HUNDRED)
        .divide(total, 0, RoundingMode.HALF_UP)
        .toInt()
}

/**
 * 计算百分比（小数）
 * @param number 分子
 * @param total  分母
 * @param scale  百分比保留的小数位数，默认2位
 */
fun getRate(number: Int, total: Int, scale: Int = 2): BigDecimal {
    if (total == 0) {
        return BigDecimal.ZERO
    }
    return BigDecimal(number)
        .multiply(ONE_HUNDRED)
        .divide(BigDecimal(total), scale, RoundingMode.HALF_UP)
}


/**
 * 格式化百分比
 * 大于100% 使用 100%
 * 小于0% 使用 0%
 */
fun formatRate(rate: Int): Int {
    return formatRate(BigDecimal(rate)).toInt()
}

/**
 * 格式化百分比
 * 大于100% 使用 100%
 * 小于0% 使用 0%
 */
fun formatRate(rate: BigDecimal): BigDecimal {
    if (rate > ONE_HUNDRED) {
        return ONE_HUNDRED.setScale(rate.scale())
    }
    if (rate < BigDecimal.ZERO) {
        return BigDecimal.ZERO.setScale(rate.scale())
    }
    return rate
}

