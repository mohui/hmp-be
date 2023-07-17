package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.health.scheme.service.ClockInService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.BatchClockInParams
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class ClockInServiceImpl(
    val healthSchemeManageService: HealthSchemeManageService,
    val healthPlanClient: HealthPlanApi
) : ClockInService {

    override fun saveClockIn(
        patientId: BigInteger,
        healthPlanType: HealthPlanType,
        currentDateTime: LocalDateTime,
        clockAt: LocalDateTime?
    ) {
        this.saveClockIn(
            patientId = patientId,
            healthPlanType = listOf(healthPlanType),
            currentDateTime = currentDateTime,
            clockAt = clockAt
        )
    }

    override fun saveClockIn(
        patientId: BigInteger,
        healthPlanType: List<HealthPlanType>,
        currentDateTime: LocalDateTime,
        clockAt: LocalDateTime?
    ) {
        try {
            val healthManage = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(
                patientId,
                currentDateTime
            )

            if (healthManage == null) {
                LOGGER.warn("查询不到该患者的健康方案，无法关联健康计划，打卡失败！患者id:${patientId}, 打卡类型：${healthPlanType.map { it.name }}")
                return
            }

            // 获取健康计划id
            val healthPlanIds =
                healthSchemeManageService.getHealthPlanList(healthManage.knId, healthPlanType.map { it.name }, currentDateTime)
                    .map { it.knForeignPlanId }
            // ID不为空,批量打卡
            healthPlanIds
                .takeIf { it.isNotEmpty() }
                ?.let { id ->
                    //批量打卡
                    healthPlanClient.batchIdClockIn(BatchClockInParams(ids = id, clockAt = clockAt))
                }
        } catch (e: Exception) {
            LOGGER.warn("调用打卡接口发生异常！患者id:${patientId}, 打卡类型：${healthPlanType.map { it.name }}", e)
        }
    }

    /**
     * 根据方案ID和健康计划类型打卡
     */
    override fun clockIn(healthManageId: BigInteger, healthPlanType: HealthPlanType, currentDateTime: LocalDateTime, clockAt: LocalDateTime?) {
        try {
            val healthPlanIds =
                healthSchemeManageService.getHealthPlanList(healthManageId, listOf(healthPlanType.name), currentDateTime)
                    .map { it.knForeignPlanId }
            // ID不为空,批量打卡
            healthPlanIds
                .takeIf { it.isNotEmpty() }
                ?.let { id ->
                    //批量打卡
                    healthPlanClient.batchIdClockIn(BatchClockInParams(ids = id, clockAt = clockAt))
                }
        } catch (e: Exception) {
            LOGGER.warn("调用打卡接口发生异常！方案id:${healthManageId}, 打卡类型：${healthPlanType.name}", e)
        }
    }
}
