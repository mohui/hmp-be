package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.doctor.patient.management.api.PatientApi
import org.springframework.stereotype.Component

@Component
class AcuteCoronaryDiseaseBehaviorEvaluateScoreStrategy(
    patientInfoRpcService: PatientApi,
) : BehaviorEvaluateScoreStrategy(patientInfoRpcService) {
    override val optionCodeList = listOf(
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_1",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_2",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_1",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320003_2",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_1",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320004_2",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_1",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320005_2",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320006_1",
        "ACUTE_CORONARY_DISEASE_BEHAVIOR_320006_2",
    )

    override val heightOptionCode = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_1"
    override val weightOptionCode = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320001_2"
    override val waistlineOptionCode = "ACUTE_CORONARY_DISEASE_BEHAVIOR_320002_1"


    override fun getCode(): String {
        return "ACUTE_CORONARY_DISEASE_BEHAVIOR"
    }

}
