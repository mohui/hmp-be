package com.bjknrt.doctor.patient.management.api

import com.bjknrt.doctor.patient.management.vo.BindDoctorInfoResponse
import com.bjknrt.doctor.patient.management.vo.BindDoctorRequest
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-doctor-patient-management.kato-server-name:\${spring.application.name}}", contextId = "DoctorPatientApi")
@Validated
interface DoctorPatientApi {


    /**
     * 患者绑定医生（PC）
     * 
     *
     * @param bindDoctorRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctorPatient/bind"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bindDoctor(@Valid bindDoctorRequest: BindDoctorRequest): Unit


    /**
     * 医生绑定患者的数量
     * 
     *
     * @param body
     * @return kotlin.Long
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctorPatient/bindPatientNum"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun bindPatientNum(@Valid body: java.math.BigInteger): kotlin.Long


    /**
     * 查询患者绑定的医生信息（PC+Wechat）
     * 
     *
     * @param body
     * @return BindDoctorInfoResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctorPatient/bindDoctorInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getBindDoctor(@Valid body: java.math.BigInteger): BindDoctorInfoResponse


    /**
     * 患者解绑医生（PC+Wechat）
     * 
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctorPatient/unbind"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun unbindDoctor(@Valid body: java.math.BigInteger): Unit


    /**
     * 更新医患状态（PC）
     * 
     *
     * @param updateStatusRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctorPatient/status"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateStatus(@Valid updateStatusRequest: UpdateStatusRequest): Unit
}
