package com.bjknrt.health.indicator.utils

import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.health.scheme.vo.Gender
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


data class IndicatorNotify(
    val id: BigInteger,
    val patientId: BigInteger,
    val indicatorName: String,
    val indicators: List<Indicator>,
    val createDate: LocalDateTime,
    val exceptionMessage: String
)

data class Indicator(
    val indicatorName: String,
    val indicatorValue: BigDecimal,
    val unit: String
)


//指标单位
//毫摩尔每升
const val M_MOL_L = "mmol/L"

//毫米汞柱
const val MM_HG = "mmHg"

//千克每平方米
const val KG_M2 = "kg/m²"

//厘米
const val CM = "cm"

//摄氏度
const val O_C = "℃"

//次/分
const val MINUTES_TIMES = "次/分"

//百分比
const val PERCENTAGE = "%"


//脉搏氧饱和度指标
const val PULSE_OXYGEN_NAME = "脉搏氧饱和度"
const val PULSE_OXYGEN_EXCEPTION_MESSAGE = "您的血氧过低，建议及时前往医院"
val PULSE_OXYGEN_ALERT_VALUE = BigDecimal(92)

/**
 * @description: 脉搏氧饱和度指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knPulseOximetry 脉搏氧饱和度值/%
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun pulseOxygenAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knPulseOximetry: BigDecimal,
    createDate: LocalDateTime
): IndicatorNotify? {
    return if (knPulseOximetry >= PULSE_OXYGEN_ALERT_VALUE) return null
    else IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = PULSE_OXYGEN_NAME,
        listOf(
            Indicator(
                indicatorName = PULSE_OXYGEN_NAME,
                indicatorValue = knPulseOximetry,
                unit = PERCENTAGE
            )
        ),
        createDate = createDate,
        exceptionMessage = PULSE_OXYGEN_EXCEPTION_MESSAGE
    )
}


//BMI指标
const val BMI_NAME = "BMI"
const val BMI_EXCEPTION_MESSAGE = "您当前属于%s，将BMI控制在18.5 kg/m² ~ 23.9 kg/m²之间有益您的健康"
const val BMI_ALERT_RULE_OVERWEIGHT_TAG = "超重"
const val BMI_EXCEPTION_MESSAGE_FAT_TAG = "肥胖"
const val BMI_EXCEPTION_MESSAGE_THIN_TAG = "消瘦"
val BMI_ALERT_VALUE_24 = BigDecimal(24)
val BMI_ALERT_VALUE_28 = BigDecimal(28)
val BMI_ALERT_VALUE_18_5 = BigDecimal(18.5)

/**
 * @description: BMI指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knBmi bmi值 kg/m2
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun bmiAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knBmi: BigDecimal,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag = if (knBmi >= BMI_ALERT_VALUE_24 && knBmi < BMI_ALERT_VALUE_28) BMI_ALERT_RULE_OVERWEIGHT_TAG
    else if (knBmi >= BMI_ALERT_VALUE_28) BMI_EXCEPTION_MESSAGE_FAT_TAG
    else if (knBmi < BMI_ALERT_VALUE_18_5) BMI_EXCEPTION_MESSAGE_THIN_TAG
    //else中表示knBmi >= BMI_ALERT_VALUE_18_5 && knBmi < BMI_ALERT_VALUE_24的情况，直接返回
    else return null
    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = BMI_NAME,
        indicators = listOf(
            Indicator(
                indicatorName = BMI_NAME,
                indicatorValue = knBmi,
                unit = KG_M2
            )
        ),
        createDate = createDate,
        exceptionMessage = String.format(BMI_EXCEPTION_MESSAGE, tag)
    )
}

//腰围指标
const val WAISTLINE_NAME = "腰围"
const val WAISTLINE_EXCEPTION_MESSAGE = "您的腰围%s，这对健康不利，建议您尽量保持挺腰收腹的姿势、清淡饮食、多做有氧运动。"
const val WAISTLINE_ALERT_RULE_OVERWEIGHT_TAG = "过大"
val WAISTLINE_ALERT_VALUE_85 = BigDecimal(85)
val WAISTLINE_ALERT_VALUE_90 = BigDecimal(90)

/**
 * @description: 腰围指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: gender 性别
 * @param: knWaistline 腰围的值/cm
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun waistlineAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    gender: Gender,
    knWaistline: BigDecimal,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag =
        if ((Gender.MAN == gender && knWaistline >= WAISTLINE_ALERT_VALUE_90)
            || (Gender.WOMAN == gender && knWaistline >= WAISTLINE_ALERT_VALUE_85)
        ) WAISTLINE_ALERT_RULE_OVERWEIGHT_TAG
        else return null
    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = WAISTLINE_NAME,
        indicators = listOf(
            Indicator(
                indicatorName = WAISTLINE_NAME,
                indicatorValue = knWaistline,
                unit = CM
            )
        ),
        createDate = createDate,
        exceptionMessage = String.format(WAISTLINE_EXCEPTION_MESSAGE, tag)
    )
}

//血压指标
const val BLOOD_PRESSURE_NAME = "血压"
const val SYSTOLIC_BLOOD_PRESSURE_NAME = "收缩压"
const val DIASTOLIC_BLOOD_PRESSURE_NAME = "舒张压"
const val BLOOD_PRESSURE_ALL_INDICATOR_EXCEPTION_MESSAGE = "您的血压异常，建议您尽快就医"
const val BLOOD_PRESSURE_TOO_HIGH_LOW_EXCEPTION_MESSAGE = "您的血压%s，建议您尽快就医"
const val BLOOD_PRESSURE_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE = "您的血压%s，如有不适请及时就医"
const val BLOOD_PRESSURE_ALERT_RULE_TOO_HIGH_TAG = "过高"
const val BLOOD_PRESSURE_ALERT_RULE_TOO_LOW_TAG = "过低"
const val BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG = "高于控制目标"
const val PATIENT_AGE = 65
val BLOOD_PRESSURE_ALERT_VALUE_60 = BigDecimal(60)
val BLOOD_PRESSURE_ALERT_VALUE_90 = BigDecimal(90)
val BLOOD_PRESSURE_ALERT_VALUE_110 = BigDecimal(110)
val BLOOD_PRESSURE_ALERT_VALUE_140 = BigDecimal(140)
val BLOOD_PRESSURE_ALERT_VALUE_150 = BigDecimal(150)
val BLOOD_PRESSURE_ALERT_VALUE_180 = BigDecimal(180)

/**
 * @description: 血压指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: age 年龄
 * @param: knSystolicBloodPressure 收缩压/mmHg
 * @param: knDiastolicBloodPressure 舒张压/mmHg
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun bloodPressureAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    age: Int,
    knSystolicBloodPressure: BigDecimal,
    knDiastolicBloodPressure: BigDecimal,
    createDate: LocalDateTime
): IndicatorNotify? {
    val sbpAlertTag = getSystolicBloodPressureAlertTag(knSystolicBloodPressure, age)
    val dbpAlertTag = getDiastolicBloodPressureAlertTag(knDiastolicBloodPressure)

    var indicators: List<Indicator>
    val expressionMessage =
        if (sbpAlertTag != null && dbpAlertTag != null) {
            indicators = listOf(
                Indicator(
                    indicatorName = SYSTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knSystolicBloodPressure,
                    unit = MM_HG
                ),
                Indicator(
                    indicatorName = DIASTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knDiastolicBloodPressure,
                    unit = MM_HG
                )
            )
            BLOOD_PRESSURE_ALL_INDICATOR_EXCEPTION_MESSAGE
        } else if (sbpAlertTag != null && sbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG) {
            indicators = listOf(
                Indicator(
                    indicatorName = SYSTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knSystolicBloodPressure,
                    unit = MM_HG
                )
            )
            String.format(
                BLOOD_PRESSURE_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE,
                sbpAlertTag
            )
        } else if (sbpAlertTag != null && (sbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_TOO_HIGH_TAG || sbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_TOO_LOW_TAG)) {
            indicators = listOf(
                Indicator(
                    indicatorName = SYSTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knSystolicBloodPressure,
                    unit = MM_HG
                )
            )
            String.format(
                BLOOD_PRESSURE_TOO_HIGH_LOW_EXCEPTION_MESSAGE,
                sbpAlertTag
            )
        } else if (dbpAlertTag != null && dbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG) {
            indicators = listOf(
                Indicator(
                    indicatorName = DIASTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knDiastolicBloodPressure,
                    unit = MM_HG
                )
            )
            String.format(
                BLOOD_PRESSURE_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE,
                dbpAlertTag
            )
        } else if (dbpAlertTag != null && (dbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_TOO_HIGH_TAG || dbpAlertTag == BLOOD_PRESSURE_ALERT_RULE_TOO_LOW_TAG)) {
            indicators = listOf(
                Indicator(
                    indicatorName = DIASTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = knDiastolicBloodPressure,
                    unit = MM_HG
                )
            )
            String.format(
                BLOOD_PRESSURE_TOO_HIGH_LOW_EXCEPTION_MESSAGE,
                dbpAlertTag
            )
        }
        //sbpAlertTag == null && dbpAlertTag == null
        else return null


    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = BLOOD_PRESSURE_NAME,
        indicators = indicators,
        createDate = createDate,
        exceptionMessage = expressionMessage
    )
}

/**
 * 生成收缩压预警标签
 */
