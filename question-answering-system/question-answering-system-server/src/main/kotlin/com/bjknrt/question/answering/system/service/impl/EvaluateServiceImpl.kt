package com.bjknrt.question.answering.system.service.impl

import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasDiseaseEvaluate
import com.bjknrt.question.answering.system.QasDiseaseEvaluateTable
import com.bjknrt.question.answering.system.assembler.addEvaluateRequestToDiseaseEvaluate
import com.bjknrt.question.answering.system.assembler.diseaseEvaluateToDiseaseEvaluateResponse
import com.bjknrt.question.answering.system.assembler.diseaseEvaluateToDiseaseOptionResponse
import com.bjknrt.question.answering.system.event.SyncPatientInfoEvent
import com.bjknrt.question.answering.system.service.AnswerHistoryService
import com.bjknrt.question.answering.system.service.EvaluateService
import com.bjknrt.question.answering.system.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigInteger

@Service
class EvaluateServiceImpl(
    val qasDiseaseEvaluateTable: QasDiseaseEvaluateTable,
    val answerHistoryService: AnswerHistoryService,
    val syncPatientInfoEvent: SyncPatientInfoEvent
) : EvaluateService {

    @Transactional
    override fun add(addEvaluateRequest: DiseaseEvaluateRequest): DiseaseEvaluateResponse {
        // 转换请求参数
        val diseaseEvaluate = addEvaluateRequestToDiseaseEvaluate(addEvaluateRequest)
        // 进行评估判断
        synthesisEvaluateHandler(diseaseEvaluate, addEvaluateRequest.age, addEvaluateRequest.gender)
        fiveDiseaseEvaluateHandler(diseaseEvaluate)
        // 进行数据存储
        qasDiseaseEvaluateTable.save(diseaseEvaluate)
        // 保存答题记录
        val saveRequest = buildSaveAnswerRecordRequest(addEvaluateRequest, diseaseEvaluate.synthesisDiseaseTag)
        answerHistoryService.saveAnswerRecord(saveRequest)
        // 同步评估结果到患者信息
        syncPatientInfoEvent.sync(diseaseEvaluate)
        // 返回结果
        return diseaseEvaluateToDiseaseEvaluateResponse(diseaseEvaluate)
    }

    private fun buildSaveAnswerRecordRequest(
        addEvaluateRequest: DiseaseEvaluateRequest,
        synthesisDiseaseTag: String
    ): SaveAnswerRecordRequest {
        return SaveAnswerRecordRequest(
            addEvaluateRequest.examinationPaperId,
            addEvaluateRequest.examinationPaperCode,
            synthesisDiseaseTag,
            addEvaluateRequest.patientId,
            AppSecurityUtil.currentUserIdWithDefault(),
        )
    }

    override fun getEvaluateInfo(patientId: BigInteger): DiseaseEvaluateResponse {
        val diseaseEvaluate = qasDiseaseEvaluateTable.select()
            .where(QasDiseaseEvaluateTable.PatientId eq patientId.arg)
            .order(QasDiseaseEvaluateTable.CreatedAt, Order.Desc)
            .findOne()
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("evaluate.result.not-found"))

        return diseaseEvaluateToDiseaseEvaluateResponse(diseaseEvaluate)
    }

    override fun getDiseaseOption(diseaseOptionRequest: DiseaseOptionRequest): List<DiseaseOption> {
        return qasDiseaseEvaluateTable.select()
            .where(QasDiseaseEvaluateTable.PatientId eq diseaseOptionRequest.patientId.arg)
            .where(QasDiseaseEvaluateTable.CreatedAt gte diseaseOptionRequest.startDate.arg)
            .where(QasDiseaseEvaluateTable.CreatedAt lt diseaseOptionRequest.endDate.arg)
            .order(QasDiseaseEvaluateTable.CreatedAt, Order.Desc)
            .find()
            .map { diseaseEvaluateToDiseaseOptionResponse(it) }
    }

    override fun getLastDiseaseOption(patientId: BigInteger): DiseaseOption {
        return qasDiseaseEvaluateTable.select()
            .where(QasDiseaseEvaluateTable.PatientId eq patientId.arg)
            .order(QasDiseaseEvaluateTable.CreatedAt, Order.Desc)
            .findOne()
            ?.let {
                diseaseEvaluateToDiseaseOptionResponse(it)
            } ?: DiseaseOption()
    }

    /**
     * 五病评估处理器
     * @param diseaseEvaluate 病种评估参数
     */
    private fun fiveDiseaseEvaluateHandler(diseaseEvaluate: QasDiseaseEvaluate) {
        // 专病风险默认为低危
        diseaseEvaluate.hypertensionDiseaseTag = PatientTag.LOW.value
        diseaseEvaluate.acuteCoronaryDiseaseTag = PatientTag.LOW.value
        diseaseEvaluate.copdDiseaseTag = PatientTag.LOW.value
        diseaseEvaluate.diabetesDiseaseTag = PatientTag.LOW.value
        diseaseEvaluate.cerebralStrokeDiseaseTag = PatientTag.LOW.value

        // 综合标签
        val synthesisTag = PatientSynthesisTag.valueOf(diseaseEvaluate.synthesisDiseaseTag)
        // 五病风险为低危，专病风险为低危。
        if (synthesisTag == PatientSynthesisTag.HIGH) {
            // 五病风险为高危的人群，如果有对应的专病症状即为专病高危。
            if (diseaseEvaluate.isSymptomDizzy) {
                diseaseEvaluate.hypertensionDiseaseTag = PatientTag.HIGH.value
            }
            if (diseaseEvaluate.isSymptomChestPain) {
                diseaseEvaluate.acuteCoronaryDiseaseTag = PatientTag.HIGH.value
            }
            if (diseaseEvaluate.isSymptomChronicCough) {
                diseaseEvaluate.copdDiseaseTag = PatientTag.HIGH.value
            }
            if (diseaseEvaluate.isSymptomWeightLoss) {
                diseaseEvaluate.diabetesDiseaseTag = PatientTag.HIGH.value
            }
            if (diseaseEvaluate.isSymptomGiddiness) {
                diseaseEvaluate.cerebralStrokeDiseaseTag = PatientTag.HIGH.value
            }
            // 五病风险为高危且没有任何专病症状专病风险为低危
            if (diseaseEvaluate.isSymptomNone) {
                diseaseEvaluate.hypertensionDiseaseTag = PatientTag.LOW.value
                diseaseEvaluate.acuteCoronaryDiseaseTag = PatientTag.LOW.value
                diseaseEvaluate.copdDiseaseTag = PatientTag.LOW.value
                diseaseEvaluate.diabetesDiseaseTag = PatientTag.LOW.value
                diseaseEvaluate.cerebralStrokeDiseaseTag = PatientTag.LOW.value
            }
        }
        // 1. 既往病史中有高血压、糖尿病、冠心病、脑卒中、慢阻肺中的任一种病时对应专病分析为“患者”标签
        if (diseaseEvaluate.isPmhEssentialHypertension) {
            diseaseEvaluate.hypertensionDiseaseTag = PatientTag.EXISTS.value
        }
        if (diseaseEvaluate.isPmhTypeTwoDiabetes) {
            diseaseEvaluate.diabetesDiseaseTag = PatientTag.EXISTS.value
        }
        if (diseaseEvaluate.isPmhCerebralInfarction) {
            diseaseEvaluate.cerebralStrokeDiseaseTag = PatientTag.EXISTS.value
        }
        if (diseaseEvaluate.isPmhCoronaryHeartDisease) {
            diseaseEvaluate.acuteCoronaryDiseaseTag = PatientTag.EXISTS.value
        }
        if (diseaseEvaluate.isPmhCopd) {
            diseaseEvaluate.copdDiseaseTag = PatientTag.EXISTS.value
        }
    }

    companion object {
        private const val AGE_LIMIT: Int = 50
        private val BMI_LIMIT: BigDecimal = BigDecimal.valueOf(24)
        private val MAN_WAISTLINE_LIMIT: BigDecimal = BigDecimal.valueOf(85)
        private val WOMAN_WAISTLINE_LIMIT: BigDecimal = BigDecimal.valueOf(80)
        private val HEIGHT_PRESSURE_LIMIT_START: BigDecimal = BigDecimal.valueOf(130)
        private val HEIGHT_PRESSURE_LIMIT_END: BigDecimal = BigDecimal.valueOf(139)
        private val LOW_PRESSURE_LIMIT_START: BigDecimal = BigDecimal.valueOf(85)
        private val LOW_PRESSURE_LIMIT_END: BigDecimal = BigDecimal.valueOf(89)
        private val BLOOD_SUGAR_LIMIT_HEIGHT: BigDecimal = BigDecimal.valueOf(85)
        private val BLOOD_SUGAR_LIMIT_LOW: BigDecimal = BigDecimal.valueOf(89)
        private val SERUM_TCH_LIMIT: BigDecimal = BigDecimal.valueOf(5.2)
        private const val HIGH_RISK_INDEX_NUM: Int = 3
    }

    /**
     * 综合评估处理器
     * @param diseaseEvaluate 病种评估参数
     * @param age 年龄
     * @param gender 性别
     */
    private fun synthesisEvaluateHandler(diseaseEvaluate: QasDiseaseEvaluate, age: Int, gender: Gender) {
        // 既往病史中有高血压、糖尿病、冠心病、脑卒中、慢阻肺中的任一种病时五病综合风险为高危。
        if (diseaseEvaluate.isPmhEssentialHypertension ||
            diseaseEvaluate.isPmhTypeTwoDiabetes ||
            diseaseEvaluate.isPmhCerebralInfarction ||
            diseaseEvaluate.isPmhCoronaryHeartDisease ||
            diseaseEvaluate.isPmhCopd
        ) {
            diseaseEvaluate.synthesisDiseaseTag = PatientSynthesisTag.HIGH.value
            return
        }
        // 1.年龄≥50 岁;
        var highRiskIndexNum = 0
        if (age >= AGE_LIMIT) {
            highRiskIndexNum++
        }
        // 2.超重且中心型肥胖前期：体重指数(BMI)≥24kg/m2 且男性腰围≥85cm， 女性腰围≥80cm;
        val bmi = diseaseEvaluate.patientWeight / ((diseaseEvaluate.patientHeight / BigDecimal.valueOf(100)).pow(2))
        val waistline = diseaseEvaluate.patientWaistline
        if (bmi >= BMI_LIMIT && gender == Gender.MAN && waistline >= MAN_WAISTLINE_LIMIT) {
            highRiskIndexNum++
        }
        if (bmi >= BMI_LIMIT && gender == Gender.WOMAN && waistline >= WOMAN_WAISTLINE_LIMIT) {
            highRiskIndexNum++
        }
        // 3.吸烟者；
        if (diseaseEvaluate.isSmoking) {
            highRiskIndexNum++
        }
        // 4.家族史：父母任一方患有高血压、糖尿病、冠心病、脑卒中及慢阻肺任一种疾病;
        if (diseaseEvaluate.isFhEssentialHypertension ||
            diseaseEvaluate.isFhTypeTwoDiabetes ||
            diseaseEvaluate.isFhCerebralInfarction ||
            diseaseEvaluate.isFhCoronaryHeartDisease ||
            diseaseEvaluate.isFhCopd
        ) {
            highRiskIndexNum++
        }
        // 5.血压水平为 130-139且85-89mmHg；
        val lowPressure = diseaseEvaluate.diastolicBloodPressure
        val heightPressure = diseaseEvaluate.systolicPressure
        if (lowPressure != null && heightPressure != null && lowPressure in LOW_PRESSURE_LIMIT_START..LOW_PRESSURE_LIMIT_END &&
            heightPressure in HEIGHT_PRESSURE_LIMIT_START..HEIGHT_PRESSURE_LIMIT_END
        ) {
            highRiskIndexNum++
        }
        // 6.空腹血糖水平为 6.1≤FBG<7.0mmol/L;
        val fastingBloodSugar = diseaseEvaluate.fastingBloodSugar
        if (fastingBloodSugar != null && fastingBloodSugar in BLOOD_SUGAR_LIMIT_LOW..BLOOD_SUGAR_LIMIT_HEIGHT) {
            highRiskIndexNum++
        }
        // 7.血清总胆固醇水平为 TC≥5.2mmol/L；
        val serumTch = diseaseEvaluate.serumTch
        if (serumTch != null && serumTch >= SERUM_TCH_LIMIT) {
            highRiskIndexNum++
        }
        // 统计高危指标数量
        if (highRiskIndexNum >= HIGH_RISK_INDEX_NUM) {
            diseaseEvaluate.synthesisDiseaseTag = PatientSynthesisTag.HIGH.value
        } else {
            diseaseEvaluate.synthesisDiseaseTag = PatientSynthesisTag.LOW.value
        }
    }
}
