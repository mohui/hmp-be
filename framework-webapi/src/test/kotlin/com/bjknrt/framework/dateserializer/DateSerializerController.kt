package com.bjknrt.framework.dateserializer

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author wjy
 */
@SpringBootApplication(scanBasePackages = ["com.bjknrt"])
@RestController
class DateSerializerController {

    @RequestMapping("/serializer/dateTime2Date")
    fun dateTime2Date(@RequestBody obj: SerializerObj): SerializerObj {
        return obj
    }

    data class SerializerObj(
        @field:JsonProperty("date1")
        val date1: LocalDate,
        @field:JsonProperty("date2")
        val date2: LocalDate?,
        @field:JsonProperty("dateTime1")
        val dateTime1: LocalDateTime
    )
}

fun main(args: Array<String>) {
    runApplication<DateSerializerController>(*args)
}