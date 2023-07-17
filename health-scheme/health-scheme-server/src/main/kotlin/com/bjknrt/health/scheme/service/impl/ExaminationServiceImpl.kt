package com.bjknrt.health.scheme.service.impl

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.HsManageDetailExaminationAdapter
import com.bjknrt.health.scheme.HsManageDetailExaminationAdapterTable
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.vo.AddCurrentSchemeExaminationAdapterParam
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ExaminationServiceImpl(
    val healthSchemeManageService: HealthSchemeManageService,
    val hsManageDetailExaminationAdapterTable: HsManageDetailExaminationAdapterTable
) : ExaminationService {
    override fun queryCurrentSchemeExaminationAdapterList(queryCurrentSchemeExaminationAdapterListParam: ExaminationService.QueryCurrentSchemeExaminationAdapterListParam): List<HsManageDetailExaminationAdapter> {
        val patientId = queryCurrentSchemeExaminationAdapterListParam.knPatientId
        val schemeId = queryCurrentSchemeExaminationAdapterListParam.knHealthManageSchemeId
        val examinationPaperCodeSet = queryCurrentSchemeExaminationAdapterListParam.knExaminationPaperCodeSet
        return hsManageDetailExaminationAdapterTable.select()
            .where(HsManageDetailExaminationAdapterTable.KnPatientId eq patientId)
            .where(HsManageDetailExaminationAdapterTable.KnHealthManageSchemeId eq schemeId)
            .where(HsManageDetailExaminationAdapterTable.KnExaminationPaperCode `in` examinationPaperCodeSet.map { arg(it.name) })
            .where(HsManageDetailExaminationAdapterTable.IsDel eq false)
            .order(HsManageDetailExaminationAdapterTable.KnOptionId, Order.Asc)
            .find() ?: emptyList()
    }

    @Transactional
    override fun deleteAndInsertSchemeExaminationAdapter(addCurrentSchemeExaminationAdapterParam: AddCurrentSchemeExaminationAdapterParam) {
        //1、先查询有效的健康方案，查不到则直接返回
        val patientId = addCurrentSchemeExaminationAdapterParam.knPatientId
        val examinationPaperCode = addCurrentSchemeExaminationAdapterParam.knExaminationPaperCode
        val healthManage = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)
        if (healthManage == null) {
            LOGGER.warn("该患者健康管理方案不存在，无法进行问卷选项的保存，患者Id:$patientId,问卷CODE:$examinationPaperCode")
            return
        }

        val schemeId = healthManage.knId

        //2、删除患者当前方案下旧的问卷适配数据
        hsManageDetailExaminationAdapterTable.update().setIsDel(true)
            .where(HsManageDetailExaminationAdapterTable.KnPatientId eq patientId)
            .where(HsManageDetailExaminationAdapterTable.KnHealthManageSchemeId eq schemeId)
            .where(HsManageDetailExaminationAdapterTable.KnExaminationPaperCode eq examinationPaperCode)
            .execute()

        //3、创建患者当前方案下新的问卷适配数据
        val manageDetailExaminationAdaptersList = addCurrentSchemeExaminationAdapterParam.knExaminationPaperOptionList.map { st ->
            HsManageDetailExaminationAdapter.builder()
                .setKnId(AppIdUtil.nextId())
                .setKnPatientId(patientId)
                .setKnHealthManageSchemeId(schemeId)
                .setKnExaminationPaperCode(examinationPaperCode)
                .setKnAnswerRecordId(st.knAnswerRecordId)
                .setKnAnswerResultId(st.knAnswerResultId)
                .setKnQuestionsId(st.knQuestionsId)
                .setKnOptionId(st.knOptionId)
                .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                .setIsDel(false)
                .setKnCreatedAt(LocalDateTime.now())
                .setKnOptionLabel(st.knOptionLabel)
                .setKnMessage(st.knMessage)
                .build()
        }
        insertSchemeExaminationAdapter(manageDetailExaminationAdaptersList)
    }

    @Transactional
    override fun insertSchemeExaminationAdapter(manageDetailExaminationAdapters: List<HsManageDetailExaminationAdapter>) {
        manageDetailExaminationAdapters.forEach { st ->
            hsManageDetailExaminationAdapterTable.insert(st)
        }
    }

}