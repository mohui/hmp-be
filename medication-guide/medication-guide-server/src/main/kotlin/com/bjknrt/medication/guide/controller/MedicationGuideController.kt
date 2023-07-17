package com.bjknrt.medication.guide.controller

import ContraindicationAnchorType
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.medication.guide.*
import com.bjknrt.medication.guide.api.MedicationGuideApi
import com.bjknrt.medication.guide.contraindication.ContraindicationQuery
import com.bjknrt.medication.guide.contraindication.ContraindicationRequest
import com.bjknrt.medication.guide.vo.*
import me.danwi.sqlex.core.query.*
import me.danwi.sqlex.core.query.expression.Expression
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@RestController("com.bjknrt.medication.guide.api.MedicationGuideController")
class MedicationGuideController(
    val drugMonographTable: MedicationGuideDrugMonographTable,
    val genericTable: MedicationGuideGenericTable,
    val drugTable: MedicationGuideDrugTable,
    val contraindicationTable: MedicationGuideSpecialCrowdContraindicationTable,
    val doseageFormTable: MedicationGuideDoseageFormTable,
    val monographImageTable: MedicationGuideMonographImageTable,
    val monographDao: DrugMonographDao,
    val patientClient: PatientApi,
    val contraindicationQuery: ContraindicationQuery,
) : AppBaseController(), MedicationGuideApi {
    override fun drugList(_param: Param): PagedResult<DrugList200ResponseInner> {
        val keys = _param.keywords.trimEnd().trimStart().split(" ")
        val doseageFormIds = doseageFormTable.select()
            .where(keys.map { MedicationGuideDoseageFormTable.KnDoseageFormName like it }.joinByOr())
            .find().map { i -> arg((i.knDoseageFormId)) }
        val keyList = keys.map {
            (MedicationGuideDrugTable.KnDrugName like ("%$it%".arg)) or
                    (MedicationGuideDrugTable.KnPinyin like ("%${it.uppercase()}%".arg)) or
                    (MedicationGuideDrugTable.KnStrength like ("%$it%".arg)) or
                    (MedicationGuideDrugTable.ManufactureName like ("%$it%".arg)) or
                    (MedicationGuideDrugTable.KnDoseageFormId `in` doseageFormIds)
        }
        val dbResult = drugTable.select()
            .where(keyList.joinByAnd())
            .order(MedicationGuideDrugTable.KnDrugId, Order.Desc)
            .page(_param.pageSize.toLong(), _param.pageNo.toLong())
        val doseageFormRows = doseageFormTable.select()
            .where(MedicationGuideDoseageFormTable.KnDoseageFormId `in` dbResult.data.map { arg(it.knDoseageFormId) })
            .find()
        return PagedResult.fromDbPaged(dbResult) {
            DrugList200ResponseInner(
                it.knDrugId,
                it.knDrugName ?: "",
                it.knStrength ?: "",
                doseageFormRows.findLast { i -> i.knDoseageFormId == it.knDoseageFormId }?.knDoseageFormName ?: "",
                it.manufactureName ?: "",
                it.doseLimitUnit
            )
        }
    }

    override fun genericList(body: String): List<GenericList200ResponseInner> {
        val result = genericTable.select()
            .where(
                MedicationGuideGenericTable.KnGenericName like arg("%${body}%")
                        and MedicationGuideGenericTable.KnGenericName.isNotNull
            )
            .order(MedicationGuideGenericTable.KnGenericName, Order.Desc)
            .find()


        return result.map {
            GenericList200ResponseInner(
                it.knGenericId,
                it.knGenericName ?: ""
            )
        }
    }

    override fun monographList(body: String): List<Inner> {
        val result = drugMonographTable.select()
            .where(
                (MedicationGuideDrugMonographTable.KnName like arg("%${body}%"))
                        or (MedicationGuideDrugMonographTable.KnPinyin like arg("%${body}%"))
                        or (MedicationGuideDrugMonographTable.KnSearchPinyin like arg("%${body}%"))
            )
            .order(MedicationGuideDrugMonographTable.KnPinyin, Order.Desc)
            .order(MedicationGuideDrugMonographTable.KnMonographName, Order.Desc)
            .order(MedicationGuideDrugMonographTable.KnMonographId, Order.Desc)
            .find()


        return result.map {
            Inner(
                it.knMonographId,
                it.knName + " " + it.knStrength,
                it.knMonographName ?: "",
                AppSpringUtil.getMessage(
                    "medication.guide.contraindication.monograph.url",
                    it.knMonographId.toString()
                ),
                it.knPinyin
            )
        }
    }

    override fun submitDrug(submitDrugRequest: SubmitDrugRequest): List<DrugContraindicationInner> {
        val patient = patientClient.getPatientInfo(submitDrugRequest.patientId)
        contraindicationQuery.request = ContraindicationRequest(patient, submitDrugRequest.medicationOrder)
        //单一药品禁忌 - 老年用药 药品间禁忌 非适应症
        val result1 = contraindicationQuery.getOneDrugContraindicationList()
        //成分重复
        val result2 = contraindicationQuery.getSameIngredientContraindicationList()
        //剂量
        val result3 = contraindicationQuery.getDoseLimitContraindicationList()
        //药品间禁忌
        val result4 = contraindicationQuery.getCombinedMedicationContraindicationList()
        return result1 + result2 + result3 + result4
    }

    override fun submitGeneric(submitGenericRequest: SubmitGenericRequest): List<SubmitGeneric200ResponseInner> {
        val genericFilter = mutableListOf<Expression>()
        if (submitGenericRequest.generic.any { it.id == null && it.name != "" }) {
            genericFilter.addAll(submitGenericRequest.generic.filter { it.id == null && it.name != "" }
                .map { (MedicationGuideGenericTable.KnGenericName like "%${it.name}%".arg) as Expression })
        }
        if (submitGenericRequest.generic.any { it.id != null }) {
            genericFilter.add(MedicationGuideGenericTable.KnGenericId `in` submitGenericRequest.generic.map { it.id.arg })
        }

        val genericRows =
            if (genericFilter.isEmpty()) emptyList()
            else genericTable.select().where(genericFilter.joinByOr()).find()

        val contraindicationFilters =
            genericRows.map { MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorId eq it.knGenericId.arg }

        val contraindicationRows = contraindicationTable.select()
            .where(
                (MedicationGuideSpecialCrowdContraindicationTable.KnSpecialCrowd
                        `in` (submitGenericRequest.crowd ?: listOf()).map { cd -> cd.name.arg })
                        and (
                        MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorType
                                eq arg(ContraindicationAnchorType.GENERIC.ordinal)
                        )
                        and (contraindicationFilters.joinByOr() ?: return emptyList())
            )
            .order(MedicationGuideSpecialCrowdContraindicationTable.KnWarningScope, Order.Desc)
            .order(MedicationGuideSpecialCrowdContraindicationTable.KnDrugAnchorId, Order.Desc)
            .order(MedicationGuideSpecialCrowdContraindicationTable.KnDoseageFormId, Order.Desc)
            .order(MedicationGuideSpecialCrowdContraindicationTable.KnStrength, Order.Desc)
            .find()

        val doseageFormRows = doseageFormTable.select()
            .where(MedicationGuideDoseageFormTable.KnDoseageFormId `in` contraindicationRows.map { it.knDoseageFormId.arg }
                .distinct())
            .find()

        val result = mutableListOf<SubmitGeneric200ResponseInner>()

        // 禁忌分组
        val map = mutableMapOf<Long, MutableList<MedicationGuideSpecialCrowdContraindication>>()
        contraindicationRows.forEach {
            if (map.containsKey(it.knDrugAnchorId)) {
                map[it.knDrugAnchorId]?.add(it)
            } else {
                map[it.knDrugAnchorId] = mutableListOf(it)
            }
        }

        for (generic in genericRows) {
            map[generic.knGenericId]?.let {
                it.forEach { contraindication ->
                    result.add(
                        SubmitGeneric200ResponseInner(
                            generic.knGenericName ?: "",
                            contraindication.knSeverityLevel ?: 3,
                            contraindication.knLongDesc ?: contraindication.knShortDec ?: "禁用",
                            contraindication.knContraindicationId,
                            Crowd.valueOf(contraindication.knSpecialCrowd),
                            generic.knGenericId,
                            if (contraindication.knDayHigh == null || contraindication.knDayHigh == 0) null else contraindication.knDayLow,
                            if (contraindication.knDayHigh == null || contraindication.knDayHigh == 0) null else contraindication.knDayHigh,
                            if (contraindication.knWeigthHigh == null || BigDecimal.ZERO.compareTo(contraindication.knWeigthHigh) == 0) null else contraindication.knWeightLow,
                            if (contraindication.knWeigthHigh == null || BigDecimal.ZERO.compareTo(contraindication.knWeigthHigh) == 0) null else contraindication.knWeigthHigh,
                            if (contraindication.knDoseageFormId == null || contraindication.knDoseageFormId == 0L) ""
                            else doseageFormRows.filter { df -> df.knDoseageFormId == contraindication.knDoseageFormId }
                                .getOrNull(0)?.knDoseageFormName,
                            if (contraindication.knStrength == null || contraindication.knStrength == "") ""
                            else contraindication.knStrength
                        ))
                }
            }
        }
        return result.toList()
    }

    override fun monograph(body: Long): String {
        val monographRows = monographDao.select(body) ?: return ""
        val monographImages = monographImageTable.select().where(
            MedicationGuideMonographImageTable.KnMonographId eq body.arg
        ).find()
        val data = StringBuilder()
        val regex = "src='(.*)'|\"(.*)\""
        val pattern: Pattern = Pattern.compile(regex, Pattern.MULTILINE)

        monographRows.map {
            val matcher: Matcher = pattern.matcher(it.knSectionContent)
            while (matcher.find()) {
                for (i in 1..matcher.groupCount()) {
                    val imageName = matcher.group(i)
                    val imageContent =
                        if (imageName.isNullOrEmpty().not())
                            Base64.getEncoder().encodeToString(
                                monographImages.filter { ig -> ig.knMonographImageName == imageName }
                                    .getOrNull(0)?.knMonographImage ?: byteArrayOf()
                            )
                        else ""
                    if (imageContent != "")
                        it.knSectionContent =
                            it.knSectionContent.replace(imageName, "data:image/png;base64,${imageContent}")
                }
            }
            data.append(
                """
            <div style="margin-top:16px;margin-right:0;margin-bottom:16px;margin-left:0;text-align:left;font-family:宋体;color: black">
                <strong>
                    【${it.knMonographSectionName}】
                </strong>
            </div>
            <div style ="line-height: 20px">
                ${it.knSectionContent}
            </div>
"""
            )
        }
        return """
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1, width=device-width, maximum-scale=1, user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <style>
        .nocopy {
            -webkit-touch-callout: none;
            -webkit-user-select: none;
            -khtml-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        body {
            font: 15px/2em "微软雅黑";
        }

        h2 {
            font-size: 20px;
        }

        p {
            line-height: 20px;
        }
    </style>
</head>
<body>
<div class="container">
        <style>
                img {
                    width: 100%;
                }
        </style>
        $data
        <br>
</div>
</body>
</html>
        """
    }
}