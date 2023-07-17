package com.bjknrt.user.permission.centre.event.listener

import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.AddManageByServicePackageRequest
import com.bjknrt.user.permission.centre.controller.HealthServiceController
import com.bjknrt.user.permission.centre.event.SubscribeHealthServiceEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 订阅服务包事件的监听器
 */
@Component
class SubscribeHealthServiceEventListener(
    val healthManageSchemeRpcService: ManageApi
) : ApplicationListener<SubscribeHealthServiceEvent> {
    override fun onApplicationEvent(event: SubscribeHealthServiceEvent) {
        val sms: List<String> =
            event.serviceModels.filter { it.healthServiceCode != HealthServiceController.BASIC_SERVICE_CODE }
                .map { it.healthServiceCode }
        if (sms.isNotEmpty()) {
            //创建健康方案
            healthManageSchemeRpcService.addHealthSchemeManageByServicePackage(
                AddManageByServicePackageRequest(
                    patientId = event.patientId,
                    startDate = LocalDate.now(),
                    serviceCodeList = sms
                )
            )
        }
    }
}
