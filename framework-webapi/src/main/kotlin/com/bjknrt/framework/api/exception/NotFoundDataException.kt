package com.bjknrt.framework.api.exception

import me.danwi.kato.common.exception.KatoCommonException


/**
 * 查询不到数据异常  查询数据为空时使用
 */
class NotFoundDataException(msg: String) : KatoCommonException(msg) {

}