package com.bjknrt.framework.api

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 后期统一扩展使用
 */
abstract class AppBaseController {

    protected fun getRequestContext(): ServletRequestAttributes {
        return RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    }

    protected fun getRequest(): HttpServletRequest {
        return getRequestContext().request
    }

    protected fun getResponse(): HttpServletResponse? {
        return getRequestContext().response
    }
}