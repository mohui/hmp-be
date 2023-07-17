package com.bjknrt.health.indicator.event.listener

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.extension.LOGGER
import com.bjknrt.health.indicator.event.SaveIndicatorEvent
import com.bjknrt.health.indicator.utils.*
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.ClockInRequest
import com.bjknrt.health.scheme.vo.Gender
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.wechat.service.vo.IndicatorNotify
import com.bjknrt.wechat.service.vo.IndicatorNotifyIndicatorsInner
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * 保存指标的事件监听器
 */
@Component
class SaveIndicatorEventListener(
    val clockInRpcService: ClockInApi,
    val patientRpcService: PatientApi,
    val notifyRpcService: com.bjknrt.wechat.service.api.PatientApi
) : ApplicationListener<SaveIndicatorEvent> {
    override fun onApplicationEvent(event: SaveIndicatorEvent) {
        val indicatorParam = event.addIndicatorParam
        val patientId = indicatorParam.knPatientId
        when (indicatorParam.indicatorName) {
            BLOOD_PRESSURE_NAME -> {
                try {
                    //血压测量自动打卡
                    clockInRpcService.saveClockIn(
                        ClockInRequest(
                            patientId,
                            HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                            indicatorParam.knCreatedAt,
                            indicatorParam.knMeasureAt
                        )
                    )
                    //血压异常预警推送消息
                    val patientInfo = patientRpcService.getPatientInfo(patientId)
                    bloodPressureAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        age = patientInfo.age,
                        indicatorParam.extend1 ?: BigDecimal.ZERO,
                        indicatorParam.extend2 ?: BigDecimal.ZERO,
                        indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}血压测量打卡或预警推送异常, 血压测量主键是${indicatorParam.knId}", e)
                }
            }

            BMI_NAME -> {
                try {
                    //bmi指标异常预警推送微信消息
                    bmiAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knBmi = indicatorParam.extend1 ?: BigDecimal.ZERO,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}BMI指标预警推送异常, BMI主键是${indicatorParam.knId}", e)
                }
            }

            BLOOD_LIPID_NAME -> {
                try {
                    //血脂指标异常预警，推送微信消息
                    bloodLipidAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knTotalCholesterol = indicatorParam.extend1,
                        knTriglycerides = indicatorParam.extend2,
                        knLowDensityLipoprotein = indicatorParam.extend3,
                        knHighDensityLipoprotein = indicatorParam.extend4,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}BMI指标预警推送异常, BMI主键是${indicatorParam.knId}", e)
                }
            }

            BLOOD_SUGAR_NAME -> {
                try {
                    //血糖指标异常预警推送微信消息
                    val patientInfo = patientRpcService.getPatientInfo(patientId)
                    bloodSugarAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        diabetesDiseaseTag = patientInfo.diabetesDiseaseTag,
                        knFastingBloodSandalwood = indicatorParam.extend1,
                        knBeforeLunchBloodSugar = indicatorParam.extend2,
                        knBeforeDinnerBloodSugar = indicatorParam.extend3,
                        knAfterMealBloodSugar = indicatorParam.extend4,
                        knAfterLunchBloodSugar = indicatorParam.extend5,
                        knAfterDinnerBloodSugar = indicatorParam.extend6,
                        knRandomBloodSugar = indicatorParam.extend7,
                        knBeforeSleepBloodSugar = indicatorParam.extend8,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}血糖指标预警推送异常, 血糖主键是${indicatorParam.knId}", e)
                }
            }

            FASTING_BLOOD_SUGAR_NAME -> {
                try {
                    // 判断空腹血糖是否有值, 有值就打卡
                    leastOneNotNull(indicatorParam.extend1) {
                        clockInRpcService.saveClockIn(
                            ClockInRequest(
                                patientId,
                                HealthPlanType.FASTING_BLOOD_GLUCOSE,
                                indicatorParam.knCreatedAt,
                                indicatorParam.knMeasureAt
                            )
                        )
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}空腹血糖打卡异常, 主键是${indicatorParam.knId}", e)
                }
            }

            BEFORE_LUNCH_BLOOD_SUGAR_NAME -> {
                try {
                    //判断餐前血糖（午/晚）至少一个有值, 如果有值就打卡
                    leastOneNotNull(indicatorParam.extend1, indicatorParam.extend2) {
                        clockInRpcService.saveClockIn(
                            ClockInRequest(
                                patientId,
                                HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE,
                                indicatorParam.knCreatedAt,
                                indicatorParam.knMeasureAt
                            )
                        )
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}餐前血糖打卡异常, 主键是${indicatorParam.knId}", e)
                }
            }

            AFTER_MEAL_BLOOD_SUGAR_NAME -> {
                try {
                    //判断餐后2h血糖（早/中/晚）至少一个有值, 如果有值就打卡
                    leastOneNotNull(
                        indicatorParam.extend1,
                        indicatorParam.extend2,
                        indicatorParam.extend3
                    ) {
                        clockInRpcService.saveClockIn(
                            ClockInRequest(
                                patientId,
                                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
                                indicatorParam.knCreatedAt,
                                indicatorParam.knMeasureAt
                            )
                        )
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}餐后2h血糖打卡异常, 主键是${indicatorParam.knId}", e)
                }
            }

            HEART_RATE_NAME -> {
                try {
                    //心率指标异常预警推送微信消息
                    heartRateAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knHeartRate = indicatorParam.extend1!!.toInt(),
                        createDate = indicatorParam.knCreatedAt
                    )?.let {

                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}心率指标预警推送异常, 心率主键是${indicatorParam.knId}", e)
                }
            }

            PULSE_NAME -> {
                try {
                    //脉搏指标异常预警推送微信消息
                    pulseAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knPulse = indicatorParam.extend1!!.toInt(),
                        createDate = indicatorParam.knCreatedAt
                    )?.let {

                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}脉搏指标预警推送异常, 脉搏主键是${indicatorParam.knId}", e)
                }
            }

            PULSE_OXYGEN_NAME -> {
                try {
                    //脉搏氧饱和度指标自动打卡
                    clockInRpcService.saveClockIn(
                        ClockInRequest(
                            patientId,
                            HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN,
                            indicatorParam.knCreatedAt,
                            indicatorParam.knMeasureAt
                        )
                    )
                    //脉搏氧饱和度指标异常预警，推送微信消息
                    pulseOxygenAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knPulseOximetry = indicatorParam.extend1!!,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)

                    }
                } catch (e: Exception) {
                    LOGGER.warn(
                        "患者${patientId}脉搏氧饱和度指标测量打卡或预警推送异常, 主键是${indicatorParam.knId}",
                        e
                    )
                }
            }

            WAISTLINE_NAME -> {
                try {
                    //腰围指标异常预警，推送微信消息
                    val patientInfo = patientRpcService.getPatientInfo(patientId)
                    waistlineAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        gender = Gender.valueOf(patientInfo.gender.name),
                        knWaistline = indicatorParam.extend1!!,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {

                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}腰围指标预警推送异常, 主键是${indicatorParam.knId}", e)
                }
            }

            BODY_TEMPERATURE_NAME -> {
                try {
                    //体温指标异常预警，推送微信消息
                    bodyTemperatureAlertRules(
                        id = indicatorParam.knId,
                        patientId = patientId,
                        knCelsius = indicatorParam.extend1!!,
                        createDate = indicatorParam.knCreatedAt
                    )?.let {
                        indicatorNotifyPost(it)
                    }
                } catch (e: Exception) {
                    LOGGER.warn("患者${patientId}体温指标预警推送异常, 主键是${indicatorParam.knId}", e)
                }
            }

            else -> {}
        }

    }

    private fun indicatorNotifyPost(it: com.bjknrt.health.indicator.utils.IndicatorNotify) =
        notifyRpcService.patientNotifyIndicatorWarningPost(
            IndicatorNotify(
                id = it.id,
                patientId = it.patientId,
                name = it.indicatorName,
                indicators = it.indicators.map { indicator ->
                    IndicatorNotifyIndicatorsInner(
                        indicator.indicatorName,
                        indicator.indicatorValue.setScale(1),
                        indicator.unit
                    )
                },
                date = it.createDate,
                message = it.exceptionMessage
            )
        )
}
