package com.bjknrt.doctor.patient.management.job

import com.bjknrt.doctor.patient.management.dao.ForeignOperationLogDAO
import com.bjknrt.doctor.patient.management.service.PatientService
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.spring.annotations.Recurring
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * @Description: 更新患者7天内未登录状态的定时任务
 */
@Component
class PatientLoginStatusJob(
    val foreignOperationLogDAO: ForeignOperationLogDAO,
    val patientService: PatientService
) {
    companion object {

        //job名称
        const val NOT_LOGGED_FOR_SEVEN_DAYS_JOB_NAME = "notLoggedForSevenDaysJobHandler"

        //未登录天数
        const val NOT_LOGIN_DAYS_NUM = 7

        //日活
        val LOG_MODULE_MAP = mapOf(
            LogModule.PATIENT_CLIENT to "患者端服务"
        )
        val LOG_ACTION_MAP = mapOf(
            LogAction.PATIENT_CLIENT_DAILY_ACTIVE_USER to "记录日活"
        )
    }

    @Transactional
    @Job(retries = 3, name = "每天凌晨1点更新7天未登录的患者状态")
    @Recurring(id = NOT_LOGGED_FOR_SEVEN_DAYS_JOB_NAME, cron = "0 1 * * *")
    fun notLoggedForSevenDaysJobHandler() {
        //1、查询7天内活跃的所有患者
        val patientIdsList =
            foreignOperationLogDAO.findSevenDayActivePatientIdsList(
                NOT_LOGIN_DAYS_NUM,
                LOG_MODULE_MAP[LogModule.PATIENT_CLIENT],
                LOG_ACTION_MAP[LogAction.PATIENT_CLIENT_DAILY_ACTIVE_USER]
            )
        //2、更新患者的待办事项状态的接口
        patientService.updateSevenDaysNotLogin(patientIdsList)
    }
}