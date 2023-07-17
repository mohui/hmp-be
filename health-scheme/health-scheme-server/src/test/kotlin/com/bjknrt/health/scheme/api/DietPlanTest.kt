package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.AbstractContainerBaseTest
import com.bjknrt.health.scheme.constant.DIET_PLAN
import com.bjknrt.health.scheme.enums.DietPlanTypeEnum
import com.bjknrt.health.scheme.vo.DietPlanType
import com.bjknrt.health.scheme.vo.TypeGetListParam
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DietPlanTest : AbstractContainerBaseTest() {
    @Autowired
    lateinit var api: DietPlanApi

    // type获取饮食计划文章测试
    @Test
    fun typeGetListTest() {
        for (it in DietPlanType.values()) {

            // 取出type
            val type = DietPlanTypeEnum.valueOf(it.value)
            // 根据 type 获取文章内容
            val getDietPlanArticle = DIET_PLAN[type]
            val getApiList = api.typeGetList(TypeGetListParam(type = it))
            // 比对数组长度
            Assertions.assertEquals(getDietPlanArticle?.size, getApiList.size)
            // 比对第一条的title
            Assertions.assertEquals(getDietPlanArticle?.first()?.title, getApiList.first().title)
            // 比对第一条context长度
            Assertions.assertEquals(getDietPlanArticle?.first()?.contexts?.size, getApiList.first().contexts?.size)
            // 比对第一条context的第一条title值
            Assertions.assertEquals(
                getDietPlanArticle?.first()?.contexts?.first()?.title,
                getApiList.first().contexts?.first()?.title
            )
            // 比对第一条context的第一条contexts值
            Assertions.assertEquals(
                getDietPlanArticle?.first()?.contexts?.first()?.context,
                getApiList.first().contexts?.first()?.context
            )
        }
    }
}