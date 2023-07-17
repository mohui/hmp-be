package com.bjknrt.health.scheme.service.health.impl

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfoTable
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.HsHsmHealthPlanTable
import com.bjknrt.health.scheme.enums.DiseaseExistsTag
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.util.frequencyIdsToString
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HealthSchemeManageServiceImpl(
    val hsHsmHealthPlanTable: HsHsmHealthPlanTable,
    val hsHealthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable
) : HealthSchemeManageService {

    override fun saveHealthPlan(
        healthManageId: BigInteger,
        type: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        planId: BigInteger,
        frequencyIds: List<BigInteger>?,
        jobId: String?
    ): HsHsmHealthPlan {
        return hsHsmHealthPlanTable.save(
            HsHsmHealthPlan.builder()
                .setKnId(AppIdUtil.nextId())
                .setKnPlanType(type)
                .setKnStartDate(startDateTime)
                .setKnSchemeManagementId(healthManageId)
                .setKnForeignPlanId(planId)
                .setKnEndDate(endDateTime)
                .setKnForeignPlanFrequencyIds(frequencyIdsToString(frequencyIds))
                .setKnJobId(jobId)
                .setIsDel(false)
                .build()
        ) ?: throw KatoException(AppSpringUtil.getMessage("patient-health-manage-plan.save-error"))
    }

    override fun saveHealthPlan(healthPlan: HsHsmHealthPlan) {
        hsHsmHealthPlanTable.save(healthPlan)
    }

    override fun saveHealthManage(
        patientId: BigInteger,
        healthManageType: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        reportDate: LocalDate?,
        manageStage: String?,
        diseaseExistsTagList: List<DiseaseExistsTag>?
    ): HsHealthSchemeManagementInfo {
        val managementInfo = HsHealthSchemeManagementInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnStartDate(startDate)
            .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setKnPatientId(patientId)
            .setKnHealthManageType(healthManageType)
            .setKnDiseaseExistsTag("")
            .setKnCreatedAt(LocalDateTime.now())
            .setKnManagementStage(manageStage)
            .setKnEndDate(endDate)
            .setKnReportOutputDate(reportDate)
            .setKnJobId(null)
            .setIsDel(false)
            .build()
        return hsHealthSchemeManagementInfoTable.save(managementInfo)
            ?: throw KatoException(AppSpringUtil.getMessage("patient-health-manage-scheme.save-error"))
    }

    override fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        manageId: BigInteger?
    ): HsHealthSchemeManagementInfo? {
        val condition = hsHealthSchemeManagementInfoTable.select()
            .where(HsHealthSchemeManagementInfoTable.KnPatientId eq patientId.arg)
            .where(HsHealthSchemeManagementInfoTable.IsDel eq false)

        manageId?.let {
            condition.where(HsHealthSchemeManagementInfoTable.KnId lt it)
        }

        return condition.order(HsHealthSchemeManagementInfoTable.KnId, Order.Desc).findOne()
    }

    override fun getLastHealthSchemeManagementInfo(
        patientId: BigInteger,
        healthManageType: String
    ): HsHealthSchemeManagementInfo? {
        val condition = hsHealthSchemeManagementInfoTable.select()
            .where(HsHealthSchemeManagementInfoTable.KnHealthManageType eq healthManageType.arg)
            .where(HsHealthSchemeManagementInfoTable.KnPatientId eq patientId.arg)
            .where(HsHealthSchemeManagementInfoTable.IsDel eq false)
        return condition.order(HsHealthSchemeManagementInfoTable.KnId, Order.Desc).findOne()
    }

    override fun getCurrentValidHealthSchemeManagementInfo(
        patientId: BigInteger,
        currentDate: LocalDateTime
    ): HsHealthSchemeManagementInfo? {

        return this.getCurrentValidHealthSchemeManagementInfoStartWithCreatedAt(patientId,currentDate)
    }

    override fun getCurrentValidHealthSchemeManagementInfoStartWithCreatedAt(
        patientId: BigInteger,
        currentDate: LocalDateTime
    ): HsHealthSchemeManagementInfo? {

        return hsHealthSchemeManagementInfoTable.select()
            .where(HsHealthSchemeManagementInfoTable.KnPatientId eq patientId.arg)
            .where(HsHealthSchemeManagementInfoTable.KnCreatedAt lte  currentDate.arg)
            .where(HsHealthSchemeManagementInfoTable.IsDel eq false)
            .where(HsHealthSchemeManagementInfoTable.KnEndDate.isNull or (HsHealthSchemeManagementInfoTable.KnEndDate gt currentDate.arg))
            .order(HsHealthSchemeManagementInfoTable.KnId, Order.Desc)
            .findOne()
    }

    override fun getHealthSchemeManagementInfo(healthManageId: BigInteger): HsHealthSchemeManagementInfo? {
        return hsHealthSchemeManagementInfoTable.select()
            .where(HsHealthSchemeManagementInfoTable.KnId eq healthManageId.arg)
            .where(HsHealthSchemeManagementInfoTable.IsDel eq false)
            .findOne()
    }


    override fun getHealthPlanList(
        healthManageId: BigInteger,
        healthPlanTypes: List<HealthPlanType>
    ): List<HsHsmHealthPlan> {
        val condition = hsHsmHealthPlanTable.select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq healthManageId)
            .where(HsHsmHealthPlanTable.IsDel eq false)

        healthPlanTypes.takeIf { it.isNotEmpty() }?.let {
            condition.where(HsHsmHealthPlanTable.KnPlanType `in` healthPlanTypes.map { it.name.arg })
        }

        return condition
            .find()
            ?: listOf()
    }

    override fun getHealthPlanList(
        healthManageId: BigInteger,
        healthPlanTypeValue: List<String>,
        currentDateTime: LocalDateTime
    ): List<HsHsmHealthPlan> {
        return hsHsmHealthPlanTable.select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq healthManageId)
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .where(HsHsmHealthPlanTable.KnPlanType `in` healthPlanTypeValue.map { it.arg })
            .where(HsHsmHealthPlanTable.KnStartDate lte currentDateTime)
            .find()
            .filter { it.knEndDate?.let { endDateIt -> endDateIt > currentDateTime } ?: true }
    }

    override fun deleteHealthManage(healthManageId: BigInteger) {
        hsHealthSchemeManagementInfoTable.update()
            .setIsDel(true)
            .where(HsHealthSchemeManagementInfoTable.KnId eq healthManageId)
            .execute()
    }

    override fun deleteHealthPlan(healthManageId: BigInteger, type: String?) {
        val condition = hsHsmHealthPlanTable.update()
            .setIsDel(true)
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq healthManageId)
            .where(HsHsmHealthPlanTable.IsDel eq false)

        if (type != null) {
            condition.where(HsHsmHealthPlanTable.KnPlanType eq type)
        }

        condition.execute()
    }

    @Transactional
    override fun delHealthPlanByHealthManageIdAndTypes(healthManageId: BigInteger, types: List<HealthPlanType>) {
        hsHsmHealthPlanTable.update()
            .setIsDel(true)
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq healthManageId)
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .where(HsHsmHealthPlanTable.KnPlanType `in` types.map { it.name.arg })
            .execute()
    }

    override fun deleteHealthPlanList(healthPlanIds: List<BigInteger>) {
        hsHsmHealthPlanTable.update()
            .setIsDel(true)
            .where(HsHsmHealthPlanTable.IsDel eq false)
            .where(HsHsmHealthPlanTable.KnId `in` healthPlanIds.map { it.arg })
            .execute()
    }

    override fun updateHealthManage(healthManage: HsHealthSchemeManagementInfo) {
        hsHealthSchemeManagementInfoTable.upsert(healthManage)
    }

    override fun updateHealthPlan(healthPlan: HsHsmHealthPlan) {
        hsHsmHealthPlanTable.upsert(healthPlan)
    }
}
