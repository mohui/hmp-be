package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.stereotype.Component

@Component
class MrsEvaluatePostProcessorsHandler(
    clockInRpcService: ClockInApi,
    indicatorRpcService: IndicatorApi
) : ExternalServiceEvaluatePostProcessorsHandler(clockInRpcService, indicatorRpcService) {

    override val examinationPaperCode = "MRS"

    override val healthPlanType = HealthPlanType.MRS

    override val supportClock = true

    override fun getOrder(): Int {
        return 3
    }
}
