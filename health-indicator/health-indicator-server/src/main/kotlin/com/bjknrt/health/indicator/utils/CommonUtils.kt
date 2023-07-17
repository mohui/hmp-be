package com.bjknrt.health.indicator.utils

import java.math.BigDecimal

/**
 * 当args可变长参数中至少有一个不为空时，才回调block
 */
fun leastOneNotNull(vararg args: Any?, block: () -> Unit) =
    args.takeIf { it.filterNotNull().isNotEmpty() }?.let { block() }

/**
 * 当args可变长参数中有且仅有一个有效值时，才回调block
 */
fun onlyOneNotNull(vararg args: Any?, block: () -> Unit) =
    args.takeIf { it.filterNotNull().size == 1 }?.let { block() }


/**
 * 判断args可变长参数中所有有效指标元素，是否都满足某个范围（左开右开）
 */
fun isAllIndicatorValueCheckPassLeftOpenRightOpen(
    leftValue: BigDecimal? = null,
    rightValue: BigDecimal? = null,
    vararg args: BigDecimal?
): Boolean {
    return args.filterNotNull()
        .takeIf { it.isNotEmpty() }
        ?.all { indicatorValue -> leftValue?.let { indicatorValue > it } ?: true && rightValue?.let { indicatorValue < it } ?: true }
        ?: false
}

/**
 * 判断args可变长参数中所有有效指标元素，是否都满足某个范围（左开右闭）
 */
fun isAllIndicatorValueCheckPassLeftOpenRightClose(
    leftValue: BigDecimal?,
    rightValue: BigDecimal?,
    vararg args: BigDecimal?
): Boolean {
    return args.filterNotNull()
        .takeIf { it.isNotEmpty() }
        ?.all { indicatorValue -> leftValue?.let { indicatorValue > it } ?: true && rightValue?.let { indicatorValue <= it } ?: true }
        ?: false
}