package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.vo.ClockInRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "ClockInApi")
@Validated
interface ClockInApi {


    /**
     * 健康计划打卡
     * 
     *
     * @param clockInRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/clockIn/add"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveClockIn(@Valid clockInRequest: ClockInRequest): Unit
}
