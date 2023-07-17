package com.bjknrt.health.scheme.service.health.impl

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHsmHealthPlan
import com.bjknrt.health.scheme.constant.SERVICE_CODE_HEALTH_MANAGE_TYPE_MAP
import com.bjknrt.health.scheme.enums.ExaminationCodeEnum
import com.bjknrt.health.scheme.service.DietPlanService
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.service.health.HealthManageFacadeService
import com.bjknrt.health.scheme.service.health.HealthManageService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.SaveHealthManageParam
import com.bjknrt.health.scheme.transfer.buildDrugPlanDTO
import com.bjknrt.health.scheme.transfer.buildHealthPlanDTO
import com.bjknrt.health.scheme.transfer.transformRemindHealthType
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.HealthPlanMain
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import me.danwi.kato.common.exception.KatoException
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.reflect.jvm.jvmName

@Service
class HealthManageFacadeServiceImpl(
    private val healthSchemeManageService: HealthSchemeManageService,
    private val healthManageServiceList: List<HealthManageService>,
    private val dietPlanService: DietPlanService,
    private val healthPlanClient: HealthPlanApi,
    private val healthServiceClient: HealthServiceApi,
    private val examinationService: ExaminationService
) : HealthManageFacadeService {

    companion object {
        //TODO 补全健康方案实现映射
        val SERVICE_MAP_CLASS = mapOf(
            HealthManageType.HYPERTENSION to HealthManageHypertensionServiceImpl::class,
            HealthManageType.DIABETES to HealthManageDiabetesServiceImpl::class,
            HealthManageType.CEREBRAL_STROKE to HealthManageCerebralStrokeServiceImpl::class,
            HealthManageType.COPD to HealthManageCopdServiceImpl::class,
            HealthManageType.ACUTE_CORONARY_DISEASE to HealthManageAcuteCoronaryDiseaseServiceImpl::class,
        )

        //高血压阶段名称Map
        val MANAGEMENT_STAGE_NAME_MAP = mapOf(
            ManageStage.INITIAL_STAGE to "初始监测阶段",
            ManageStage.STABLE_STAGE to "短期稳定阶段",
            ManageStage.METAPHASE_STABLE_STAGE to "中期稳定阶段",
            ManageStage.SECULAR_STABLE_STAGE to "长期稳定阶段"
        )

        const val MANAGE_RESULT_STAGE = "常规监测阶段"

        const val CEREBRAL_STROKE_BLOOD_PRESSURE_TITLE = "一周一次"
        const val COPD_BLOOD_PRESSURE_TITLE = "1个月1次"

        const val DIABETES_FBG = "空腹血糖（早餐前）："
        const val DIABETES_PBG = "餐前血糖（中、晚）："
        const val DIABETES_BG2 = "餐后2小时血糖（任一餐后）："
        const val DIABETES_1_WEEK_2_DAY = "一周2天，一天1次"
        const val DIABETES_1_WEEK_1_DAY = "一周1天，一天1次"

        const val HYPERTENSION_INITIAL_STAGE_TITLE = "监测2周，每天1次。"
        const val HYPERTENSION_STABLE_STAGE_TITLE = "监测4周，每周3天，每天1次。"
        const val HYPERTENSION_METAPHASE_STABLE_STAGE_TITLE = "监测12周，每周1次。"
        const val HYPERTENSION_SECULAR_STABLE_STAGE_TITLE = "监测1周，每天2次，早晚各1次。"
        const val HYPERTENSION_DESC_1 =
            "根据您以前一天中血压最高的时间点去测量，如果您不知道自己一天中血压最高的时间，就统一测量早上的血压（即在早上起床后、服降压药和早餐前、排尿后，每天固定时间采取坐位测量）。"
        const val HYPERTENSION_DESC_2 =
            "早上测量血压需注意：最好在早上起床后、服降压药和早餐前、排尿后，每天固定时间采取坐位测量血压。\n 晚上测量血压的时间为睡觉前（尽量在睡前1小时内）。"

        const val DATE_FORMAT = "yyyy.MM.dd"

        const val POPULAR_SCIENCES_MSG = "阅读科普文章"
        const val POPULAR_SCIENCES_DESC = "每日一次"

        //血糖测量
        val BLOOD_GLUCOSE_TYPE_LIST = listOf(
            HealthPlanType.FASTING_BLOOD_GLUCOSE,
            HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE,
            HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
        )

        //脉搏氧饱和度计划
        val PULSE_OXYGEN_SATURATION_TYPE_LIST = listOf(
            HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN
        )

        //科普计划
        val POPULAR_SCIENCES_TYPE_LIST = listOf(
            HealthPlanType.SCIENCE_POPULARIZATION_PLAN
        )

        //随访计划
        val VISIT_TYPE_LIST = listOf(
            HealthPlanType.BEHAVIOR_VISIT,
            HealthPlanType.DIABETES_BEHAVIOR_VISIT,
            HealthPlanType.HYPERTENSION_VISIT,
            HealthPlanType.ONLINE_DIABETES,
            HealthPlanType.ONLINE_ACUTE_CORONARY_DISEASE,
            HealthPlanType.ONLINE_CEREBRAL_STROKE,
            HealthPlanType.ONLINE_COPD,
            HealthPlanType.OFFLINE_HYPERTENSION,
            HealthPlanType.OFFLINE_DIABETES,
            HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
            HealthPlanType.OFFLINE_CEREBRAL_STROKE,
            HealthPlanType.OFFLINE_COPD,
            HealthPlanType.CEREBRAL_STROKE,
            HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
            HealthPlanType.LEAVE_HOSPITAL_VISIT,
            HealthPlanType.ACUTE_CORONARY_DISEASE_BEHAVIOR_VISIT,
            HealthPlanType.COPD_BEHAVIOR_VISIT,
        )

        //体检计划
        val HEALTH_EXAMINATION_TYPE_LIST = listOf(
            HealthPlanType.PULMONARY_FUNCTION_EXAMINATION_PLAN,
            HealthPlanType.RESPIRATORY_DEPARTMENT_EXAMINATION,
        )

        //疫苗接种计划
        val VACCINATIONS_TYPE_LIST = listOf(
            HealthPlanType.INFLUENZA_VACCINATION,
            HealthPlanType.PNEUMOCOCCAL_VACCINATION,
        )

        //评估计划
        val EVALUATE_TYPE_LIST = listOf(
            HealthPlanType.MRS,
            HealthPlanType.BARTHEL,
            HealthPlanType.EQ_5_D,
            HealthPlanType.ZUNG_SELF_RATING_ANXIETY_SCALE,
            HealthPlanType.ZUNG_SELF_RATING_DEPRESSION_SCALE,
        )

        //康复计划
        val REHABILITATION_TYPE_LIST = listOf(
            HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
            HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT,
        )

        //运动计划
        val SPORT_TYPE_LIST = listOf(
            HealthPlanType.EXERCISE_PROGRAM
        )

        //饮食计划
        val FOOD_TYPE_LIST = listOf(
            HealthPlanType.DIET_PLAN
        )
        // 健康方案实现映射(代理)
        val SERVICE_MAP = mutableMapOf<HealthManageType, HealthManageService?>(
            HealthManageType.HYPERTENSION to null,
            HealthManageType.DIABETES to null,
            HealthManageType.CEREBRAL_STROKE to null,
            HealthManageType.COPD to null,
            HealthManageType.ACUTE_CORONARY_DISEASE to null,
        )
    }

    init {
        if (!(CollUtil.containsAll(
                SERVICE_MAP_CLASS.values,
                healthManageServiceList.map { (AopProxyUtils.getSingletonTarget(it) as HealthManageService):: class }
            ) && HealthManageType.values().size - 1 == healthManageServiceList.size)
        ) {
            val message = AppSpringUtil.getMessage("health-manage-scheme.mapping-lack")
            val builder = StringBuilder(message).append("\n")
            builder.append(
                "健康方案类型：",
                HealthManageType.values().filter { it != HealthManageType.NONE }.map { it.value + "\t" },
                "\n",
                "健康方案实现类：",
                SERVICE_MAP_CLASS.values.map { it.jvmName + "\t" }
            )
            throw KatoException(builder.toString())
        } else {
            healthManageServiceList
                .forEach {
                    val ser = AopProxyUtils.getSingletonTarget(it) as HealthManageService
                    for (item in SERVICE_MAP_CLASS) {
                        if (item.value == ser::class) {
                            SERVICE_MAP[item.key] = it
                        }
                    }
                }
        }
    }

    private fun getHealthManageService(healthManageType: HealthManageType): HealthManageService {
        return SERVICE_MAP[healthManageType]
            ?: throw KatoException(AppSpringUtil.getMessage("health-manage-scheme.mapping-lack"));
    }

    override fun saveHealthManage(saveHealthManageParam: SaveHealthManageParam): HsHealthSchemeManagementInfo? {
        val patientId = saveHealthManageParam.patientId

        //确认健康方案类型
        val healthManageType = getHealthManageType(patientId)

        if (HealthManageType.NONE == healthManageType) {
            LOGGER.warn("健康方案类型为NONE,无法创建健康方案。患者Id:{}", patientId)
            return null
        }

        //获取健康管理具体实现的服务
        val healthManageService = getHealthManageService(healthManageType)

        //幂等校验
        // 获取正在运行的健康方案
        healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)?.let { validHealthManage ->
            //切换方案时删除之前的健康方案
            healthManageService.removeHealthManage(validHealthManage.knId, validHealthManage.knJobId)
        }

        //保存健康方案
        val healthManage = healthManageService.saveHealthManage(
            patientId,
            healthManageType,
            saveHealthManageParam.startDate,
            null,
            null
        )

        //保存健康计划
        healthManageService.saveCommentHealthPlan(healthManage)
        val healthPlanList = healthManageService.saveHealthPlan(healthManage)

        //发布保存健康方案事件
        healthManageService.publishSaveHealthManageEvent(healthManage)
        //发布保存健康计划事件
        healthManageService.publishSaveHealthPlanEvent(healthManage, healthPlanList)
        return healthManage
    }

    override fun removeHealthManage(healthManageId: BigInteger) {
        val healthManage = healthSchemeManageService.getHealthSchemeManagementInfo(healthManageId)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("health-manage-scheme.is-null"))

        val healthManageService = getHealthManageService(HealthManageType.valueOf(healthManage.knHealthManageType))
        healthManageService.removeHealthManage(healthManageId, healthManage.knJobId)
    }

    override fun getManageDetail(patientId: BigInteger): HealthManageResult {
        healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)
            ?.let { managementInfo ->
                //管理阶段
                var stage: HealthManageResultStage? = null
                //血压测量计划
                var hypertension: HealthManageResultHypertension? = null
                //血糖测量计划
                var diabetes: List<String> = listOf()

                //需要查询健康提醒计划的Id集合
                val ids = healthSchemeManageService.getHealthPlanList(managementInfo.knId)
                    .map { it.knForeignPlanId }

                //查询所有的健康提醒计划
                val healthPlanList: List<HealthPlanMain> = healthPlanClient.idGetList(ids)
                    .filter {
                        //如果是出院随访计划，则完成后不显示，其他计划不影响
                        if (HealthPlanType.LEAVE_HOSPITAL_VISIT == transformRemindHealthType(it.type)) it.display else true
                    }

                val healthManageType = HealthManageType.valueOf(managementInfo.knHealthManageType)
                if (healthManageType == HealthManageType.CEREBRAL_STROKE) {
                    //管理阶段
                    stage = HealthManageResultStage(
                        title = MANAGE_RESULT_STAGE,
                        desc = ""
                    )
                    //血压测量计划
                    hypertension = HealthManageResultHypertension(
                        title = CEREBRAL_STROKE_BLOOD_PRESSURE_TITLE,
                        desc = ""
                    )
                } else if (healthManageType == HealthManageType.COPD) {
                    //管理阶段
                    stage = HealthManageResultStage(
                        title = MANAGE_RESULT_STAGE,
                        desc = ""
                    )
                    //血压测量计划
                    hypertension = HealthManageResultHypertension(
                        title = COPD_BLOOD_PRESSURE_TITLE,
                        desc = ""
                    )
                } else {
                    //管理阶段
                    var weeks = 0L
                    var desc = ""
                    val startDate = managementInfo.knStartDate
                    managementInfo.knEndDate?.let {

                        weeks = LocalDateTimeUtil.between(
                            startDate.atStartOfDay(),
                            it.atStartOfDay(),
                            ChronoUnit.WEEKS
                        )

                        val endDate = it.plusDays(-1)

                        desc =
                            "共${weeks}周 （${startDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-${
                                endDate.format(
                                    DateTimeFormatter.ofPattern(DATE_FORMAT)
                                )
                            }）"
                    }

                    //管理阶段
                    stage = HealthManageResultStage(
                        title = managementInfo.knManagementStage?.let { MANAGEMENT_STAGE_NAME_MAP[ManageStage.valueOf(it)] }
                            ?: "",
                        desc = desc
                    )

                    if (healthManageType == HealthManageType.DIABETES) {
                        //血糖测量计划
                        val diabetesList = mutableListOf<String>()
                        when (managementInfo.knManagementStage?.let { ManageStage.valueOf(it) }) {
                            ManageStage.INITIAL_STAGE -> {
                                diabetesList.add("$DIABETES_FBG$DIABETES_1_WEEK_2_DAY")
                                diabetesList.add("$DIABETES_PBG$DIABETES_1_WEEK_2_DAY")
                                diabetesList.add("$DIABETES_BG2$DIABETES_1_WEEK_2_DAY")
                            }

                            ManageStage.STABLE_STAGE -> {
                                diabetesList.add("$DIABETES_FBG$DIABETES_1_WEEK_1_DAY")
                                diabetesList.add("$DIABETES_PBG$DIABETES_1_WEEK_1_DAY")
                                diabetesList.add("$DIABETES_BG2$DIABETES_1_WEEK_2_DAY")
                            }

                            else -> {
                                diabetesList.add("$DIABETES_FBG$DIABETES_1_WEEK_1_DAY")
                                diabetesList.add("$DIABETES_PBG$DIABETES_1_WEEK_1_DAY")
                                diabetesList.add("$DIABETES_BG2$DIABETES_1_WEEK_1_DAY")
                            }
                        }
                        diabetes = diabetesList
                    }
                    if (healthManageType == HealthManageType.HYPERTENSION) {
                        //血压测量计划
                        hypertension = when (managementInfo.knManagementStage?.let { ManageStage.valueOf(it) }) {

                            ManageStage.STABLE_STAGE -> HealthManageResultHypertension(
                                title = HYPERTENSION_INITIAL_STAGE_TITLE,
                                desc = HYPERTENSION_DESC_1
                            )

                            ManageStage.METAPHASE_STABLE_STAGE -> HealthManageResultHypertension(
                                title = HYPERTENSION_STABLE_STAGE_TITLE,
                                desc = HYPERTENSION_DESC_1
                            )

                            ManageStage.SECULAR_STABLE_STAGE -> HealthManageResultHypertension(
                                title = HYPERTENSION_METAPHASE_STABLE_STAGE_TITLE,
                                desc = HYPERTENSION_DESC_1
                            )

                            else -> {
                                HealthManageResultHypertension(
                                    title = HYPERTENSION_SECULAR_STABLE_STAGE_TITLE,
                                    desc = HYPERTENSION_DESC_2
                                )
                            }
                        }
                    }
                }


                //随访列表
                val visits = healthPlanList.filter { VISIT_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                    .sortedBy { it.cycleStartTime }
                    .map {
                        //慢阻肺(线下随访)
                        if (it.type.name == HealthPlanType.OFFLINE_COPD.name) {
                            "${it.name} （上、下半年各完成1次）"
                        }
                        //高血压/冠心病/脑卒中/糖尿病
                        else if (
                            it.type.name == HealthPlanType.OFFLINE_DIABETES.name ||
                            it.type.name == HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE.name ||
                            it.type.name == HealthPlanType.OFFLINE_CEREBRAL_STROKE.name ||
                            it.type.name == HealthPlanType.OFFLINE_HYPERTENSION.name
                        ) {
                            "${it.name} （每个季度完成1次）"
                        }
                        //有结束时间展示周期
                        else if (it.cycleEndTime != null) {
                            "${it.name} " +
                                    "（${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    }）"
                        }
                        //其余情况显示
                        else {
                            it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                        }
                    }


                //运动计划
                val sports = healthPlanList.filter { SPORT_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                    .sortedBy { it.cycleStartTime }
                    .map {
                        it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                    }

                //运动禁忌查询
                val queryExaminationAdapterListParam = ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                    knPatientId = patientId,
                    knHealthManageSchemeId = managementInfo.knId,
                    knExaminationPaperCodeSet = setOf(ExaminationCodeEnum.SPORT)
                )
                val examinationAdapterOptionLabelList =
                    examinationService.queryCurrentSchemeExaminationAdapterList(queryExaminationAdapterListParam)
                        .mapNotNull { it.knOptionLabel }

                //饮食计划
                val foods = healthPlanList.filter { FOOD_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                    .sortedBy { it.cycleStartTime }
                    .flatMap { healthPlan ->
                        healthPlan.externalKey?.let { externalKey ->
                            dietPlanService
                                .typeGetList(TypeGetListParam(DietPlanType.valueOf(externalKey)))
                                .map { it.title }

                        } ?: listOf()
                    }
                //评估计划
                val evaluates =
                    healthPlanList.filter { EVALUATE_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map "${it.name} （每个阶段完成1次)"
                            }
                            return@map "${it.name} （${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    },完成1次）"
                        }
                //康复计划rehabilitation
                val rehabilitations =
                    healthPlanList.filter { REHABILITATION_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                            }
                            return@map "${it.name} （${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    }）"
                        }

                //脉搏氧饱和度计划
                val pulseOxygenSaturations =
                    healthPlanList.filter { PULSE_OXYGEN_SATURATION_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                            }
                            return@map "${it.name} （${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    }）"
                        }

                //科普计划
                val popularSciences =
                    healthPlanList.filter { POPULAR_SCIENCES_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map it.desc?.let { desc -> "$POPULAR_SCIENCES_MSG （${desc}）" }
                                    ?: "$POPULAR_SCIENCES_MSG （$POPULAR_SCIENCES_DESC）"
                            }
                            return@map "$POPULAR_SCIENCES_MSG （$POPULAR_SCIENCES_DESC）"
                        }
                //体检计划
                val healthExaminations =
                    healthPlanList.filter { HEALTH_EXAMINATION_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                            }
                            return@map "${it.name} （${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    }）"
                        }

                //疫苗接种计划
                val vaccinations =
                    healthPlanList.filter { VACCINATIONS_TYPE_LIST.contains(transformRemindHealthType(it.type)) }
                        .sortedBy { it.cycleStartTime }
                        .map {
                            if (it.cycleStartTime == null || it.cycleEndTime == null) {
                                return@map it.desc?.let { desc -> "${it.name} （${desc}）" } ?: "${it.name} "
                            }
                            return@map "${it.name} （${it.cycleStartTime?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}-" +
                                    "${
                                        it.cycleEndTime?.plusDays(-1)
                                            ?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                                    }）"
                        }

                // 用药详情
                var drugPrograms = listOf<String>()
                // 饮食详情
                var dietEvaluateItems = listOf<String>()
                if (healthManageType == HealthManageType.HYPERTENSION || healthManageType == HealthManageType.DIABETES) {
                    // 用药问卷code, 默认为糖尿病
                    var drugCode = ExaminationCodeEnum.DIABETES_DRUG_PROGRAM
                    // 饮食问卷code 默认为糖尿病
                    var dietCode = ExaminationCodeEnum.DIET_EVALUATE_DIABETES

                    if (healthManageType == HealthManageType.HYPERTENSION) {
                        // 高血压问卷用药code
                        drugCode = ExaminationCodeEnum.HYPERTENSION_DRUG_PROGRAM
                        // 高血压问卷饮食code
                        dietCode = ExaminationCodeEnum.DIET_EVALUATE_HYPERTENSION
                    }

                    // 根据患者ID、方案ID、问卷CODE查询问卷适配集合的接口, 只要一条
                    drugPrograms = examinationService.queryCurrentSchemeExaminationAdapterList(
                        ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                            knPatientId = patientId,
                            knHealthManageSchemeId = managementInfo.knId,
                            knExaminationPaperCodeSet = setOf(drugCode)
                        )
                    )
                        .mapNotNull { it.knMessage }
                        .take(1)

                    //饮食评估结果
                    val answerDetail = examinationService.queryCurrentSchemeExaminationAdapterList(
                        ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                            knPatientId = patientId,
                            knHealthManageSchemeId = managementInfo.knId,
                            knExaminationPaperCodeSet = setOf(dietCode)
                        )
                    )
                    dietEvaluateItems = answerDetail
                        .filter { !it.knMessage.isNullOrBlank() }
                        .distinctBy { it.knQuestionsId }
                        .map { i ->
                            answerDetail
                                .filter { i.knQuestionsId == it.knQuestionsId }
                                .map { it.knMessage }
                                .joinToString()
                        }
                }

                return HealthManageResult(
                    patientId = patientId,
                    createdAt = managementInfo.knCreatedAt.toLocalDate(),
                    createdBy = managementInfo.knCreatedBy,
                    diabetes = diabetes,
                    visits = visits,
                    sports = sports,
                    sportTabooList = examinationAdapterOptionLabelList,
                    foods = foods,
                    evaluates = evaluates,
                    rehabilitations = rehabilitations,
                    pulseOxygenSaturations = pulseOxygenSaturations,
                    popularSciences = popularSciences,
                    healthExaminations = healthExaminations,
                    vaccinations = vaccinations,
                    stage = stage,
                    hypertension = hypertension,
                    reportDate = managementInfo.knReportOutputDate,
                    drugPrograms = drugPrograms,
                    dietEvaluate = dietEvaluateItems
                )
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("patient-health-manage-scheme.not-found"))
    }

    @Transactional
    override fun saveHealthPlan(addHealthPlanParam: AddHealthPlanParam): List<HsHsmHealthPlan>? {
        return this.saveHealthPlanService(addHealthPlanParam, false)
    }

    @Transactional
    override fun addHealthPlan(addHealthPlanParam: AddHealthPlanParam): List<HsHsmHealthPlan>? {
        return this.saveHealthPlanService(addHealthPlanParam, true)
    }

    @Transactional
    override fun delHealthPlanByPatientIdAndTypes(
        delHealthPlanByPatientIdAndTypes: DelHealthPlanByPatientIdAndTypes
    ) {
        val patientId = delHealthPlanByPatientIdAndTypes.patientId
        val types = delHealthPlanByPatientIdAndTypes.types

        // 根据患者ID查询有效的健康方案
        val healthManage = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)

        if (healthManage == null) {
            LOGGER.warn(AppSpringUtil.getMessage("logger.manage-scheme.is-null.can-not-delete-health-plan"), patientId)
            return
        }
        return getHealthManageService(HealthManageType.valueOf(healthManage.knHealthManageType))
            .delHealthPlanByPatientIdAndHealthManageIdAndTypes(
                patientId = patientId,
                healthManageId = healthManage.knId,
                types = types
            )
    }

    private fun saveHealthPlanService(addHealthPlanParam: AddHealthPlanParam, isOnlyAdd: Boolean): List<HsHsmHealthPlan>? {
        val patientIdSet = mutableSetOf<Id>()
        addHealthPlanParam.healthPlans.forEach {
            patientIdSet.add(it.patientId)
        }
        addHealthPlanParam.drugPlans.forEach {
            patientIdSet.add(it.patientId)
        }
        val groupHealthPlan = addHealthPlanParam.healthPlans.groupBy { it.patientId }
        val groupDrugPlan = addHealthPlanParam.drugPlans.groupBy { it.patientId }
        val returnList = mutableListOf<HsHsmHealthPlan>()
        for (patientId in patientIdSet) {

            val healthManage = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfo(patientId)

            if (healthManage == null) {
                LOGGER.warn(AppSpringUtil.getMessage("logger.manage-scheme.is-null.can-not-create-health-plan"), patientId)
            } else {
                val healthPlanList = if (isOnlyAdd) {
                    // 如果是仅添加, 调用你此方法
                    getHealthManageService(HealthManageType.valueOf(healthManage.knHealthManageType))
                        .addHealthPlan(
                            patientId = patientId,
                            healthManageId = healthManage.knId,
                            healthPlans = buildHealthPlanDTO(groupHealthPlan[patientId]?: listOf()),
                            drugPlans = buildDrugPlanDTO(groupDrugPlan[patientId]?: listOf())
                        )
                } else {
                    // 如果不是仅添加, 调用先删除后添加的
                    getHealthManageService(HealthManageType.valueOf(healthManage.knHealthManageType))
                        .saveHealthPlanAndRemindPlan(
                            patientId = patientId,
                            healthManageId = healthManage.knId,
                            healthPlans = buildHealthPlanDTO(groupHealthPlan[patientId]?: listOf()),
                            drugPlans = buildDrugPlanDTO(groupDrugPlan[patientId]?: listOf())
                        )
                }

                healthPlanList?.let {
                    returnList.addAll(healthPlanList)
                }
            }
        }
        return returnList
    }

    /**
     * 获取健康方案类型
     * @param patientId 患者Id
     */
    fun getHealthManageType(
        patientId: BigInteger,
    ): HealthManageType {
        //查询患者订阅的服务包，并已订阅时间升序排序
        val servicePackageCodeList: List<String> = healthServiceClient.getHealthServicePatient(patientId)
            .filter { it.isSigned }
            .filter { it.activationDate != null }
            .sortedByDescending { it.activationDate }
            .map { it.healthServiceCode }

        if (servicePackageCodeList.isEmpty()) {
            return HealthManageType.NONE
        }
        return SERVICE_CODE_HEALTH_MANAGE_TYPE_MAP[servicePackageCodeList.first()] ?: HealthManageType.NONE
    }
}
