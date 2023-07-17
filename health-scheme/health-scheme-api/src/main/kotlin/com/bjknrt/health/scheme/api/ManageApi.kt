package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "ManageApi")
@Validated
interface ManageApi {


    /**
     * 批量添加健康计划(仅添加)
     * 
     *
     * @param addHealthPlanParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/addHealthPlan"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addHealthPlan(@Valid addHealthPlanParam: AddHealthPlanParam): Unit


    /**
     * 做完五病评估后创建健康管理方案
     * 
     *
     * @param addManageByDiseaseEvaluateRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/addByDiseaseEvaluate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addHealthSchemeManageByDiseaseEvaluate(@Valid addManageByDiseaseEvaluateRequest: AddManageByDiseaseEvaluateRequest): Unit


    /**
     * 购买服务包后创建健康管理方案
     * 
     *
     * @param addManageByServicePackageRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/addByServicePackage"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addHealthSchemeManageByServicePackage(@Valid addManageByServicePackageRequest: AddManageByServicePackageRequest): Unit


    /**
     * 根据患者ID和types删除健康计划
     * 
     *
     * @param delHealthPlanByPatientIdAndTypes
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/delHealthPlanByPatientIdAndTypes"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun delHealthPlanByPatientIdAndTypes(@Valid delHealthPlanByPatientIdAndTypes: DelHealthPlanByPatientIdAndTypes): Unit


    /**
     * 健康方案详情(FE V1.9)
     * 
     *
     * @param body
     * @return HealthManageResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/detail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun detail(@Valid body: java.math.BigInteger): HealthManageResult


    /**
     * 获取当前有效的健康管理方案 (FE v1.10)
     * 
     *
     * @param currentHealthSchemeManagementInfoParam
     * @return HealthSchemeManagementInfo
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/getCurrentValidHealthSchemeManagementInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCurrentValidHealthSchemeManagementInfo(@Valid currentHealthSchemeManagementInfoParam: CurrentHealthSchemeManagementInfoParam): HealthSchemeManagementInfo


    /**
     * 获取健康管理方案使用信息
     * 
     *
     * @param body
     * @return HealthManageUsedInfo
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/getHealthManageUsedInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getHealthManageUsedInfo(@Valid body: java.math.BigInteger): HealthManageUsedInfo


    /**
     * 根据健康方案类型获取健康管理方案使用信息
     * 
     *
     * @param healthManageUsedInfoRequest
     * @return List<HealthManageUsedInfo>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/getHealthManageUsedInfoByType"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getHealthManageUsedInfoByType(@Valid healthManageUsedInfoRequest: HealthManageUsedInfoRequest): List<HealthManageUsedInfo>


    /**
     * 获取最新的健康管理方案(FE v1.6)
     * 
     *
     * @param body
     * @return HealthSchemeManagementInfo
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/getLastHealthSchemeManagementInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastHealthSchemeManagementInfo(@Valid body: java.math.BigInteger): HealthSchemeManagementInfo


    /**
     * 停止患者的健康管理方案
     * 
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/pause"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pause(@Valid body: java.math.BigInteger): Unit


    /**
     * 批量添加健康计划(先删除,后添加)
     * 
     *
     * @param addHealthPlanParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/manage/upsertTypeFrequencyHealth"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun upsertTypeFrequencyHealth(@Valid addHealthPlanParam: AddHealthPlanParam): Unit
}
