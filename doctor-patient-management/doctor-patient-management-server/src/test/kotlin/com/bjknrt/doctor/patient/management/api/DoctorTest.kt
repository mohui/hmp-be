package com.bjknrt.doctor.patient.management.api

import com.bjknrt.doctor.patient.management.AbstractContainerBaseTest
import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.DpmDoctorInfoTable
import com.bjknrt.doctor.patient.management.DpmDoctorPatientTable
import com.bjknrt.doctor.patient.management.service.DoctorService
import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorEditRequest
import com.bjknrt.doctor.patient.management.vo.DoctorLevel
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.api.UserApi
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
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


@AutoConfigureMockMvc
class DoctorTest : AbstractContainerBaseTest() {
    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var dpmDoctorInfoTable: DpmDoctorInfoTable

    @Autowired
    lateinit var dpmDoctorPatientTable: DpmDoctorPatientTable

    @Autowired
    lateinit var doctorService: DoctorService

    @MockBean
    lateinit var authRpcService: UserApi

    @BeforeEach
    fun before() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = AppIdUtil.nextId(),
            roleCodeSet = setOf("SUPER_ADMIN")
        )
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

    }

    @Test
    fun doctorAddTest() {

        val request = DoctorAddRequest(
            authId = AppIdUtil.nextId(),
            name = "www",
            gender = Gender.MAN,
            deptName = "儿科",
            address = "北京西城菜市口",
            phone = "110110110",
            doctorLevel = DoctorLevel.ASSOCIATE_CHIEF_PHYSICIAN,
            hospitalId = AppIdUtil.nextId(),
            hospitalName = "北京儿童医院"
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/doctor/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val doctorInfo = dpmDoctorInfoTable.select()
            .where(DpmDoctorInfoTable.KnDel eq false.arg)
            .where(DpmDoctorInfoTable.KnName eq "www".arg)
            .findOne()

        Assertions.assertEquals(true, doctorInfo != null)
        Assertions.assertEquals(true, doctorInfo?.knName == request.name)
        Assertions.assertEquals(true, doctorInfo?.knDeptName == request.deptName)
        Assertions.assertEquals(true, doctorInfo?.knAddress == request.address)
    }

    @Test
    fun doctorEditTest() {

        dpmDoctorInfoTable.deleteByKnId(BigInteger.valueOf(4))

        val result =
            dpmDoctorInfo(BigInteger.valueOf(4), "doctorEditTest")

        dpmDoctorInfoTable.save(result)

        val request = DoctorEditRequest(
            result.knId,
            "Wry",
            Gender.valueOf(result.knGender),
            "北京医院",
            "北京西城菜市口",
            "1234567",
            DoctorLevel.ASSOCIATE_CHIEF_PHYSICIAN
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/doctor/edit")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val doctorInfo = dpmDoctorInfoTable.select()
            .where(DpmDoctorInfoTable.KnDel eq false.arg)
            .where(DpmDoctorInfoTable.KnName eq "Wry".arg)
            .findOne()
        Assertions.assertEquals(true, doctorInfo != null)
        Assertions.assertEquals(true, doctorInfo?.knName == request.name)
        Assertions.assertEquals(true, doctorInfo?.knDeptName == request.deptName)
        Assertions.assertEquals(true, doctorInfo?.knAddress == request.address)
    }

    @Test
    fun doctorDeleteTest() {

        var doctorId = AppIdUtil.nextId()
        val result = dpmDoctorInfo(doctorId, "doctorDeleteTest")
        dpmDoctorInfoTable.insertWithoutNull(result)

        Mockito.doNothing().`when`(authRpcService).del(doctorId)

        //删除
        mvc.perform(
            MockMvcRequestBuilders.post("/doctor/remove")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(result.knId))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


        val deleteDoctorInfo = dpmDoctorInfoTable.select()
            .where(DpmDoctorInfoTable.KnDel eq true.arg)
            .where(DpmDoctorInfoTable.KnId eq doctorId.arg)
            .findOne()

        Assertions.assertEquals(true, deleteDoctorInfo != null)
        Assertions.assertEquals(true, deleteDoctorInfo?.knId == result.knId)

        doctorId = AppIdUtil.nextId()

        val request = DoctorAddRequest(
            authId = AppIdUtil.nextId(),
            name = "www",
            gender = Gender.MAN,
            deptName = "儿科",
            address = "北京西城菜市口",
            phone = "110110110",
            doctorLevel = DoctorLevel.ASSOCIATE_CHIEF_PHYSICIAN,
            hospitalId = AppIdUtil.nextId(),
            hospitalName = "北京儿童医院"
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/doctor/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


        //绑定的患者数量应该为0
        val count = dpmDoctorPatientTable.select().where(DpmDoctorPatientTable.KnDoctorId eq doctorId.arg).count()
        Assertions.assertEquals(0, count)


    }

    @Test
    fun doctorInfoTest() {
        val id = AppIdUtil.nextId()
        dpmDoctorInfoTable.deleteByKnId(id)
        val result = dpmDoctorInfo(id, "doctorInfoTest")
        dpmDoctorInfoTable.save(result)

        val response = doctorService.getDoctorInfo(id)

        mvc.perform(
            MockMvcRequestBuilders.post("/doctor/info")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(result.knId))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)))
    }

    private fun dpmDoctorInfo(
        id: BigInteger,
        name: String
    ): DpmDoctorInfo = DpmDoctorInfo.builder()
        .setKnId(id)
        .setKnName(name)
        .setKnGender(Gender.WOMAN.name)
        .setKnPhone("123")
        .setKnDeptName("妇科")
        .setKnHospitalId(BigInteger.ONE)
        .setKnHospitalName("北京儿童医院")
        .setKnAddress("北京西城区南礼士路")
        .setKnAuthId(BigInteger.ONE)
        .setKnCreatedBy(BigInteger.ONE)
        .setKnUpdatedBy(BigInteger.ONE)
        .build()

}

