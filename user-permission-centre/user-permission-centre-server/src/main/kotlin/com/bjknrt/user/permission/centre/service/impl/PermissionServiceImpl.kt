package com.bjknrt.user.permission.centre.service.impl

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.UpcsRolePermissionTable
import com.bjknrt.user.permission.centre.service.PermissionService
import com.bjknrt.user.permission.centre.vo.PermissionEnum
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Service

@Service
class PermissionServiceImpl(
    val rolePermissionTable: UpcsRolePermissionTable
): PermissionService {
    override fun roleIdGetMenuPermission(id: Id): List<PermissionEnum> {
        return rolePermissionTable.select()
            .where(UpcsRolePermissionTable.KnRoleId eq id)
            .find()
            .map {
                PermissionEnum.valueOf(it.knPermissionCode)
            }
    }
}