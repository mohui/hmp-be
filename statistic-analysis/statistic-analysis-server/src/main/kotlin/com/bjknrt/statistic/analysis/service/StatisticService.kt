package com.bjknrt.statistic.analysis.service

import java.math.BigInteger

interface StatisticService {

    fun getDataType(code: BigInteger): DataType

}

enum class DataType(
    val value: String,
) {
    /**
     * 行政区域
     */
    ADMIN_DIVISION("ADMIN_DIVISION"),

    /**
     * 组织机构
     */
    ORGANIZE("ORGANIZE"),

    /**
     * 未查到
     */
    NONE("NONE")
}

