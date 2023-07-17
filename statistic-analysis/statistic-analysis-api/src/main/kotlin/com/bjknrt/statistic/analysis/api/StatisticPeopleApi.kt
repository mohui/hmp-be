package com.bjknrt.statistic.analysis.api


import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-statistic-analysis.kato-server-name:\${spring.application.name}}", contextId = "StatisticPeopleApi")
@Validated
interface StatisticPeopleApi {

    /**
     * 高血压糖尿病管理统计
     *
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/statisticPeople/htn_t2dm"],
        produces = ["application/json"]
    )
    fun statisticPeopleHtnT2dmGet(@Valid @RequestParam(value = "id", required = false) id: kotlin.String?): Unit


    /**
     * 管理患者人数统计
     *
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/statisticPeople/manager"],
        produces = ["application/json"]
    )
    fun statisticPeopleManagerGet(@Valid @RequestParam(value = "id", required = false) id: kotlin.String?): Unit


    /**
     * 用户统计
     *
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/statisticPeople/user"],
        produces = ["application/json"]
    )
    fun statisticPeopleUserGet(@Valid @RequestParam(value = "id", required = false) id: kotlin.String?): Unit
}
