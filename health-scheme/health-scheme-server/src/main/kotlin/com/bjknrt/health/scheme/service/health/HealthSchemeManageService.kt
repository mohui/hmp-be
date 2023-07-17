package com.bjknrt.health.scheme.service.health

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.enums.DiseaseExistsTag
import com.bjknrt.health.scheme.vo.HealthPlanType
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 对健康方案、健康计划数据库表的操作
 */
interface HealthSchemeManageService {


    /**
     * 保存方案提醒计划
     * @param healthManageId 健康方案Id
     * @param type 健康计划类型
     * @param startDateTime 健康计划开始时间
     * @param endDateTime 健康计划结束时间
     * @param planId 提醒服务回传的健康计划id
     * @param frequencyIds 提醒服务回传的健康计划频率id集合
     * @param jobId 健康计划回调任务Id
     */
    fun saveHealthPlan(
        healthManageId: BigInteger,
        type: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        planId: BigInteger,
        frequencyIds: List<BigInteger>?,
        jobId: String? = null
    ): HsHsmHealthPlan

    /**
     * 保存方案提醒计划
     * @param healthPlan 健康计划
     */
    fun saveHealthPlan(healthPlan: HsHsmHealthPlan)


    /**
     * 保存方案方案
     * @param patientId 患者Id
     * @param healthManageType 健康方案类型
     * @param startDate 健康方案开始时间
     * @param endDate 健康方案结束时间
     * @param reportDate 健康方案报告生成时间
     * @param manageStage 健康方案阶段
     * @param diseaseExistsTagList 五病患者标签集合
     */
    fun saveHealthManage(
        patientId: BigInteger,
        healthManageType: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        reportDate: LocalDate?,
        manageStage: String?,
        diseaseExistsTagList: List<DiseaseExistsTag>?
    ): HsHealthSchemeManagementInfo


    /**
     * 获取最新的健康管理方案
     * @param patientId 患者Id
     * @param manageId 健康管理方案Id(不为空查询此健康方案前一条健康管理方案)
     */
    fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        manageId: BigInteger? = null
    ): HsHealthSchemeManagementInfo?

    /**
     * 根据健康方案类型查询该类型最新的健康管理方案
     */
    fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        healthManageType: String
    ): HsHealthSchemeManagementInfo?

    /**
     * 获取当前时间大于等于开始时间，小于结束时间的管理方案
     * @param patientId 患者Id
     * @param currentDate 当前时间
     */
    fun getCurrentValidHealthSchemeManagementInfo(
        patientId: BigInteger,
        currentDate: LocalDateTime = LocalDateTime.now()
    ): HsHealthSchemeManagementInfo?

    /**
     * 获取当前时间大于等于方案创建时间，小于结束时间的脑卒中管理方案
     * @param patientId 患者Id
     * @param currentDate 当前时间
     */
    fun getCurrentValidHealthSchemeManagementInfoStartWithCreatedAt(
        patientId: BigInteger,
        currentDate: LocalDateTime = LocalDateTime.now()
    ): HsHealthSchemeManagementInfo?

    /**
     * 获取指定时间范围内的健康管理方案
     * @param healthManageId 健康方案Id
     */
    fun getHealthSchemeManagementInfo(healthManageId: BigInteger): HsHealthSchemeManagementInfo?

    /**
     * 查询指定计划类型的健康计划
     * @param healthManageId 健康方案Id
     * @param healthPlanTypes 健康计划类型的集合
     */
    fun getHealthPlanList(healthManageId: BigInteger, healthPlanTypes: List<HealthPlanType> = listOf()): List<HsHsmHealthPlan>

    /**
     * 根据类型获取指定时间内健康计划
     * @param healthManageId 健康方案Id
     * @param healthPlanTypeValue 健康计划类型
     * @param currentDateTime 指定的时间
     */
    fun getHealthPlanList(healthManageId: BigInteger, healthPlanTypeValue: List<String>, currentDateTime: LocalDateTime): List<HsHsmHealthPlan>

    /**
     * 更新健康方案
     * @param healthManage 健康方案
     */
    fun updateHealthManage(healthManage: HsHealthSchemeManagementInfo)

    /**
     * 更新健康计划
     * @param healthPlan 健康计划
     */
    fun updateHealthPlan(healthPlan: HsHsmHealthPlan)

    /**
     * 删除健康方案
     * @param healthManageId 健康方案Id
     */
    fun deleteHealthManage(healthManageId: BigInteger)

    /**
     * 删除健康方案提醒计划
     * @param healthManageId 健康方案Id
     * @param type 健康计划类型
     */
    fun deleteHealthPlan(healthManageId: BigInteger, type: String? = null)

    /**
     * 根据健康方案ID和types删除健康计划
     */
    fun delHealthPlanByHealthManageIdAndTypes(healthManageId: BigInteger, types: List<HealthPlanType>)

    /**
     * 批量删除本地健康计划
     * @param healthPlanIds 健康计划ID集合
     */
    fun deleteHealthPlanList(healthPlanIds: List<BigInteger>)
}
