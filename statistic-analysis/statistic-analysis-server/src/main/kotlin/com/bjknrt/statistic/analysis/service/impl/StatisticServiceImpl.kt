package com.bjknrt.statistic.analysis.service.impl

import com.bjknrt.statistic.analysis.StatisticDao
import com.bjknrt.statistic.analysis.service.DataType
import com.bjknrt.statistic.analysis.service.StatisticService
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class StatisticServiceImpl(
    val statisticDao: StatisticDao
) : StatisticService {

    override fun getDataType(code: BigInteger): DataType {
        var number = statisticDao.getOrgNum(code)
        if (number != null && number > 0) {
            return DataType.ORGANIZE
        }
        number = statisticDao.getRegionNum(code)
        if (number != null && number > 0) {
            return DataType.ADMIN_DIVISION
        }
        return DataType.NONE
    }
}
