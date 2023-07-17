package com.bjknrt.framework.util

import cn.hutool.core.lang.Assert
import cn.hutool.core.lang.Snowflake
import cn.hutool.core.lang.id.NanoId
import java.math.BigInteger
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AppIdUtil {

    private val NUM_ALPHABET = "0123456789".toCharArray()
    private const val MAX_WORKER_DATACENTER: Long = 9L
    private const val MIN_WORKER_DATACENTER: Long = 0L

    private val workerId: Long = Assert.checkBetween(System.getenv("WORKER_ID")?.toLong() ?: 0L, MIN_WORKER_DATACENTER, MAX_WORKER_DATACENTER)
    private val dataCenterId: Long = Assert.checkBetween(System.getenv("DATA_CENTER_ID")?.toLong() ?: 0L, MIN_WORKER_DATACENTER, MAX_WORKER_DATACENTER)

    private val SNOWFLAKE: Snowflake = Snowflake(workerId, dataCenterId, true)

    fun nextId(): BigInteger {
        return SNOWFLAKE.nextId().toBigInteger()
    }

    /**
     * @param len id长度
     * @return id
     */
    fun nextNanoId(len: Int = 21, alphabet: CharArray? = null): String {
        return NanoId.randomNanoId(null, alphabet, len)
    }


    /**
     * 生成数字字符串，每台机器每天，从一亿个数中随机生成
     * @return 带分隔符的数字字符串
     */
    fun next16NumStrWithSeparator(separator: String = "-"): String {
        return "${
            DateTimeFormatter.ofPattern("yyMM${separator}dd").format(LocalDate.now())
        }${dataCenterId}${workerId}${separator}${
            NanoId.randomNanoId(null, NUM_ALPHABET, 4)
        }${separator}${
            NanoId.randomNanoId(null, NUM_ALPHABET, 4)
        }"
    }

    /**
     * 生成数字字符串，每台机器每天，从一亿个数中随机生成
     * @return 数字字符串
     */
    fun next16NumStr(): String {
        return "${
            DateTimeFormatter.ofPattern("yyMMdd").format(LocalDate.now())
        }${dataCenterId}${workerId}${
            NanoId.randomNanoId(null, NUM_ALPHABET, 8)
        }"
    }
}