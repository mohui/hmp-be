package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import com.bjknrt.health.scheme.vo.Gender
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * 高血压报告服务
 */
interface HypertensionReportService {

    /**
     * 生成报告
     * @param hypertensionReport 生成报告请求参数
     */
    fun generateReport(hypertensionReport: HypertensionReport)
}


data class HypertensionReport(
    val patientId: BigInteger,
    val patientName: String,
    val healthSchemeManagementInfoId: BigInteger,
    val gender: Gender,
    val age: Int,
    val isStandard: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val bloodPressureList: List<BloodPressure>,
    val smokeNum: Int,
    val height: BigDecimal,
    val weight: BigDecimal,
    val waistline: BigDecimal,
    val reportName: String,
    val planFrequencyValue: List<PlanFrequencyValue>,
    val managementStage: String
)
