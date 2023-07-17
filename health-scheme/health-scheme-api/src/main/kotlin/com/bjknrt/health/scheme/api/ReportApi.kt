package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.vo.AcuteCoronaryDiseaseStageStatisticsDetail
import com.bjknrt.health.scheme.vo.CerebralStrokeStageStatisticsResponse
import com.bjknrt.health.scheme.vo.CopdStageStatisticsResponse
import com.bjknrt.health.scheme.vo.CopdStatisticsDetail
import com.bjknrt.health.scheme.vo.DiabetesStageStatisticsDetail
import com.bjknrt.health.scheme.vo.DiabetesStageStatisticsResponse
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.health.scheme.vo.ReportPageRequest
import com.bjknrt.health.scheme.vo.StageStatisticsDetail
import com.bjknrt.health.scheme.vo.StageStatisticsResponse
import com.bjknrt.health.scheme.vo.StageReport
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "ReportApi")
@Validated
interface ReportApi {


    /**
     * 查询阶段报告详情(冠心病) （FE v1.9）
     * 
     *
     * @param body
     * @return StageStatisticsResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsAcuteCoronaryDisease"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getAcuteCoronaryDiseaseStageStatistics(@Valid body: java.math.BigInteger): StageStatisticsResponse


    /**
     * 查询阶段报告血压指标统计(冠心病) （FE v1.9）
     * 
     *
     * @param body
     * @return List<AcuteCoronaryDiseaseStageStatisticsDetail>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsAcuteCoronaryDiseaseDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getAcuteCoronaryDiseaseStageStatisticsDetailList(@Valid body: java.math.BigInteger): List<AcuteCoronaryDiseaseStageStatisticsDetail>


    /**
     * 查询阶段报告详情(脑卒中)(FE v1.8)
     * 
     *
     * @param body
     * @return CerebralStrokeStageStatisticsResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsCerebralStroke"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCerebralStrokeStageStatistics(@Valid body: java.math.BigInteger): CerebralStrokeStageStatisticsResponse


    /**
     * 查询阶段报告血压指标统计(脑卒中) (FE v1.8)
     * 
     *
     * @param body
     * @return List<StageStatisticsDetail>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsCerebralStrokeDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCerebralStrokeStageStatisticsDetailList(@Valid body: java.math.BigInteger): List<StageStatisticsDetail>


    /**
     * 查询阶段报告详情(慢阻肺) (FE v1.9)
     * 
     *
     * @param body
     * @return CopdStageStatisticsResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsCopd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCopdStageStatistics(@Valid body: java.math.BigInteger): CopdStageStatisticsResponse


    /**
     * 查询阶段报告脉搏氧饱和度指标统计(慢阻肺) (FE v1.9)
     * 
     *
     * @param body
     * @return List<CopdStatisticsDetail>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsCopdDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCopdStatisticsDetailList(@Valid body: java.math.BigInteger): List<CopdStatisticsDetail>


    /**
     * 查询阶段报告详情(糖尿病) 
     * 
     *
     * @param body
     * @return DiabetesStageStatisticsResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsDiabetes"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getDiabetesStageStatistics(@Valid body: java.math.BigInteger): DiabetesStageStatisticsResponse


    /**
     * 查询阶段报告血糖指标统计(糖尿病)
     * 
     *
     * @param body
     * @return List<DiabetesStageStatisticsDetail>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsDiabetesDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getDiabetesStageStatisticsDetailList(@Valid body: java.math.BigInteger): List<DiabetesStageStatisticsDetail>


    /**
     * 查询阶段报告(FE)
     * 
     *
     * @param reportPageRequest
     * @return PagedResult<StageReport>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getReportPage(@Valid reportPageRequest: ReportPageRequest): PagedResult<StageReport>


    /**
     * 查询阶段报告详情(高血压)
     * 
     *
     * @param body
     * @return StageStatisticsResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatistics"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getStageStatistics(@Valid body: java.math.BigInteger): StageStatisticsResponse


    /**
     * 查询阶段报告血压指标统计(高血压)
     * 
     *
     * @param body
     * @return List<StageStatisticsDetail>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/report/stageStatisticsDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getStageStatisticsDetailList(@Valid body: java.math.BigInteger): List<StageStatisticsDetail>
}
