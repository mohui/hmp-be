package com.bjknrt.doctor.patient.management.service.impl

import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.DpmDoctorInfoTable
import com.bjknrt.doctor.patient.management.DpmDoctorPatient
import com.bjknrt.doctor.patient.management.DpmDoctorPatientTable
import com.bjknrt.doctor.patient.management.service.DoctorPatientService
import com.bjknrt.doctor.patient.management.vo.BindDoctorRequest
import com.bjknrt.doctor.patient.management.vo.ToDoStatus
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class DoctorPatientServiceImpl(
    val doctorPatientTable: DpmDoctorPatientTable,
    val dpmDoctorInfoTable: DpmDoctorInfoTable
) : DoctorPatientService {

    @Transactional
    override fun bindDoctor(request: BindDoctorRequest) {
        val doctorPatient = doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq request.patientId.arg)
            .findOne()
            ?.let { throw MsgException(AppSpringUtil.getMessage("doctor-patient.patient-exists-bind-doctor")) }
            ?: DpmDoctorPatient()

        doctorPatient.knId = AppIdUtil.nextId()
        doctorPatient.knPatientId = request.patientId
        doctorPatient.knDoctorId = request.doctorId
        doctorPatient.knBindDoctorDatetime = LocalDateTime.now()
        doctorPatient.knStatus = ToDoStatus.START_ASSESS.name
        doctorPatientTable.saveOrUpdate(doctorPatient)
    }

    @Transactional
    override fun unbindDoctor(patientId: BigInteger) {
        val dpmDoctorPatient = (doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq patientId.arg)
            .findOne()
            ?: throw MsgException(AppSpringUtil.getMessage("doctor-patient.patient-not-bind-doctor")))
        //删除绑定关系
        deleteDoctorPatient(dpmDoctorPatient.knDoctorId, dpmDoctorPatient.knPatientId)
    }

    @Transactional
    override fun updateStatus(updateStatusRequest: UpdateStatusRequest) {
        doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq updateStatusRequest.patientId.arg)
            .findOne()
            ?.let { dpmDoctorPatient ->
                dpmDoctorPatient.knStatus = updateStatusRequest.status.name
                doctorPatientTable.saveOrUpdate(dpmDoctorPatient)
            }
    }

    override fun getBindDoctor(patientId: BigInteger): DpmDoctorInfo? {
        return doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnPatientId eq patientId)
            .findOne()
            ?.let {
                dpmDoctorInfoTable.findByKnId(it.knDoctorId)
            }
    }

    @Transactional
    override fun deleteDoctorPatient(doctorId: BigInteger, patientId: BigInteger?) {
        val queryCondition = doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnDoctorId eq doctorId.arg)

        patientId?.let {
            queryCondition.where(DpmDoctorPatientTable.KnPatientId eq patientId)
        }

        //同步健康方案服务
        val doctorPatientList = queryCondition.find()
        doctorPatientList.takeIf { it.isNotEmpty() }
            ?.let { list ->
                //删除绑定的关系
                doctorPatientTable.delete().where(DpmDoctorPatientTable.KnId `in` list.map { it.knId.arg }).execute()
            }
    }

    override fun getBindPatientNum(doctorId: BigInteger): Long {
        return doctorPatientTable.select()
            .where(DpmDoctorPatientTable.KnDoctorId eq doctorId.arg)
            .count()
    }
}
