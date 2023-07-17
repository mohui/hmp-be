package com.bjknrt.framework.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigInteger
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Configuration
class ApiConfig {

    /**
     * ObjectMapper 统一增加配置
     */
    @Bean
    fun apiObjectMapperJsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        val clock = Clock.systemDefaultZone()
        val offset = clock.zone.rules.getOffset(clock.instant())
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder ->
            jacksonObjectMapperBuilder?.let {

                it.serializersByType(mapOf(
                    // Long 类型序列化
                    Long::class.java to object : JsonSerializer<Long>() {
                        override fun serialize(
                            value: Long?,
                            gen: JsonGenerator?,
                            serializers: SerializerProvider?
                        ) {
                            value?.let { v -> gen?.writeString(v.toString()) }
                        }
                    },
                    // BigInteger 类型序列化
                    BigInteger::class.java to object : JsonSerializer<BigInteger>() {
                        override fun serialize(
                            value: BigInteger?,
                            gen: JsonGenerator?,
                            serializers: SerializerProvider?
                        ) {
                            value?.let { v -> gen?.writeString(v.toString()) }
                        }
                    },
                    // LocalDateTime 序列化
                    LocalDateTime::class.java to object : JsonSerializer<LocalDateTime?>() {
                        override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
                            value?.let { v ->
                                gen?.writeString(OffsetDateTime.of(v, offset).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            }
                        }
                    }
                ))

                it.deserializersByType(mapOf(
                    // LocalDateTime 反序列化
                    LocalDateTime::class.java to object : JsonDeserializer<LocalDateTime?>() {
                        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
                            return p?.let { parse ->
                                toLocalDateTime(parse, offset)
                            }
                        }
                    },
                    // DateTime -> LocalDate 反序列化时区转换
                    LocalDate::class.java to object : JsonDeserializer<LocalDate?>() {
                        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {
                            return p?.let { parse ->
                                return try {
                                    toLocalDateTime(parse, offset)?.toLocalDate()
                                } catch (e: DateTimeParseException) {
                                    LocalDate.parse(parse.text)
                                }
                            }
                        }
                    }
                ))

            }
        }
    }

    private fun toLocalDateTime(parse: JsonParser, offset: ZoneOffset?): LocalDateTime? {
        val zonedDateTime = ZonedDateTime.parse(parse.text, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return LocalDateTime.ofEpochSecond(zonedDateTime.toEpochSecond(), zonedDateTime.nano, offset)
    }
}