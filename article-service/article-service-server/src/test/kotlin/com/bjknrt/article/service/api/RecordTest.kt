package com.bjknrt.article.service.api

import com.bjknrt.article.service.AbstractContainerBaseTest
import com.bjknrt.article.service.AsReadRecordInfoTable
import com.bjknrt.article.service.vo.LastReadTimeParam
import com.bjknrt.article.service.vo.SaveReadRecordParam
import com.bjknrt.framework.util.AppIdUtil
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.Charset

class RecordTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: RecordApi

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mvc: MockMvc

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var asReadRecordInfoTable: AsReadRecordInfoTable

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

    }

    /**
     * To test RecordApi.saveReadRecord
     */
    @Test
    fun saveReadRecordTest() {
        val readId = AppIdUtil.nextId()
        val articleId = AppIdUtil.nextId()
        val readRecordRequest = SaveReadRecordParam(readId, articleId)

        //1、test add
        mvc.perform(
            MockMvcRequestBuilders.post("/record/saveReadRecord")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(readRecordRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //2、test query
        val returnRecordInfo = asReadRecordInfoTable.select()
            .where(AsReadRecordInfoTable.KnReaderId eq readId.arg)
            .findOne() ?: fail("保存阅读记录测试：未查询到数据异常")

        //3、test validation
        assertEquals(readRecordRequest.articleId, returnRecordInfo.knArticleId)

        // 测试查询根据阅读人id获取最后一次阅读时间接口
        val lastReadTimeResult = api.lastReadTime(LastReadTimeParam(readerId = readId))
        assertTrue(lastReadTimeResult.readDateTime != null)
    }
}
