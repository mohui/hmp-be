package com.bjknrt.doctor.patient.management.util

import com.bjknrt.doctor.patient.management.vo.FamilyHistory
import com.bjknrt.doctor.patient.management.vo.MedicalHistory

/**
 * 疾病史集合转字符串
 * @param medicalHistorySet 疾病史集合
 * @return 枚举转后字符串
 */
fun getMedicalHistoryString(medicalHistorySet: Set<MedicalHistory>): String {
    return medicalHistorySet.sorted().joinToString { it.name }
}

/**
 * 疾病史字符串转集合
 * @param medicalHistoryString 疾病史字符串
 * @return 疾病史集合
 */
fun getMedicalHistorySet(medicalHistoryString: String): Set<MedicalHistory> {
    return medicalHistoryString.split(",").map { MedicalHistory.valueOf(it) }.toSet()
}

/**
 * 家族史集合转字符串
 * @param familyHistorySet 家族史集合
 * @return 枚举转后字符串
 */
fun getFamilyHistoryString(familyHistorySet: Set<FamilyHistory>): String {
    return familyHistorySet.sorted().joinToString { it.name }
}

/**
 * 家族史字符串转集合
 * @param familyHistoryString 家族史字符串
 * @return 家族史集合
 */
fun getFamilyHistorySet(familyHistoryString: String): Set<FamilyHistory> {
    return familyHistoryString.split(",").map { FamilyHistory.valueOf(it) }.toSet()
}
