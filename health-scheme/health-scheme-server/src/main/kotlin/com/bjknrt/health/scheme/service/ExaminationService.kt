package com.bjknrt.health.scheme.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.scheme.HsManageDetailExaminationAdapter
import com.bjknrt.health.scheme.enums.ExaminationCodeEnum
import com.bjknrt.health.scheme.vo.AddCurrentSchemeExaminationAdapterParam
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 运动禁忌服务
 */
interface ExaminationService {
    /**
     * 根据患者ID、方案ID、问卷CODE查询问卷适配集合的接口
     * @param queryCurrentSchemeExaminationAdapterListParam 查询参数
     * @return List<HsManageDetailExaminationAdapter> 健康方案详情问卷适配集合
     */
    fun queryCurrentSchemeExaminationAdapterList(queryCurrentSchemeExaminationAdapterListParam: QueryCurrentSchemeExaminationAdapterListParam): List<HsManageDetailExaminationAdapter>


    /**
     * 同步患者当前方案的问卷选项
     *
     * 1. 先根据患者ID查询出有效的健康方案
     * 2. 根据患者ID, 方案ID, 问卷code删除
     * 3. 同步患者当前方案的问卷选项
     */
    fun deleteAndInsertSchemeExaminationAdapter(addCurrentSchemeExaminationAdapterParam: AddCurrentSchemeExaminationAdapterParam)

    /**
     * 同步患者当前方案的问卷选项(仅添加)
     */
    fun insertSchemeExaminationAdapter(manageDetailExaminationAdapters: List<HsManageDetailExaminationAdapter>)

    /**
     * QueryCurrentSchemeExaminationAdapterListParam
     * @param knPatientId 患者ID
     * @param knHealthManageSchemeId 方案ID
     * @param knExaminationPaperCodeSet 问卷CODE数组
     */
    data class QueryCurrentSchemeExaminationAdapterListParam(

        @field:Valid
        @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,

        @field:Valid
        @field:JsonProperty("knHealthManageSchemeId", required = true) val knHealthManageSchemeId: Id,

        @field:JsonProperty("knExaminationPaperCode", required = true) val knExaminationPaperCodeSet: Set<ExaminationCodeEnum>
    )
}