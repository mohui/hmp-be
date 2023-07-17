package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.entity.PlanFrequencyValue
import com.bjknrt.health.scheme.vo.ManageStage
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * 糖尿病报告服务
 */
interface DiabetesReportService {

    /**
     * 生成报告
     * @param diabetesReport 生成报告请求参数
     */
    fun generateReport(diabetesReport: DiabetesReport)
}

/**
 * 生成糖尿病报告请求对象
 * @param patientId 患者Id
 * @param patientName 患者姓名
 * @param age 患者年龄
 * @param healthManageId 健康方案Id
 * @param manageStage 报告的结束时间
 * @param isStandard 血糖测量次数是否达标
 * @param startDateTime 报告的开始时间
 * @param endDateTime 报告的结束时间
 * @param reportName 报告名称
 * @param bloodSugarList 阶段内血糖数据
 * @param planFrequencyValue 阶段内计划与频率的数据
 */
data class DiabetesReport(
    val patientId: BigInteger,
    val patientName: String,
    val age: Int,
    val healthManageId: BigInteger,
    val manageStage: ManageStage,
    val isStandard: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val reportName: String,
    val bloodSugarList: List<BloodSugar>,
    val planFrequencyValue: List<PlanFrequencyValue>,
)


/**
 * 血糖
 * @param fastingBloodSugar 空腹血糖
 * @param randomBloodSugar 随机血糖
 * @param afterMealBloodSugar 餐后2个小时血糖
 * @param beforeLunchBloodSugar 午餐-餐前血糖
 * @param beforeDinnerBloodSugar 晚餐-餐前血糖
 * @param afterLunchBloodSugar 午餐-餐后2h血糖
 * @param afterDinnerBloodSugar 晚餐-餐后2h血糖
 * @param beforeSleepBloodSugar 睡前血糖
 */
data class BloodSugar(
    val fastingBloodSugar: BigDecimal?,
    val randomBloodSugar: BigDecimal?,
    val afterMealBloodSugar: BigDecimal?,
    val beforeLunchBloodSugar: BigDecimal?,
    val beforeDinnerBloodSugar: BigDecimal?,
    val afterLunchBloodSugar: BigDecimal?,
    val afterDinnerBloodSugar: BigDecimal?,
    val beforeSleepBloodSugar: BigDecimal?,
    val measureDatetime: LocalDateTime
)
