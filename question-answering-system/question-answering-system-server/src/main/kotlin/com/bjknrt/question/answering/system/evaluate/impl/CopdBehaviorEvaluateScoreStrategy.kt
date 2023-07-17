package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.util.getBmiValue
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CopdBehaviorEvaluateScoreStrategy(
    patientInfoRpcService: PatientApi,
) : BehaviorEvaluateScoreStrategy(patientInfoRpcService) {
    override val optionCodeList = listOf(
        "COPD_BEHAVIOR_310002_1",
        "COPD_BEHAVIOR_310002_2",
        "COPD_BEHAVIOR_310002_3",

        "COPD_BEHAVIOR_310003_1",
        "COPD_BEHAVIOR_310003_2",
        "COPD_BEHAVIOR_310003_3",

        "COPD_BEHAVIOR_310004_1",
        "COPD_BEHAVIOR_310004_2",
        "COPD_BEHAVIOR_310004_3",
    )

    override val heightOptionCode = "COPD_BEHAVIOR_310001_1"
    override val weightOptionCode = "COPD_BEHAVIOR_310001_2"

    override fun getCode(): String {
        return "COPD_BEHAVIOR"
    }


    override fun getWaistline(options: Collection<EvaluateScoreOption>): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun getBmiScore(options: Collection<EvaluateScoreOption>): BigDecimal {
        val optionMap = options.associate { it.optionCode to it.optionValue }
        if (optionMap.containsKey(heightOptionCode) && optionMap.containsKey(weightOptionCode)) {
            val bmiValue = getBmiValue(
                optionMap[heightOptionCode]?.toBigDecimal(),
                optionMap[weightOptionCode]?.toBigDecimal()
            )
            if (bmiValue < BigDecimal.valueOf(24) &&
                bmiValue > BigDecimal.valueOf(18.5)
            ) {
                return BigDecimal.valueOf(10)
            }
        }
        return BigDecimal.ZERO
    }

}
