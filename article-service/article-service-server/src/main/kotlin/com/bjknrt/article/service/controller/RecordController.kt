package com.bjknrt.article.service.controller

import com.bjknrt.article.service.api.RecordApi
import com.bjknrt.article.service.service.ReadRecordService
import com.bjknrt.article.service.vo.LastReadTimeParam
import com.bjknrt.article.service.vo.LastReadTimeResult
import com.bjknrt.article.service.vo.SaveReadRecordParam
import com.bjknrt.framework.api.AppBaseController
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.article.service.api.RecordController")
class RecordController(
    val readRecordService: ReadRecordService
) : AppBaseController(), RecordApi {
    override fun lastReadTime(lastReadTimeParam: LastReadTimeParam): LastReadTimeResult {
        return readRecordService.lastReadTime(lastReadTimeParam)
    }

    override fun saveReadRecord(saveReadRecordParam: SaveReadRecordParam) {
        readRecordService.saveReadRecord(saveReadRecordParam)
    }

}