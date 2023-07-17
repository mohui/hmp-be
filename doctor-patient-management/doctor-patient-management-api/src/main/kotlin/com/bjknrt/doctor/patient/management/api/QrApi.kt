package com.bjknrt.doctor.patient.management.api

import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@KatoClient(name = "\${app.hmp-doctor-patient-management.kato-server-name:\${spring.application.name}}", contextId = "QrApi")
@Validated
interface QrApi {


    /**
     * 生成医生二维码
     *
     *
     * @param doctorId
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/qr/code/{doctorId}"],
        produces = ["*/*"]
    )
    fun getDoctorQrCode(@PathVariable("doctorId") doctorId: kotlin.String): kotlin.Any?
}
