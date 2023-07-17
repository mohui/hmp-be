package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.IndicatorEnum
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class BehaviorEvaluatePostProcessorsHandler(
    clockInRpcService: ClockInApi,
    indicatorRpcService: IndicatorApi
) : ExternalServiceEvaluatePostProcessorsHandler(clockInRpcService, indicatorRpcService) {

    override val examinationPaperCode = "BEHAVIOR"

    override val healthPlanType = HealthPlanType.BEHAVIOR_VISIT

    override val supportClock = true

    override val supportSyncIndicator = true

    override val optionIndicatorList = mapOf(
        BigInteger.valueOf(14000101) to IndicatorEnum.BODY_HEIGHT,
        BigInteger.valueOf(14000102) to IndicatorEnum.BODY_WEIGHT,
        BigInteger.valueOf(14000201) to IndicatorEnum.WAISTLINE
    )

    override fun getOrder(): Int {
        return 1
    }
}
