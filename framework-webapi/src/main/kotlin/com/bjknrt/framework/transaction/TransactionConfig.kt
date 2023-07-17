package com.bjknrt.framework.transaction

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.core.KotlinDetector
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute
import org.springframework.transaction.interceptor.TransactionAttribute
import org.springframework.transaction.interceptor.TransactionAttributeSource
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

@ConditionalOnClass(ProxyTransactionManagementConfiguration::class)
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class TransactionConfig : ProxyTransactionManagementConfiguration() {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    override fun transactionAttributeSource(): TransactionAttributeSource {
        return object : AnnotationTransactionAttributeSource() {
            override fun determineTransactionAttribute(element: AnnotatedElement): TransactionAttribute? {
                val transactionAttribute = super.determineTransactionAttribute(element) ?: return null
                val isKotlinClass = when (element) {
                    is Class<*> -> KotlinDetector.isKotlinType(element)
                    is Method -> KotlinDetector.isKotlinType(element.declaringClass)
                    else -> false
                }
                if (!isKotlinClass) return transactionAttribute
                return object : DelegatingTransactionAttribute(transactionAttribute) {
                    override fun rollbackOn(ex: Throwable): Boolean {
                        return super.rollbackOn(ex) || ex is Exception
                    }
                }
            }
        }
    }
}