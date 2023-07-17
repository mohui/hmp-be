package com.bjknrt.statistic.analysis.api

import com.bjknrt.statistic.analysis.vo.OrgHospitalDoctorPatientNum
import com.bjknrt.statistic.analysis.vo.OrgHospitalPatientNum
import com.bjknrt.statistic.analysis.vo.OrgStatisticRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-statistic-analysis.kato-server-name:\${spring.application.name}}", contextId = "OrgApi")
@Validated
interface OrgApi {


    /**
     * 机构医生管理的患者人数统计
     * 
     *
     * @param orgStatisticRequest
     * @return List<OrgHospitalDoctorPatientNum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/org/hospitalDoctorPatientNum"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getOrgHospitalDoctorPatientNum(@Valid orgStatisticRequest: OrgStatisticRequest): List<OrgHospitalDoctorPatientNum>


    /**
     * 机构管理的子机构患者人数统计
     * 
     *
     * @param orgStatisticRequest
     * @return List<OrgHospitalPatientNum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/org/hospitalPatientNum"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getOrgHospitalPatientNum(@Valid orgStatisticRequest: OrgStatisticRequest): List<OrgHospitalPatientNum>
}
