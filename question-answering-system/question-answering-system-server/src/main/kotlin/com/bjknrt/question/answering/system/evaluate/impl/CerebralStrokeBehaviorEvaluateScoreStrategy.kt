package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import org.springframework.stereotype.Component

@Component
class CerebralStrokeBehaviorEvaluateScoreStrategy(
    patientInfoRpcService: PatientApi,
) : BehaviorEvaluateScoreStrategy(patientInfoRpcService) {
    override val optionCodeList = listOf(
       "CEREBRAL_STROKE_BEHAVIOR_250003_1",
       "CEREBRAL_STROKE_BEHAVIOR_250003_2",
       "CEREBRAL_STROKE_BEHAVIOR_250004_1",
       "CEREBRAL_STROKE_BEHAVIOR_250004_2",
       "CEREBRAL_STROKE_BEHAVIOR_250005_1",
       "CEREBRAL_STROKE_BEHAVIOR_250005_2",
       "CEREBRAL_STROKE_BEHAVIOR_250006_1",
       "CEREBRAL_STROKE_BEHAVIOR_250006_2",
    )

    override val heightOptionCode = "CEREBRAL_STROKE_BEHAVIOR_250001_1"
    override val weightOptionCode = "CEREBRAL_STROKE_BEHAVIOR_250001_2"
    override val waistlineOptionCode = "CEREBRAL_STROKE_BEHAVIOR_250002_1"


    override fun getCode(): String {
        return "CEREBRAL_STROKE_BEHAVIOR"
    }

}
