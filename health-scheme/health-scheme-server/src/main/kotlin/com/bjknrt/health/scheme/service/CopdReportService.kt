package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * 慢阻肺报告服务
 */
interface CopdReportService {

    /**
     * 生成报告
     * @param copdReport 生成报告请求参数
     */
    fun generateReport(copdReport: CopdReport)
}

/**
 * 生成慢阻肺报告请求对象
 * @param patientId 患者Id
 * @param patientName 患者姓名
 * @param age 患者年龄
 * @param healthManageId 健康方案Id
 * @param startDateTime 报告的开始时间
 * @param endDateTime 报告的结束时间
 * @param reportName 报告名称
 * @param pulseOxygenSaturationList 阶段内脉搏氧饱和度
 * @param planFrequencyValue 阶段内计划与频率的数据
 */
data class CopdReport(
    val patientId: BigInteger,
    val patientName: String,
    val age: Int,
    val healthManageId: BigInteger,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val reportName: String,
    val pulseOxygenSaturationList: List<PulseOxygenSaturation>,
    val planFrequencyValue: List<PlanFrequencyValue>,
    )

/**
 * 脉搏氧饱和度
 * @param pulseOxygenSaturation 脉搏氧饱和度
 * @param measureDatetime 记录时间
 */
data class PulseOxygenSaturation(
    val pulseOxygenSaturation: BigDecimal,
    val measureDatetime: LocalDateTime
)
