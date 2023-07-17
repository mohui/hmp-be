package com.bjknrt.article.service.service.impl

import com.bjknrt.article.service.AsRecommendRecordInfo
import com.bjknrt.article.service.AsRecommendRecordInfoTable
import com.bjknrt.article.service.RecommendDAO
import com.bjknrt.article.service.service.RecommendRecordService
import com.bjknrt.article.service.vo.ArticleTag
import com.bjknrt.article.service.vo.CardRecommendResponse
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import me.danwi.sqlex.core.type.PagedResult
import org.springframework.stereotype.Service

/**
 * @author wjy
 */
@Service
class RecommendRecordServiceImpl(
    val recommendDAO: RecommendDAO,
    val asRecommendRecordInfoTable: AsRecommendRecordInfoTable,
    val patientClient: PatientApi
) : RecommendRecordService {

    override fun cardRecommend(
        readerId: Id,
        cardId: Id,
        pageNo: Long,
        pageSize: Long
    ): PagedResult<RecommendDAO.CardRecommendResult> {
        // 1.查询推荐数据
        val pagedResult = recommendDAO.cardRecommend(readerId, cardId, pageSize, pageNo)
        // 2.保存推荐关系
        pagedResult.data.forEach {
            asRecommendRecordInfoTable.insertWithoutNull(
                AsRecommendRecordInfo.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnReaderId(readerId)
                    .setKnArticleId(it.knId)
                    .build()
            )
        }
        return pagedResult
    }

    override fun articleRecommend(
        readerId: Id,
        pageNo: Long,
        pageSize: Long
    ): PagedResult<RecommendDAO.ArticleRecommendResult> {
        // 0.查询个人标签
        var patientInfo: PatientInfoResponse? = null
        try {
            patientInfo = patientClient.getPatientInfo(readerId)
        } catch (e: Exception) {
            LOGGER.warn("文章推荐,未查询到患者信息,阅读人id:{}", readerId)
        }

        // 1.查询推荐数据
        val isHypertension: Boolean = patientInfo?.let { it.hypertensionDiseaseTag == PatientTag.EXISTS } ?: true
        val isDiabetes: Boolean = patientInfo?.let { it.diabetesDiseaseTag == PatientTag.EXISTS } ?: true
        val isAcuteCoronary: Boolean = patientInfo?.let { it.acuteCoronaryDiseaseTag == PatientTag.EXISTS } ?: true
        val isCopd: Boolean = patientInfo?.let { it.copdDiseaseTag == PatientTag.EXISTS } ?: true
        val isCerebralStroke: Boolean = patientInfo?.let { it.cerebralStrokeDiseaseTag == PatientTag.EXISTS } ?: true

        val pagedResult = recommendDAO.articleRecommend(
            readerId,
            isHypertension,
            isDiabetes,
            isAcuteCoronary,
            isCerebralStroke,
            isCopd,
            pageSize,
            pageNo
        )
        // 2.保存推荐关系
        pagedResult.data.forEach {
            asRecommendRecordInfoTable.insertWithoutNull(
                AsRecommendRecordInfo.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnReaderId(readerId)
                    .setKnArticleId(it.knId)
                    .build()
            )
        }
        return pagedResult
    }

    override fun cardRecommendList(
        readerId: Id,
        pageNo: Long,
        pageSize: Long
    ): PagedResult<CardRecommendResponse> {
        // 0.查询个人标签
        var patientInfo: PatientInfoResponse? = null
        try {
            patientInfo = patientClient.getPatientInfo(readerId)
        } catch (e: Exception) {
            LOGGER.warn("卡片推荐,未查询到患者信息,阅读人id:{}", readerId)
        }
        val isHypertension = patientInfo?.let { it.hypertensionDiseaseTag == PatientTag.EXISTS } ?: true
        val isDiabetes = patientInfo?.let { it.diabetesDiseaseTag == PatientTag.EXISTS } ?: true
        val isAcuteCoronary = patientInfo?.let { it.acuteCoronaryDiseaseTag == PatientTag.EXISTS } ?: true
        val isCopd = patientInfo?.let { it.copdDiseaseTag == PatientTag.EXISTS } ?: true
        val isCerebralStroke = patientInfo?.let { it.cerebralStrokeDiseaseTag == PatientTag.EXISTS } ?: true

        val pagedResult = recommendDAO.cardRecommendList(
            readerId,
            isHypertension,
            isDiabetes,
            isAcuteCoronary,
            isCerebralStroke,
            isCopd,
            pageSize,
            pageNo
        )
        // 2.保存推荐关系
        pagedResult.data.forEach {
            asRecommendRecordInfoTable.insertWithoutNull(
                AsRecommendRecordInfo.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnReaderId(readerId)
                    .setKnArticleId(it.knId)
                    .build()
            )
        }

        val cardRecommendResponseList = pagedResult.data.map {
            val articleTags = mutableListOf<ArticleTag>()
            if (it.hypertension == 1L) articleTags.add(ArticleTag.HYPERTENSION)
            if (it.diabetes == 1L) articleTags.add(ArticleTag.DIABETES)
            if (it.acutecoronary == 1L) articleTags.add(ArticleTag.ACUTE_CORONARY)
            if (it.cerebralstroke == 1L) articleTags.add(ArticleTag.CEREBRAL_STROKE)
            if (it.copd == 1L) articleTags.add(ArticleTag.COPD)
            CardRecommendResponse(
                id = it.knId,
                title = it.knCardTitle,
                isCard = true,
                tags = articleTags
            )
        }

        return PagedResult(
            pagedResult.pageSize,
            pagedResult.pageNo,
            pagedResult.total,
            cardRecommendResponseList
        )
    }
}