package com.bjknrt.question.answering.system.api

import com.bjknrt.doctor.patient.management.api.DoctorPatientApi
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.EditRequest
import com.bjknrt.doctor.patient.management.vo.ToDoStatus
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.FromTag
import com.bjknrt.question.answering.system.AbstractContainerBaseTest
import com.bjknrt.question.answering.system.vo.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
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
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDateTime

@AutoConfigureMockMvc
class EvaluateTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var patientInfoRpcService: PatientApi

    @MockBean
    lateinit var doctorPatientInfoRpcService: DoctorPatientApi

    @MockBean
    lateinit var indicatorRpcService: IndicatorApi


    @BeforeEach
    fun before() {
        // 获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()
    }

    /**
     * To test EvaluateApi.diseaseEvaluate
     */
    @Test
    fun addTest() {
        val patientId = AppIdUtil.nextId()

        val request = DiseaseEvaluateRequest(
            examinationPaperId = AppIdUtil.nextId(),
            examinationPaperCode = "CODE${AppIdUtil.nextId()}",
            patientId = patientId,
            age = 18,
            gender = Gender.WOMAN,
            patientHeight = BigDecimal.valueOf(167),
            patientWeight = BigDecimal.valueOf(60),
            patientWaistline = BigDecimal.valueOf(100),
            isSmoking = true,
            isPmhEssentialHypertension = false,
            isPmhTypeTwoDiabetes = false,
            isPmhCerebralInfarction = false,
            isPmhCoronaryHeartDisease = false,
            isPmhCopd = false,
            isPmhDyslipidemiaHyperlipidemia = false,
            isPmhDiabeticNephropathy = false,
            isPmhDiabeticRetinopathy = false,
            isPmhDiabeticFoot = false,
            isFhEssentialHypertension = false,
            isFhTypeTwoDiabetes = false,
            isFhCerebralInfarction = false,
            isFhCoronaryHeartDisease = false,
            isFhCopd = false,
            isSymptomDizzy = false,
            isSymptomChestPain = false,
            isSymptomChronicCough = false,
            isSymptomWeightLoss = false,
            isSymptomGiddiness = false,
            isSymptomNone = false,
            systolicPressure = BigDecimal.valueOf(85),
            diastolicBloodPressure = BigDecimal.valueOf(120),
            fastingBloodSugar = BigDecimal.valueOf(4.5),
            serumTch = BigDecimal.valueOf(4.2),
        )

        mockSyncPatient(request)

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, DiseaseEvaluateResponse::class.java)

        Assertions.assertEquals(true, response.synthesisDiseaseTag == PatientSynthesisTag.LOW)
        Assertions.assertEquals(true, response.hypertensionDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.diabetesDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.acuteCoronaryDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.cerebralStrokeDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.copdDiseaseTag == PatientTag.LOW)
    }

    @Test
    fun getEvaluateInfo() {
        val patientId = AppIdUtil.nextId()


        val request = getRequest(patientId)

        mockSyncPatient(request)

        mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/getEvaluateInfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(patientId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, DiseaseEvaluateResponse::class.java)

        Assertions.assertEquals(true, response.synthesisDiseaseTag == PatientSynthesisTag.LOW)
        Assertions.assertEquals(true, response.hypertensionDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.diabetesDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.acuteCoronaryDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.cerebralStrokeDiseaseTag == PatientTag.LOW)
        Assertions.assertEquals(true, response.copdDiseaseTag == PatientTag.LOW)
    }

    @Test
    fun getEvaluateOption() {
        val patientId = AppIdUtil.nextId()

        val request = getRequest(patientId)

        mockSyncPatient(request)

        mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val optionRequest =
            DiseaseOptionRequest(patientId, LocalDateTime.now().plusDays(-1), LocalDateTime.now())

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/getDiseaseOption")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(optionRequest)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString


        val response = objectMapper.readValue(resultString, object : TypeReference<List<DiseaseOption>>() {})

        Assertions.assertEquals(1, response.size)
        Assertions.assertEquals(false, response[0].symptomNone)
        Assertions.assertEquals(false, response[0].symptomDizzy)

    }


    @Test
    fun getLastEvaluateOption() {
        val patientId = AppIdUtil.nextId()

        val request = getRequest(patientId)

        mockSyncPatient(request)

        mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/evaluate/getLastDiseaseOption")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(patientId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString


        val response = objectMapper.readValue(resultString, DiseaseOption::class.java)

        Assertions.assertEquals(false, response.symptomNone)
        Assertions.assertEquals(false, response.symptomDizzy)

    }

    fun mockSyncPatient(request: DiseaseEvaluateRequest) {
        Mockito.doNothing().`when`(patientInfoRpcService).edit(
            EditRequest(
                id = request.patientId,
                synthesisDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientSynthesisTag.LOW,
                hypertensionDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.LOW,
                diabetesDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.LOW,
                acuteCoronaryDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.LOW,
                cerebralStrokeDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.LOW,
                copdDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.LOW,
                pmhEssentialHypertension = false,
                pmhTypeTwoDiabetes = false,
                pmhCerebralInfarction = false,
                pmhCoronaryHeartDisease = false,
                pmhCopd = false,
                pmhDyslipidemiaHyperlipidemia = false,
                pmhDiabeticNephropathy = false,
                pmhDiabeticRetinopathy = false,
                pmhDiabeticFoot = false,
            )
        )
        Mockito.doNothing().`when`(doctorPatientInfoRpcService).updateStatus(
            UpdateStatusRequest(
                patientId = request.patientId,
                status = ToDoStatus.SUCCESS
            )
        )
        Mockito.doNothing().`when`(indicatorRpcService).batchAddIndicator(
            BatchIndicator(
                patientId = request.patientId,
                knBodyHeight = request.patientHeight,
                knBodyWeight = request.patientWeight,
                knWaistline = request.patientWaistline,
                knSystolicBloodPressure = request.systolicPressure,
                knDiastolicBloodPressure = request.diastolicBloodPressure,
                knFastingBloodSandalwood = request.fastingBloodSugar,
                knTotalCholesterol = request.serumTch,
                fromTag = FromTag.PATIENT_SELF
            )
        )
    }


    fun getRequest(patientId: BigInteger): DiseaseEvaluateRequest {
        return DiseaseEvaluateRequest(
            examinationPaperId = AppIdUtil.nextId(),
            examinationPaperCode = "CODE${AppIdUtil.nextId()}",
            patientId = patientId,
            age = 18,
            gender = Gender.WOMAN,
            patientHeight = BigDecimal.valueOf(167),
            patientWeight = BigDecimal.valueOf(60),
            patientWaistline = BigDecimal.valueOf(100),
            isSmoking = true,
            isPmhEssentialHypertension = false,
            isPmhTypeTwoDiabetes = false,
            isPmhCerebralInfarction = false,
            isPmhCoronaryHeartDisease = false,
            isPmhCopd = false,
            isPmhDyslipidemiaHyperlipidemia = false,
            isPmhDiabeticNephropathy = false,
            isPmhDiabeticRetinopathy = false,
            isPmhDiabeticFoot = false,
            isFhEssentialHypertension = false,
            isFhTypeTwoDiabetes = false,
            isFhCerebralInfarction = false,
            isFhCoronaryHeartDisease = false,
            isFhCopd = false,
            isSymptomDizzy = false,
            isSymptomChestPain = false,
            isSymptomChronicCough = false,
            isSymptomWeightLoss = false,
            isSymptomGiddiness = false,
            isSymptomNone = false,
            systolicPressure = BigDecimal.valueOf(85),
            diastolicBloodPressure = BigDecimal.valueOf(120),
            fastingBloodSugar = BigDecimal.valueOf(4.5),
            serumTch = BigDecimal.valueOf(4.2),
        )
    }
}
