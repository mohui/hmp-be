package com.bjknrt.medication.remind.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.api.MedicationRemindApi
import com.bjknrt.medication.remind.entity.DrugDTO
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.service.MedicationRemindService
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.medication.remind.api.MedicationRemindController")
class MedicationRemindController(
    val medicationRemindService: MedicationRemindService,
    val healthPlanService: HealthPlanService
) : AppBaseController(), MedicationRemindApi {
    @Transactional
    override fun batchDel(batchDelParam: BatchDelParam) {
        // 根据type获取此患者下该类型的所有健康计划ID
        val ids = healthPlanService.typeGetHealthIds(batchDelParam.patientId, batchDelParam.type)
        // 根据id列表删除这些健康计划
        medicationRemindService.delReminds(ids)
    }

    @Transactional
    override fun batchDeleteByTypeAndPatientId(typesAndPatientParam: List<TypesAndPatientParam>) {
        // 根据id列表删除这些健康计划
        medicationRemindService.batchDeleteByTypeAndPatientId(typesAndPatientParam)
    }

    @Transactional
    override fun batchIdDel(id: List<Id>) {
        medicationRemindService.delReminds(id)
    }

    @Transactional
    override fun del(id: Id): Boolean {
        // 执行删除语句
        return medicationRemindService.delRemind(id)
    }

    override fun list(): List<Inner> {
        return medicationRemindService.list().map {
            Inner(
                id = it.id,
                drugName = it.drugName,
                time = it.time,
                status = it.status,
                weeks = it.weeks
            )
        }
    }

    override fun updBatchStatus(updBatchStatusParams: UpdBatchStatusParams) {
        medicationRemindService.updBatchStatus(updBatchStatusParams)
    }

    override fun updStatus(updStatusParams: UpdStatusParams): Boolean {
        return medicationRemindService.updStatus(updStatusParams)
    }

    override fun upsert(upsertParams: UpsertParams): Boolean {
        medicationRemindService
            .upsert(
                DrugDTO(
                    drugName = upsertParams.drugName,
                    isUsed = true,
                    time = upsertParams.time,
                    frequencys = upsertParams.frequencys,
                    subName = null,
                    type = HealthPlanType.DRUG,
                    patientId = AppSecurityUtil.currentUserIdWithDefault(),
                    id = upsertParams.id,
                    cycleStartTime = null,
                    cycleEndTime = null
                )
            )
        return true
    }
}