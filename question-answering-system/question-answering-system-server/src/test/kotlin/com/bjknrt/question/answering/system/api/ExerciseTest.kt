package com.bjknrt.question.answering.system.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import com.bjknrt.question.answering.system.AbstractContainerBaseTest
import com.bjknrt.question.answering.system.vo.*
import java.math.BigDecimal

class ExerciseTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: ExerciseApi

    /**
     * To test ExerciseApi.exerciseAddPost
     */
    @Test
    fun exercisePostTest() {
        val exerciseAddPostRequest: ExerciseAddPostRequest = ExerciseAddPostRequest(
            1.toBigInteger(), "code",
            ExerciseEvaluationInner(
                listOf(
                    ContraindicationToExercise(0, "不稳定性心绞痛"),
                    ContraindicationToExercise(16, "其他的一个禁忌症")
                ),
                false,
                false,
                true,
                listOf(),
                0,
                listOf(0, 1),
                listOf(1),
                listOf(2, 3),
                Gender.WOMAN,
                28,
                BigDecimal(30),
                BigDecimal(30),
                80,
                1,
                BigDecimal(1000),
                BigDecimal(10),
                1234567890.toBigInteger()
            )
        )
        val response1: ExerciseEvaluationOut = api.exerciseAddPost(exerciseAddPostRequest)
        assertEquals(RiskLevel.HIGH, response1.exerciseResult)

        val response2: ExerciseEvaluationOut = api.exerciseGetPost(response1.id)
        assertEquals(response1.id, response2.id)

        val response3: ExerciseEvaluationOut = api.exerciseLastPost(1234567890.toBigInteger())
        assertEquals(response1.id, response3.id)
    }
}
