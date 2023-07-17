package com.bjknrt.medication.guide.contraindication

import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.medication.guide.vo.MedicationOrderInner

class ContraindicationRequest(
    val patient: PatientInfoResponse,
    val medicationOrderInner: List<MedicationOrderInner>,
)