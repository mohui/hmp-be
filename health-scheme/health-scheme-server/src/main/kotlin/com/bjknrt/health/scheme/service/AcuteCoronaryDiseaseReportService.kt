package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * 冠心病报告服务
 */
interface AcuteCoronaryDiseaseReportService {

    /**
     * 生成报告
     * @param acdReport 生成报告请求参数
     */
    fun generateReport(acdReport: AcuteCoronaryDiseaseReport)
}

/**
 * 生成冠心病报告请求对象
 * @param patientId 患者Id
 * @param patientName 患者姓名
 * @param age 患者年龄
 * @param height 患者身高
 * @param weight 患者体重
 * @param waistline 患者腰围
 * @param healthManageId 健康方案Id
 * @param managementStage 健康方案阶段
 * @param isStandard 血压测量次数是否达标
 * @param startDateTime 报告的开始时间
 * @param endDateTime 报告的结束时间
 * @param reportName 报告名称
 * @param bloodPressureList 阶段内血压数据集合
 * @param planFrequencyValue 阶段内计划与频率的数据
 */
data class AcuteCoronaryDiseaseReport(
    val patientId: BigInteger,
    val patientName: String,
    val age: Int,
    val height: BigDecimal,
    val weight: BigDecimal,
    val waistline: BigDecimal,
    val healthManageId: BigInteger,
    val managementStage: String,
    val isStandard: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val reportName: String,
    val bloodPressureList: List<BloodPressure>,
    val planFrequencyValue: List<PlanFrequencyValue>,
)
