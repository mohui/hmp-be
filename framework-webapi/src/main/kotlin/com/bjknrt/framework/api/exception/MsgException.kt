package com.bjknrt.framework.api.exception

import me.danwi.kato.common.exception.KatoCommonException


/**
 * 此异常msg会在客户端以notify的形式弹出，服务端调用可以try-catch处理
 */
class MsgException(msg: String) : KatoCommonException(msg) {

}