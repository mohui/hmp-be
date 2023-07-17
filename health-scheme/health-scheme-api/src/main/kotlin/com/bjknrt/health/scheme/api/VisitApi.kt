package com.bjknrt.health.scheme.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.health.scheme.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "VisitApi")
@Validated
interface VisitApi {


    /**
     * 冠心病添加(FE v1.9)
     * 
     *
     * @param coronaryAddParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/acuteCoronaryAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun acuteCoronaryAdd(@Valid coronaryAddParams: CoronaryAddParams): kotlin.Boolean


    /**
     * 冠心病详情
     * 
     *
     * @param body
     * @return AcuteCoronaryVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/acuteCoronaryDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun acuteCoronaryDetail(@Valid body: java.math.BigInteger): AcuteCoronaryVisit


    /**
     * 冠心病随访列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/acuteCoronaryList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun acuteCoronaryList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 脑卒中添加(FE v1.8)
     * 
     *
     * @param cerebralStrokeVisit
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeAdd(@Valid cerebralStrokeVisit: CerebralStrokeVisit): Unit


    /**
     * 脑卒中详情
     * 
     *
     * @param body
     * @return CerebralStrokeVisitInfo
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeDetail(@Valid body: java.math.BigInteger): CerebralStrokeVisitInfo


    /**
     * 脑卒中出院随访添加(FE v1.8)
     * 
     *
     * @param cerebralStrokeLeaveHospitalVisitAddParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeLeaveHospitalVisitAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeLeaveHospitalVisitAdd(@Valid cerebralStrokeLeaveHospitalVisitAddParam: CerebralStrokeLeaveHospitalVisitAddParam): Unit


    /**
     * 脑卒中出院随访详情
     * 
     *
     * @param body
     * @return CerebralStrokeLeaveHospitalVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeLeaveHospitalVisitDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeLeaveHospitalVisitDetail(@Valid body: java.math.BigInteger): CerebralStrokeLeaveHospitalVisit


    /**
     * 脑卒中出院随访列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeLeaveHospitalVisitList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeLeaveHospitalVisitList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 脑卒中随访列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun cerebralStrokeList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 慢阻肺添加(FE v1.9)
     * 
     *
     * @param copdAddParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/copdAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun copdAdd(@Valid copdAddParams: CopdAddParams): kotlin.Boolean


    /**
     * 慢阻肺详情
     * 
     *
     * @param body
     * @return CopdVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/copdDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun copdDetail(@Valid body: java.math.BigInteger): CopdVisit


    /**
     * 慢阻肺随访列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/copdList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun copdList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 脑卒中题目选项回填
     * 
     *
     * @param body
     * @return CerebralStrokeDetailDefaultValue
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/cerebralStrokeDefault"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCerebralStrokeDefaultValue(@Valid body: java.math.BigInteger): CerebralStrokeDetailDefaultValue


    /**
     * 最近一次脑卒中出院随访记录
     * 
     *
     * @param body
     * @return CerebralStrokeLeaveHospitalVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/getLastOneCerebralStrokeLeaveHospitalVisit"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastOneCerebralStrokeLeaveHospitalVisit(@Valid body: java.math.BigInteger): CerebralStrokeLeaveHospitalVisit


    /**
     * 高血压添加
     * 
     *
     * @param htnAddParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/htnAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun htnAdd(@Valid htnAddParams: HtnAddParams): kotlin.Boolean


    /**
     * 高血压详情
     * 
     *
     * @param body
     * @return HtnVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/htnDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun htnDetail(@Valid body: java.math.BigInteger): HtnVisit


    /**
     * 高血压列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/htnList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun htnList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 糖尿病添加
     * 
     *
     * @param t2dmAddParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/t2dmAdd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun t2dmAdd(@Valid t2dmAddParams: T2dmAddParams): kotlin.Boolean


    /**
     * 糖尿病详情
     * 
     *
     * @param body
     * @return T2dmVisit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/t2dmDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun t2dmDetail(@Valid body: java.math.BigInteger): T2dmVisit


    /**
     * 糖尿病列表
     * 
     *
     * @param visitListParams
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/t2dmList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun t2dmList(@Valid visitListParams: VisitListParams): PagedResult<VisitList>


    /**
     * 慢病随访列表
     *
     *
     * @param visitListParam
     * @return PagedResult<VisitList>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/visit/visitList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun visitList(@Valid visitListParam: VisitListParam): PagedResult<VisitList>
}
