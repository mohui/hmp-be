package com.bjknrt.doctor.patient.management.api

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.doctor.patient.management.*
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.operation.log.api.LogApi
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.hamcrest.Matchers
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
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDateTime


@AutoConfigureMockMvc
class DoctorPatientTest : AbstractContainerBaseTest() {
    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var dpmDoctorPatientTable: DpmDoctorPatientTable

    @Autowired
    lateinit var dpmDoctorInfoTable: DpmDoctorInfoTable

    @Autowired
    lateinit var dpmPatientInfoTable: DpmPatientInfoTable

    @MockBean
    lateinit var operationLogClient: LogApi

    @MockBean
    lateinit var manageRpcService: ManageApi

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

    }

    @Test
    fun bindTest() {
        val hospitalId = AppIdUtil.nextId()

        val doctorId = AppIdUtil.nextId()

        dpmDoctorInfoTable.save(
            dpmDoctorInfo(doctorId, hospitalId)

        )

        val patientId = AppIdUtil.nextId()
        dpmPatientInfoTable.save(
            dpmPatientInfo(patientId)
        )

        val request = BindDoctorRequest(patientId, doctorId)
        mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/bind")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val doctorPatient = dpmDoctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq patientId.arg)
            .findOne()
        Assertions.assertEquals(true, doctorPatient != null)
        Assertions.assertEquals(true, doctorPatient?.knDoctorId == doctorId)
    }

    @Test
    fun unbindTest() {
        val hospitalId = AppIdUtil.nextId()
        val doctorId = AppIdUtil.nextId()
        val patientId = AppIdUtil.nextId()

        Mockito.doNothing().`when`(manageRpcService).pause(patientId)

        dpmDoctorInfoTable.save(
            dpmDoctorInfo(doctorId, hospitalId)
        )


        dpmPatientInfoTable.save(
            dpmPatientInfo(patientId)
        )

        dpmDoctorPatientTable.save(
            DpmDoctorPatient(
                AppIdUtil.nextId(),
                patientId,
                doctorId,
                LocalDateTime.now(),
                ToDoStatus.START_ASSESS.value,
                MessageStatus.NONE.value,
                0
            )
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/unbind")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(patientId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


        mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/unbind")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(AppIdUtil.nextId())
                )
        )
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(AppSpringUtil.getMessage("doctor-patient.patient-not-bind-doctor")))
            )

    }


    @Test
    fun updateStatusTest() {
        val hospitalId = AppIdUtil.nextId()
        val patientId = AppIdUtil.nextId()
        val doctorId = AppIdUtil.nextId()

        dpmDoctorInfoTable.save(
            dpmDoctorInfo(doctorId, hospitalId)
        )
        dpmPatientInfoTable.save(
            dpmPatientInfo(patientId)
        )
        val dpId = AppIdUtil.nextId()
        dpmDoctorPatientTable.save(
            DpmDoctorPatient(
                dpId,
                patientId,
                doctorId,
                LocalDateTime.now(),
                ToDoStatus.START_ASSESS.value,
                MessageStatus.NONE.value,
                0
            )
        )

        val request = UpdateStatusRequest(patientId, ToDoStatus.SUCCESS)

        mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/status")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val doctorPatient = dpmDoctorPatientTable.findByKnId(dpId)

        Assertions.assertEquals(true, doctorPatient != null)
        Assertions.assertEquals(ToDoStatus.SUCCESS, doctorPatient?.knStatus?.let { ToDoStatus.valueOf(it) })


    }

    @Test
    fun getBindDoctorTest() {
        val patientId = AppIdUtil.nextId()

        val dpmPatientInfo = dpmPatientInfo(patientId)

        dpmPatientInfoTable.save(dpmPatientInfo)

        var resultString = mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/bindDoctorInfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(patientId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        var doctorInfoResponse = objectMapper.readValue(resultString, BindDoctorInfoResponse::class.java)

        Assertions.assertEquals(true, doctorInfoResponse != null)
        Assertions.assertEquals(true, doctorInfoResponse.doctorId == null)

        val hospitalId = AppIdUtil.nextId()
        val doctorId = AppIdUtil.nextId()
        val hospitalName = "北京儿童医院"

        dpmDoctorInfoTable.save(
            dpmDoctorInfo(doctorId, hospitalId)
        )
        val dpId = AppIdUtil.nextId()
        dpmDoctorPatientTable.save(
            DpmDoctorPatient(
                dpId,
                patientId,
                doctorId,
                LocalDateTime.now(),
                ToDoStatus.START_ASSESS.value,
                MessageStatus.NONE.value,
                0
            )
        )

        resultString = mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/bindDoctorInfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(patientId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        doctorInfoResponse = objectMapper.readValue(resultString, BindDoctorInfoResponse::class.java)

        Assertions.assertEquals(true, doctorInfoResponse != null)
        Assertions.assertEquals(hospitalId, doctorInfoResponse.doctorHospitalId)
        Assertions.assertEquals(hospitalName, doctorInfoResponse.doctorHospitalName)
    }

    @Test
    fun getBindPatientNum() {
        val doctorId = AppIdUtil.nextId()
        val patientId = AppIdUtil.nextId()
        dpmDoctorPatientTable.save(
            DpmDoctorPatient(
                AppIdUtil.nextId(),
                patientId,
                doctorId,
                LocalDateTime.now(),
                ToDoStatus.START_ASSESS.value,
                MessageStatus.NONE.value,
                0
            )
        )
        val result = mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/bindPatientNum")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(doctorId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val num = result.response.contentAsString.toLong()
        Assertions.assertEquals(1, num)

        val result2 = mvc.perform(
            MockMvcRequestBuilders.post("/doctorPatient/bindPatientNum")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(AppIdUtil.nextId())
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val num2 = result2.response.contentAsString.toLong()
        Assertions.assertEquals(0, num2)
    }

    private fun dpmDoctorInfo(
        doctorId: BigInteger,
        hospitalId: BigInteger
    ): DpmDoctorInfo =
        DpmDoctorInfo.builder()
            .setKnId(doctorId)
            .setKnName("zhangsan")
            .setKnGender(Gender.WOMAN.name)
            .setKnPhone("123")
            .setKnDeptName("妇科")
            .setKnHospitalId(hospitalId)
            .setKnHospitalName("北京儿童医院")
            .setKnAddress("北京西城区南礼士路")
            .setKnAuthId(BigInteger.ONE)
            .setKnCreatedBy(BigInteger.ONE)
            .setKnUpdatedBy(BigInteger.ONE)
            .build()

    private fun dpmPatientInfo(patientId: BigInteger): DpmPatientInfo =
        DpmPatientInfo.builder()
            .setKnId(patientId)
            .setKnName("t2")
            .setKnGender(Gender.MAN.name)
            .setKnPhone("123")
            .setKnBirthday(LocalDateTimeUtil.parse("2022-07-15T00:00:00"))
            .setKnIdCard("123")
            .setKnAuthId(BigInteger.ONE)
            .setKnCreatedBy(BigInteger.ONE)
            .setKnUpdatedBy(BigInteger.ONE)
            .build()
}

