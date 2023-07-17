package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.user.permission.centre.api.RegionApi
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.service.RegionService
import com.bjknrt.user.permission.centre.vo.ChildListRequest
import com.bjknrt.user.permission.centre.vo.Region
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger


@RestController("com.bjknrt.user.permission.centre.api.AdministrativeController")
class RegionController(
    val regionService: RegionService
) : AppBaseController(), RegionApi {
    override fun getRegionList(codes: List<BigInteger>): List<Region> {
      return  regionService.getRegionList(codes)
          .map { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) }

    }

    override fun getChildList(childListRequest: ChildListRequest): List<Region> {
       return regionService.getChildList(childListRequest.code)
            .map { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) }
    }

    override fun getCurrentRegion(body: BigInteger): Region {
        return regionService.getCurrentRegion(body)
            ?.let { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) }
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("administrative.division.no-found"))
    }

    override fun getCurrentRegion(): Region {
        return AppSecurityUtil.executeWithLogin {
            it.regionIdSet.firstOrNull()?.let { adminDivCode ->
                regionService.getCurrentRegion(adminDivCode)
            }
        }
            ?.let { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) }
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("administrative.division.no-found"))
    }
}
