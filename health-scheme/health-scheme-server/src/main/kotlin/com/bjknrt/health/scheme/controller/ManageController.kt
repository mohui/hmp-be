package com.bjknrt.health.scheme.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.enums.Invoker
import com.bjknrt.health.scheme.service.HealthManageUsedInfoService
import com.bjknrt.health.scheme.service.health.HealthManageFacadeService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.SaveHealthManageParam
import com.bjknrt.health.scheme.vo.*
import me.danwi.kato.common.exception.KatoCommonException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.time.LocalDateTime

@RestController("com.bjknrt.health.scheme.api.ManageController")
class ManageController(
    val healthSchemeManageService: HealthSchemeManageService,
    val healthManageFacadeService: HealthManageFacadeService,
    val healthManageUsedInfoService: HealthManageUsedInfoService
) : AppBaseController(), ManageApi {
    @Transactional
    override fun addHealthSchemeManageByDiseaseEvaluate(addManageByDiseaseEvaluateRequest: AddManageByDiseaseEvaluateRequest) {
        throw KatoCommonException(AppSpringUtil.getMessage("health-manage-scheme.addHealthSchemeManageByDiseaseEvaluate-error"))
        /*
        val param = SaveHealthManageParam(
            patientId = addManageByDiseaseEvaluateRequest.patientId,
            invoker = Invoker.DISEASE_EVALUATE,
            startDate = addManageByDiseaseEvaluateRequest.startDate
        )
        healthManageFacadeService.saveHealthManage(param)
         */
    }

    @Transactional
    override fun addHealthSchemeManageByServicePackage(addManageByServicePackageRequest: AddManageByServicePackageRequest) {
        val param = SaveHealthManageParam(
            patientId = addManageByServicePackageRequest.patientId,
            invoker = Invoker.SERVICE_PACKAGE,
            startDate = addManageByServicePackageRequest.startDate
        )
        healthManageFacadeService.saveHealthManage(param)
    }

    @Transactional
    override fun delHealthPlanByPatientIdAndTypes(
        delHealthPlanByPatientIdAndTypes: DelHealthPlanByPatientIdAndTypes
    ) {
        healthManageFacadeService.delHealthPlanByPatientIdAndTypes(delHealthPlanByPatientIdAndTypes)
    }

    override fun detail(body: BigInteger): HealthManageResult {
        return healthManageFacadeService.getManageDetail(body)
    }

    override fun getCurrentValidHealthSchemeManagementInfo(currentHealthSchemeManagementInfoParam: CurrentHealthSchemeManagementInfoParam): HealthSchemeManagementInfo {
        val schemeManagementInfo = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(
            currentHealthSchemeManagementInfoParam.patientId,
            currentHealthSchemeManagementInfoParam.currentDate ?: LocalDateTime.now()
        )
        return transformHealthManage(schemeManagementInfo)
    }

    override fun getHealthManageUsedInfo(body: BigInteger): HealthManageUsedInfo {
        return healthManageUsedInfoService.getHealthManageUsedInfo(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("patient-health-manage-scheme.not-found"))
    }

    override fun getHealthManageUsedInfoByType(healthManageUsedInfoRequest: HealthManageUsedInfoRequest): List<HealthManageUsedInfo> {
        return healthManageUsedInfoRequest.healthManageType
            .mapNotNull {
                healthManageUsedInfoService.getHealthManageUsedInfo(healthManageUsedInfoRequest.patientId, it)
            }
            .sortedByDescending { it.startDate }
    }

    override fun getLastHealthSchemeManagementInfo(body: BigInteger): HealthSchemeManagementInfo {
        val schemeManagementInfo = healthSchemeManageService.getLastHealthSchemeManagementInfo(body)
        return transformHealthManage(schemeManagementInfo)
    }

    @Transactional
    override fun pause(body: BigInteger) {
        healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(body)?.let {
            healthManageFacadeService.removeHealthManage(it.knId)
        }
    }

    @Transactional
    override fun upsertTypeFrequencyHealth(addHealthPlanParam: AddHealthPlanParam) {
        healthManageFacadeService.saveHealthPlan(addHealthPlanParam)
    }

    @Transactional
    override fun addHealthPlan(addHealthPlanParam: AddHealthPlanParam) {
        healthManageFacadeService.addHealthPlan(addHealthPlanParam)
    }

    private fun transformHealthManage(healthManage: HsHealthSchemeManagementInfo?): HealthSchemeManagementInfo {
        return healthManage?.let {
            HealthSchemeManagementInfo(
                knId = it.knId,
                knPatientId = it.knPatientId,
                knHealthManageType = HealthManageType.valueOf(it.knHealthManageType),
                knDiseaseExistsTag = it.knDiseaseExistsTag,
                knManagementStage = it.knManagementStage?.let { stage -> ManageStage.valueOf(stage) },
                knStartDate = it.knStartDate,
                knEndDate = it.knEndDate,
                knReportOutputDate = it.knReportOutputDate,
                knJobId = it.knJobId
            )
        } ?: throw NotFoundDataException(AppSpringUtil.getMessage("patient-health-manage-scheme.not-found"))
    }

}
