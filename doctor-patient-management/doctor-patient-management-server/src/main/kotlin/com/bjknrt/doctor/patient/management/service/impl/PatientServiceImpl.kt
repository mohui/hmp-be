package com.bjknrt.doctor.patient.management.service.impl

import cn.hutool.core.collection.ListUtil
import com.bjknrt.doctor.patient.management.*
import com.bjknrt.doctor.patient.management.assembler.*
import com.bjknrt.doctor.patient.management.dao.DpmPatientInfoDAO
import com.bjknrt.doctor.patient.management.event.PatientEvent
import com.bjknrt.doctor.patient.management.event.PatientEventStatus
import com.bjknrt.doctor.patient.management.service.PatientService
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.enum.BooleanIntEnum
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.SelectPatientIndicatorParam
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.question.answering.system.api.EvaluateApi
import com.bjknrt.user.permission.centre.api.RegionApi
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionParam
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class PatientServiceImpl(
    val dpmDoctorInfoTable: DpmDoctorInfoTable,
    val dpmDoctorPatientTable: DpmDoctorPatientTable,
    val dpmPatientInfoTable: DpmPatientInfoTable,
    val dpmPatientInfoDAO: DpmPatientInfoDAO,
    val healthRpcService: IndicatorApi,
    val regionRpcService: RegionApi,
    val diseaseEvaluateRpcService: EvaluateApi,
    val authRpcService: UserApi,
    @Lazy
    val healthManageSchemeRpcService: ManageApi
) : PatientService {

    @Value("\${app.data.batch-num:5000}")
    var batchNumber: Int = 5000

    companion object {
        const val DELIMITERS = ","
        val SERVICE_CODE_HEALTH_MANAGE_MAP = mapOf(
            "htn" to "HYPERTENSION", //高血压
            "t2dm" to "DIABETES", //糖尿病
            "dnt" to "CEREBRAL_STROKE", //脑卒中
            "chd" to "ACUTE_CORONARY_DISEASE", //冠心病
            "copd" to "COPD" //慢阻肺
        )
    }

    @Transactional
    override fun edit(editRequest: EditRequest) {

        val info = dpmPatientInfoTable.select()
            .where(DpmPatientInfoTable.KnId eq editRequest.id.arg)
            .where(DpmPatientInfoTable.KnDel eq false.arg)
            .findOne()
            ?: throw MsgException(AppSpringUtil.getMessage("patient.edit.not-found"))
        editRequestCopyToPatientInfo(editRequest, info)


        val provincialCode = info.knProvincialCode?.let { if (it.isEmpty()) null else BigInteger(it) }
        val municipalCode = info.knMunicipalCode?.let { if (it.isEmpty()) null else BigInteger(it) }
        val countyCode = info.knCountyCode?.let { if (it.isEmpty()) null else BigInteger(it) }
        val townshipCode = info.knTownshipCode?.let { if (it.isEmpty()) null else BigInteger(it) }

        listOfNotNull(
            provincialCode,
            municipalCode,
            countyCode,
            townshipCode
        ).takeIf { it.isNotEmpty() }
            ?.let { codeList ->
                val map = regionRpcService.getRegionList(codeList)
                    .associateBy { it.code }

                val nameList = mutableListOf<String>()
                map[provincialCode]?.let { nameList.add(it.name) }
                map[municipalCode]?.let { nameList.add(it.name) }
                map[countyCode]?.let { nameList.add(it.name) }
                map[townshipCode]?.let { nameList.add(it.name) }

                info.knRegionAddress = nameList.joinToString("-")
            }

        dpmPatientInfoTable.upsert(info)
        //发布患者事件
        AppSpringUtil.publishEvent(
            PatientEvent(this, PatientEventStatus.MODIFY, info, editRequest)
        )
    }


    override fun getPatientInfo(patientId: BigInteger): PatientInfoResponse {

        val patientInfo = dpmPatientInfoTable.select()
            .where(DpmPatientInfoTable.KnId eq patientId.arg)
            .where(DpmPatientInfoTable.KnDel eq false.arg)
            .findOne()
            ?: throw MsgException(AppSpringUtil.getMessage("patient.edit.not-found"))

        val doctorPatient = dpmDoctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq patientInfo.knId.arg)
            .findOne()

        var doctorInfo: DpmDoctorInfo? = null
        if (doctorPatient != null) {
            doctorInfo = dpmDoctorInfoTable.select()
                .where(DpmDoctorInfoTable.KnId eq doctorPatient.knDoctorId.arg)
                .where(DpmDoctorInfoTable.KnDel eq false.arg)
                .findOne()

        }

        val indicator = healthRpcService.selectIndicatorbyPatientId(SelectPatientIndicatorParam(patientId))

        val diseaseOption = diseaseEvaluateRpcService.getLastDiseaseOption(patientId)

        val servicePackageUseDays = try {
            healthManageSchemeRpcService.getHealthManageUsedInfo(patientId)
                .let { ChronoUnit.DAYS.between(it.createdAt, LocalDateTime.now()) }
        } catch (_: NotFoundDataException) {
            null
        }

        return patientInfoToPatientResponse(
            patientInfo,
            doctorInfo,
            indicator,
            diseaseOption,
            doctorPatient?.knBindDoctorDatetime,
            servicePackageUseDays
        )
    }

    override fun page(patientPageRequest: PatientPageRequest): PagedResult<PatientPageResult> {
        val validOrgRegionObj = authRpcService.validOrgRegionPermission(
            ValidOrgRegionPermissionParam(
                orgIdSet = patientPageRequest.orgIdSet?.toList(),
                regionIdSet = patientPageRequest.regionIdSet?.toList()
            )
        )
        // 如果isRun是false,直接返回
        if (!validOrgRegionObj.isRun)
            return PagedResult.emptyPaged(Page(patientPageRequest.pageNo, patientPageRequest.pageSize))

        val orgIdList: List<BigInteger>? = validOrgRegionObj.orgIdSet
        val regionIdList: List<BigInteger>? = validOrgRegionObj.regionIdSet

        val crowdTypeList = patientPageRequest.crowdTypeSet
        val hypertensionCrowdType =
            crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }?.name
        val diabetesCrowdType = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }?.name
        val acuteCoronaryCrowdType =
            crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }?.name
        val cerebralStrokeCrowdType =
            crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }?.name
        val copdCrowdType = crowdTypeList?.firstOrNull { it == CrowdType.COPD }?.name

        val doctorId = patientPageRequest.doctorId
        val pageSize = patientPageRequest.pageSize
        val pageNo = patientPageRequest.pageNo
        val status = patientPageRequest.status?.takeIf { it != ToDoStatus.SEVEN_DAYS_NOT_LOGIN }?.value
        val sevenDaysNotLoginStatus = patientPageRequest.status?.let { it == ToDoStatus.SEVEN_DAYS_NOT_LOGIN } == true
        val crowdTypeAnyMatch = patientPageRequest.crowdTypeAnyMatch ?: false

        val notHaveServicePackage = patientPageRequest.servicePackageCodeSet?.contains(ServiceCode.NONE) ?: false

        val healthManageTypeList = patientPageRequest.servicePackageCodeSet
            ?.mapNotNull { SERVICE_CODE_HEALTH_MANAGE_MAP[it.value] }
            ?.takeIf { it.isNotEmpty() }

        val messageStatus = patientPageRequest.messageStatus

        val data: List<PatientPageResult>

        val pageInfo = dpmPatientInfoDAO.pagePatientInfoByRegionOrg(
            orgIdList,
            regionIdList,
            doctorId,
            patientPageRequest.keyword,
            hypertensionCrowdType,
            diabetesCrowdType,
            acuteCoronaryCrowdType,
            cerebralStrokeCrowdType,
            copdCrowdType,
            crowdTypeAnyMatch,
            status,
            sevenDaysNotLoginStatus,
            notHaveServicePackage,
            healthManageTypeList,
            messageStatus?.name,
            pageSize,
            pageNo
        )
        //查询到的数据获取服务包Id
        val servicePackageCodeIds =
            pageInfo.data
                .filter { !it.knHealthServiceIdStr.isNullOrEmpty() }
                .flatMap { it.knHealthServiceIdStr.split(DELIMITERS) }
                .distinct()
                .filter { it.isNotEmpty() }
                .map { BigInteger(it) }

        //服务包编码-服务包
        val servicePackageMap =
            servicePackageCodeIds.takeIf { it.isNotEmpty() }?.let {
                dpmPatientInfoDAO.findServicePackageList(servicePackageCodeIds).associateBy { it.healthServiceId }
            } ?: emptyMap()

        data = pageInfo.data.map {
            PatientPageResult(
                id = it.knId,
                name = it.knName,
                gender = Gender.valueOf(it.knGender),
                phone = it.knPhone,
                idCard = it.knIdCard,
                toDoStatusList = if (it.isSevenDaysNotLogin) listOf(
                    ToDoStatus.SEVEN_DAYS_NOT_LOGIN,
                    ToDoStatus.valueOf(it.knStatus)
                ) else listOf(ToDoStatus.valueOf(it.knStatus)),
                servicePackageList = transferServicePackageInfo(
                    it.knHealthManageType,
                    it.knHealthServiceIdStr,
                    servicePackageMap
                ),
                messageStatus = MessageStatus.valueOf(it.knMessageStatus),
                messageNum = it.knMessageNum,
                synthesisDiseaseTag = it.knSynthesisDiseaseTag?.let { tag -> PatientSynthesisTag.valueOf(tag) },
                hypertensionDiseaseTag = it.knHypertensionDiseaseTag?.let { tag -> PatientTag.valueOf(tag) },
                diabetesDiseaseTag = it.knDiabetesDiseaseTag?.let { tag -> PatientTag.valueOf(tag) },
                acuteCoronaryDiseaseTag = it.knAcuteCoronaryDiseaseTag?.let { tag -> PatientTag.valueOf(tag) },
                cerebralStrokeDiseaseTag = it.knCerebralStrokeDiseaseTag?.let { tag -> PatientTag.valueOf(tag) },
                copdDiseaseTag = it.knCopdDiseaseTag?.let { tag -> PatientTag.valueOf(tag) },
                bindDatetime = it.knBindDoctorDatetime,
                doctorId = it.knDoctorId,
                doctorName = it.knDoctorName,
                doctorHospitalId = it.knDoctorHospitalId,
                doctorHospitalName = it.knDoctorHospitalName,
                doctorDeptName = it.knDoctorDeptName,
                doctorGender = it.knDoctorGender?.let { gender -> Gender.valueOf(gender) },
                doctorPhone = it.knDoctorPhone,
                pmhEssentialHypertension = it.isPmhEssentialHypertension,
                pmhTypeTwoDiabetes = it.isPmhTypeTwoDiabetes,
                pmhCoronaryHeartDisease = it.isPmhCoronaryHeartDisease,
                pmhCerebralInfarction = it.isPmhCerebralInfarction,
                pmhCopd = it.isPmhCopd,
                pmhDyslipidemiaHyperlipidemia = it.isPmhDyslipidemiaHyperlipidemia,
                pmhDiabeticNephropathy = it.isPmhDiabeticNephropathy,
                pmhDiabeticRetinopathy = it.isPmhDiabeticRetinopathy,
                pmhDiabeticFoot = it.isPmhDiabeticFoot
            )
        }
        return PagedResult(pageInfo.totalPage, pageSize, pageNo, pageInfo.total, data)

    }

    @Transactional
    override fun register(registerRequest: RegisterRequest) {
        val id = registerRequest.id
        // 数据库保存数据
        //查询旧数据
        var patientInfo = dpmPatientInfoTable.findByKnId(id)
        patientInfo = if (patientInfo != null) {
            ////参数转换为旧实体上
            registerRequestToOldPatientInfo(registerRequest, patientInfo)
        } else {
            //参数转换为新实体上
            registerRequestToPatientInfo(registerRequest)
        }
        //入库
        dpmPatientInfoTable.upsert(patientInfo)
    }


    private fun transferServicePackageInfo(
        healthManageType: String?,
        healthServiceIdStr: String?,
        servicePackageMap: Map<BigInteger, DpmPatientInfoDAO.FindServicePackageListResult>
    ): List<ServicePackageInfo> {
        if (healthServiceIdStr.isNullOrEmpty()) {
            return emptyList()
        }
        return healthServiceIdStr.split(DELIMITERS)
            .filter { it.isNotEmpty() }
            .mapNotNull { healthServiceId ->
                val result = servicePackageMap[BigInteger(healthServiceId)] ?: return@mapNotNull null
                val healthManageTypeValue = SERVICE_CODE_HEALTH_MANAGE_MAP[result.healthServiceCode]
                    ?: kotlin.run {
                        LOGGER.warn("服务包编码与健康方案类型无法对应。服务包类型：${result.healthServiceCode}, 健康方案类型: ${SERVICE_CODE_HEALTH_MANAGE_MAP.values}")
                        return@mapNotNull null
                    }
                val isUsed = healthManageType
                    ?.let { type -> healthManageTypeValue == type }
                    ?: false
                ServicePackageInfo(
                    result.healthServiceId,
                    result.healthServiceCode,
                    result.healthServiceName,
                    isUsed,
                    HealthManageType.valueOf(healthManageTypeValue)
                )
            }
            .sortedByDescending { it.isUsed }
            .distinctBy { it.code }
    }


    @Transactional
    override fun updateSevenDaysNotLogin(ids: List<BigInteger>) {
        // 更新所有7天未登录字段为true
        dpmPatientInfoTable.update()
            .setIsSevenDaysNotLogin(true)
            .where(DpmPatientInfoTable.KnDel eq BooleanIntEnum.FALSE.value)
            .execute()

        // 根据参数中的患者Id，分批处理，更新登录的患者7天未登录字段为false
        ids.takeIf { it.isNotEmpty() } ?: return
        ListUtil.split(ids, batchNumber).forEach { list ->
            dpmPatientInfoTable
                .update()
                .setIsSevenDaysNotLogin(false)
                .where(DpmPatientInfoTable.KnId `in` list.map { it.arg })
                .where(DpmPatientInfoTable.KnDel eq BooleanIntEnum.FALSE.value)
                .execute()
        }
    }
}
