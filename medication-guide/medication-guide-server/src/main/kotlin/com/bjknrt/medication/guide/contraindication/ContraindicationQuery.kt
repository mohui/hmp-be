package com.bjknrt.medication.guide.contraindication

import ContraindicationAnchorType
import cn.hutool.core.collection.CollectionUtil
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.medication.guide.*
import com.bjknrt.medication.guide.vo.ContraindicationType
import com.bjknrt.medication.guide.vo.DrugContraindicationInner
import me.danwi.sqlex.core.query.*
import me.danwi.sqlex.core.query.expression.Expression
import me.danwi.sqlex.core.query.expression.ParameterExpression
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ContraindicationQuery(
    val drugTable: MedicationGuideDrugTable,
    val contraindicationTable: MedicationGuideSpecialCrowdContraindicationTable,
    val combinedMedicationContraindicationTable: MgCombinedMedicationContraindicationTable,
    val doseLimitTable: MedicationGuideDoseLimitTable,
    val drugContraindicationInit: DrugContraindicationInit,
    val genericCategoryTable: MedicationGuideCategoryGenericTable,
    val genericIngredientTable: MedicationGuideGenericIngredientTable,
) {
    //性别
    private val patientGender: String
        get() = if (request.patient.gender == Gender.MAN) "M" else "F"

    //年龄
    private val patientAge: Int
        get() = request.patient.age

    //在patient初始化时根据五病标签提取应有诊断
    private var patientDiseaseIds: List<Long> = listOf()

    //在patient初始化时根据提交的药品知识库数据
    private var drugRows: List<MedicationGuideDrug> = listOf()

    //通用名成分
    private var genericIngredientRows: List<MedicationGuideGenericIngredient> = listOf()

    //通用名分类
    private var genericCategoryRows: List<MedicationGuideCategoryGeneric> = listOf()

    //当值被设置时对应用该成员的元素进行变更
    private val requestTreadLocal: ThreadLocal<ContraindicationRequest> = ThreadLocal()
    var request: ContraindicationRequest
        get() = requestTreadLocal.get()
        set(value) {
            requestTreadLocal.set(value)
            //根据五病患者标签初始化时获取一次患者诊断列表
            if (value.patient.copdDiseaseTag == PatientTag.EXISTS ||
                value.patient.hypertensionDiseaseTag == PatientTag.EXISTS ||
                value.patient.diabetesDiseaseTag == PatientTag.EXISTS ||
                value.patient.acuteCoronaryDiseaseTag == PatientTag.EXISTS ||
                value.patient.cerebralStrokeDiseaseTag == PatientTag.EXISTS
            )
                patientDiseaseIds = drugContraindicationInit.diseaseList.filter {
                    (value.patient.copdDiseaseTag == PatientTag.EXISTS && it.knDiseaseName.indexOf("慢阻肺") >= 0)
                            ||
                            (value.patient.hypertensionDiseaseTag == PatientTag.EXISTS && it.knDiseaseName.indexOf("高血压") >= 0)
                            ||
                            (value.patient.diabetesDiseaseTag == PatientTag.EXISTS && it.knDiseaseName.indexOf("糖尿病") >= 0)
                            ||
                            (value.patient.acuteCoronaryDiseaseTag == PatientTag.EXISTS && it.knDiseaseName.indexOf("冠心病") >= 0)
                            ||
                            (value.patient.cerebralStrokeDiseaseTag == PatientTag.EXISTS && it.knDiseaseName.indexOf("脑卒中") >= 0)
                }
                    .map { it.knDiseaseId }
            else
                patientDiseaseIds = listOf(0)
            //初始化数据库用药数据
            drugRows = drugTable.select()
                .where(MedicationGuideDrugTable.KnDrugId `in` request.medicationOrderInner.map { arg(it.drugId) })
                .find()
            //初始化药品通用名成分
            var genericCondition: Expression = if (drugRows.any { it.knGenericId != null })
                MedicationGuideGenericIngredientTable.KnGenericId `in` drugRows.filter { it.knGenericId != null }
                    .map { arg(it.knGenericId) }
            else false.arg
            genericIngredientRows = genericIngredientTable.select().where(genericCondition).find()
            //初始化药品通用名分类
            genericCondition = if (drugRows.any { it.knGenericId != null })
                MedicationGuideCategoryGenericTable.GenericId `in` drugRows.filter { it.knGenericId != null }
                    .map { arg(it.knGenericId) }
            else false.arg
            genericCategoryRows = genericCategoryTable.select().where(genericCondition).find()
        }

    //药品禁忌 老年禁忌 非适应症
    fun getOneDrugContraindicationList(): List<DrugContraindicationInner> {
        //性别, 无限制或者性别符合
        val genderCondition = MedicationGuideSpecialCrowdContraindicationTable.KnGender.isNull or
                (MedicationGuideSpecialCrowdContraindicationTable.KnGender eq "") or
                (MedicationGuideSpecialCrowdContraindicationTable.KnGender eq patientGender)
        //年龄，无限制或者在区间内
        val ageCondition = MedicationGuideSpecialCrowdContraindicationTable.KnDayHigh.isNull or
                (MedicationGuideSpecialCrowdContraindicationTable.KnDayHigh eq 0) or
                (
                        (MedicationGuideSpecialCrowdContraindicationTable.KnDayLow lte (patientAge * 365))
                                and
                                (MedicationGuideSpecialCrowdContraindicationTable.KnDayHigh gte (patientAge * 365))
                        )

        //用药条件，符合通用名规格剂型或药品id
        fun drugGenericCondition(drug: MedicationGuideDrug): Expression {
            if (drug.knGenericId == null)
                return arg(false)
            else
            //返回通用名或药理分类的判断条件
                return ((MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorType eq ContraindicationAnchorType.GENERIC.ordinal.arg)
                        and
                        (MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorId eq drug.knGenericId.arg)
                        and
                        (
                                MedicationGuideSpecialCrowdContraindicationTable.KnDoseageFormId.isNull
                                        or
                                        (MedicationGuideSpecialCrowdContraindicationTable.KnDoseageFormId eq 0)
                                        or
                                        (MedicationGuideSpecialCrowdContraindicationTable.KnDoseageFormId eq drug.knDoseageFormId.arg)
                                )
                        and (
                        MedicationGuideSpecialCrowdContraindicationTable.KnStrength.isNull
                                or
                                (MedicationGuideSpecialCrowdContraindicationTable.KnStrength eq "")
                                or
                                (MedicationGuideSpecialCrowdContraindicationTable.KnStrength eq drug.knStrength.arg)
                        )
                        ) or
                        (
                                (MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorType eq ContraindicationAnchorType.CATEGORY.ordinal.arg)
                                        and
                                        (MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorId `in`
                                                drugCategoryList(1L, drug.knGenericId!!).map { it.categoryId.arg })
                                )
        }

        val drugCondition = drugRows.map { drug ->
            //通用名 规格 剂型
            (
                    //通用名或药理分类
                    drugGenericCondition(drug) or
                            //药品
                            (
                                    (MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorType eq ContraindicationAnchorType.DRUG.ordinal.arg)
                                            and
                                            (MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorId eq drug.knDrugId.arg)
                                    )
                    )
        }.joinByOr()
        //查询药品禁忌 老年用药禁忌
        val specialCrowdCondition = MedicationGuideSpecialCrowdContraindicationTable.KnSpecialCrowd `in`
                listOf("DRUGDISEASE".arg, "GERIATRIC".arg)
        //诊断条件，无限制或诊断符合
        val diseaseCondition = MedicationGuideSpecialCrowdContraindicationTable.KnDiseaseId.isNull or
                (MedicationGuideSpecialCrowdContraindicationTable.KnDiseaseId eq 0) or
                (MedicationGuideSpecialCrowdContraindicationTable.KnDiseaseId `in` patientDiseaseIds.map { it.arg })
        //老年用药禁忌和药品禁忌
        val drugDiseaseContraindication = contraindicationTable.select()
            .where(specialCrowdCondition)
            .where(genderCondition)
            .where(ageCondition)
            .where(drugCondition)
            .where(diseaseCondition)
            .find()
        //通过查询药品录入的适应症，核对患者是否对症用药（存在任一诊断即适症），否则为非适应症用药禁忌
        val indicationRows = contraindicationTable.select()
            .where(MedicationGuideSpecialCrowdContraindicationTable.KnSpecialCrowd.eq(arg("INDICATION")))
            .where(genderCondition)
            .where(ageCondition)
            .where(drugCondition)
            .order(MedicationGuideSpecialCrowdContraindicationTable.KnContraindicationId)
            .find()
        return drugRows.flatMap { drug ->
            //找到药品的药品禁忌数据
            val drugDiseaseRows = drugDiseaseContraindication.filter {
                //如果是药品相关的禁忌,则只需要药品ID对应就OK
                val drugFilterCondition =
                    it.knDrugAnchorType.toInt() == ContraindicationAnchorType.DRUG.ordinal && drug.knDrugId == it.knDrugAnchorId
                //如果是通用名相关的禁忌 除了通用名ID外还需要匹配规格和剂型
                val genericFilterCondition =
                    it.knDrugAnchorType.toInt() == ContraindicationAnchorType.GENERIC.ordinal &&
                            drug.knGenericId == it.knDrugAnchorId &&
                            (it.knDoseageFormId == null || it.knDoseageFormId == 0L || it.knDoseageFormId == drug.knDoseageFormId) &&
                            (it.knStrength == null || it.knStrength == "" || it.knStrength == drug.knStrength)
                //满足任一条件即可
                drugFilterCondition || genericFilterCondition
            }
            //找到药品的适应症数据
            val drugIndicationRows = indicationRows.filter {
                //如果是药品相关的禁忌,则只需要药品ID对应就OK
                val drugFilterCondition =
                    it.knDrugAnchorType.toInt() == ContraindicationAnchorType.DRUG.ordinal && drug.knDrugId == it.knDrugAnchorId
                //如果是通用名相关的禁忌 除了通用名ID外还需要匹配规格和剂型
                val genericFilterCondition =
                    it.knDrugAnchorType.toInt() == ContraindicationAnchorType.GENERIC.ordinal &&
                            drug.knGenericId == it.knDrugAnchorId &&
                            (it.knDoseageFormId == null || it.knDoseageFormId == 0L || it.knDoseageFormId == drug.knDoseageFormId) &&
                            (it.knStrength == null || it.knStrength == "" || it.knStrength == drug.knStrength)
                //满足任一条件即可
                drugFilterCondition || genericFilterCondition
            }
            val drugContraindication = mutableListOf<MedicationGuideSpecialCrowdContraindication>()
            drugContraindication.addAll(drugDiseaseRows)
            //如果该药品不存在任一适应症则应当报非适应症用药
            if (drugIndicationRows.isNotEmpty() && drugIndicationRows.none { row -> patientDiseaseIds.any { id -> id == row.knDiseaseId } })
                drugContraindication.add(drugIndicationRows[0])
            drugContraindication.map {
                //将禁忌转换成前端需要的形式
                DrugContraindicationInner(
                    drug.knDrugId,
                    drug.knDrugName ?: "",
                    it.knContraindicationId,
                    ContraindicationType.valueOf(it.knSpecialCrowd),
                    it.knSeverityLevel ?: 3,
                    it.knLongDesc ?: it.knShortDec ?: "禁用",
                    null
                )
            }
        }
    }

    //成分重复
    fun getSameIngredientContraindicationList(): List<DrugContraindicationInner> {
        //通用名药品数量计数
        val genericDrugCount = drugRows
            .filter { it.knGenericId != null }
            .distinctBy { it.knGenericId }
            .map {
                it.knGenericId!! to drugRows.filter { i -> i.knGenericId == it.knGenericId }.size
            }
        //成分基础表-用于显示名称
        val ingredientRows =
            drugContraindicationInit.ingredientDataList.filter { i -> genericIngredientRows.any { i.knIngredientId == it.knIngredientId } }
        //查询不同通用名间的成分重复或药品计数大于1的通用名成分-从成分维度产生报警
        val ingredientContraindicationRows =
            genericIngredientRows
                .filter { r1 ->
                    //不同通用名之间有相同成分或通用名药品计数大于1
                    genericIngredientRows.any { r2 -> r1.knIngredientId == r2.knIngredientId && r1.knGenericId != r2.knIngredientId }
                            || genericDrugCount.any { it.second > 1 && r1.knGenericId == it.first }
                }
                .map { it.knIngredientId }
                .distinct()
        return ingredientContraindicationRows.flatMap { ingredientId ->
            //查找该成分的通用名
            val genericList = genericIngredientRows.filter { it.knIngredientId == ingredientId }
            //生成通用名的通用名禁忌数组
            val genericResult = mutableMapOf<Long, MutableList<Long>>()
            for (index in genericList.indices) {
                val genericId = genericList[index].knGenericId
                if (index != genericList.size - 1)
                    genericResult[genericId] =
                        genericList.subList(index + 1, genericList.size).map { it.knGenericId ?: 0L }
                            .toMutableList()
                //如果通用名药品计数大于1，此时应报通用名本组药品的禁忌
                if ((genericDrugCount.find { it.first == genericId }?.second ?: 1) > 1)
                    genericResult[genericId]?.add(genericId)

            }
            genericResult.flatMap { row ->
                //查找通用名所有药品
                val drugList = drugRows.filter { it.knGenericId == row.key }
                //如果通用名自重复，通用名药品正向生成1次禁忌 例如1,2,3,4生成1,2; 1,3; 1,4; 2,3; 2,4; 3,4;
                if (row.value.any { it == row.key }) {
                    val drugResult = mutableMapOf<Long, MutableList<Long>>()
                    //查找通用名的所有药品11报警
                    for (index in 0 until drugList.size - 1) {
                        //自己报警 自己与同通用名右侧药品所有 最后一个没有右侧
                        drugResult[drugList[index].knDrugId] =
                            drugList.subList(index + 1, drugList.size).map { it.knDrugId }.toMutableList()
                    }
                    drugResult.flatMap { dr ->
                        val drugRow = drugRows.first { it.knDrugId == dr.key }
                        dr.value.map { conflictDrug ->
                            DrugContraindicationInner(
                                drugRow.knDrugId,
                                drugRow.knDrugName ?: "",
                                0L,
                                ContraindicationType.INGREDIENT,
                                3,
                                ingredientRows.first { it.knIngredientId == ingredientId }.knIngredientName.let { "含有相同成分:${it}" },
                                conflictDrug
                            )
                        }
                    }
                } else {
                    //通用名生成不同通用名之间的成分重复
                    drugList.flatMap { drug ->
                        row.value.filter { it != row.key }.flatMap { conflictGenericId ->
                            drugRows.filter { it.knGenericId == conflictGenericId }.map { conflictDrug ->
                                DrugContraindicationInner(
                                    drug.knDrugId,
                                    drug.knDrugName ?: "",
                                    0L,
                                    ContraindicationType.INGREDIENT,
                                    3,
                                    ingredientRows.first { it.knIngredientId == ingredientId }.knIngredientName.let { "含有相同成分:${it}" },
                                    conflictDrug.knDrugId
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //药品间禁忌
    fun getCombinedMedicationContraindicationList(): List<DrugContraindicationInner> {
        //当药品数量小于2时，直接返回空数组
        if (drugRows.count() < 2) return listOf()
        //性别, 无限制或者性别符合
        val genderCondition = MgCombinedMedicationContraindicationTable.Gender.isNull or
                (MgCombinedMedicationContraindicationTable.Gender eq "") or
                (MgCombinedMedicationContraindicationTable.Gender eq patientGender)
        //年龄，无限制或者在区间内
        val ageCondition = MgCombinedMedicationContraindicationTable.DayHigh.isNull or
                (MgCombinedMedicationContraindicationTable.DayHigh eq 0) or
                (
                        (MgCombinedMedicationContraindicationTable.DayLow lte (patientAge * 365))
                                and
                                (MgCombinedMedicationContraindicationTable.DayHigh gte (patientAge * 365))
                        )
        //诊断条件，无限制或诊断符合
        val diseaseCondition = MgCombinedMedicationContraindicationTable.DiseaseId.isNull or
                (MgCombinedMedicationContraindicationTable.DiseaseId eq 0) or
                (MgCombinedMedicationContraindicationTable.DiseaseId `in` patientDiseaseIds.map { it.arg })
        //生成两两用药禁忌查询数组
        val drugList = mutableMapOf<Long, MutableList<Long>>()
        for (index in 0 until drugRows.size - 1) {
            drugList[drugRows[index].knDrugId] =
                drugRows.subList(index + 1, drugRows.size).map { it.knDrugId }.toMutableList()
        }
        //正向查询两两用药禁忌
        val forwardDrugCondition = drugList.map { row ->
            val drug = drugRows.first { it.knDrugId == row.key }
            row.value.map { conflictDrugId ->
                val conflictDrug = drugRows.first { it.knDrugId == conflictDrugId }
                val conflictDrugCategory =
                    conflictDrug.knGenericId?.let { drugCategoryList(0L, it) } ?: listOf()
                //拼接一组正向查询
                (
                        //通用名 规格 剂型
                        (MgCombinedMedicationContraindicationTable.DrugAnchorType eq ContraindicationAnchorType.GENERIC.ordinal.arg)
                                and
                                (MgCombinedMedicationContraindicationTable.DrugAnchorId eq drug.knGenericId.arg)
                                and
                                (
                                        MgCombinedMedicationContraindicationTable.DoseageFormId.isNull
                                                or
                                                (MgCombinedMedicationContraindicationTable.DoseageFormId eq 0)
                                                or
                                                (MgCombinedMedicationContraindicationTable.DoseageFormId eq drug.knDoseageFormId.arg)
                                        )
                                and (
                                MgCombinedMedicationContraindicationTable.Strength.isNull
                                        or
                                        (MgCombinedMedicationContraindicationTable.Strength eq "")
                                        or
                                        (MgCombinedMedicationContraindicationTable.Strength eq drug.knStrength.arg))
                        ) and (
                        // 1 4 5
                        (
                                //冲突通用名-1
                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.GENERIC.ordinal.arg)
                                        and
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId eq conflictDrug.knGenericId.arg)
                                ) or
                                //冲突报警药品分类-4
                                (
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.CONFLICT_DRUG_CATEGORY.ordinal.arg)
                                                and
                                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId.`in`(
                                                    conflictDrugCategory.map { arg(it.categoryId) }
                                                ))
                                                and (conflictDrugCategory.any { i -> i.doseFormId == conflictDrug.knDoseageFormId }).arg
                                        ) or
                                //冲突成分-5
                                (
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.INGREDIENT_CATEGORY.ordinal.arg)
                                                and
                                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId.`in`(
                                                    conflictDrug.knGenericId?.let { drugIngredients(it) }
                                                )))
                        )
            }.joinByOr() ?: arg(false)
        }.joinByOr()
        //反向查询两两用药禁忌
        val reverseDrugCondition = drugList.map { row ->
            val drug = drugRows.first { it.knDrugId == row.key }
            val drugCategory = drug.knGenericId?.let { drugCategoryList(0L, it) } ?: listOf()
            row.value.map { conflictDrugId ->
                val conflictDrug = drugRows.first { it.knDrugId == conflictDrugId }
                //拼接一组正向查询
                (
                        //通用名 规格 剂型-冲突
                        (MgCombinedMedicationContraindicationTable.DrugAnchorType eq ContraindicationAnchorType.GENERIC.ordinal.arg)
                                and
                                (MgCombinedMedicationContraindicationTable.DrugAnchorId eq conflictDrug.knGenericId.arg)
                                and
                                (
                                        MgCombinedMedicationContraindicationTable.DoseageFormId.isNull
                                                or
                                                (MgCombinedMedicationContraindicationTable.DoseageFormId eq 0)
                                                or
                                                (MgCombinedMedicationContraindicationTable.DoseageFormId eq conflictDrug.knDoseageFormId.arg)
                                        )
                                and (
                                MgCombinedMedicationContraindicationTable.Strength.isNull
                                        or
                                        (MgCombinedMedicationContraindicationTable.Strength eq "")
                                        or
                                        (MgCombinedMedicationContraindicationTable.Strength eq conflictDrug.knStrength.arg))
                        ) and (
                        //通用名-药品
                        (
                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.GENERIC.ordinal.arg)
                                        and
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId eq drug.knGenericId.arg)
                                ) or
                                //冲突报警药品分类-4
                                (
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.CONFLICT_DRUG_CATEGORY.ordinal.arg)
                                                and
                                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId.`in`(
                                                    drugCategory.map { arg(it.categoryId) }
                                                ))
                                                and (drugCategory.any { i -> i.doseFormId == drug.knDoseageFormId }).arg
                                        ) or
                                //冲突成分-5
                                (
                                        (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorType eq ContraindicationAnchorType.INGREDIENT_CATEGORY.ordinal.arg)
                                                and
                                                (MgCombinedMedicationContraindicationTable.ConflictDrugAnchorId.`in`(
                                                    drug.knGenericId?.let { drugIngredients(it) }
                                                )))
                        )
            }.joinByOr() ?: arg(false)
        }.joinByOr()
        val resultList = mutableMapOf<Int, List<DrugContraindicationInner>>()
        var index = 0
        listOf(forwardDrugCondition, reverseDrugCondition)
            .forEach { drugCondition ->
                val result = mutableListOf<DrugContraindicationInner>()
                combinedMedicationContraindicationTable.select()
                    .where(genderCondition)
                    .where(ageCondition)
                    .where(diseaseCondition)
                    .where(drugCondition)
                    .find()
                    .forEach { row ->
                        if (row.drugAnchorType?.toInt() == ContraindicationAnchorType.DRUG.ordinal) {
                            val drug = drugRows.find { d -> d.knDrugId == row.drugAnchorId }
                            //查找冲突药品
                            drugRows
                                .filter {
                                    if (row.conflictDrugAnchorType?.toInt() == ContraindicationAnchorType.DRUG.ordinal)
                                        it.knDrugId == row.conflictDrugAnchorId
                                    else if (row.conflictDrugAnchorType?.toInt() == ContraindicationAnchorType.GENERIC.ordinal)
                                        it.knGenericId == row.conflictDrugAnchorId
                                    else false
                                }
                                .forEach { conflictDrug ->
                                    val contraindication = DrugContraindicationInner(
                                        drug?.knDrugId ?: 0L,
                                        drug?.knDrugName ?: "",
                                        row.contraindicationId,
                                        ContraindicationType.valueOf(row.contraindicationType),
                                        row.severityLevel ?: 3,
                                        row.longDesc ?: row.shortDec ?: "禁用",
                                        conflictDrug.knDrugId
                                    )
                                    //当为正向查询或反向查询时无正向结果时 保存到数据集中
                                    if (index == 0
                                        || (index == 1
                                                && resultList[0]?.any { it.drugId == contraindication.drugId && it.conflictDrugId == contraindication.conflictDrugId } == false)
                                    ) {
                                        result.add(contraindication)
                                    }
                                }
                        } else if (row.drugAnchorType?.toInt() == ContraindicationAnchorType.GENERIC.ordinal) {
                            drugRows
                                .filter { d -> d.knGenericId == row.drugAnchorId && (row.doseageFormId == null || row.doseageFormId == 0L || row.doseageFormId == d.knDoseageFormId) && (row.strength == null || row.strength == "" || row.strength == d.knStrength) }
                                .forEach { drug ->
                                    //查找冲突药品
                                    drugRows
                                        .filter {
                                            if (row.conflictDrugAnchorType?.toInt() == ContraindicationAnchorType.DRUG.ordinal)
                                                it.knDrugId == row.conflictDrugAnchorId
                                            else if (row.conflictDrugAnchorType?.toInt() == ContraindicationAnchorType.GENERIC.ordinal)
                                                it.knGenericId == row.conflictDrugAnchorId
                                            else false
                                        }
                                        .forEach { conflictDrug ->
                                            val contraindication = DrugContraindicationInner(
                                                drug.knDrugId,
                                                drug.knDrugName ?: "",
                                                row.contraindicationId,
                                                ContraindicationType.valueOf(row.contraindicationType),
                                                row.severityLevel ?: 3,
                                                row.longDesc ?: row.shortDec ?: "禁用",
                                                conflictDrug.knDrugId
                                            )
                                            //当为正向查询或反向查询时无正向结果时 保存到数据集中
                                            if (index == 0
                                                || (index == 1
                                                        && resultList[0]?.any { it.drugId == contraindication.drugId && it.conflictDrugId == contraindication.conflictDrugId } == false)
                                            ) {
                                                result.add(contraindication)
                                            }
                                        }
                                }
                        }
                    }
                resultList[index] = result
                index++
            }
        return CollectionUtil.union(resultList[0], resultList[1]).toList()
    }

    //剂量报警
    fun getDoseLimitContraindicationList(): List<DrugContraindicationInner> {
        //性别, 无限制或者性别符合
        val genderCondition = MedicationGuideDoseLimitTable.KnGender.isNull or
                (MedicationGuideDoseLimitTable.KnGender eq "") or
                (MedicationGuideDoseLimitTable.KnGender eq patientGender)
        //年龄，无限制或者在区间内
        val ageCondition = MedicationGuideDoseLimitTable.KnDayHigh.isNull or
                (MedicationGuideDoseLimitTable.KnDayHigh eq 0) or
                (
                        (MedicationGuideDoseLimitTable.KnDayLow lte (patientAge * 365))
                                and
                                (MedicationGuideDoseLimitTable.KnDayHigh gte (patientAge * 365))
                        )
        //诊断条件，无限制或诊断符合
        val diseaseCondition = MedicationGuideDoseLimitTable.KnDiseaseId.isNull or
                (MedicationGuideDoseLimitTable.KnDiseaseId eq 0) or
                (MedicationGuideDoseLimitTable.KnDiseaseId `in` patientDiseaseIds.map { it.arg })
        //用药条件
        val drugCondition =
            (MedicationGuideDoseLimitTable.KnDrugId `in` drugRows.map { arg(it.knDrugId) }).or(
                drugRows
                    .filter { it.knGenericId != null }
                    .map {
                        MedicationGuideDoseLimitTable.KnGenericId eq arg(it.knGenericId) and
                                (MedicationGuideDoseLimitTable.KnDoseageFormId.isNull or
                                        (MedicationGuideDoseLimitTable.KnDoseageFormId eq arg(0)) or
                                        (MedicationGuideDoseLimitTable.KnDoseageFormId eq arg(it.knDoseageFormId))
                                        ) and
                                (MedicationGuideDoseLimitTable.KnStrength.isNull or
                                        (MedicationGuideDoseLimitTable.KnStrength eq arg("")) or
                                        (MedicationGuideDoseLimitTable.KnStrength eq arg(it.knStrength))
                                        )
                    }
                    .joinByOr() ?: arg(false)
            ) ?: false.arg

        //查询剂量用药规范数据
        val doseLimitRows = doseLimitTable.select()
            .where(genderCondition)
            .where(ageCondition)
            .where(diseaseCondition)
            .where(drugCondition)
            .find()

        return drugRows
            .flatMap { drug ->
                val drugContraindicationRows =
                    doseLimitRows.filter {
                        it.knDrugId == drug.knDrugId ||
                                (it.knGenericId == drug.knGenericId &&
                                        (it.knDoseageFormId == null ||
                                                it.knDoseageFormId == 0L ||
                                                it.knDoseageFormId == drug.knDoseageFormId) &&
                                        (it.knStrength == null || it.knStrength == "" || it.knStrength == drug.knStrength))
                    }
                //按优先顺序取单一药品禁忌，药品优先级大于通用名
                var rows = drugContraindicationRows.filter { it.knDrugId != null }
                if (rows.isEmpty())
                    rows = drugContraindicationRows
                //获取药品的使用剂量（列表药品一定是不重复的）
                val drugDose = request.medicationOrderInner.first { i -> i.drugId == drug.knDrugId }
                //判断药品是否超规范使用
                rows.filter {
                    //药品维护了剂量报警但后期重置了药品剂量单位，这时报警信息不生效
                    if (drug.doseLimitUnit.isNullOrEmpty() || drug.doseLimitUnit != it.knDoseLimitUnit) return@filter false
                    val singleDose = drugDose.singleDose
                    val dayDose = drugDose.singleDose * drugDose.frequency.toBigDecimal()
                    val frequencyDay = 1
                    val frequencyTimes = drugDose.frequency
                    //剂量过高 单日或单次或极量
                    val highResult =
                        (it.knDoseHighEach != null && it.knDoseHighEach!! > BigDecimal.ZERO && singleDose > it.knDoseHighEach) ||
                                (it.knDoseHighPerDay != null && it.knDoseHighPerDay!! > BigDecimal.ZERO && dayDose > it.knDoseHighPerDay) ||
                                (it.knDoseMaxPerDay != null && it.knDoseMaxPerDay!! > BigDecimal.ZERO && dayDose > it.knDoseMaxPerDay) ||
                                (it.knDoseMaxEach != null && it.knDoseMaxEach!! > BigDecimal.ZERO && dayDose > it.knDoseMaxEach) ||
                                (it.knDoseMaxLife != null && it.knDoseMaxLife!! > BigDecimal.ZERO && dayDose > it.knDoseMaxLife)
                    //剂量过低 单日或单次
                    val lowResult =
                        (it.knDoseLowEach != null && it.knDoseLowEach!! > BigDecimal.ZERO && singleDose < it.knDoseLowEach) ||
                                (it.knDoseLowPerDay != null && it.knDoseLowPerDay!! > BigDecimal.ZERO && dayDose < it.knDoseLowPerDay)
                    //给药频次过高 f1/d1（知识库维护）<f2/d2（实际用药）即f1d2<f2d1
                    val frequencyDaysHigh = it.knFrequencyDaysHigh
                    val frequencyHighResult =
                        if (frequencyDaysHigh != null && it.knFrequencyTimesHigh != null)
                            (frequencyTimes * frequencyDaysHigh).toBigDecimal() > frequencyDay.toBigDecimal() * it.knFrequencyTimesHigh!!
                        else false
                    //给药频次过低
                    val frequencyDaysLow = it.knFrequencyDaysLow
                    val frequencyLowResult = if (frequencyDaysLow != null && it.knFrequencyTimesLow != null)
                        (frequencyTimes * frequencyDaysLow).toBigDecimal() < frequencyDay.toBigDecimal() * it.knFrequencyTimesLow!!
                    else false
                    //存在剂量过高/过低、频次过高/过低任一情况都应产生禁忌
                    highResult || lowResult || frequencyHighResult || frequencyLowResult
                }
                    .map { row ->
                        //将禁忌转换成前端需要的形式
                        DrugContraindicationInner(
                            drug.knDrugId,
                            drug.knDrugName ?: "",
                            row.knDoseLimitId.toLong(),
                            ContraindicationType.DOSELIMIT,
                            row.knSeverityLevel ?: 3,
                            row.knLongDesc ?: row.knShortDec ?: "此剂量存在问题，详情请查看说明书",
                            null
                        )
                    }
            }
    }

    /**
     * 根据通用名id获取药品分类及上级树
     * @param type 分类类型 0报警药品分类 1药理分类
     * @param genericId 通用名id
     */
    private fun drugCategoryList(
        type: Long,
        genericId: Long,
    ): List<MedicationGuideCategory> {
        if (!drugContraindicationInit.categoryTreeData.containsKey(type)) return listOf()
        val tree = drugContraindicationInit.categoryTreeData[type]

        return genericCategoryRows.filter { it.categoryTypeId == type && it.genericId == genericId }.flatMap {
            tree?.findChildrenTreeNode(it.categoryId)
                ?.findCurrentAndParentNodeList()
                ?.filter { id -> drugContraindicationInit.categoryDataList.any { i -> i.categoryTypeId == type && i.categoryId == id } }
                ?.map { id ->
                    drugContraindicationInit.categoryDataList.first { i -> i.categoryTypeId == type && i.categoryId == id }
                }
                ?: listOf()
        }
    }

    /**
     * 根据通用名id获取成分分类及上级树
     * @param genericId 通用名id
     */
    private fun drugIngredients(genericId: Long): List<ParameterExpression> {
        return genericIngredientRows.filter { it.knGenericId == genericId }.flatMap {
            drugContraindicationInit.ingredientTreeData.findChildrenTreeNode(it.knIngredientId)
                ?.findCurrentAndParentNodeList()
                ?.map { id -> arg(id) }
                ?: listOf()
        }
    }
}
