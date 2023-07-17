package com.bjknrt.operation.log.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.operation.log.AbstractContainerBaseTest
import com.bjknrt.operation.log.vo.AddOperationLogParam
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.security.test.AppSecurityTestUtil
import org.junit.jupiter.api.*
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.capture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import java.math.BigInteger
import java.nio.charset.Charset

@TestMethodOrder(MethodOrderer.MethodName::class)
@AutoConfigureMockMvc
class OperationLogSdkTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var api: TestController

    lateinit var mvc: MockMvc

    @MockBean
    lateinit var operationLogClient: LogApi

    val defaultOrgId: BigInteger = AppIdUtil.nextId()

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

    @Transactional
    @Test
    fun saveLogDefaultTest() {
        Mockito.reset(operationLogClient)

        val request =
            AddOperationLogParam(
                loginName = "登录名",
                createdBy = BigInteger.valueOf(1),
                operatorModel = LogModule.DPM,
                operatorAction = LogAction.HEALTHY_ADD_ARTICLE,
                operatorSystem = "操作服务",
                timeMillis = Long.MAX_VALUE,
                ip = "192.168.1.1"
            )

        api.saveLogDefault(request)

        val param: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient).saveLog(capture(param))
        Assertions.assertEquals(defaultOrgId, param.value.orgId)
    }

    @Transactional
    @Test
    fun saveLogWithOrgIdTest() {
        Mockito.reset(operationLogClient)

        val request =
            AddOperationLogParam(
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

        api.saveLogWithOrgId(request)

        val param: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient).saveLog(capture(param))
        Assertions.assertEquals(request.orgId, param.value.orgId)
    }

}

@RestController
@EnableOperationLog
class TestController {

    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_ADD_ADMINISTRATOR)
    fun saveLogDefault(addOperationLogParam: AddOperationLogParam) {
    }

    @OperationLog(
        module = LogModule.UPS,
        action = LogAction.UPS_ADD_ADMINISTRATOR,
        currentOrgId = "#addOperationLogParam.orgId",
        context = "#addOperationLogParam.content"
    )
    fun saveLogWithOrgId(addOperationLogParam: AddOperationLogParam) {

    }
}