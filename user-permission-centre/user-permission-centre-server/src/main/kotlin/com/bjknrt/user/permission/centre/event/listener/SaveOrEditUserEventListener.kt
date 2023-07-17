package com.bjknrt.user.permission.centre.event.listener

import com.bjknrt.doctor.patient.management.api.DoctorApi
import com.bjknrt.doctor.patient.management.api.DoctorPatientApi
import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorLevel
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.event.SaveUserEvent
import com.bjknrt.user.permission.centre.vo.IdentityTag
import com.bjknrt.user.permission.centre.vo.UserExtendInfo
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.eq
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigInteger

/**
 * 保存或编辑用户后的事件监听器
 * （1）处理医生的信息
 * （2）处理用户与角色、机构、区域、身份标签的关联表信息
 */
@Component
class SaveOrEditUserEventListener(
    val orgTable: UpcsOrgTable,
    val regionTable: UpcsRegionTable,
    val doctorInfoRpcService: DoctorApi,
    val doctorPatientRpcService: DoctorPatientApi,
    val userRoleTable: UpcsUserRoleTable,
    val userOrgTable: UpcsUserOrgTable,
    val userRegionTable: UpcsUserRegionTable,
    val userIdentityTagTable: UpcsUserIdentityTagTable,
) : ApplicationListener<SaveUserEvent> {

    override fun onApplicationEvent(event: SaveUserEvent) {
        //准备数据
        val userInfo = event.userInfo
        val userId = event.userInfo.knId
        val extendInfo = event.extendInfo ?: return
        //1、根据用户身份标识的变更，来处理医生信息
        this.doctorInfoHandler(userId, userInfo, extendInfo)
        //2、用于删除之前用户的关联关系，重新保存用户与角色、机构、区域、身份标签的关联关系
        this.userRelationHandler(userId, extendInfo)
    }

    private fun doctorInfoHandler(
        userId: BigInteger,
        userInfo: UpcsUser,
        extendInfo: UserExtendInfo,
    ) {
        //该用户新的身份标签包含医生或者护士，则添加或更新医生信息
        if (extendInfo.identityTagList?.contains(IdentityTag.DOCTOR) == true ||
            extendInfo.identityTagList?.contains(IdentityTag.NURSE) == true
        ) {
            val hospitalId =
                (extendInfo.orgId ?: extendInfo.regionId)
                    ?: throw KatoException(AppSpringUtil.getMessage("user.extend.org-or-region-is-null"))

            val hospitalName = if (extendInfo.orgId != null) {
                orgTable.findByKnId(extendInfo.orgId)?.knName
                    ?: throw KatoException(AppSpringUtil.getMessage("user.extend.org-id-error"))
            } else {
                regionTable.findByKnCode(extendInfo.regionId)?.knName
                    ?: throw KatoException(AppSpringUtil.getMessage("user.extend.region-id-error"))
            }

            doctorInfoRpcService.addDoctor(
                DoctorAddRequest(
                    authId = userId,
                    name = userInfo.knName ?: userInfo.knLoginName,
                    gender = Gender.valueOf(userInfo.knGender),
                    deptName = extendInfo.deptName ?: "",
                    address = userInfo.knAddress ?: "",
                    phone = userInfo.knPhone,
                    doctorLevel = extendInfo.doctorLevel?.let { DoctorLevel.valueOf(it.name) } ?: DoctorLevel.OTHER,
                    hospitalId = hospitalId,
                    hospitalName = hospitalName
                )
            )
        } else {
            //查询该用户之前的身份标签
            val identityTags = userIdentityTagTable.select()
                .where(UpcsUserIdentityTagTable.KnUserId eq userId)
                .where(UpcsUserIdentityTagTable.IsDel eq false)
                .find()
                .map { IdentityTag.valueOf(it.knIdentifyTag) }

            //该用户之前的身份标签有医生或者护士，但是新的身份标签不包含医生和护士，则删除医生信息
            if (identityTags.contains(IdentityTag.DOCTOR) || identityTags.contains(IdentityTag.NURSE)) {
                //判断是否存在绑定的患者
                if (doctorPatientRpcService.bindPatientNum(userId) != 0L) {
                    throw MsgException(AppSpringUtil.getMessage("doctor-patient.bind-patient-num-is-not-zero"))
                }
                doctorInfoRpcService.deleteDoctor(userId)
            }
        }
    }

    private fun userRelationHandler(
        userId: BigInteger,
        extendInfo: UserExtendInfo
    ) {
        val currentUserId = AppSecurityUtil.currentUserIdWithDefault()
        //先删除用户现之前的关联关系
        userRoleTable.update().setIsDel(true).where(UpcsUserRoleTable.KnUserId eq userId).execute()
        userOrgTable.update().setIsDel(true).where(UpcsUserOrgTable.KnUserId eq userId).execute()
        userRegionTable.update().setIsDel(true).where(UpcsUserRegionTable.KnUserId eq userId).execute()
        userIdentityTagTable.update().setIsDel(true).where(UpcsUserIdentityTagTable.KnUserId eq userId).execute()

        //保存新的关联角色关系
        extendInfo.roleIdList?.forEach {
            userRoleTable.insertWithoutNull(
                UpcsUserRole.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnUserId(userId)
                    .setKnRoleId(it)
                    .setKnCreatedBy(currentUserId)
                    .setKnUpdatedBy(currentUserId)
                    .build()
            )
        }

        //保存新的关联机构关系
        extendInfo.orgId?.let {
            userOrgTable.insertWithoutNull(
                UpcsUserOrg.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnUserId(userId)
                    .setKnOrgId(it)
                    .setKnCreatedBy(currentUserId)
                    .setKnUpdatedBy(currentUserId)
                    .build()
            )
        }

        //保存新的关联区域关系
        extendInfo.regionId?.let {
            userRegionTable.insertWithoutNull(
                UpcsUserRegion.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnUserId(userId)
                    .setKnRegionCode(it)
                    .setKnCreatedBy(currentUserId)
                    .setKnUpdatedBy(currentUserId)
                    .build()
            )
        }

        //保存新的关联身份标签关系
        extendInfo.identityTagList?.forEach {
            userIdentityTagTable.insertWithoutNull(
                UpcsUserIdentityTag.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnUserId(userId)
                    .setKnIdentifyTag(it.name)
                    .setKnCreatedBy(currentUserId)
                    .setKnUpdatedBy(currentUserId)
                    .build()
            )
        }
    }
}
