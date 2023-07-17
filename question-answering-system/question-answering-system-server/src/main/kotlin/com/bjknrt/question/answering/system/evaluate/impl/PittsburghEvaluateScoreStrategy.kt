package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import me.danwi.kato.common.exception.KatoException
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PittsburghEvaluateScoreStrategy : EvaluateScoreStrategy {

    val zeroScore: BigDecimal = BigDecimal.valueOf(0)
    val oneScore: BigDecimal = BigDecimal.valueOf(1)
    val twoScore: BigDecimal = BigDecimal.valueOf(2)
    val threeScore: BigDecimal = BigDecimal.valueOf(3)
    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        var totalScore: BigDecimal = BigDecimal.valueOf(0)
        options.filter {
            it.optionCode == "SIX_GOOD"
                    || it.optionCode == "SIX_BAD"
                    || it.optionCode == "SIX_WORSE"
                    || it.optionCode == "FOUR"
                    || it.optionCode == "SEVEN_ONE_NIGHT"
                    || it.optionCode == "SEVEN_TWO_NIGHT"
                    || it.optionCode == "SEVEN_THREE_NIGHT"
        }.forEach {
            //睡眠质量得分
            if (it.optionCode == "SIX_GOOD") totalScore += oneScore
            if (it.optionCode == "SIX_BAD") totalScore += twoScore
            if (it.optionCode == "SIX_WORSE") totalScore += threeScore

            //睡眠时间得分
            if (it.optionCode == "FOUR" && it.optionValue.toDouble() in 6.0..7.0) totalScore += oneScore
            if (it.optionCode == "FOUR" && it.optionValue.toDouble() in 5.0..6.0) totalScore += twoScore
            if (it.optionCode == "FOUR" && it.optionValue.toBigDecimal() - BigDecimal.valueOf(5.0) < zeroScore) totalScore += threeScore

            //催眠药物得分
            if (it.optionCode == "SEVEN_ONE_NIGHT") totalScore += oneScore
            if (it.optionCode == "SEVEN_TWO_NIGHT") totalScore += twoScore
            if (it.optionCode == "SEVEN_THREE_NIGHT") totalScore += threeScore

        }

        //入睡时间得分
        var questionTwo = BigDecimal.valueOf(0)
        var questionFiveA = BigDecimal.valueOf(0)
        options.filter {
            it.optionCode == "TWO_THIRTY_MINUTE"
                    || it.optionCode == "TWO_SIXTY_MINUTE"
                    || it.optionCode == "TWO_THAN_SIXTY_MINUTE"
                    || it.optionCode == "FIVE_A_ONE_NIGHT"
                    || it.optionCode == "FIVE_A_TWO_NIGHT"
                    || it.optionCode == "FIVE_A_THREE_NIGHT"
        }.forEach {
            if (it.optionCode == "TWO_THIRTY_MINUTE") questionTwo += oneScore
            if (it.optionCode == "TWO_SIXTY_MINUTE") questionTwo += twoScore
            if (it.optionCode == "TWO_THAN_SIXTY_MINUTE") questionTwo += threeScore
            if (it.optionCode == "FIVE_A_ONE_NIGHT") questionFiveA += oneScore
            if (it.optionCode == "FIVE_A_TWO_NIGHT") questionFiveA += twoScore
            if (it.optionCode == "FIVE_A_THREE_NIGHT") questionFiveA += threeScore
        }

        if ((questionTwo + questionFiveA).toDouble() in 1.0..2.0) totalScore += oneScore
        if ((questionTwo + questionFiveA).toDouble() in 3.0..4.0) totalScore += twoScore
        if ((questionTwo + questionFiveA).toDouble() in 5.0..6.0) totalScore += threeScore

        //睡眠效率得分
        var oneClock = 0
        var threeClock = 0
        options.filter {
            it.optionCode == "ONE" || it.optionCode == "THREE"
        }.forEach {
            if (it.optionCode == "ONE") {
                oneClock = it.optionValue.toInt()
            }
            if (it.optionCode == "THREE") {
                threeClock = it.optionValue.toInt()
            }
        }
        //起床时间和上床时间的相差小时
        val duration = diff(oneClock, threeClock).toBigDecimal()
        options.filter {
            it.optionCode == "FOUR"
        }.forEach {
            //睡眠效率
            val fourSleepEfficiency =
                (it.optionValue.toBigDecimal().divide(duration, 2, RoundingMode.HALF_UP)).toDouble()
            if (fourSleepEfficiency in 0.75..0.84) totalScore += oneScore
            if (fourSleepEfficiency in 0.65..0.74) totalScore += twoScore
            if ((fourSleepEfficiency - 0.65).toBigDecimal() < zeroScore) totalScore += threeScore
        }

        var fiveB = zeroScore
        var fiveC = zeroScore
        var fiveD = zeroScore
        var fiveE = zeroScore
        var fiveF = zeroScore
        var fiveG = zeroScore
        var fiveH = zeroScore
        var fiveI = zeroScore
        var fiveJ = zeroScore

        //睡眠障碍得分
        options.filter {
            it.optionCode?.startsWith("FIVE", true) ?: false
        }.forEach {
            if (it.optionCode == "FIVE_B_ONE_NIGHT") fiveB = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_B_TWO_NIGHT") fiveB = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_B_THREE_NIGHT") fiveB = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_C_ONE_NIGHT") fiveC = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_C_TWO_NIGHT") fiveC = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_C_THREE_NIGHT") fiveC = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_D_ONE_NIGHT") fiveD = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_D_TWO_NIGHT") fiveD = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_D_THREE_NIGHT") fiveD = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_E_ONE_NIGHT") fiveE = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_E_TWO_NIGHT") fiveE = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_E_THREE_NIGHT") fiveE = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_F_ONE_NIGHT") fiveF = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_F_TWO_NIGHT") fiveF = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_F_THREE_NIGHT") fiveF = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_G_ONE_NIGHT") fiveG = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_G_TWO_NIGHT") fiveG = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_G_THREE_NIGHT") fiveG = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_H_ONE_NIGHT") fiveH = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_H_TWO_NIGHT") fiveH = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_H_THREE_NIGHT") fiveH = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_I_ONE_NIGHT") fiveI = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_I_TWO_NIGHT") fiveI = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_I_THREE_NIGHT") fiveI = it.optionScore ?: BigDecimal.ZERO

            if (it.optionCode == "FIVE_J_ONE_NIGHT") fiveJ = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_J_TWO_NIGHT") fiveJ = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "FIVE_J_THREE_NIGHT") fiveJ = it.optionScore ?: BigDecimal.ZERO
        }
        val totalSleepObstacleScore = (fiveB + fiveC + fiveD + fiveE + fiveF + fiveG + fiveH + fiveI + fiveJ).toDouble()
        if (totalSleepObstacleScore in 1.0..9.0) totalScore += oneScore
        if (totalSleepObstacleScore in 10.0..18.0) totalScore += twoScore
        if (totalSleepObstacleScore in 19.0..27.0) totalScore += threeScore

        //日间功能障碍得分
        var eightScore = zeroScore
        var nineScore = zeroScore

        options.filter {
            it.optionCode?.startsWith("EIGHT", true) ?: false
        }.forEach {
            if (it.optionCode == "EIGHT_ONE_NIGHT") eightScore = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "EIGHT_TWO_NIGHT") eightScore = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "EIGHT_THREE_NIGHT") eightScore = it.optionScore ?: BigDecimal.ZERO
        }

        options.filter {
            it.optionCode?.startsWith("NINE", true) ?: false
        }.forEach {
            if (it.optionCode == "NINE_ONE_NIGHT") nineScore = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "NINE_TWO_NIGHT") nineScore = it.optionScore ?: BigDecimal.ZERO
            if (it.optionCode == "NINE_THREE_NIGHT") nineScore = it.optionScore ?: BigDecimal.ZERO
        }

        val enTotal = (eightScore + nineScore).toDouble()
        if (enTotal in 1.0..2.0) totalScore += oneScore
        if (enTotal in 3.0..4.0) totalScore += twoScore
        if (enTotal in 5.0..6.0) totalScore += threeScore

        return totalScore

    }

    override fun getCode(): String {
        return "PITTSBURGH"
    }

    /**
     * 判断环形 0..23 之间的距离
     */
    fun diff(start: Int, end: Int): Int {
        if ((start !in 0..23) || end !in 0..23) throw KatoException("时间范围必须在0到23之间")
        var count = 0
        var processStart = false
        var linedNode = circleLinked
        while (true) {
            if (processStart) {
                count++
            }
            if (linedNode.value == start) {
                processStart = true
            }
            if (processStart && linedNode.value == end) {
                break
            }
            linedNode.nextNode?.let { linedNode = it } ?: break
        }
        return count
    }

    private val circleLinked = (23 downTo 0)
        .map { CircleLinkedNode(it) }
        .let {
            it.first().apply { nextNode = it.reduce { acc, circleLined -> circleLined.apply { nextNode = acc } } }
        }

    private class CircleLinkedNode(
        val value: Int,
        var nextNode: CircleLinkedNode? = null
    )
}
