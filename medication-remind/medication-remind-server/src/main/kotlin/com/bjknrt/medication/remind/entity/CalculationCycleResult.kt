package com.bjknrt.medication.remind.entity

data class CalculationCycleResult(
    val start: java.time.LocalDateTime,
    val end: java.time.LocalDateTime,
    val between: kotlin.Long
) {

}