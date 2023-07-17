package com.bjknrt.user.permission.centre.event

import com.bjknrt.user.permission.centre.UpcsUser
import com.bjknrt.user.permission.centre.UpcsUserIdentityTag
import org.springframework.context.ApplicationEvent

class DeleteManagerEvent(
    provider: Any,
    val user: UpcsUser,
    val userIdentityTags: List<UpcsUserIdentityTag>
) : ApplicationEvent(provider) {
}