private fun getSystolicBloodPressureAlertTag(knSystolicBloodPressure: BigDecimal, age: Int): String? {
    return if (knSystolicBloodPressure >= BLOOD_PRESSURE_ALERT_VALUE_180)
        BLOOD_PRESSURE_ALERT_RULE_TOO_HIGH_TAG
    else if ((age >= PATIENT_AGE && knSystolicBloodPressure >= BLOOD_PRESSURE_ALERT_VALUE_150 && knSystolicBloodPressure < BLOOD_PRESSURE_ALERT_VALUE_180)
        || (age < PATIENT_AGE && knSystolicBloodPressure >= BLOOD_PRESSURE_ALERT_VALUE_140 && knSystolicBloodPressure < BLOOD_PRESSURE_ALERT_VALUE_180)
    )
        BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG
    else if (knSystolicBloodPressure < BLOOD_PRESSURE_ALERT_VALUE_90)
        BLOOD_PRESSURE_ALERT_RULE_TOO_LOW_TAG
    else null
}

/**
 * 生成舒张压预警标签
 */
private fun getDiastolicBloodPressureAlertTag(knDiastolicBloodPressure: BigDecimal): String? {
    return if (knDiastolicBloodPressure >= BLOOD_PRESSURE_ALERT_VALUE_110)
        BLOOD_PRESSURE_ALERT_RULE_TOO_HIGH_TAG
    else if (knDiastolicBloodPressure >= BLOOD_PRESSURE_ALERT_VALUE_90 && knDiastolicBloodPressure < BLOOD_PRESSURE_ALERT_VALUE_110)
        BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG
    else if (knDiastolicBloodPressure < BLOOD_PRESSURE_ALERT_VALUE_60)
        BLOOD_PRESSURE_ALERT_RULE_TOO_LOW_TAG
    else null
}

