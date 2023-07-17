package com.bjknrt.health.scheme.service

import com.bjknrt.health.scheme.vo.HealthManageType
import com.bjknrt.health.scheme.vo.HealthManageUsedInfo
import java.math.BigInteger

interface HealthManageUsedInfoService {
    fun getHealthManageUsedInfo(patientId: BigInteger, healthManageType: HealthManageType? = null): HealthManageUsedInfo?
}
