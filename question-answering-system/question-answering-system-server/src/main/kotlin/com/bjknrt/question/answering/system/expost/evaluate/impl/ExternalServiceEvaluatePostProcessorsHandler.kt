package com.bjknrt.question.answering.system.expost.evaluate.impl

import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.FromTag
import com.bjknrt.health.indicator.vo.IndicatorEnum
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.ClockInRequest
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.QasQuestionsAnswerRecord
import com.bjknrt.question.answering.system.QasQuestionsAnswerResult
import com.bjknrt.question.answering.system.expost.evaluate.EvaluatePostProcessorsHandler
import com.bjknrt.security.client.AppSecurityUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

/**
 * 外部服务后置处理逻辑
 * 计划打卡与指标回传
 */
open class ExternalServiceEvaluatePostProcessorsHandler(
    val clockInRpcService: ClockInApi,
    val indicatorRpcService: IndicatorApi
) : EvaluatePostProcessorsHandler {

    protected open val examinationPaperCode = "BEHAVIOR"

    override fun getOrder(): Int {
        return 1
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return examinationPaper.examinationPaperCode == examinationPaperCode
    }

    override fun execute(answerRecord: QasQuestionsAnswerRecord, answerResultList: List<QasQuestionsAnswerResult>) {
        val answerBy = answerRecord.answerBy
        if (supportClock) {
            clock(answerBy)
        }
        if (supportSyncIndicator) {
            syncIndicator(answerBy, answerResultList);
        }
    }

    /** clock **/
    protected open val healthPlanType = HealthPlanType.BEHAVIOR_VISIT
    protected open val supportClock: Boolean = false
    private fun clock(answerBy: BigInteger) {
        clockInRpcService.saveClockIn(
            ClockInRequest(
                answerBy,
                healthPlanType,
                LocalDateTime.now()
            )
        )
    }


    /** 同步指标 **/
    protected open val supportSyncIndicator: Boolean = false

    protected open val optionIndicatorList = mapOf(
        BigInteger.valueOf(14000101) to IndicatorEnum.BODY_HEIGHT,
        BigInteger.valueOf(14000102) to IndicatorEnum.BODY_WEIGHT,
        BigInteger.valueOf(14000201) to IndicatorEnum.WAISTLINE
    )

    private fun syncIndicator(answerBy: BigInteger, answerResultList: List<QasQuestionsAnswerResult>) {
        //如果答卷人为当前用户，则为自填写
        var fromTag = FromTag.DOCTOR_VISIT
        if (answerBy == AppSecurityUtil.currentUserIdWithDefault())
            fromTag = FromTag.PATIENT_SELF
        var systolicPressure: BigDecimal? = null
        var diastolicBloodPressure: BigDecimal? = null
        var patientHeight: BigDecimal? = null
        var patientWeight: BigDecimal? = null
        var patientWaistline: BigDecimal? = null
        var fastingBloodSugar: BigDecimal? = null
        var serumTch: BigDecimal? = null
        //过滤设定指标的题目且有填写值
        answerResultList
            .filter { it.optionValue.isNotBlank() && optionIndicatorList.containsKey(it.optionId) }
            .forEach {
                //同步问卷指标
                when (optionIndicatorList[it.optionId]) {
                    IndicatorEnum.BODY_HEIGHT -> patientHeight = it.optionValue.toBigDecimal()

                    IndicatorEnum.BODY_WEIGHT -> patientWeight = it.optionValue.toBigDecimal()

                    IndicatorEnum.WAISTLINE -> patientWaistline = it.optionValue.toBigDecimal()

                    IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE -> systolicPressure = it.optionValue.toBigDecimal()

                    IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE -> diastolicBloodPressure = it.optionValue.toBigDecimal()

                    IndicatorEnum.FASTING_BLOOD_SUGAR -> fastingBloodSugar = it.optionValue.toBigDecimal()

                    IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL -> serumTch = it.optionValue.toBigDecimal()

                    else -> {}
                }
            }
        indicatorRpcService.batchAddIndicator(
            BatchIndicator(
                patientId = answerBy,
                knBodyHeight = patientHeight,
                knBodyWeight = patientWeight,
                knWaistline = patientWaistline,
                knSystolicBloodPressure = systolicPressure,
                knDiastolicBloodPressure = diastolicBloodPressure,
                knFastingBloodSandalwood = fastingBloodSugar,
                knTotalCholesterol = serumTch,
                fromTag = fromTag
            )
        )
    }

}
