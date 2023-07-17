package com.bjknrt.medication.remind.service.impl

import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.medication.remind.*
import com.bjknrt.medication.remind.entity.DrugDTO
import com.bjknrt.medication.remind.entity.DrugHealthPlanDTO
import com.bjknrt.medication.remind.entity.RemindListResult
import com.bjknrt.medication.remind.service.MedicationRemindService
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class MedicationRemindServiceImpl(
    val mrHealthPlanTable: MrHealthPlanTable
) : MedicationRemindService {
    @Transactional
    override fun upsert(upsertParams: DrugDTO): DrugHealthPlanDTO {
        // 周一
        var monday = false;
        // 周二
        var tuesday = false;
        // 周三
        var wednesday = false;
        // 周四
        var thursday = false;
        // 周五
        var friday = false;
        // 周六
        var saturday = false;
        // 周日
        var sunday = false;
        for (it in upsertParams.frequencys) {
            if (it == Week.MONDAY) monday = true
            if (it == Week.TUESDAY) tuesday = true
            if (it == Week.WEDNESDAY) wednesday = true
            if (it == Week.THURSDAY) thursday = true
            if (it == Week.FRIDAY) friday = true
            if (it == Week.SATURDAY) saturday = true
            if (it == Week.SUNDAY) sunday = true
        }

        // 当前时间
        val currentTime = upsertParams.time.truncatedTo(ChronoUnit.MINUTES)

        val current = LocalDateTime.now()
        // 执行 upsert 语句,根据主键判断, 主键存在: 执行修改, 不存在: 添加
        val result = mrHealthPlanTable.saveOrUpdate(
            MrHealthPlan.builder()
                .setKnId(upsertParams.id ?: AppIdUtil.nextId())
                .setKnPatientId(upsertParams.patientId)
                .setKnType(upsertParams.type.name)
                .setKnCreatedAt(current)
                .setKnCreatedBy(upsertParams.patientId)
                .setKnUpdatedAt(current)
                .setKnUpdatedBy(upsertParams.patientId)
                .setKnTime(currentTime)
                .setKnName(upsertParams.drugName)
                .setKnSubName(upsertParams.subName)
                .setIsMonday(monday)
                .setIsTuesday(tuesday)
                .setIsWednesday(wednesday)
                .setIsThursday(thursday)
                .setIsFriday(friday)
                .setIsSaturday(saturday)
                .setIsSunday(sunday)
                .setIsUsed(upsertParams.isUsed)
                .setIsDel(false)
                .setKnCycleStartTime(upsertParams.cycleStartTime ?: current)
                .setKnCycleEndTime(upsertParams.cycleEndTime)
                .build()
        )

        return result?.let {
            DrugHealthPlanDTO(
                id = it.knId,
                patientId = it.knPatientId,
                name = it.knName,
                type = HealthPlanType.valueOf(it.knType),
                isClockDisplay = it.isClockDisplay,
                isUsed = it.isUsed,
                isMonday = it.isMonday,
                isTuesday = it.isTuesday,
                isWednesday = it.isWednesday,
                isThursday = it.isThursday,
                isFriday = it.isFriday,
                isSaturday = it.isSaturday,
                isSunday = it.isSunday,
                createdAt = it.knCreatedAt,
                updatedAt = it.knUpdatedAt,
                createdBy = it.knCreatedBy,
                updatedBy = it.knUpdatedBy,
                cycleStartTime = it.knCycleStartTime,
                cycleEndTime = it.knCycleEndTime,
                subName = it.knSubName,
                desc = it.knDesc,
                time = it.knTime,
                displayTime = it.knDisplayTime,
                externalKey = it.externalKey,
                group = it.knGroup

            )
        } ?: throw MsgException(AppSpringUtil.getMessage("mrs.add-drug-health-plan-fail"))
    }
    @Transactional
    override fun updStatus(updStatusParams: UpdStatusParams): Boolean {
        // 查询所传ID是否存在
        mrHealthPlanTable.findByKnId(updStatusParams.id)
            ?: throw MsgException(AppSpringUtil.getMessage("mrs.no-find-data"))

        // 存在, 修改状态
        mrHealthPlanTable
            .update()
            .setIsUsed(updStatusParams.status)
            .where(MrHealthPlanTable.KnId.eq(arg(updStatusParams.id)))
            .execute()
        return true;
    }

    @Transactional
    override fun updBatchStatus(updBatchStatusParams: UpdBatchStatusParams) {
        val ids = updBatchStatusParams.id
        if (ids.isEmpty()) throw MsgException(AppSpringUtil.getMessage("mrs.parameter-can-not-empty"))
        // 存在, 修改状态
        mrHealthPlanTable
            .update()
            .setIsUsed(updBatchStatusParams.status)
            .where(MrHealthPlanTable.KnId `in` ids.map { it.arg })
            .execute()
    }

    /**
     * 用药提醒列表
     */
    override fun list(): List<RemindListResult> {
        val parentId = AppSecurityUtil.currentUserIdWithDefault()

        val list = mrHealthPlanTable
            .select()
            .where(MrHealthPlanTable.IsDel.eq(arg(false)))
            .where(MrHealthPlanTable.KnPatientId.eq(arg(parentId)))
            .where(MrHealthPlanTable.KnType.eq(arg(HealthPlanType.DRUG.name)))
            .find()

        return list.map {
            val weeks = mutableListOf<Week>()
            if (it.isMonday) weeks.add(Week.MONDAY)
            if (it.isTuesday) weeks.add(Week.TUESDAY)
            if (it.isWednesday) weeks.add(Week.WEDNESDAY)
            if (it.isThursday) weeks.add(Week.THURSDAY)
            if (it.isFriday) weeks.add(Week.FRIDAY)
            if (it.isSaturday) weeks.add(Week.SATURDAY)
            if (it.isSunday) weeks.add(Week.SUNDAY)

            RemindListResult(
                it.knId,
                it.knName,
                it?.knTime ?: LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
                it.isUsed,
                weeks
            )
        }
    }

    @Transactional
    override fun delRemind(id: Id): Boolean {
        // 执行删除语句
        mrHealthPlanTable
            .update()
            .setIsDel(true)
            .where(MrHealthPlanTable.KnId.eq(arg(id)))
            .execute()
        return true
    }

    @Transactional
    override fun batchDeleteByTypeAndPatientId(typesAndPatientParam: List<TypesAndPatientParam>) {
        val delList = mutableListOf<Id>()
        for (it in typesAndPatientParam) {
            // 根据type获取此患者下该类型的所有健康计划ID
            val ids = this.getHealthIdsByPatientAndTypes(it.patientId, it.types)
            delList.addAll(ids)
        }
        // 根据id列表删除这些健康计划
        this.delReminds(delList)
    }

    /**
     * 根据患者ID 和 types 获取健康计划ID
     */
    override fun getHealthIdsByPatientAndTypes(patientId: Id, types: List<HealthPlanType>): List<Id> {
        val healthPlanList = types.takeIf { it.isNotEmpty() }?.let {
            // 获取患者ID
            mrHealthPlanTable
                .select()
                .where(MrHealthPlanTable.KnType `in` types.map { arg(it.name) })
                .where(MrHealthPlanTable.IsDel eq arg(false))
                .where(MrHealthPlanTable.KnPatientId.eq(arg(patientId)))
                .find()
        }
        // 取出所有主键ID
        return healthPlanList?.map {
            it.knId
        }?: listOf()
    }

    @Transactional
    override fun delReminds(ids: List<Id>) {
        ids.takeIf { it.isNotEmpty() }?.let {
            // 执行删除语句
            mrHealthPlanTable
                .update()
                .setIsDel(true)
                .where(MrHealthPlanTable.KnId `in` ids.map { it.arg })
                .execute()
        }

    }
}