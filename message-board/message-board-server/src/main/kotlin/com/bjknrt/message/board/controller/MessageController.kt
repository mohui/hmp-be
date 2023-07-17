package com.bjknrt.message.board.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.message.board.MbMessageBoard
import com.bjknrt.message.board.MbMessageBoardTable
import com.bjknrt.message.board.api.MessageApi
import com.bjknrt.message.board.assembler.messageBoardToMessage
import com.bjknrt.message.board.event.SyncPatientMessageStatusEvent
import com.bjknrt.message.board.vo.*
import me.danwi.sqlex.core.query.*
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.util.*

@RestController("com.bjknrt.message.board.api.MessageController")
class MessageController(
    val mbMessageBoardTable: MbMessageBoardTable,
    val syncPatientMessageStatusEvent: SyncPatientMessageStatusEvent
) : AppBaseController(), MessageApi {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun addMessage(addMessageRequest: AddMessageRequest): Message {
        val fromId = addMessageRequest.fromId
        val toId = addMessageRequest.toId
        var messageBoard = MbMessageBoard.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnContent(addMessageRequest.content)
            .setKnFromId(fromId)
            .setKnToId(toId)
            .build()
        messageBoard = mbMessageBoardTable.save(messageBoard)

        syncPatient(fromId, toId)

        return messageBoardToMessage(messageBoard)
    }

    override fun getMessageNumberStatus(messageNumberStatusRequest: MessageNumberStatusRequest): List<MessageNumberStatus> {
        val messageList = getMessageList(messageNumberStatusRequest.fromId, messageNumberStatusRequest.toId)
        return messageNumberStatusRequest.fromId.map { buildStatus(it, messageNumberStatusRequest.toId, messageList) }
    }

    private fun getMessageList(fromIdList: List<BigInteger>, toId: BigInteger): List<MbMessageBoard> {
        return mbMessageBoardTable.select()
            .where(
                ((MbMessageBoardTable.KnFromId `in` fromIdList.map { it.arg }) and (MbMessageBoardTable.KnToId eq toId.arg))
                        or
                        ((MbMessageBoardTable.KnToId `in` fromIdList.map { it.arg }) and (MbMessageBoardTable.KnFromId eq toId.arg))
            )
            .find()
            .sortedBy { it.knMessageDatetime }
    }

    private fun buildStatus(fromId: BigInteger, key: BigInteger, list: List<MbMessageBoard>): MessageNumberStatus {
        // 回复的消息集合
        val fromMessageList =
            list.filter { it.knFromId == key && it.knToId == fromId }.sortedBy { it.knMessageDatetime }
        // 发送的消息集合
        val toMessageList = list.filter { it.knFromId == fromId && it.knToId == key }.sortedBy { it.knMessageDatetime }

        // 发送的消息不为空
        if (toMessageList.isNotEmpty()) {
            // 获取最后一次发送的消息
            val last = toMessageList.last()
            val status = last.knMessageStatus
            // 判断最后一次的发送消息的状态是不是未读
            if (status == MessageStatus.UNREAD.value) {
                return MessageNumberStatus(
                    key,
                    toMessageList.count { it.knMessageStatus == MessageStatus.UNREAD.value },
                    MessageStatus.UNREAD
                )
            }
            // 判断回复的消息是否为空，或者是回复上次的消息
            if (fromMessageList.isEmpty() || (last.knMessageDatetime > fromMessageList.last().knMessageDatetime)) {
                return MessageNumberStatus(
                    key,
                    0,
                    MessageStatus.READ
                )
            }
        }
        // 返回已读已回复状态
        return MessageNumberStatus(key, 0, MessageStatus.NONE)
    }


    override fun messageList(messageListRequest: MessageListRequest): List<Message> {
        return mbMessageBoardTable.select()
            .where(
                (
                        (MbMessageBoardTable.KnFromId eq messageListRequest.fromId) and
                                (MbMessageBoardTable.KnToId eq messageListRequest.toId)
                        ) or
                        (
                                (MbMessageBoardTable.KnFromId eq messageListRequest.toId) and
                                        (MbMessageBoardTable.KnToId eq messageListRequest.fromId))
            )
            .order(MbMessageBoardTable.KnMessageDatetime, Order.Asc)
            .find()?.map { messageBoardToMessage(it) } ?: Collections.emptyList()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateStatus(messageStatusRequest: MessageStatusRequest) {
        val fromId = messageStatusRequest.fromId
        val toId = messageStatusRequest.toId
        mbMessageBoardTable.update()
            .setKnMessageStatus(MessageStatus.READ.value)
            .where(
                (MbMessageBoardTable.KnFromId eq fromId)
                        and
                        (MbMessageBoardTable.KnToId eq toId)
                        and
                        (MbMessageBoardTable.KnMessageStatus eq MessageStatus.UNREAD.value)
            )
            .execute()

        syncPatient(fromId, toId)
    }


    private fun getMessageNumberStatus(fromId: BigInteger, toId: BigInteger): List<MessageNumberStatus> {
        val list = mutableListOf<MessageNumberStatus>()
        addStatus(list, fromId, toId)
        addStatus(list, toId, fromId)
        return list
    }

    private fun addStatus(list: MutableList<MessageNumberStatus>, fromId: BigInteger, toId: BigInteger) {
        val messageList = this.getMessageList(listOf(fromId), toId)
        if (messageList.isEmpty()) {
            list.add(MessageNumberStatus(toId, 0, MessageStatus.NONE))
        } else {
            list.add(buildStatus(fromId, toId, messageList))
        }
    }

    private fun syncPatient(fromId: BigInteger, toId: BigInteger) {
        val statusList = getMessageNumberStatus(fromId, toId)
        syncPatientMessageStatusEvent.sync(fromId, toId, statusList)
    }
}
