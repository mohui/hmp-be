package com.bjknrt.doctor.patient.management.api

import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-doctor-patient-management.kato-server-name:\${spring.application.name}}", contextId = "PatientApi")
@Validated
interface PatientApi {


    /**
     * 患者编辑（PC）
     * 
     *
     * @param editRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/edit"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun edit(@Valid editRequest: EditRequest): Unit


    /**
     * 患者信息（WeChat）
     * 
     *
     * @return PatientInfoResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/info"],
        produces = ["application/json"]
    )
    fun getInfo(): PatientInfoResponse


    /**
     * 患者信息（PC）
     * 
     *
     * @param body
     * @return PatientInfoResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/patientInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getPatientInfo(@Valid body: java.math.BigInteger): PatientInfoResponse


    /**
     * 患者分页查询（PC+WeChat）
     * 
     *
     * @param patientPageRequest
     * @return PagedResult<PatientPageResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid patientPageRequest: PatientPageRequest): PagedResult<PatientPageResult>


    /**
     * 患者注册（WeChat）
     * 
     *
     * @param registerRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/register"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun register(@Valid registerRequest: RegisterRequest): Unit
}
