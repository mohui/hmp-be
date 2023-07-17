package com.bjknrt.health.scheme.service.impl

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.service.HealthManageUsedInfoService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.HealthManageUsedInfo
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class HealthManageUsedInfoServiceImpl(
    val healthSchemeManageService: HealthSchemeManageService
) : HealthManageUsedInfoService {
    override fun getHealthManageUsedInfo(
        patientId: BigInteger,
        healthManageType: HealthManageType?
    ): HealthManageUsedInfo? {

        //查询当前患者有效的健康方案或者某个类型最新的健康方案
        val validHealthManage = if (healthManageType == null) {
            healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)
        } else {
            healthSchemeManageService.getLastHealthSchemeManagementInfo(patientId, healthManageType.name)
        } ?: return null

        //健康方案没有阶段的无需后续处理，直接返回
        val validHealthManageType = HealthManageType.valueOf(validHealthManage.knHealthManageType)
        if (validHealthManage.knEndDate == null) {
            return HealthManageUsedInfo(
                startDate = validHealthManage.knStartDate,
                endDate = validHealthManage.knEndDate,
                healthManageType = validHealthManageType,
                createdAt = validHealthManage.knCreatedAt
            )
        }

        //查询当前方案的初始阶段数据
        var healthManageId = validHealthManage.knId
        //上一次的健康方案
        var currentHealthManage :HsHealthSchemeManagementInfo = validHealthManage

        var conditon = true
        while (conditon) {
            healthSchemeManageService.getLastHealthSchemeManagementInfo(patientId, healthManageId)
                ?.let { nextHealthManage ->
                    val currentHealthManageType = HealthManageType.valueOf(currentHealthManage.knHealthManageType)
                    val nextHealthManageType = HealthManageType.valueOf(nextHealthManage.knHealthManageType)
                    //如果健康方案类型相同，继续循环
                    if (currentHealthManageType == nextHealthManageType) {
                        healthManageId = nextHealthManage .knId
                        currentHealthManage = nextHealthManage
                    }
                    //如果健康方案类型不相同，证明切换方案了，跳出循环
                    else {
                        conditon = false
                    }
                }
                ?: kotlin.run { conditon = false } //查询不到数据，跳出循环
        }
        return  HealthManageUsedInfo(
            startDate = currentHealthManage.knStartDate,
            endDate = validHealthManage.knEndDate,
            healthManageType = validHealthManageType,
            createdAt = validHealthManage.knCreatedAt
        )
    }
}
