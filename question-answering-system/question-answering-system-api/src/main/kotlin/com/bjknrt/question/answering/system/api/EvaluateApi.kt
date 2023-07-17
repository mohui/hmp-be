package com.bjknrt.question.answering.system.api

import com.bjknrt.question.answering.system.vo.DiseaseEvaluateRequest
import com.bjknrt.question.answering.system.vo.DiseaseEvaluateResponse
import com.bjknrt.question.answering.system.vo.DiseaseOption
import com.bjknrt.question.answering.system.vo.DiseaseOptionRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-question-answering-system.kato-server-name:\${spring.application.name}}", contextId = "EvaluateApi")
@Validated
interface EvaluateApi {


    /**
     * 五病综合评估
     *
     *
     * @param diseaseEvaluateRequest
     * @return DiseaseEvaluateResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/evaluate/add"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun diseaseEvaluate(@Valid diseaseEvaluateRequest: DiseaseEvaluateRequest): DiseaseEvaluateResponse


    /**
     * 查询五病综合评估选项结果
     *
     *
     * @param diseaseOptionRequest
     * @return List<DiseaseOption>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/evaluate/getDiseaseOption"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getDiseaseOption(@Valid diseaseOptionRequest: DiseaseOptionRequest): List<DiseaseOption>


    /**
     * 查询五病综合评估结果
     *
     *
     * @param body
     * @return DiseaseEvaluateResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/evaluate/getEvaluateInfo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getEvaluateInfo(@Valid body: java.math.BigInteger): DiseaseEvaluateResponse


    /**
     * 查询最新的五病综合评估选项结果
     *
     *
     * @param body
     * @return DiseaseOption
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/evaluate/getLastDiseaseOption"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getLastDiseaseOption(@Valid body: java.math.BigInteger): DiseaseOption
}
