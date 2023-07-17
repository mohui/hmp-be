package com.bjknrt.doctor.patient.management.api

import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorEditRequest
import com.bjknrt.doctor.patient.management.vo.DoctorInfo
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-doctor-patient-management.kato-server-name:\${spring.application.name}}", contextId = "DoctorApi")
@Validated
interface DoctorApi {


    /**
     * 新增医生（PC）
     *
     *
     * @param doctorAddRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/add"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addDoctor(@Valid doctorAddRequest: DoctorAddRequest): Unit


    /**
     * 删除医生（PC）
     *
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/remove"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun deleteDoctor(@Valid body: java.math.BigInteger): Unit


    /**
     * 编辑医生（PC）
     *
     *
     * @param doctorEditRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/edit"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun editDoctor(@Valid doctorEditRequest: DoctorEditRequest): Unit


    /**
     * 医生信息（PC）
     *
     *
     * @param body
     * @return DoctorInfo
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/info"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getInfo(@Valid body: java.math.BigInteger): DoctorInfo
}
