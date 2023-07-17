package com.bjknrt.health.scheme.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 血压值对象
 */
data class BloodPressureValue(
    val systolicBloodPressure: BigDecimal,
    val diastolicBloodPressure: BigDecimal
)

//年龄
const val AGE_65 = 65

//收缩压上限，大于等于65岁
const val SBP_MAX_150 = 150

//收缩压上限，小于65岁
const val SBP_MAX_140 = 140

//收缩压下限
const val SBP_MIN_90 = 90

//舒张压下限
const val DBP_MIN_60 = 60

//舒张压上限
const val DBP_MAX_90 = 90


/**
 * 获取血压达标的次数
 * 大于等于65岁： 收缩压大于等于90且小于150并且舒张压大于等于60且小于90算达标
 * 小于65岁：收缩压大于等于90且小于140并且舒张压大于等于60且小于90算达标
 *
 * @param age 年龄
 * @param bloodPressureList 血压集合
 * @return  达标次数
 */
fun getBloodPressureStandardNumber(
    age: Int,
    bloodPressureList: List<BloodPressureValue>,
): Int {
    var num = 0
    for (bloodPressure in bloodPressureList) {
        val dbpValue = bloodPressure.diastolicBloodPressure.toDouble()
        val sbpValue = bloodPressure.systolicBloodPressure.toDouble()

        if (isBloodPressureStandard(age, sbpValue, dbpValue)) {
            num++
        }
    }
    return num
}

/**
 * 获取血压达标的次数
 * 收缩压大于等于90且小于140并且舒张压大于等于60且小于90算达标
 *
 * @param bloodPressureList 血压集合
 * @return  达标次数
 */
fun getBloodPressureStandardNumber(
    bloodPressureList: List<BloodPressureValue>,
): Int {
    var num = 0
    for (bloodPressure in bloodPressureList) {
        val dbpValue = bloodPressure.diastolicBloodPressure.toDouble()
        val sbpValue = bloodPressure.systolicBloodPressure.toDouble()

        if (isBloodPressureStandard(sbpValue, dbpValue)) {
            num++
        }
    }
    return num
}


/**
 * 获取血压平均值是否达标
 * 大于等于65岁： 收缩压大于90且小于150并且舒张压大于60且小于90算达标
 * 小于65岁：收缩压大于90且小于140并且舒张压大于60且小于90算达标
 *
 * @param age 年龄
 * @param bloodPressureList 血压集合
 * @return  达标次数
 */
fun getBloodPressureAvgStandard(age: Int, bloodPressureList: List<BloodPressureValue>): Boolean {
    val sbpAvgValue = getAvg(bloodPressureList.map { it.systolicBloodPressure }).toDouble()
    val dbpAvgValue = getAvg(bloodPressureList.map { it.diastolicBloodPressure }).toDouble()
    return isBloodPressureStandard(age, sbpAvgValue, dbpAvgValue)
}

/**
 * 获取血压平均值是否达标
 * 收缩压大于90且小于140并且舒张压大于60且小于90算达标
 *
 * @param bloodPressureList 血压集合
 * @return  达标次数
 */
fun getBloodPressureAvgStandard(bloodPressureList: List<BloodPressureValue>): Boolean {
    val sbpAvgValue = getAvg(bloodPressureList.map { it.systolicBloodPressure }).toDouble()
    val dbpAvgValue = getAvg(bloodPressureList.map { it.diastolicBloodPressure }).toDouble()
    return isBloodPressureStandard(sbpAvgValue, dbpAvgValue)
}


/**
 * 计算平均值
 * @param numberList 数组
 */
fun getAvg(numberList: List<BigDecimal>): BigDecimal {
    if (numberList.isEmpty()) {
        return BigDecimal.ZERO
    }
    return numberList.fold(BigDecimal.ZERO) { acc, element -> acc + element }
        .divide(BigDecimal(numberList.size), RoundingMode.HALF_UP)
}


