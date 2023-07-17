package com.bjknrt.question.answering.system.expost.question.impl

import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.health.scheme.api.VisitApi
import com.bjknrt.health.scheme.vo.CerebralStrokeLeaveHospitalVisit
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
class CerebralStrokeBehaviorQuestionPostProcessorsHandler(
    val visitRpcService: VisitApi
) : QuestionPostProcessorsHandler {

    companion object {
        val EXAMINATION_PAPER_CODE_LIST = listOf("CEREBRAL_STROKE_BEHAVIOR")

        //问题Id集合
        val QUESTION_ID_LIST = listOf<BigInteger>(
            //脑卒中行为习惯随访-含有%s占位符的问题id集合
            BigInteger.valueOf(250003),
            BigInteger.valueOf(250004),
            BigInteger.valueOf(250005),
            BigInteger.valueOf(250006),
        )

        //格式化器
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZoneId.systemDefault())
    }

    override fun execute(questionsOption: List<Questions>): List<Questions> {
        return try {
            val lastOneLeaveHospitalVisit =
                visitRpcService.getLastOneCerebralStrokeLeaveHospitalVisit(AppSecurityUtil.currentUserIdWithDefault())
            questionsOption.map { this.transformQuestion(it, lastOneLeaveHospitalVisit) }
        } catch (ex: NotFoundDataException) {
            questionsOption.map { this.transformQuestion(it, null) }
        }
    }

    override fun support(examinationPaper: QasExaminationPaper): Boolean {
        return EXAMINATION_PAPER_CODE_LIST.contains(examinationPaper.examinationPaperCode)
    }

    override fun getOrder(): Int {
        return 4
    }

    private fun transformQuestion(
        questions: Questions,
        hsCerebralStrokeLeaveHospitalVisit: CerebralStrokeLeaveHospitalVisit?
    ): Questions {
        if (QUESTION_ID_LIST.contains(questions.id)) {
            val updateQuestionTitle = hsCerebralStrokeLeaveHospitalVisit?.let {
                val startDateAndEndDate = this.getStageStartDateAndEndDate(it.knCreatedAt)
                //格式化时间
                val startDateStr = formatter.format(startDateAndEndDate.first)
                val endDateStr = formatter.format(startDateAndEndDate.second)
                //替换占位符
                String.format(questions.questionsTitle, "（$startDateStr-$endDateStr）")
            } ?: questions.questionsTitle.replace("%s", "")
            return questions.copy(questionsTitle = updateQuestionTitle)
        }
        return questions
    }

    private fun getStageStartDateAndEndDate(createdAt: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val currentTime = LocalDateTime.now()
        var tempThreeMonthAfterTime = createdAt
        //阶段的开始时间和结束时间
        val stageStartDate: LocalDateTime
        val stageEndDate: LocalDateTime
        while (true) {
            val threeMonthAfterTime = tempThreeMonthAfterTime.plusMonths(3)
            if (currentTime < threeMonthAfterTime) {
                stageStartDate = threeMonthAfterTime.plusMonths(-3)
                stageEndDate = threeMonthAfterTime
                break
            }
            tempThreeMonthAfterTime = threeMonthAfterTime
        }
        return Pair(stageStartDate, stageEndDate)
    }
}