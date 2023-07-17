package com.bjknrt.operation.log.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppIpAddressUtil
import com.bjknrt.operation.log.LOG_ACTION_MAP
import com.bjknrt.operation.log.LOG_MODULE_MAP
import com.bjknrt.operation.log.OlOperatorLog
import com.bjknrt.operation.log.OlOperatorLogTable
import com.bjknrt.operation.log.api.LogApi
import com.bjknrt.operation.log.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.*
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController("com.bjknrt.operation.log.api.LogController")
class LogController(
    val logTable: OlOperatorLogTable
) : AppBaseController(), LogApi {

    override fun page(operationLogPageParam: OperationLogPageParam): PagedResult<OperationLogInner> {

        return AppSecurityUtil.executeWithLogin { user ->
            val condition = logTable.select()
                .where(OlOperatorLogTable.KnCreatedAt gte operationLogPageParam.startTime)
                .where(OlOperatorLogTable.KnCreatedAt lt operationLogPageParam.endTime)

            operationLogPageParam.loginName?.let {
                condition.where(OlOperatorLogTable.KnLoginName like arg("%${it}%"))
            }

            user.takeIf { !user.isSuperAdmin() }?.let {
                condition.where(
                    OlOperatorLogTable.KnOrgId `in` setOf(it.orgIdSet, it.regionIdSet).flatten().map { id -> id.arg })
            }
            // 模块
            val logModules = LOG_MODULE_MAP.getValue(LogModule.PATIENT_CLIENT)
            // 方法
            val logActions = LOG_ACTION_MAP.getValue(LogAction.PATIENT_CLIENT_DAILY_ACTIVE_USER)

            // 添加两个条件
            val pagedResult = condition
                .where(OlOperatorLogTable.KnOperatorModel `ne` logModules)
                .where(OlOperatorLogTable.KnOperatorAction `ne` logActions)
                .order(OlOperatorLogTable.KnCreatedAt, Order.Desc)
                .page(operationLogPageParam.pageSize, operationLogPageParam.pageNo)

            PagedResult.fromDbPaged(pagedResult) {
                OperationLogInner(
                    loginName = it.knLoginName,
                    operatorModel = it.knOperatorModel,
                    operatorAction = it.knOperatorAction,
                    operatorSystem = it.knSys,
                    timeMillis = it.knTimeMillis,
                    id = it.knId,
                    createdAt = it.knCreatedAt,
                    ip = it.knIp,
                    context = it.knContext
                )
            }
        } ?: PagedResult.emptyPaged(Page(operationLogPageParam.pageNo, operationLogPageParam.pageSize))
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun saveLog(addOperationLogParam: AddOperationLogParam) {
        val olOperatorLog = OlOperatorLog.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnLoginName(addOperationLogParam.loginName)
            .setKnCreatedBy(addOperationLogParam.createdBy)
            .setKnOperatorModel(
                LOG_MODULE_MAP.getOrDefault(
                    addOperationLogParam.operatorModel,
                    addOperationLogParam.operatorModel.name
                )
            )
            .setKnOperatorAction(
                LOG_ACTION_MAP.getOrDefault(
                    addOperationLogParam.operatorAction,
                    addOperationLogParam.operatorAction.name
                )
            )
            .setKnSys(addOperationLogParam.operatorSystem)
            .setKnTimeMillis(addOperationLogParam.timeMillis)
            .setKnOrgId(addOperationLogParam.orgId)
            .setKnCreatedAt(LocalDateTime.now())
            .setKnContext(addOperationLogParam.content)
            .setKnIp(addOperationLogParam.ip ?: AppIpAddressUtil.getRemoteIpAdder(getRequest()))
            .build()

        logTable.insert(olOperatorLog)
    }

}