package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.vo.DietPlanArticle
import com.bjknrt.health.scheme.vo.TypeGetListParam
import me.danwi.kato.client.KatoClient
import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "DietPlanApi")
@Validated
interface DietPlanApi {


    /**
     * 根据饮食计划type，查询列表
     * 
     *
     * @param typeGetListParam
     * @return List<DietPlanArticle>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/dietPlan/typeGetList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun typeGetList(@Valid typeGetListParam: TypeGetListParam): List<DietPlanArticle>
}
