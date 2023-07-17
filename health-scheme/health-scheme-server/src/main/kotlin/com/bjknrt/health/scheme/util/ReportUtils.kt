package com.bjknrt.health.scheme.util

import com.bjknrt.health.scheme.entity.AbnormalDataAlertMsg
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

val objectMapper = ObjectMapper()
val javaType = object : TypeReference<List<AbnormalDataAlertMsg>>() {}

fun abnormalDataAlertMsgToList(abnormalDataAlertMsg: String?): MutableList<AbnormalDataAlertMsg> {
    return if (abnormalDataAlertMsg.isNullOrEmpty()) mutableListOf() else
        objectMapper.readValue(abnormalDataAlertMsg, javaType).toMutableList()
}

fun abnormalDataAlertMsgListToString(list: List<AbnormalDataAlertMsg>): String {
    return objectMapper.writeValueAsString(list)
}
