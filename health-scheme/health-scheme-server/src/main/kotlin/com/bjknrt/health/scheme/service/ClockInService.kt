package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.vo.HealthPlanType
import java.math.BigInteger
import java.time.LocalDateTime

interface ClockInService {

    fun saveClockIn(patientId: BigInteger, healthPlanType: HealthPlanType, currentDateTime: LocalDateTime, clockAt: LocalDateTime? = null)
    fun saveClockIn(patientId: BigInteger, healthPlanType: List<HealthPlanType>, currentDateTime: LocalDateTime, clockAt: LocalDateTime? = null)

    fun clockIn(healthManageId: BigInteger, healthPlanType: HealthPlanType, currentDateTime: LocalDateTime, clockAt: LocalDateTime? = null)

}
