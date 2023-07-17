package com.bjknrt.user.permission.centre.controller

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import com.bjknrt.user.permission.centre.dao.PatientHealthServiceDao
import com.bjknrt.user.permission.centre.dao.PatientHealthServiceDao.ActivationCodeListResult
import com.bjknrt.user.permission.centre.event.SubscribeHealthServiceEvent
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

@RestController("com.bjknrt.user.permission.centre.api.HealthServiceController")
class HealthServiceController(
    val dao: PatientHealthServiceDao,
    val serviceTable: UpcsHealthServiceTable,
    val activationCodeTable: UpcsHealthServiceActivationCodeTable,
    val patientHealthServiceTable: UpcsPatientHealthServiceTable,
    val eventPublisher: ApplicationEventPublisher,
    val patientInfoRpcService: PatientApi,
) : AppBaseController(), HealthServiceApi {
    companion object {
        const val BASIC_SERVICE_CODE = "basic"
    }

    override fun existsService(healthServiceExistsRequest: HealthServiceExistsRequest): Boolean {
        val number = dao.patientServiceNumber(
            healthServiceExistsRequest.patientId, healthServiceExistsRequest.serviceCode
        ) ?: 0

        return number > 0
    }

    @Transactional
    override fun getHealthServicePatient(body: BigInteger): List<HealthService> {
        return getHealthServiceList(body)
    }

    override fun healthServiceActivationCodeListPost(activationCodeListParam: ActivationCodeListParam): PagedResult<HealthServiceActivationCode> {
        if (activationCodeListParam.pageSize == 0L) throw MsgException("分页大小错误")
        val rows: List<ActivationCodeListResult> = getHealthServiceActivationCodeList(
            null,
            activationCodeListParam.serviceIds,
            activationCodeListParam.createdBy,
            activationCodeListParam.status,
            activationCodeListParam.createdDateMin,
            activationCodeListParam.createdDateMax
        )
        val list = rows.groupBy { it.activationCode }
        //0,10为0-9
        val formIndex = ((activationCodeListParam.pageNo - 1) * activationCodeListParam.pageSize).toInt()
        val toIndex = (activationCodeListParam.pageNo * activationCodeListParam.pageSize).toInt()
        val keys = if (formIndex >= list.keys.size || toIndex <= formIndex) listOf()
        else
            list.keys.toList()
                .subList(
                    formIndex,
                    if (toIndex >= list.keys.size) list.keys.size else toIndex
                )
        val patientBindingRows = dao.activationCodeBindingList(keys)
        return PagedResult(
            totalPage = ceil(list.keys.size.toDouble() / activationCodeListParam.pageSize.toDouble()).toLong(),
            pageSize = activationCodeListParam.pageSize,
            pageNo = activationCodeListParam.pageNo,
            total = list.size.toLong(),
            _data = keys.map { code ->
                val row = rows.first { it.activationCode == code }
                //状态为初始化待使用
                var status: HealthServiceActivationCodeStatus = HealthServiceActivationCodeStatus.DEFINED
                if (row.forbiddenBy != null) {
                    status = HealthServiceActivationCodeStatus.FORBIDDEN
                } else {
                    if (patientBindingRows.any { it.activationCode == code }) {
                        status = HealthServiceActivationCodeStatus.USED
                    }
                }
                val healthServices = rows.filter { r -> r.activationCode == code }.map {
                    HealthServiceGetActivationCodePost200ResponseHealthServicesInner(
                        it.healthServiceId,
                        it.healthServiceCode,
                        it.healthServiceName,
                        it.duringTime
                    )
                }
                HealthServiceActivationCode(
                    code,
                    status,
                    row.createdByName,
                    row.createdAt,
                    healthServices,
                    row.forbiddenByName,
                    row.forbiddenAt,
                    row.purchasedBy,
                    row.purchaserPhone,
                    row.purchaseRemarks
                )
            }
        )
    }

    @Transactional
    override fun healthServiceAddActivationCodePost(healthServiceAddActivationCodePostRequest: HealthServiceAddActivationCodePostRequest): String {
        val code = AppIdUtil.next16NumStrWithSeparator()
        val createdBy = AppSecurityUtil.currentUserIdWithDefault()
        val createdAt = LocalDateTime.now()
        for (service in healthServiceAddActivationCodePostRequest.healthServices)
            activationCodeTable.insertWithoutNull(
                UpcsHealthServiceActivationCode.builder()
                    .setActivationCode(code)
                    .setHealthServiceId(service.id)
                    .setCreatedBy(createdBy.toLong())
                    .setCreatedAt(createdAt)
                    .setUsageTimes(1)
                    .setPurchasedBy(healthServiceAddActivationCodePostRequest.purchasedByName)
                    .setPurchaserPhone(healthServiceAddActivationCodePostRequest.purchaserPhone)
                    .setPurchaseRemarks(healthServiceAddActivationCodePostRequest.purchaseRemarks)
                    .build()
            )
        return code
    }

    override fun healthServiceForbiddenActivationCodePost(body: String): Boolean {
        val rows = activationCodeTable
            .select()
            .where(UpcsHealthServiceActivationCodeTable.ActivationCode eq body.arg)
            .where(UpcsHealthServiceActivationCodeTable.ForbiddenBy.isNull)
            .find()

        if (rows.isNullOrEmpty()) return false
        else {
            val forbiddenBy = AppSecurityUtil.currentUserIdWithDefault()
            val forbiddenAt = LocalDateTime.now()
            activationCodeTable
                .update()
                .where(UpcsHealthServiceActivationCodeTable.ActivationCode eq body.arg)
                .where(UpcsHealthServiceActivationCodeTable.ForbiddenBy.isNull)
                .setForbiddenAt(forbiddenAt)
                .setForbiddenBy(forbiddenBy.toLong())
                .execute()
        }
        return true
    }

    override fun healthServiceGetActivationCodePost(body: String): HealthServiceGetActivationCodePost200Response {
        val rows = getHealthServiceActivationCodeList(
            body,
            null,
            null,
            null,
            null,
            null
        )
        if (rows.isEmpty()) throw MsgException("激活码未找到")
        val row = rows.first()
        //状态为初始化待使用
        var status: HealthServiceActivationCodeStatus = HealthServiceActivationCodeStatus.DEFINED
        val recordRows = mutableListOf<HealthServiceGetActivationCodePost200ResponseRecordInner>()
        recordRows.add(
            HealthServiceGetActivationCodePost200ResponseRecordInner(
                "创建",
                row.createdBy.toBigInteger(),
                row.createdByName,
                row.createdByLoginName,
                row.createdAt
            )
        )
        if (row.forbiddenBy != null) {
            status = HealthServiceActivationCodeStatus.FORBIDDEN
            recordRows.add(
                HealthServiceGetActivationCodePost200ResponseRecordInner(
                    "禁用",
                    row.forbiddenBy.toBigInteger(),
                    row.forbiddenByName,
                    row.forbiddenByLoginName,
                    row.forbiddenAt
                )
            )
        } else {
            val bindingRows = dao.activationCodeBindingList(listOf(body))
            if (bindingRows.isNotEmpty()) {
                status = HealthServiceActivationCodeStatus.USED
                val patientUsingRecord = bindingRows.first()
                val patientName = patientInfoRpcService.getPatientInfo(patientUsingRecord.patientId).name
                recordRows.add(
                    HealthServiceGetActivationCodePost200ResponseRecordInner(
                        "激活",
                        patientUsingRecord.patientId,
                        patientName,
                        patientUsingRecord.patientLoginName,
                        patientUsingRecord.createdAt
                    )
                )
            }
        }
        val healthServices = rows.map {
            HealthServiceGetActivationCodePost200ResponseHealthServicesInner(
                it.healthServiceId,
                it.healthServiceCode,
                it.healthServiceName,
                it.duringTime
            )
        }
        return HealthServiceGetActivationCodePost200Response(
            body,
            status,
            row.createdByName,
            row.createdAt,
            healthServices,
            recordRows,
            row.forbiddenByName,
            row.forbiddenAt,
            row.purchasedBy,
            row.purchaserPhone,
            row.purchaseRemarks
        )
    }

    override fun healthServiceGetPost(body: BigInteger): List<HealthManagementItem> {
        return dao.serviceItems(body).map {
            HealthManagementItem(
                it.healthManagementItemId, it.healthManagementItemCode, it.healthManagementItemName
            )
        }
    }

    @Transactional
    override fun healthServiceListPost(): List<HealthService> {
        val patientId = AppSecurityUtil.currentUserIdWithDefault()
        return getHealthServiceList(patientId)
    }

    override fun healthServicePatientPost(): List<HealthManagementItem> {
        val patientId = AppSecurityUtil.currentUserIdWithDefault()
        return getHealthItemList(patientId)
    }

    @Transactional
    override fun healthServiceReceivePost(body: String): List<ReceiveServiceResultInner> {
        //查询领取的激活码是否存在
        val codeBindingServiceList =
            activationCodeTable.select()
                .where(UpcsHealthServiceActivationCodeTable.ActivationCode eq arg(body))
                .where(UpcsHealthServiceActivationCodeTable.ForbiddenBy.isNull)
                .find()
        if (codeBindingServiceList.isNullOrEmpty()) throw MsgException("激活码不存在或已失效")
        //判断激活码对应服务包订阅记录
        val patientHealthServiceList = patientHealthServiceTable.select()
            .where(UpcsPatientHealthServiceTable.HealthServiceId `in` codeBindingServiceList.map { it.healthServiceId.arg })
            .order(UpcsPatientHealthServiceTable.CreatedAt, Order.Desc).find()
        //使用次数达到设定数量时或已患者使用该激活码激活，提示不允许激活
        val usageTimes = codeBindingServiceList.firstOrNull()?.usageTimes ?: 0
        if (patientHealthServiceList.filter { it.activationCode == body }.size >= usageTimes
            || patientHealthServiceList.any { it.activationCode == body && it.patientId == AppSecurityUtil.currentUserIdWithDefault() }
        )
            throw MsgException("激活码已使用")
        val result = mutableListOf<ReceiveServiceResultInner>()
        val serviceModels = serviceTable.select()
            .where(UpcsHealthServiceTable.HealthServiceId `in` codeBindingServiceList.map { it.healthServiceId.arg })
            .find()
            ?: throw MsgException("激活码对应的服务包不存在")
        for (service in serviceModels) {
            //领取存储
            val data = UpcsPatientHealthService.builder()
                .setPatientId(AppSecurityUtil.currentUserIdWithDefault())
                .setHealthServiceId(service.healthServiceId)
                .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault().toLong())
                .setId(AppIdUtil.nextId())
                .setActivationCode(body)
                .build()
            service.duringTime?.toLong()?.let { duringTime ->
                if (!patientHealthServiceList.any { it.patientId == AppSecurityUtil.currentUserIdWithDefault() })
                    data.expireDate = calculateExpireDate(duringTime, null)
                else {
                    val prevExpireDate =
                        patientHealthServiceList.filter { it.patientId == AppSecurityUtil.currentUserIdWithDefault() && it.expireDate?.let { expireDate -> expireDate >= LocalDateTime.now() } ?: true }
                            .getOrNull(0)?.expireDate
                    data.expireDate = calculateExpireDate(duringTime, prevExpireDate)
                }
            }
            patientHealthServiceTable.insertWithoutNull(data)
            result.add(
                ReceiveServiceResultInner(
                    data.id,
                    service.healthServiceId,
                    service.healthServiceCode,
                    service.healthServiceName,
                    data.expireDate
                )
            )
        }
        //发布订阅服务包事件
        eventPublisher.publishEvent(
            SubscribeHealthServiceEvent(
                this,
                serviceModels,
                AppSecurityUtil.currentUserIdWithDefault()
            )
        )
        return result
    }

    @Transactional
    override fun healthServiceSubscribePost(healthServiceId: BigInteger): Boolean {
        //获取当前用户ID
        val userIdWithDefault = AppSecurityUtil.currentUserIdWithDefault()
        //查询订阅的服务包是否存在
        val serviceModel =
            serviceTable.select().where(UpcsHealthServiceTable.HealthServiceId eq arg(healthServiceId)).findOne()
                ?: throw MsgException("服务包不存在")
        //查询本人已订阅信息
        val patientHealthServiceList =
            patientHealthServiceTable.select().where(UpcsPatientHealthServiceTable.HealthServiceId eq arg(healthServiceId))
                .where(UpcsPatientHealthServiceTable.PatientId eq arg(userIdWithDefault))
                .order(UpcsPatientHealthServiceTable.CreatedAt, Order.Desc).find()
        //订阅存储
        val data = UpcsPatientHealthService.builder()
            .setPatientId(userIdWithDefault)
            .setHealthServiceId(healthServiceId)
            .setCreatedBy(userIdWithDefault.toLong())
            .setId(AppIdUtil.nextId())
            .build().apply {
                serviceModel.duringTime?.toLong()?.let { duringTime ->
                    if (patientHealthServiceList.size == 0)
                        this.expireDate = calculateExpireDate(duringTime, null)
                    else {
                        val prevExpireDate = patientHealthServiceList
                            .filter {
                                it.expireDate?.let { expireDate -> expireDate >= LocalDateTime.now() } ?: true
                            }.getOrNull(0)?.expireDate
                        this.expireDate = calculateExpireDate(duringTime, prevExpireDate)
                    }
                }
            }
        patientHealthServiceTable.insertWithoutNull(data)
        //发布订阅服务包事件
        eventPublisher.publishEvent(SubscribeHealthServiceEvent(this, listOf(serviceModel), data.patientId))
        return true
    }

    fun calculateExpireDate(duringTime: Long, prevExpireDate: LocalDateTime?): LocalDateTime {
        val date = prevExpireDate ?: LocalDateTime.now()
        return if (duringTime >= 12) date.plusYears(duringTime / 12).plusDays(30 * (duringTime % 12))
        else date.plusDays(30 * duringTime)
    }

    private fun getHealthItemList(patientId: BigInteger): List<HealthManagementItem> {
        return dao.patientItemList(patientId).map {
            HealthManagementItem(
                it.healthManagementItemId, it.healthManagementItemCode, it.healthManagementItemName
            )
        }
    }


    private fun getHealthServiceList(patientId: BigInteger): List<HealthService> {
        val serviceList = dao.serviceList(patientId)
        //如果基础包没有订阅，补充订阅
        serviceList.filter { it.healthServiceCode == BASIC_SERVICE_CODE && it.signed != 1L }.getOrNull(0)?.let {
            healthServiceSubscribePost(
                it.healthServiceId
            )
        }
        return serviceList.map {
            HealthService(
                healthServiceId = it.healthServiceId,
                healthServiceCode = it.healthServiceCode,
                healthServiceName = it.healthServiceName,
                isSigned = if (it.healthServiceCode == BASIC_SERVICE_CODE) true else it.signed == 1L,
                duringTime = it.duringTime,
                expireDate = it.expire,
                activationDate = it.activationDate
            )
        }
    }

    private fun getHealthServiceActivationCodeList(
        code: String?,
        serviceIds: List<Id>?,
        createdUserIds: List<Id>?,
        status: HealthServiceActivationCodeStatus?,
        createdAtMin: LocalDateTime?,
        createdAtMax: LocalDateTime?,
    ): List<ActivationCodeListResult> {
        if ((serviceIds != null && serviceIds.isEmpty()) || (createdUserIds != null && createdUserIds.isEmpty()))
            return listOf()
        var regionCode: Id? = null
        var hospitalId: Id? = null
        if (AppSecurityUtil.jwtUser()?.isSuperAdmin() != true) {
            val user = AppSecurityUtil.jwtUser()
            regionCode = user?.regionIdSet?.firstOrNull()
            hospitalId = user?.orgIdSet?.firstOrNull()
            //非超级管理员，无授权不查询数据
            if (regionCode == null && hospitalId == null)
                return listOf()
        }
        val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return dao.activationCodeList(
            code,
            regionCode,
            hospitalId,
            createdUserIds,
            serviceIds,
            status == HealthServiceActivationCodeStatus.DEFINED,
            status == HealthServiceActivationCodeStatus.USED,
            status == HealthServiceActivationCodeStatus.FORBIDDEN,
            createdAtMin?.format(df),
            createdAtMax?.format(df)
        )
    }
}
