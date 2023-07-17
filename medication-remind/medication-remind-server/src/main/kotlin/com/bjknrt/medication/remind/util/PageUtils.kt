package com.bjknrt.medication.remind.util

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.common.exception.KatoException

/**
 * @author wjy
 */
object PageUtils {

    /**
     * 分页查询执行
     *
     * @param query 分页查询数据函数
     * @param exec  分页数据处理函数
     * @param isValidateTotalElement 是否校验总条数（总条数发生变化，停止进行）
     * @param <R>   处理数据类型
     * @return 处理数据页数
    </R> */
    fun <R> pageExec(query: (Long) -> PagedResult<R>, exec: (List<R>) -> Unit, allowedExecFailed: Boolean = true, isValidateTotalElement: Boolean = false): Long {
        var isFirstPage = true
        var totalElement = 0L
        var totalPage = 1L
        var currentPage = 1L
        while (currentPage <= totalPage) {
            // 执行分页查询
            val page: PagedResult<R> = query(currentPage)
            // 处理分页查询数据
            page._data
                .takeIf { !it.isNullOrEmpty() }
                ?.let {
                    if (allowedExecFailed) {
                        try {
                            exec(it)
                        } catch (e: Exception) {
                            LOGGER.warn("查询第${currentPage}页数据，执行报错", e)
                        }
                    } else {
                        exec(it)
                    }
                }
                ?: return --currentPage
            // 确认总页数
            if (isFirstPage) {
                totalPage = page.totalPage
                totalElement = page.total
                isFirstPage = false
            } else {
                if (isValidateTotalElement) {
                    if (totalElement != page.total) {
                        // 总记录数发生变化，可能新增数据加到之前同步过的页中，导致遗漏数据和重复同步
                        throw KatoException("分页执行过程中，总记录数发生变化")
                    }
                } else {
                    totalPage = page.totalPage
                    totalElement = page.total
                    isFirstPage = false
                }
            }
            LOGGER.debug(String.format("第%d页 处理完成", currentPage))

            // 添加页码递增
            ++currentPage
        }
        return totalPage
    }

}