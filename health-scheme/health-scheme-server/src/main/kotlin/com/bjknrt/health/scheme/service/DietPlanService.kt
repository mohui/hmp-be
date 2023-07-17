package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.vo.DietPlanArticle
import com.bjknrt.health.scheme.vo.TypeGetListParam

interface DietPlanService {
    fun typeGetList(typeGetListParam: TypeGetListParam): List<DietPlanArticle>
}
