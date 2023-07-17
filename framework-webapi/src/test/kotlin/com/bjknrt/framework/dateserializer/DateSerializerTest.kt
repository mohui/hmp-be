package com.bjknrt.framework.dateserializer

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.Charset
import java.time.*

/**
 * @author wjy
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DateSerializerTest {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper


    lateinit var mvc: MockMvc

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()
    }

    @Test
    fun testDateTime2LocalDate() {
        val clock = Clock.systemDefaultZone()
        val localOffset = clock.zone.rules.getOffset(clock.instant())

        val dateTime = OffsetDateTime.of(2022, 1, 3, 16, 22, 22, 0, ZoneOffset.UTC)
        val date = LocalDate.of(2022, 1, 3)

        val param = mapOf<String, Any>(
            "date1" to dateTime,
            "date2" to date,
            "dateTime1" to dateTime
        )
        val strParam = objectMapper.writeValueAsString(param)
        val result = mvc.perform(
            MockMvcRequestBuilders.post("/serializer/dateTime2Date")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                // .content("""{"date1":"2022-01-02T17:22:22.000Z","dateTime1":"2022-01-02T17:22:22.000Z"}""")
                .content(strParam)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString
        val resultObj = objectMapper.readValue(result, DateSerializerController.SerializerObj::class.java)

        val localDateTime = LocalDateTime.ofEpochSecond(dateTime.toEpochSecond(), dateTime.nano, localOffset)
        Assertions.assertEquals(localDateTime.toLocalDate(), resultObj.date1)
        Assertions.assertEquals(date, resultObj.date2)
        Assertions.assertEquals(localDateTime, resultObj.dateTime1)
    }


}