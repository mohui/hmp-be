package com.bjknrt.question.answering.system.service

import com.bjknrt.question.answering.system.vo.DiseaseEvaluateRequest
import com.bjknrt.question.answering.system.vo.DiseaseEvaluateResponse
import com.bjknrt.question.answering.system.vo.DiseaseOption
import com.bjknrt.question.answering.system.vo.DiseaseOptionRequest
import java.math.BigInteger

interface EvaluateService {

    fun add(addEvaluateRequest: DiseaseEvaluateRequest):DiseaseEvaluateResponse
    fun getEvaluateInfo(patientId: BigInteger): DiseaseEvaluateResponse
    fun getDiseaseOption(diseaseOptionRequest: DiseaseOptionRequest): List<DiseaseOption>
    fun getLastDiseaseOption(patientId: BigInteger): DiseaseOption
}
