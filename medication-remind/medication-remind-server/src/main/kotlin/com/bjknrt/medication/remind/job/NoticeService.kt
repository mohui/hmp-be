package com.bjknrt.medication.remind.job

import com.bjknrt.medication.remind.MrHealthPlanTable
import com.bjknrt.medication.remind.dao.MrHealthPlanDAO
import com.bjknrt.medication.remind.entity.HealthPlanEntity
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.util.clockInStatusUtil
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.wechat.service.api.PatientApi
import com.bjknrt.wechat.service.vo.DischargeVisitNotify
import com.bjknrt.wechat.service.vo.PatientNotifyDrugPostRequestInner
import com.bjknrt.wechat.service.vo.PatientNotifyIndicatorScheduledPostRequestInner
import com.bjknrt.wechat.service.vo.VisitNotify
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class NoticeService(
    val mrHealthPlanTable: MrHealthPlanTable,
    val notifyPatientClient: PatientApi,
    val mrHealthPlanDAO: MrHealthPlanDAO,
    val healthPlanService: HealthPlanService
) {

    /**
     * 用药提醒
     *
     * 计划-用药提醒 到达提醒时间
     */
    @Transactional
    fun remind(patientIds: List<BigInteger>) {
        // 当前时间
        val current = LocalDateTime.now()
        // 获取今天是周几
        val week = current.dayOfWeek.name
        // 当前时间的分钟
        val currentTime = current.toLocalTime()

        // 当前分钟的开始时间
        val start = currentTime.truncatedTo(ChronoUnit.MINUTES)
        // 当前分钟的结束时间
        val end = start.plusSeconds(59)
        val typeList = listOf(
            HealthPlanType.DRUG.name,
            HealthPlanType.HYPERTENSION_DRUG_PROGRAM.name,
            HealthPlanType.DIABETES_DRUG_PROGRAM.name
        )

        val drugList = mrHealthPlanTable
            .select()
            .where(MrHealthPlanTable.IsDel eq arg(false))
            .where(MrHealthPlanTable.IsUsed eq arg(true))
            .where(MrHealthPlanTable.KnType `in` typeList.map { it.arg })
            .where(MrHealthPlanTable.KnTime gte arg(start))
            .where(MrHealthPlanTable.KnTime lte arg(end))
            .where(MrHealthPlanTable.KnPatientId `in` patientIds.map { it.arg })
            .filter {
                if (week == "MONDAY") {
                    (MrHealthPlanTable.IsMonday eq true)
                } else if (week == "TUESDAY") {
                    (MrHealthPlanTable.IsTuesday eq true)
                } else if (week == "WEDNESDAY") {
                    (MrHealthPlanTable.IsWednesday eq true)
                } else if (week == "THURSDAY") {
                    (MrHealthPlanTable.IsThursday eq true)
                } else if (week == "FRIDAY") {
                    (MrHealthPlanTable.IsFriday eq true)
                } else if (week == "SATURDAY") {
                    (MrHealthPlanTable.IsSaturday eq true)
                } else {
                    (MrHealthPlanTable.IsSunday eq true)
                }
            }
            .find()
            .map {
                PatientNotifyDrugPostRequestInner(
                    id = it.knId,
                    patientId = it.knPatientId,
                    name = it.knName,
                    time = it.knTime ?: start
                )
            }

        notifyPatientClient.patientNotifyDrugPost(drugList)
    }

    /**
     * 线下随访开始时间提醒
     *
     * 高血压, 糖尿病, 脑卒中, 慢阻肺, 冠心病
     */
    @Transactional
    fun offlineVisitStartDate(patientIds: List<BigInteger>) {
        // 高血压、糖尿病、脑卒中、慢阻肺、冠心病
        val typeList = listOf(
            HealthPlanType.OFFLINE_HYPERTENSION,
            HealthPlanType.OFFLINE_DIABETES,
            HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
            HealthPlanType.OFFLINE_CEREBRAL_STROKE,
            HealthPlanType.OFFLINE_COPD
        )
        val healthPlan = healthPlanList(patientIds, typeList)
            .filter {
                !it.isCycleClockIn && it.cycleStartTime.toLocalDate() == LocalDate.now()
            }
        // 线下随访开始时间调用消息推送
        notifyPatientClient.patientNotifyVisitOfflinePost(transferVisit(healthPlan))
    }

    private fun healthPlanList(patientIds: List<BigInteger>, typeList: List<HealthPlanType>): List<HealthPlanEntity> {
        val healthPlanModels = mrHealthPlanDAO.healthPlanList(patientIds, LocalDateTime.now(), typeList.map { it.name })
            .map {
                HealthPlanEntity(
                    id = it.knId,
                    patientId = it.knPatientId,
                    name = it.knName,
                    type = HealthPlanType.valueOf(it.knType),
                    clockIn = false,
                    isClockDisplay = it.isClockDisplay,
                    isUsed = it.isUsed,
                    cycleStartTime = it?.knCycleStartTime ?: it.knCreatedAt,
                    isMonday = it.isMonday,
                    isTuesday = it.isTuesday,
                    isWednesday = it.isWednesday,
                    isThursday = it.isThursday,
                    isFriday = it.isFriday,
                    isSaturday = it.isSaturday,
                    isSunday = it.isSunday,
                    isCycleClockIn = false,
                    display = true,
                    subName = it.knSubName,
                    desc = it.knDesc,
                    time = it.knTime,
                    cycleEndTime = it.knCycleEndTime,
                    displayTime = it.knDisplayTime,
                    externalKey = it.externalKey,
                    group = it.knGroup,
                    frequency = null,
                    todayFrequency = null,
                    subFrequency = null,
                    mainFrequency = null
                )
            }
        // 整理后的返回值
        return healthPlanService.arrangementHealthPlanList(healthPlanModels)
    }

    /**
     * 测量计划(血压、空腹血糖、餐前血糖、餐后2小时血糖、脉搏氧饱和度)
     */
    @Transactional
    fun measure(patientIds: List<BigInteger>) {
        //血压、空腹血糖、餐前血糖、餐后2小时血糖、脉搏氧饱和度
        val typeList = listOf(
            HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            HealthPlanType.FASTING_BLOOD_GLUCOSE,
            HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE,
            HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
            HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN
        )
        val healthPlan = healthPlanList(patientIds, typeList)
            .filter { !clockInStatusUtil(it.todayFrequency, it.subFrequency) }
            .map {
                PatientNotifyIndicatorScheduledPostRequestInner(
                    id = it.id,
                    patientId = it.patientId,
                    name = it.name,
                    desc = it.desc ?: ""
                )
            }
        // 测量计划调用消息推送
        notifyPatientClient.patientNotifyIndicatorScheduledPost(healthPlan)
    }

    /**
     * 线下随访结束时间提醒
     *
     * 高血压, 糖尿病, 脑卒中, 慢阻肺, 冠心病
     */
    @Transactional
    fun offlineVisitEndDate(patientIds: List<BigInteger>, day: Int = 1) {
        val typeList = listOf(
            HealthPlanType.OFFLINE_HYPERTENSION,
            HealthPlanType.OFFLINE_DIABETES,
            HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
            HealthPlanType.OFFLINE_CEREBRAL_STROKE,
            HealthPlanType.OFFLINE_COPD
        )
        // 高血压、糖尿病、脑卒中、慢阻肺、冠心病
        val healthPlan = healthPlanList(patientIds, typeList)
            .filter {
                !it.isCycleClockIn &&
                        it.cycleEndTime?.let { endIt ->
                            LocalDate.of(endIt.plusSeconds(-1).year, endIt.plusSeconds(-1).month, day) == LocalDate.now()
                        } ?: false
            }
        // 线下随访结束时间调用消息推送
        notifyPatientClient.patientNotifyVisitOfflineFinalPost(transferVisit(healthPlan))
    }

    /**
     * 线上随访开始时间提醒
     *
     */
    @Transactional
    fun onlineVisitStartDate(patientIds: List<BigInteger>) {
        val typeList = listOf(
            // 高血压线上
            HealthPlanType.HYPERTENSION_VISIT,
            // 糖尿病
            HealthPlanType.ONLINE_DIABETES,
            // 脑卒中
            HealthPlanType.ONLINE_CEREBRAL_STROKE,
            HealthPlanType.CEREBRAL_STROKE,
            // 冠心病
            HealthPlanType.ONLINE_ACUTE_CORONARY_DISEASE,
            // 慢阻肺
            HealthPlanType.ONLINE_COPD,
            // 行为习惯
            HealthPlanType.BEHAVIOR_VISIT,
            // 糖尿病行为习惯
            HealthPlanType.DIABETES_BEHAVIOR_VISIT,
            // 脑卒中行为习惯
            HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
            // 冠心病行为习惯随访
            HealthPlanType.ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT,
            // 慢阻肺行为习惯随访
            HealthPlanType.COPD_BEHAVIOR_VISIT
        )
        val healthPlan = healthPlanList(patientIds, typeList).filter {
            !it.isCycleClockIn && it.displayTime.toLocalDate() == LocalDate.now()
        }

        // 线上随访调用消息推送
        notifyPatientClient.patientNotifyVisitOnlinePost(transferVisit(healthPlan))
    }

    /**
     * 随访公共方法转换
     */
    private fun transferVisit(healthPlan: List<HealthPlanEntity>): List<VisitNotify> {
        return healthPlan.map {
            VisitNotify(
                id = it.id,
                patientId = it.patientId,
                name = it.name,
                start = it.cycleStartTime,
                end = it.cycleEndTime ?: LocalDateTime.now(),
                remark = it.desc ?: ""
            )
        }
    }

    fun leaveHospitalVisit(healthPlanId: BigInteger, patientId: BigInteger) {
        //发送wechat消息
        notifyPatientClient.patientNotifyVisitDischargePost(
            DischargeVisitNotify(
                id = healthPlanId,
                patientId = patientId
            )
        )
    }
}