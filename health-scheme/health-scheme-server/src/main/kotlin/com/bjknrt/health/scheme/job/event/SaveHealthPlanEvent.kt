package com.bjknrt.health.scheme.job.event

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import org.springframework.context.ApplicationEvent


/**
 * 保存健康计划事件对象
 * @param sourceObject 创建者
 * @param healthManage 健康方案
 * @param healthPlanList 健康计划集合
 */
class SaveHealthPlanEvent(
    sourceObject: Any,
    val healthManage: HsHealthSchemeManagementInfo,
    val healthPlanList: List<HsHsmHealthPlan>
) : ApplicationEvent(sourceObject) {
}
