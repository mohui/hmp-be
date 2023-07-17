package com.bjknrt.health.indicator.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @description: 计算总饮酒量 = 白酒 + 啤酒 + 红酒 + 黄酒
 * @param: 白酒量
 * @param: 啤酒量（啤酒量/10=白酒量）
 * @param: 红酒量（红酒量/4=白酒量）
 * @param: 黄酒量（黄酒量/5=白酒量）
 * @return: 总饮酒量
 */
fun getTotalDrink(
    knWhiteSpirit: BigDecimal,
    knBeer: BigDecimal,
    knWine: BigDecimal,
    knYellowRiceSpirit: BigDecimal
): Double {
    // 啤酒/10 = 白酒量
    val beerTransferWhiteSpirit = knBeer.divide(BigDecimal(10), 2, RoundingMode.HALF_EVEN)
    // 红酒/4 = 白酒量
    val wineTransferWhiteSpirit = knWine.divide(BigDecimal(4), 2, RoundingMode.HALF_EVEN)
    // 黄酒/5 = 白酒量
    val yellowTransferWhiteSpirit = knYellowRiceSpirit.divide(BigDecimal(5), 2, RoundingMode.HALF_EVEN)
    //计算总饮酒量
    return knWhiteSpirit.add(beerTransferWhiteSpirit).add(wineTransferWhiteSpirit).add(yellowTransferWhiteSpirit)
        .toDouble()
}