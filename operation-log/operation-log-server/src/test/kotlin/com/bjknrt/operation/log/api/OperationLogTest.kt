package com.bjknrt.operation.log.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.operation.log.AbstractContainerBaseTest
import com.bjknrt.operation.log.LOG_ACTION_MAP
import com.bjknrt.operation.log.LOG_MODULE_MAP
import com.bjknrt.operation.log.OlOperatorLogTable
import com.bjknrt.operation.log.vo.AddOperationLogParam
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.log.vo.OperationLogPageParam
import com.bjknrt.security.test.AppSecurityTestUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.annotation.Resource

@TestMethodOrder(MethodOrderer.MethodName::class)
@AutoConfigureMockMvc
class OperationLogTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Resource(name = "com.bjknrt.operation.log.api.LogController")
    lateinit var api: LogApi

    lateinit var mvc: MockMvc

    val defaultOrgId: BigInteger = AppIdUtil.nextId()

    @Autowired
    lateinit var logTable: OlOperatorLogTable

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = AppIdUtil.nextId(),
            orgIdSet = setOf(defaultOrgId)
        )
    }

    /**
     * To test LogApi.saveLog
     */
    @Test
    fun saveLogTest() {
        logTable.delete().execute()
        //test save
        val request = AddOperationLogParam(
            loginName = "登录名",
            createdBy = BigInteger.valueOf(1),
            operatorModel = LogModule.DPM,
            operatorAction = LogAction.HEALTHY_ADD_ARTICLE,
            operatorSystem = "操作服务",
            timeMillis = Long.MAX_VALUE,
            ip = "192.168.1.1",
            orgId = defaultOrgId,
            content = "文章标题"
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/log/saveLog")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //test page
        val startTime: LocalDateTime = LocalDateTime.of(
            LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN
        )

        val endTime: LocalDateTime = LocalDateTime.of(
            LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX
        )

        val pageRequest = OperationLogPageParam(startTime, endTime, 1, 10, "登录名")

        val pageResult = api.page(pageRequest)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(request.ip, pageResult._data?.get(0)?.ip)
        Assertions.assertEquals(request.loginName, pageResult._data?.get(0)?.loginName)
        Assertions.assertEquals(LOG_MODULE_MAP[request.operatorModel], pageResult._data?.get(0)?.operatorModel)
        Assertions.assertEquals(LOG_ACTION_MAP[request.operatorAction], pageResult._data?.get(0)?.operatorAction)
        Assertions.assertEquals(request.content, pageResult._data?.get(0)?.context)

        mvc.perform(
            MockMvcRequestBuilders.post("/log/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(pageRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

}