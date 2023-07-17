package com.bjknrt.user.permission.centre.service

import com.bjknrt.user.permission.centre.UpcsRegion
import java.math.BigInteger

interface RegionService {

    fun getChildList(code: BigInteger?): List<UpcsRegion>

    fun getCurrentRegion(code: BigInteger): UpcsRegion?
    fun getRegionList(codes: List<BigInteger>): List<UpcsRegion>

}

