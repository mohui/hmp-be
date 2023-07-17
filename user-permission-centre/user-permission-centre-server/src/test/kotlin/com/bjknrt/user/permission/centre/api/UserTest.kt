package com.bjknrt.user.permission.centre.api

import com.bjknrt.doctor.patient.management.api.DoctorApi
import com.bjknrt.doctor.patient.management.api.DoctorPatientApi
import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorLevel
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.AbstractContainerBaseTest
import com.bjknrt.user.permission.centre.UpcsUserTable
import com.bjknrt.user.permission.centre.vo.IdentityTag
import com.bjknrt.user.permission.centre.vo.PageQueryUserParam
import com.bjknrt.user.permission.centre.vo.SaveUserRoleOrgRequest
import com.bjknrt.user.permission.centre.vo.UserExtendInfo
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

class UserTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: UserApi
    val roleCode = "R${AppIdUtil.nextId()}"

    @Autowired
    lateinit var userTable: UpcsUserTable

    @MockBean
    lateinit var doctorInfoRpcService: DoctorApi

    @MockBean
    lateinit var doctorPatientRpcService: DoctorPatientApi

    @BeforeEach
    fun before() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = AppIdUtil.nextId(),
            nickName = "nick",
            loginName = "admin",
            roleCodeSet = setOf(roleCode, "SUPER_ADMIN"),
            regionIdSet = setOf(BigInteger.valueOf(110000000000)),
            orgIdSet = setOf(BigInteger.ONE)
        )
    }

    @Transactional
    @Test
    fun testAll() {
        val deptName = "儿科"
        val address = "北京"
        val hospitalId = BigInteger.valueOf(110000000000)
        val hospitalName = "北京市"
        val insertParam =
            SaveUserRoleOrgRequest(
                name = "name",
                loginName = "loginName",
                phone = "11011011011",
                gender = com.bjknrt.user.permission.centre.vo.Gender.MAN,
                address = address,
                extendInfo = UserExtendInfo(
                    identityTagList = setOf(IdentityTag.DOCTOR),
                    roleIdList = setOf(BigInteger.valueOf(1), BigInteger.valueOf(2)),
                    deptName = deptName,
                    doctorLevel = com.bjknrt.user.permission.centre.vo.DoctorLevel.RESIDENT_PHYSICIAN,
                    regionId = BigInteger.valueOf(110000000000)
                )
            )

        val doctorAddRequest = DoctorAddRequest(
            authId = AppIdUtil.nextId(),
            name = insertParam.name,
            gender = Gender.MAN,
            deptName = deptName,
            address = address,
            phone = insertParam.phone,
            doctorLevel = insertParam.extendInfo?.doctorLevel?.let { DoctorLevel.valueOf(it.name) }
                ?: DoctorLevel.OTHER,
            hospitalId = hospitalId,
            hospitalName = hospitalName
        )

        Mockito.doNothing().`when`(doctorInfoRpcService).addDoctor(any())
        Mockito.doNothing().`when`(doctorInfoRpcService).deleteDoctor(any())
        Mockito.`when`(doctorPatientRpcService.bindPatientNum(any())).thenReturn(0L)

        // 新增
        api.saveUserRoleOrg(insertParam)

        val frequencyCaptor: ArgumentCaptor<DoctorAddRequest> = ArgumentCaptor.forClass(DoctorAddRequest::class.java)
        Mockito.verify(doctorInfoRpcService).addDoctor(capture(frequencyCaptor))

        Assertions.assertEquals(doctorAddRequest.name, frequencyCaptor.value.name)
        Assertions.assertEquals(doctorAddRequest.phone, frequencyCaptor.value.phone)
        Assertions.assertEquals(doctorAddRequest.doctorLevel, frequencyCaptor.value.doctorLevel)
        Assertions.assertEquals(doctorAddRequest.hospitalId, frequencyCaptor.value.hospitalId)
        Assertions.assertEquals(doctorAddRequest.hospitalName, frequencyCaptor.value.hospitalName)

        val userId = frequencyCaptor.value.authId
        val user = userTable.findByKnId(userId)
        Assertions.assertEquals(false, user == null)
        Assertions.assertEquals(userId, user?.knId)
        Assertions.assertEquals(frequencyCaptor.value.name, user?.knName)
        Assertions.assertEquals(frequencyCaptor.value.phone, user?.knPhone)
        Assertions.assertEquals(frequencyCaptor.value.address, user?.knAddress)
        // 分页查询
        val pagedResult = api.page(Page(1, 10))
        Assertions.assertTrue(pagedResult.total > 0)


        val pageByRoleCodes = api.pageList(PageQueryUserParam(1, 10))
        Assertions.assertTrue(pageByRoleCodes.total > 0)

        // 修改
        val row = pagedResult._data?.first { it.phone == "11011011011" }!!
        val updateParam = SaveUserRoleOrgRequest(
            name = row.name + "update",
            loginName = row.loginName,
            phone = "11011011011",
            gender = com.bjknrt.user.permission.centre.vo.Gender.MAN,
            id = row.id,
        )
        api.saveUserRoleOrg(updateParam)

        // id查询
        val listByIds = api.listByIds(listOf(row.id))
        Assertions.assertEquals(1, listByIds.size)
        Assertions.assertEquals(updateParam.name, listByIds[0].name)

        // 登录名重复验证
        val reParam = SaveUserRoleOrgRequest(
            name = "name",
            loginName = updateParam.loginName,
            phone = "11011011011",
            gender = com.bjknrt.user.permission.centre.vo.Gender.MAN,
        )
        Assertions.assertThrows(
            MsgException::class.java,
            { api.saveUserRoleOrg(reParam) },
            AppSpringUtil.getMessage("user.register.name.repetition")
        )
    }

    @Transactional
    @Test
    fun saveUserRoleOrgRequest() {
        val deptName = "儿科"
        val address = "北京"
        val hospitalId = BigInteger.valueOf(110000000000)
        val hospitalName = "北京市"
        val loginName = "loginName" + AppIdUtil.nextId()
        //没有医生或护士的身份标签
        val insertParam =
            SaveUserRoleOrgRequest(
                name = "name",
                loginName = loginName,
                phone = "110110110112",
                gender = com.bjknrt.user.permission.centre.vo.Gender.MAN,
                address = address,
                extendInfo = UserExtendInfo(
                    identityTagList = setOf(IdentityTag.REGION_ADMIN),
                    roleIdList = setOf(BigInteger.valueOf(1), BigInteger.valueOf(2)),
                    deptName = deptName,
                    doctorLevel = com.bjknrt.user.permission.centre.vo.DoctorLevel.RESIDENT_PHYSICIAN,
                    regionId = BigInteger.valueOf(110000000000)
                )
            )
        val doctorAddRequest = DoctorAddRequest(
            authId = AppIdUtil.nextId(),
            name = insertParam.name,
            gender = Gender.MAN,
            deptName = deptName,
            address = address,
            phone = insertParam.phone,
            doctorLevel = insertParam.extendInfo?.doctorLevel?.let { DoctorLevel.valueOf(it.name) }
                ?: DoctorLevel.OTHER,
            hospitalId = hospitalId,
            hospitalName = hospitalName
        )
        api.saveUserRoleOrg(insertParam)


        val user = userTable.select()
            .where(UpcsUserTable.IsDel eq false)
            .where(UpcsUserTable.KnLoginName eq loginName)
            .where(UpcsUserTable.KnPhone eq insertParam.phone)
            .findOne()

        //之前没有有医生或护士的身份标签，现在有
        var updateParam = insertParam.copy(
            id = user!!.knId,
            extendInfo = insertParam.extendInfo?.copy(
                identityTagList = setOf(
                    IdentityTag.ORG_ADMIN,
                    IdentityTag.DOCTOR
                )
            )
        )
        Mockito.doNothing().`when`(doctorInfoRpcService).addDoctor(doctorAddRequest)
        api.saveUserRoleOrg(updateParam)


        //之前有医生或护士的身份标签，现在也有，但是改了其他
        updateParam = updateParam.copy(name = "zs")
        api.saveUserRoleOrg(updateParam)

        //之前有医生或护士的身份标签，现在没有
        updateParam = updateParam.copy(
            extendInfo = updateParam.extendInfo?.copy(
                identityTagList = setOf(
                    IdentityTag.ORG_ADMIN
                )
            )
        )
        Mockito.`when`(doctorPatientRpcService.bindPatientNum(any())).thenReturn(2L)
        Mockito.doNothing().`when`(doctorInfoRpcService).deleteDoctor(any())
        Assertions.assertThrows(
            MsgException::class.java,
            { api.saveUserRoleOrg(updateParam) },
            AppSpringUtil.getMessage("doctor-patient.bind-patient-num-is-not-zero")
        )
    }
}
