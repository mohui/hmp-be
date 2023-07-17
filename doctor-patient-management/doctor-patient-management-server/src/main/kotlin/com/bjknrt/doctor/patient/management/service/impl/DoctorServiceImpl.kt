package com.bjknrt.doctor.patient.management.service.impl

import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.DpmDoctorInfoTable
import com.bjknrt.doctor.patient.management.assembler.doctorEditRequestCopyToDoctorInfo
import com.bjknrt.doctor.patient.management.assembler.doctorInfoToDoctorInfo
import com.bjknrt.doctor.patient.management.assembler.doctorRequestToDoctorInfo
import com.bjknrt.doctor.patient.management.service.DoctorPatientService
import com.bjknrt.doctor.patient.management.service.DoctorService
import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorEditRequest
import com.bjknrt.doctor.patient.management.vo.DoctorInfo
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.enum.BooleanIntEnum
import com.bjknrt.framework.util.AppSpringUtil
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Service
class DoctorServiceImpl(
    val doctorPatientService: DoctorPatientService,
    val dpmDoctorInfoTable: DpmDoctorInfoTable,
) : DoctorService {

    @Transactional
    override fun addDoctor(request: DoctorAddRequest) {
        val dpmDoctorInfo = doctorRequestToDoctorInfo(request)
        dpmDoctorInfoTable.saveOrUpdate(dpmDoctorInfo)
    }

    @Transactional
    override fun deleteDoctor(id: BigInteger) {
        this.getDpmDoctorInfo(id)?.let {
            //删除医生
            dpmDoctorInfoTable.update()
                .setKnDel(BooleanIntEnum.TRUE.value)
                .where(DpmDoctorInfoTable.KnId eq id.arg)
                .execute()
            //删除绑定关系
            doctorPatientService.deleteDoctorPatient(id)
        }
    }

    @Transactional
    override fun editDoctor(request: DoctorEditRequest) {

        val dpmDoctorInfo =
            getDpmDoctorInfo(request.id) ?: throw MsgException(AppSpringUtil.getMessage("doctor.edit.not-found"))

        doctorEditRequestCopyToDoctorInfo(request, dpmDoctorInfo)

        dpmDoctorInfoTable.saveOrUpdate(dpmDoctorInfo)
    }

    override fun getDoctorInfo(doctorId: BigInteger): DoctorInfo {
        val dpmDoctorInfo =
            getDpmDoctorInfo(doctorId) ?: throw NotFoundDataException(AppSpringUtil.getMessage("doctor.edit.not-found"))
        return doctorInfoToDoctorInfo(dpmDoctorInfo)
    }

    private fun getDpmDoctorInfo(id: BigInteger): DpmDoctorInfo? {
        return dpmDoctorInfoTable.select()
            .where(DpmDoctorInfoTable.KnDel eq BooleanIntEnum.FALSE.value)
            .where(DpmDoctorInfoTable.KnId eq id)
            .findOne()
    }

}
