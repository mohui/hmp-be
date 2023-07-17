package com.bjknrt.user.permission.centre.service.impl

import com.bjknrt.user.permission.centre.UpcsRegion
import com.bjknrt.user.permission.centre.UpcsRegionTable
import com.bjknrt.user.permission.centre.service.RegionService
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class RegionServiceImpl(
    val regionTable: UpcsRegionTable
) : RegionService {
    override fun getChildList(code: BigInteger?): List<UpcsRegion> {
        code?.let {
            return regionTable.select()
                .where(UpcsRegionTable.KnParentCode eq code.arg)
                .find()
        }
        return regionTable.select()
            .where(UpcsRegionTable.KnParentCode.isNull)
            .find()
    }

    override fun getCurrentRegion(code: BigInteger): UpcsRegion? {
        return regionTable.findByKnCode(code)
    }

    override fun getRegionList(codes: List<BigInteger>): List<UpcsRegion> {
        return regionTable.select()
            .where(UpcsRegionTable.KnCode `in` codes.map { it.arg })
            .find()
    }
}
