package com.bjknrt.message.board.api

import com.bjknrt.doctor.patient.management.api.MessageApi
import com.bjknrt.doctor.patient.management.vo.UpdateMessageStatusRequest
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.message.board.AbstractContainerBaseTest
import com.bjknrt.message.board.MbMessageBoard
import com.bjknrt.message.board.MbMessageBoardTable
import com.bjknrt.message.board.vo.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.*

@AutoConfigureMockMvc
class MessageTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var messageBoardTable: MbMessageBoardTable

    @MockBean
    lateinit var updateMessageRpcService: MessageApi

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build();

    }

    @Transactional
    @Test
    fun addTest() {
        val fromId = AppIdUtil.nextId()
        val toId = AppIdUtil.nextId()

        val content = "我是第一次留言。"
        val request = AddMessageRequest(fromId, toId, content)

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/message/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val message = objectMapper.readValue(resultString, Message::class.java)

        val messageBoard = messageBoardTable.select()
            .where(MbMessageBoardTable.KnFromId eq fromId.arg)
            .where(MbMessageBoardTable.KnToId eq toId.arg)
            .findOne()

        Assertions.assertEquals(true, messageBoard != null)
        Assertions.assertEquals(content, messageBoard?.knContent)
        Assertions.assertEquals(message.content, messageBoard?.knContent)
        Assertions.assertEquals(message.toId, messageBoard?.knToId)
        Assertions.assertEquals(message.fromId, messageBoard?.knFromId)

    }

    @Transactional
    @Test
    fun messageListTest() {

        val fromId = AppIdUtil.nextId()
        val toId = AppIdUtil.nextId()

        Mockito.doNothing().`when`(updateMessageRpcService).updateMessageStatus(
            UpdateMessageStatusRequest(
                fromId,
                toId,
                com.bjknrt.doctor.patient.management.vo.MessageStatus.valueOf(MessageStatus.UNREAD.value),
                1,
                com.bjknrt.doctor.patient.management.vo.MessageStatus.valueOf(MessageStatus.NONE.value),
                0,
            )
        )


        messageBoardTable.save(
            mbMessageBoard("哈喽啊", fromId, toId)
        )

        val request = MessageListRequest(fromId, toId)

        val result = mvc.perform(
            MockMvcRequestBuilders.post("/message/list")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString


        val list = objectMapper.readValue(
            result,
            object : TypeReference<List<Message>>() {}
        )

        Assertions.assertEquals(1, list.size)
        Assertions.assertEquals("哈喽啊", list[0].content)
    }

    @Test
    fun messageStatusTest() {
        val id = AppIdUtil.nextId()
        val fromId = AppIdUtil.nextId()
        val toId = AppIdUtil.nextId()

        messageBoardTable.save(
            mbMessageBoard("哈喽啊", fromId, toId, id)
        )


        Mockito.doNothing().`when`(updateMessageRpcService).updateMessageStatus(
            UpdateMessageStatusRequest(
                fromId,
                toId,
                com.bjknrt.doctor.patient.management.vo.MessageStatus.valueOf(MessageStatus.UNREAD.value),
                1,
                com.bjknrt.doctor.patient.management.vo.MessageStatus.valueOf(MessageStatus.NONE.value),
                0,
            )
        )

        val request = MessageStatusRequest(fromId, toId)

        mvc.perform(
            MockMvcRequestBuilders.post("/message/status")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        val messageBoard = messageBoardTable.findByKnId(id)

        Assertions.assertEquals(true, messageBoard != null)
        Assertions.assertEquals(MessageStatus.READ.value, messageBoard?.knMessageStatus)
    }

    @Transactional
    @Test
    fun messageNumberTest() {
        val fromId = AppIdUtil.nextId()
        val toId = AppIdUtil.nextId()
        messageBoardTable.save(
            mbMessageBoard("哈喽啊", fromId, toId)
        )
        Thread.sleep(1000)
        messageBoardTable.save(
            mbMessageBoard("您好，有什么能帮到您？", toId, fromId)
        )

        val request = MessageNumberStatusRequest(Collections.singletonList(fromId), toId)

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/message/number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, object : TypeReference<List<MessageNumberStatus>>() {})

        Assertions.assertEquals(1, response.size)
        Assertions.assertEquals(MessageStatus.UNREAD, response[0].messageStatus)


    }

    private fun mbMessageBoard(
        content: String,
        fromId: BigInteger,
        toId: BigInteger,
        id: BigInteger? = null
    ): MbMessageBoard = MbMessageBoard.builder()
        .setKnId(id ?: AppIdUtil.nextId())
        .setKnContent(content)
        .setKnFromId(fromId)
        .setKnToId(toId)
        .build()
}

