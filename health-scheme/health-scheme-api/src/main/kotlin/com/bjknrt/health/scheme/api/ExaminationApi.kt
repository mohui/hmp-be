package com.bjknrt.health.scheme.api

import com.bjknrt.health.scheme.vo.AddCurrentSchemeExaminationAdapterParam
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import kotlin.collections.List
import kotlin.collections.Map

@KatoClient(name = "\${app.hmp-health-scheme.kato-server-name:\${spring.application.name}}", contextId = "ExaminationApi")
@Validated
interface ExaminationApi {


    /**
     * 同步患者当前方案的问卷选项
     * 
     *
     * @param addCurrentSchemeExaminationAdapterParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/examination/syncCurrentSchemeExaminationAdapter"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun syncCurrentSchemeExaminationAdapter(@Valid addCurrentSchemeExaminationAdapterParam: AddCurrentSchemeExaminationAdapterParam): Unit
}
