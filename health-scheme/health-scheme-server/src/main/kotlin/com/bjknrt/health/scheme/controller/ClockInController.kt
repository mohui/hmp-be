package com.bjknrt.health.scheme.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.service.ClockInService
import com.bjknrt.health.scheme.vo.ClockInRequest
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.health.scheme.api.ClockInController")
class ClockInController(
    val clockInService: ClockInService
) : AppBaseController(), ClockInApi {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun saveClockIn(clockInRequest: ClockInRequest) {
        clockInService.saveClockIn(
            clockInRequest.patientId,
            clockInRequest.healthPlanType,
            clockInRequest.currentDateTime,
            clockInRequest.clockAt
        )
    }
}
