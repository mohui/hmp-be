package com.bjknrt.health.indicator.controller

import com.bjknrt.health.indicator.vo.*
import com.bjknrt.health.indicator.api.StatisticsApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.health.indicator.dao.StatisticsDao
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.health.indicator.api.StatisticsController")
class StatisticsController(
    val statisticsDao: StatisticsDao
) : AppBaseController(), StatisticsApi {
    override fun bMIList(findListParam: FindListParam): List<BmiStatistic> {
        return statisticsDao.findBmiData(findListParam.startTime, findListParam.endTime, findListParam.patientId)
            .map {
                BmiStatistic(
                    measureDate = it.measureDate,
                    bmi = it.value.toBigDecimal()
                )
            }
    }

    override fun bloodLipidsList(findListParam: FindListParam): List<BloodLipidsStatistic> {
        return statisticsDao.findBloodLipidsData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            BloodLipidsStatistic(
                measureDate = it.measureDate,
                hdlValue = it.hdlValue.takeIf { value -> value != 0.0 }?.toBigDecimal(),
                ldlValue = it.ldlvalue.takeIf { value -> value != 0.0 }?.toBigDecimal(),
                tcValue = it.tcvalue.takeIf { value -> value != 0.0 }?.toBigDecimal(),
                tgValue = it.tgvalue.takeIf { value -> value != 0.0 }?.toBigDecimal()
            )
        }

    }

    override fun bloodPressureList(findListParam: FindListParam): List<BloodPressureStatistic> {
        return statisticsDao.findBloodPressureData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            BloodPressureStatistic(
                measureDate = it.measureDate,
                dbpValue = it.dbpValue.toBigDecimal(),
                sbpValue = it.sbpValue.toBigDecimal()
            )
        }
    }

    override fun bloodSugarList(findListParam: FindListParam): List<BloodSugarStatistic> {
        return statisticsDao.findBloodSugarData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            BloodSugarStatistic(
                measureDate = it.measureDate,
                fbsValue = it.fbsValue?.toBigDecimal(),
                bbsValue = it.bbsValue?.toBigDecimal(),
                abpValue = it.abpValue?.toBigDecimal(),
                sbsValue = it.sbsValue?.toBigDecimal(),
                rbsValue = it.rbsValue?.toBigDecimal()
            )
        }
    }

    override fun bodyHeightList(findListParam: FindListParam): List<HeightStatistic> {
        return statisticsDao.findHeightData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            HeightStatistic(
                measureDate = it.measureDate,
                height = it.value.toBigDecimal()
            )
        }
    }

    override fun bodyTemperatureList(findListParam: FindListParam): List<TemperatureStatistic> {
        return statisticsDao.findTemperatureData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            TemperatureStatistic(
                measureDate = it.measureDate,
                celsius = it.value.toBigDecimal()
            )
        }
    }

    override fun bodyWeightList(findListParam: FindListParam): List<WeightStatistic> {
        return statisticsDao.findWeightData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            WeightStatistic(
                measureDate = it.measureDate,
                weight = it.value.toBigDecimal()
            )
        }
    }

    override fun drinkingList(findListParam: FindListParam): List<DrinkingStatistic> {
        return statisticsDao.findDrinkingData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            DrinkingStatistic(
                measureDate = it.measureDate,
                beer = it.bValue.toBigDecimal(),
                wine = it.wValue.toBigDecimal(),
                whiteSpirit = it.wsValue.toBigDecimal(),
                yellowRichSpirit = it.yrsValue.toBigDecimal(),
                totalAlcohol = it.taValue.toBigDecimal()
            )
        }
    }

    override fun heartRateList(findListParam: FindListParam): List<HeartRateStatistic> {
        return statisticsDao.findHeartRateData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            HeartRateStatistic(
                measureDate = it.measureDate,
                heartRate = it.value
            )
        }
    }

    override fun pulseList(findListParam: FindListParam): List<PulseStatistic> {
        return statisticsDao.findPulseData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            PulseStatistic(
                measureDate = it.measureDate,
                pulse = it.value
            )
        }
    }

    override fun pulseOximetryList(findListParam: FindListParam): List<StatisticPulseOximetry> {
        return statisticsDao.findPulseOximetryData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            StatisticPulseOximetry(
                knMeasureAt = it.measureDate,
                knPulseOximetry = it.value
            )
        }
    }

    override fun smokeList(findListParam: FindListParam): List<SmokeStatistic> {
        return statisticsDao.findSmokeData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            SmokeStatistic(
                measureDate = it.measureDate,
                num = it.value
            )
        }
    }

    override fun waistLineList(findListParam: FindListParam): List<WaistlineStatistic> {
        return statisticsDao.findWaistlineData(
            findListParam.startTime,
            findListParam.endTime,
            findListParam.patientId
        ).map {
            WaistlineStatistic(
                measureDate = it.measureDate,
                waistline = it.value.toBigDecimal()
            )
        }
    }

}
