package com.bjknrt.question.answering.system.api

import com.bjknrt.question.answering.system.vo.ExerciseAddPostRequest
import com.bjknrt.question.answering.system.vo.ExerciseEvaluationOut
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-question-answering-system.kato-server-name:\${spring.application.name}}", contextId = "ExerciseApi")
@Validated
interface ExerciseApi {


    /**
     * 保存运动风险评估
     *
     *
     * @param exerciseAddPostRequest
     * @return ExerciseEvaluationOut
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/exercise/add"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun exerciseAddPost(@Valid exerciseAddPostRequest: ExerciseAddPostRequest): ExerciseEvaluationOut


    /**
     * 获取运动风险评估详情
     *
     *
     * @param body
     * @return ExerciseEvaluationOut
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/exercise/get"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun exerciseGetPost(@Valid body: java.math.BigInteger): ExerciseEvaluationOut


    /**
     * 获取最新运动风险评估详情
     *
     *
     * @param body
     * @return ExerciseEvaluationOut
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/exercise/last"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun exerciseLastPost(@Valid body: java.math.BigInteger): ExerciseEvaluationOut
}
