package com.bjknrt.health.scheme.transfer

import com.bjknrt.health.scheme.entity.DrugPlanDTO
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.vo.Frequency
import com.bjknrt.health.scheme.vo.FrequencyHealthAllParam
import com.bjknrt.medication.remind.vo.*
import java.math.BigInteger


/**
 * 拼接根据type添加健康计划
 *
 * @param healthPlans 频率为几周几次的健康计划
 * @param drugPlans 频率为周几几点的健康计划
 * @return [FrequencyHealthAllParam]
 */
fun buildRemindFrequencyHealthAllParam(
    patientId: BigInteger,
    healthPlans: List<HealthPlanDTO>,
    drugPlans: List<DrugPlanDTO>
): com.bjknrt.medication.remind.vo.FrequencyHealthAllParam {
    val remindHealthPlans = healthPlans.map { params ->
        val frequencyList = params.frequencys?.let { frequencys ->
            frequencys.map { buildRemindFrequency(it) };
        }
        FrequencyHealthParams(
            name = params.name,
            type = HealthPlanType.valueOf(params.type.name),
            patientId = patientId,
            id = null,
            subName = params.subName,
            desc = params.desc,
            cycleStartTime = params.cycleStartTime,
            cycleEndTime = params.cycleEndTime,
            externalKey = params.externalKey,
            displayTime = params.displayTime,
            frequencys = frequencyList,
            group = params.group,
            clockDisplay = params.clockDisplay ?: true
        )
    }

    // 拼接以周一到周日为频率的条件
    val remindDrugPlan = drugPlans.map {
        AddDrugRemindParams(
            patientId = patientId,
            drugName = it.drugName,
            isUsed = it.isUsed,
            time = it.time,
            type = HealthPlanType.valueOf(it.type.name),
            frequencys = it.frequencys.map { item ->
                Week.valueOf(item.name)
            },
            cycleStartTime = it.cycleStartTime,
            subName = it.subName,
            cycleEndTime = it.cycleEndTime
        )
    }

    return FrequencyHealthAllParam(
        healthPlans = remindHealthPlans,
        drugPlans = remindDrugPlan
    )
}


fun buildRemindFrequency(frequency: Frequency): com.bjknrt.medication.remind.vo.Frequency {
    val children = frequency.children?.let {
        buildRemindFrequency(it)
    }
    return Frequency(
        frequencyTime = frequency.frequencyTime,
        frequencyTimeUnit = TimeUnit.valueOf(frequency.frequencyTimeUnit.name),
        frequencyNum = frequency.frequencyNum,
        frequencyNumUnit = FrequencyNumUnit.valueOf(frequency.frequencyNumUnit.name),
        frequencyMaxNum = frequency.frequencyMaxNum,
        children = children
    )
}


fun transformRemindHealthType(type:  com.bjknrt.health.scheme.vo.HealthPlanType): HealthPlanType {
   return HealthPlanType.valueOf(type.name)
}

fun transformRemindHealthType(type:  HealthPlanType): com.bjknrt.health.scheme.vo.HealthPlanType {
    return com.bjknrt.health.scheme.vo.HealthPlanType.valueOf(type.name)
}

/**
 * 转换频率为几周几次的健康计划
 */
fun buildHealthPlanDTO(
    healthPlans: List<com.bjknrt.health.scheme.vo.FrequencyHealthParams>,
): List<HealthPlanDTO> {
    return healthPlans.map { params ->
        HealthPlanDTO(
            name = params.name,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.valueOf(params.type.name),
            subName = params.subName,
            desc = params.desc,
            externalKey = params.externalKey,
            cycleStartTime = params.cycleStartTime,
            cycleEndTime = params.cycleEndTime,
            displayTime = params.displayTime,
            group = params.group,
            clockDisplay = params.clockDisplay ?: true,
            frequencys = params.frequencys
        )
    }
}

/**
 * 转换频率为周几几点的健康计划
 */
fun buildDrugPlanDTO(
    drugPlans: List<com.bjknrt.health.scheme.vo.AddDrugRemindParams>
): List<DrugPlanDTO> {
    return drugPlans.map {
        DrugPlanDTO(
            drugName = it.drugName,
            isUsed = it.isUsed,
            time = it.time,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.valueOf(it.type.name),
            frequencys = it.frequencys.map { item -> com.bjknrt.health.scheme.vo.Week.valueOf(item.name) },
            cycleStartTime = it.cycleStartTime,
            subName = it.subName,
            cycleEndTime = it.cycleEndTime
        )
    }
}