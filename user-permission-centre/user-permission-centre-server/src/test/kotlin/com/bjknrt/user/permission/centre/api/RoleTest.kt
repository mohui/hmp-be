package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.AbstractContainerBaseTest
import com.bjknrt.user.permission.centre.UpcsRoleTable
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

class RoleTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: RoleApi

    @Autowired
    lateinit var roleTable: UpcsRoleTable
    companion object {
        val roleId = AppIdUtil.nextId()
        val roleName = "角色名称"
        // 修改的角色名称
        val updateRoleName = "修改的角色名称"
        // 菜单权限
        val permissions = listOf(
            PermissionEnum.ROLE_MANAGE,
            PermissionEnum.ALL_PATIENT,
            PermissionEnum.HOME
        )
        val saveRolePermissionParam: SaveRolePermissionParam = SaveRolePermissionParam(
            name = roleName,
            permissions = permissions,
            id = roleId
        )

        // 修改的角色变量
        val updateRole: SaveRolePermissionParam = SaveRolePermissionParam(
            name = updateRoleName,
            permissions = saveRolePermissionParam.permissions,
            id = saveRolePermissionParam.id
        )
    }

    @BeforeEach
    fun beforeEach() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = BigInteger.ZERO,
            nickName = "nickName",
            loginName = "loginName",
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE)
        )
    }

    /**
     * To test RoleApi
     */
    @Transactional
    @Test
    fun test() {
        // 测试添加
        api.saveRolePermission(saveRolePermissionParam)

        // 校验角色详情
        val roleDetail = api.roleDetail(RoleDetailParam(id = roleId))
        Assertions.assertEquals(roleName, roleDetail.name)


        val firstQuery = api.page(PageQueryRoleParam(pageNo = 1, pageSize = 10))

        // 测试修改
        api.saveRolePermission(updateRole)
        val updateFindOne = roleTable.select().where(UpcsRoleTable.KnId eq roleId).findOne()
        // 前后ID相同,代表是同一条数据
        Assertions.assertEquals(roleDetail.id, updateFindOne?.knId)
        // 判断修改后的名称是否是要修改的名称
        Assertions.assertEquals(updateRoleName, updateFindOne?.knName)

        // 测试启用禁用
        api.updUsed(UpdUsedParam(id = roleId, isUsed = false))
        val usedRoleFind = roleTable.select().where(UpcsRoleTable.KnId eq roleId).findOne()
        Assertions.assertTrue(usedRoleFind?.isUsed == false)

        // 测试删除
        api.del(roleId)
        val roleFind = roleTable.select().where(UpcsRoleTable.KnId eq roleId).findOne()
        // 判断删除字段是否是false
        Assertions.assertTrue(roleFind?.isDel == true)

        val result = api.page(PageQueryRoleParam(pageNo = 1, pageSize = 10))
        Assertions.assertEquals(firstQuery.total - 1, result.total)

        // 角色列表,不带数字
        val rolePageResult = api.rolePage(Page(pageNo = 1, pageSize = 10))
        Assertions.assertEquals(result.total, rolePageResult.total)
    }

}
