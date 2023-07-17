package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import org.springframework.stereotype.Component

@Component
class DiabetesBehaviorEvaluateScoreStrategy(
    patientInfoRpcService: PatientApi,
) : BehaviorEvaluateScoreStrategy(patientInfoRpcService) {
    override val optionCodeList = listOf(
       "DIABETES_BEHAVIOR_210003_1",
       "DIABETES_BEHAVIOR_210003_2",
       "DIABETES_BEHAVIOR_210004_1",
       "DIABETES_BEHAVIOR_210004_2",
       "DIABETES_BEHAVIOR_210005_1",
       "DIABETES_BEHAVIOR_210005_2",
       "DIABETES_BEHAVIOR_210006_1",
       "DIABETES_BEHAVIOR_210006_2",
    )

    override val heightOptionCode = "DIABETES_BEHAVIOR_210001_1"
    override val weightOptionCode = "DIABETES_BEHAVIOR_210001_2"
    override val waistlineOptionCode = "DIABETES_BEHAVIOR_210002_1"


    override fun getCode(): String {
        return "DIABETES_BEHAVIOR"
    }
}
