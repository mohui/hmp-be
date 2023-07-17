package com.bjknrt.health.scheme.entity

import java.math.BigDecimal
import java.time.LocalDateTime

data class BloodPressure(
    val systolicBloodPressure: BigDecimal,
    val diastolicBloodPressure: BigDecimal,
    val measureDatetime: LocalDateTime
)
