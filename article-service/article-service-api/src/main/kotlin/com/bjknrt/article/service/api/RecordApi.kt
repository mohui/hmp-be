package com.bjknrt.article.service.api

import com.bjknrt.article.service.vo.LastReadTimeParam
import com.bjknrt.article.service.vo.LastReadTimeResult
import com.bjknrt.article.service.vo.SaveReadRecordParam
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-article-service.kato-server-name:\${spring.application.name}}", contextId = "RecordApi")
@Validated
interface RecordApi {


    /**
     * 指定用户最后阅读时间
     * 
     *
     * @param lastReadTimeParam
     * @return LastReadTimeResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/record/lastReadTime"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun lastReadTime(@Valid lastReadTimeParam: LastReadTimeParam): LastReadTimeResult


    /**
     * 保存阅读记录
     * 
     *
     * @param saveReadRecordParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/record/saveReadRecord"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveReadRecord(@Valid saveReadRecordParam: SaveReadRecordParam): Unit
}
