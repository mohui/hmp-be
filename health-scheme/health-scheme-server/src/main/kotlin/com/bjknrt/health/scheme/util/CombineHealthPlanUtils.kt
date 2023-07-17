package com.bjknrt.health.scheme.util

import com.bjknrt.framework.enum.BooleanIntEnum
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.service.StandardVerificationService
import com.bjknrt.health.scheme.vo.*
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.LocalDateTime

/**
 * @description: 综合服务-健康计划参数工具类
 * @author: hgm
 */
@Component
class CombineHealthPlanUtils(
    val standardVerificationService: StandardVerificationService
) {


    companion object {
        //day
        const val SEVEN_DAY: Long = 7
        const val FOURTEEN_DAY: Long = 14
        const val TWENTY_EIGHT_DAY: Long = 28

        //综合服务-血压监测
        const val COMBINE_BLOOD_PRESSURE_PLAN_NAME = "测血压"
        val COMBINE_BLOOD_PRESSURE_MEASURE_DESC_MAP = mapOf(
            BooleanIntEnum.FALSE.value to "一周5天 一天1次",
            BooleanIntEnum.TRUE.value to "一周3天 一天1次"
        )

        //综合服务-空腹血糖监测
        const val COMBINE_FASTING_BLOOD_SUGAR_PLAN_NAME = "测空腹血糖"
        val COMBINE_FASTING_BLOOD_SUGAR_DESC_MAP = mapOf(
            BooleanIntEnum.FALSE.value to "一周3天 一天1次",
            BooleanIntEnum.TRUE.value to "一周1天 一天1次"
        )

        //综合服务-餐后2h血糖监测
        const val COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_PLAN_NAME = "测餐后2小时血糖/随机血糖"
        val COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_DESC_MAP = mapOf(
            BooleanIntEnum.FALSE.value to "一周3天 一天1次",
            BooleanIntEnum.TRUE.value to "一周1天 一天1次"
        )

        //综合服务-血压监测频次map
        val COMBINE_BLOOD_PRESSURE_FREQUENCY_MAP = mapOf(
            BooleanIntEnum.TRUE.value to listOf(
                Frequency(
                    frequencyTime = 4,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 4,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 3,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 3
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 3,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 3
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            ),
            BooleanIntEnum.FALSE.value to listOf(
                Frequency(
                    frequencyTime = 2,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 2,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 5,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 5
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 5,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 5
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            )

        )

        //综合服务-空腹血糖频次map
        val COMBINE_FASTING_BLOOD_SUGAR_FREQUENCY_MAP = mapOf(
            BooleanIntEnum.TRUE.value to listOf(
                Frequency(
                    frequencyTime = 4,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 4,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 1,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 1
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 1
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            ),
            BooleanIntEnum.FALSE.value to listOf(
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 1,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 3,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 3
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 3,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 3
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            )

        )

        //综合服务-餐后2h血糖频次map
        val COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_FREQUENCY_MAP = mapOf(
            BooleanIntEnum.TRUE.value to listOf(
                Frequency(
                    frequencyTime = 4,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 4,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 1,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 1
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 1
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            ),
            BooleanIntEnum.FALSE.value to listOf(
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 0,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 1,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 3,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 3
                    )
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 3,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 3
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.DAYS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1
                )
            )

        )


        //健康计划类型映射健康计划
        fun combineCommonFrequencyParams(
            planType: HealthPlanType,
            isStandard: Boolean,
            patientId: BigInteger,
            startDateTime: LocalDateTime
        ): FrequencyHealthParams? {
            return when (planType) {
                HealthPlanType.BLOOD_PRESSURE_MEASUREMENT -> bloodPressureFrequencyParams(
                    isStandard,
                    patientId,
                    startDateTime
                )

                HealthPlanType.FASTING_BLOOD_GLUCOSE -> fastingBloodSugarFrequencyParams(
                    isStandard,
                    patientId,
                    startDateTime
                )

                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE -> mealHourBloodSugarFrequencyParams(
                    isStandard,
                    patientId,
                    startDateTime
                )

                else -> null
            }
        }

        //血压监测参数
        fun bloodPressureFrequencyParams(
            isStandard: Boolean,
            patientId: BigInteger,
            startDateTime: LocalDateTime
        ): FrequencyHealthParams {
            val descTemp = if (isStandard)
                COMBINE_BLOOD_PRESSURE_MEASURE_DESC_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_BLOOD_PRESSURE_MEASURE_DESC_MAP[BooleanIntEnum.FALSE.value]

            val frequencyTemp = if (isStandard)
                COMBINE_BLOOD_PRESSURE_FREQUENCY_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_BLOOD_PRESSURE_FREQUENCY_MAP[BooleanIntEnum.FALSE.value]

            val cycle = if (isStandard) TWENTY_EIGHT_DAY else FOURTEEN_DAY

            return FrequencyHealthParams(
                name = COMBINE_BLOOD_PRESSURE_PLAN_NAME,
                type = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                patientId = patientId,
                desc = descTemp,
                cycleStartTime = startDateTime,
                cycleEndTime = startDateTime.plusDays(cycle),
                frequencys = frequencyTemp
            )
        }

        //空腹血糖参数
        fun fastingBloodSugarFrequencyParams(
            isStandard: Boolean,
            patientId: BigInteger,
            startDateTime: LocalDateTime
        ): FrequencyHealthParams {
            val descTemp = if (isStandard)
                COMBINE_FASTING_BLOOD_SUGAR_DESC_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_FASTING_BLOOD_SUGAR_DESC_MAP[BooleanIntEnum.FALSE.value]

            val frequencyTemp = if (isStandard)
                COMBINE_FASTING_BLOOD_SUGAR_FREQUENCY_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_FASTING_BLOOD_SUGAR_FREQUENCY_MAP[BooleanIntEnum.FALSE.value]

            val cycle = if (isStandard) TWENTY_EIGHT_DAY else SEVEN_DAY

            return FrequencyHealthParams(
                name = COMBINE_FASTING_BLOOD_SUGAR_PLAN_NAME,
                type = HealthPlanType.FASTING_BLOOD_GLUCOSE,
                patientId = patientId,
                desc = descTemp,
                cycleStartTime = startDateTime,
                cycleEndTime = startDateTime.plusDays(cycle),
                frequencys = frequencyTemp
            )
        }

        //餐后2h血糖参数
        fun mealHourBloodSugarFrequencyParams(
            isStandard: Boolean,
            patientId: BigInteger,
            startDateTime: LocalDateTime
        ): FrequencyHealthParams {
            val descTemp = if (isStandard)
                COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_DESC_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_DESC_MAP[BooleanIntEnum.FALSE.value]

            val frequencyTemp = if (isStandard)
                COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_FREQUENCY_MAP[BooleanIntEnum.TRUE.value]
            else
                COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_FREQUENCY_MAP[BooleanIntEnum.FALSE.value]

            val cycle = if (isStandard) TWENTY_EIGHT_DAY else SEVEN_DAY

            return FrequencyHealthParams(
                name = COMBINE_MEAL_TWO_HOUR_BLOOD_SUGAR_PLAN_NAME,
                type = HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
                patientId = patientId,
                desc = descTemp,
                cycleStartTime = startDateTime,
                cycleEndTime = startDateTime.plusDays(cycle),
                frequencys = frequencyTemp
            )
        }
    }

    fun onlineVisitCommonIsStandard(
        addManageRequest: AddManageRequest,
        healthManage: HsHealthSchemeManagementInfo,
        isFirstWeek: Boolean
    ): Boolean? {
        //1、根据五病标签走对应的判断规则分支来计算是否达标，进而获取到周期

        //1.1、有且仅有糖尿病的患者标签
        if (
            addManageRequest.diabetesDiseaseTag == PatientTag.EXISTS &&
            addManageRequest.hypertensionDiseaseTag != PatientTag.EXISTS &&
            addManageRequest.acuteCoronaryDiseaseTag != PatientTag.EXISTS &&
            addManageRequest.cerebralStrokeDiseaseTag != PatientTag.EXISTS &&
            addManageRequest.copdDiseaseTag != PatientTag.EXISTS
        ) {
            //根据判断规则，判断最近一次血糖是否达标
            //根据是否达标来计算周期
            return standardVerificationService.bloodSugarIsStandard(isFirstWeek, healthManage)
        }

        //1.2、不含有糖尿病患者标签且同时有(高血压/冠心病/脑卒中/慢阻肺)的患者
        else if (addManageRequest.diabetesDiseaseTag != PatientTag.EXISTS &&
            (addManageRequest.hypertensionDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.acuteCoronaryDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.cerebralStrokeDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.copdDiseaseTag == PatientTag.EXISTS)
        ) {
            //根据判断规则，判断此随访(测血压)周期内最近一次血压是否达标
            //根据是否达标来计算周期
            return standardVerificationService.lastTimeBloodPressureIsStandard(
                isFirstWeek,
                healthManage.knId,
                healthManage.knPatientId
            )
        }

        //1.3、有糖尿病且同时有高血压/冠心病/脑卒中/慢阻肺的患者
        else if (addManageRequest.diabetesDiseaseTag == PatientTag.EXISTS &&
            (addManageRequest.hypertensionDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.acuteCoronaryDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.cerebralStrokeDiseaseTag == PatientTag.EXISTS ||
                    addManageRequest.copdDiseaseTag == PatientTag.EXISTS)
        ) {
            //根据判断规则，判断此随访(测血压)周期内最近一次血压是否达标
            val isBloodPressureStandard = standardVerificationService.lastTimeBloodPressureIsStandard(
                isFirstWeek,
                healthManage.knId,
                healthManage.knPatientId
            )
            //根据判断规则，判断最近一次血糖是否达标
            val isBloodSugarStandard = standardVerificationService.bloodSugarIsStandard(isFirstWeek, healthManage)
            //根据是否达标来计算周期
            return isBloodPressureStandard && isBloodSugarStandard
        }
        return null
    }
}
