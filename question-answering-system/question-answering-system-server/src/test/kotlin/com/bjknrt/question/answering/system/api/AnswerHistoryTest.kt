package com.bjknrt.question.answering.system.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.question.answering.system.*
import com.bjknrt.question.answering.system.vo.AnswerRecordListRequest
import com.bjknrt.question.answering.system.vo.PageAnswerRecordRequest
import com.bjknrt.question.answering.system.vo.SaveAnswerRecordRequest
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.nio.charset.Charset
import java.time.LocalDateTime

@AutoConfigureMockMvc
class AnswerHistoryTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var questionsAnswerRecordTable: QasQuestionsAnswerRecordTable

    @Autowired
    lateinit var qasExaminationPaperTable: QasExaminationPaperTable


    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build();

    }


    @Test
    fun saveAnswerRecordTest() {
        val id = AppIdUtil.nextId()

        val request = SaveAnswerRecordRequest(id, "Code$id", "危险", id, id,BigDecimal.ZERO , "")

        mvc.perform(
            MockMvcRequestBuilders.post("/answerHistory/save")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        val answerRecord = questionsAnswerRecordTable.select()
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperId eq id)
            .where(QasQuestionsAnswerRecordTable.ExaminationPaperCode eq "Code$id")
            .findOne()

        Assertions.assertEquals(true, answerRecord != null)
        Assertions.assertEquals(BigDecimal.ZERO.intValueExact(), answerRecord?.totalScore?.intValueExact())
        Assertions.assertEquals("危险", answerRecord?.resultsTag)


    }

    @Test
    fun pageTest() {
        questionsAnswerRecordTable.delete().execute()

        val patientId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()
        val examinationPaperCode = "EatingHabitsAssessment$examinationPaperId"
        qasExaminationPaperTable.insert(
            QasExaminationPaper.builder()
                .setId(examinationPaperId)
                .setExaminationPaperTitle("运动风险评估")
                .setExaminationPaperCode(examinationPaperCode)
                .setCreatedBy(patientId)
                .setUpdatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setEvaluationTime("")
                .setIsDel(false)
                .setStrategyCode("EHA")
                .setUpdatedAt(LocalDateTime.now())
                .setExaminationPaperDesc("")
                .setExaminationPaperReference("")
                .setExaminationPaperTip("")
                .build()
        )

        questionsAnswerRecordTable.save(
            QasQuestionsAnswerRecord.builder()
                .setId(AppIdUtil.nextId())
                .setAnswerBy(patientId)
                .setExaminationPaperId(examinationPaperId)
                .setExaminationPaperCode(examinationPaperCode)
                .setResultsTag("危险")
                .setCreatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setResultsMsg("")
                .setTotalScore(BigDecimal.TEN)
                .build()
        )

        questionsAnswerRecordTable.save(
            QasQuestionsAnswerRecord.builder()
                .setId(AppIdUtil.nextId())
                .setAnswerBy(patientId)
                .setExaminationPaperId(examinationPaperId)
                .setExaminationPaperCode(examinationPaperCode)
                .setResultsTag("良好")
                .setCreatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setResultsMsg("")
                .setTotalScore(BigDecimal.ONE)
                .build()
        )

        var request = PageAnswerRecordRequest(listOf(), 1, 10, patientId, patientId)

        mvc.perform(
            MockMvcRequestBuilders.post("/answerHistory/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        request = PageAnswerRecordRequest(listOf(), 1, 10)

        mvc.perform(
            MockMvcRequestBuilders.post("/answerHistory/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun listTest() {

        val patientId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()
        val examinationPaperCode = "listTest"
        qasExaminationPaperTable.insert(
            QasExaminationPaper.builder()
                .setId(examinationPaperId)
                .setExaminationPaperTitle("运动风险评估")
                .setExaminationPaperCode(examinationPaperCode)
                .setCreatedBy(patientId)
                .setUpdatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setEvaluationTime("")
                .setIsDel(false)
                .setStrategyCode("EHA")
                .setUpdatedAt(LocalDateTime.now())
                .setExaminationPaperDesc("")
                .setExaminationPaperReference("")
                .setExaminationPaperTip("")
                .build()
        )
        questionsAnswerRecordTable.save(
            QasQuestionsAnswerRecord.builder()
                .setId(AppIdUtil.nextId())
                .setAnswerBy(patientId)
                .setExaminationPaperId(examinationPaperId)
                .setExaminationPaperCode(examinationPaperCode)
                .setResultsTag("危险")
                .setCreatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setResultsMsg("")
                .setTotalScore(BigDecimal.TEN)
                .build()
        )
        questionsAnswerRecordTable.save(
            QasQuestionsAnswerRecord.builder()
                .setId(AppIdUtil.nextId())
                .setAnswerBy(patientId)
                .setExaminationPaperId(examinationPaperId)
                .setExaminationPaperCode(examinationPaperCode)
                .setResultsTag("良好")
                .setCreatedBy(patientId)
                .setCreatedAt(LocalDateTime.now())
                .setResultsMsg("")
                .setTotalScore(BigDecimal.ONE)
                .build()
        )

        val request = AnswerRecordListRequest(
            listOf(examinationPaperCode),
            patientId,
            LocalDateTime.now().plusDays(-1),
            LocalDateTime.now()
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/answerHistory/list")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }

}
