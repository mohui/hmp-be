package com.bjknrt.article.service.service

import com.bjknrt.article.service.vo.LastReadTimeParam
import com.bjknrt.article.service.vo.LastReadTimeResult
import com.bjknrt.article.service.vo.SaveReadRecordParam

interface ReadRecordService {
    fun saveReadRecord(saveReadRecordParam: SaveReadRecordParam)

    fun lastReadTime(lastReadTimeParam: LastReadTimeParam): LastReadTimeResult
}