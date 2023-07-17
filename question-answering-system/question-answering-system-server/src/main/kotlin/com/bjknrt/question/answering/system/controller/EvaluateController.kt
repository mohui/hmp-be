package com.bjknrt.question.answering.system.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.question.answering.system.api.EvaluateApi
import com.bjknrt.question.answering.system.service.EvaluateService
import com.bjknrt.question.answering.system.vo.DiseaseEvaluateRequest
import com.bjknrt.question.answering.system.vo.DiseaseEvaluateResponse
import com.bjknrt.question.answering.system.vo.DiseaseOption
import com.bjknrt.question.answering.system.vo.DiseaseOptionRequest
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.question.answering.system.api.EvaluateController")
class EvaluateController(
    val evaluateService: EvaluateService
) : AppBaseController(), EvaluateApi {

    @Transactional
    override fun diseaseEvaluate(diseaseEvaluateRequest: DiseaseEvaluateRequest): DiseaseEvaluateResponse {
        return evaluateService.add(diseaseEvaluateRequest)
    }

    override fun getEvaluateInfo(body: BigInteger): DiseaseEvaluateResponse {
        return evaluateService.getEvaluateInfo(body)
    }

    override fun getDiseaseOption(diseaseOptionRequest: DiseaseOptionRequest): List<DiseaseOption> {
      return  evaluateService.getDiseaseOption(diseaseOptionRequest)
    }

    override fun getLastDiseaseOption(body: BigInteger): DiseaseOption {
        return  evaluateService.getLastDiseaseOption(body)
    }

}
