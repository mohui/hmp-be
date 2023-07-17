package com.bjknrt.article.service.api

import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-article-service.kato-server-name:\${spring.application.name}}", contextId = "ArticleApi")
@Validated
interface ArticleApi {


    /**
     * 添加或修改文章接口
     * 
     *
     * @param articleInfo
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/addOrUpdate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addOrUpdate(@Valid articleInfo: ArticleInfo): Unit


    /**
     * 下架文章接口
     * 
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/batchOffShelf"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchOffShelf(@Valid id: kotlin.collections.List<Id>): Unit


    /**
     * 删除文章接口
     * 
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/delete"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun delete(@Valid id: kotlin.collections.List<Id>): Unit


    /**
     * 查看文章详情接口
     * 
     *
     * @param body
     * @return ArticleInfoResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/detail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun detail(@Valid body: java.math.BigInteger): ArticleInfoResponse


    /**
     * 分页查询文章接口
     * 
     *
     * @param articlePageParam
     * @return PagedResult<ArticleInfoResponse>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid articlePageParam: ArticlePageParam): PagedResult<ArticleInfoResponse>


    /**
     * 个人文章推荐接口
     * 
     *
     * @param articleAndCardRecommendParam
     * @return PagedResult<ArticleRecommendResponse>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/article/recommend"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun recommend(@Valid articleAndCardRecommendParam: ArticleAndCardRecommendParam): PagedResult<ArticleRecommendResponse>
}
