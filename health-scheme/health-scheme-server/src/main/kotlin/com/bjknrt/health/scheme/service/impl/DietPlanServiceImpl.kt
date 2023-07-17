package com.bjknrt.health.scheme.service.impl

import com.bjknrt.health.scheme.constant.DIET_PLAN
import com.bjknrt.health.scheme.enums.DietPlanTypeEnum
import com.bjknrt.health.scheme.service.DietPlanService
import com.bjknrt.health.scheme.vo.DietPlanArticle
import com.bjknrt.health.scheme.vo.DietPlanContext
import com.bjknrt.health.scheme.vo.Picture
import com.bjknrt.health.scheme.vo.TypeGetListParam
import org.springframework.stereotype.Service

@Service
class DietPlanServiceImpl: DietPlanService {
    override fun typeGetList(typeGetListParam: TypeGetListParam): List<DietPlanArticle> {
        // 取出type
        val type = DietPlanTypeEnum.valueOf(typeGetListParam.type.value)
        // 根据 type 获取文章内容
        val articles = DIET_PLAN[type]

        // 初始化返回值
        val returnList = mutableListOf<DietPlanArticle>()

        if (articles != null) {
            // 转换为返回值
            for (articleIt in articles) {
                // 图片列表
                val pictures = mutableListOf<Picture>()
                if (articleIt.pictures != null){
                    for (pictureIt in articleIt.pictures) {
                        pictures.add(
                            Picture(
                                url = pictureIt.url,
                                desc = pictureIt.desc
                            )
                        )
                    }
                }

                // 文章列表
                val contexts = mutableListOf<DietPlanContext>()
                if (articleIt.contexts != null) {
                    for (contextIt in articleIt.contexts) {
                        // 文章内也可能存在图片列表,处理文章内的图片列表
                        val contextPictures = mutableListOf<Picture>()
                        if (contextIt.pictures != null){
                            for (contextPictureIt in contextIt.pictures) {
                                contextPictures.add(
                                    Picture(
                                        url = contextPictureIt.url,
                                        desc = contextPictureIt.desc
                                    )
                                )
                            }
                        }
                        contexts.add(
                            DietPlanContext(
                                title = contextIt.title,
                                context = contextIt.context,
                                pictures = contextPictures
                            )
                        )
                    }
                }

                returnList.add(
                    DietPlanArticle(
                        title = articleIt.title,
                        pictures = pictures,
                        contexts = contexts
                    )
                )
            }
        }
        return returnList
    }
}
