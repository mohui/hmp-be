package com.bjknrt.user.permission.centre.event

import com.bjknrt.user.permission.centre.UpcsHealthService
import org.springframework.context.ApplicationEvent
import java.math.BigInteger

/**
 * 订阅服务包事件
 * @param serviceModels 服务包集合
 * @param patientId 患者id
 */
class SubscribeHealthServiceEvent(
    provider: Any,
    val serviceModels: List<UpcsHealthService>,
    val patientId: BigInteger
) : ApplicationEvent(provider) {
}
