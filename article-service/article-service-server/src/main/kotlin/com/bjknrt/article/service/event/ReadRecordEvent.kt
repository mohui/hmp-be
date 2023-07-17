package com.bjknrt.article.service.event

import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.context.ApplicationEvent
import java.math.BigInteger
import java.time.LocalDateTime

/**
 * 查询阅读记录的事件
 * @param readerId 阅读人id
 * @param articleId 文章id
 * @param healthPlanType 健康计划类型
 * @param currentDate 当前时间
 */
class ReadRecordEvent(
    provider: Any,
    val readerId: BigInteger,
    val articleId: BigInteger,
    val healthPlanType: HealthPlanType,
    val currentDate: LocalDateTime
) : ApplicationEvent(provider)