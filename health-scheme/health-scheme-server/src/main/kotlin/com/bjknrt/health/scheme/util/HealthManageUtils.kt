package com.bjknrt.health.scheme.util

import java.math.BigInteger

fun getFrequencyIds(frequencyIds: String): List<BigInteger> {
    return frequencyIds.split(",").toList().filter { it.isNotEmpty() }.map { BigInteger(it) }
}

fun frequencyIdsToString(frequencyIds: List<BigInteger>?): String {
    return frequencyIds?.joinToString(",") ?: ""
}
