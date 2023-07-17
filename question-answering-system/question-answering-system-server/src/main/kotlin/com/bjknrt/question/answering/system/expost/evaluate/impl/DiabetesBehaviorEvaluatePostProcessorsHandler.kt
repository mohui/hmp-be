package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.IndicatorEnum
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.HealthPlanType
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class DiabetesBehaviorEvaluatePostProcessorsHandler(
    clockInRpcService: ClockInApi,
    indicatorRpcService: IndicatorApi
) : ExternalServiceEvaluatePostProcessorsHandler(clockInRpcService, indicatorRpcService) {

    override val examinationPaperCode = "DIABETES_BEHAVIOR"

    override val healthPlanType = HealthPlanType.DIABETES_BEHAVIOR_VISIT

    override val supportClock = true

    override val supportSyncIndicator = true

    override val optionIndicatorList = mapOf(
        BigInteger.valueOf(21000101) to IndicatorEnum.BODY_HEIGHT,
        BigInteger.valueOf(21000102) to IndicatorEnum.BODY_WEIGHT,
        BigInteger.valueOf(21000201) to IndicatorEnum.WAISTLINE
    )

    override fun getOrder(): Int {
        return 2
    }
}
