package com.bjknrt.message.board.event

import com.bjknrt.doctor.patient.management.api.MessageApi
import com.bjknrt.doctor.patient.management.vo.MessageStatus
import com.bjknrt.doctor.patient.management.vo.UpdateMessageStatusRequest
import com.bjknrt.message.board.vo.MessageNumberStatus
import org.springframework.stereotype.Component
import java.math.BigInteger


@Component
class SyncPatientMessageStatusEvent(
    val updateMessageRpcService: MessageApi
) {
    fun sync(
        fromId: BigInteger,
        toId: BigInteger,
        statusList: List<MessageNumberStatus>
    ) {

        val toStatus = statusList.first { it.id == toId }
        val fromStatus = statusList.first { it.id == fromId }

        updateMessageRpcService.updateMessageStatus(
            UpdateMessageStatusRequest(
                fromId,
                toId,
                MessageStatus.valueOf(fromStatus.messageStatus.value),
                fromStatus.messageNum,
                MessageStatus.valueOf(toStatus.messageStatus.value),
                toStatus.messageNum,
            )
        )
    }
}
