package com.bjknrt.question.answering.system.expost.question.impl

import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.CurrentHealthSchemeManagementInfoParam
import com.bjknrt.health.scheme.vo.HealthSchemeManagementInfo
import com.bjknrt.question.answering.system.QasExaminationPaper
import com.bjknrt.question.answering.system.expost.question.QuestionPostProcessorsHandler
import com.bjknrt.question.answering.system.vo.Questions
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class CopdBehaviorQuestionPostProcessorsHandler(
    val healthPlanManageRpcService: ManageApi
) : QuestionPostProcessorsHandler {

    companion object {
        const val EXAMINATION_PAPER_CODE = "COPD_BEHAVIOR"

        //问题Id集合
        val QUESTION_ID_LIST = listOf<BigInteger>(
            BigInteger.valueOf(310002),
            BigInteger.valueOf(310003),
            BigInteger.valueOf(310004),
        )

        //格式化器
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZoneId.systemDefault())
    }

    override fun execute(questionsOption: List<Questions>): List<Questions> {
        return try {
            val currentValidHealthManage =
                healthPlanManageRpcService.getCurrentValidHealthSchemeManagementInfo(
                    CurrentHealthSchemeManagementInfoParam(
                        AppSecurityUtil.currentUserIdWithDefault(),
                        LocalDateTime.now()
                    )
                )
            questionsOption.map { this.transformQuestion(it, currentValidHealthManage) }
        } catch (ex: NotFoundDataException) {
            questionsOption.map { it.copy(questionsTitle = it.questionsTitle.replace("%s", "")) }
        }
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return EXAMINATION_PAPER_CODE.contains(examinationPaper.examinationPaperCode)
    }

    override fun getOrder(): Int {
        return 3
    }

    private fun transformQuestion(questions: Questions, healthManage: HealthSchemeManagementInfo): Questions {
        if (QUESTION_ID_LIST.contains(questions.id)) {

            val healthManageStartDate = healthManage.knStartDate
            val now = LocalDate.now()

            //开始时间到当前时间间隔的月份
            val betweenMonths: Long = ChronoUnit.MONTHS.between(healthManageStartDate, now)
            //新的开始时间,只会小于或者等于当前时间
            val startDate = healthManageStartDate.plusMonths(betweenMonths)
            //计算出新的结束时间
            val endDate: LocalDate = startDate.plusMonths(1).plusDays(-1)

            //格式化时间由yyyy-MM-dd转为yyyy.MM.dd
            val startDateStr = DATE_FORMATTER.format(startDate)
            val endDateStr = DATE_FORMATTER.format(endDate)
            //替换占位符
            val newQuestionTitle = String.format(questions.questionsTitle, "（$startDateStr-$endDateStr）")
            return questions.copy(questionsTitle = newQuestionTitle)
        }
        return questions
    }
}
