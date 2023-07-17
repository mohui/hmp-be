package com.bjknrt.health.scheme.service

import com.bjknrt.health.indicator.vo.BloodSugarResult
import com.bjknrt.health.indicator.vo.PatientIndicatorListResult
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.vo.Frequency
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.health.scheme.vo.ManageStage
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

/**
 * 标准校验服务接口
 */
interface StandardVerificationService {

    /**
     * 血压指标集合平均值是否达标
     */
    fun isBloodPressureStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * 冠心病方案的血压测量计划周期内的血压指标集合平均值是否达标
     */
    fun isAcuteCoronaryDiseaseBloodPressureStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * （“空腹血糖值＜7.0 mmol/L”）且“（餐后2h血糖＜10.0mmol/L或无数据）或（随机血糖＜10.0mmol/L或无数据）”
     */
    fun lastTimeAllBloodSugarIsStandard(
        healthManage: HsHealthSchemeManagementInfo
    ): Boolean


    /**
     * 获取健康计划中的指标集合
     */
    fun patientPlanIndicatorListResult(
        manageId: BigInteger,
        patientId: BigInteger,
        planType: HealthPlanType
    ): PatientIndicatorListResult?

    /**
     * 患者最近一次的空腹血糖指标是否达标
     */
    fun lastTimeFastingBloodSugarIsStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * 患者最近一次的餐后2h血糖指标是否达标
     */
    fun lastTimeMealTwoHourBloodSugarIsStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * 获取健康管理中的指标集合
     */
    fun patientManageIndicatorListResult(managementInfo: HsHealthSchemeManagementInfo): PatientIndicatorListResult

    /**
     * 获取健康指标服务中血糖指标集合
     */
    fun patientPlanBloodSugarIndicatorListResult(
        manageId: BigInteger,
        patientId: BigInteger,
        planType: HealthPlanType
    ): List<BloodSugarResult>?

    /**
     * 判断高血压包健康管理中的健康计划是否达标
     */
    fun isHypertensionBloodPressureHealthPlanStandard(healthManageStartDate: LocalDateTime, healthManageEndDate: LocalDateTime,hsmHealthPlan: HsHsmHealthPlan, manageStage: ManageStage): Boolean

    /**
     * 判断糖尿病包健康管理中的空腹血糖计划是否达标
     */
    fun isDiabetesFastingBloodSugarHealthPlanStandard(healthManageStartDate: LocalDateTime, healthManageEndDate: LocalDateTime,hsmHealthPlan: HsHsmHealthPlan, manageStage: ManageStage): Boolean

    /**
     * 判断糖尿病包健康管理中的餐前血糖计划是否达标
     */
    fun isDiabetesBeforeMealBloodSugarHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean

    /**
     * 判断糖尿病包健康管理中的餐后血糖计划是否达标
     */
    fun isDiabetesAfterMealBloodSugarHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean

    /**
     * 判断糖尿病包健康管理中血糖指标值集合（空腹，餐前，餐后）是否全部达标
     */
    fun isDiabetesBloodSugarValueStandard(
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * 判断冠心病包健康管理中的健康计划是否达标
     */
    fun isAcuteCoronaryDiseaseBloodPressureHealthPlanStandard(
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
        hsmHealthPlan: HsHsmHealthPlan,
        manageStage: ManageStage
    ): Boolean

    /**
     * 获取抽烟数量
     */
    fun smokeNum(indicatorListForDpm: PatientIndicatorListResult): Int

    /**
     * 获取血压平均值
     */
    fun getBloodPressureAvg(bloodPressureList: List<BigDecimal>): BigDecimal

    /**
     * 线上随访-血糖是否达标
     */
    fun bloodSugarIsStandard(
        isFirstWeek: Boolean,
        healthManage: HsHealthSchemeManagementInfo
    ): Boolean

    /**
     * 线上随访-周期内最近一次血压指标是否达标
     */
    fun lastTimeBloodPressureIsStandard(
        isFirstWeek: Boolean,
        manageId: BigInteger,
        patientId: BigInteger
    ): Boolean

    /**
     * 通过计划id和频次获取远程计划
     */
    fun getRemoteHealthPlanByPlanIdAndFrequency(
        foreignPlanId: BigInteger,
        frequency: Frequency,
        now: LocalDateTime
    ): Int

    /**
     * 获取某个时间范围内的血压指标集合数据
     * @param patientId 患者ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    fun getBloodPressureIndicatorListResult(
        patientId: BigInteger,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<BloodPressure>
}
