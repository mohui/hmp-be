package com.bjknrt.question.answering.system.evaluate.impl

import com.bjknrt.question.answering.system.evaluate.EvaluateScoreOption
import com.bjknrt.question.answering.system.evaluate.EvaluateScoreStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger

@Component
class Ai2dmEvaluateScoreStrategy : EvaluateScoreStrategy {

    override fun evaluate(options: Collection<EvaluateScoreOption>): BigDecimal? {
        val age = options.first { it.questionsId == BigInteger.valueOf(1801) }.optionValue.toInt()
        //0feman女1男man
        val sex = options.first { it.questionsId == BigInteger.valueOf(1803) }.optionValue == "MAN"
        val bmi = options.first { it.questionsId == BigInteger.valueOf(1804) }.optionValue.toBigDecimal()
        //腰围
        val waistline = options.first { it.questionsId == BigInteger.valueOf(1805) }.optionValue.toInt()
        //收缩压
        val sbp = options.first { it.questionsId == BigInteger.valueOf(1802) }.optionValue.toInt()
        val history = options.first { it.questionsId == BigInteger.valueOf(1806) }.optionValue == "YES"
        return (
                when (age) {
                    in 25..34 -> 4
                    in 35..39 -> 8
                    in 40..44 -> 11
                    in 45..49 -> 12
                    in 50..54 -> 13
                    in 55..59 -> 15
                    in 60..64 -> 16
                    in 65..74 -> 18
                    else -> 0
                } + when (bmi.toInt()) {
                    in 22 until 24 -> 1
                    in 24 until 30 -> 3
                    else -> if (bmi >= BigDecimal.valueOf(30)) 5 else 0
                } + (
                        //性别与腰围
                        if (sex)
                            2 + when (waistline) {
                                in 75 until 80 -> 3
                                in 80 until 85 -> 5
                                in 85 until 90 -> 7
                                in 90 until 95 -> 8
                                else -> if (waistline >= 95) 10 else 0
                            }
                        else
                            when (waistline) {
                                in 70 until 75 -> 3
                                in 75 until 80 -> 5
                                in 80 until 85 -> 7
                                in 85 until 90 -> 8
                                else -> if (waistline >= 90) 10 else 0
                            }
                        )
                        //血压
                        + when (sbp) {
                    in 110 until 120 -> 1
                    in 120 until 130 -> 3
                    in 130 until 140 -> 6
                    in 140 until 150 -> 7
                    in 150 until 160 -> 8
                    else -> if (sbp >= 160) 10 else 0
                } + (if (history) 6 else 0))
            .toBigDecimal()
    }

    override fun getCode(): String {
        return "AI_2DM"
    }
}
