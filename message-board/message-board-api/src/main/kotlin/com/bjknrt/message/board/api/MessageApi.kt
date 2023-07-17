package com.bjknrt.message.board.api

import com.bjknrt.message.board.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-message-board.kato-server-name:\${spring.application.name}}", contextId = "MessageApi2")
@Validated
interface MessageApi {


    /**
     * 新增留言
     *
     *
     * @param addMessageRequest
     * @return Message
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/message/add"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addMessage(@Valid addMessageRequest: AddMessageRequest): Message


    /**
     * 查询留言数量和状态
     *
     *
     * @param messageNumberStatusRequest
     * @return List<MessageNumberStatus>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/message/number"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getMessageNumberStatus(@Valid messageNumberStatusRequest: MessageNumberStatusRequest): List<MessageNumberStatus>


    /**
     * 查看留言
     *
     *
     * @param messageListRequest
     * @return List<Message>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/message/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun messageList(@Valid messageListRequest: MessageListRequest): List<Message>


    /**
     * 更新留言状态
     *
     *
     * @param messageStatusRequest
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/message/status"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateStatus(@Valid messageStatusRequest: MessageStatusRequest): Unit
}
