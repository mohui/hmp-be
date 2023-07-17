package com.bjknrt.health.scheme.constant

import com.bjknrt.health.scheme.vo.*

//server-name
const val MedicationRemindServer = "medication-remind-server"
const val QuestionAnsweringSystemServer = "question-answering-system-server"
const val DoctorPatientManagementServer = "doctor-patient-management-server"
const val HealthIndicatorServer = "health-indicator-server"
const val UserPermissionCentreServer = "user-permission-centre-server"

//job-name
const val HYPERTENSION_HEALTH_MANAGE_JOB_NAME = "hypertensionHealthManageJobHandler"
const val CEREBRAL_STROKE_CYCLE_PLAN_JOB_NAME = "cerebralStrokeCyclePlanJobHandler"
const val DIABETES_HEALTH_MANAGE_JOB_NAME = "diabetesHealthManageJobHandler"
const val COPD_HEALTH_MANAGE_JOB_NAME = "copdHealthManageJobHandler"
const val ACUTE_CORONARY_DISEASE_HEALTH_MANAGE_JOB_NAME = "acuteCoronaryDiseaseHealthManageJobHandler"

// 高血压测量计划
val BLOOD_PRESSURE_FREQUENCY_MAP = mapOf(
    ManageStage.INITIAL_STAGE to listOf(
        Frequency(
            frequencyTime = 7,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 4,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 7,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 2
            ),
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 2
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 2
        )
    ),
    ManageStage.STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 14,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 10,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 14
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
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
    ManageStage.METAPHASE_STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 4,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 4,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 4,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 3,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 3
            ),
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
    ManageStage.SECULAR_STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 12,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 8,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 12
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
)

//day
const val SEVEN_DAY: Long = 7
const val FOURTEEN_DAY: Long = 14
const val TWENTY_EIGHT_DAY: Long = 28
const val EIGHTY_FOUR_DAY: Long = 84

//month
const val THREE_MONTH = 3L
const val SIX_MONTH = 6L
const val TWELVE_MONTH = 12L


//服务包编码
const val HYPERTENSION_SERVICE_CODE = "htn"
const val DIABETES_SERVICE_CODE = "t2dm"
const val CEREBRAL_STROKE_SERVICE_CODE = "dnt"
const val COPD_SERVICE_CODE = "copd"
const val ACUTE_CORONARY_DISEASE_SERVICE_CODE = "chd"

//服务包编码与健康方案类型对应关系
val SERVICE_CODE_HEALTH_MANAGE_TYPE_MAP = mapOf(
    HYPERTENSION_SERVICE_CODE to HealthManageType.HYPERTENSION,
    DIABETES_SERVICE_CODE to HealthManageType.DIABETES,
    ACUTE_CORONARY_DISEASE_SERVICE_CODE to HealthManageType.ACUTE_CORONARY_DISEASE,
    CEREBRAL_STROKE_SERVICE_CODE to HealthManageType.CEREBRAL_STROKE,
    COPD_SERVICE_CODE to HealthManageType.COPD
)

//糖尿病服务-空腹血糖（餐前的早餐）计划频次map
val DIABETES_FASTING_BLOOD_SUGAR_FREQUENCY_MAP = mapOf(
    ManageStage.INITIAL_STAGE to listOf(
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 1,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 2
            ),
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 2
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
    ),
    ManageStage.STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 2,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 2,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 1
            ),
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
    ManageStage.METAPHASE_STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 4,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 4,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 4,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 1
            ),
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
    )
)

//糖尿病服务-餐前血糖(中、晚)计划频次map
val DIABETES_BEFORE_LUNCH_OR_DINNER_BLOOD_SUGAR_FREQUENCY_MAP = mapOf(
    ManageStage.INITIAL_STAGE to listOf(
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 1,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 2
            ),
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 2
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
    ),
    ManageStage.STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 2,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 2,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 1
            ),
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
    ManageStage.METAPHASE_STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 4,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 4,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 4,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 1
            ),
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
    )
)

//糖尿病服务-餐后2h血糖(早、中、晚)计划频次map
val DIABETES_AFTER_MEAL_ANY_BLOOD_SUGAR_FREQUENCY_MAP = mapOf(
    ManageStage.INITIAL_STAGE to listOf(
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 1,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 2
            ),
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 2
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
    ),
    ManageStage.STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 2,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 2,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 2
            ),
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 2
        ),
        Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.DAYS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
    ),
    ManageStage.METAPHASE_STABLE_STAGE to listOf(
        Frequency(
            frequencyTime = 4,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 4,
            frequencyNumUnit = FrequencyNumUnit.WEEKS,
            frequencyMaxNum = 4,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.WEEKS,
                frequencyNum = 1,
                frequencyNumUnit = FrequencyNumUnit.DAYS,
                frequencyMaxNum = 1
            ),
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
    )
)

