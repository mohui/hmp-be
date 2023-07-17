package com.bjknrt.user.permission.centre.event.listener

import com.bjknrt.doctor.patient.management.api.DoctorApi
import com.bjknrt.user.permission.centre.event.DeleteManagerEvent
import com.bjknrt.user.permission.centre.vo.IdentityTag
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * 删除带有医生和护士身份标签的用户的事件监听器
 */
@Component
class DeleteManagerEventListener(
    val doctorInfoRpcService: DoctorApi,
) : ApplicationListener<DeleteManagerEvent> {

    override fun onApplicationEvent(event: DeleteManagerEvent) {
        //判断是否包含医护身份标识
        val containsDoctorNurseIdentityTag =
            event.userIdentityTags.map { IdentityTag.valueOf(it.knIdentifyTag) }.stream()
                .anyMatch { it == IdentityTag.DOCTOR || it == IdentityTag.NURSE }

        if (containsDoctorNurseIdentityTag) {
            doctorInfoRpcService.deleteDoctor(event.user.knId)
        }
    }
}
