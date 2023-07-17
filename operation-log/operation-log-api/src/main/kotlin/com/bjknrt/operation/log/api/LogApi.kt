package com.bjknrt.operation.log.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.operation.log.vo.AddOperationLogParam
import com.bjknrt.operation.log.vo.OperationLogInner
import com.bjknrt.operation.log.vo.OperationLogPageParam
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-operation-log.kato-server-name:\${spring.application.name}}", contextId = "LogApi")
@Validated
interface LogApi {


    /**
     * 操作日志分页查询
     * 
     *
     * @param operationLogPageParam
     * @return PagedResult<OperationLogInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/log/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid operationLogPageParam: OperationLogPageParam): PagedResult<OperationLogInner>


    /**
     * 新增操作日志
     * 
     *
     * @param addOperationLogParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/log/saveLog"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveLog(@Valid addOperationLogParam: AddOperationLogParam): Unit
}
