package com.bjknrt.health.scheme.api

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.HYPERTENSION_SERVICE_CODE
import com.bjknrt.health.scheme.vo.ClockInRequest
import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.ManageStage
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.BatchClockInParams
import com.bjknrt.medication.remind.vo.HealthPlan
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import com.bjknrt.user.permission.centre.vo.HealthServiceExistsRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
class ClockInTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mvc: MockMvc

    @Autowired
    lateinit var hsHealthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable

    @Autowired
    lateinit var hsHsmHealthPlanTable: HsHsmHealthPlanTable

    @MockBean
    lateinit var patientClient: PatientApi

    @MockBean
    lateinit var healthPlanClient: HealthPlanApi

    @MockBean
    lateinit var healthServiceClient: HealthServiceApi

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()
    }


    @Test
    fun clockInTest() {
        val patientId = AppIdUtil.nextId()
        val healthPlanId = AppIdUtil.nextId()
        val managementId = AppIdUtil.nextId()

        val patientInfo =
            PatientInfoResponse(patientId, "zs", Gender.MAN, "123", "123", LocalDateTime.of(1997, 10, 15, 0, 0), 24)
        Mockito.`when`(
            patientClient.getPatientInfo(patientId)
        ).thenReturn(
            patientInfo
        )

        Mockito.`when`(
            healthPlanClient.batchIdClockIn(BatchClockInParams(ids = listOf(healthPlanId)))
        ).thenReturn(
            listOf(
                HealthPlan(
                    id = AppIdUtil.nextId(),
                    name = "行为习惯随访",
                    type = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                    clockIn = true,
                    cycleStartTime = LocalDateTime.now())
            )
        )

        Mockito.`when`(
            healthServiceClient.existsService(
                HealthServiceExistsRequest(
                    patientId,
                    HYPERTENSION_SERVICE_CODE
                )
            )
        ).thenReturn(
            true
        )


        hsHealthSchemeManagementInfoTable.save(
            HsHealthSchemeManagementInfo.builder()
                .setKnId(managementId)
                .setKnStartDate(LocalDate.now().plusDays(-7))
                .setKnCreatedBy(AppIdUtil.nextId())
                .setKnPatientId(patientId)
                .setKnHealthManageType(HealthManageType.HYPERTENSION.name)
                .setKnDiseaseExistsTag("")
                .setKnJobId(null)
                .setKnEndDate(LocalDate.now().plusDays(7))
                .setKnReportOutputDate(LocalDate.now().plusDays(8))
                .setIsDel(false)
                .setKnCreatedAt(LocalDateTime.now())
                .setKnManagementStage(ManageStage.INITIAL_STAGE.name)
                .build()
        )


        hsHsmHealthPlanTable.save(
            HsHsmHealthPlan.builder()
                .setKnId(healthPlanId)
                .setKnPlanType(HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name)
                .setKnStartDate(LocalDate.now().plusDays(-7).atStartOfDay())
                .setKnSchemeManagementId(managementId)
                .setKnForeignPlanId(AppIdUtil.nextId())
                .setIsDel(false)
                .setKnEndDate(LocalDate.now().plusDays(7).atStartOfDay())
                .setKnJobId(null)
                .setKnForeignPlanFrequencyIds("")
                .build()
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/clockIn/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(
                        ClockInRequest(
                            patientId,
                            com.bjknrt.health.scheme.vo.HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                            LocalDateTime.now()
                        )
                    )
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }
}
