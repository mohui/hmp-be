package com.bjknrt.statistic.analysis.api

import com.bjknrt.statistic.analysis.vo.RegionHospitalPatientPeopleNum
import com.bjknrt.statistic.analysis.vo.RegionPatientPeopleNum
import com.bjknrt.statistic.analysis.vo.RegionRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-statistic-analysis.kato-server-name:\${spring.application.name}}", contextId = "RegionApi")
@Validated
interface RegionApi {


    /**
     * 行政区域直属医院管理患者人数统计
     *
     *
     * @param regionRequest
     * @return List<RegionHospitalPatientPeopleNum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/hospitalPatientPeopleNum"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getRegionHospitalPatientPeopleNum(@Valid regionRequest: RegionRequest): List<RegionHospitalPatientPeopleNum>


    /**
     * 行政区域管理患者人数统计
     *
     *
     * @param regionRequest
     * @return List<RegionPatientPeopleNum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/patientPeopleNum"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getRegionPatientPeopleNum(@Valid regionRequest: RegionRequest): List<RegionPatientPeopleNum>
}
