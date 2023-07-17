package com.bjknrt.operation.log

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

// https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing
// https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testing-introduction
// https://www.testcontainers.org/quickstart/junit_5_quickstart/ https://spring.io/guides/gs/testing-web/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfigTest::class)
abstract class AbstractContainerBaseTest {

}