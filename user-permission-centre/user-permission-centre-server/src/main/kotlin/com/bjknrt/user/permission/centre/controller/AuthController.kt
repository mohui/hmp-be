package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.user.permission.centre.UpcsUser
import com.bjknrt.user.permission.centre.api.AuthApi
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.service.UserService
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.kato.common.exception.KatoException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.user.permission.centre.api.AuthController")
class AuthController(
    val userService: UserService
) : AppBaseController(), AuthApi {
    companion object {
        val identityTagSet = setOf(
            IdentityTag.PATIENT,
            IdentityTag.DOCTOR,
            IdentityTag.NURSE,
            IdentityTag.REGION_ADMIN,
            IdentityTag.ORG_ADMIN
        )
    }

    override fun login(loginParam: LoginParam): String {
        return getResponse()?.let {
            return userService.login(loginParam.loginName, loginParam.loginPwd, it)
        } ?: throw MsgException(AppSpringUtil.getMessage("user.login.error"))
    }

    @Transactional
    override fun loginNoVerify(loginNoVerifyParam: LoginNoVerifyParam): LoginNoVerifyResponse {
        if (identityTagSet.containsAll(loginNoVerifyParam.identityTagList).not()) {
            throw KatoException(AppSpringUtil.getMessage("identity-tag.code.error"))
        }
        return userService.loginNoVerify(
            loginNoVerifyParam.phone,
            loginNoVerifyParam.name,
            loginNoVerifyParam.identityTagList.map { it.name }.toSet()
        )
    }

    override fun logout() {
        getResponse()?.let {
            userService.logout(it)
        }
    }

    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_REGISTER_USER)
    override fun register(registerParam: RegisterParam): Id {
        return userService.save(
            user = UpcsUser.builder()
                .setKnId(AppIdUtil.nextId())
                .setKnLoginName(registerParam.loginName)
                .setKnLoginPassword(registerParam.loginPwd)
                .setKnName(registerParam.name)
                .setKnPhone(registerParam.phone)
                .setKnEmail(registerParam.email)
                .setKnProfilePic(registerParam.profilePic)
                .setKnBirthday(registerParam.birthday)
                .setKnGender(registerParam.gender.value)
                .setKnIdCard(registerParam.idCard)
                .setKnExtends(registerParam.extends)
                .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setKnUpdatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setKnAddress(registerParam.address)
                .build(),
            isNew = true
        )
    }

    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_RESET_USER_PWD)
    override fun reset(body: Id) {
        userService.reset(body)
    }

    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_UPDATE_USER_PWD)
    override fun updatePwd(updatePwdParam: UpdatePwdParam) {
        AppSecurityUtil.currentUserId()?.let {
            if (!this.verifyPwd(updatePwdParam.oldPwd)) {
                throw MsgException(AppSpringUtil.getMessage("user.pwd.change.error"))
            }
            userService.changePassword(it, updatePwdParam.newPwd)
        }
    }

    override fun verifyPwd(body: String): Boolean {
        return userService.verifyPwd(body)
    }

}
