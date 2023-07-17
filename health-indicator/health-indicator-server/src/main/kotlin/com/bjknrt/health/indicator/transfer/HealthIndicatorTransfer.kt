package com.bjknrt.health.indicator.transfer

import com.bjknrt.health.indicator.HiBloodLipids
import com.bjknrt.health.indicator.HiBloodPressure
import com.bjknrt.health.indicator.HiBloodSugar
import com.bjknrt.health.indicator.HiBmi
import com.bjknrt.health.indicator.HiBodyHeight
import com.bjknrt.health.indicator.HiBodyTemperature
import com.bjknrt.health.indicator.HiBodyWeight
import com.bjknrt.health.indicator.HiDrinking
import com.bjknrt.health.indicator.HiHeartRate
import com.bjknrt.health.indicator.HiPulse
import com.bjknrt.health.indicator.HiPulseOximetry
import com.bjknrt.health.indicator.HiSmoke
import com.bjknrt.health.indicator.HiWaistline
import com.bjknrt.health.indicator.vo.*

fun toBMIListResponse(hiBmiList: MutableList<HiBmi>): List<BMIResult> {
    return hiBmiList.map { hiBmi ->
        BMIResult(
            knId = hiBmi.knId,
            knCreatedAt = hiBmi.knCreatedAt,
            knBmi = hiBmi.knBmi.toBigDecimal(),
            knMeasureAt = hiBmi.knMeasureAt
        )
    }
}

fun toBloodLipidsListResponse(hiBloodLipids: MutableList<HiBloodLipids>): List<BloodLipidsResult> {
    return hiBloodLipids.map { bloodLipid ->
        BloodLipidsResult(
            knId = bloodLipid.knId,
            knCreatedAt = bloodLipid.knCreatedAt,
            knMeasureAt = bloodLipid.knMeasureAt,
            knTotalCholesterol = bloodLipid.knTotalCholesterol?.toBigDecimal(),
            knTriglycerides = bloodLipid.knTriglycerides?.toBigDecimal(),
            knLowDensityLipoprotein = bloodLipid.knLowDensityLipoprotein?.toBigDecimal(),
            knHighDensityLipoprotein = bloodLipid.knHighDensityLipoprotein?.toBigDecimal()
        )
    }
}

fun toBloodPressureListResponse(hiBloodPressure: MutableList<HiBloodPressure>): List<BloodPressureResult> {
    return hiBloodPressure.map { bloodPressure ->
        BloodPressureResult(
            knId = bloodPressure.knId,
            knCreatedAt = bloodPressure.knCreatedAt,
            knSystolicBloodPressure = bloodPressure.knSystolicBloodPressure.toBigDecimal(),
            knDiastolicBloodPressure = bloodPressure.knDiastolicBloodPressure.toBigDecimal(),
            knMeasureAt = bloodPressure.knMeasureAt
        )
    }
}

fun toBloodSugarListResponse(hiBloodSugarList: MutableList<HiBloodSugar>): List<BloodSugarResult> {
    return hiBloodSugarList.map { bloodSugar ->
        BloodSugarResult(
            knId = bloodSugar.knId,
            knCreatedAt = bloodSugar.knCreatedAt,
            knMeasureAt = bloodSugar.knMeasureAt,
            knFastingBloodSandalwood = bloodSugar.knFastingBloodSandalwood?.toBigDecimal(),
            knBeforeLunchBloodSugar = bloodSugar.knBeforeLunchBloodSugar?.toBigDecimal(),
            knBeforeDinnerBloodSugar = bloodSugar.knBeforeDinnerBloodSugar?.toBigDecimal(),
            knRandomBloodSugar = bloodSugar.knRandomBloodSugar?.toBigDecimal(),
            knAfterMealBloodSugar = bloodSugar.knAfterMealBloodSugar?.toBigDecimal(),
            knAfterLunchBloodSugar = bloodSugar.knAfterLunchBloodSugar?.toBigDecimal(),
            knAfterDinnerBloodSugar = bloodSugar.knAfterDinnerBloodSugar?.toBigDecimal(),
            knBeforeSleepBloodSugar = bloodSugar.knBeforeSleepBloodSugar?.toBigDecimal()
        )
    }
}