//心率指标
const val HEART_RATE_NAME = "心率"
const val HEART_RATE_EXCEPTION_MESSAGE = "您的心率%s，请您多加监测，如有不适请及时就诊"
const val HEART_RATE_ALERT_RULE_TOO_FAST_TAG = "过快"
const val HEART_RATE_ALERT_RULE_TOO_SLOW_TAG = "过慢"
const val HEART_RATE_ALERT_VALUE_60 = 60
const val HEART_RATE_ALERT_VALUE_100 = 100

/**
 * @description: 心率指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knHeartRate 心率值
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun heartRateAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knHeartRate: Int,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag =
        if (knHeartRate < HEART_RATE_ALERT_VALUE_60) HEART_RATE_ALERT_RULE_TOO_SLOW_TAG
        else if (knHeartRate > HEART_RATE_ALERT_VALUE_100) HEART_RATE_ALERT_RULE_TOO_FAST_TAG
        else return null
    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = HEART_RATE_NAME,
        listOf(
            Indicator(
                indicatorName = HEART_RATE_NAME,
                indicatorValue = knHeartRate.toBigDecimal(),
                unit = MINUTES_TIMES
            )
        ),
        createDate = createDate,
        exceptionMessage = String.format(HEART_RATE_EXCEPTION_MESSAGE, tag)
    )
}

//脉搏指标
const val PULSE_NAME = "脉搏"
const val PULSE_EXCEPTION_MESSAGE = "您的脉搏%s，请您多加监测，如有不适请及时就诊"
const val PULSE_ALERT_RULE_TOO_FAST_TAG = "过快"
const val PULSE_ALERT_RULE_TOO_SLOW_TAG = "过慢"
const val PULSE_ALERT_VALUE_60 = 60
const val PULSE_ALERT_VALUE_100 = 100

/**
 * @description: 脉搏指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knPulse 脉搏值
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun pulseAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knPulse: Int,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag =
        if (knPulse < PULSE_ALERT_VALUE_60) PULSE_ALERT_RULE_TOO_SLOW_TAG
        else if (knPulse > PULSE_ALERT_VALUE_100) PULSE_ALERT_RULE_TOO_FAST_TAG
        else return null
    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = PULSE_NAME,
        listOf(
            Indicator(
                indicatorName = PULSE_NAME,
                indicatorValue = knPulse.toBigDecimal(),
                unit = MINUTES_TIMES
            )
        ),
        createDate = createDate,
        exceptionMessage = String.format(PULSE_EXCEPTION_MESSAGE, tag)
    )
}

//体温指标
const val BODY_TEMPERATURE_NAME = "体温"
const val BODY_TEMPERATURE_EXCEPTION_MESSAGE = "您处于%s，建议您及时就诊"
const val BODY_TEMPERATURE_ALERT_RULE_LOW_HEAT_TAG = "低热"
const val BODY_TEMPERATURE_ALERT_RULE_MODERATE_HEAT_TAG = "中热"
const val BODY_TEMPERATURE_ALERT_RULE_HIGH_HEAT_TAG = "高热"
const val BODY_TEMPERATURE_ALERT_RULE_SUPER_HEAT_TAG = "超高热"
val BODY_TEMPERATURE_ALERT_VALUE_37_5 = BigDecimal(37.5)
val BODY_TEMPERATURE_ALERT_VALUE_38 = BigDecimal(38)
val BODY_TEMPERATURE_ALERT_VALUE_39 = BigDecimal(39)
val BODY_TEMPERATURE_ALERT_VALUE_41 = BigDecimal(41)

/**
 * @description: 体温指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knCelsius 体温值
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun bodyTemperatureAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knCelsius: BigDecimal,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag =
        if (knCelsius in BODY_TEMPERATURE_ALERT_VALUE_37_5..BODY_TEMPERATURE_ALERT_VALUE_38) BODY_TEMPERATURE_ALERT_RULE_LOW_HEAT_TAG
        else if (knCelsius > BODY_TEMPERATURE_ALERT_VALUE_38 && knCelsius <= BODY_TEMPERATURE_ALERT_VALUE_39) BODY_TEMPERATURE_ALERT_RULE_MODERATE_HEAT_TAG
        else if (knCelsius > BODY_TEMPERATURE_ALERT_VALUE_39 && knCelsius <= BODY_TEMPERATURE_ALERT_VALUE_41) BODY_TEMPERATURE_ALERT_RULE_HIGH_HEAT_TAG
        else if (knCelsius > BODY_TEMPERATURE_ALERT_VALUE_41) BODY_TEMPERATURE_ALERT_RULE_SUPER_HEAT_TAG
        else return null
    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = BODY_TEMPERATURE_NAME,
        listOf(
            Indicator(
                indicatorName = BODY_TEMPERATURE_NAME,
                indicatorValue = knCelsius,
                unit = O_C
            )
        ),
        createDate = createDate,
        exceptionMessage = String.format(BODY_TEMPERATURE_EXCEPTION_MESSAGE, tag)
    )
}

//血糖指标
const val BLOOD_SUGAR_NAME = "血糖"
const val FASTING_BLOOD_SUGAR_NAME = "空腹血糖"
const val BEFORE_LUNCH_BLOOD_SUGAR_NAME = "餐前血糖（午餐）"
const val BEFORE_DINNER_BLOOD_SUGAR_NAME = "餐前血糖（晚餐）"
const val AFTER_MEAL_BLOOD_SUGAR_NAME = "餐后2h血糖"
const val AFTER_LUNCH_BLOOD_SUGAR_NAME = "餐后2h血糖（午餐）"
const val AFTER_DINNER_BLOOD_SUGAR_NAME = "餐后2h血糖（晚餐）"
const val RANDOM_BLOOD_SUGAR_NAME = "随机血糖"
const val BEFORE_SLEEP_BLOOD_SUGAR_NAME = "睡前血糖"
const val BLOOD_SUGAR_EXCEPTION_MESSAGE = "您的血糖%s，请及时就医"
const val BLOOD_SUGAR_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE = "您的血糖%s，请您多加监测，如有不适请及时就医"
const val BLOOD_SUGAR_ALERT_RULE_TOO_HIGH_TAG = "过高"
const val BLOOD_SUGAR_ALERT_RULE_TOO_LOW_TAG = "过低"
const val BLOOD_SUGAR_ALERT_RULE_ABOVE_CONTROL_STANDARD_TAG = "高于控制标准"
val BLOOD_SUGAR_ALERT_VALUE_2_8 = BigDecimal(2.8)
val BLOOD_SUGAR_ALERT_VALUE_3_9 = BigDecimal(3.9)
val BLOOD_SUGAR_ALERT_VALUE_7 = BigDecimal(7)
val BLOOD_SUGAR_ALERT_VALUE_13_9 = BigDecimal(13.9)
val BLOOD_SUGAR_ALERT_VALUE_10 = BigDecimal(10)

/**
 * @description: 血糖指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knFastingBloodSandalwood 空腹血糖mmol/L
 * @param: knBeforeLunchBloodSugar 餐前血糖mmol/L（午餐）
 * @param: knBeforeDinnerBloodSugar 餐前血糖mmol/L（晚餐）
 * @param: knAfterMealBloodSugar 餐后2h血糖mmol/L
 * @param: knAfterLunchBloodSugar 餐后2h血糖mmol/L（午餐）
 * @param: knAfterDinnerBloodSugar 餐后2h血糖mmol/L（晚餐）
 * @param: knRandomBloodSugar 随机血糖mmol/L
 * @param: knBeforeSleepBloodSugar 睡前血糖mmol/L
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun bloodSugarAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    diabetesDiseaseTag: PatientTag?,
    knFastingBloodSandalwood: BigDecimal?,
    knBeforeLunchBloodSugar: BigDecimal?,
    knBeforeDinnerBloodSugar: BigDecimal?,
    knAfterMealBloodSugar: BigDecimal?,
    knAfterLunchBloodSugar: BigDecimal?,
    knAfterDinnerBloodSugar: BigDecimal?,
    knRandomBloodSugar: BigDecimal?,
    knBeforeSleepBloodSugar: BigDecimal?,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tag =
        if ((diabetesDiseaseTag == PatientTag.EXISTS && knFastingBloodSandalwood != null && knFastingBloodSandalwood < BLOOD_SUGAR_ALERT_VALUE_3_9)
            || (diabetesDiseaseTag != PatientTag.EXISTS && knFastingBloodSandalwood != null && knFastingBloodSandalwood < BLOOD_SUGAR_ALERT_VALUE_2_8)
        ) BLOOD_SUGAR_ALERT_RULE_TOO_LOW_TAG
        else if (knFastingBloodSandalwood != null && knFastingBloodSandalwood > BLOOD_SUGAR_ALERT_VALUE_7 && knFastingBloodSandalwood <= BLOOD_SUGAR_ALERT_VALUE_13_9)
            BLOOD_SUGAR_ALERT_RULE_ABOVE_CONTROL_STANDARD_TAG
        else if (knFastingBloodSandalwood != null && knFastingBloodSandalwood > BLOOD_SUGAR_ALERT_VALUE_13_9)
            BLOOD_SUGAR_ALERT_RULE_TOO_HIGH_TAG
        else if (
            (diabetesDiseaseTag == PatientTag.EXISTS
                 && isAllIndicatorValueCheckPassLeftOpenRightOpen(
                    rightValue = BLOOD_SUGAR_ALERT_VALUE_3_9,
                    args = arrayOf(
                        knBeforeLunchBloodSugar,
                        knBeforeDinnerBloodSugar,
                        knAfterMealBloodSugar,
                        knAfterLunchBloodSugar,
                        knAfterDinnerBloodSugar,
                        knRandomBloodSugar,
                        knBeforeSleepBloodSugar
                        )
                    )
            ) ||
            (diabetesDiseaseTag != PatientTag.EXISTS
                 && isAllIndicatorValueCheckPassLeftOpenRightOpen(
                    rightValue = BLOOD_SUGAR_ALERT_VALUE_2_8,
                    args = arrayOf(knBeforeLunchBloodSugar,
                        knBeforeDinnerBloodSugar,
                        knAfterMealBloodSugar,
                        knAfterLunchBloodSugar,
                        knAfterDinnerBloodSugar,
                        knRandomBloodSugar,
                        knBeforeSleepBloodSugar
                        )
                    )
            )
        )
            BLOOD_SUGAR_ALERT_RULE_TOO_LOW_TAG
        else if (isAllIndicatorValueCheckPassLeftOpenRightClose(
                leftValue = BLOOD_SUGAR_ALERT_VALUE_10,
                rightValue = BLOOD_SUGAR_ALERT_VALUE_13_9,
                args = arrayOf(knBeforeLunchBloodSugar,
                knBeforeDinnerBloodSugar,
                knAfterMealBloodSugar,
                knAfterLunchBloodSugar,
                knAfterDinnerBloodSugar,
                knRandomBloodSugar,
                knBeforeSleepBloodSugar
            ))
        )
            BLOOD_SUGAR_ALERT_RULE_ABOVE_CONTROL_STANDARD_TAG
        else if (isAllIndicatorValueCheckPassLeftOpenRightOpen(
                leftValue = BLOOD_SUGAR_ALERT_VALUE_13_9,
                args = arrayOf(
                knBeforeLunchBloodSugar,
                knBeforeDinnerBloodSugar,
                knAfterMealBloodSugar,
                knAfterLunchBloodSugar,
                knAfterDinnerBloodSugar,
                knRandomBloodSugar,
                knBeforeSleepBloodSugar
            ))
        )
            BLOOD_SUGAR_ALERT_RULE_TOO_HIGH_TAG
        else return null

    val indicators = bloodSugarIndicators(
        knFastingBloodSandalwood,
        knBeforeLunchBloodSugar,
        knBeforeDinnerBloodSugar,
        knAfterMealBloodSugar,
        knAfterLunchBloodSugar,
        knAfterDinnerBloodSugar,
        knRandomBloodSugar,
        knBeforeSleepBloodSugar
    )

    val exceptionMessage =
        if (tag == BLOOD_SUGAR_ALERT_RULE_ABOVE_CONTROL_STANDARD_TAG)
            String.format(BLOOD_SUGAR_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE, tag)
        else
            String.format(BLOOD_SUGAR_EXCEPTION_MESSAGE, tag)

    return IndicatorNotify(
        id = id,
        patientId = patientId,
        indicatorName = BLOOD_SUGAR_NAME,
        indicators = indicators,
        createDate = createDate,
        exceptionMessage = exceptionMessage
    )
}

private fun bloodSugarIndicators(
    knFastingBloodSandalwood: BigDecimal?,
    knBeforeLunchBloodSugar: BigDecimal?,
    knBeforeDinnerBloodSugar: BigDecimal?,
    knAfterMealBloodSugar: BigDecimal?,
    knAfterLunchBloodSugar: BigDecimal?,
    knAfterDinnerBloodSugar: BigDecimal?,
    knRandomBloodSugar: BigDecimal?,
    knBeforeSleepBloodSugar: BigDecimal?,
): List<Indicator> {
    val indicators: MutableList<Indicator> = mutableListOf()
    if (knFastingBloodSandalwood != null) {
        indicators.add(
            Indicator(
                indicatorName = FASTING_BLOOD_SUGAR_NAME,
                indicatorValue = knFastingBloodSandalwood,
                unit = M_MOL_L
            )
        )
    } else if (knBeforeLunchBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = BEFORE_LUNCH_BLOOD_SUGAR_NAME,
                indicatorValue = knBeforeLunchBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knBeforeDinnerBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = BEFORE_DINNER_BLOOD_SUGAR_NAME,
                indicatorValue = knBeforeDinnerBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knAfterMealBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = AFTER_MEAL_BLOOD_SUGAR_NAME,
                indicatorValue = knAfterMealBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knAfterLunchBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = AFTER_LUNCH_BLOOD_SUGAR_NAME,
                indicatorValue = knAfterLunchBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knAfterDinnerBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = AFTER_DINNER_BLOOD_SUGAR_NAME,
                indicatorValue = knAfterDinnerBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knRandomBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = RANDOM_BLOOD_SUGAR_NAME,
                indicatorValue = knRandomBloodSugar,
                unit = M_MOL_L
            )
        )
    } else if (knBeforeSleepBloodSugar != null) {
        indicators.add(
            Indicator(
                indicatorName = BEFORE_SLEEP_BLOOD_SUGAR_NAME,
                indicatorValue = knBeforeSleepBloodSugar,
                unit = M_MOL_L
            )
        )
    } else {
        return indicators
    }
    return indicators
}

//血脂指标
const val BLOOD_LIPID_NAME = "血脂"
const val TOTAL_CHOLESTEROL_NAME = "总胆固醇"
const val TRIGLYCERIDES_NAME = "甘油三酯"
const val LOW_DENSITY_LIPOPROTEIN_NAME = "低密度脂蛋白"
const val HIGH_DENSITY_LIPOPROTEIN_NAME = "高密度脂蛋白"
const val BLOOD_LIPID_EXCEPTION_MESSAGE = "您的血脂控制不佳，请及时就医"
const val BLOOD_LIPID_ALERT_RULE_TOO_HIGH_TAG = "过高"
const val BLOOD_LIPID_ALERT_RULE_TOO_LOW_TAG = "过低"
val BLOOD_LIPID_ALERT_VALUE_6_2 = BigDecimal(6.2)
val BLOOD_LIPID_ALERT_VALUE_2_3 = BigDecimal(2.3)
val BLOOD_LIPID_ALERT_VALUE_4_1 = BigDecimal(4.1)
val BLOOD_LIPID_ALERT_VALUE_1 = BigDecimal(1.0)

/**
 * @description: 血脂指标异常预警规则
 * @param: id 指标id
 * @param: patientId 患者id
 * @param: knTotalCholesterol 总胆固醇
 * @param: knTriglycerides 甘油三酯
 * @param: knLowDensityLipoprotein 低密度脂蛋白
 * @param: knHighDensityLipoprotein 高密度脂蛋白
 * @param: createDate 创建时间
 * @return: 指标异常通知对象
 */
