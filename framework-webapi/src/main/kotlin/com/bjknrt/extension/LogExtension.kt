package com.bjknrt.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap


// 缓存
val LOGGER_CACHE = ConcurrentHashMap<Class<*>, Logger>()

/**
 * 日志
 */
inline val Any.LOGGER: Logger
    get() = LOGGER_CACHE[this::class.java] ?: getLogger(this::class.java)

fun getLogger(clazz: Class<*>): Logger {
    val logger = LoggerFactory.getLogger(clazz)
    LOGGER_CACHE[clazz] = logger
    return logger
}