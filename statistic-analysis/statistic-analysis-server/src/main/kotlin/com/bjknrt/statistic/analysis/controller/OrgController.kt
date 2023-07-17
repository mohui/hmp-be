package com.bjknrt.statistic.analysis.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.statistic.analysis.OrgStatisticDao
import com.bjknrt.statistic.analysis.api.OrgApi
import com.bjknrt.statistic.analysis.vo.*
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.statistic.analysis.api.OrgController")
class OrgController(
    val orgStatisticDao: OrgStatisticDao
) : AppBaseController(), OrgApi {

    override fun getOrgHospitalDoctorPatientNum(orgStatisticRequest: OrgStatisticRequest): List<OrgHospitalDoctorPatientNum> {
        //超级管理员特殊处理
        val id: BigInteger? = orgStatisticRequest.id

        AppSecurityUtil.jwtUser()?.let {
            if (id == null && it.isSuperAdmin().not()) {
                return listOf()
            }
            return@let
        } ?: return listOf()

        // 人群分类
        val crowdTypeList = orgStatisticRequest.crowdTypeList
        // 高血压
        val hypertensionTag = crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }
        // 糖尿病
        val diabetesTag = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }
        // 冠心病
        val acuteCoronaryDiseaseTag = crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }
        // 脑卒中
        val cerebralStrokeTag = crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }
        // 慢阻肺
        val copdTag = crowdTypeList?.firstOrNull { it == CrowdType.COPD }

        return orgStatisticDao.getOrgHospitalDoctorPatientNum(
            id,
            PatientTag.EXISTS.value,
            hypertensionTag?.name,
            diabetesTag?.name,
            acuteCoronaryDiseaseTag?.name,
            cerebralStrokeTag?.name,
            copdTag?.name
        )
            .map {
                OrgHospitalDoctorPatientNum(
                    it.knId,
                    it.knName,
                    DoctorLevel.valueOf(it.knDoctorLevel),
                    it.num
                )
            }
            .sortedByDescending { it.num }
    }

    override fun getOrgHospitalPatientNum(orgStatisticRequest: OrgStatisticRequest): List<OrgHospitalPatientNum> {
        //超级管理员特殊处理
        val id: BigInteger? = orgStatisticRequest.id

        AppSecurityUtil.jwtUser()?.let {
            if (id == null && it.isSuperAdmin().not()) {
                return listOf()
            }
            return@let
        } ?: return listOf()

        // 人群分类
        val crowdTypeList = orgStatisticRequest.crowdTypeList
        // 高血压
        val hypertensionTag = crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }
        // 糖尿病
        val diabetesTag = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }
        // 冠心病
        val acuteCoronaryDiseaseTag = crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }
        // 脑卒中
        val cerebralStrokeTag = crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }
        // 慢阻肺
        val copdTag = crowdTypeList?.firstOrNull { it == CrowdType.COPD }

        val patientNumResults = orgStatisticDao.getOrgHospitalPatientNum(
            id,
            PatientTag.EXISTS.value,
            hypertensionTag?.name,
            diabetesTag?.name,
            acuteCoronaryDiseaseTag?.name,
            cerebralStrokeTag?.name,
            copdTag?.name
        )

        if (patientNumResults.isEmpty()) {
            return listOf()
        }

        return patientNumResults.map { OrgHospitalPatientNum(it.knId, it.knName, it.num, it.knRegionCode) }
    }
}
