package com.bjknrt.article.service.service.impl

import com.bjknrt.article.service.AsReadRecordInfo
import com.bjknrt.article.service.AsReadRecordInfoTable
import com.bjknrt.article.service.service.ReadRecordService
import com.bjknrt.article.service.vo.LastReadTimeParam
import com.bjknrt.article.service.vo.LastReadTimeResult
import com.bjknrt.article.service.vo.SaveReadRecordParam
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.client.AppSecurityUtil
import me.danwi.sqlex.core.query.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import me.danwi.sqlex.core.query.eq

@Service
class ReadRecordServiceImpl(
    val asReadRecordInfoTable: AsReadRecordInfoTable
) : ReadRecordService {

    @Transactional
    override fun saveReadRecord(saveReadRecordParam: SaveReadRecordParam) {
        val recordInfo = AsReadRecordInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnReaderId(saveReadRecordParam.readerId)
            .setKnArticleId(saveReadRecordParam.articleId)
            .setKnCreatedBy(AppSecurityUtil.currentUserId())
            .build()
        asReadRecordInfoTable.insertWithoutNull(recordInfo)
    }

    override fun lastReadTime(lastReadTimeParam: LastReadTimeParam): LastReadTimeResult {
        val asReadRecordInfoModel = asReadRecordInfoTable
            .select()
            .where(AsReadRecordInfoTable.KnReaderId eq lastReadTimeParam.readerId)
            .order(AsReadRecordInfoTable.KnId, Order.Desc)
            .findOne()
        return LastReadTimeResult(readDateTime = asReadRecordInfoModel?.knCreatedAt)
    }
}