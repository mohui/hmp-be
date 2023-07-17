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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class BehaviorQuestionPostProcessorsHandler(
    val healthPlanManageRpcService: ManageApi
) : QuestionPostProcessorsHandler {

    companion object {
        val EXAMINATION_PAPER_CODE_LIST = listOf("BEHAVIOR", "DIABETES_BEHAVIOR", "ACUTE_CORONARY_DISEASE_BEHAVIOR")

        //问题Id集合
        val QUESTION_ID_LIST = listOf<BigInteger>(
            //高血压行为习惯随访-含有%s占位符的问题id集合
            BigInteger.valueOf(140003),
            BigInteger.valueOf(140004),
            BigInteger.valueOf(140005),
            BigInteger.valueOf(140006),

            //糖尿病行为习惯随访-含有%s占位符的问题id集合
            BigInteger.valueOf(210003),
            BigInteger.valueOf(210004),
            BigInteger.valueOf(210005),
            BigInteger.valueOf(210006),

            //冠心病行为习惯随访-含有%s占位符的问题id集合
            BigInteger.valueOf(320003),
            BigInteger.valueOf(320004),
            BigInteger.valueOf(320005),
            BigInteger.valueOf(320006)
        )

        //格式化器
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZoneId.systemDefault())
    }

    override fun execute(questionsOption: List<Questions>): List<Questions> {
        return try {
            val managementInfoParam = CurrentHealthSchemeManagementInfoParam(
                AppSecurityUtil.currentUserIdWithDefault(),
                LocalDateTime.now()
            )
            val currentValidManagementScheme =
                healthPlanManageRpcService.getCurrentValidHealthSchemeManagementInfo(managementInfoParam)
            questionsOption.map { this.transformQuestion(it, currentValidManagementScheme) }
        } catch (ex: NotFoundDataException) {
            questionsOption.map { this.transformQuestion(it, null) }
        }
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return EXAMINATION_PAPER_CODE_LIST.contains(examinationPaper.examinationPaperCode)
    }

    override fun getOrder(): Int {
        return 2
    }

    private fun transformQuestion(
        questions: Questions,
        currentValidManagementScheme: HealthSchemeManagementInfo?
    ): Questions {
        if (QUESTION_ID_LIST.contains(questions.id)) {
            val updateQuestionTitle = currentValidManagementScheme?.let {
                //格式化时间由yyyy-MM-dd转为yyyy.MM.dd
                val startDateStr = formatter.format(it.knStartDate)
                val endDateStr = it.knEndDate?.let { endDate -> formatter.format(endDate) } ?: ""
                //替换占位符
                String.format(questions.questionsTitle, "（$startDateStr-$endDateStr）")
            } ?: questions.questionsTitle.replace("%s", "")
            return questions.copy(questionsTitle = updateQuestionTitle)
        }
        return questions
    }
}
