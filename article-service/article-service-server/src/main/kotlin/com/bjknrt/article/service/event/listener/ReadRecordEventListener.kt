package com.bjknrt.article.service.event.listener

import com.bjknrt.article.service.AsReadRecordInfoTable
import com.bjknrt.article.service.event.ReadRecordEvent
import com.bjknrt.extension.LOGGER
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.ClockInRequest
import com.bjknrt.health.scheme.vo.HealthPlanType
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.gte
import me.danwi.sqlex.core.query.lt
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 查询阅读记录后的事件监听器
 */
@Component
class ReadRecordEventListener(
    val healthSchemeClockInClient: ClockInApi,
    val recordInfoTable: AsReadRecordInfoTable
) : ApplicationListener<ReadRecordEvent> {
    override fun onApplicationEvent(event: ReadRecordEvent) {
        val readerId = event.readerId
        val articleId = event.articleId
        when (event.healthPlanType) {
            HealthPlanType.SCIENCE_POPULARIZATION_PLAN -> {
                try {
                    //打卡去重逻辑：先查询今天是否阅读过此文章，如果有数据则已打过卡无需打卡，无数据则需要打一次卡
                    val startOfNowDay = LocalDate.now().atStartOfDay()
                    val dailyRecordCount = recordInfoTable.select()
                        .where(AsReadRecordInfoTable.KnReaderId eq readerId.arg)
                        .where(AsReadRecordInfoTable.KnArticleId eq articleId.arg)
                        .where(AsReadRecordInfoTable.KnCreatedAt gte startOfNowDay)
                        .where(AsReadRecordInfoTable.KnCreatedAt lt startOfNowDay.plusDays(1))
                        .count()
                    if (dailyRecordCount == 0L) {
                        // 自动打卡
                        healthSchemeClockInClient.saveClockIn(
                            ClockInRequest(
                                readerId,
                                event.healthPlanType,
                                event.currentDate
                            )
                        )
                    }
                } catch (e: Exception) {
                    LOGGER.warn("阅读文章自动打卡失败：阅读人：${readerId}，文章id:$articleId", e)
                }
            }

            else -> {}
        }
    }
}
