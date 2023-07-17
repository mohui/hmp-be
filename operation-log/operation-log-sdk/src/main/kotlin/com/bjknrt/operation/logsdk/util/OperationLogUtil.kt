package com.bjknrt.operation.logsdk.util

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIpAddressUtil
import com.bjknrt.operation.log.api.LogApi
import com.bjknrt.operation.log.vo.AddOperationLogParam
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.config.ServerNameProperties
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigInteger

@Component
class OperationLogUtil(_operationLogClient: LogApi) {
    init {
        operationLogClient = _operationLogClient
    }

    companion object {

        lateinit var operationLogClient: LogApi

        fun buzLog(
            module: LogModule,
            action: LogAction,
            currentOrgId: BigInteger? = null,
            context: String? = null,
            operator: () -> Any? = {}
        ): Any? {
            val beginTime = System.currentTimeMillis()
            val result = operator()
            try {
                saveLog(module, action, context, currentOrgId, System.currentTimeMillis() - beginTime)
            } catch (e: Exception) {
                LOGGER.warn("记录业务日志错误", e)
            }
            return result
        }

        private fun saveLog(module: LogModule, action: LogAction, context: String? = null, currentOrgId: BigInteger? = null, time: Long = 0) {

            val loginName = AppSecurityUtil.jwtUser()?.loginName ?: "UNKNOWN"
            val userId = AppSecurityUtil.currentUserIdWithDefault()

            val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            val ipAdder = attributes?.request?.let { AppIpAddressUtil.getRemoteIpAdder(it) }

            val addOperationLogParam =
                AddOperationLogParam(
                    loginName = loginName,
                    createdBy = userId,
                    ip = ipAdder,
                    operatorModel = module,
                    operatorAction = action,
                    operatorSystem = ServerNameProperties.name,
                    timeMillis = time,
                    orgId = currentOrgId,
                    content = context
                )
            operationLogClient.saveLog(addOperationLogParam)
        }
    }

}