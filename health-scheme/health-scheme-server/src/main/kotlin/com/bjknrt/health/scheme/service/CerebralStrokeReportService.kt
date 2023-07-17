package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.entity.BloodPressure
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * 脑卒中报告服务
 */
interface CerebralStrokeReportService {

    /**
     * 生成报告
     * @param cerebralStrokeReport 生成报告请求参数
     */
    fun generateReport(cerebralStrokeReport: CerebralStrokeReport)
}

/**
 * 生成脑卒中报告请求对象
 * @param patientId 患者Id
 * @param patientName 患者姓名
 * @param age 患者年龄
 * @param healthManageId 健康方案Id
 * @param startDateTime 报告的开始时间
 * @param endDateTime 报告的结束时间
 * @param reportName 报告名称
 * @param bloodPressureList 血压数据
 */
data class CerebralStrokeReport(
    val patientId: BigInteger,
    val patientName: String,
    val age: Int,
    val healthManageId: BigInteger,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val reportName: String,
    val bloodPressureList: List<BloodPressure>
)
