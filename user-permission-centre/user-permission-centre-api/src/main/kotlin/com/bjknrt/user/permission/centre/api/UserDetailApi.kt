package com.bjknrt.user.permission.centre.api

import com.bjknrt.user.permission.centre.vo.UserPermissions
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(
    value = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}",
    contextId = "UserDetailApi"
)
@Validated
interface UserDetailApi {


    /**
     * 获取用户角色、权限、身份标签、区域、机构、身份标签信息
     * 
     *
     * @param body
     * @return UserPermissions
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/userDetail/userInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getUserPermissions(@Valid body: java.math.BigInteger): UserPermissions
}
