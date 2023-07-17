package com.bjknrt.medication.remind.transfer

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.vo.AddDrugRemindParams
import com.bjknrt.medication.remind.vo.FrequencyHealthParams
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.medication.remind.vo.TypesAndPatientParam

fun buildTypePatientParam(
    healthPlans: List<FrequencyHealthParams>,
    drugPlans: List<AddDrugRemindParams>
): List<TypesAndPatientParam> {
    val delMap = mutableMapOf<Id,  MutableSet<HealthPlanType>>()
    // 以几周几次为频率的健康计划类型
    for (delIt in healthPlans) {
        val set = delMap[delIt.patientId] ?: mutableSetOf()
        delMap[delIt.patientId] = set
        set.add(delIt.type)

    }
    // 以周一到周日为频率的健康计划类型
    for (delIt in drugPlans) {
        val set = delMap[delIt.patientId] ?: mutableSetOf()
        delMap[delIt.patientId] = set
        set.add(delIt.type)
    }
    val delList = delMap.map {
        TypesAndPatientParam(patientId = it.key, types = it.value.toList())
    }
    return delList;
}