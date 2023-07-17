package com.bjknrt.doctor.patient.management.api

import com.bjknrt.doctor.patient.management.vo.UpdateMessageStatusRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-doctor-patient-management.kato-server-name:\${spring.application.name}}", contextId = "MessageApi")
@Validated
interface MessageApi {


    /**
     * 更新医患消息状态
     *
     *
     * @param updateMessageStatusRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor-patient/message/status"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateMessageStatus(@Valid updateMessageStatusRequest: UpdateMessageStatusRequest): Unit
}
