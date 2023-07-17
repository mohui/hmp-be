package com.bjknrt.statistic.analysis.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.statistic.analysis.RegionStatisticDao
import com.bjknrt.statistic.analysis.api.RegionApi
import com.bjknrt.statistic.analysis.utils.TreeNode
import com.bjknrt.statistic.analysis.utils.filterTreeNode
import com.bjknrt.statistic.analysis.vo.*
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.util.*

@RestController("com.bjknrt.statistic.analysis.api.AdminDivisionController")
class RegionController(
    val regionStatisticDao: RegionStatisticDao,
) : AppBaseController(), RegionApi {

    override fun getRegionHospitalPatientPeopleNum(regionRequest: RegionRequest): List<RegionHospitalPatientPeopleNum> {
        //超级管理员特殊处理
        val code: BigInteger? = regionRequest.code
        // 人群分类
        val crowdTypeList = regionRequest.crowdTypeList

        AppSecurityUtil.jwtUser()?.let {
            if (code == null && it.isSuperAdmin().not()) {
                return listOf()
            }
            return@let
        } ?: return listOf()

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

        return regionStatisticDao.getRegionHospitalPatientPeopleNum(
            code,
            PatientTag.EXISTS.value,
            hypertensionTag?.name,
            diabetesTag?.name,
            acuteCoronaryDiseaseTag?.name,
            cerebralStrokeTag?.name,
            copdTag?.name
        )
            .map {
                RegionHospitalPatientPeopleNum(
                    it.knId, it.knName, it.num, it.knRegionCode
                )
            }
    }

    override fun getRegionPatientPeopleNum(regionRequest: RegionRequest): List<RegionPatientPeopleNum> {
        //超级管理员特殊处理
        val code: BigInteger? = regionRequest.code
        // 人群分类
        val crowdTypeList = regionRequest.crowdTypeList

        AppSecurityUtil.jwtUser()?.let {
            if (code == null && it.isSuperAdmin().not()) {
                return listOf()
            }
            return@let
        } ?: return listOf()

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

        val peopleNumResults = regionStatisticDao.getRegionPeopleNum(
            code,
            PatientTag.EXISTS.value,
            hypertensionTag?.name,
            diabetesTag?.name,
            acuteCoronaryDiseaseTag?.name,
            cerebralStrokeTag?.name,
            copdTag?.name
        )

        if (peopleNumResults.isEmpty()) {
            return listOf()
        }

        val nodeList =
            peopleNumResults.map { TreeNode(it.knCode, it.knName, it.knParentCode, it) }

        val treeNodeList = filterTreeNode(nodeList, code)

        val resultList = mutableListOf<RegionPatientPeopleNum>()
        for (treeNode in treeNodeList) {
            val list = LinkedList<TreeNode<BigInteger, RegionStatisticDao.GetRegionPeopleNumResult>>()
            list.add(treeNode)

            var sum = 0L
            while (list.isNotEmpty()) {
                val currentNode = list.poll()
                sum += currentNode.sourceData?.num ?: 0
                list.addAll(currentNode.child)
            }

            resultList.add(
                RegionPatientPeopleNum(
                    treeNode.id,
                    treeNode.name,
                    sum
                )
            )
        }

        return resultList.sortedByDescending { it.num }
    }
}
