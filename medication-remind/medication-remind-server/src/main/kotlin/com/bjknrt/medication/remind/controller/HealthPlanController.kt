package com.bjknrt.medication.remind.controller

import com.bjknrt.article.service.api.ArticleApi
import com.bjknrt.article.service.vo.ArticleAndCardRecommendParam
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.medication.remind.MrHealthPlanTable
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.entity.*
import com.bjknrt.medication.remind.event.SavePlanEvent
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.service.MedicationRemindService
import com.bjknrt.medication.remind.transfer.buildTypePatientParam
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.kato.common.exception.KatoException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.time.format.DateTimeFormatter

@RestController("com.bjknrt.medication.remind.api.HealthPlanController")
class HealthPlanController(
    val mrHealthPlanTable: MrHealthPlanTable,
    val healthPlanService: HealthPlanService,
    val medicationRemindService: MedicationRemindService,
    val articleServer: ArticleApi,
    val eventPublisher: ApplicationEventPublisher
) : AppBaseController(), HealthPlanApi {
    // 根据健康计划ID批量打卡
    @Transactional
    override fun batchIdClockIn(id: List<Id>): List<HealthPlan> {
        // 根据健康计划id列表调用打卡
        return this.batchIdClockIn(
            BatchClockInParams(
                ids = id
            )
        )
    }

    /**
     * 支持补卡
     */
    override fun batchIdClockIn(batchClockInParams: BatchClockInParams): List<HealthPlan> {
        for (it in batchClockInParams.ids) {
            healthPlanService.clockIn(it, batchClockInParams.clockAt)
        }
        return this.list()
    }

    // 根据健康计划type和患者ID打卡
    @Transactional
    override fun batchIdTypeClockIn(batchTypeClockInParams: BatchTypeClockInParams): List<HealthPlan> {
        // 根据健康计划type和患者ID获取健康计划id列表
        val ids = healthPlanService.typeGetHealthIds(batchTypeClockInParams.patientId, batchTypeClockInParams.type)

        return this.batchIdClockIn(
            BatchClockInParams(
                ids = ids,
                clockAt = batchTypeClockInParams.clockAt
            )
        )
    }

    override fun clockIn(body: BigInteger): List<HealthPlan> {

        return this.batchIdClockIn(
            BatchClockInParams(
                ids = listOf(body)
            )
        )
    }

    override fun clockInHistory(clockInHistoryParam: ClockInHistoryParam): List<ClockInHistoryResult> {
        return healthPlanService.clockInHistory(clockInHistoryParam)
    }

    /**
     * 根据开始时间和结束时间, 健康计划ID获取打卡次数
     */
    override fun dateTimeGetClockIn(dateTimeGetClockInParams: DateTimeGetClockInParams): Int {
        return healthPlanService.dateTimeGetClockIn(dateTimeGetClockInParams)
    }

    override fun frequencyGetClockIn(frequencyGetClockInParam: FrequencyGetClockInParam): Int {

        // 根据规则ID查询规则
        val healthPlanOne = mrHealthPlanTable.findByKnId(frequencyGetClockInParam.id) ?: throw MsgException(
            AppSpringUtil.getMessage("mrs.no-find-health_plan")
        )

        val frequency = frequencyGetClockInParam.frequency

        // 转换变量
        val healthPlanRule = HealthPlanRule(
            id = AppIdUtil.nextId(),
            frequencyTime = frequency.frequencyTime,
            frequencyTimeUnit = TimeServiceUnit.valueOf(frequency.frequencyTimeUnit.name),
            frequencyNum = frequency.frequencyNum,
            frequencyNumUnit = TimeServiceUnit.valueOf(frequency.frequencyNumUnit.name),
            frequencyMaxNum = frequency.frequencyMaxNum,
            children = null
        )
        var child = frequency.children
        val healthPlanRules = mutableListOf<HealthPlanRule>()
        healthPlanRules.add(healthPlanRule)

        while (child != null) {
            // 不为null, 转换一下
            val healthPlanRuleChild = HealthPlanRule(
                id = AppIdUtil.nextId(),
                frequencyTime = child.frequencyTime,
                frequencyTimeUnit = TimeServiceUnit.valueOf(child.frequencyTimeUnit.name),
                frequencyNum = child.frequencyNum,
                frequencyNumUnit = TimeServiceUnit.valueOf(child.frequencyNumUnit.name),
                frequencyMaxNum = child.frequencyMaxNum,
                children = null
            )
            healthPlanRules.last().children = healthPlanRuleChild

            healthPlanRules.add(healthPlanRuleChild)

            child = child.children
        }

        // 调用方法
        val clockInNum = healthPlanService.getClockIn(
            healthPlanOne.knCycleStartTime ?: healthPlanOne.knCreatedAt,
            healthPlanOne.knCycleEndTime,
            frequencyGetClockInParam.id,
            healthPlanRule,
            frequencyGetClockInParam.now
        )
        return clockInNum.actualNum
    }

    override fun healthPlanFrequencyGetClockIn(healthPlanFrequencyGetClockInParam: HealthPlanFrequencyGetClockInParam): Int {
        // 根据规则ID查询规则
        val healthPlanOne = mrHealthPlanTable.findByKnId(healthPlanFrequencyGetClockInParam.id) ?: throw MsgException(
            AppSpringUtil.getMessage("mrs.no-find-health_plan")
        )


        // 健康方案规则放到数组中
        val healthPlanId = mutableListOf(healthPlanFrequencyGetClockInParam.id)
        // 根据健康方案ID获取规则
        val healthPlanFrequencyMap = healthPlanService.getHealthPlanFrequency(healthPlanId)

        // 此健康规则的方案
        val frequencys = healthPlanFrequencyMap[healthPlanFrequencyGetClockInParam.id] ?: throw MsgException(
            AppSpringUtil.getMessage("mrs.no-find-health_plan_frequency")
        )

        // 根据此健康方案ID获取
        val frequency =
            frequencys.find { it.id == healthPlanFrequencyGetClockInParam.frequencyId } ?: throw MsgException(
                AppSpringUtil.getMessage("mrs.no-find-health_plan_frequency")
            )
        val clockInNum = healthPlanService.getClockIn(
            healthPlanOne.knCycleStartTime ?: healthPlanOne.knCreatedAt,
            healthPlanOne.knCycleEndTime,
            healthPlanFrequencyGetClockInParam.id,
            frequency,
            healthPlanFrequencyGetClockInParam.now
        )
        return clockInNum.actualNum
    }

    override fun list(): List<HealthPlan> {
        // 获取患者ID
        val patientId = AppSecurityUtil.currentUserIdWithDefault()

        // 获取健康计划列表(包含今日打卡)
        val todayClockResult = healthPlanService.getHealthPlanTodayClockList(patientId)

        // 通过患者ID获取健康计划及其规则列表
        val healthPlanFrequencyMap = healthPlanService.getHealthPlanFrequency(todayClockResult.map { it.id })

        // 如果过滤出科普文章, 获取科普文章列表
        val articleResult = todayClockResult
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

        // 把健康规则添加到数组中
        val todayClockResult1 = todayClockResult.map {
            PlanClockResult(
                id = it.id,
                name = it.name,
                type = it.type,
                clockIn = it.clockIn,
                subName = it.subName,
                desc = it.desc,
                time = it.time,
                cycleStartTime = it.cycleStartTime,
                cycleEndTime = it.cycleEndTime,
                externalKey = it.externalKey,
                group = it.group,
                isClockDisplay = it.isClockDisplay,
                frequency = healthPlanFrequencyMap[it.id]
            )
        }

        for (todayIt in todayClockResult1) {
            todayIt.frequency?.let { frequencyList ->
                var i = 0;
                for (frequencyIt in frequencyList) {
                    if (i > 2) break
                    // 获取打卡记录
                    val f = healthPlanService.getClockIn(
                        todayIt.cycleStartTime,
                        todayIt.cycleEndTime,
                        todayIt.id,
                        frequencyIt
                    )

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
                        if (todayIt.cycleEndTime == null) {
                            todayIt.cycleStartTime = f.cycleStartTime
                            todayIt.cycleEndTime = f.cycleEndTime
                        }
                        // 特殊处理 饮食计划和线下随访的desc返回值, 科普计划的文章
                        when (todayIt.type) {
                            // 饮食计划
                            HealthPlanType.DIET_PLAN -> todayIt.desc =
                                "${todayIt.cycleStartTime.format(DateTimeFormatter.ofPattern("MM.dd"))}-${
                                    todayIt.cycleEndTime?.plusSeconds(-1)?.format(DateTimeFormatter.ofPattern("MM.dd"))
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
                            HealthPlanType.OFFLINE_COPD -> todayIt.desc =
                                "${todayIt.cycleStartTime.format(DateTimeFormatter.ofPattern("MM.dd"))}-${
                                    todayIt.cycleEndTime?.plusSeconds(-1)?.format(DateTimeFormatter.ofPattern("MM.dd"))
                                }完成1次"
                            // 科普计划
                            HealthPlanType.SCIENCE_POPULARIZATION_PLAN -> todayIt.externalKey =
                                articleResult?._data?.takeIf { it.isNotEmpty() }?.first()?.id?.toString()
                            else -> {}
                        }

                        todayIt.mainFrequency = frequencyClockResult
                        // 如果只有一条, 把 mainFrequency 的值也放到 subFrequency 里面
                        if (frequencyList.size == 1) {
                            todayIt.subFrequency = todayIt.mainFrequency
                        }
                    } else if (i == 1) {
                        todayIt.subFrequency = frequencyClockResult
                    } else {
                        // 当天的
                        todayIt.todayFrequency = frequencyClockResult
                    }
                    i++
                }
            }
        }
        // 排序
        val listSort = healthPlanService.listSort(todayClockResult1)

        return listSort
            .filter { filterIt ->
                !(!filterIt.isClockDisplay && filterIt.mainFrequency?.let { _it -> (_it.actualNum) >= (_it.frequencyNum) } ?: false)
            }
            .map {
                HealthPlan(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    clockIn = it.clockIn,
                    subName = it.subName,
                    desc = it.desc,
                    time = it.time,
                    externalKey = it.externalKey,
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    group = it.group,
                    todayFrequency = it.todayFrequency?.let { todayIt ->
                        FrequencyClock(
                            frequencyTime = todayIt.frequencyTime,
                            frequencyTimeUnit = TimeUnit.valueOf(todayIt.frequencyTimeUnit.name),
                            frequencyNum = todayIt.frequencyNum,
                            frequencyNumUnit = FrequencyNumUnit.valueOf(todayIt.frequencyNumUnit.name),
                            frequencyMaxNum = todayIt.frequencyMaxNum,
                            actualNum = todayIt.actualNum
                        )
                    },
                    subFrequency = it.subFrequency?.let { subIt ->
                        FrequencyClock(
                            frequencyTime = subIt.frequencyTime,
                            frequencyTimeUnit = TimeUnit.valueOf(subIt.frequencyTimeUnit.name),
                            frequencyNum = subIt.frequencyNum,
                            frequencyNumUnit = FrequencyNumUnit.valueOf(subIt.frequencyNumUnit.name),
                            frequencyMaxNum = subIt.frequencyMaxNum,
                            actualNum = subIt.actualNum
                        )
                    },
                    mainFrequency = it.mainFrequency?.let { mainIt ->
                        FrequencyClock(
                            frequencyTime = mainIt.frequencyTime,
                            frequencyTimeUnit = TimeUnit.valueOf(mainIt.frequencyTimeUnit.name),
                            frequencyNum = mainIt.frequencyNum,
                            frequencyNumUnit = FrequencyNumUnit.valueOf(mainIt.frequencyNumUnit.name),
                            frequencyMaxNum = mainIt.frequencyMaxNum,
                            actualNum = mainIt.actualNum
                        )
                    }
                )
            }
    }

    override fun idGetList(id: List<Id>): List<HealthPlanMain> {
        // 根据健康计划id数组获取健康计划
        val idGetHealthPlanModels = healthPlanService.idsGetHealthPlanList(id)
        // 处理返回值

        return healthPlanService.arrangementHealthPlanList(idGetHealthPlanModels)
            .filter { filterIt ->
                id.contains(filterIt.id)
            }
            .map {
                HealthPlanMain(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    subName = it.subName,
                    desc = it.desc,
                    time = it.time,
                    externalKey = it.externalKey,
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    group = it.group,
                    display = it.display
                )
            }
    }

    override fun patientIdGetList(patientIdGetListParam: PatientIdGetListParam): List<HealthPlanMain> {
        // 获取健康计划列表(包含今日打卡)
        val healthPlanModels = healthPlanService.patientIdGetHealthPlanList(patientIdGetListParam.patientId)
        return healthPlanService.arrangementHealthPlanList(healthPlanModels, patientIdGetListParam.patientId)
            .map {
                HealthPlanMain(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    subName = it.subName,
                    desc = it.desc,
                    time = it.time,
                    externalKey = it.externalKey,
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    group = it.group,
                    display = it.display
                )
            }
    }

    /**
     * 根据type添加健康计划
     */
    @Transactional
    override fun upsertTypeFrequencyHealth(frequencyHealthAllParam: FrequencyHealthAllParam): BatchHealthPlanResult {
        val healthPlans = frequencyHealthAllParam.healthPlans
        val drugPlans = frequencyHealthAllParam.drugPlans

        // 药品添加计划和其他类型添加计划必须有一个有值
        if (healthPlans.isEmpty() && drugPlans.isEmpty())
            throw KatoException(AppSpringUtil.getMessage("mrs.health_plan_and_drug_plan_cannot_all_empty"))

        val delList = buildTypePatientParam(healthPlans, drugPlans)
        // 根据患者ID和types列表删除这些健康计划
        medicationRemindService.batchDeleteByTypeAndPatientId(delList)

        // 批量添加频率为几周几次的健康计划和频率为周一到周日的健康计划
        return batchAddHealthPlan(frequencyHealthAllParam)
    }

    /**
     * 批量添加健康计划
     */
    override fun batchAddHealthPlan(frequencyHealthAllParam: FrequencyHealthAllParam): BatchHealthPlanResult {
        val healthPlans = frequencyHealthAllParam.healthPlans
        val drugPlans = frequencyHealthAllParam.drugPlans
        // 几周几次返回值
        val healthPlanResults = mutableListOf<UpsertHealthFrequencyResult>()
        // 周几几点返回值
        val drugPlanResults = mutableListOf<UpsertHealthFrequencyResult>()

        // 批量添加频率为几周几次类型的
        for (it in healthPlans) {
            val healthPlanFrequencyIds = this.upsertFrequencyHealth(it)
            healthPlanResults.add(healthPlanFrequencyIds)
        }
        // 批量添加频率是周一到周日的
        for (it in drugPlans) {
            val upsertResult = medicationRemindService.upsert(DrugDTO(
                drugName = it.drugName,
                isUsed = it.isUsed,
                time = it.time,
                frequencys = it.frequencys,
                subName = it.subName,
                patientId = it.patientId,
                type = it.type,
                id = null,
                cycleStartTime = it.cycleStartTime,
                cycleEndTime = it.cycleEndTime
            ))
            drugPlanResults.add(UpsertHealthFrequencyResult(
                id = upsertResult.id,
                type = upsertResult.type,
                cycleStartTime = it.cycleStartTime,
                cycleEndTime = it.cycleEndTime
            ))
        }
        return BatchHealthPlanResult(
            healthPlans = healthPlanResults,
            drugPlans = drugPlanResults
        );
    }

    @Transactional
    override fun upsertFrequencyHealth(frequencyHealthParams: FrequencyHealthParams): UpsertHealthFrequencyResult {
        // 添加主表
        val healthPlanModel = healthPlanService.upsertHealth(frequencyHealthParams)
            ?: throw MsgException(AppSpringUtil.getMessage("mrs.add-health-plan-fail"))
        val id = healthPlanModel.knId
        val frequency = frequencyHealthParams.frequencys
        val frequencyIds = mutableListOf<Id>()

        if (frequency != null) {
            for (it in frequency) {
                val addFrequency = mutableListOf<FrequencyParams>()
                // 第一层
                addFrequency.add(
                    FrequencyParams(
                        id = AppIdUtil.nextId(),
                        healthPlanId = id,
                        explainId = null,
                        frequencyTime = it.frequencyTime,
                        frequencyTimeUnit = it.frequencyTimeUnit,
                        frequencyNum = it.frequencyNum,
                        frequencyNumUnit = it.frequencyNumUnit,
                        frequencyMaxNum = it.frequencyMaxNum
                    )
                )
                // 下级
                var child = it.children

                // 如果下级不是null, 说明下级还有下级
                while (child != null) {
                    // 把下级放到数组中
                    addFrequency.add(
                        FrequencyParams(
                            id = AppIdUtil.nextId(),
                            healthPlanId = id,
                            explainId = addFrequency.last().id,
                            frequencyTime = child.frequencyTime,
                            frequencyTimeUnit = child.frequencyTimeUnit,
                            frequencyNum = child.frequencyNum,
                            frequencyNumUnit = child.frequencyNumUnit,
                            frequencyMaxNum = child.frequencyMaxNum
                        )
                    )
                    child = child.children
                }

                healthPlanService.upsertFrequency(addFrequency)
                frequencyIds.add(addFrequency.first().id)
            }
        }
        //发布保存计划事件
        eventPublisher.publishEvent(
            SavePlanEvent(
                this,
                id,
                HealthPlanType.LEAVE_HOSPITAL_VISIT,
                frequencyHealthParams.patientId
            )
        )

        return UpsertHealthFrequencyResult(
            id = id,
            type = HealthPlanType.valueOf(healthPlanModel.knType),
            cycleStartTime = healthPlanModel.knCycleStartTime,
            cycleEndTime = healthPlanModel.knCycleEndTime,
            frequencyIds = frequencyIds
        )
    }
}
