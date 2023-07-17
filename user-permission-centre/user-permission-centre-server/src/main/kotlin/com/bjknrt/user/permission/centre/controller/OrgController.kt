package com.bjknrt.user.permission.centre.controller

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.UpcsOrgTable
import com.bjknrt.user.permission.centre.api.OrgApi
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.vo.Org
import com.bjknrt.user.permission.centre.vo.OrgCodePageParam
import com.bjknrt.user.permission.centre.vo.OrgPageParam
import me.danwi.sqlex.core.query.*
import me.danwi.sqlex.core.query.expression.Expression
import me.danwi.sqlex.core.query.expression.InExpression
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.user.permission.centre.api.OrgController")
class OrgController(val orgTable: UpcsOrgTable) : AppBaseController(), OrgApi {
    override fun listByIds(id: List<Id>): List<Org> {
        return id.takeIf { it.isNotEmpty() }
            ?.let { ids ->
                orgTable.select()
                    .where(UpcsOrgTable.IsDel eq false)
                    .where(UpcsOrgTable.KnId `in` ids.map { it.arg })
                    .find()
                    .map {
                        Org(
                            id = it.knId,
                            name = it.knName ?: "",
                            regionCode = it.knRegionCode,
                            sort = it.knSort
                        )
                    }
            } ?: listOf()
    }

    /**
     * 组织/机构列表查询
     *
     *
     * @param orgPageParam
     * @return PagedResult<Org>
     */
    override fun page(orgPageParam: OrgPageParam): PagedResult<Org> {
        LOGGER.debug("参数：$orgPageParam")
        return AppSecurityUtil.jwtUser()?.let { user ->
            PagedResult.fromDbPaged(
                orgTable.select()
                    .apply {
                        where(UpcsOrgTable.IsDel eq false)
                        orgPageParam.name.takeIf { it.isNotBlank() }?.let { where(UpcsOrgTable.KnName like "%${it}%") }
                        if (!user.isSuperAdmin()) {
                            // 当前登录账号权限下的机构
                            val orgIdExpress = user.orgIdSet.map { it.arg }
                                .takeIf { it.isNotEmpty() }
                                ?.let { UpcsOrgTable.KnId `in` it }
                            // 当前登录账号权限下的区域
                            val regionIdExpress = user.regionIdSet.map { it.arg }
                                .takeIf { it.isNotEmpty() }
                                ?.let { UpcsOrgTable.KnRegionCode `in` it }
                            val userWhere = mutableListOf<InExpression>()
                            if (orgIdExpress != null) userWhere.add(orgIdExpress)
                            if (regionIdExpress != null) userWhere.add(regionIdExpress)
                            where(Expression.joinByOr(userWhere))
                        }
                    }
                    .page(orgPageParam.pageSize, orgPageParam.pageNo)
            ) {
                Org(
                    id = it.knId,
                    name = it.knName ?: "",
                    regionCode = it.knRegionCode,
                    sort = it.knSort
                )
            }
        } ?: PagedResult(0, orgPageParam.pageSize, orgPageParam.pageNo, 0)
    }

    override fun pageOrgByAdministrativeCode(orgCodePageParam: OrgCodePageParam): PagedResult<Org> {
        return PagedResult.fromDbPaged(
            orgTable.select()
                .where(UpcsOrgTable.IsDel eq false)
                .where(UpcsOrgTable.KnRegionCode eq orgCodePageParam.code)
                .page(orgCodePageParam.pageSize, orgCodePageParam.pageNo)
        ) {
            Org(
                id = it.knId,
                name = it.knName ?: "",
                regionCode = it.knRegionCode,
                sort = it.knSort
            )
        }
    }
}
