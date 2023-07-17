package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.HsAcuteCoronaryVisit
import com.bjknrt.health.scheme.HsCerebralStrokeVisit
import com.bjknrt.health.scheme.HsCopdVisit
import com.bjknrt.health.scheme.HsHtnVisit
import com.bjknrt.health.scheme.HsT2dmVisit
import java.math.BigInteger
import java.time.LocalDateTime

interface VisitService {
    /**
     * 查询高血压随访记录
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 高血压随访记录
     */
    fun getHypertensionVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsHtnVisit>


    /**
     * 查询脑卒中随访记录
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 脑卒中随访记录
     */
    fun getCerebralStrokeVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsCerebralStrokeVisit>


    /**
     * 查询慢阻肺随访记录
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 慢阻肺随访记录
     */
    fun getCopdVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsCopdVisit>

    /**
     * 获取糖尿病随访记录
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime  结束时间
     * @return 糖尿病随访记录
     */
    fun getDiabetesVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsT2dmVisit>

    /**
     * 查询冠心病随访记录
     * @param patientId 患者Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 慢阻肺随访记录
     */
    fun getAcuteCoronaryDiseaseVisitList(
        patientId: BigInteger,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<HsAcuteCoronaryVisit>
}
