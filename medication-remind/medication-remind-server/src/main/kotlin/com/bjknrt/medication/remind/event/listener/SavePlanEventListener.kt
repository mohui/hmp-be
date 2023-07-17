package com.bjknrt.medication.remind.event.listener

import com.bjknrt.extension.LOGGER
import com.bjknrt.medication.remind.event.SavePlanEvent
import com.bjknrt.medication.remind.job.NoticeService
import com.bjknrt.medication.remind.vo.HealthPlanType
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * 保存计划后的事件监听器
 */
@Component
class SavePlanEventListener(
    val noticeService: NoticeService
) : ApplicationListener<SavePlanEvent> {
    override fun onApplicationEvent(event: SavePlanEvent) {
        val knPlanId = event.knPlanId
        when (event.knPlanType) {
            HealthPlanType.LEAVE_HOSPITAL_VISIT -> {
                try {
                    //补充出院随访消息提醒
                    noticeService.leaveHospitalVisit(knPlanId, event.knPatientId)
                } catch (e: Exception) {
                    LOGGER.warn("出院随访消息提醒失败,患者ID：$event.knPatientId", e)
                }
            }

            else -> {}
        }
    }
}