//运动计划分组名称
const val AEROBIC_EXERCISE = "有氧运动"
const val RESISTANCE_EXERCISE = "抗阻运动"
const val BREATHING_EXERCISE = "呼吸操"

//饮食计划分组名称
const val DIET = "饮食"

//科普计划分组名称
const val SCIENCE_POPULARIZATION = "每日一科普"

//饮食打卡标准次数
val CLOCK_IN_DIET_MAP = mapOf(
    ManageStage.INITIAL_STAGE to 1,
    ManageStage.STABLE_STAGE to 2,
    ManageStage.METAPHASE_STABLE_STAGE to 4,
    ManageStage.SECULAR_STABLE_STAGE to 12,
)

//有氧运动打卡标准次数
val CLOCK_IN_AEROBIC_EXERCISE_MAP = mapOf(
    ManageStage.INITIAL_STAGE to mapOf(
        "步行" to 5,
        "慢跑" to 5,
        "骑自行车" to 5,
        "球类（乒乓球、羽毛球、网球）" to 5,
        "健身操" to 5,
        "游泳" to 3,
    ),
    ManageStage.STABLE_STAGE to mapOf(
        "步行" to 10,
        "慢跑" to 10,
        "骑自行车" to 10,
        "球类（乒乓球、羽毛球、网球）" to 10,
        "健身操" to 10,
        "游泳" to 6,
    ),
    ManageStage.METAPHASE_STABLE_STAGE to mapOf(
        "步行" to 20,
        "球类（乒乓球、羽毛球、网球）" to 20,
        "骑自行车" to 20,
        "球类" to 20,
        "健身操" to 20,
        "游泳" to 12,
    ),
    ManageStage.SECULAR_STABLE_STAGE to mapOf(
        "步行" to 60,
        "慢跑" to 60,
        "骑自行车" to 60,
        "球类（乒乓球、羽毛球、网球）" to 60,
        "健身操" to 60,
        "游泳" to 36,
    ),
)

//抗阻运动打卡标准次数
val CLOCK_IN_RESISTANCE_EXERCISE_MAP = mapOf(
    ManageStage.INITIAL_STAGE to mapOf(
        "握手训练" to 2,
    ),
    ManageStage.STABLE_STAGE to mapOf(
        "握手训练" to 4,
    ),
    ManageStage.METAPHASE_STABLE_STAGE to mapOf(
        "握手训练" to 8,
    ),
    ManageStage.SECULAR_STABLE_STAGE to mapOf(
        "握手训练" to 24,
    ),
)

//问卷编码
const val MRS_EXAMINATION_PAPER_CODE = "MRS"
const val BARTHEL_EXAMINATION_PAPER_CODE = "BARTHEL"
const val EQ5D_EXAMINATION_PAPER_CODE = "EQ5D"
const val REHABILITATION_TRAINING_EXAMINATION_PAPER_CODE = "REHABILITATION_TRAINING"
//选项编码
const val NORMAL_RECOVER_SPORT = "REHABILITATION_TRAINING_260001_26000101"
const val NORMAL_RECOVER_WORK = "REHABILITATION_TRAINING_260001_26000102"
const val NORMAL_RECOVER_ACKNOWLEDGE = "REHABILITATION_TRAINING_260001_26000103"
const val NORMAL_RECOVER_PAROLE = "REHABILITATION_TRAINING_260001_26000104"
const val NORMAL_RECOVER_SWALLOW = "REHABILITATION_TRAINING_260001_26000105"
const val NORMAL_RECOVER_NONE = "REHABILITATION_TRAINING_260001_26000106"

const val INTELLIGENT_RECOVER_BCI = "REHABILITATION_TRAINING_260002_26000201"
const val INTELLIGENT_RECOVER_ROBOT = "REHABILITATION_TRAINING_260002_26000202"
const val INTELLIGENT_RECOVER_BALANCE = "REHABILITATION_TRAINING_260002_26000203"
const val INTELLIGENT_RECOVER_VIRTUAL_REALITY = "REHABILITATION_TRAINING_260002_26000204"
const val INTELLIGENT_RECOVER_OTHER = "REHABILITATION_TRAINING_260002_26000205"
const val INTELLIGENT_RECOVER_NONE = "REHABILITATION_TRAINING_260002_26000206"

//慢阻肺间隔时间
const val COPD_INTERVAL_MONTH = 1L

