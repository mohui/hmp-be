package com.bjknrt.article.service.api

import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-article-service.kato-server-name:\${spring.application.name}}", contextId = "CardApi")
@Validated
interface CardApi {


    /**
     * 添加或修改卡片接口
     * 
     *
     * @param cardInfo
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/addOrUpdate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addOrUpdate(@Valid cardInfo: CardInfo): Unit


    /**
     * 分页查询所有文章和卡片
     * 
     *
     * @param articleAndCardPageRequest
     * @return PagedResult<SimpleEntity>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/articleAndCardPage"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun articleAndCardPage(@Valid articleAndCardPageRequest: ArticleAndCardPageRequest): PagedResult<SimpleEntity>


    /**
     * 下架卡片接口
     * 
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/batchOffShelf"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchOffShelf(@Valid id: kotlin.collections.List<Id>): Unit


    /**
     * 删除卡片接口
     * 
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/delete"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun delete(@Valid id: kotlin.collections.List<Id>): Unit


    /**
     * 查看卡片详情接口
     * 
     *
     * @param body
     * @return CardDetailResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/detail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun detail(@Valid body: java.math.BigInteger): CardDetailResponse


    /**
     * 分页查询卡片接口
     * 
     *
     * @param articlePageParam
     * @return PagedResult<CardInfoResponse>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid articlePageParam: ArticlePageParam): PagedResult<CardInfoResponse>


    /**
     * 知识延伸接口
     * 
     *
     * @param recommendRequest
     * @return PagedResult<SimpleEntity>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/recommend"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun recommend(@Valid recommendRequest: RecommendRequest): PagedResult<SimpleEntity>


    /**
     * 个人卡片推荐接口
     * 
     *
     * @param articleAndCardRecommendParam
     * @return PagedResult<CardRecommendResponse>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/card/recommendList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun recommendList(@Valid articleAndCardRecommendParam: ArticleAndCardRecommendParam): PagedResult<CardRecommendResponse>
}
