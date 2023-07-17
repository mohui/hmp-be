package com.bjknrt.doctor.patient.management.event.listener

import com.bjknrt.doctor.patient.management.event.PatientEvent
import com.bjknrt.health.scheme.api.ManageApi
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PatientEventListener(
    val healthManageSchemeRpcService: ManageApi
) {

    /**
     * 患者事件监听器
     * 用于修改患者信息后创建健康方案
     */
    @EventListener(PatientEvent::class)
    fun patientEventListener(event: PatientEvent) {
    }
}

