package com.bjknrt.user.permission.centre.api

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.AddManageByServicePackageRequest
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.vo.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthServiceTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: HealthServiceApi

    @Autowired
    lateinit var userTable: UpcsUserTable

    @Autowired
    lateinit var serviceTable: UpcsHealthServiceTable

    @Autowired
    lateinit var itemTable: UpcsHealthManagementItemTable

    @Autowired
    lateinit var serviceItemTable: UpcsHealthServiceItemTable

    @Autowired
    lateinit var activationCodeTable: UpcsHealthServiceActivationCodeTable

    @Autowired
    lateinit var patientHealthServiceTable: UpcsPatientHealthServiceTable

    @MockBean
    lateinit var healthManageSchemeRpcService: ManageApi

    @MockBean
    lateinit var patientInfoRpcService: PatientApi

    //测试用激活码
    val activationCode = "TNT"

    @BeforeAll
    fun addData() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.ONE,
            nickName = "nickName1",
            loginName = "loginName1",
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE)
        )
        userTable.insertWithoutNull(
            UpcsUser.builder()
                .setKnId(BigInteger.ONE)
                .setKnLoginName("loginName1")
                .setKnLoginPassword("pwd")
                .setIsEnabled(true)
                .setKnPhone("1")
                .setKnGender("MAN")
                .setKnName("nickName1")
                .setKnCreatedBy(BigInteger.ZERO)
                .setKnUpdatedBy(BigInteger.ZERO)
                .build()
        )

        itemTable.insertWithoutNull(
            UpcsHealthManagementItem.builder()
                .setHealthManagementItemId(BigInteger.ONE)
                .setHealthManagementItemCode("code")
                .setHealthManagementItemName("name")
                .setCreatedBy(1)
                .build()
        )
        serviceTable.insertWithoutNull(
            UpcsHealthService.builder()
                .setHealthServiceId(BigInteger.ONE)
                .setHealthServiceCode("code")
                .setHealthServiceName("name")
                .setCreatedBy(1)
                .setDuringTime(1)
                .build()
        )
        serviceItemTable.insertWithoutNull(
            UpcsHealthServiceItem.builder()
                .setHealthServiceId(BigInteger.ONE)
                .setHealthManagementItemId(BigInteger.ONE)
                .setCreatedBy(1)
                .build()
        )
        activationCodeTable.insertWithoutNull(
            UpcsHealthServiceActivationCode.builder()
                .setActivationCode(activationCode)
                .setHealthServiceId(BigInteger.ONE)
                .setCreatedBy(1)
                .setUsageTimes(1)
                .build()
        )
        patientHealthServiceTable.insertWithoutNull(
            UpcsPatientHealthService.builder()
                .setPatientId(BigInteger.ONE)
                .setHealthServiceId(BigInteger.ONE)
                .setCreatedBy(1)
                .setId(AppIdUtil.nextId())
                .build()
        )
    }

    /**
     * To test HealthServiceApi.getHealthServicePatient
     */
    @Transactional
    @Test
    fun getHealthServicePatientTest() {
        val body: java.math.BigInteger = BigInteger.ONE
        val response: List<HealthService> = api.getHealthServicePatient(body)
        assertEquals(response.isNotEmpty(), true)
    }

    /**
     * To test HealthServiceApi.healthServiceGetPost
     */
    @Test
    fun healthServiceGetPostTest() {
        val body: java.math.BigInteger = BigInteger.ONE
        val response: List<HealthManagementItem> = api.healthServiceGetPost(body)
        assertEquals(response.isNotEmpty(), true)
    }

    /**
     * To test HealthServiceApi.healthServiceListPost
     */
    @Transactional
    @Test
    fun healthServiceListPostTest() {
        val response: List<HealthService> = api.healthServiceListPost()
        assertEquals(response.isNotEmpty(), true)
    }

    /**
     * To test HealthServiceApi.healthServicePatientPost
     */
    @Test
    fun healthServicePatientPostTest() {
        val response: List<HealthManagementItem> = api.healthServicePatientPost()
        assertEquals(response.isNotEmpty(), true)
    }

    /**
     * To test HealthServiceApi.healthServiceReceivePost
     */
    @Transactional
    @Test
    fun healthServiceReceivePostTest() {
        val response: List<ReceiveServiceResultInner> = api.healthServiceReceivePost(activationCode)
        assertEquals(response.first().healthServiceId == BigInteger.ONE, true)
        Mockito.`when`(patientInfoRpcService.getPatientInfo(BigInteger.ONE)).thenReturn(
            PatientInfoResponse(
                BigInteger.ONE,
                "nickName1",
                com.bjknrt.doctor.patient.management.vo.Gender.MAN,
                "1",
                "",
                LocalDateTime.now(),
                28
            )
        )
        //测试已领取激活码的详情
        healthServiceGetActivationCodePostTest()
    }

    /**
     * To test HealthServiceApi.healthServiceSubscribePost
     */
    @Transactional
    @Test
    fun healthServiceSubscribePostTest() {
        val body: java.math.BigInteger = BigInteger.ONE
        val response: kotlin.Boolean = api.healthServiceSubscribePost(body)

        Mockito.doNothing().`when`(healthManageSchemeRpcService).addHealthSchemeManageByServicePackage(
            AddManageByServicePackageRequest(
                patientId = BigInteger.ONE,
                startDate = LocalDate.now(),
                serviceCodeList = listOf("code")
            )
        )
        assertEquals(response, true)
    }

    @Test
    fun healthServiceActivationCodeListPostTest() {
        val body = ActivationCodeListParam(1, 10, listOf(BigInteger.ONE), listOf(BigInteger.ONE))
        val response = api.healthServiceActivationCodeListPost(body)
        assertEquals(response._data?.isNotEmpty(), true)
        assertEquals(response._data?.any { it.activationCode == activationCode }, true)
    }

    @Transactional
    @Test
    fun healthServiceAddActivationCodePostTest() {
        val body = HealthServiceAddActivationCodePostRequest(
            listOf(
                HealthServiceGetActivationCodePost200ResponseHealthServicesInner(
                    BigInteger.ONE, "code", "name", 1
                )
            )
        )
        val response = api.healthServiceAddActivationCodePost(body)
        assertEquals(response.isNotEmpty(), true)
    }

    @Transactional
    @Test
    fun healthServiceForbiddenActivationCodePostTest() {
        val response = api.healthServiceForbiddenActivationCodePost(activationCode)
        assertEquals(response, true)
    }

    fun healthServiceGetActivationCodePostTest() {
        val response = api.healthServiceGetActivationCodePost(activationCode)
        assertEquals(response.healthServices.size == 1, true)
    }
}
