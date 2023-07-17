package com.bjknrt.question.answering.system.event

import com.bjknrt.doctor.patient.management.api.DoctorPatientApi
import com.bjknrt.doctor.patient.management.vo.ToDoStatus
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.FromTag
import com.bjknrt.question.answering.system.QasDiseaseEvaluate
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component

@Component
class SyncPatientInfoEvent(
    val doctorPatientInfoRpcService: DoctorPatientApi,
    val indicatorRpcService: IndicatorApi
) {
    fun sync(diseaseEvaluate: QasDiseaseEvaluate) {
        // 同步医患状态
        doctorPatientInfoRpcService.updateStatus(
            UpdateStatusRequest(
                patientId = diseaseEvaluate.patientId,
                status = ToDoStatus.SUCCESS
            )
        )
        //同步患者指标数据
        var fromTag = FromTag.DOCTOR_VISIT
        if (diseaseEvaluate.patientId == AppSecurityUtil.currentUserIdWithDefault())
            fromTag = FromTag.PATIENT_SELF
        indicatorRpcService.batchAddIndicator(BatchIndicator(
            patientId = diseaseEvaluate.patientId,
            knBodyHeight = diseaseEvaluate.patientHeight,
            knBodyWeight = diseaseEvaluate.patientWeight,
            knWaistline = diseaseEvaluate.patientWaistline,
            knSystolicBloodPressure = diseaseEvaluate.systolicPressure,
            knDiastolicBloodPressure = diseaseEvaluate.diastolicBloodPressure,
            knFastingBloodSandalwood = diseaseEvaluate.fastingBloodSugar,
            knTotalCholesterol = diseaseEvaluate.serumTch,
            fromTag = fromTag
        ))
    }
}
