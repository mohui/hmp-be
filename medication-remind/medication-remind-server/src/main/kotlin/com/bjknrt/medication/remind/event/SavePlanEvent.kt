package com.bjknrt.medication.remind.event

import com.bjknrt.medication.remind.vo.HealthPlanType
import org.springframework.context.ApplicationEvent
import java.math.BigInteger

/**
 * 保存计划的事件
 * @param knPlanId: 计划id
 * @param knPlanType: 计划类型
 * @param knPatientId: 患者id
 */
class SavePlanEvent(
    provider: Any,
    val knPlanId: BigInteger,
    val knPlanType: HealthPlanType,
    val knPatientId: BigInteger
) : ApplicationEvent(provider)
