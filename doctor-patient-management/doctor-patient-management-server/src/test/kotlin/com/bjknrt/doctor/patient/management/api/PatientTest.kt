package com.bjknrt.doctor.patient.management.api

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.doctor.patient.management.*
import com.bjknrt.doctor.patient.management.assembler.registerRequestToPatientInfo
import com.bjknrt.doctor.patient.management.constant.HYPERTENSION_HEALTH_MANAGEMENT_SERVICE_CODE
import com.bjknrt.doctor.patient.management.service.PatientService
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.PatientIndicatorResult
import com.bjknrt.health.indicator.vo.SelectPatientIndicatorParam
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.HealthManageUsedInfo
import com.bjknrt.question.answering.system.api.EvaluateApi
import com.bjknrt.question.answering.system.vo.DiseaseOption
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import com.bjknrt.user.permission.centre.api.RegionApi
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.vo.HealthServiceExistsRequest
import com.bjknrt.user.permission.centre.vo.Region
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionParam
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PatientTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: PatientApi

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var dpmPatientInfoTable: DpmPatientInfoTable

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var patientService: PatientService

    @Autowired
    lateinit var dpmDoctorPatientTable: DpmDoctorPatientTable

    @Autowired
    lateinit var dpmDoctorInfoTable: DpmDoctorInfoTable


    lateinit var mvc: MockMvc


    @MockBean
    lateinit var healthRpcService: IndicatorApi

    @MockBean
    lateinit var regionRpcService: RegionApi

    @MockBean
    lateinit var diseaseEvaluateRpcService: EvaluateApi

    @MockBean
    lateinit var healthServiceClient: HealthServiceApi

    @MockBean
    lateinit var healthManageSchemeRpcService: ManageApi

    @MockBean
    lateinit var authRpcService: UserApi

    // 填充患者信息
    val patient1 = BigInteger.valueOf(20221114011)
    val patient2 = BigInteger.valueOf(20221114012)
    val patient3 = BigInteger.valueOf(20221114013)
    val patientName1 = "悟空"
    val patientName2 = "八戒"
    val patientName3 = "沙僧"

    // 填充医生
    val doctorId1 = BigInteger.valueOf(20221114001)
    val doctorId2 = BigInteger.valueOf(20221114002)
    val doctorId3 = BigInteger.valueOf(20221114003)

    // 填充医院
    val hospitalId1 = BigInteger.valueOf(1)
    val hospitalId2 = BigInteger.valueOf(5)
    val hospitalId3 = BigInteger.valueOf(2)

    // 填充行政编码
    val regionCode = BigInteger.valueOf(110000000000)


    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.valueOf(1554368119177216000),
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE),
            orgIdSet = setOf(BigInteger.ZERO, BigInteger.ONE)
        )
    }


    /**
     * To test PatientApi.register
     */
    @Test
    fun registerTest() {
        val patientId = AppIdUtil.nextId()
        val request = RegisterRequest(
            id = patientId,
            name = "沈超",
            gender = Gender.MAN,
            birthday = LocalDateTime.parse("1986-03-07T00:00:00"),
            phone = "18135633248",
            idCard = "59122119280415371X",
            pmhEssentialHypertension = true,
            pmhTypeTwoDiabetes = null,
            pmhCerebralInfarction = null,
            pmhCoronaryHeartDisease = null,
            pmhCopd = null,
            pmhDyslipidemiaHyperlipidemia = null,
            pmhDiabeticNephropathy = null,
            pmhDiabeticRetinopathy = null,
            pmhDiabeticFoot = null
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/patient/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(
                        request
                    )
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun editFailedTest() {
        val patientId = AppIdUtil.nextId()
        Mockito.`when`(
            healthServiceClient.existsService(
                HealthServiceExistsRequest(
                    patientId,
                    HYPERTENSION_HEALTH_MANAGEMENT_SERVICE_CODE
                )
            )
        ).thenReturn(true)

        val editRequest = EditRequest(
            id = patientId,
            name = "zs",
            gender = Gender.MAN,
            phone = "1986-03-07",
            idCard = "18135633248",
            municipalCode = "59122119280415371X",
            pmhEssentialHypertension = true,
            pmhTypeTwoDiabetes = null,
            pmhCerebralInfarction = null,
            pmhCoronaryHeartDisease = null,
            pmhCopd = null,
            pmhDyslipidemiaHyperlipidemia = null,
            pmhDiabeticNephropathy = null,
            pmhDiabeticRetinopathy = null,
            pmhDiabeticFoot = null
        )

        mvc.perform(
            MockMvcRequestBuilders
                .post("/patient/edit")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(
                    objectMapper.writeValueAsString(
                        editRequest
                    )
                )

        )
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
            .andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(AppSpringUtil.getMessage("patient.edit.not-found")))
            )
    }


    @Test
    fun editSuccessTest() {
        val patientId = AppIdUtil.nextId()
        Mockito.`when`(
            healthServiceClient.existsService(
                HealthServiceExistsRequest(
                    patientId,
                    HYPERTENSION_HEALTH_MANAGEMENT_SERVICE_CODE
                )
            )
        ).thenReturn(true)

        val info = registerRequestToPatientInfo(
            RegisterRequest(
                id = patientId,
                name = "沈超",
                gender = Gender.MAN,
                birthday = LocalDateTime.parse("1986-03-07T00:00:00"),
                phone = "18135633248",
                idCard = "59122119280415371X",
                pmhEssentialHypertension = true,
                pmhTypeTwoDiabetes = null,
                pmhCerebralInfarction = null,
                pmhCoronaryHeartDisease = null,
                pmhCopd = null,
                pmhDyslipidemiaHyperlipidemia = null,
                pmhDiabeticNephropathy = null,
                pmhDiabeticRetinopathy = null,
                pmhDiabeticFoot = null
            )
        )


        val editRequest = EditRequest(
            id = info.knId,
            name = "zs",
            gender = Gender.WOMAN,
            phone = "18135633248",
            idCard = "59122119280415371X",
            pmhEssentialHypertension = null,
            pmhTypeTwoDiabetes = true,
            pmhCerebralInfarction = null,
            pmhCoronaryHeartDisease = null,
            pmhCopd = null,
            pmhDyslipidemiaHyperlipidemia = null,
            pmhDiabeticNephropathy = null,
            pmhDiabeticRetinopathy = null,
            pmhDiabeticFoot = null
        )

        info.knAuthId = BigInteger.valueOf(123456)
        info.knCreatedBy = BigInteger.valueOf(123456)
        info.knUpdatedBy = info.knCreatedBy

        val provincialCode = info.knProvincialCode?.let { BigInteger(it) }
        val municipalCode = info.knMunicipalCode?.let { BigInteger(it) }
        val countyCode = info.knCountyCode?.let { BigInteger(it) }
        val townshipCode = info.knTownshipCode?.let { BigInteger(it) }


        Mockito.`when`(
            regionRpcService.getRegionList(
                listOfNotNull(
                    provincialCode,
                    municipalCode,
                    countyCode,
                    townshipCode
                )
            )
        ).thenReturn(
            listOf(
                Region(BigInteger.valueOf(110000000000), "北京", 1),
                Region(BigInteger.valueOf(110100000000), "北京", 1),
                Region(BigInteger.valueOf(110101000000), "北京", 1),
            )
        )

        dpmPatientInfoTable.save(info)

        mvc.perform(
            MockMvcRequestBuilders.post("/patient/edit")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(
                        editRequest
                    )
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun pageTest() {

//        dpmPatientInfoTable.delete().execute()

        dpmPatientInfoTable.save(dpmPatientInfo(BigInteger.valueOf(1), "t1", Gender.WOMAN))
        dpmPatientInfoTable.save(dpmPatientInfo(BigInteger.valueOf(2), "t2", Gender.MAN))
        val localDate = LocalDateTime.now()
        dpmDoctorPatientTable.delete().execute()
        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                BigInteger.valueOf(1),
                BigInteger.valueOf(1),
                BigInteger.valueOf(1),
                localDate
            )
        )

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = null,
                    regionIdSet = null
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = null,
                regionIdSet = null
            )
        )

        val request = PatientPageRequest(1, 10, keyword = "t", doctorId = BigInteger.valueOf(1))


        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/patient/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(
                        request
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val pageResult = objectMapper.readValue(resultString, PagedResult::class.java)

        Assertions.assertEquals(0, pageResult.total)
    }

    @Test
    fun patientInfoServiceTest() {
        val patientId = BigInteger.valueOf(1554368119177216000)
        Mockito.`when`(
            healthRpcService.selectIndicatorbyPatientId(
                SelectPatientIndicatorParam(patientId)
            )
        ).thenReturn(
            PatientIndicatorResult(
                BigInteger.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
            )
        )

        Mockito.`when`(
            diseaseEvaluateRpcService.getLastDiseaseOption(patientId)
        ).thenReturn(
            DiseaseOption(
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                LocalDateTime.now()
            )
        )

        Mockito.`when`(
            healthManageSchemeRpcService.getHealthManageUsedInfo(patientId)
        ).thenReturn(
            HealthManageUsedInfo(
                startDate = LocalDate.now().minusMonths(1),
                endDate = LocalDate.now(),
                healthManageType = com.bjknrt.health.scheme.vo.HealthManageType.HYPERTENSION,
                createdAt = LocalDateTime.now().minusMonths(1)
            )
        )

        dpmPatientInfoTable.save(dpmPatientInfo(patientId, "t1", Gender.MAN))


        val result = mvc.perform(
            MockMvcRequestBuilders.post("/patient/info")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString


        val response = objectMapper.readValue(result, PatientInfoResponse::class.java)

        Assertions.assertEquals(patientId, response.id)
        Assertions.assertEquals("t1", response.name)
        Assertions.assertEquals(true, response.doctor == null)
    }


    @Sql(value = ["/ddl.sql"])
    @Test
    @Order(1)
    fun patientInfoControllerTest() {
        val patientId = BigInteger.valueOf(1015)
        dpmPatientInfoTable.deleteByKnId(patientId)

        Mockito.`when`(
            healthRpcService.selectIndicatorbyPatientId(
                SelectPatientIndicatorParam(patientId)
            )
        ).thenReturn(
            PatientIndicatorResult(
                BigInteger.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
            )
        )

        Mockito.`when`(
            diseaseEvaluateRpcService.getLastDiseaseOption(patientId)
        ).thenReturn(
            DiseaseOption(
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                LocalDateTime.now()
            )
        )

        dpmPatientInfoTable.save(dpmPatientInfo(patientId, "t2", Gender.MAN))

        val doctorId = AppIdUtil.nextId()
        dpmDoctorInfoTable.save(dpmDoctorInfo(doctorId, "zhangsan", Gender.MAN, BigInteger.ONE))

        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                AppIdUtil.nextId(),
                patientId,
                doctorId,
                LocalDateTime.now()
            )
        )

        Mockito.`when`(
            healthManageSchemeRpcService.getHealthManageUsedInfo(patientId)
        ).thenReturn(
            HealthManageUsedInfo(
                startDate = LocalDate.now().minusMonths(1),
                endDate = LocalDate.now(),
                healthManageType = com.bjknrt.health.scheme.vo.HealthManageType.HYPERTENSION,
                createdAt = LocalDateTime.now().minusMonths(1)
            )
        )

        val response = patientService.getPatientInfo(patientId)


        mvc.perform(
            MockMvcRequestBuilders.post("/patient/patientInfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(patientId))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun patientPageTest() {
//        dpmPatientInfoTable.delete().execute()
//        dpmDoctorPatientTable.delete().execute()

        dpmPatientInfoTable.save(dpmPatientInfo(AppIdUtil.nextId(), "t2", Gender.MAN))

        val patientId = AppIdUtil.nextId()
        dpmPatientInfoTable.save(dpmPatientInfo(patientId, "patientPageTest", Gender.MAN))

        val doctorId = doctorId3
        dpmDoctorInfoTable.save(dpmDoctorInfo(doctorId, "zhangsan", Gender.WOMAN, BigInteger.ONE))

        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                AppIdUtil.nextId(),
                patientId,
                doctorId,
                LocalDateTime.now()
            )
        )

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = null,
                    regionIdSet = null
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = null,
                regionIdSet = null
            )
        )

        val request = PatientPageRequest(
            1, 10, doctorId
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/patient/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(objectMapper.writeValueAsString(patientId)))
            )
    }

    @Test
    fun patientHospitalTest() {
//        dpmPatientInfoTable.delete().execute()
//        dpmDoctorInfoTable.delete().execute()
//        dpmDoctorPatientTable.delete().execute()

        dpmPatientInfoTable.save(dpmPatientInfo(patient1, patientName1, Gender.MAN))
        dpmPatientInfoTable.save(dpmPatientInfo(patient2, patientName2, Gender.MAN))
        dpmPatientInfoTable.save(dpmPatientInfo(patient3, patientName3, Gender.MAN))

        dpmDoctorInfoTable.save(dpmDoctorInfo(doctorId1, "锦毛鼠1", Gender.WOMAN, hospitalId1))
        dpmDoctorInfoTable.save(dpmDoctorInfo(doctorId2, "锦毛鼠2", Gender.WOMAN, hospitalId2))

        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                AppIdUtil.nextId(),
                patient1,
                doctorId1,
                LocalDateTime.now()
            )
        )

        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                AppIdUtil.nextId(),
                patient2,
                doctorId2,
                LocalDateTime.now()
            )
        )
        dpmDoctorPatientTable.save(
            dpmDoctorPatient(
                AppIdUtil.nextId(),
                patient3,
                doctorId2,
                LocalDateTime.now()
            )
        )

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = listOf(hospitalId1),
                    regionIdSet = null
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = listOf(hospitalId1),
                regionIdSet = null
            )
        )

        val result1 = api.page(PatientPageRequest(pageNo = 1, pageSize = 100, orgIdSet = setOf(hospitalId1)))

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = listOf(hospitalId1),
                    regionIdSet = listOf(regionCode)
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = listOf(hospitalId1),
                regionIdSet = listOf(regionCode)
            )
        )
        val result2 = api.page(
            PatientPageRequest(
                pageNo = 1,
                pageSize = 100,
                orgIdSet = setOf(hospitalId1),
                regionIdSet = setOf(regionCode)
            )
        )

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = null,
                    regionIdSet = listOf(regionCode)
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = null,
                regionIdSet = listOf(regionCode)
            )
        )
        val result3 = api.page(PatientPageRequest(pageNo = 1, pageSize = 100, regionIdSet = setOf(regionCode)))

        Mockito.`when`(
            authRpcService.validOrgRegionPermission(
                ValidOrgRegionPermissionParam(
                    orgIdSet = listOf(hospitalId3),
                    regionIdSet = null
                )
            )
        ).thenReturn(
            ValidOrgRegionPermissionResult(
                isRun = true,
                orgIdSet = listOf(hospitalId3),
                regionIdSet = null
            )
        )
        val result4 = api.page(
            PatientPageRequest(
                pageNo = 1,
                pageSize = 100,
                orgIdSet = setOf(hospitalId3)
            )
        )

        // 只有一条
        Assertions.assertTrue(result1.total == 1L)
        val result1Find = result1._data?.find { it.name == patientName1 }
        Assertions.assertTrue(result1Find != null)

        Assertions.assertTrue(result2.total == 3L)
        val result11Find = result2._data?.find { it.name == patientName1 }
        val result12Find = result2._data?.find { it.name == patientName2 }
        val result13Find = result2._data?.find { it.name == patientName3 }
        Assertions.assertTrue(result11Find != null)
        Assertions.assertTrue(result12Find != null)
        Assertions.assertTrue(result13Find != null)

        // 有两条
        Assertions.assertTrue(result3.total == 3L)
        val nameFind21 = result3._data?.find { it.name == patientName1 }
        val nameFind22 = result3._data?.find { it.name == patientName2 }
        val nameFind23 = result3._data?.find { it.name == patientName3 }
        Assertions.assertTrue(nameFind21 != null)
        Assertions.assertTrue(nameFind22 != null)
        Assertions.assertTrue(nameFind23 != null)

        // 零条
        Assertions.assertTrue(result4.total == 0L)
    }

    private fun dpmDoctorInfo(
        doctorId: BigInteger,
        name: String,
        gender: Gender,
        hospitalId: BigInteger
    ): DpmDoctorInfo =
        DpmDoctorInfo.builder()
            .setKnId(doctorId)
            .setKnName(name)
            .setKnGender(gender.name)
            .setKnPhone("123")
            .setKnDeptName("妇科")
            .setKnHospitalId(hospitalId)
            .setKnHospitalName("北京儿童医院")
            .setKnAddress("北京西城区南礼士路")
            .setKnAuthId(BigInteger.ONE)
            .setKnCreatedBy(BigInteger.ONE)
            .setKnUpdatedBy(BigInteger.ONE)
            .build()

    private fun dpmDoctorPatient(
        id: BigInteger,
        patientId: BigInteger,
        doctorId: BigInteger,
        localDate: LocalDateTime,
    ): DpmDoctorPatient =
        DpmDoctorPatient.builder()
            .setKnId(id)
            .setKnPatientId(patientId)
            .setKnDoctorId(doctorId)
            .setKnBindDoctorDatetime(localDate)
            .setKnMessageStatus(MessageStatus.NONE.name)
            .setKnStatus(ToDoStatus.START_ASSESS.name)
            .setKnMessageNum(0)
            .build()

    private fun dpmPatientInfo(
        id: BigInteger,
        name: String,
        gender: Gender
    ): DpmPatientInfo = DpmPatientInfo.builder()
        .setKnId(id)
        .setKnName(name)
        .setKnGender(gender.name)
        .setKnPhone("123")
        .setKnBirthday(LocalDateTimeUtil.parse("2022-07-15T00:00:00"))
        .setKnIdCard("123")
        .setKnAuthId(BigInteger.ONE)
        .setKnCreatedBy(BigInteger.ONE)
        .setKnUpdatedBy(BigInteger.ONE)
        .build()
}

