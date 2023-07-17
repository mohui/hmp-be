package com.bjknrt.operation.log

import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule

val LOG_MODULE_MAP = mapOf(
    LogModule.HS to "健康方案模块",
    LogModule.DPM to "医患关系服务",
    LogModule.UPS to "用户权限中心服务",
    LogModule.PATIENT_CLIENT to "患者端服务",
    LogModule.HEALTHY to "健康科普",
)

val LOG_ACTION_MAP = mapOf(
    LogAction.HS_MAKE_HEALTH_SCHEME to "制定健康方案",
    LogAction.DPM_POST_TO_PATIENT_CLIENT to "发送给患者端",
    LogAction.DPM_PATIENT_UN_BINDING_DOCTOR to "患者解绑医生",
    LogAction.DPM_PATIENT_BINDING_DOCTOR to "患者绑定医生",
    LogAction.UPS_DELETE_USER to "删除用户",
    LogAction.UPS_ADD_ADMINISTRATOR to "新增管理员",
    LogAction.UPS_REGISTER_USER to "用户注册",
    LogAction.UPS_RESET_USER_PWD to "重置用户密码",
    LogAction.UPS_UPDATE_USER_PWD to "更新用户密码",
    LogAction.UPS_DELETE_ROLE to "删除角色",
    LogAction.UPS_SAVE_ROLE_PERMISSION to "保存角色权限",
    LogAction.UPS_DELETE_ROLE_PERMISSION to "删除角色权限",
    LogAction.PATIENT_CLIENT_DAILY_ACTIVE_USER to "记录日活",
    LogAction.HEALTHY_ADD_ARTICLE to "新建文章",
    LogAction.HEALTHY_UPDATE_ARTICLE to "修改文章",
    LogAction.HEALTHY_DELETE_ARTICLE to "删除文章",
    LogAction.HEALTHY_PUBLISH_ARTICLE to "发布文章",
    LogAction.HEALTHY_OFF_SHELF_ARTICLE to "下架文章",
    LogAction.HEALTHY_ADD_CARD to "新建卡片",
    LogAction.HEALTHY_UPDATE_CARD to "修改卡片",
    LogAction.HEALTHY_DELETE_CARD to "删除卡片",
    LogAction.HEALTHY_PUBLISH_CARD to "发布卡片",
    LogAction.HEALTHY_OFF_SHELF_CARD to "下架卡片"
)