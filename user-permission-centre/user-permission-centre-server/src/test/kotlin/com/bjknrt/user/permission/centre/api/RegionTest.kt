package com.bjknrt.user.permission.centre.api

import com.bjknrt.user.permission.centre.AbstractContainerBaseTest
import com.bjknrt.user.permission.centre.UpcsRegion
import com.bjknrt.user.permission.centre.UpcsRegionTable
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.vo.ChildListRequest
import com.bjknrt.user.permission.centre.vo.Region
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.math.BigInteger
import java.nio.charset.Charset

@AutoConfigureMockMvc
class RegionTest : AbstractContainerBaseTest() {
    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var upcsRegionTable: UpcsRegionTable

    @BeforeEach
    fun beforeEach() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.ZERO,
            nickName = "nickName",
            loginName = "loginName",
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE)
        )
    }

    @Transactional
    @Test
    fun listTest() {
        upcsRegionTable.delete().execute()

        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(110000000000))
                .setKnName("北京市")
                .setKnLevelCode(1)
                .build()
        )
        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(110100000000))
                .setKnName("市辖区")
                .setKnParentCode(BigInteger.valueOf(110000000000))
                .setKnLevelCode(2)
                .build()
        )
        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(110101000000))
                .setKnName("东城区")
                .setKnParentCode(BigInteger.valueOf(110000000000))
                .setKnLevelCode(3)
                .build()
        )
        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(110101001000))
                .setKnName("东华门街道")
                .setKnParentCode(BigInteger.valueOf(110101000000))
                .setKnLevelCode(4)
                .build()
        )

        val result = mvc.perform(
            MockMvcRequestBuilders.post("/region/childList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(ChildListRequest(BigInteger.valueOf(110000000000)))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val list = upcsRegionTable.select()
            .where(UpcsRegionTable.KnParentCode eq BigInteger.valueOf(110000000000).arg)
            .find()

        val resultList = objectMapper.readValue(result, object : TypeReference<List<Region>>() {})

        Assertions.assertEquals(list.size, resultList.size)
        Assertions.assertEquals(2, list.size)
    }

    @Transactional
    @Test
    fun currentCode() {
        upcsRegionTable.delete().execute()

        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(110000000000))
                .setKnName("北京市")
                .setKnLevelCode(1)
                .build()
        )
        val result = mvc.perform(
            MockMvcRequestBuilders.post("/region/currentCode")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString("110000000000")
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val division = objectMapper.readValue(result, Region::class.java)

        Assertions.assertEquals(BigInteger.valueOf(110000000000), division.code)
    }

    @Transactional
    @Test
    fun listAllTest() {
        upcsRegionTable.delete().execute()

        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(120000000000))
                .setKnName("北京市")
                .setKnLevelCode(1)
                .build()
        )
        upcsRegionTable.save(
            UpcsRegion.builder()
                .setKnCode(BigInteger.valueOf(120100000000))
                .setKnName("市辖区")
                .setKnParentCode(BigInteger.valueOf(120000000000))
                .setKnLevelCode(2)
                .build()
        )

        val result = mvc.perform(
            MockMvcRequestBuilders.post("/region/list")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(
                        listOf(
                            BigInteger.valueOf(120000000000),
                            BigInteger.valueOf(120100000000)
                        )
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString


        val resultList = objectMapper.readValue(result, object : TypeReference<List<Region>>() {})

        Assertions.assertEquals(2, resultList.size)
    }
}