fun toBodyHeightListResponse(hiBodyHeightList: MutableList<HiBodyHeight>): List<BodyHeightResult> {
    return hiBodyHeightList.map { bodyHeight ->
        BodyHeightResult(
            knId = bodyHeight.knId,
            knCreatedAt = bodyHeight.knCreatedAt,
            knBodyHeight = bodyHeight.knBodyHeight.toBigDecimal(),
            knMeasureAt = bodyHeight.knMeasureAt
        )
    }
}

fun toBodyTemperatureListResponse(hiBodyTemperatureList: MutableList<HiBodyTemperature>): List<BodyTemperatureResult> {
    return hiBodyTemperatureList.map { bodyTemperature ->
        BodyTemperatureResult(
            knId = bodyTemperature.knId,
            knCreatedAt = bodyTemperature.knCreatedAt,
            knCelsius = bodyTemperature.knCelsius.toBigDecimal(),
            knMeasureAt = bodyTemperature.knMeasureAt
        )
    }
}

fun toBodyWeightListResponse(hiBodyWeightList: MutableList<HiBodyWeight>): List<BodyWeightResult> {
    return hiBodyWeightList.map { bodyWeight ->
        BodyWeightResult(
            knId = bodyWeight.knId,
            knCreatedAt = bodyWeight.knCreatedAt,
            knBodyWeight = bodyWeight.knBodyWeight.toBigDecimal(),
            knMeasureAt = bodyWeight.knMeasureAt
        )
    }
}

fun toDrinkingListResponse(hiDrinkingList: MutableList<HiDrinking>): List<DrinkingResult> {
    return hiDrinkingList.map { drinking ->
        DrinkingResult(
            knId = drinking.knId,
            knCreatedAt = drinking.knCreatedAt,
            knBeer = drinking.knBeer.toBigDecimal(),
            knWhiteSpirit = drinking.knWhiteSpirit.toBigDecimal(),
            knWine = drinking.knWine.toBigDecimal(),
            knYellowRichSpirit = drinking.knYellowRiceSpirit.toBigDecimal(),
            knMeasureAt = drinking.knMeasureAt,
            knTotalAlcohol = drinking.knTotalAlcohol.toBigDecimal()
        )
    }
}

fun toHeartRateListResponse(hiHeartRateList: MutableList<HiHeartRate>): List<HeartRateResult> {
    return hiHeartRateList.map { heartRate ->
        HeartRateResult(
            knId = heartRate.knId,
            knCreatedAt = heartRate.knCreatedAt,
            knHeartRate = heartRate.knHeartRate,
            knMeasureAt = heartRate.knMeasureAt
        )
    }

}


fun toPulseListResponse(hiPulseList: MutableList<HiPulse>): List<PulseResult> {
    return hiPulseList.map { pulse ->
        PulseResult(
            knId = pulse.knId,
            knCreatedAt = pulse.knCreatedAt,
            knPulse = pulse.knPulse,
            knMeasureAt = pulse.knMeasureAt
        )
    }

}

fun toSmokeListResponse(hiSmokeList: MutableList<HiSmoke>): List<SmokeResult> {
    return hiSmokeList.map { smoke ->
        SmokeResult(
            knId = smoke.knId,
            knCreatedAt = smoke.knCreatedAt,
            knNum = smoke.knNum,
            knMeasureAt = smoke.knMeasureAt
        )
    }
}

fun toWaistlineListResponse(hiWaistlineList: MutableList<HiWaistline>): List<WaistLineResult> {
    return hiWaistlineList.map { waistline ->
        WaistLineResult(
            knId = waistline.knId,
            knCreatedAt = waistline.knCreatedAt,
            knWaistline = waistline.knWaistline.toBigDecimal(),
            knMeasureAt = waistline.knMeasureAt
        )
    }
}

fun toPulseOximetryListResponse(hiPulseOximetryList: MutableList<HiPulseOximetry>): List<PulseOximetryResult> {
    return hiPulseOximetryList.map { pulseOximetry ->
        PulseOximetryResult(
            knId = pulseOximetry.knId,
            knCreatedAt = pulseOximetry.knCreatedAt,
            knPulseOximetry = pulseOximetry.knPulseOximetry,
            knMeasureAt = pulseOximetry.knMeasureAt
        )
    }
}