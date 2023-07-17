package com.bjknrt.doctor.patient.management.service

import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.api.vo.PagedResult
import java.math.BigInteger

interface PatientService {

    fun getPatientInfo(patientId: BigInteger): PatientInfoResponse

    fun edit(editRequest: EditRequest)

    fun register(registerRequest: RegisterRequest)

    fun page(patientPageRequest: PatientPageRequest): PagedResult<PatientPageResult>

    /**
     * 更新7天未登录状态
     * @param ids 7天内登录的患者Id
     */
    fun updateSevenDaysNotLogin(ids: List<BigInteger>)
}
