package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.AbstractContainerBaseTest
import com.bjknrt.user.permission.centre.UpcsOrg
import com.bjknrt.user.permission.centre.UpcsOrgTable
import com.bjknrt.user.permission.centre.vo.Org
import com.bjknrt.user.permission.centre.vo.OrgCodePageParam
import com.bjknrt.user.permission.centre.vo.OrgPageParam
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@TestMethodOrder(MethodOrderer.MethodName::class)
class OrgTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: OrgApi

    @Autowired
    lateinit var orgTable: UpcsOrgTable
    val parentId = AppIdUtil.nextId()
    val roleCode = "R${AppIdUtil.nextId()}"

    val obj = UpcsOrg.builder()
        .setKnId(AppIdUtil.nextId())
        .setKnRegionCode(BigInteger.valueOf(123))
        .setIsDel(false)
        .setKnName("医院")
        .setKnSort(100)
        .setKnOrgLevel("")
        .setKnCreatedBy(BigInteger.valueOf(100))
        .setKnUpdatedBy(BigInteger.valueOf(100))
        .build()

    @BeforeEach
    fun beforeEach() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.ZERO,
            nickName = "nickName",
            loginName = "loginName",
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE)
        )
    }


    @Transactional
    @Test
    fun test() {
        orgTable.insertWithoutNull(obj)

        val orgPageParam = OrgPageParam(
            name = "医院",
            pageNo = 1,
            pageSize = 100
        )
        val response: PagedResult<Org> = api.page(orgPageParam)
        Assertions.assertTrue(response.total > 0)

        // 测试分页支持行政管理员查询
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.ZERO,
            nickName = "nickName",
            loginName = "loginName",
            roleCodeSet = setOf(roleCode),
            regionIdSet = setOf(BigInteger.valueOf(510131000000))
        )
        val regionParam = OrgPageParam(
            name = "卫生",
            pageNo = 1,
            pageSize = 100
        )
        val regionResult = api.page(regionParam)
        Assertions.assertTrue(regionResult.total > 0)

        val pageResult = api.pageOrgByAdministrativeCode(
            OrgCodePageParam(
                code = 320685000000.toBigInteger(),
                pageNo = 1,
                pageSize = 100
            )
        )
        Assertions.assertTrue(pageResult.total > 0)
    }
}
