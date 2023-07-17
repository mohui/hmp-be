package com.bjknrt.user.permission.centre.event

import com.bjknrt.user.permission.centre.UpcsUser
import com.bjknrt.user.permission.centre.vo.UserExtendInfo
import org.springframework.context.ApplicationEvent

class SaveUserEvent(
    provider: Any,
    val userInfo: UpcsUser,
    val extendInfo: UserExtendInfo?
) : ApplicationEvent(provider) {
}
