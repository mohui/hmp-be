package com.bjknrt.health.scheme.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.AbstractContainerBaseTest
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfoTable
import com.bjknrt.health.scheme.enums.ExaminationCodeEnum
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.vo.AddCurrentSchemeExaminationAdapterParam
import com.bjknrt.health.scheme.vo.ExaminationPaperOption
import com.bjknrt.health.scheme.vo.HealthManageType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

class ExaminationTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var examinationApi: ExaminationApi

    @Autowired
    lateinit var examinationService: ExaminationService

    @Autowired
    lateinit var hsHealthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable

    /**
     * 运动禁忌添加接口、查询接口测试
     */
    @Test
    fun sportTabooTest() {
        val knExaminationPaperCode = ExaminationCodeEnum.SPORT
        //准备添加参数
        val addParam =
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = AppIdUtil.nextId(),
                knExaminationPaperCode = knExaminationPaperCode.name,
                knExaminationPaperOptionList = listOf(
                    ExaminationPaperOption(
                        knAnswerRecordId = AppIdUtil.nextId(),
                        knAnswerResultId = AppIdUtil.nextId(),
                        knQuestionsId = AppIdUtil.nextId(),
                        knOptionId = AppIdUtil.nextId(),
                        knOptionLabel = "运动禁忌内容",
                        knMessage = "扩展信息"
                    )
                )
            )
        //准备方案数据
        val patientId = addParam.knPatientId
        val hsHealthSchemeManagementInfo = HsHealthSchemeManagementInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnStartDate(LocalDate.now().plusDays(1))
            .setKnCreatedBy(patientId)
            .setKnPatientId(patientId)
            .setKnHealthManageType(HealthManageType.HYPERTENSION.name)
            .setKnDiseaseExistsTag("HYPERTENSION")
            .setKnManagementStage(null)
            .setIsDel(false)
            .setKnCreatedAt(LocalDateTime.now())
            .setKnEndDate(null)
            .setKnJobId(null)
            .setKnReportOutputDate(null)
            .build()

        hsHealthSchemeManagementInfoTable.save(hsHealthSchemeManagementInfo)

        //调用添加运动禁忌接口
        examinationApi.syncCurrentSchemeExaminationAdapter(addParam)

        //准备查询参数
        val queryCurrentSchemeExaminationAdapterListParam =
            ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                knPatientId = patientId,
                knHealthManageSchemeId = hsHealthSchemeManagementInfo.knId,
                knExaminationPaperCodeSet = setOf(knExaminationPaperCode)
            )

        //调用查询问卷适配集合接口
        val sportTabooResultList =
            examinationService.queryCurrentSchemeExaminationAdapterList(queryCurrentSchemeExaminationAdapterListParam)

        //断言
        Assertions.assertEquals(
            addParam.knPatientId,
            sportTabooResultList[0].knPatientId
        )
        Assertions.assertEquals(
            addParam.knExaminationPaperCode,
            sportTabooResultList[0].knExaminationPaperCode
        )

        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knAnswerRecordId,
            sportTabooResultList[0].knAnswerRecordId
        )
        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knAnswerResultId,
            sportTabooResultList[0].knAnswerResultId
        )
        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knQuestionsId,
            sportTabooResultList[0].knQuestionsId
        )
        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knOptionId,
            sportTabooResultList[0].knOptionId
        )
        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knOptionLabel,
            sportTabooResultList[0].knOptionLabel
        )

        Assertions.assertEquals(
            addParam.knExaminationPaperOptionList[0].knMessage,
            sportTabooResultList[0].knMessage
        )
    }
}
