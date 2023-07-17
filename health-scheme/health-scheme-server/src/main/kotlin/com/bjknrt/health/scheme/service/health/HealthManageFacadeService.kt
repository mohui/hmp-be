package com.bjknrt.health.scheme.service.health

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.enums.Invoker
import com.bjknrt.health.scheme.vo.*
import java.math.BigInteger

interface HealthManageFacadeService {

    /**
     * 新建健康方案
     * @param saveHealthManageParam 新建健康方案参数
     * @return 健康方案
     */
    fun saveHealthManage(saveHealthManageParam: SaveHealthManageParam): HsHealthSchemeManagementInfo?

    /**
     * 删除健康方案
     * @param healthManageId 健康方案Id
     */
    fun removeHealthManage(healthManageId: BigInteger)

    /**
     * 先删除,后添加健康计划
     * @param addHealthPlanParam 创建健康计划的参数
     * @return 健康计划列表
     */
    fun saveHealthPlan(addHealthPlanParam: AddHealthPlanParam): List<HsHsmHealthPlan>?

    /**
     * 仅添加健康计划
     */
    fun addHealthPlan(addHealthPlanParam: AddHealthPlanParam): List<HsHsmHealthPlan>?

    /**
     * 根据患者ID和健康计划类型删除健康计划
     */
    fun delHealthPlanByPatientIdAndTypes(delHealthPlanByPatientIdAndTypes: DelHealthPlanByPatientIdAndTypes)

    /**
     * 获取健康方案详情
     * @param  patientId 患者Id
     * @return 健康方案详情
     */
    fun getManageDetail(patientId: BigInteger): HealthManageResult
}


data class SaveHealthManageParam(
    val patientId: BigInteger,

    val startDate: java.time.LocalDate,

    val invoker: Invoker
)
