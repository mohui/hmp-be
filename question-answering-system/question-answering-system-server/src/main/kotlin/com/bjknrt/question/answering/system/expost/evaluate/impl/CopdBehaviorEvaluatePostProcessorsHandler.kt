package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.IndicatorEnum
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class CopdBehaviorEvaluatePostProcessorsHandler(
    clockInRpcService: ClockInApi,
    indicatorRpcService: IndicatorApi
) : ExternalServiceEvaluatePostProcessorsHandler(clockInRpcService, indicatorRpcService) {

    override val examinationPaperCode = "COPD_BEHAVIOR"

    override val healthPlanType = HealthPlanType.COPD_BEHAVIOR_VISIT

    override val supportClock = true

    override val supportSyncIndicator = true

    override val optionIndicatorList = mapOf(
        BigInteger.valueOf(31000101) to IndicatorEnum.BODY_HEIGHT,
        BigInteger.valueOf(31000102) to IndicatorEnum.BODY_WEIGHT
    )

    override fun getOrder(): Int {
        return 7
    }
}