/**
 * 判断是否达标
 * 大于等于65岁： 收缩压大于等于90且小于150并且舒张压大于等于60且小于90算达标
 * 小于65岁：收缩压大于等于90且小于140并且舒张压大于等于60且小于90算达标
 * @param age 年龄
 * @param sbpValue 收缩压
 * @param dbpValue 舒张压
 */
fun isBloodPressureStandard(age: Int, sbpValue: Double, dbpValue: Double): Boolean {
    return (age >= AGE_65 && sbpValue >= SBP_MIN_90 && sbpValue < SBP_MAX_150 && dbpValue >= DBP_MIN_60 && dbpValue < DBP_MAX_90)
            ||
            (age < AGE_65 && sbpValue >= SBP_MIN_90 && sbpValue < SBP_MAX_140 && dbpValue >= DBP_MIN_60 && dbpValue < DBP_MAX_90)
}


/**
 * 判断是否达标
 * 收缩压大于等于90且小于140并且舒张压大于等于60且小于90算达标
 * @param sbpValue 收缩压
 * @param dbpValue 舒张压
 */
fun isBloodPressureStandard(sbpValue: Double, dbpValue: Double): Boolean {
    return sbpValue >= SBP_MIN_90 && sbpValue < SBP_MAX_140 && dbpValue >= DBP_MIN_60 && dbpValue < DBP_MAX_90
}

/**
 * 超收缩压上限的次数（ 年龄＜65岁者,收缩压≥140mmHg的次数；年龄≥65岁者，收缩压≥150mmHg的次数 ）
 * @param age 年龄
 * @param bloodPressureList 血压集合
 * @return  次数
 */
fun getSystolicBloodPressureUpperLimitNum(age: Int, bloodPressureList: List<BloodPressureValue>): Int {
    if (age >= AGE_65) {
        return bloodPressureList.filter { it.systolicBloodPressure.toDouble() >= SBP_MAX_150 }.size
    }
    return bloodPressureList.filter { it.systolicBloodPressure.toDouble() >= SBP_MAX_140 }.size
}

/**
 * 超收缩压上限的次数 收缩压≥140mmHg的次数
 * @param bloodPressureList 血压集合
 * @return  次数
 */
fun getSystolicBloodPressureUpperLimitNum(bloodPressureList: List<BloodPressureValue>): Int {
    return bloodPressureList.filter { it.systolicBloodPressure.toDouble() >= SBP_MAX_140 }.size
}

/**
 * 超舒张压下限的次数（ 舒张压≥90mmHg ）
 * @param bloodPressureList 血压集合
 * @return  次数
 */
fun getDiastolicBloodPressureLowerLimitNum(bloodPressureList: List<BloodPressureValue>): Int {
    return bloodPressureList.filter { it.diastolicBloodPressure.toDouble() >= DBP_MAX_90 }.size
}

/**
 * 低血压次数（ 收缩压＜90mmHg 或 舒张压＜60mmHg ）
 * @param bloodPressureList 血压集合
 * @return  次数
 */
fun getLowBloodPressureNum(bloodPressureList: List<BloodPressureValue>): Int {
    return bloodPressureList.filter {
        it.systolicBloodPressure.toDouble() < SBP_MIN_90 || it.diastolicBloodPressure.toDouble() < DBP_MIN_60
    }.size
}


/**
 * 获取血压标准差
 * @param bloodPressureList 血压集合
 * @return 血压标准差
 */
fun getBloodPressureStandardDeviation(
    bloodPressureList: List<BigDecimal>,
): BigDecimal {
    // 个数
    val size = bloodPressureList.size
    // 求和
    val sum = bloodPressureList.fold(BigDecimal.ZERO) { acc, element -> acc + element }
    // 平均值
    val avg = sum.divide(BigDecimal(size), RoundingMode.HALF_UP)
    // 方差
    val variance =
        bloodPressureList.map { it.subtract(avg).pow(2) }.fold(BigDecimal.ZERO) { acc, element -> acc + element }
    // 标准差
    val standardDeviation = kotlin.math.sqrt(variance.toDouble() / size)

    return BigDecimal.valueOf(standardDeviation)
}
