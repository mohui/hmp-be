package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.api.UserDetailApi
import com.bjknrt.user.permission.centre.dao.RoleUserPermissionDao
import com.bjknrt.user.permission.centre.dao.UserIdentityTagDao
import com.bjknrt.user.permission.centre.dao.UserRoleOrgRegionDao
import com.bjknrt.user.permission.centre.vo.*
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.user.permission.centre.api.UserDetailController")
class UserDetailController(
    val userIdentityTagDao: UserIdentityTagDao,
    val userRoleOrgRegionDao: UserRoleOrgRegionDao,
    val roleUserPermissionDao: RoleUserPermissionDao
) : AppBaseController(), UserDetailApi {

    override fun getUserPermissions(body: BigInteger): UserPermissions {
        val userIdList = listOf(body)
        val roleCodeSet = userRoleOrgRegionDao.selectRoleByUserId(userIdList).map { it.knCode }.toSet()
        val regionIdSet = userRoleOrgRegionDao.selectRegionByUserId(userIdList).map { it.knCode }.toSet()
        val orgIdSet = userRoleOrgRegionDao.selectOrgByUserId(userIdList).map { it.knId }.toSet()
        val permissionCodeSet = roleUserPermissionDao.listPermissionByUserIds(userIdList).map { it.permissionCode }.toSet()
        val identityTagSet = userIdentityTagDao.selectIdentityTagByUserId(userIdList).map { it.knIdentifyTag }.toSet()
        return UserPermissions(roleCodeSet, orgIdSet, regionIdSet, permissionCodeSet, identityTagSet)
    }
}
