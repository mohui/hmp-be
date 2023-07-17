package com.bjknrt.health.scheme.service.impl

import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.service.VisitService
import me.danwi.sqlex.core.query.*
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class VisitServiceImpl(
    val hsHtnVisitTable: HsHtnVisitTable,
    val hsCerebralStrokeVisitTable: HsCerebralStrokeVisitTable,
    val hsCopdVisitTable: HsCopdVisitTable,
    val hsT2dmVisitTable: HsT2dmVisitTable,
    val hsAcuteCoronaryVisitTable: HsAcuteCoronaryVisitTable
) : VisitService {
    override fun getHypertensionVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsHtnVisit> {
        return hsHtnVisitTable.select()
            .where(HsHtnVisitTable.PatientId eq patientId)
            .where(HsHtnVisitTable.FollowDate gte startDateTime)
            .where(HsHtnVisitTable.FollowDate lt endDateTime)
            .order(HsHtnVisitTable.FollowDate, Order.Desc)
            .find()
    }

    override fun getCerebralStrokeVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsCerebralStrokeVisit> {
        return hsCerebralStrokeVisitTable.select()
            .where(HsCerebralStrokeVisitTable.PatientId eq patientId.arg)
            .where(HsCerebralStrokeVisitTable.FollowDate gte startDateTime.arg)
            .where(HsCerebralStrokeVisitTable.FollowDate lt endDateTime.arg)
            .order(HsCerebralStrokeVisitTable.FollowDate, Order.Desc)
            .find()
    }

    override fun getCopdVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsCopdVisit> {
        return hsCopdVisitTable.select()
            .where(HsCopdVisitTable.PatientId eq patientId)
            .where(HsCopdVisitTable.FollowDate gte startDateTime)
            .where(HsCopdVisitTable.FollowDate lt endDateTime)
            .order(HsCopdVisitTable.FollowDate, Order.Desc)
            .find()
    }

    override fun getDiabetesVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsT2dmVisit> {
        //查询随访记录
        return hsT2dmVisitTable.select()
            .where(HsT2dmVisitTable.PatientId eq patientId)
            .where(HsT2dmVisitTable.FollowDate gte startDateTime)
            .where(HsT2dmVisitTable.FollowDate lt endDateTime)
            .order(HsT2dmVisitTable.FollowDate, Order.Desc)
            .find()
    }

    override fun getAcuteCoronaryDiseaseVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsAcuteCoronaryVisit> {
        //查询随访记录
        return hsAcuteCoronaryVisitTable.select()
            .where(HsAcuteCoronaryVisitTable.PatientId eq patientId)
            .where(HsAcuteCoronaryVisitTable.FollowDate gte startDateTime)
            .where(HsAcuteCoronaryVisitTable.FollowDate lt endDateTime)
            .order(HsAcuteCoronaryVisitTable.FollowDate, Order.Desc)
            .find()
    }
}
