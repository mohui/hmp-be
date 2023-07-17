package com.bjknrt.user.permission.centre.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.vo.PermissionEnum

interface PermissionService {
    fun roleIdGetMenuPermission(id: Id): List<PermissionEnum>
}