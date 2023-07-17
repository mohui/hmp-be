package com.bjknrt.question.answering.system.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.question.answering.system.QasExercise
import com.bjknrt.question.answering.system.QasExerciseTable
import com.bjknrt.question.answering.system.api.ExerciseApi
import com.bjknrt.question.answering.system.vo.*
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.arg
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import com.bjknrt.question.answering.system.service.AnswerHistoryService
import com.bjknrt.security.client.AppSecurityUtil
import java.time.LocalDateTime

@RestController("com.bjknrt.question.answering.system.api.ExerciseController")
class ExerciseController(val table: QasExerciseTable, val answerHistoryService: AnswerHistoryService) :
    AppBaseController(), ExerciseApi {
    @Transactional
    override fun exerciseAddPost(exerciseAddPostRequest: ExerciseAddPostRequest): ExerciseEvaluationOut {
        val exerciseEvaluationInner = exerciseAddPostRequest.examinationPaperData
        val patientId = exerciseEvaluationInner.patientId
        val exercise = QasExercise.builder()
            .setId(AppIdUtil.nextId())
            .setPatientId(patientId ?: AppSecurityUtil.currentUserIdWithDefault())
            .setProfessionalActivities(exerciseEvaluationInner.professionalActivities)
            .setDailyActivities(exerciseEvaluationInner.dailyActivities.joinToString(","))
            .setOutboundTransportation(exerciseEvaluationInner.outboundTransportation.joinToString(","))
            .setMovementMode(exerciseEvaluationInner.movementMode.joinToString(","))
            .setPatientGender(exerciseEvaluationInner.patientGender.name)
            .setPatientAge(exerciseEvaluationInner.patientAge)
            .setBodyFatRatio(exerciseEvaluationInner.bodyFatRatio)
            .setBodyMassIndex(exerciseEvaluationInner.bodyMassIndex)
            .setStaticHeartRate(exerciseEvaluationInner.staticHeartRate)
            .setNumberOfPushUps(exerciseEvaluationInner.numberOfPushUps)
            .setWalkingDistance6(exerciseEvaluationInner.walkingDistance6)
            .setSittingBodyForwardBendingDistance(exerciseEvaluationInner.sittingBodyForwardBendingDistance)
            .setExerciseResult(
                if (exerciseEvaluationInner.contraindicationToExercise.any { it.code != 17 } ||
                    exerciseEvaluationInner.isIndicationUnsuitableForExercise ||
                    exerciseEvaluationInner.isTakeDrugsUnsuitableForExercise)
                    RiskLevel.HIGH.name
                else
                    RiskLevel.OTHER.name
            )
            .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
            .setContraindicationToExercise(exerciseEvaluationInner.contraindicationToExercise.joinToString(",") { it.code.toString() })
            .setFlexibilityResult(
                when (exerciseEvaluationInner.patientAge) {
                    in 30..39 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 1 until 6 -> "很好"
                                in -3..0 -> "良好"
                                in -9 until -3 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 6.toBigDecimal()) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 6 until 11 -> "很好"
                                in 2..5 -> "良好"
                                in -3 until -1 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 11.toBigDecimal()) "极好" else "需改善"
                            }
                        }
                    }

                    in 40..49 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 1 until 6 -> "很好"
                                in -4..0 -> "良好"
                                in -9 until -3 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 6.toBigDecimal()) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 5 until 10 -> "很好"
                                in 0..4 -> "良好"
                                in -4 until -1 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 10.toBigDecimal()) "极好" else "需改善"
                            }
                        }
                    }

                    in 50..59 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 1 until 5 -> "很好"
                                in -5..0 -> "良好"
                                in -11 until -6 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 5.toBigDecimal()) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.sittingBodyForwardBendingDistance.toInt()) {
                                in 3 until 8 -> "很好"
                                in 0..2 -> "良好"
                                in -5 until -1 -> "正常"
                                else -> if (exerciseEvaluationInner.sittingBodyForwardBendingDistance >= 8.toBigDecimal()) "极好" else "需改善"
                            }
                        }
                    }

                    else -> null
                }
            )
            .setOtherContraindicationToExercise(exerciseEvaluationInner.contraindicationToExercise.firstOrNull { it.code == 16 }?.name)
            .setPowerResult(
                when (exerciseEvaluationInner.patientAge) {
                    in 20..29 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 29..35 -> "很好"
                                in 22..28 -> "良好"
                                in 17..21 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 36) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 21..29 -> "很好"
                                in 15..20 -> "良好"
                                in 10..14 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 30) "极好" else "需改善"
                            }
                        }
                    }

                    in 30..39 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 22..29 -> "很好"
                                in 17..21 -> "良好"
                                in 12..16 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 30) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 20..26 -> "很好"
                                in 13..19 -> "良好"
                                in 8..12 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 27) "极好" else "需改善"
                            }
                        }
                    }

                    in 40..49 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 17..24 -> "很好"
                                in 13..16 -> "良好"
                                in 10..12 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 25) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 15..23 -> "很好"
                                in 11..14 -> "良好"
                                in 5..10 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 24) "极好" else "需改善"
                            }
                        }
                    }

                    in 50..59 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 13..20 -> "很好"
                                in 10..12 -> "良好"
                                in 7..9 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 21) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 11..20 -> "很好"
                                in 7..10 -> "良好"
                                in 2..6 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 21) "极好" else "需改善"
                            }
                        }
                    }

                    in 60..69 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 11..17 -> "很好"
                                in 8..10 -> "良好"
                                in 5..7 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 18) "极好" else "需改善"
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.numberOfPushUps) {
                                in 12..16 -> "很好"
                                in 5..11 -> "良好"
                                in 2..4 -> "正常"
                                else -> if (exerciseEvaluationInner.numberOfPushUps >= 17) "极好" else "需改善"
                            }
                        }
                    }

                    else -> null
                }
            )
            .setIsHoldingModerateIntensityExercise(exerciseEvaluationInner.isHoldingModerateIntensityExercise)
            .setIsIndicationUnsuitableForExercise(exerciseEvaluationInner.isIndicationUnsuitableForExercise)
            .setIsTakeDrugsUnsuitableForExercise(exerciseEvaluationInner.isTakeDrugsUnsuitableForExercise)
            .setWalkResult(
                when (exerciseEvaluationInner.patientAge) {
                    in 20..29 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 1090.toBigDecimal()..1200.toBigDecimal() -> "好"
                                in 1000.toBigDecimal()..1080.toBigDecimal() -> "正常"
                                in 890.toBigDecimal()..990.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1200.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 890.toBigDecimal()) "较差"
                                        else null
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 970.toBigDecimal()..1070.toBigDecimal() -> "好"
                                in 880.toBigDecimal()..960.toBigDecimal() -> "正常"
                                in 780.toBigDecimal()..870.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1070.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 780.toBigDecimal()) "较差"
                                        else null
                            }
                        }
                    }

                    in 30..39 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 1150.toBigDecimal()..1240.toBigDecimal() -> "好"
                                in 1080.toBigDecimal()..1140.toBigDecimal() -> "正常"
                                in 990.toBigDecimal()..1070.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1240.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 990.toBigDecimal()) "较差"
                                        else null
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 930.toBigDecimal()..1030.toBigDecimal() -> "好"
                                in 850.toBigDecimal()..920.toBigDecimal() -> "正常"
                                in 750.toBigDecimal()..840.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1030.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 750.toBigDecimal()) "较差"
                                        else null
                            }
                        }
                    }

                    in 40..49 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 1120.toBigDecimal()..1240.toBigDecimal() -> "好"
                                in 1020.toBigDecimal()..1110.toBigDecimal() -> "正常"
                                in 900.toBigDecimal()..1010.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1240.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 900.toBigDecimal()) "较差"
                                        else null
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 900.toBigDecimal()..980.toBigDecimal() -> "好"
                                in 840.toBigDecimal()..890.toBigDecimal() -> "正常"
                                in 760.toBigDecimal()..830.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 980.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 760.toBigDecimal()) "较差"
                                        else null
                            }
                        }
                    }

                    in 50..59 -> when (exerciseEvaluationInner.patientGender) {
                        Gender.MAN -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 1010.toBigDecimal()..1110.toBigDecimal() -> "好"
                                in 930.toBigDecimal()..1000.toBigDecimal() -> "正常"
                                in 830.toBigDecimal()..930.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 1110.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 830.toBigDecimal()) "较差"
                                        else null
                            }
                        }

                        else -> {
                            when (exerciseEvaluationInner.walkingDistance6) {
                                in 840.toBigDecimal()..940.toBigDecimal() -> "好"
                                in 760.toBigDecimal()..830.toBigDecimal() -> "正常"
                                in 670.toBigDecimal()..750.toBigDecimal() -> "差"
                                else ->
                                    if (exerciseEvaluationInner.walkingDistance6 > 940.toBigDecimal()) "非常好"
                                    else
                                        if (exerciseEvaluationInner.walkingDistance6 < 670.toBigDecimal()) "较差"
                                        else null
                            }
                        }
                    }

                    else -> null
                }
            )
            .setWhyNotModerateIntensityExercise(exerciseEvaluationInner.whyNotModerateIntensityExercise.joinToString(","))
            .build()
        val row = table.save(exercise) ?: throw MsgException(AppSpringUtil.getMessage("exercise.save.error"))
        //保存答题记录
        val saveRequest = SaveAnswerRecordRequest(
            exerciseAddPostRequest.examinationPaperId,
            exerciseAddPostRequest.examinationPaperCode,
            exercise.exerciseResult,
            exercise.patientId,
            exercise.createdBy
        )
        answerHistoryService.saveAnswerRecord(saveRequest)
        return changeModel(row)
    }

    override fun exerciseGetPost(body: BigInteger): ExerciseEvaluationOut {
        val row = table.findById(body) ?: throw NotFoundDataException(AppSpringUtil.getMessage("exercise.result.row.not-found"))
        return changeModel(row)
    }

    override fun exerciseLastPost(body: BigInteger): ExerciseEvaluationOut {
        val row = table.select().where(QasExerciseTable.PatientId.eq(body.arg))
            .order(QasExerciseTable.CreatedAt, Order.Desc)
            .findOne()
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("exercise.result.not-found"))
        return changeModel(row)
    }

    fun changeModel(row: QasExercise): ExerciseEvaluationOut {
        return ExerciseEvaluationOut(
            row.id,
            row.patientId,
            row.createdAt,
            RiskLevel.valueOf(row.exerciseResult),
            row.isTakeDrugsUnsuitableForExercise,
            row.isIndicationUnsuitableForExercise,
            row.isHoldingModerateIntensityExercise,
            row.professionalActivities,
            row.dailyActivities.split(",").map { it.toInt() },
            row.outboundTransportation.split(",").map { it.toInt() },
            row.movementMode.split(",").map { it.toInt() },
            Gender.valueOf(row.patientGender),
            row.patientAge,
            row.bodyFatRatio,
            row.bodyMassIndex,
            row.staticHeartRate,
            row.numberOfPushUps,
            row.walkingDistance6,
            row.sittingBodyForwardBendingDistance,
            row.walkResult,
            row.powerResult,
            row.flexibilityResult,
            if (row.contraindicationToExercise != "")
                row.contraindicationToExercise?.split(",")?.map {
                    ContraindicationToExercise(
                        it.toInt(),
                        if (it.toInt() == 16) row.otherContraindicationToExercise else ""
                    )
                }
            else listOf(),
            if (row.whyNotModerateIntensityExercise != "")
                row.whyNotModerateIntensityExercise?.split(",")?.map { it.toInt() }
            else listOf()
        )
    }
}
