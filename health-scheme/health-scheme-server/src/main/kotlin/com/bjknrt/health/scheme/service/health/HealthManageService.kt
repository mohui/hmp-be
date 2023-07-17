package com.bjknrt.health.scheme.service.health

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.entity.DrugPlanDTO
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.enums.DiseaseExistsTag
import com.bjknrt.health.scheme.vo.*
import java.math.BigInteger
import java.time.LocalDate


interface HealthManageService {

    /**
     * 保存健康方案
     * @param patientId 患者Id
     * @param healthManageType 健康方案类型
     * @param startDate 健康方案开始时间
     * @param manageStage 健康方案阶段
     * @param diseaseExistsTagList 五病患者标签集合
     */
    fun saveHealthManage(
        patientId: BigInteger,
        healthManageType: HealthManageType,
        startDate: LocalDate,
        manageStage: ManageStage?,
        diseaseExistsTagList: List<DiseaseExistsTag>?
    ): HsHealthSchemeManagementInfo

    /**
     * 保存通用的健康方案提醒计划
     * @param healthManage 健康方案
     */
    fun saveCommentHealthPlan(healthManage: HsHealthSchemeManagementInfo)

    /**
     * 保存个性化健康计划
     * @param healthManage 健康方案
     */
    fun saveHealthPlan(healthManage: HsHealthSchemeManagementInfo): List<HsHsmHealthPlan>

    /**
     * 发布健康保存健康方案事件
     * @param healthManage 健康方案
     */
    fun publishSaveHealthManageEvent(healthManage: HsHealthSchemeManagementInfo)

    /**
     * 发布健康保存健康计划事件，进行回调任务的创建
     * @param healthManage 健康方案
     * @param healthPlanList 健康方案对应的提醒计划
     */
    fun publishSaveHealthPlanEvent(healthManage: HsHealthSchemeManagementInfo, healthPlanList: List<HsHsmHealthPlan>)

    /**
     * 删除健康方案
     * @param healthManageId 健康方案Id
     * @param jobId 健康方案回调任务Id
     */
    fun removeHealthManage(healthManageId: BigInteger, jobId: String? = null)

    /**
     * 先删除后添加健康计划到本地和远程
     * @param patientId 患者ID
     * @param healthManageId 健康方案Id
     * @param healthPlans 几周几次的健康计划参数
     * @param drugPlans 周几几点的健康计划参数
     */
    fun saveHealthPlanAndRemindPlan(
        patientId: BigInteger,
        healthManageId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>,
    ): List<HsHsmHealthPlan>?

    /**
     * 仅添加健康计划到本地和远程
     * @param healthManageId 健康方案ID
     * @param healthPlans 频率为一周几次的健康计划
     * @param drugPlans 频率为周几几点的健康计划
     */
    fun addHealthPlan(
        patientId: BigInteger,
        healthManageId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>
    ): List<HsHsmHealthPlan>?

    /**
     * 根据患者ID, 方案ID 健康计划类型删除健康计划
     */
    fun delHealthPlanByPatientIdAndHealthManageIdAndTypes(
        patientId: BigInteger,
        healthManageId: BigInteger,
        types: List<HealthPlanType>
    )

    /**
     * 删除本地和远程指定计划类型的健康计划
     * @param healthManageId 健康方案Id
     * @param  healthPlanTypes 健康计划类型集合
     */
    fun deleteHealthPlanAndRemindPlan(
        healthManageId: BigInteger,
        healthPlanTypes: List<HealthPlanType>,
    )
}
