package com.bjknrt.security.client

import com.bjknrt.security.DefaultInfo
import com.bjknrt.security.ExtendUserDetails
import com.bjknrt.security.JwtExtendUserDetailsObtainHandler
import com.bjknrt.user.permission.centre.api.UserDetailApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Configuration
import java.math.BigInteger

@Configuration
@ConditionalOnMissingBean(JwtExtendUserDetailsObtainHandler::class)
class JwtExtendUserDetailsObtainHandlerDefaultImpl(
    private val securityUserClient: UserDetailApi
) : JwtExtendUserDetailsObtainHandler {
    override fun getExtendInfo(userId: BigInteger): ExtendUserDetails {
        val userPermissions = securityUserClient.getUserPermissions(userId)
        return DefaultInfo(
            roleCodeSet = userPermissions.roleCodeSet,
            identityTagSet = userPermissions.identityTagSet,
            orgIdSet = userPermissions.orgIdSet,
            regionIdSet = userPermissions.regionIdSet,
            permissionCodeSet = userPermissions.permissionCodeSet,
        )
    }
}
