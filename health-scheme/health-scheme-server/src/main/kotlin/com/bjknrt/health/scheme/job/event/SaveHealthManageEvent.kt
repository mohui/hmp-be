package com.bjknrt.health.scheme.job.event

import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import org.springframework.context.ApplicationEvent

/**
 * 保存健康方案事件对象
 * @param sourceObject 创建者
 * @param healthManage 健康方案
 */
class SaveHealthManageEvent(
    sourceObject: Any,
    val healthManage: HsHealthSchemeManagementInfo
) : ApplicationEvent(sourceObject) {
}
