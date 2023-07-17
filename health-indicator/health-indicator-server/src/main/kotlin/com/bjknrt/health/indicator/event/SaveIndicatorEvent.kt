package com.bjknrt.health.indicator.event

import org.springframework.context.ApplicationEvent
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

/**
 * 保存指标事件
 * @param addIndicatorParam: 添加指标参数
 */
class SaveIndicatorEvent(
    provider: Any,
    val addIndicatorParam: AddIndicatorParam,
) : ApplicationEvent(provider)

data class AddIndicatorParam(
    val knId: BigInteger,
    val knPatientId: BigInteger,
    val indicatorName: String,
    val knCreatedAt: LocalDateTime,
    val knMeasureAt: LocalDateTime? = null,
    val extend1: BigDecimal? = null,
    val extend2: BigDecimal? = null,
    val extend3: BigDecimal? = null,
    val extend4: BigDecimal? = null,
    val extend5: BigDecimal? = null,
    val extend6: BigDecimal? = null,
    val extend7: BigDecimal? = null,
    val extend8: BigDecimal? = null,
)
