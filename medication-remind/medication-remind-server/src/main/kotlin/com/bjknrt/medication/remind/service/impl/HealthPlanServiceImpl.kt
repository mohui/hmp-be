package com.bjknrt.medication.remind.service.impl

import com.bjknrt.article.service.api.ArticleApi
import com.bjknrt.article.service.vo.ArticleAndCardRecommendParam
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.medication.remind.*
import com.bjknrt.medication.remind.dao.MrHealthPlanDAO
import com.bjknrt.medication.remind.entity.*
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.*
import me.danwi.sqlex.core.type.PagedResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class HealthPlanServiceImpl(
    val table: MrHealthPlanTable,
    val clockInTable: MrClockInTable,
    val mrFrequencyTable: MrFrequencyTable,
    val mrHealthPlanDAO: MrHealthPlanDAO,
    val articleServer: ArticleApi
) : HealthPlanService {
    /**
     * 打卡
     */
    @Transactional
    override fun clockIn(id: BigInteger, clockAt: LocalDateTime?) {
        val healthPlanOne = table.findByKnId(id)
        val currentTime = LocalDateTime.now()

        healthPlanOne?.let { healthPlanIt ->
            //打卡
            clockInTable.save(
                MrClockIn.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnHealthPlanId(id)
                    .setKnClockAt(clockAt ?: currentTime)
                    .setKnCreatedAt(currentTime)
                    .setKnCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
                    .setKnTime(healthPlanIt.knTime)
                    .build()
            )
        }
    }

    override fun clockInHistory(clockInHistoryParam: ClockInHistoryParam): List<ClockInHistoryResult> {
        // 患者ID
        val patientId = clockInHistoryParam.patientId
        // 日期
        val date = clockInHistoryParam.date

        val startDate = date.atStartOfDay()
        val endDate = date.plusDays(1).atStartOfDay()

        // 根据患者ID和时间获取打卡记录
        return mrHealthPlanDAO.clockInHistorySql(patientId, startDate, endDate)
            .map {
                ClockInHistoryResult(
                    healthPlanId = it.knId,
                    healthPlanName = it.knName,
                    clockInAt = it.knClockAt
                )
            }
    }

    /**
     * 新增健康计划
     */
    @Transactional
    override fun upsertHealth(frequencyHealthParams: FrequencyHealthParams): MrHealthPlan? {
        // 计划名称
        val name = frequencyHealthParams.name
        // 计划类型
        val type = frequencyHealthParams.type
        // 患者ID
        val patientId = frequencyHealthParams.patientId
        // 主键
        val id = frequencyHealthParams.id
        // 副标题
        val subName = frequencyHealthParams.subName
        // 描述
        val desc = frequencyHealthParams.desc
        // 饮食计划使用
        val externalKey = frequencyHealthParams.externalKey
        // 计划开始时间
        val cycleStartTime = frequencyHealthParams.cycleStartTime
        // 计划结束时间
        val cycleEndTime = frequencyHealthParams.cycleEndTime
        // 分组字段
        val group = frequencyHealthParams.group
        // 打卡完成后是否展示
        val clockDisplay = frequencyHealthParams.clockDisplay ?: true

        // 登录人id
        val createdId = AppSecurityUtil.currentUserIdWithDefault();
        // 当前日期时间
        val current = LocalDateTime.now()

        // 展示时间
        val displayTime = frequencyHealthParams.displayTime ?: frequencyHealthParams.cycleStartTime ?: current

        return table.saveOrUpdate(
            MrHealthPlan.builder()
                .setKnId(id ?: AppIdUtil.nextId())
                .setKnPatientId(patientId)
                .setKnType(type.name)
                .setKnCreatedAt(current)
                .setKnCreatedBy(createdId)
                .setKnUpdatedAt(current)
                .setKnUpdatedBy(createdId)
                .setKnName(name)
                .setKnSubName(subName)
                .setKnDesc(desc)
                .setIsMonday(false)
                .setIsTuesday(false)
                .setIsWednesday(false)
                .setIsThursday(false)
                .setIsFriday(false)
                .setIsSaturday(false)
                .setIsSunday(false)
                .setKnCycleStartTime(cycleStartTime ?: current)
                .setKnCycleEndTime(cycleEndTime)
                .setIsUsed(true)
                .setIsDel(false)
                .setExternalKey(externalKey)
                .setKnDisplayTime(displayTime)
                .setKnGroup(group)
                .setIsClockDisplay(clockDisplay)
                .build()
        )
    }

    @Transactional
    override fun upsertFrequency(frequency: List<FrequencyParams>): List<Id> {
        // 登录人id
        val patientId = AppSecurityUtil.currentUserIdWithDefault();
        // 当前日期时间
        val current = LocalDateTime.now()

        // ID数组
        val listId = mutableListOf<Id>()
        for (it in frequency) {
            mrFrequencyTable.saveOrUpdate(
                MrFrequency.builder()
                    .setKnId(it.id)
                    .setKnHealthPlanId(it.healthPlanId)
                    .setKnFrequencyTime(it.frequencyTime)
                    .setKnFrequencyTimeUnit(it.frequencyTimeUnit.name)
                    .setKnFrequencyNum(it.frequencyNum)
                    .setKnFrequencyNumUnit(it.frequencyNumUnit.name)
                    .setKnFrequencyMaxNum(it.frequencyMaxNum)
                    .setKnExplainId(it.explainId)
                    .setKnCreatedAt(current)
                    .setKnCreatedBy(patientId)
                    .build()
            )
            listId.add(it.id)
        }
        return listId
    }

    /**
     * 通过患者ID和type获取健康计划id列表
     */
    override fun typeGetHealthIds(patientId: Id, type: HealthPlanType): List<Id> {
        // 获取患者ID
        val healthPlanList = table
            .select()
            .where(MrHealthPlanTable.KnType eq arg(type.name))
            .where(MrHealthPlanTable.IsDel eq arg(false))
            .where(MrHealthPlanTable.KnPatientId.eq(arg(patientId)))
            .find()
        // 取出所有主键ID
        val ids = healthPlanList.map {
            it.knId
        }
        return ids
    }

    /**
     * 获取健康计划今日打卡情况列表
     */
    override fun getHealthPlanTodayClockList(patientId: Id): List<PlanClockResult> {
        val currentTime = LocalDateTime.now();
        // 今天开始时间
        val todayStart = LocalDateTime.now().toLocalDate().atStartOfDay()
        // 今天结束时间
        val todayEnd = LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
        // 查询列表, is_used: 当type是DRUG的时候,只显示true的, 否则都显示
        val healthPlanList = mrHealthPlanDAO.healthPlanTodayClockList(patientId, todayStart, todayEnd, currentTime)
        // 获取今天是周几
        val week = LocalDateTime.now().dayOfWeek.name
        // 过滤掉类型为周几几点的当天不显示的提醒计划
        val healthPlanResult = healthPlanList.filter {
            if (
                it.knType == HealthPlanType.DRUG.name ||
                it.knType == HealthPlanType.HYPERTENSION_DRUG_PROGRAM.name ||
                it.knType == HealthPlanType.DIABETES_DRUG_PROGRAM.name
            ) {
                when (week) {
                    Week.MONDAY.name -> it.isMonday
                    Week.TUESDAY.name -> it.isTuesday
                    Week.WEDNESDAY.name -> it.isWednesday
                    Week.THURSDAY.name -> it.isThursday
                    Week.FRIDAY.name -> it.isFriday
                    Week.SATURDAY.name -> it.isSaturday
                    Week.SUNDAY.name -> it.isSunday
                    else -> false
                }
            } else {
                true
            }

        }

        val result = mutableListOf<PlanClockResult>();
        for (remindIt in healthPlanResult) {
            val findIndex = result.find {
                it.id == remindIt.knId
            }
            if (findIndex == null) {
                val planClockResult = PlanClockResult(
                    id = remindIt.knId,
                    name = remindIt.knName,
                    type = HealthPlanType.valueOf(remindIt.knType),
                    clockIn = remindIt.count > 0,
                    subName = remindIt.knSubName,
                    desc = remindIt.knDesc,
                    time = remindIt.knTime,
                    cycleStartTime = if (remindIt.knCycleStartTime == null) remindIt.knCreatedAt else remindIt.knCycleStartTime,
                    cycleEndTime = remindIt.knCycleEndTime,
                    externalKey = remindIt.externalKey,
                    group = remindIt.knGroup,
                    frequency = null,
                    isClockDisplay = remindIt.isClockDisplay
                )
                result.add(planClockResult)
            }
        }
        return result;
    }

    /**
     * 计算周期
     */
    override fun calculationCycle(
        chronoNum: Int,
        chronoUnit: ChronoUnit,
        startDateTime: LocalDateTime,
        now: LocalDateTime
    ): CalculationCycleResult {
        val start: LocalDateTime
        val end: LocalDateTime

        val between: Long = chronoUnit.between(startDateTime, now) / chronoNum

        if (between <= 0) {
            start = startDateTime
            end = chronoUnit.addTo(startDateTime, chronoNum.toLong())
        } else {
            val temp = chronoUnit.addTo(startDateTime, between * chronoNum)
            if (temp.isAfter(now)) {
                start = chronoUnit.addTo(startDateTime, between * chronoNum - chronoNum)
                end = temp
            } else {
                start = temp
                end = chronoUnit.addTo(startDateTime, between * chronoNum + chronoNum)
            }
        }

        return CalculationCycleResult(
            start = start,
            end = end,
            between = between
        )
    }

    // 通过患者ID获取健康计划及其规则
    override fun getHealthPlanFrequency(ids: List<Id>): Map<Id, List<HealthPlanRule>> {

        // 转map
        val frequencies = mrFrequencyTable.select()
            .where(MrFrequencyTable.KnHealthPlanId `in` ids.map { it.arg })
            .order(MrFrequencyTable.KnId, Order.Asc)
            .find()

        return frequencies.takeIf { it.isNotEmpty() }
            ?.let { list ->
                // 转map
                val frequencieMap = list.mapNotNull { freq ->
                    HealthPlanRule(
                        id = freq.knId,
                        frequencyTime = freq.knFrequencyTime,
                        frequencyTimeUnit = TimeServiceUnit.valueOf(freq.knFrequencyTimeUnit),
                        frequencyNum = freq.knFrequencyNum,
                        frequencyNumUnit = TimeServiceUnit.valueOf(freq.knFrequencyNumUnit),
                        frequencyMaxNum = freq.knFrequencyMaxNum,
                    )

                }.associateBy { it.id }
                // 拼关系
                // - 关联下级
                list.forEach { freq ->
                    if (freq.knExplainId != null) {
                        frequencieMap[freq.knExplainId]?.children = frequencieMap[freq.knId]
                    }
                }
                // - 找出顶层对象
                list.filter { it.knExplainId == null && frequencieMap.contains(it.knId) }
                    .groupBy({ it.knHealthPlanId }, { frequencieMap[it.knId] ?: HealthPlanRule.EMPTY })
            } ?: mapOf()
    }

    /**
     * 获取有效打卡记录
     */
    override fun getClockIn(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        healthPlanId: Id,
        frequency: HealthPlanRule,
        now: LocalDateTime
    ): FrequencyClockResult {
        // 如果当前时间，超过结束时间则，默认为0
        endDateTime?.let {
            if (now >= it) return FrequencyClockResult(
                frequencyTime = frequency.frequencyTime,
                frequencyTimeUnit = frequency.frequencyTimeUnit,
                frequencyNum = frequency.frequencyNum,
                frequencyNumUnit = frequency.frequencyNumUnit,
                frequencyMaxNum = frequency.frequencyMaxNum,
                actualNum = 0,
                cycleStartTime = startDateTime,
                cycleEndTime = it
            )
        }

        // 计算动态周期
        val timeObj = this.calculationCycle(
            frequency.frequencyTime,
            ChronoUnit.valueOf(frequency.frequencyTimeUnit.name),
            startDateTime,
            now
        )
        val start = timeObj.start
        val end = timeObj.end

        // 获取最大时间范围内的所有打卡记录
        val clockInList = clockInTable
            .select()
            .where(MrClockInTable.KnHealthPlanId eq arg(healthPlanId))
            .where(MrClockInTable.KnClockAt gte arg(start))
            .where(MrClockInTable.KnClockAt lt arg(end))
            .find()

        // 计算逻辑
        val resultList = groupByFrequency(
            frequency,
            clockInList.map { GroupNode(it.knClockAt, null, null, it, null) },
            start,
            end
        )
        // 输出 frequency 统计数据
        val num = resultList.size

        return FrequencyClockResult(
            frequencyTime = frequency.frequencyTime,
            frequencyTimeUnit = frequency.frequencyTimeUnit,
            frequencyNum = frequency.frequencyNum,
            frequencyNumUnit = frequency.frequencyNumUnit,
            frequencyMaxNum = frequency.frequencyMaxNum,
            actualNum = num,
            cycleStartTime = start,
            cycleEndTime = end
        )
    }

    override fun listSort(planList: List<PlanClockResult>): List<PlanClockResult> {
        val returnList = mutableListOf<PlanClockResult>()
        // 用药提醒, 高血压,糖尿病用药提醒计划
        val drugs = mutableListOf<PlanClockResult>()
        // 具体运动方案 （运动计划）
        val exerciseProgram = mutableListOf<PlanClockResult>()
        // 测血压
        val bloodPressureMeasurement = mutableListOf<PlanClockResult>()
        // 未进行评估（运动计划）(尽快完成运动评估)、未进行评估（饮食）尽快完成饮食评估, 未进行用药评估(高血压, 糖尿病)
        val notEvaluated = mutableListOf<PlanClockResult>()
        // 运动调整提醒（运动计划）
        val motionAdjustmentRemind = mutableListOf<PlanClockResult>()
        // 空腹血糖
        val fastingBloodGlucose = mutableListOf<PlanClockResult>()
        // 餐后2h/随机血糖
        val mealTwoHourRandomBloodGlucose = mutableListOf<PlanClockResult>()
        // 饮食计划
        val dietPlan = mutableListOf<PlanClockResult>()
        // 科普计划
        val sciencePopularizationPlan = mutableListOf<PlanClockResult>()
        // 高血压随访
        val onlineVisit = mutableListOf<PlanClockResult>()
        // 线下随访
        val offlineVisit = mutableListOf<PlanClockResult>()
        // 其他
        val otherList = mutableListOf<PlanClockResult>()
        // 出院随访计划
        val leaveHospital = mutableListOf<PlanClockResult>()
        // 评估计划(mRS Barthel EQ-5D)
        val evaluated = mutableListOf<PlanClockResult>()
        // 康复训练
        val rehabilitationTraining = mutableListOf<PlanClockResult>()
        // 提醒查看报告
        val reminderViewReportPlan = mutableListOf<PlanClockResult>()
        // 提醒康复训练(尽快完成康复评估)
        val rehabilitationTrainingRemind = mutableListOf<PlanClockResult>()
        // 脉搏氧饱和度计划
        val pulseOxygenSaturationPlan = mutableListOf<PlanClockResult>()
        // 体检计划(肺功能检查计划,呼吸科体检)
        val physicalExaminationPlan = mutableListOf<PlanClockResult>()
        // 疫苗计划(接种流感疫苗, 接种肺炎球菌疫苗)
        val vaccinationPlan = mutableListOf<PlanClockResult>()

        // 先按照时间排序(倒序)
        val planList1 = planList.sortedWith { a, b -> b.id.compareTo(a.id) }
        for (listIt in planList1) {
            when (listIt.type) {
                // 用药提醒
                HealthPlanType.DRUG,
                    // 高血压用药
                HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
                    // 糖尿病用药
                HealthPlanType.DIABETES_DRUG_PROGRAM -> drugs.add(listIt)
                // 提醒查看报告
                HealthPlanType.REMINDER_VIEW_REPORT -> reminderViewReportPlan.add(listIt)
                // 出院随访
                HealthPlanType.LEAVE_HOSPITAL_VISIT -> leaveHospital.add(listIt)
                // 具体运动方案
                HealthPlanType.EXERCISE_PROGRAM -> exerciseProgram.add(listIt)
                // 测血压
                HealthPlanType.BLOOD_PRESSURE_MEASUREMENT -> bloodPressureMeasurement.add(listIt)
                // mRS(评估计划)
                HealthPlanType.MRS,
                    // Barthel(评估计划)
                HealthPlanType.BARTHEL,
                    // EQ-5D(评估计划)
                HealthPlanType.EQ_5_D,
                    // Zung氏焦虑自评量表(评估计划)
                HealthPlanType.ZUNG_SELF_RATING_ANXIETY_SCALE,
                    // Zung氏抑郁自评量表(评估计划)
                HealthPlanType.ZUNG_SELF_RATING_DEPRESSION_SCALE -> evaluated.add(listIt)
                // 提醒康复训练(未进行康复计划)(尽快完成康复评估)
                HealthPlanType.REHABILITATION_TRAINING_REMIND -> rehabilitationTrainingRemind.add(listIt)
                // 康复训练-常规训练
                HealthPlanType.REHABILITATION_TRAINING_ROUTINE,
                    // 康复训练-智能训练
                HealthPlanType.REHABILITATION_TRAINING_INTELLIGENT -> rehabilitationTraining.add(listIt)
                // 未进行评估（运动计划）尽快完成运动评估
                HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                    // 未进行用药评估(高血压)
                HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED,
                    // 未进行用药评估(糖尿病)]
                HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED,
                    // 未进行评估（饮食）尽快完成饮食评估
                HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION,
                HealthPlanType.DIET_NOT_EVALUATED_DIABETES -> notEvaluated.add(listIt)
                // 运动调整提醒（运动计划）
                HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND -> motionAdjustmentRemind.add(listIt)
                // 空腹血糖
                HealthPlanType.FASTING_BLOOD_GLUCOSE,
                    // 餐前血糖
                HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE -> fastingBloodGlucose.add(listIt)
                // 餐后2h/随机血糖
                HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE -> mealTwoHourRandomBloodGlucose.add(listIt)
                // 饮食计划
                HealthPlanType.DIET_PLAN -> dietPlan.add(listIt)
                // 科普计划
                HealthPlanType.SCIENCE_POPULARIZATION_PLAN -> sciencePopularizationPlan.add(listIt)
                // 行为习惯随访(线上)
                HealthPlanType.BEHAVIOR_VISIT,
                    // 糖尿病行为习惯随访
                HealthPlanType.DIABETES_BEHAVIOR_VISIT,
                    // 脑卒中行为习惯随访
                HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
                    // 高血压随访(线上随访)
                HealthPlanType.HYPERTENSION_VISIT,
                    // 糖尿病(线上随访)
                HealthPlanType.ONLINE_DIABETES,
                    // 冠心病(线上随访)
                HealthPlanType.ONLINE_ACUTE_CORONARY_DISEASE,
                    // 脑卒中(线上随访)
                HealthPlanType.ONLINE_CEREBRAL_STROKE,
                    // 慢阻肺(线上随访)
                HealthPlanType.ONLINE_COPD -> onlineVisit.add(listIt)
                // 高血压(线下随访)
                HealthPlanType.OFFLINE_HYPERTENSION,
                    // 糖尿病(线下随访)
                HealthPlanType.OFFLINE_DIABETES,
                    // 冠心病(线下随访)
                HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
                    // 脑卒中(线下随访)
                HealthPlanType.OFFLINE_CEREBRAL_STROKE,
                    // 慢阻肺(线下随访)
                HealthPlanType.OFFLINE_COPD -> offlineVisit.add(listIt)
                // 脉搏氧饱和度计划
                HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN -> pulseOxygenSaturationPlan.add(listIt)
                // 肺功能检查计划
                HealthPlanType.PULMONARY_FUNCTION_EXAMINATION_PLAN,
                    // 呼吸科体检
                HealthPlanType.RESPIRATORY_DEPARTMENT_EXAMINATION -> physicalExaminationPlan.add(listIt)
                // 接种流感疫苗(疫苗计划)
                HealthPlanType.INFLUENZA_VACCINATION,
                    // 接种肺炎球菌疫苗(疫苗计划)
                HealthPlanType.PNEUMOCOCCAL_VACCINATION -> vaccinationPlan.add(listIt)
                else -> otherList.add(listIt)
            }
        }
        drugs.sortBy { it.time }

        // 1、用药提醒(患者自填)
        returnList.addAll(drugs)
        // 2、提醒查看报告
        returnList.addAll(reminderViewReportPlan)
        // 3、出院随访
        returnList.addAll(leaveHospital)
        // 4、未进行评估（运动计划）尽快完成运动评估、未进行评估（饮食）尽快完成饮食评估, 未进行(高血压,糖尿病)用药评估
        returnList.addAll(notEvaluated)
        // 5、提醒康复训练(未进行康复计划)(尽快完成康复评估)
        returnList.addAll(rehabilitationTrainingRemind)
        // 6、运动调整提醒（运动计划）
        returnList.addAll(motionAdjustmentRemind)
        // 7、科普计划
        returnList.addAll(sciencePopularizationPlan)
        // 8、线上随访(行为习惯, 高血压, 糖尿病, 冠心病, 脑卒中, 慢阻肺)
        returnList.addAll(onlineVisit)
        // 9、测血压
        returnList.addAll(bloodPressureMeasurement)
        // 10、空腹血糖
        returnList.addAll(fastingBloodGlucose)
        // 11、餐后2h/随机血糖
        returnList.addAll(mealTwoHourRandomBloodGlucose)
        // 12、脉搏氧饱和度计划
        returnList.addAll(pulseOxygenSaturationPlan)
        // 13、具体运动方案（运动计划）
        returnList.addAll(exerciseProgram)
        // 14、(具体)康复训练
        returnList.addAll(rehabilitationTraining)
        // 15、饮食计划
        returnList.addAll(dietPlan)
        // 16、线下随访(高血压, 糖尿病, 冠心病, 脑卒中, 慢阻肺)
        returnList.addAll(offlineVisit)
        // 17. 评估计划(mRS,Barthel,EQ-5D,Zung氏焦虑自评量表,Zung氏抑郁自评量表)
        returnList.addAll(evaluated)
        // 18、体检计划
        returnList.addAll(physicalExaminationPlan)
        // 19、疫苗计划
        returnList.addAll(vaccinationPlan)
        // 20、其他
        returnList.addAll(otherList)

        return returnList
    }

    /**
     * 根据患者ID获取健康计划列表
     */
    override fun patientIdGetHealthPlanList(patientId: Id): List<HealthPlanEntity> {
        return table
            .select()
            .where(MrHealthPlanTable.IsDel eq arg(false))
            .where(MrHealthPlanTable.KnPatientId.eq(arg(patientId)))
            .find()
            .map {
                HealthPlanEntity(
                    id = it.knId,
                    patientId = it.knPatientId,
                    name = it.knName,
                    type = HealthPlanType.valueOf(it.knType),
                    clockIn = false,
                    isClockDisplay = it.isClockDisplay,
                    isUsed = it.isUsed,
                    cycleStartTime = it?.knCycleStartTime ?: it.knCreatedAt,
                    isMonday = it.isMonday,
                    isTuesday = it.isTuesday,
                    isWednesday = it.isWednesday,
                    isThursday = it.isThursday,
                    isFriday = it.isFriday,
                    isSaturday = it.isSaturday,
                    isSunday = it.isSunday,
                    isCycleClockIn = false,
                    display = true,
                    subName = it.knSubName,
                    desc = it.knDesc,
                    time = it.knTime,
                    cycleEndTime = it.knCycleEndTime,
                    displayTime = it.knDisplayTime,
                    externalKey = it.externalKey,
                    group = it.knGroup,
                    frequency = null,
                    todayFrequency = null,
                    subFrequency = null,
                    mainFrequency = null
                )
            }
    }

    /**
     * 根据主键ID数组获取健康计划列表
     */
    override fun idsGetHealthPlanList(ids: List<Id>): List<HealthPlanEntity> {
        return table
            .select()
            .where(MrHealthPlanTable.IsDel eq arg(false))
            .where(MrHealthPlanTable.KnId `in` ids.map { it.arg })
            .find()
            .map {
                HealthPlanEntity(
                    id = it.knId,
                    patientId = it.knPatientId,
                    name = it.knName,
                    type = HealthPlanType.valueOf(it.knType),
                    clockIn = false,
                    isClockDisplay = it.isClockDisplay,
                    isUsed = it.isUsed,
                    cycleStartTime = it?.knCycleStartTime ?: it.knCreatedAt,
                    isMonday = it.isMonday,
                    isTuesday = it.isTuesday,
                    isWednesday = it.isWednesday,
                    isThursday = it.isThursday,
                    isFriday = it.isFriday,
                    isSaturday = it.isSaturday,
                    isSunday = it.isSunday,
                    isCycleClockIn = false,
                    display = true,
                    subName = it.knSubName,
                    desc = it.knDesc,
                    time = it.knTime,
                    cycleEndTime = it.knCycleEndTime,
                    displayTime = it.knDisplayTime,
                    externalKey = it.externalKey,
                    group = it.knGroup,
                    frequency = null,
                    todayFrequency = null,
                    subFrequency = null,
                    mainFrequency = null
                )
            }
    }


    override fun pagePatientIds(pageNo: Long, pageSize: Long): PagedResult<BigInteger> {
        return mrHealthPlanDAO.pagePatientIds(pageSize, pageNo)
    }

    /**
     * 根据开始时间和结束时间, 健康计划ID获取打卡次数
     */
    override fun dateTimeGetClockIn(dateTimeGetClockInParams: DateTimeGetClockInParams): Int {
        return clockInTable
            .select()
            .where(MrClockInTable.KnHealthPlanId eq arg(dateTimeGetClockInParams.healthPlanId))
            .where(MrClockInTable.KnClockAt gte arg(dateTimeGetClockInParams.startTime))
            .where(MrClockInTable.KnClockAt lt arg(dateTimeGetClockInParams.endTime))
            .count()
            .toInt()

    }

    /**
     * 整理健康计划列表
     * 1. 根据患者ID获取健康计划列表
     * 2. 通过健康计划ID获取健康计划规则列表
     * 3. 把健康规则添加到数组中
     */
    override fun arrangementHealthPlanList(
        healthPlanModels: List<HealthPlanEntity>,
        patientId: Id?
    ): List<HealthPlanEntity> {
        // 2. 通过健康计划ID获取健康计划规则列表
        val healthPlanFrequencyMap = this.getHealthPlanFrequency(healthPlanModels.map { it.id })

        // 如果过滤出科普文章, 获取科普文章列表
        val articleResult = patientId?.let {
            healthPlanModels
                .filter { it.type == HealthPlanType.SCIENCE_POPULARIZATION_PLAN }
                .takeIf {
                    it.isNotEmpty()
                }
                ?.let {
                    articleServer.recommend(
                        ArticleAndCardRecommendParam(
                            pageNo = 1,
                            pageSize = 1,
                            readerId = patientId
                        )
                    )
                }
        }

        // 3. 把健康规则添加到数组中
        for (planIt in healthPlanModels) {
            healthPlanFrequencyMap[planIt.id]?.let { frequencyList ->
                var i = 0
                for (frequencyIt in frequencyList) {
                    if (i > 2) break
                    // 获取打卡记录
                    val f = this.getClockIn(planIt.cycleStartTime, planIt.cycleEndTime, planIt.id, frequencyIt)

                    // 打卡规则和实际打卡次数
                    val frequencyClockResult = FrequencyClockResult(
                        frequencyTime = frequencyIt.frequencyTime,
                        frequencyTimeUnit = frequencyIt.frequencyTimeUnit,
                        frequencyNum = frequencyIt.frequencyNum,
                        frequencyNumUnit = frequencyIt.frequencyNumUnit,
                        frequencyMaxNum = frequencyIt.frequencyMaxNum,
                        actualNum = f.actualNum,
                        cycleStartTime = f.cycleStartTime,
                        cycleEndTime = f.cycleEndTime
                    )
                    if (i == 0) {
                        // 如果结束时间为null, 用计算的周期时间
                        if (planIt.cycleEndTime == null) {
                            planIt.cycleStartTime = f.cycleStartTime
                            planIt.cycleEndTime = f.cycleEndTime
                        }
                        // 特殊处理 饮食计划和线下随访的desc返回值, 科普计划的文章
                        when (planIt.type) {
                            // 饮食计划
                            HealthPlanType.DIET_PLAN -> planIt.desc =
                                "${planIt.cycleStartTime.format(DateTimeFormatter.ofPattern("MM.dd"))}-${
                                    planIt.cycleEndTime?.plusSeconds(-1)?.format(
                                        DateTimeFormatter.ofPattern("MM.dd")
                                    )
                                }完成"
                            // 高血压线下随访
                            HealthPlanType.OFFLINE_HYPERTENSION,
                                // 糖尿病线下随访
                            HealthPlanType.OFFLINE_DIABETES,
                                // 冠心病线下随访
                            HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE,
                                // 脑卒中线下随访
                            HealthPlanType.OFFLINE_CEREBRAL_STROKE,
                                // 慢阻肺线下随访
                            HealthPlanType.OFFLINE_COPD -> planIt.desc = "${
                                planIt.cycleStartTime.format(
                                    DateTimeFormatter.ofPattern("MM.dd")
                                )
                            }-${
                                planIt.cycleEndTime?.plusSeconds(-1)?.format(
                                    DateTimeFormatter.ofPattern("MM.dd")
                                )
                            }完成1次"
                            // 科普计划
                            HealthPlanType.SCIENCE_POPULARIZATION_PLAN -> planIt.externalKey =
                                articleResult?._data?.takeIf { it.isNotEmpty() }?.first()?.id?.toString()
                            else -> {}
                        }

                        planIt.mainFrequency = frequencyClockResult
                        // 如果只有一条, 把 mainFrequency 的值也放到 subFrequency 里面
                        if (frequencyList.size == 1) {
                            planIt.subFrequency = planIt.mainFrequency
                        }
                    } else if (i == 1) {
                        planIt.subFrequency = frequencyClockResult
                    } else {
                        // 当天的
                        planIt.todayFrequency = frequencyClockResult
                    }
                    i++
                }
            }
            // 默认为未完成, 如果打卡数大于等于要求打卡数, 为完成
            planIt.isCycleClockIn = planIt.mainFrequency?.let { _it -> (_it.actualNum) >= (_it.frequencyNum) } ?: false
            // 是否展示, 如果打卡后展示字段为true, 并且打卡已完成未true, 否则为false
            planIt.display = planIt.isClockDisplay || !planIt.isCycleClockIn

        }
        return healthPlanModels
    }

    data class GroupNode(
        val key: LocalDateTime,
        var keyNum: Int? = null, // 为null说明未分组
        var keyUnit: TimeServiceUnit? = null, // 为null说明未分组
        var value: MrClockIn? = null,
        var child: MutableSet<GroupNode>? = null
    )

    /**
     * 根据频次深度分组
     *
     * 0. 以周期分割时间，以频次数量统计有效数据
     * 1. 下级周期必须和上级频次单位和数量相同
     * 2. 末级频次单位不是次的，补充次的统计逻辑
     * 3. 顶级不用计算（下级统计的就是顶级需要的）
     * 4. 下级周期值目前只支持1
     *
     */
    private fun groupByFrequency(
        frequency: HealthPlanRule,
        originList: Collection<GroupNode>,
        start: LocalDateTime,
        end: LocalDateTime,
        deep: Int = 1
    ): Collection<GroupNode> {

        var resultList: Collection<GroupNode> = originList
        var hasChild = false

        frequency.children?.let {
            resultList = groupByFrequency(it, originList, start, end, deep + 1)
            hasChild = true
        }

        // 获取分组时间段
        val isSequence = frequency.frequencyNumUnit == TimeServiceUnit.SEQUENCE // 单位是否是次
        val bucket = mutableListOf<LocalDateTime>() // 周期分组
        var onlyKey = false // 是否不需要分组
        var chronoUnit = ChronoUnit.valueOf(frequency.frequencyTimeUnit.name) // 分组时间单位
        var frequencyNum = frequency.frequencyTime.toLong() // 分组时间数量
        if (start != end) {

            if (deep == 1) {
                // 顶级处理逻辑
                if (hasChild) {
                    // 有下级，直接返回
                    return resultList
                }
                if (isSequence) {
                    // 频次单位为次，直接返回
                    return resultList
                } else {
                    // 频次单位不为次，根据频次单位为周期计算默认规则
                    frequencyNum = 1L
                    chronoUnit = ChronoUnit.valueOf(frequency.frequencyNumUnit.name)
                }
            } else {
                // 非顶级处理逻辑
                if (!hasChild && !isSequence) {
                    // 没有下级并且频次单位不是次，则需要添加默认频次统计处理
                    resultList = groupByFrequency(
                        HealthPlanRule(
                            id = AppIdUtil.nextId(),
                            frequencyTime = 1,
                            frequencyTimeUnit = frequency.frequencyNumUnit,
                            frequencyNum = 1,
                            frequencyNumUnit = TimeServiceUnit.SEQUENCE,
                            frequencyMaxNum = 1
                        ), originList, start, end, deep + 1
                    )
                }
            }

            // 获取分组桶
            var startDateTime = start
            bucket.add(startDateTime.minus(frequencyNum, chronoUnit))
            while (startDateTime.isBefore(end)) {
                bucket.add(startDateTime)
                startDateTime = startDateTime.plus(frequencyNum, chronoUnit)
            }
            bucket.add(startDateTime)
            if (startDateTime == end) {
                bucket.add(startDateTime.plus(frequencyNum, chronoUnit))
            }
        } else {
            onlyKey = true
        }

        // 时间段切割统计
        return resultList
            .groupingBy { groupNode ->
                if (onlyKey)
                    groupNode.key
                else
                    bucket.last { (groupNode.key >= it) && (groupNode.key < (bucket[bucket.indexOf(it) + 1])) }
            }
            .fold({ k, t ->
                GroupNode(
                    k,
                    frequency.frequencyNum,
                    frequency.frequencyNumUnit,
                    null,
                    mutableSetOf(t)
                )
            }) { _, r, t ->
                r.child?.add(t)
                r
            }.filter { deep == 1 || it.value.child?.let { child -> child.size >= frequency.frequencyNum } ?: false }
            .values
    }

}
