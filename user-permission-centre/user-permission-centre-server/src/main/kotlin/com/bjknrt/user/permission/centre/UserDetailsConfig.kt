package com.bjknrt.user.permission.centre

import com.bjknrt.security.DefaultInfo
import com.bjknrt.security.ExtendUserDetails
import com.bjknrt.security.JwtExtendUserDetailsObtainHandler
import com.bjknrt.user.permission.centre.controller.UserDetailController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigInteger

@Configuration
class UserDetailsConfig(
    val userDetailController: UserDetailController
) {
    @Bean
    fun getExtendUserDetailsBean(): JwtExtendUserDetailsObtainHandler {
        return object : JwtExtendUserDetailsObtainHandler {
            override fun getExtendInfo(userId: BigInteger): ExtendUserDetails {
                val userPermissions = userDetailController.getUserPermissions(userId)
                return DefaultInfo(
                    roleCodeSet = userPermissions.roleCodeSet,
                    identityTagSet = userPermissions.identityTagSet,
                    orgIdSet = userPermissions.orgIdSet,
                    regionIdSet = userPermissions.regionIdSet,
                    permissionCodeSet = userPermissions.permissionCodeSet,
                )
            }
        }
    }
}
