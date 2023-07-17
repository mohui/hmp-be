package com.bjknrt.health.scheme.controller

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.FromTag
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.api.VisitApi
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.service.ClockInService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageCerebralStrokeServiceImpl
import com.bjknrt.health.scheme.transfer.toCerebralStrokeVisitInfo
import com.bjknrt.health.scheme.transfer.toHsCerebralStrokeVisit
import com.bjknrt.health.scheme.util.getBmiValue
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.api.AnswerHistoryApi
import com.bjknrt.question.answering.system.vo.LastAnswerDetailRequest
import com.bjknrt.question.answering.system.vo.LastAnswerRecord
import com.bjknrt.question.answering.system.vo.LastAnswerRecordListRequest
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.jobrunr.jobs.JobId
import org.jobrunr.scheduling.JobScheduler
import org.jobrunr.storage.JobNotFoundException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@RestController("com.bjknrt.health.scheme.api.VisitController")
class VisitController(
    val hsHtnVisitTable: HsHtnVisitTable,
    val hsHtnVisitDrugTable: HsHtnVisitDrugTable,
    val hsT2dmVisitTable: HsT2dmVisitTable,
    val hsT2dmVisitDrugTable: HsT2dmVisitDrugTable,
    val hsAcuteCoronaryVisitTable: HsAcuteCoronaryVisitTable,
    val hsAcuteCoronaryVisitDrugTable: HsAcuteCoronaryVisitDrugTable,
    val hsCerebralStrokeVisitTable: HsCerebralStrokeVisitTable,
    val hsCerebralStrokeLeaveHospitalVisitTable: HsCerebralStrokeLeaveHospitalVisitTable,
    val healthManageCerebralStrokeServiceImpl: HealthManageCerebralStrokeServiceImpl,
    val healthSchemeManageService: HealthSchemeManageService,
    val hsCopdVisitTable: HsCopdVisitTable,
    val hsCopdVisitDrugTable: HsCopdVisitDrugTable,
    val hsHtnVisitDao: HsHtnVisitDao,
    val indicatorServer: IndicatorApi,
    val clockInService: ClockInService,
    val questionsAnswerClient: AnswerHistoryApi,
    val jobScheduler: JobScheduler
) : AppBaseController(), VisitApi {
    // 冠心病
    @Transactional
    override fun acuteCoronaryAdd(coronaryAddParams: CoronaryAddParams): Boolean {
        // 取出Bmi值, 如果是患者自填,需要根据身高体重计算
        var signsBmi = coronaryAddParams.signsBim
        // 默认是医生随访, 如果是患者自填, 是患者端随访
        var fromTag = FromTag.DOCTOR_VISIT;
        // 冠心病type 默认为线下随访
        var healthPlanType = HealthPlanType.OFFLINE_ACUTE_CORONARY_DISEASE

        if (coronaryAddParams.followWay == FllowWay.SELF) {
            fromTag = FromTag.PATIENT_SELF
            // 患者自填为线上随访
            healthPlanType = HealthPlanType.ONLINE_ACUTE_CORONARY_DISEASE
            // 患者自填, 身高必填, 计算bmi
            coronaryAddParams.signsHeight?.let {
                if (coronaryAddParams.signsBim == null)  {
                    signsBmi = getBmiValue(height = it, weight =  coronaryAddParams.signsWeight)
                }
            } ?: throw MsgException(AppSpringUtil.getMessage("visit.height-not-null"))
        }

        // 主键ID
        val htnVisitId = AppIdUtil.nextId()
        // 添加人id
        val createdId = AppSecurityUtil.currentUserIdWithDefault()

        //  拼接冠心病需要添加的字段
        val hsAcuteCoronaryVisitAdd = HsAcuteCoronaryVisit.builder()
            .setId(htnVisitId)
            .setPatientId(coronaryAddParams.patientId)
            .setPatientName(coronaryAddParams.patientName)
            .setFollowWay(coronaryAddParams.followWay.name)
            .setFollowDate(coronaryAddParams.followDate)
            .setSignsSbp(coronaryAddParams.signsSbp.toDouble())
            .setSignsDbp(coronaryAddParams.signsDbp.toDouble())
            .setSignsWeight(coronaryAddParams.signsWeight.toDouble())
            .setSignsHeartRate(coronaryAddParams.signsHeartRate.toDouble())
            .setLifeCigarettesPerDay(coronaryAddParams.lifeCigarettesPerDay)
            .setLifeAlcoholPerDay(coronaryAddParams.lifeAlcoholPerDay.toDouble())
            .setLifeSportPerWeek(coronaryAddParams.lifeSportPerWeek)
            .setLifeSportPerTime(coronaryAddParams.lifeSportPerTime.toDouble())
            .setLifeSaltSituation(coronaryAddParams.lifeSaltSituation.name)
            .setLifeFollowMedicalAdvice(coronaryAddParams.lifeFollowMedicalAdvice.name)
            .setDrugCompliance(coronaryAddParams.drugCompliance.name)
            .setCreatedBy(createdId)
            .setUpdatedBy(createdId)
            .setDoctorId(coronaryAddParams.doctorId)
            .setDoctorName(coronaryAddParams.doctorName)
            // 无症状
            .setIsSymptomNone(coronaryAddParams.isSymptomNone)
            // 心慌
            .setIsSymptomPains(coronaryAddParams.isSymptomPains)
            // 头晕
            .setIsSymptomDizziness(coronaryAddParams.isSymptomDizziness)
            // 恶心
            .setIsSymptomNausea(coronaryAddParams.isSymptomNausea)
            // 胸口处压榨性疼痛或憋闷感或紧缩感
            .setIsSymptomPalpitationsToc(coronaryAddParams.isSymptomPalpitationsToc)
            // 颈部或喉咙感觉发紧
            .setIsSymptomThroatTight(coronaryAddParams.isSymptomThroatTight)
            // 其它
            .setSymptomOther(coronaryAddParams.symptomOther)
            // 心理调整
            .setLifeMentalAdjustment(coronaryAddParams.lifeMentalAdjustment?.name)
            // 辅助检查
            .setAbbreviationsAboutExamination(coronaryAddParams.abbreviationsAboutExamination)
            // 有无药物不良反应
            .setIsDrugNone(coronaryAddParams.isDrugNone)
            // 肌肉痛
            .setIsDrugMyalgia(coronaryAddParams.isDrugMyalgia)
            // 肌肉无力
            .setIsDrugMuscleWeakness(coronaryAddParams.isDrugMuscleWeakness)
            // 眼白或皮肤发黄
            .setIsDrugSkinYellowness(coronaryAddParams.isDrugSkinYellowness)
            // 其他: 药物不良反应描述
            .setDrugOther(coronaryAddParams.drugOther)
            // 无
            .setIsBleedNone(coronaryAddParams.isBleedNone)
            // 鼻出血
            .setIsBleedNose(coronaryAddParams.isBleedNose)
            // 牙龈出血
            .setIsBleedGums(coronaryAddParams.isBleedGums)
            // 多次黑褐色大便
            .setIsBleedShit(coronaryAddParams.isBleedShit)
            // 其他
            .setBleedOther(coronaryAddParams.bleedOther)
            // 此次随访分类
            .setVisitClass(coronaryAddParams.visitClass?.name)
            // 是否转诊
            .setIsReferral(coronaryAddParams.isReferral)
            // 转诊原因
            .setReferralReason(coronaryAddParams.referralReason)
            // 转诊机构及科别
            .setReferralAgencies(coronaryAddParams.referralAgencies)
            // 下次随访日期
            .setNextVisit(coronaryAddParams.nextVisit)
            //身高
            .setSignsHeight(coronaryAddParams.signsHeight?.toDouble())
            //建议体重
            .setRecommendWeight(coronaryAddParams.recommendWeight?.toDouble())
            //体质指数
            .setSignsBim(coronaryAddParams.signsBim?.toDouble())
            //建议体质指数
            .setRecommendBim(coronaryAddParams.recommendBim?.toDouble())
            //建议日吸烟量
            .setRecommendCigarettesPer(coronaryAddParams.recommendCigarettesPer)
            //建议饮酒量
            .setRecommendAlcoholPer(coronaryAddParams.recommendAlcoholPer?.toDouble())
            //建议每周运动次数 单位: 次
            .setRecommendSportPerWeek(coronaryAddParams.recommendSportPerWeek)
            //建议每次运动时间. 单位: 分钟
            .setRecommendSportPerTime(coronaryAddParams.recommendSportPerTime?.toDouble())
            //建议摄盐情况. LIGHT: 轻; MIDDLE: 中; DEEP: 重
            .setRecommendSaltSituation(coronaryAddParams.recommendSaltSituation?.name)
            .build()
        // 执行添加语句
        hsAcuteCoronaryVisitTable.save(hsAcuteCoronaryVisitAdd)
        // 拼接需要添加的冠心病药品
        coronaryAddParams.drugs
            .takeIf { it.isNullOrEmpty().not() }
            ?.let { drugs ->
                for (it in drugs) {
                    val hsAcuteCoronaryVisitDrugAdd = HsAcuteCoronaryVisitDrug.builder()
                        .setId(AppIdUtil.nextId())
                        .setVisitId(htnVisitId)
                        .setCreatedBy(createdId)
                        .setUpdatedBy(createdId)
                        .setDrugName(it.drugName)
                        .setTimesPerDay(it.timesPerDay)
                        .setUnitsPerTime(it.unitsPerTime)
                        .setDrugId(it.drugId)
                        .setDrugManufacturers(it.drugManufacturers)
                        .build()
                    hsAcuteCoronaryVisitDrugTable.save(hsAcuteCoronaryVisitDrugAdd)
                }
            }

        // 添加成功,调用 batchAddIndicator的参数
        val batchAddIndicatorParam = BatchIndicator(
            // 患者ID
            patientId = coronaryAddParams.patientId,
            // 来源标识
            fromTag = fromTag,
            // 身高
            knBodyHeight = coronaryAddParams.signsHeight,
            // 体重
            knBodyWeight = coronaryAddParams.signsWeight,
            // 体质指数
            knBmi = signsBmi,
            // 腰围
            knWaistline = null,
            // 收缩压
            knSystolicBloodPressure = coronaryAddParams.signsSbp,
            // 舒张压
            knDiastolicBloodPressure = coronaryAddParams.signsDbp,
            // 空腹血糖
            knFastingBloodSandalwood = null,
            // 餐前血糖mmol/L（午餐）
            knBeforeLunchBloodSugar = null,
            // 餐前血糖mmol/L（晚餐）
            knBeforeDinnerBloodSugar = null,
            // 随机血糖
            knRandomBloodSugar = null,
            // 餐后2个小时血糖
            knAfterMealBloodSugar = null,
            // 餐后2h血糖mmol/L（午餐）
            knAfterLunchBloodSugar = null,
            // 餐后2h血糖mmol/L（晚餐）
            knAfterDinnerBloodSugar = null,
            // 睡前血糖
            knBeforeSleepBloodSugar = null,
            // 总胆固醇
            knTotalCholesterol = null,
            // 甘油三酯
            knTriglycerides = null,
            // 低密度脂蛋白
            knLowDensityLipoprotein = null,
            // 高密度脂蛋白
            knHighDensityLipoprotein = null,
            // 吸烟几支
            knNum = null,
            // 白酒
            knWhiteSpirit = null,
            // 啤酒
            knBeer = null,
            // 红酒
            knWine = null,
            // 黄酒
            knYellowRiceSpirit = null,
            // 脉搏氧饱和度
            knPulseOximetry = null,
            // 心率
            knHeartRate = coronaryAddParams.signsHeartRate.toInt(),
            // 脉搏
            knPulse = null,
            // 摄氏度(体温)
            knCelsius = null
        )
        // 添加成功,调用 batchAddIndicator接口
        indicatorServer.batchAddIndicator(batchAddIndicatorParam)

        // 冠心病随访打卡
        clockInService.saveClockIn(coronaryAddParams.patientId, healthPlanType, LocalDateTime.now())
        return true;
    }

    // 冠心病详情
    override fun acuteCoronaryDetail(body: BigInteger): AcuteCoronaryVisit {
        // 根据随访id获得随访详情列表
        val visitModel = hsAcuteCoronaryVisitTable
            .select()
            .where(HsAcuteCoronaryVisitTable.Id.eq(arg(body)))
            .findOne() ?: throw NotFoundDataException(AppSpringUtil.getMessage("mrs.no-find-data"))

        // 获取随访id获取该随访药品详情
        val visitDrugs = hsAcuteCoronaryVisitDrugTable
            .select()
            .where(HsAcuteCoronaryVisitDrugTable.VisitId.eq(arg(body)))
            .find()

        val drugsResult = visitDrugs.map {
            DrugsInner(it.drugName, it.timesPerDay, it.unitsPerTime, it.drugId, it.drugManufacturers)
        }

        return AcuteCoronaryVisit(
            patientId = visitModel.patientId,
            id = visitModel.id,
            patientName = visitModel.patientName,
            doctorId = visitModel.doctorId,
            doctorName = visitModel.doctorName,
            followWay = visitModel.followWay.let { FllowWay.valueOf(it) },
            followDate = visitModel.followDate,
            // 无症状
            isSymptomNone = visitModel.isSymptomNone,
            // 心慌
            isSymptomPains = visitModel.isSymptomPains,
            // 头晕
            isSymptomDizziness = visitModel.isSymptomDizziness,
            // 恶心
            isSymptomNausea = visitModel.isSymptomNausea,
            // 胸口处压榨性疼痛或憋闷感或紧缩感
            isSymptomPalpitationsToc = visitModel.isSymptomPalpitationsToc,
            // 颈部或喉咙感觉发紧
            isSymptomThroatTight = visitModel.isSymptomThroatTight,
            // 其他
            symptomOther = visitModel.symptomOther,
            // 血压 - 收缩压
            signsSbp = visitModel.signsSbp.toBigDecimal(),
            // 血压 - 舒张压
            signsDbp = visitModel.signsDbp.toBigDecimal(),
            //身高
            signsHeight = visitModel.signsHeight?.toBigDecimal(),
            // 体重
            signsWeight = visitModel.signsWeight.toBigDecimal(),
            // 建议体重
            recommendWeight = visitModel.recommendWeight?.toBigDecimal(),
            // 体质指数
            signsBim = visitModel.signsBim?.toBigDecimal(),
            // 建议体质指数
            recommendBim = visitModel.recommendBim?.toBigDecimal(),
            // 心率
            signsHeartRate = visitModel.signsHeartRate.toBigDecimal(),
            // 日吸烟量
            lifeCigarettesPerDay = visitModel.lifeCigarettesPerDay,
            // 建议日吸烟量
            recommendCigarettesPer = visitModel.recommendCigarettesPer,
            // 日饮酒量
            lifeAlcoholPerDay = visitModel.lifeAlcoholPerDay.toBigDecimal(),
            // 建议饮酒量
            recommendAlcoholPer = visitModel.recommendAlcoholPer?.toBigDecimal(),
            // 每周运动次数
            lifeSportPerWeek = visitModel.lifeSportPerWeek,
            // 每次运动时间
            lifeSportPerTime = visitModel.lifeSportPerTime.toBigDecimal(),
            // 建议每周运动次数
            recommendSportPerWeek = visitModel.recommendSportPerWeek,
            // 建议每次运动时间
            recommendSportPerTime = visitModel.recommendSportPerTime?.toBigDecimal(),
            // 摄盐情况
            lifeSaltSituation = visitModel.lifeSaltSituation.let { LifeSalt.valueOf(it) },
            // 建议摄盐情况
            recommendSaltSituation = visitModel.recommendSaltSituation?.let { LifeSalt.valueOf(it) },
            // 心理调整
            lifeMentalAdjustment = visitModel.lifeMentalAdjustment?.let { LifeState.valueOf(it) },
            // 遵医行为
            lifeFollowMedicalAdvice = visitModel.lifeFollowMedicalAdvice.let { LifeState.valueOf(it) },
            // 服药依从性
            drugCompliance = visitModel.drugCompliance.let { DrugCompliance.valueOf(it) },
            // 辅助检查
            abbreviationsAboutExamination = visitModel.abbreviationsAboutExamination,
            // 有无药物不良反应
            isDrugNone = visitModel.isDrugNone,
            // 肌肉痛
            isDrugMyalgia = visitModel.isDrugMyalgia,
            // 肌肉无力
            isDrugMuscleWeakness = visitModel.isDrugMuscleWeakness,
            // 眼白或皮肤发黄
            isDrugSkinYellowness = visitModel.isDrugSkinYellowness,
            // 其他: 药物不良反应描述
            drugOther = visitModel.drugOther,
            // 无: 出血
            isBleedNone = visitModel.isBleedNone,
            // 鼻出血
            isBleedNose = visitModel.isBleedNose,
            // 牙龈出血
            isBleedGums = visitModel.isBleedGums,
            // 多次黑褐色大便
            isBleedShit = visitModel.isBleedShit,
            // 其他: 描述
            bleedOther = visitModel.bleedOther,
            // 此次随访分类
            visitClass = visitModel.visitClass?.let { VisitClass.valueOf(it) },
            // 是否转诊
            isReferral = visitModel.isReferral,
            // 转诊原因
            referralReason = visitModel.referralReason,
            // 转诊机构及科别
            referralAgencies = visitModel.referralAgencies,
            // 下次随访日期
            nextVisit = visitModel.nextVisit,
            createdBy = visitModel.createdBy,
            createdAt = visitModel.createdAt.toLocalDateTime(),
            updatedBy = visitModel.updatedBy,
            updatedAt = visitModel.updatedAt.toLocalDateTime(),
            drugs = drugsResult
        );
    }

    //冠心病列表
    override fun acuteCoronaryList(visitListParams: VisitListParams): PagedResult<VisitList> {
        // 根据患者id获得该患者冠心病随访列表
        val listModels = hsAcuteCoronaryVisitTable
            .select()
            .where(HsAcuteCoronaryVisitTable.PatientId.eq(arg(visitListParams.patientId)))
            .order(HsAcuteCoronaryVisitTable.FollowDate, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)
        // 处理data数据
        val data = listModels.data.map {
            VisitList(
                id = it.id,
                name = VisitType.ACUTE_CORONARY_DISEASE,
                followDate = it.followDate,
                signsSbp = it.signsSbp.toBigDecimal(),
                signsDbp = it.signsDbp.toBigDecimal(),
                signsHeight = it.signsHeight?.toBigDecimal(),
                signsWeight = it.signsWeight.toBigDecimal(),
                signsHeartRate = it.signsHeartRate.toBigDecimal(),
                signsBim = it.signsBim?.toBigDecimal(),
                createdBy = it.createdBy
            )
        }
        return PagedResult(
            listModels.totalPage,
            listModels.pageSize,
            listModels.pageNo,
            listModels.total,
            data
        )
    }

    // 脑卒中添加
    @Transactional
    override fun cerebralStrokeAdd(cerebralStrokeVisit: CerebralStrokeVisit) {
        val visit = toHsCerebralStrokeVisit(cerebralStrokeVisit)
        hsCerebralStrokeVisitTable.insertWithoutNull(visit)

        // 脑卒中type 默认为线下随访
        var healthPlanType = HealthPlanType.OFFLINE_CEREBRAL_STROKE
        if (cerebralStrokeVisit.followWay == FllowWay.SELF) {
            // 如果是自填为线上随访
            healthPlanType = HealthPlanType.ONLINE_CEREBRAL_STROKE
        }

        //自动打卡
        clockInService.saveClockIn(visit.patientId, listOf(HealthPlanType.CEREBRAL_STROKE, healthPlanType) , LocalDateTime.now())
    }

    // 脑卒中详情
    override fun cerebralStrokeDetail(body: BigInteger): CerebralStrokeVisitInfo {
        return hsCerebralStrokeVisitTable.findById(body)
            ?.let { toCerebralStrokeVisitInfo(it) }
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("visit.cerebral-stroke.not-found"))
    }

    // 慢阻肺添加
    @Transactional
    override fun copdAdd(copdAddParams: CopdAddParams): Boolean {
        // 主键ID
        val copdVisitId = AppIdUtil.nextId()
        // 添加人id
        val createdId = AppSecurityUtil.currentUserIdWithDefault()

        val signsBmi = getBmiValue(height = copdAddParams.signsHeight, weight =  copdAddParams.signsWeight)

        // 舒张压不能大于收缩压
        if (copdAddParams.signsDbp > copdAddParams.signsSbp)
            throw MsgException(AppSpringUtil.getMessage("visit.copd-visit.blood-pressure"))

        //  拼接慢阻肺需要添加的字段
        val hsCopdVisitAdd = HsCopdVisit.builder()
            .setId(copdVisitId)
            .setPatientId(copdAddParams.patientId)
            .setPatientName(copdAddParams.patientName)
            .setFollowWay(copdAddParams.followWay.name)
            .setFollowDate(copdAddParams.followDate)
            .setPulseOxygenSaturation(copdAddParams.pulseOxygenSaturation.toDouble())
            .setPulse(copdAddParams.pulse.toDouble())
            // 收缩压(高压)
            .setSignsSbp(copdAddParams.signsSbp.toDouble())
            // 舒张压(低压)
            .setSignsDbp(copdAddParams.signsDbp.toDouble())
            // 身高
            .setSignsHeight(copdAddParams.signsHeight.toDouble())
            // 体重
            .setSignsWeight(copdAddParams.signsWeight.toDouble())
            // 每周运动次数
            .setLifeSportPerWeek(copdAddParams.lifeSportPerWeek)
            // 每次运动时间
            .setLifeSportPerTime(copdAddParams.lifeSportPerTime.toDouble())
            // 摄盐情况
            .setLifeSaltSituation(copdAddParams.lifeSaltSituation.name)
            // 遵医行为
            .setLifeFollowMedicalAdvice(copdAddParams.lifeFollowMedicalAdvice.name)
            // 服药依从性
            .setDrugCompliance(copdAddParams.drugCompliance.name)
            // 肺功能
            .setPulmonaryFunction(copdAddParams.pulmonaryFunction.toDouble())
            .setCreatedBy(createdId)
            .setUpdatedBy(createdId)
            .setDoctorId(copdAddParams.doctorId)
            .setDoctorName(copdAddParams.doctorName)
            .setIsWheezingEffort(copdAddParams.isWheezingEffort)
            .setIsWheezingWalkFast(copdAddParams.isWheezingWalkFast)
            .setIsWheezingStroll(copdAddParams.isWheezingStroll)
            .setIsWheezingHundredMeters(copdAddParams.isWheezingHundredMeters)
            .setIsWheezingLittle(copdAddParams.isWheezingLittle)
            .setIsSymptomCough(copdAddParams.isSymptomCough)
            .setIsSymptomPurulentSputum(copdAddParams.isSymptomPurulentSputum)
            .setIsSymptomInappetence(copdAddParams.isSymptomInappetence)
            .setIsSymptomAbdominalDistention(copdAddParams.isSymptomAbdominalDistention)
            .setIsSymptomBreathing(copdAddParams.isSymptomBreathing)
            // 下肢或全身浮肿
            .setIsSymptomSystemicEdema(copdAddParams.isSymptomSystemicEdema)
            // 无以上症状
            .setIsSymptomNone(copdAddParams.isSymptomNone)
            // 其它
            .setSymptomOther(copdAddParams.symptomOther)
            // 不吸烟
            .setIsCigarettesPer(copdAddParams.isCigarettesPer)
            // 日吸烟量
            .setLifeCigarettesPerDay(copdAddParams.lifeCigarettesPerDay)
            //建议日吸烟量
            .setRecommendCigarettesPer(copdAddParams.recommendCigarettesPer)
            .setIsAlcoholPer(copdAddParams.isAlcoholPer)
            // 日饮酒量
            .setLifeAlcoholPerDay(copdAddParams.lifeAlcoholPerDay.toDouble())
            //建议日饮酒量
            .setRecommendAlcoholPer(copdAddParams.recommendAlcoholPer?.toDouble())
            //建议每周运动次数 单位: 次
            .setRecommendSportPerWeek(copdAddParams.recommendSportPerWeek)
            //建议每次运动时间. 单位: 分钟
            .setRecommendSportPerTime(copdAddParams.recommendSportPerTime?.toDouble())
            // 心跳过快或心慌
            .setIsReactionsPains(copdAddParams.isReactionsPains)
            .setIsReactionsConvulsion(copdAddParams.isReactionsConvulsion)
            .setIsReactionsDizzinessHeadache(copdAddParams.isReactionsDizzinessHeadache)
            .setIsReactionsNauseaVomiting(copdAddParams.isReactionsNauseaVomiting)
            .setIsReactionsNone(copdAddParams.isReactionsNone)
            .setReactionsOther(copdAddParams.reactionsOther)
            // 体质
            .setSignsBim(signsBmi.toDouble())
            .build()
        // 执行添加语句
        hsCopdVisitTable.save(hsCopdVisitAdd);
        // 拼接需要添加的慢阻肺药品
        copdAddParams.drugs
            .takeIf { it.isNullOrEmpty().not() }
            ?.let { drugs ->
                for (it in drugs) {
                    val hsCopdVisitDrug = HsCopdVisitDrug.builder()
                        .setId(AppIdUtil.nextId())
                        .setVisitId(copdVisitId)
                        .setCreatedBy(createdId)
                        .setUpdatedBy(createdId)
                        .setDrugId(it.drugId)
                        .setDrugName(it.drugName)
                        .setTimesPerDay(it.timesPerDay)
                        .setUnitsPerTime(it.unitsPerTime)
                        .setDrugManufacturers(it.drugManufacturers)
                        .build()
                    hsCopdVisitDrugTable.save(hsCopdVisitDrug)
                }
            }

        // 默认是医生随访, 如果是患者自填, 是患者端随访
        var fromTag = FromTag.DOCTOR_VISIT;
        // 慢阻肺type 默认为线下随访
        var healthPlanType = HealthPlanType.OFFLINE_COPD
        if (copdAddParams.followWay == FllowWay.SELF) {
            fromTag = FromTag.PATIENT_SELF
            // 如果是自填为线上随访
            healthPlanType = HealthPlanType.ONLINE_COPD
        }
        // 添加成功,调用 batchAddIndicator的参数
        val batchAddIndicatorParam = BatchIndicator(
            // 患者ID
            patientId = copdAddParams.patientId,
            // 来源标识
            fromTag = fromTag,
            // 身高
            knBodyHeight = copdAddParams.signsHeight,
            // 体重
            knBodyWeight = copdAddParams.signsWeight,
            // 体质指数
            knBmi = signsBmi,
            // 腰围
            knWaistline = null,
            // 收缩压
            knSystolicBloodPressure = copdAddParams.signsSbp,
            // 舒张压
            knDiastolicBloodPressure = copdAddParams.signsDbp,
            // 空腹血糖
            knFastingBloodSandalwood = null,
            // 餐前血糖mmol/L（午餐）
            knBeforeLunchBloodSugar = null,
            // 餐前血糖mmol/L（晚餐）
            knBeforeDinnerBloodSugar = null,
            // 随机血糖
            knRandomBloodSugar = null,
            // 餐后2个小时血糖
            knAfterMealBloodSugar = null,
            // 餐后2h血糖mmol/L（午餐）
            knAfterLunchBloodSugar = null,
            // 餐后2h血糖mmol/L（晚餐）
            knAfterDinnerBloodSugar = null,
            // 睡前血糖
            knBeforeSleepBloodSugar = null,
            // 总胆固醇
            knTotalCholesterol = null,
            // 甘油三酯
            knTriglycerides = null,
            // 低密度脂蛋白
            knLowDensityLipoprotein = null,
            // 高密度脂蛋白
            knHighDensityLipoprotein = null,
            // 吸烟几支
            knNum = null,
            // 白酒
            knWhiteSpirit = null,
            // 啤酒
            knBeer = null,
            // 红酒
            knWine = null,
            // 黄酒
            knYellowRiceSpirit = null,
            // 脉搏氧饱和度
            knPulseOximetry = copdAddParams.pulseOxygenSaturation,
            // 心率
            knHeartRate = null,
            // 脉搏
            knPulse = copdAddParams.pulse.intValueExact(),
            // 摄氏度(体温)
            knCelsius = null
        )
        // 添加成功,调用 batchAddIndicator接口
        indicatorServer.batchAddIndicator(batchAddIndicatorParam)

        // 慢阻肺随访打卡
        clockInService.saveClockIn(copdAddParams.patientId, healthPlanType, LocalDateTime.now())
        return true;
    }

    // 慢阻肺详情
    override fun copdDetail(body: BigInteger): CopdVisit {
        // 根据随访id获得随访详情列表
        val visitModel = hsCopdVisitTable
            .select()
            .where(HsCopdVisitTable.Id.eq(arg(body)))
            .findOne() ?: throw NotFoundDataException(AppSpringUtil.getMessage("mrs.no-find-data"))

        // 获取随访id获取该随访药品详情
        val visitDrugs = hsCopdVisitDrugTable
            .select()
            .where(HsCopdVisitDrugTable.VisitId.eq(arg(body)))
            .find()

        val drugsResult = visitDrugs.map {
            DrugsInner(it.drugName, it.timesPerDay, it.unitsPerTime, it.drugId, it.drugManufacturers)
        }

        return CopdVisit(
            id = visitModel.id,
            patientId = visitModel.patientId,
            patientName = visitModel.patientName,
            doctorId = visitModel.doctorId,
            doctorName = visitModel.doctorName,
            followWay = FllowWay.valueOf(visitModel.followWay),
            followDate = visitModel.followDate,
            isWheezingEffort = visitModel.isWheezingEffort,
            isWheezingWalkFast = visitModel.isWheezingWalkFast,
            isWheezingStroll = visitModel.isWheezingStroll,
            isWheezingHundredMeters = visitModel.isWheezingHundredMeters,
            isWheezingLittle = visitModel.isWheezingLittle,
            isSymptomCough = visitModel.isSymptomCough,
            isSymptomPurulentSputum = visitModel.isSymptomPurulentSputum,
            isSymptomInappetence = visitModel.isSymptomInappetence,
            isSymptomAbdominalDistention = visitModel.isSymptomAbdominalDistention,
            isSymptomBreathing = visitModel.isSymptomBreathing,
            isSymptomSystemicEdema = visitModel.isSymptomSystemicEdema,
            isSymptomNone = visitModel.isSymptomNone,
            symptomOther = visitModel.symptomOther,
            pulseOxygenSaturation = visitModel.pulseOxygenSaturation.toBigDecimal(),
            pulse = visitModel.pulse.toBigDecimal(),
            signsSbp = visitModel.signsSbp.toBigDecimal(),
            signsDbp = visitModel.signsDbp.toBigDecimal(),
            signsHeight = visitModel.signsHeight.toBigDecimal(),
            signsWeight = visitModel.signsWeight.toBigDecimal(),

            isCigarettesPer = visitModel.isCigarettesPer,
            lifeCigarettesPerDay = visitModel.lifeCigarettesPerDay,
            recommendCigarettesPer = visitModel.recommendCigarettesPer,
            // 不饮酒
            isAlcoholPer = visitModel.isAlcoholPer,
            lifeAlcoholPerDay = visitModel.lifeAlcoholPerDay.toBigDecimal(),
            recommendAlcoholPer = visitModel.recommendAlcoholPer?.toBigDecimal(),
            // 每周运动次数
            lifeSportPerWeek = visitModel.lifeSportPerWeek,
            lifeSportPerTime = visitModel.lifeSportPerTime.toBigDecimal(),
            recommendSportPerWeek = visitModel.recommendSportPerWeek,
            recommendSportPerTime = visitModel.recommendSportPerTime?.toBigDecimal(),
            // 摄盐情况
            lifeSaltSituation = visitModel.lifeSaltSituation.let { LifeSalt.valueOf(it) },
            // 遵医行为
            lifeFollowMedicalAdvice = LifeState.valueOf(visitModel.lifeFollowMedicalAdvice),
            // 服药依从性
            drugCompliance = DrugCompliance.valueOf(visitModel.drugCompliance),

            isReactionsPains = visitModel.isReactionsPains,
            isReactionsConvulsion = visitModel.isReactionsConvulsion,
            isReactionsDizzinessHeadache = visitModel.isReactionsDizzinessHeadache,
            isReactionsNauseaVomiting = visitModel.isReactionsNauseaVomiting,
            isReactionsNone = visitModel.isReactionsNone,
            reactionsOther = visitModel.reactionsOther,
            // 肺功能
            pulmonaryFunction = visitModel.pulmonaryFunction.toBigDecimal(),

            createdBy = visitModel.createdBy,
            createdAt = visitModel.createdAt.toLocalDateTime(),
            updatedBy = visitModel.updatedBy,
            updatedAt = visitModel.updatedAt.toLocalDateTime(),
            drugs = drugsResult,
        )
    }

    // 慢阻肺列表
    override fun copdList(visitListParams: VisitListParams): PagedResult<VisitList> {
        // 根据患者id获得该患者慢阻肺随访列表
        val listModels = hsCopdVisitTable
            .select()
            .where(HsCopdVisitTable.PatientId.eq(arg(visitListParams.patientId)))
            .order(HsCopdVisitTable.FollowDate, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)
        // 处理data数据
        val data = listModels.data.map {
            VisitList(
                id = it.id,
                name = VisitType.COPD,
                followDate = it.followDate,
                signsSbp = it.signsSbp.toBigDecimal(),
                signsDbp = it.signsDbp.toBigDecimal(),
                signsHeight = it.signsHeight.toBigDecimal(),
                signsWeight = it.signsWeight.toBigDecimal(),
                signsHeartRate = null,
                signsBim = it.signsBim?.toBigDecimal(),
                createdBy = it.createdBy
            )
        }
        return PagedResult(
            listModels.totalPage,
            listModels.pageSize,
            listModels.pageNo,
            listModels.total,
            data
        )
    }

    // 脑卒中题目选项回填
    override fun getCerebralStrokeDefaultValue(body: BigInteger): CerebralStrokeDetailDefaultValue {
        val mrsScore = this.getLastEvaluateRecord(MRS_EXAMINATION_PAPER_CODE, body)?.totalScore?: BigDecimal.ZERO
        val barthelScore = this.getLastEvaluateRecord(BARTHEL_EXAMINATION_PAPER_CODE, body)?.totalScore?: BigDecimal.ZERO
        val eq5dScore = this.getLastEvaluateRecord(EQ5D_EXAMINATION_PAPER_CODE, body)?.totalScore?: BigDecimal.ZERO

        val optionCodeMap = questionsAnswerClient.getLastAnswerDetailList(
            LastAnswerDetailRequest(
                REHABILITATION_TRAINING_EXAMINATION_PAPER_CODE,
                body
            )
        )
            .groupBy { it.optionCode }

        return CerebralStrokeDetailDefaultValue(
            mrsScore=mrsScore,
            barthelScore=barthelScore,
            eq5dScore=eq5dScore,
            isNormalRecoverSport = optionCodeMap.containsKey(NORMAL_RECOVER_SPORT),
            isNormalRecoverWork = optionCodeMap.containsKey(NORMAL_RECOVER_WORK),
            isNormalRecoverAcknowledge = optionCodeMap.containsKey(NORMAL_RECOVER_ACKNOWLEDGE),
            isNormalRecoverParole = optionCodeMap.containsKey(NORMAL_RECOVER_PAROLE),
            isNormalRecoverSwallow = optionCodeMap.containsKey(NORMAL_RECOVER_SWALLOW),
            isNormalRecoverNone = optionCodeMap.containsKey(NORMAL_RECOVER_NONE),
            isIntelligentRecoverBci = optionCodeMap.containsKey(INTELLIGENT_RECOVER_BCI),
            isIntelligentRecoverRobot = optionCodeMap.containsKey(INTELLIGENT_RECOVER_ROBOT),
            isIntelligentRecoverVirtualReality = optionCodeMap.containsKey(INTELLIGENT_RECOVER_VIRTUAL_REALITY),
            isIntelligentRecoverBalance = optionCodeMap.containsKey(INTELLIGENT_RECOVER_BALANCE),
            isIntelligentRecoverOther = optionCodeMap.containsKey(INTELLIGENT_RECOVER_OTHER),
            isIntelligentRecoverNone = optionCodeMap.containsKey(INTELLIGENT_RECOVER_NONE),
        )
    }

    // 高血压添加
    @Transactional
    override fun htnAdd(htnAddParams: HtnAddParams): Boolean {
        // 主键ID
        val htnVisitId = AppIdUtil.nextId()
        // 添加人id
        val createdId = AppSecurityUtil.currentUserIdWithDefault()

        // 取出变量bmi
        var signsBmi = htnAddParams.signsBim

        // 默认是医生随访, 如果是患者自填, 是患者端随访
        var fromTag = FromTag.DOCTOR_VISIT;

        // 高血压类型为线上还是线下, 医生填写的为线下,
        var healthPlanType = HealthPlanType.OFFLINE_HYPERTENSION

        // 判断是否为患者自填
        if (htnAddParams.followWay == FllowWay.SELF) {
            fromTag = FromTag.PATIENT_SELF
            // 如果是患者自填, 为线上
            healthPlanType = HealthPlanType.HYPERTENSION_VISIT
            // 如果是患者自填,自己计算bmi
            htnAddParams.signsHeight?.let {
                if (htnAddParams.signsBim == null) {
                    signsBmi = getBmiValue(height = it, weight =  htnAddParams.signsWeight)
                }
            } ?: throw MsgException(AppSpringUtil.getMessage("visit.height-not-null"))
        }

        //  拼接高血压需要添加的字段
        val hsHtnVisitAdd = HsHtnVisit.builder()
            .setId(htnVisitId)
            .setPatientId(htnAddParams.patientId)
            .setPatientName(htnAddParams.patientName)
            // 随访方式
            .setFollowWay(htnAddParams.followWay.name)
            // 随访日期
            .setFollowDate(htnAddParams.followDate)
            // 血压 - 收缩压. 单位: mmHg
            .setSignsSbp(htnAddParams.signsSbp.toDouble())
            // 血压 - 舒张压. 单位: mmHg
            .setSignsDbp(htnAddParams.signsDbp.toDouble())
            // 体重
            .setSignsWeight(htnAddParams.signsWeight.toDouble())
            // 心率. 单位: 次/分钟
            .setSignsHeartRate(htnAddParams.signsHeartRate.toDouble())
            // 日吸烟量. 单位: 支
            .setLifeCigarettesPerDay(htnAddParams.lifeCigarettesPerDay)
            // 日饮酒量. 单位: 两
            .setLifeAlcoholPerDay(htnAddParams.lifeAlcoholPerDay.toDouble())
            // 每周运动次数. 单位: 次
            .setLifeSportPerWeek(htnAddParams.lifeSportPerWeek)
            // 每次运动时间. 单位: 分钟
            .setLifeSportPerTime(htnAddParams.lifeSportPerTime.toDouble())
            // 摄盐情况. 1: 轻; 2: 中; 3: 重
            .setLifeSaltSituation(htnAddParams.lifeSaltSituation.name)
            // 遵医行为. 1: 良好; 2: 一般; 3: 差
            .setLifeFollowMedicalAdvice(htnAddParams.lifeFollowMedicalAdvice.name)
            // 服药依从性. 1: 规律; 2: 间断; 3: 不服药
            .setDrugCompliance(htnAddParams.drugCompliance.name)
            .setCreatedBy(createdId)
            .setUpdatedBy(createdId)

            .setDoctorId(htnAddParams.doctorId)
            .setDoctorName(htnAddParams.doctorName)
            // 无症状
            .setIsSymptomNone(htnAddParams.isSymptomNone)
            // 下次随访日期
            .setNextVisit(htnAddParams.nextVisit)
            // 头痛头晕
            .setIsSymptomDizzinessHeadache(htnAddParams.isSymptomDizzinessHeadache)
            // 恶心呕吐
            .setIsSymptomNauseaVomiting(htnAddParams.isSymptomNauseaVomiting)
            // 眼花耳鸣
            .setIsSymptomBlurredTinnitus(htnAddParams.isSymptomBlurredTinnitus)
            // 呼吸困难
            .setIsSymptomBreathing(htnAddParams.isSymptomBreathing)
            // 心悸胸闷
            .setIsSymptomPalpitationsToc(htnAddParams.isSymptomPalpitationsToc)
            // 鼻衄出血不止
            .setIsSymptomNoseBleed(htnAddParams.isSymptomNoseBleed)
            // 四肢发麻
            .setIsSymptomNumbness(htnAddParams.isSymptomNumbness)
            // 下肢水肿
            .setIsSymptomLowerExtremityEdema(htnAddParams.isSymptomLowerExtremityEdema)
            // 其它
            .setSymptomOther(htnAddParams.symptomOther)
            // 身高
            .setSignsHeight(htnAddParams.signsHeight?.toDouble())
            //体质指数
            .setSignsBim(htnAddParams.signsBim?.toDouble())
            // 体征 - 其他
            .setSignsOther(htnAddParams.signsOther)
            // 心理调整. 1: 良好; 2: 一般; 3: 差
            .setLifeMentalAdjustment(htnAddParams.lifeMentalAdjustment?.name)
            // 辅助检查
            .setAbbreviationsAboutExamination(htnAddParams.abbreviationsAboutExamination)
            // 有无药物不良反应
            .setIsAdr(htnAddParams.isAdr)
            // 药物不良反应描述
            .setAdrDesc(htnAddParams.adrDesc)
            // 此次随访分类. 1: 控制满意; 2: 控制不满意; 3: 不良反应; 4: 并发症
            .setVisitClass(htnAddParams.visitClass?.name)
            // 是否转诊
            .setIsReferral(htnAddParams.isReferral)
            // 转诊原因
            .setReferralReason(htnAddParams.referralReason)
            // 转诊机构及科别
            .setReferralAgencies(htnAddParams.referralAgencies)
            // 下次随访日期
            .setNextVisit(htnAddParams.nextVisit)
            // 建议体重
            .setRecommendWeight(htnAddParams.recommendWeight?.toDouble())
            // 建议体质指数
            .setRecommendBim(htnAddParams.recommendBim?.toDouble())
            // 建议日吸烟量
            .setRecommendCigarettesPer(htnAddParams.recommendCigarettesPer)
            // 建议日饮酒量
            .setRecommendAlcoholPer(htnAddParams.recommendAlcoholPer?.toDouble())
            // 建议每周运动次数
            .setRecommendSportPerWeek(htnAddParams.recommendSportPerWeek)
            // 建议每次运动时间
            .setRecommendSportPerTime(htnAddParams.recommendSportPerTime?.toDouble())
            // 建议摄盐情况
            .setRecommendSaltSituation(htnAddParams.recommendSaltSituation?.name)
            .build()
        // 执行添加语句
        hsHtnVisitTable.save(hsHtnVisitAdd)
        // 拼接需要添加的高血压药品
        htnAddParams.drugs
            .takeIf { it.isNullOrEmpty().not() }
            ?.let { drugs ->
                for (it in drugs) {
                    val hsHtnVisitDrug = HsHtnVisitDrug.builder()
                        .setId(AppIdUtil.nextId())
                        .setVisitId(htnVisitId)
                        .setCreatedBy(createdId)
                        .setUpdatedBy(createdId)
                        .setDrugId(it.drugId)
                        .setDrugName(it.drugName)
                        .setTimesPerDay(it.timesPerDay)
                        .setUnitsPerTime(it.unitsPerTime)
                        .setDrugManufacturers(it.drugManufacturers)
                        .build()
                    hsHtnVisitDrugTable.save(hsHtnVisitDrug)
                }
            }

        // 添加成功,调用 batchAddIndicator的参数
        val batchAddIndicatorParam = BatchIndicator(
            // 患者ID
            patientId = htnAddParams.patientId,
            // 来源标识
            fromTag = fromTag,
            // 身高
            knBodyHeight = htnAddParams.signsHeight,
            // 体重
            knBodyWeight = htnAddParams.signsWeight,
            // 体质指数
            knBmi = signsBmi,
            // 腰围
            knWaistline = null,
            // 收缩压
            knSystolicBloodPressure = htnAddParams.signsSbp,
            // 舒张压
            knDiastolicBloodPressure = htnAddParams.signsDbp,
            // 空腹血糖
            knFastingBloodSandalwood = null,
            // 餐前血糖mmol/L（午餐）
            knBeforeLunchBloodSugar = null,
            // 餐前血糖mmol/L（晚餐）
            knBeforeDinnerBloodSugar = null,
            // 随机血糖
            knRandomBloodSugar = null,
            // 餐后2个小时血糖
            knAfterMealBloodSugar = null,
            // 餐后2h血糖mmol/L（午餐）
            knAfterLunchBloodSugar = null,
            // 餐后2h血糖mmol/L（晚餐）
            knAfterDinnerBloodSugar = null,
            // 睡前血糖
            knBeforeSleepBloodSugar = null,
            // 总胆固醇
            knTotalCholesterol = null,
            // 甘油三酯
            knTriglycerides = null,
            // 低密度脂蛋白
            knLowDensityLipoprotein = null,
            // 高密度脂蛋白
            knHighDensityLipoprotein = null,
            // 吸烟几支
            knNum = null,
            // 白酒
            knWhiteSpirit = null,
            // 啤酒
            knBeer = null,
            // 红酒
            knWine = null,
            // 黄酒
            knYellowRiceSpirit = null,
            // 脉搏氧饱和度
            knPulseOximetry = null,
            // 心率
            knHeartRate = htnAddParams.signsHeartRate.toInt(),
            // 脉搏
            knPulse = null,
            // 摄氏度(体温)
            knCelsius = null
        )
        // 添加成功,调用 batchAddIndicator接口
        indicatorServer.batchAddIndicator(batchAddIndicatorParam)

        //高血压随访打卡
        clockInService.saveClockIn(htnAddParams.patientId, healthPlanType, LocalDateTime.now())
        return true
    }

    // 高血压详情
    override fun htnDetail(body: BigInteger): HtnVisit {
        // 根据随访id获得随访详情列表
        val visitModel = hsHtnVisitTable
            .select()
            .where(HsHtnVisitTable.Id.eq(arg(body)))
            .findOne() ?: throw NotFoundDataException(AppSpringUtil.getMessage("mrs.no-find-data"))

        // 获取随访id获取该随访高血压药品详情
        val hypertensionDrugs = hsHtnVisitDrugTable
            .select()
            .where(HsHtnVisitDrugTable.VisitId.eq(arg(body)))
            .find()

        val drugsResult = hypertensionDrugs.map {
            DrugsInner(it.drugName, it.timesPerDay, it.unitsPerTime, it.drugId, it.drugManufacturers)
        }

        return HtnVisit(
            patientId = visitModel.patientId,
            id = visitModel.id,
            patientName = visitModel.patientName,
            doctorId = visitModel.doctorId,
            doctorName = visitModel.doctorName,
            followWay = visitModel.followWay.let { FllowWay.valueOf(it) },
            followDate = visitModel.followDate,
            isSymptomNone = visitModel.isSymptomNone,
            isSymptomDizzinessHeadache = visitModel.isSymptomDizzinessHeadache,
            isSymptomNauseaVomiting = visitModel.isSymptomNauseaVomiting,
            isSymptomBlurredTinnitus = visitModel.isSymptomBlurredTinnitus,
            isSymptomBreathing = visitModel.isSymptomBreathing,
            isSymptomPalpitationsToc = visitModel.isSymptomPalpitationsToc,
            isSymptomNoseBleed = visitModel.isSymptomNoseBleed,
            isSymptomNumbness = visitModel.isSymptomNumbness,
            isSymptomLowerExtremityEdema = visitModel.isSymptomLowerExtremityEdema,
            symptomOther = visitModel.symptomOther,
            signsSbp = visitModel.signsSbp.toBigDecimal(),
            signsDbp = visitModel.signsDbp.toBigDecimal(),
            signsHeight = visitModel.signsHeight?.toBigDecimal(),
            signsWeight = visitModel.signsWeight.toBigDecimal(),
            signsHeartRate = visitModel.signsHeartRate.toBigDecimal(),
            signsBim = visitModel.signsBim?.toBigDecimal(),
            signsOther = visitModel.signsOther,
            lifeCigarettesPerDay = visitModel.lifeCigarettesPerDay,
            lifeAlcoholPerDay = visitModel.lifeAlcoholPerDay.toBigDecimal(),
            lifeSportPerWeek = visitModel.lifeSportPerWeek,
            lifeSportPerTime = visitModel.lifeSportPerTime.toBigDecimal(),
            lifeSaltSituation = LifeSalt.valueOf(visitModel.lifeSaltSituation),
            lifeMentalAdjustment = visitModel.lifeMentalAdjustment?.let { LifeState.valueOf(it) },
            lifeFollowMedicalAdvice = LifeState.valueOf(visitModel.lifeFollowMedicalAdvice),
            abbreviationsAboutExamination = visitModel.abbreviationsAboutExamination,
            drugCompliance = DrugCompliance.valueOf(visitModel.drugCompliance),
            isAdr = visitModel.isAdr,
            adrDesc = visitModel.adrDesc,
            visitClass = visitModel.visitClass?.let { VisitClass.valueOf(it) },
            isReferral = visitModel.isReferral,
            referralReason = visitModel.referralReason,
            referralAgencies = visitModel.referralAgencies,
            nextVisit = visitModel.nextVisit,
            createdBy = visitModel.createdBy,
            createdAt = visitModel.createdAt.toLocalDateTime(),
            updatedBy = visitModel.updatedBy,
            updatedAt = visitModel.updatedAt.toLocalDateTime(),
            drugs = drugsResult,
            recommendWeight = visitModel.recommendWeight?.toBigDecimal(),
            recommendBim = visitModel.recommendBim?.toBigDecimal(),
            recommendCigarettesPer = visitModel.recommendAlcoholPer?.toInt(),
            recommendAlcoholPer = visitModel.recommendAlcoholPer?.toBigDecimal(),
            recommendSportPerWeek = visitModel.recommendSportPerWeek,
            recommendSportPerTime = visitModel.recommendSportPerTime?.toBigDecimal(),
            recommendSaltSituation = visitModel.recommendSaltSituation?.let { LifeSalt.valueOf(it) }
        )
    }

    // 高血压列表
    override fun htnList(visitListParams: VisitListParams): PagedResult<VisitList> {
        // 根据患者id获得该患者高血压随访列表
        var hypertensionVisitList = hsHtnVisitTable
            .select()
            .where(HsHtnVisitTable.PatientId.eq(arg(visitListParams.patientId)))
            .order(HsHtnVisitTable.FollowDate, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)
        val data = hypertensionVisitList.data.map {
            VisitList(
                id = it.id,
                name = VisitType.HYPERTENSION,
                followDate = it.followDate,
                signsSbp = it.signsSbp.toBigDecimal(),
                signsDbp = it.signsDbp.toBigDecimal(),
                signsHeight = it.signsHeight?.toBigDecimal(),
                signsWeight = it.signsWeight.toBigDecimal(),
                signsHeartRate = it.signsHeartRate.toBigDecimal(),
                signsBim = it.signsBim?.toBigDecimal(),
                createdBy = it.createdBy
            )
        }
        return PagedResult(
            hypertensionVisitList.totalPage,
            hypertensionVisitList.pageSize,
            hypertensionVisitList.pageNo,
            hypertensionVisitList.total,
            data
        )
    }

    // 慢病随访列表
    override fun visitList(visitListParam: VisitListParam): PagedResult<VisitList> {
        val type =
            if (visitListParam.type == null || visitListParam.type!!.size == 0) null else visitListParam.type!!.map { it.name }

        // 判断是医生随访还是患者自填
        val followWays = if (visitListParam.isDoctor == null)
            null
        else if (visitListParam.isDoctor == true) {
            listOf(FllowWay.FAMILY.name,  FllowWay.ONLINE.name,  FllowWay.OUTPATIENT.name,  FllowWay.PHONE.name)
        } else {
            listOf(FllowWay.SELF.name)
        }

        val list = hsHtnVisitDao.htnVistTablelist(
            visitListParam.patientId,
            type,
            followWays,
            visitListParam.pageSize,
            visitListParam.pageNo
        )
        return PagedResult.fromDbPaged(list) {
            VisitList(
                id = it.id,
                name = VisitType.valueOf(it.name),
                followDate = it.followDate,
                signsSbp = it.signsSbp?.toBigDecimal(),
                signsDbp = it.signsDbp?.toBigDecimal(),
                signsHeight = it.signsHeight?.toBigDecimal(),
                signsWeight = it.signsWeight?.toBigDecimal(),
                signsHeartRate = it.signsHeartRate?.toBigDecimal(),
                signsBim = it.signsBim?.toBigDecimal(),
                glu = it.glu?.toBigDecimal(),
                createdBy = it.createdBy
            )
        }
    }

    // 糖尿病添加
    @Transactional
    override fun t2dmAdd(t2dmAddParams: T2dmAddParams): Boolean {
        // 主键ID
        val visitId = AppIdUtil.nextId()
        // 添加人id
        val createdId = AppSecurityUtil.currentUserIdWithDefault()

        // 取出变量bmi
        var signsBmi = t2dmAddParams.signsBim
        // 默认是医生随访, 如果是患者自填, 是患者端随访
        var fromTag = FromTag.DOCTOR_VISIT;
        // 判断糖尿病类型为线上还是线下, 医生填写的为线下,
        var healthPlanType = HealthPlanType.OFFLINE_DIABETES

        if (t2dmAddParams.followWay == FllowWay.SELF) {
            fromTag = FromTag.PATIENT_SELF
            // 如果为自填的为线上
            healthPlanType = HealthPlanType.ONLINE_DIABETES
            // 如果是患者自填,自己计算bmi
            t2dmAddParams.signsHeight?.let {
                if (t2dmAddParams.signsBim == null) {
                    signsBmi = getBmiValue(height = it, weight =  t2dmAddParams.signsWeight)
                }
            } ?: throw MsgException(AppSpringUtil.getMessage("visit.height-not-null"))
        }

        //  拼接糖尿病需要添加的字段
        val diabetesVisitAdd = HsT2dmVisit.builder()
            .setId(visitId)
            .setPatientId(t2dmAddParams.patientId)
            .setPatientName(t2dmAddParams.patientName)
            // 随访方式
            .setFollowWay(t2dmAddParams.followWay.name)
            // 随访日期
            .setFollowDate(t2dmAddParams.followDate)
            // 血压 - 收缩压. 单位: mmHg
            .setSignsSbp(t2dmAddParams.signsSbp.toDouble())
            // 血压 - 舒张压. 单位: mmHg
            .setSignsDbp(t2dmAddParams.signsDbp.toDouble())
            // 体重
            .setSignsWeight(t2dmAddParams.signsWeight.toDouble())
            // 足背动脉搏动左侧:NORMAL-正常,RECEDE-减弱,VANISH-消失
            .setSignsAdpLeft(t2dmAddParams.signsAdpLeft.name)
            // 足背动脉搏动右侧:NORMAL-正常,RECEDE-减弱,VANISH-消失
            .setSignsAdpRight(t2dmAddParams.signsAdpRight.name)
            // 日吸烟量. 单位: 支
            .setLifeCigarettesPerDay(t2dmAddParams.lifeCigarettesPerDay)
            // 日饮酒量. 单位: 两
            .setLifeAlcoholPerDay(t2dmAddParams.lifeAlcoholPerDay.toDouble())
            // 每周运动次数. 单位: 次
            .setLifeSportPerWeek(t2dmAddParams.lifeSportPerWeek.toDouble())
            // 每次运动时间. 单位: 分钟
            .setLifeSportPerTime(t2dmAddParams.lifeSportPerTime.toDouble())
            // 主食. 单位: 克/天
            .setLifeFoodSituation(t2dmAddParams.lifeFoodSituation.toDouble())
            // 遵医行为
            .setLifeFollowMedicalAdvice(t2dmAddParams.lifeFollowMedicalAdvice.name)
            // 空腹血糖. 单位: mmol/L
            .setGlu(t2dmAddParams.glu.toDouble())
            // 服药依从性
            .setDrugCompliance(t2dmAddParams.drugCompliance.name)
            // 低血糖反应
            .setRhg(t2dmAddParams.rhg.name)
            .setCreatedBy(createdId)
            .setUpdatedBy(createdId)
            .setDoctorId(t2dmAddParams.doctorId)
            .setDoctorName(t2dmAddParams.doctorName)
            // 无症状
            .setIsSymptomNone(t2dmAddParams.isSymptomNone)
            .setIsSymptomDrink(t2dmAddParams.isSymptomDrink)
            .setIsSymptomEat(t2dmAddParams.isSymptomEat)
            .setIsSymptomDiuresis(t2dmAddParams.isSymptomDiuresis)
            .setIsSymptomBlurred(t2dmAddParams.isSymptomBlurred)
            .setIsSymptomInfection(t2dmAddParams.isSymptomInfection)
            .setIsSymptomHandFeetNumbness(t2dmAddParams.isSymptomHandFeetNumbness)
            .setIsSymptomLowerExtremityEdema(t2dmAddParams.isSymptomLowerExtremityEdema)
            .setIsSymptomWeightLossDiagnosed(t2dmAddParams.isSymptomWeightLossDiagnosed)
            // 其它
            .setSymptomOther(t2dmAddParams.symptomOther)
            // 身高
            .setSignsHeight(t2dmAddParams.signsHeight?.toDouble())
            .setSignsBim(t2dmAddParams.signsBim?.toDouble())
            // 心理调整
            .setLifeMentalAdjustment(t2dmAddParams.lifeMentalAdjustment?.name)
            // 辅助检查其他检查
            .setAssistOther(t2dmAddParams.assistOther)
            // 有无药物不良反应
            .setIsAdr(t2dmAddParams.isAdr)
            // 药物不良反应描述
            .setAdrDesc(t2dmAddParams.adrDesc)
            // 此次随访分类
            .setVisitClass(t2dmAddParams.visitClass?.name)
            // 胰岛素名称
            .setInsulinName(t2dmAddParams.insulinName)
            // 胰岛素用法用量
            .setInsulinDesc(t2dmAddParams.insulinDesc)
            // 下次随访日期
            .setNextVisit(t2dmAddParams.nextVisit)
            // 是否转诊
            .setIsHasReferral(t2dmAddParams.isHasReferral)
            // 转诊原因
            .setReferralReason(t2dmAddParams.referralReason)
            // 转诊机构及科别
            .setReferralAgencies(t2dmAddParams.referralAgencies)
            // 建议体重
            .setRecommendWeight(t2dmAddParams.recommendWeight?.toDouble())
            // 建议体质指数
            .setRecommendBim(t2dmAddParams.recommendBim?.toDouble())
            // 建议日吸烟量
            .setRecommendCigarettesPer(t2dmAddParams.recommendCigarettesPer)
            // 建议日饮酒量
            .setRecommendAlcoholPer(t2dmAddParams.recommendAlcoholPer?.toDouble())
            // 建议每周运动次数
            .setRecommendSportPerWeek(t2dmAddParams.recommendSportPerWeek?.toInt())
            // 建议每次运动时间
            .setRecommendSportPerTime(t2dmAddParams.recommendSportPerTime?.toDouble())
            // 糖化血糖蛋白值. 单位: %
            .setHbA1c(t2dmAddParams.hbA1c?.toDouble())
            // 糖化血糖蛋白检查日期
            .setHbA1cDate(t2dmAddParams.hbA1cDate)
            .build()
        hsT2dmVisitTable.save(diabetesVisitAdd)

        // 拼接需要添加的糖尿病药品
        if (t2dmAddParams.drugs.isNullOrEmpty().not()) {
            for (it in t2dmAddParams.drugs!!) {
                val diabetesVisitDrugAdd = HsT2dmVisitDrug.builder()
                    .setId(AppIdUtil.nextId())
                    .setVisitId(visitId)
                    .setCreatedBy(createdId)
                    .setUpdatedBy(createdId)
                    .setDrugId(it.drugId)
                    .setDrugName(it.drugName)
                    .setTimesPerDay(it.timesPerDay)
                    .setUnitsPerTime(it.unitsPerTime)
                    .setDrugManufacturers(it.drugManufacturers)
                    .build()
                hsT2dmVisitDrugTable.save(diabetesVisitDrugAdd)
            }
        }

        // 添加成功,调用 batchAddIndicator的参数
        val batchAddIndicatorParam = BatchIndicator(
            // 患者ID
            patientId = t2dmAddParams.patientId,
            // 来源标识
            fromTag = fromTag,
            // 身高
            knBodyHeight = t2dmAddParams.signsHeight,
            // 体重
            knBodyWeight = t2dmAddParams.signsWeight,
            // 体质指数
            knBmi = signsBmi,
            // 腰围
            knWaistline = null,
            // 收缩压
            knSystolicBloodPressure = t2dmAddParams.signsSbp,
            // 舒张压
            knDiastolicBloodPressure = t2dmAddParams.signsDbp,
            // 空腹血糖
            knFastingBloodSandalwood = t2dmAddParams.glu,
            // 餐前血糖mmol/L（午餐）
            knBeforeLunchBloodSugar = null,
            // 餐前血糖mmol/L（晚餐）
            knBeforeDinnerBloodSugar = null,
            // 随机血糖
            knRandomBloodSugar = null,
            // 餐后2个小时血糖
            knAfterMealBloodSugar = null,
            // 餐后2h血糖mmol/L（午餐）
            knAfterLunchBloodSugar = null,
            // 餐后2h血糖mmol/L（晚餐）
            knAfterDinnerBloodSugar = null,
            // 睡前血糖
            knBeforeSleepBloodSugar = null,
            // 总胆固醇
            knTotalCholesterol = null,
            // 甘油三酯
            knTriglycerides = null,
            // 低密度脂蛋白
            knLowDensityLipoprotein = null,
            // 高密度脂蛋白
            knHighDensityLipoprotein = null,
            // 吸烟几支
            knNum = null,
            // 白酒
            knWhiteSpirit = null,
            // 啤酒
            knBeer = null,
            // 红酒
            knWine = null,
            // 黄酒
            knYellowRiceSpirit = null,
            // 脉搏氧饱和度
            knPulseOximetry = null,
            // 心率
            knHeartRate = null,
            // 脉搏
            knPulse = null,
            // 摄氏度(体温)
            knCelsius = null
        )
        // 添加成功,调用 batchAddIndicator接口
        indicatorServer.batchAddIndicator(batchAddIndicatorParam)
        // 糖尿病随访打卡
        clockInService.saveClockIn(t2dmAddParams.patientId, healthPlanType, LocalDateTime.now())
        return true
    }

    // 糖尿病详情
    override fun t2dmDetail(body: BigInteger): T2dmVisit {
        // 糖尿病随访信息
        val diabetesVisits = hsT2dmVisitTable
            .select()
            .where(HsT2dmVisitTable.Id.eq(arg(body)))
            .findOne() ?: throw NotFoundDataException(AppSpringUtil.getMessage("mrs.no-find-data"))

        // 获取随访id获取该随访糖尿病药品详情
        val diabetesDrugs = hsT2dmVisitDrugTable
            .select()
            .where(HsT2dmVisitDrugTable.VisitId.eq(arg(body)))
            .find()

        val drugsResult = diabetesDrugs.map {
            DrugsInner(it.drugName, it.timesPerDay, it.unitsPerTime, it.drugId, it.drugManufacturers)
        }
        return T2dmVisit(
            patientId = diabetesVisits.patientId,
            id = diabetesVisits.id,
            patientName = diabetesVisits.patientName,
            doctorId = diabetesVisits.doctorId,
            doctorName = diabetesVisits.doctorName,
            followWay = FllowWay.valueOf(diabetesVisits.followWay),
            followDate = diabetesVisits.followDate,
            isSymptomNone = diabetesVisits.isSymptomNone,
            isSymptomDrink = diabetesVisits.isSymptomDrink,
            isSymptomEat = diabetesVisits.isSymptomEat,
            isSymptomDiuresis = diabetesVisits.isSymptomDiuresis,
            isSymptomBlurred = diabetesVisits.isSymptomBlurred,
            isSymptomInfection = diabetesVisits.isSymptomInfection,
            isSymptomHandFeetNumbness = diabetesVisits.isSymptomHandFeetNumbness,
            isSymptomLowerExtremityEdema = diabetesVisits.isSymptomLowerExtremityEdema,
            isSymptomWeightLossDiagnosed = diabetesVisits.isSymptomWeightLossDiagnosed,
            symptomOther = diabetesVisits.symptomOther,
            signsSbp = diabetesVisits.signsSbp.toBigDecimal(),
            signsDbp = diabetesVisits.signsDbp.toBigDecimal(),
            signsHeight = diabetesVisits.signsHeight?.toBigDecimal(),
            signsWeight = diabetesVisits.signsWeight.toBigDecimal(),
            signsBim = diabetesVisits.signsBim?.toBigDecimal(),
            signsAdpLeft = SignsAdp.valueOf(diabetesVisits.signsAdpLeft),
            signsAdpRight = SignsAdp.valueOf(diabetesVisits.signsAdpRight),
            lifeCigarettesPerDay = diabetesVisits.lifeCigarettesPerDay,
            lifeAlcoholPerDay = diabetesVisits.lifeAlcoholPerDay.toBigDecimal(),
            lifeSportPerTime = diabetesVisits.lifeSportPerTime.toBigDecimal(),
            lifeFoodSituation = diabetesVisits.lifeFoodSituation.toBigDecimal(),
            lifeMentalAdjustment = diabetesVisits.lifeMentalAdjustment?.let { LifeState.valueOf(it) },
            lifeFollowMedicalAdvice = LifeState.valueOf(diabetesVisits.lifeFollowMedicalAdvice),
            glu = diabetesVisits.glu.toBigDecimal(),
            hbA1c = diabetesVisits.hbA1c?.toBigDecimal(),
            hbA1cDate = diabetesVisits.hbA1cDate,
            assistOther = diabetesVisits.assistOther,
            drugCompliance = DrugCompliance.valueOf(diabetesVisits.drugCompliance),
            isAdr = diabetesVisits.isAdr,
            adrDesc = diabetesVisits.adrDesc,
            rhg = Rhg.valueOf(diabetesVisits.rhg),
            visitClass = diabetesVisits.visitClass?.let { VisitClass.valueOf(it) },
            insulinName = diabetesVisits.insulinName,
            insulinDesc = diabetesVisits.insulinDesc,
            isHasReferral = diabetesVisits.isHasReferral,
            referralReason = diabetesVisits.referralReason,
            referralAgencies = diabetesVisits.referralAgencies,
            nextVisit = diabetesVisits.nextVisit,
            createdBy = diabetesVisits.createdBy,
            createdAt = diabetesVisits.createdAt.toLocalDateTime(),
            updatedBy = diabetesVisits.updatedBy,
            updatedAt = diabetesVisits.updatedAt.toLocalDateTime(),
            drugs = drugsResult,
            recommendWeight = diabetesVisits.recommendWeight?.toBigDecimal(),
            recommendBim = diabetesVisits.recommendBim?.toBigDecimal(),
            recommendCigarettesPer = diabetesVisits.recommendAlcoholPer?.toInt(),
            recommendAlcoholPer = diabetesVisits.recommendAlcoholPer?.toBigDecimal(),
            recommendSportPerWeek = diabetesVisits.recommendSportPerWeek?.toBigDecimal(),
            recommendSportPerTime = diabetesVisits.recommendSportPerTime?.toBigDecimal(),
            lifeSportPerWeek = diabetesVisits.lifeSportPerWeek.toBigDecimal()
        )
    }

    // 糖尿病列表
    override fun t2dmList(visitListParams: VisitListParams): PagedResult<VisitList> {
        // 根据患者id获得该患者糖尿病随访列表
        var diabetesVisitList = hsT2dmVisitTable
            .select()
            .where(HsT2dmVisitTable.PatientId.eq(arg(visitListParams.patientId)))
            .order(HsT2dmVisitTable.FollowDate, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)
        val data = diabetesVisitList.data.map {
            VisitList(
                id = it.id,
                name = VisitType.DIABETES,
                followDate = it.followDate,
                signsSbp = it.signsSbp.toBigDecimal(),
                signsDbp = it.signsDbp.toBigDecimal(),
                signsHeight = it.signsHeight?.toBigDecimal(),
                signsWeight = it.signsWeight.toBigDecimal(),
                signsBim = it.signsBim?.toBigDecimal(),
                glu = it.glu.toBigDecimal(),
                createdBy = it.createdBy
            )
        }
        return PagedResult(
            diabetesVisitList.totalPage,
            diabetesVisitList.pageSize,
            diabetesVisitList.pageNo,
            diabetesVisitList.total,
            data
        )
    }

    //脑卒中出院随访添加
    @Transactional
    override fun cerebralStrokeLeaveHospitalVisitAdd(cerebralStrokeLeaveHospitalVisitAddParam: CerebralStrokeLeaveHospitalVisitAddParam) {
        val leaveHospitalDate = cerebralStrokeLeaveHospitalVisitAddParam.knLeaveHospitalDate
        val patientId = cerebralStrokeLeaveHospitalVisitAddParam.patientId
        val createdAt = LocalDateTime.now()
        //1、先查询此患者最近一次出院随访
        val oldLeaveHospitalVisit = getLastOneCSLeaveHospitalVisit(patientId)

        //2、插入出院随访表
        val hsCerebralStrokeLeaveHospitalVisitAdd = HsCerebralStrokeLeaveHospitalVisit.builder()
            .setKnId(AppIdUtil.nextId())
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setPatientId(patientId)
            .setPatientName(cerebralStrokeLeaveHospitalVisitAddParam.patientName)
            .setDoctorId(cerebralStrokeLeaveHospitalVisitAddParam.doctorId)
            .setFollowWay(cerebralStrokeLeaveHospitalVisitAddParam.followWay.name)
            .setFollowDate(cerebralStrokeLeaveHospitalVisitAddParam.followDate)
            .setKnGender(cerebralStrokeLeaveHospitalVisitAddParam.knGender.name)
            .setKnAge(cerebralStrokeLeaveHospitalVisitAddParam.knAge)
            .setKnMedicalRecordNumber(cerebralStrokeLeaveHospitalVisitAddParam.knMedicalRecordNumber)
            .setKnImageNo(cerebralStrokeLeaveHospitalVisitAddParam.knImageNo)
            .setKnTelephone(cerebralStrokeLeaveHospitalVisitAddParam.knTelephone)
            .setKnAdmissionDate(cerebralStrokeLeaveHospitalVisitAddParam.knAdmissionDate)
            .setKnLeaveHospitalDate(leaveHospitalDate)
            .setKnOaSoiBrainInfarctionSite(cerebralStrokeLeaveHospitalVisitAddParam.knOaSoiBrainInfarctionSite.name)
            .setKnOaBrainInfarctionToast(cerebralStrokeLeaveHospitalVisitAddParam.knOaBrainInfarctionToast.name)
            .setKnOaPreMorbidMrsScore(cerebralStrokeLeaveHospitalVisitAddParam.knOaPreMorbidMrsScore)
            .setKnOaMrsScore(cerebralStrokeLeaveHospitalVisitAddParam.knOaMrsScore)
            .setKnOaNiHssScore(cerebralStrokeLeaveHospitalVisitAddParam.knOaNiHssScore)
            .setKnOaBarthelScore(cerebralStrokeLeaveHospitalVisitAddParam.knOaBarthelScore)
            .setKnOaEq5dScore(cerebralStrokeLeaveHospitalVisitAddParam.knOaEq5dScore)
            .setKnLhIsStandardMedication(cerebralStrokeLeaveHospitalVisitAddParam.knLhIsStandardMedication.name)
            .setIsStandardLipidLoweringTreat(cerebralStrokeLeaveHospitalVisitAddParam.knLhIsStandardLipidLoweringTreat)
            .setIsThrombolytic(cerebralStrokeLeaveHospitalVisitAddParam.knLhIsThrombolytic)
            .setIsAcuteStageCerebralInfarction(cerebralStrokeLeaveHospitalVisitAddParam.knLhAcuteStageCerebralInfarction)
            .setIsMergeSeriousIllness(cerebralStrokeLeaveHospitalVisitAddParam.knLhIsMergeSeriousIllness)
            .setIsStandardHighRiskElementControl(cerebralStrokeLeaveHospitalVisitAddParam.knLhIsStandardHighRiskElementControl)
            .setKnLhTotalRehabilitationTime(cerebralStrokeLeaveHospitalVisitAddParam.knLhTotalRehabilitationTime)
            .setKnLhMrsScore(cerebralStrokeLeaveHospitalVisitAddParam.knLhMrsScore)
            .setKnLhBarthelScore(cerebralStrokeLeaveHospitalVisitAddParam.knLhBarthelScore)
            .setKnLhEq5dScore(cerebralStrokeLeaveHospitalVisitAddParam.knLhEq5dScore)
            .setIsDysfunctionSportObstacle(cerebralStrokeLeaveHospitalVisitAddParam.knDysfunctionSportObstacle)
            .setIsDysfunctionCognitionObstacle(cerebralStrokeLeaveHospitalVisitAddParam.knDysfunctionCognitionObstacle)
            .setIsDysfunctionSpeechObstacle(cerebralStrokeLeaveHospitalVisitAddParam.knDysfunctionSpeechObstacle)
            .setIsDysfunctionSwallowObstacle(cerebralStrokeLeaveHospitalVisitAddParam.knDysfunctionSwallowObstacle)
            .setDoctorName(cerebralStrokeLeaveHospitalVisitAddParam.doctorName)
            .setCreatedAt(createdAt)
            .build()
        hsCerebralStrokeLeaveHospitalVisitTable.save(hsCerebralStrokeLeaveHospitalVisitAdd)

        //3、（如果存在有效的脑卒中健康管理方案）并且（上次出院随访的出院时间和此次随访填写的出院时间不同），才会创建脑卒中方案关联的计划
        val healthManage =
            healthSchemeManageService.getCurrentValidHealthSchemeManagementInfoStartWithCreatedAt(cerebralStrokeLeaveHospitalVisitAddParam.patientId)

        // 出院随访打卡
        healthManage?.let {
            clockInService.clockIn(it.knId, healthPlanType = HealthPlanType.LEAVE_HOSPITAL_VISIT, createdAt)
        }
        if (healthManage != null
            && HealthManageType.CEREBRAL_STROKE.name == healthManage.knHealthManageType
            && (oldLeaveHospitalVisit == null || oldLeaveHospitalVisit.knLeaveHospitalDate != leaveHospitalDate)
        ) {
            this.deleteAndSaveCerebralStrokeRelationPlan(patientId, createdAt.toLocalDate(), healthManage)
        }
    }

    // 脑卒中出院随访详情
    override fun cerebralStrokeLeaveHospitalVisitDetail(body: BigInteger): CerebralStrokeLeaveHospitalVisit {
        val leaveHospitalVisit = hsCerebralStrokeLeaveHospitalVisitTable
            .select()
            .where(HsCerebralStrokeLeaveHospitalVisitTable.KnId eq body.arg)
            .findOne() ?: throw NotFoundDataException(AppSpringUtil.getMessage("visit.leave-hospital-visit.not-found"))

        return toCerebralStrokeLeaveHospitalVisit(leaveHospitalVisit)

    }

    // 脑卒中出院随访列表
    override fun cerebralStrokeLeaveHospitalVisitList(visitListParams: VisitListParams): PagedResult<VisitList> {
        val page = hsCerebralStrokeLeaveHospitalVisitTable.select()
            .where(HsCerebralStrokeLeaveHospitalVisitTable.PatientId eq visitListParams.patientId)
            .order(HsCerebralStrokeLeaveHospitalVisitTable.CreatedAt, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)

        return PagedResult.fromDbPaged(page) {
            VisitList(
                id = it.knId,
                name = VisitType.LEAVE_HOSPITAL_VISIT,
                followDate = it.createdAt,
                signsSbp = null,
                signsDbp = null,
                signsHeight = null,
                signsWeight = null,
                signsBim = null,
                glu = null,
                createdBy = it.createdBy
            )
        }
    }

    // 最近一次脑卒中出院随访记录
    override fun getLastOneCerebralStrokeLeaveHospitalVisit(body: BigInteger): CerebralStrokeLeaveHospitalVisit {
        val leaveHospitalVisit = getLastOneCSLeaveHospitalVisit(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("visit.leave-hospital-visit.not-found"))
        return toCerebralStrokeLeaveHospitalVisit(leaveHospitalVisit)
    }

    private fun getLastOneCSLeaveHospitalVisit(patientId: BigInteger): HsCerebralStrokeLeaveHospitalVisit? {
        return hsCerebralStrokeLeaveHospitalVisitTable.select()
            .where(HsCerebralStrokeLeaveHospitalVisitTable.PatientId eq patientId.arg)
            .order(HsCerebralStrokeLeaveHospitalVisitTable.CreatedAt, Order.Desc)
            .findOne()
    }

    // 脑卒中列表
    override fun cerebralStrokeList(visitListParams: VisitListParams): PagedResult<VisitList> {
        val page = hsCerebralStrokeVisitTable.select()
            .where(HsCerebralStrokeVisitTable.PatientId eq visitListParams.patientId)
            .order(HsCerebralStrokeVisitTable.CreatedAt, Order.Desc)
            .page(visitListParams.pageSize, visitListParams.pageNo)

        return PagedResult.fromDbPaged(page) {
            VisitList(
                id = it.id,
                name = VisitType.CEREBRAL_STROKE,
                followDate = it.createdAt,
                signsSbp = null,
                signsDbp = null,
                signsHeight = null,
                signsWeight = null,
                signsBim = null,
                glu = null,
                createdBy = it.createdBy
            )
        }
    }

    private fun deleteAndSaveCerebralStrokeRelationPlan(
        patientId: BigInteger,
        createdAt: LocalDate,
        healthManage: HsHealthSchemeManagementInfo
    ) {
        //停止上一周期计划的定时任务
        healthSchemeManageService.getHealthPlanList(
            healthManage.knId,
            listOf(
                HealthPlanType.CEREBRAL_STROKE,
                HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
                HealthPlanType.MRS,
                HealthPlanType.BARTHEL,
                HealthPlanType.EQ_5_D
            )
        ).firstOrNull { it.knJobId != null }?.knJobId?.let {
            try {
                jobScheduler.delete(JobId.parse(it), "做完出院随访,新建新的健康方案需要删除旧的健康方案")
            }  catch (e: JobNotFoundException) {
                LOGGER.warn("删除健康方案job任务异常", e)
            }
        }

        //删除并创建周期计划
        healthManageCerebralStrokeServiceImpl.addCerebralStrokeCyclePlan(
            patientId,
            healthManage,
            createdAt,
            THREE_MONTH
        )
    }

    /**
     * 获取问卷得分
     * @param patientId 患者Id
     * @return  问卷记录
     */
    private fun getLastEvaluateRecord(
        code: String,
        patientId: BigInteger
    ): LastAnswerRecord? {
        return questionsAnswerClient.getLastAnswerRecordList(
            LastAnswerRecordListRequest(
                examinationPaperCode = code,
                answerBy = patientId,
                needNum = 1,
                startDate = null,
                endDate = null
            )
        ).firstOrNull()
    }

    private fun toCerebralStrokeLeaveHospitalVisit(leaveHospitalVisit: HsCerebralStrokeLeaveHospitalVisit) =
        CerebralStrokeLeaveHospitalVisit(
            knId = leaveHospitalVisit.knId,
            patientId = leaveHospitalVisit.patientId,
            patientName = leaveHospitalVisit.patientName,
            doctorId = leaveHospitalVisit.doctorId,
            doctorName = leaveHospitalVisit.doctorName,
            followWay = FllowWay.valueOf(leaveHospitalVisit.followWay),
            followDate = leaveHospitalVisit.followDate,
            knGender = Gender.valueOf(leaveHospitalVisit.knGender),
            knAge = leaveHospitalVisit.knAge,
            knMedicalRecordNumber = leaveHospitalVisit.knMedicalRecordNumber,
            knImageNo = leaveHospitalVisit.knImageNo,
            knTelephone = leaveHospitalVisit.knTelephone,
            knAdmissionDate = leaveHospitalVisit.knAdmissionDate,
            knLeaveHospitalDate = leaveHospitalVisit.knLeaveHospitalDate,
            knOaSoiBrainInfarctionSite = InfarctionSite.valueOf(leaveHospitalVisit.knOaSoiBrainInfarctionSite),
            knOaBrainInfarctionToast = ToastTyping.valueOf(leaveHospitalVisit.knOaBrainInfarctionToast),
            knOaPreMorbidMrsScore = leaveHospitalVisit.knOaPreMorbidMrsScore,
            knOaMrsScore = leaveHospitalVisit.knOaMrsScore,
            knOaNiHssScore = leaveHospitalVisit.knOaNiHssScore,
            knOaBarthelScore = leaveHospitalVisit.knOaBarthelScore,
            knOaEq5dScore = leaveHospitalVisit.knOaEq5dScore,
            knLhIsStandardMedication = StandardSecondaryPrevention.valueOf(leaveHospitalVisit.knLhIsStandardMedication),
            knLhIsStandardLipidLoweringTreat = leaveHospitalVisit.isStandardLipidLoweringTreat,
            knLhIsThrombolytic = leaveHospitalVisit.isThrombolytic,
            knLhAcuteStageCerebralInfarction = leaveHospitalVisit.isAcuteStageCerebralInfarction,
            knLhIsMergeSeriousIllness = leaveHospitalVisit.isMergeSeriousIllness,
            knLhIsStandardHighRiskElementControl = leaveHospitalVisit.isStandardHighRiskElementControl,
            knLhTotalRehabilitationTime = leaveHospitalVisit.knLhTotalRehabilitationTime,
            knLhMrsScore = leaveHospitalVisit.knLhMrsScore,
            knLhBarthelScore = leaveHospitalVisit.knLhBarthelScore,
            knLhEq5dScore = leaveHospitalVisit.knLhEq5dScore,
            knDysfunctionSportObstacle = leaveHospitalVisit.isDysfunctionSportObstacle,
            knDysfunctionCognitionObstacle = leaveHospitalVisit.isDysfunctionCognitionObstacle,
            knDysfunctionSpeechObstacle = leaveHospitalVisit.isDysfunctionSpeechObstacle,
            knDysfunctionSwallowObstacle = leaveHospitalVisit.isDysfunctionSwallowObstacle,
            knCreatedAt = leaveHospitalVisit.createdAt
        )
}
