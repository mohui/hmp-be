package com.bjknrt.framework.util

import com.bjknrt.extension.LOGGER
import java.net.InetAddress
import java.net.UnknownHostException
import javax.servlet.http.HttpServletRequest

object AppIpAddressUtil {

    /**
     * 获取当前网络客户端ip
     * @param request
     * @return
     */
    fun getRemoteIpAdder(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        var ipAddress = xForwardedFor ?: request.remoteAddr

        if ("127.0.0.1" == ipAddress || "0:0:0:0:0:0:0:1" == ipAddress) {
            //根据网卡取本机配置的IP
            var inet: InetAddress? = null
            try {
                inet = InetAddress.getLocalHost()
            } catch (e: UnknownHostException) {
                LOGGER.warn("获取本机ip地址失败", e)
            }
            ipAddress = inet?.hostAddress
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        val delimiter = ","
        if (ipAddress.contains(delimiter)) {
            ipAddress = ipAddress.split(delimiter)[0]
        }

        return ipAddress
    }


}