fun bloodLipidAlertRules(
    id: BigInteger,
    patientId: BigInteger,
    knTotalCholesterol: BigDecimal?,
    knTriglycerides: BigDecimal?,
    knLowDensityLipoprotein: BigDecimal?,
    knHighDensityLipoprotein: BigDecimal?,
    createDate: LocalDateTime
): IndicatorNotify? {
    val tooHighTag =
        if ((knTotalCholesterol != null && knTotalCholesterol >= BLOOD_LIPID_ALERT_VALUE_6_2)
            || (knTriglycerides != null && knTriglycerides >= BLOOD_LIPID_ALERT_VALUE_2_3)
            || (knLowDensityLipoprotein != null && knLowDensityLipoprotein >= BLOOD_LIPID_ALERT_VALUE_4_1)
        ) BLOOD_LIPID_ALERT_RULE_TOO_HIGH_TAG
        else null

    val tooLowTag =
        if (knHighDensityLipoprotein != null && knHighDensityLipoprotein < BLOOD_LIPID_ALERT_VALUE_1)
            BLOOD_LIPID_ALERT_RULE_TOO_LOW_TAG
        else null

    return if (tooHighTag != null || tooLowTag != null) {
        val indicators = bloodLipidIndicators(
            knTotalCholesterol,
            knTriglycerides,
            knLowDensityLipoprotein,
            knHighDensityLipoprotein
        )
        IndicatorNotify(
            id = id,
            patientId = patientId,
            indicatorName = BLOOD_LIPID_NAME,
            indicators = indicators,
            createDate = createDate,
            exceptionMessage = BLOOD_LIPID_EXCEPTION_MESSAGE
        )
    } else null
}

