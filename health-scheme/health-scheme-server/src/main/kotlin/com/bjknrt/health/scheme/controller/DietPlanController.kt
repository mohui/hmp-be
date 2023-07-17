package com.bjknrt.health.scheme.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.health.scheme.api.DietPlanApi
import com.bjknrt.health.scheme.service.DietPlanService
import com.bjknrt.health.scheme.vo.DietPlanArticle
import com.bjknrt.health.scheme.vo.TypeGetListParam
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.health.scheme.api.DietPlanController")
class DietPlanController(
    val dietPlanService: DietPlanService
): AppBaseController(), DietPlanApi {
    override fun typeGetList(typeGetListParam: TypeGetListParam): List<DietPlanArticle> {
        return dietPlanService.typeGetList(typeGetListParam)
    }
}
