package com.bjknrt.health.scheme.util

import java.math.BigDecimal


//空腹血糖下限
val FBS_3_9: BigDecimal = BigDecimal(3.9)

//空腹血糖上限
val FBS_7: BigDecimal = BigDecimal(7)

//其他血糖下限
val BS_3_9: BigDecimal = BigDecimal(3.9)

//其他血糖上限
val BS_10: BigDecimal = BigDecimal(10)


/**
 * 判断空腹血糖是否达标
 * @param bloodSugar 空腹血糖值
 */
fun isFastingBloodSugarStandard(bloodSugar: BigDecimal): Boolean {
    return FBS_3_9 < bloodSugar && bloodSugar < FBS_7
}

/**
 * 判断血糖是否达标
 * @param bloodSugar 血糖值
 */
fun isBloodSugarStandard(bloodSugar: BigDecimal): Boolean {
    return BS_3_9 < bloodSugar && bloodSugar < BS_10
}
