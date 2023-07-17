package com.bjknrt.doctor.patient.management.controller

import cn.hutool.core.img.ImgUtil
import cn.hutool.extra.qrcode.QrCodeUtil
import cn.hutool.extra.qrcode.QrConfig
import com.bjknrt.doctor.patient.management.api.QrApi
import com.bjknrt.doctor.patient.management.service.DoctorService
import com.bjknrt.framework.api.AppBaseController
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger


@RestController("com.bjknrt.doctor.patient.management.api.QrController")
class QrController(
    val objectMapper: ObjectMapper,
    val doctorService: DoctorService
) : AppBaseController(), QrApi {

    companion object {
        const val WIDTH = 300
        const val HEIGHT = 300
    }

    override fun getDoctorQrCode(doctorId: String): Any? {
        val doctorInfo = doctorService.getDoctorInfo(BigInteger(doctorId))
        val qr = Qr(doctorInfo.id, doctorInfo.name, doctorInfo.hospitalId, doctorInfo.hospitalName)
        getResponse()?.let {
            QrCodeUtil.generate(
                objectMapper.writeValueAsString(qr),
                QrConfig(WIDTH, HEIGHT),
                ImgUtil.IMAGE_TYPE_PNG,
                it.outputStream
            )
        }
        return null
    }

}

class Qr(doctorId: BigInteger, doctorName: String, hospitalId: BigInteger, hospitalName: String) {
    var doctor: QrInfo
    var hospital: QrInfo

    init {
        doctor = QrInfo(doctorId, doctorName)
        hospital = QrInfo(hospitalId, hospitalName)
    }
}

data class QrInfo(val id: BigInteger, val name: String)
