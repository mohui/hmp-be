package com.bjknrt.statistic.analysis.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.statistic.analysis.StatisticManagerDao
import com.bjknrt.statistic.analysis.api.StatisticManagerApi
import com.bjknrt.statistic.analysis.vo.*
import com.google.common.collect.ImmutableMap
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.statistic.analysis.api.StatisticManagerController")
class StatisticManagerController(val managerDao: StatisticManagerDao) :
    AppBaseController(), StatisticManagerApi {
    fun getHospitalList(id: BigInteger?): List<BigInteger>? {
        // 获取登录信息
        val jwtUser = AppSecurityUtil.jwtUser() ?: return null
        // 判断不是超级管理员,且没有绑定机构也没有请求机构 后续操作不进行
        if (jwtUser.isSuperAdmin().not() && jwtUser.orgIdSet.isEmpty() && id == null) return null
        val regionCode = id ?: (if (!jwtUser.isSuperAdmin()) getCurrentUserHospitalOrRegion() else null)
        return if (regionCode == null)
            listOf()
        else
            managerDao.findHospitalSelfAndChildren(regionCode)
                .map { it.knId }
    }

    /**
     * 慢病管理人群占比
     */
    override fun chronicCrowd(statisticManagerParam: StatisticManagerParam): List<CrowdRateResultInner> {
        val hospitalOrRegion = if (statisticManagerParam.id == null) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) null
            else (getCurrentUserHospitalOrRegion() ?: return listOf())
        } else statisticManagerParam.id
        val hospitalIds = getHospitalList(hospitalOrRegion) ?: return listOf()
        val result =
            managerDao.chronicCrowdList(
                if (hospitalIds.isEmpty()) hospitalOrRegion else null,
                hospitalIds.ifEmpty { null }
            )
        return result.map {
            CrowdRateResultInner(
                CrowdType.valueOf(it.disease),
                PatientTag.valueOf(it.tag),
                it.count
            )
        }
    }

    /**
     * 慢病人群柱状图
     */
    override fun hypertensionDiabetesControl(statisticManagerParam: StatisticManagerParam): List<ControlRateResultInner> {
        val hospitalOrRegion = if (statisticManagerParam.id == null) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) null
            else (getCurrentUserHospitalOrRegion() ?: return listOf())
        } else statisticManagerParam.id
        val hospitalIds = getHospitalList(hospitalOrRegion) ?: return listOf()

        // 人群分类
        val crowdTypeList = statisticManagerParam.crowdTypeList
        // 高血压
        val hypertensionTag = crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }
        // 糖尿病
        val diabetesTag = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }
        // 冠心病
        val acuteCoronaryDiseaseTag =
            crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }
        // 脑卒中
        val cerebralStrokeTag =
            crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }
        // 慢阻肺
        val copdTag = crowdTypeList?.firstOrNull { it == CrowdType.COPD }

        // 查询高血压,糖尿病男女患者数量
        val patientDistrict =
            managerDao.diseaseGenderList(
                if (hospitalIds.isEmpty()) hospitalOrRegion else null,
                PatientTag.EXISTS.value,
                hospitalIds.ifEmpty { null },
                hypertensionTag?.name,
                diabetesTag?.name,
                acuteCoronaryDiseaseTag?.name,
                cerebralStrokeTag?.name,
                copdTag?.name
            )

        val list: MutableList<ControlRateResultInner> = mutableListOf()

        for (it in patientDistrict) {
            // 高血压
            list.add(
                ControlRateResultInner(
                    crowd = CrowdType.HYPERTENSION,
                    gender = Gender.valueOf(it.gender),
                    count = it.hypertensionDiseaseCount
                )
            )
            // 糖尿病
            list.add(
                ControlRateResultInner(
                    crowd = CrowdType.DIABETES,
                    gender = Gender.valueOf(it.gender),
                    count = it.diabetesDiseaseCount
                )
            )
            // 脑卒中
            list.add(
                ControlRateResultInner(
                    crowd = CrowdType.CEREBRAL_STROKE,
                    gender = Gender.valueOf(it.gender),
                    count = it.cerebralStrokeDiseaseCount
                )
            )
            // 冠心病
            list.add(
                ControlRateResultInner(
                    crowd = CrowdType.ACUTE_CORONARY_DISEASE,
                    gender = Gender.valueOf(it.gender),
                    count = it.acuteCoronaryDiseaseCount
                )
            )
            // 慢阻肺
            list.add(
                ControlRateResultInner(
                    crowd = CrowdType.COPD,
                    gender = Gender.valueOf(it.gender),
                    count = it.copdDiseaseCount
                )
            )
        }
        return list
    }

    /**
     * 慢病人群年龄分布
     */
    override fun statisticManagerAgeDistributionOfChronicDiseasePopulationPost(statisticManagerParam: StatisticManagerParam): List<ChronicDiseasePopulationInner> {
        val hospitalOrRegion = if (statisticManagerParam.id == null) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) null
            else (getCurrentUserHospitalOrRegion() ?: return listOf())
        } else statisticManagerParam.id
        val hospitalIds = getHospitalList(hospitalOrRegion) ?: return listOf()

        // 人群分类
        val crowdTypeList = statisticManagerParam.crowdTypeList

        // 高血压
        val hypertensionTag = crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }
        // 糖尿病
        val diabetesTag = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }
        // 冠心病
        val acuteCoronaryDiseaseTag =
            crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }
        // 脑卒中
        val cerebralStrokeTag =
            crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }
        // 慢阻肺
        val copdTag = crowdTypeList?.firstOrNull { it == CrowdType.COPD }

        // 查询
        val rows = managerDao.selectAgeGenderOfChronicDiseasePopulation(
            if (hospitalIds.isEmpty()) hospitalOrRegion else null,
            PatientTag.EXISTS.value,
            hospitalIds.ifEmpty { null },
            hypertensionTag?.name,
            diabetesTag?.name,
            acuteCoronaryDiseaseTag?.name,
            cerebralStrokeTag?.name,
            copdTag?.name
        )
        val ageMapList = ImmutableMap.of(
            18, listOf(0, 18),
            44, listOf(18, 44),
            59, listOf(45, 59),
            79, listOf(60, 79),
            80, listOf(80, 100)
        )
        val changeData = mutableListOf<ChronicDiseasePopulationInner>()
        //处理每个人群的返回数据
        for (rangeMap in ageMapList) {
            // 根据年龄和性别查找男生的病人数
            val rowManFind = rows.find { it.ageRange.compareTo(rangeMap.key) == 0 && it.gender == Gender.MAN.name }
            // 根据年龄和性别查找女生的病人数
            val rowWomanFind = rows.find { it.ageRange.compareTo(rangeMap.key) == 0 && it.gender == Gender.WOMAN.name }
            val obj = ChronicDiseasePopulationInner(
                ageRangeMin = rangeMap.value[0],
                ageRangeMax = rangeMap.value[1],
                manCount = rowManFind?.patientCount?.toInt() ?: 0,
                womanCount = rowWomanFind?.patientCount?.toInt() ?: 0
            )
            changeData.add(obj)
        }
        return changeData
    }

    override fun summaryTitle(statisticManagerParam: StatisticManagerParam): HomeSummaryTitle {
        val hospitalOrRegion = if (statisticManagerParam.id == null) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) null
            else getCurrentUserHospitalOrRegion() ?: return HomeSummaryTitle(0, 0, 0, 0, 0)
        } else statisticManagerParam.id
        val hospitalIds = getHospitalList(hospitalOrRegion) ?: return HomeSummaryTitle(0, 0, 0, 0, 0)

        // 人群分类
        val crowdTypeList = statisticManagerParam.crowdTypeList

        // 高血压
        val hypertensionTag = crowdTypeList?.firstOrNull { it == CrowdType.HYPERTENSION }
        // 糖尿病
        val diabetesTag = crowdTypeList?.firstOrNull { it == CrowdType.DIABETES }
        // 冠心病
        val acuteCoronaryDiseaseTag =
            crowdTypeList?.firstOrNull { it == CrowdType.ACUTE_CORONARY_DISEASE }
        // 脑卒中
        val cerebralStrokeTag =
            crowdTypeList?.firstOrNull { it == CrowdType.CEREBRAL_STROKE }
        // 慢阻肺
        val copdTag = crowdTypeList?.firstOrNull { it == CrowdType.COPD }

        val result1 =
            managerDao.findHomeSummaryTitle(
                if (hospitalIds.isEmpty()) hospitalOrRegion else null,
                hospitalIds.ifEmpty { null },
                PatientTag.EXISTS.value,
                hypertensionTag?.name,
                diabetesTag?.name,
                acuteCoronaryDiseaseTag?.name,
                cerebralStrokeTag?.name,
                copdTag?.name
            )
        val result2 =
            managerDao.findHomeSummaryTitle_DAU(
                if (hospitalIds.isEmpty()) hospitalOrRegion else null,
                hospitalIds.ifEmpty { null },
                hypertensionTag?.name,
                diabetesTag?.name,
                acuteCoronaryDiseaseTag?.name,
                cerebralStrokeTag?.name,
                copdTag?.name
            )
        val result3 =
            managerDao.findHomeSummaryTitle_Staff(
                if (hospitalIds.isEmpty()) hospitalOrRegion else null,
                hospitalIds.ifEmpty { null }
            )
        return HomeSummaryTitle(
            peopleNumber = result1?.count?.toInt() ?: 0,
            patientNumber = result2?.toInt() ?: 0,
            serviceRegisterNumber = result1?.servicesNumber?.toInt() ?: 0,
            medicalPersonnelNumber = result3?.toInt() ?: 0,
            fiveDiseaseNumber = result1?.fiveDisease?.toInt() ?: 0
        )
    }

    private fun getCurrentUserHospitalOrRegion(): BigInteger? {
        val user = AppSecurityUtil.jwtUser()
        return user?.regionIdSet?.firstOrNull() ?: user?.orgIdSet?.firstOrNull()
    }
}
