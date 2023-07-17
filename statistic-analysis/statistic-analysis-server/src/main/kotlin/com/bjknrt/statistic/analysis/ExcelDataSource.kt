package com.bjknrt.statistic.analysis

import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import org.apache.poi.ss.usermodel.CellType
import java.io.OutputStream
import kotlin.math.max

class ExcelHeaderNode(var content: String? = null, vararg val children: ExcelHeaderNode) {
    init {
        //填充父级节点
        children.forEach { it.parent = this }
    }

    //父节点
    private var parent: ExcelHeaderNode? = null

    //获取整个节点的起始行偏移
    val rowOffset: Int by lazy {
        //如果父节点为null,则返回0
        val parent = this.parent ?: return@lazy 0
        //否则就是父节点行偏移+父节点自有内容的行高
        parent.rowOffset + parent.selfRowHeight
    }

    //获取整个节点的起始列偏移
    val colOffset: Int by lazy {
        //如果父节点为null,则返回0
        val parent = this.parent ?: return@lazy 0
        //找到自己的index
        val index = parent.children.indexOf(this)
        //如果自己是第一个元素,则列偏移等于父节点的列偏移
        //否则等于前一个兄弟的列偏移+列宽度
        if (index == 0)
            parent.colOffset
        else
            parent.children[index - 1].colOffset + parent.children[index - 1].colWidth
    }

    //自适应行高,因为自己可能被兄弟节点撑高=取最大(自己的行高,父节点的行高-父节点的自有内容高度)
    private val fitRowHeight: Int by lazy {
        val parent = this.parent ?: return@lazy rowHeight
        max(rowHeight, (parent.fitRowHeight - parent.selfRowHeight))
    }

    //获取整个节点的行高=子节点行高+1?
    val rowHeight: Int by lazy {
        if (!content.isNullOrBlank())
            children.rowHeight + 1
        else
            children.rowHeight
    }

    //获取整个节点的列宽=取最大(子节点的列宽和, 1)
    val colWidth: Int
        get() = max(children.colWidth, 1)

    //获取自有内容的行高=整体行高-子内容的行高
    val selfRowHeight: Int
        get() = fitRowHeight - children.rowHeight

    //获取自有内容的列宽=整个节点的列宽
    val selfColWidth: Int
        get() = colWidth
}

//所有子内容的行高=子内容中最大的行高
val Array<out ExcelHeaderNode>.rowHeight: Int
    inline get() = this.maxOfOrNull { it.rowHeight } ?: 0

//所有子内容的列宽=子内容列宽相加
val Array<out ExcelHeaderNode>.colWidth: Int
    inline get() = this.sumOf { it.colWidth }

fun ExcelWriter.merge(node: ExcelHeaderNode) {
    if (!node.content.isNullOrBlank()) {
        if (node.selfRowHeight > 1 || node.colWidth > 1)
            this.merge(
                node.rowOffset, node.rowOffset + node.selfRowHeight - 1,
                node.colOffset, node.colOffset + node.selfColWidth - 1,
                node.content,
                false
            )
        else
            this.writeCellValue(node.colOffset, node.rowOffset, node.content)
    }
    //合并自己的子节点
    node.children.forEach { merge(it) }
}

/**
 * 封装的单个工作表内容
 * @param dataRows 行对应的列数据
 */
class ExcelSheetTable(val sheetName: String, val headerNode: ExcelHeaderNode? = null, val dataRows: List<List<Any>>)

/**
 * 封装的excel文件对应的数据源
 */
class ExcelDataSource(private vararg val sheetTables: ExcelSheetTable) {
    fun writeTo(outputStream: OutputStream) {
        if (sheetTables.isNotEmpty()) {
            val firstSheetName = sheetTables.firstNotNullOf { it.sheetName }
            val writer = ExcelUtil.getWriterWithSheet(firstSheetName)
            for (table in sheetTables) {
                if (table.sheetName != firstSheetName) {
                    writer.setSheet(table.sheetName)
                }
                if (table.headerNode != null) {
                    writer.merge(table.headerNode)
                    //定位到新的行
                    writer.currentRow = table.headerNode.rowHeight
                }
                writer.write(table.dataRows)
                val sheet = writer.sheet
                //设置当前表格自适应宽度
                for (columnNum in 0..(table.dataRows.getOrNull(0)?.size ?: 0)) {
                    var columnWidth: Int = sheet.maxOf { currentRow ->
                        currentRow.getCell(columnNum).let { cell ->
                            if (cell != null && cell.cellType == CellType.STRING) cell.stringCellValue?.toByteArray()?.size
                                ?: 0
                            else 0
                        }
                    }
                    if (columnWidth < sheet.getColumnWidth(columnNum) / 256)
                        columnWidth = sheet.getColumnWidth(columnNum) / 256
                    sheet.setColumnWidth(columnNum, columnWidth * 256)
                }
            }
            writer.flush(outputStream, false)
            writer.close()
        }
    }
}