package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "HealthServiceApi")
@Validated
interface HealthServiceApi {


    /**
     * 是否存在患者健康服务
     * 
     *
     * @param healthServiceExistsRequest
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/exists"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun existsService(@Valid healthServiceExistsRequest: HealthServiceExistsRequest): kotlin.Boolean


    /**
     * 根据Id获取患者订阅的健康服务项目
     * 
     *
     * @param body
     * @return List<HealthService>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/servicePackageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getHealthServicePatient(@Valid body: java.math.BigInteger): List<HealthService>


    /**
     * 获取激活码列表(FE v1.8)
     * 
     *
     * @param activationCodeListParam
     * @return PagedResult<HealthServiceActivationCode>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/activationCodeList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceActivationCodeListPost(@Valid activationCodeListParam: ActivationCodeListParam): PagedResult<HealthServiceActivationCode>


    /**
     * 生成激活码(FE v1.8)
     * 
     *
     * @param healthServiceAddActivationCodePostRequest
     * @return kotlin.String
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/addActivationCode"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceAddActivationCodePost(@Valid healthServiceAddActivationCodePostRequest: HealthServiceAddActivationCodePostRequest): kotlin.String


    /**
     * 禁用激活码(FE v1.8)
     * 
     *
     * @param body
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/forbiddenActivationCode"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceForbiddenActivationCodePost(@Valid body: kotlin.String): kotlin.Boolean


    /**
     * 获取激活码详情(FE v1.8)
     * 
     *
     * @param body
     * @return HealthServiceGetActivationCodePost200Response
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/getActivationCode"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceGetActivationCodePost(@Valid body: kotlin.String): HealthServiceGetActivationCodePost200Response


    /**
     * 获取健康服务包内项目详情
     * 
     *
     * @param body
     * @return List<HealthManagementItem>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/get"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceGetPost(@Valid body: java.math.BigInteger): List<HealthManagementItem>


    /**
     * 获取健康服务列表（含是否订阅）
     * 
     *
     * @return List<HealthService>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/list"],
        produces = ["application/json"]
    )
    fun healthServiceListPost(): List<HealthService>


    /**
     * 获取患者订阅的健康服务项目
     * 
     *
     * @return List<HealthManagementItem>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/patient"],
        produces = ["application/json"]
    )
    fun healthServicePatientPost(): List<HealthManagementItem>


    /**
     * 患者领取健康服务（激活码）FE v1.8
     * 
     *
     * @param body
     * @return List<ReceiveServiceResultInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/receive"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceReceivePost(@Valid body: kotlin.String): List<ReceiveServiceResultInner>


    /**
     * 患者订阅健康服务（点击）
     * 
     *
     * @param body
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/health-service/subscribe"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthServiceSubscribePost(@Valid body: java.math.BigInteger): kotlin.Boolean
}
