package com.bjknrt.health.scheme.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.vo.AddCurrentSchemeExaminationAdapterParam
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.health.scheme.api.ExaminationController")
class ExaminationController(
    val examinationService: ExaminationService
) : AppBaseController(), ExaminationApi {
    @Transactional
    override fun syncCurrentSchemeExaminationAdapter(addCurrentSchemeExaminationAdapterParam: AddCurrentSchemeExaminationAdapterParam) {
        examinationService.deleteAndInsertSchemeExaminationAdapter(addCurrentSchemeExaminationAdapterParam)
    }

}