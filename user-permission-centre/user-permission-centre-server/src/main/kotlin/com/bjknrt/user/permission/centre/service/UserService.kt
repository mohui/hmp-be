package com.bjknrt.user.permission.centre.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.UpcsUser
import com.bjknrt.security.JwtUserDetails
import com.bjknrt.user.permission.centre.vo.LoginNoVerifyResponse
import com.bjknrt.user.permission.centre.vo.UserInfo
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionResult
import java.math.BigInteger
import java.time.LocalDate
import javax.servlet.http.HttpServletResponse

interface UserService {

    fun save(user: UpcsUser, isNew: Boolean): Id

    fun deleteUser(username: String)

    fun verifyPwd(pwd: String): Boolean

    fun changePassword(userId: Id, newPassword: String)

    fun userExists(username: String?): Boolean

    fun login(loginName: String, loginPwd: String, response: HttpServletResponse?): String
    fun logout(response: HttpServletResponse?)

    fun loadUserByUsername(username: String?): JwtUserDetails?

    fun reset(id: Id)

    fun loginNoVerify(phone: String, name: String, identityTags: Set<String>): LoginNoVerifyResponse

    fun page(
        page: Page,
        roleIds: Set<BigInteger>? = null,
        identityTags: Set<String>? = null,
        orgIds: Set<BigInteger>? = null,
        regionIds: Set<BigInteger>? = null,
        name: String? = null,
        createdBy: String? = null,
        createdAtStart: LocalDate? = null,
        createdAtEnd: LocalDate? = null,
    ): PagedResult<UserInfo>

    fun validOrgRegionPermission(orgIds: List<BigInteger>? = null, regionIds: List<BigInteger>? = null): ValidOrgRegionPermissionResult
}
