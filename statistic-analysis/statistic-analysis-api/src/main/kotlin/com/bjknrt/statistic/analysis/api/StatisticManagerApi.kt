package com.bjknrt.statistic.analysis.api

import com.bjknrt.statistic.analysis.vo.ChronicDiseasePopulationInner
import com.bjknrt.statistic.analysis.vo.ControlRateResultInner
import com.bjknrt.statistic.analysis.vo.CrowdRateResultInner
import com.bjknrt.statistic.analysis.vo.HomeSummaryTitle
import com.bjknrt.statistic.analysis.vo.StatisticManagerParam
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import kotlin.collections.List
import kotlin.collections.Map

@KatoClient(name = "\${app.hmp-statistic-analysis.kato-server-name:\${spring.application.name}}", contextId = "StatisticManagerApi")
@Validated
interface StatisticManagerApi {


    /**
     * 慢病管理人群占比
     * 
     *
     * @param statisticManagerParam
     * @return List<CrowdRateResultInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statisticManager/chronicCrowd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun chronicCrowd(@Valid statisticManagerParam: StatisticManagerParam): List<CrowdRateResultInner>


    /**
     * 糖尿病高血压控制
     * 
     *
     * @param statisticManagerParam
     * @return List<ControlRateResultInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statisticManager/hypertensionDiabetesControl"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun hypertensionDiabetesControl(@Valid statisticManagerParam: StatisticManagerParam): List<ControlRateResultInner>


    /**
     * 慢病人群年龄分布
     * 
     *
     * @param statisticManagerParam
     * @return List<ChronicDiseasePopulationInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statisticManager/ageDistributionOfChronicDiseasePopulation"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun statisticManagerAgeDistributionOfChronicDiseasePopulationPost(@Valid statisticManagerParam: StatisticManagerParam): List<ChronicDiseasePopulationInner>


    /**
     * 首页汇总指标
     * 
     *
     * @param statisticManagerParam
     * @return HomeSummaryTitle
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/statisticManager/summaryTitle"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun summaryTitle(@Valid statisticManagerParam: StatisticManagerParam): HomeSummaryTitle
}
