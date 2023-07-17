package com.bjknrt.health.scheme.entity

import java.math.BigInteger

/**
 * 计划与频率的关系
 */
data class PlanFrequencyValue(
    val planId: BigInteger,
    val planType: String,
    val frequencyId: BigInteger?
)
