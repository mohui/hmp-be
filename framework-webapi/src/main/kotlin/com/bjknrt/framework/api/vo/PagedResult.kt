package com.bjknrt.framework.api.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class PagedResult<T>(
    @field:JsonProperty("totalPage", required = true) var totalPage: Long,

    @field:JsonProperty("pageSize", required = true) val pageSize: Long,

    @field:JsonProperty("pageNo", required = true) val pageNo: Long,

    @field:JsonProperty("total", required = true) val total: Long,

    @field:Valid
    @field:JsonProperty("data", required = false) val _data: List<T>? = listOf()
) {
    companion object {
        fun <T> emptyPaged(page: Page, data: List<T> = listOf()): PagedResult<T> {
            return PagedResult(0, page.pageSize, page.pageNo, 0, data)
        }

        fun <T> fromDbPaged(page: me.danwi.sqlex.core.type.PagedResult<T>): PagedResult<T> {
            return PagedResult(page.totalPage, page.pageSize, page.pageNo, page.total, page.data)
        }

        fun <S, T> fromDbPaged(page: me.danwi.sqlex.core.type.PagedResult<S>, converter: (S) -> T): PagedResult<T> {
            return PagedResult(page.totalPage, page.pageSize, page.pageNo, page.total, page.data?.map(converter))
        }
    }
}