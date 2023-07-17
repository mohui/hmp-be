package com.bjknrt.medication.remind.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.entity.DrugDTO
import com.bjknrt.medication.remind.entity.DrugHealthPlanDTO
import com.bjknrt.medication.remind.entity.RemindListResult
import com.bjknrt.medication.remind.vo.*

interface MedicationRemindService {
    fun updStatus(updStatusParams: UpdStatusParams): Boolean

    fun updBatchStatus(updBatchStatusParams: UpdBatchStatusParams)

    fun upsert(upsertParams: DrugDTO): DrugHealthPlanDTO

    fun list(): List<RemindListResult>

    fun delRemind(id: Id): Boolean

    // 通过患者ID和types获取健康计划id列表
    fun getHealthIdsByPatientAndTypes(patientId: Id, types: List<HealthPlanType>): List<Id>

    /**
     * 根据list<患者ID, 健康计划type列表>批量删除健康计划
     */
    fun batchDeleteByTypeAndPatientId(typesAndPatientParam: List<TypesAndPatientParam>)

    fun delReminds(ids: List<Id>)
}