private fun bloodLipidIndicators(
    knTotalCholesterol: BigDecimal?,
    knTriglycerides: BigDecimal?,
    knLowDensityLipoprotein: BigDecimal?,
    knHighDensityLipoprotein: BigDecimal?,
): List<Indicator> {
    val indicators: MutableList<Indicator> = mutableListOf()
    if (knTotalCholesterol != null && knTotalCholesterol >= BLOOD_LIPID_ALERT_VALUE_6_2) {
        indicators.add(
            Indicator(
                indicatorName = TOTAL_CHOLESTEROL_NAME,
                indicatorValue = knTotalCholesterol,
                unit = M_MOL_L
            )
        )
    }
    if (knTriglycerides != null && knTriglycerides >= BLOOD_LIPID_ALERT_VALUE_2_3) {
        indicators.add(
            Indicator(
                indicatorName = TRIGLYCERIDES_NAME,
                indicatorValue = knTriglycerides,
                unit = M_MOL_L
            )
        )
    }
    if (knLowDensityLipoprotein != null && knLowDensityLipoprotein >= BLOOD_LIPID_ALERT_VALUE_4_1) {
        indicators.add(
            Indicator(
                indicatorName = LOW_DENSITY_LIPOPROTEIN_NAME,
                indicatorValue = knLowDensityLipoprotein,
                unit = M_MOL_L
            )
        )
    }
    if (knHighDensityLipoprotein != null && knHighDensityLipoprotein < BLOOD_LIPID_ALERT_VALUE_1) {
        indicators.add(
            Indicator(
                indicatorName = HIGH_DENSITY_LIPOPROTEIN_NAME,
                indicatorValue = knHighDensityLipoprotein,
                unit = M_MOL_L
            )
        )
    }
    return indicators
}