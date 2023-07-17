package com.bjknrt.statistic.analysis.utils

/**
 * 过滤树形结构数据
 * @param nodeList 数据
 * @param parent 根节点的父节点为null或指定parent后返回数据，否则返回空数组
 */
fun <T, I> filterTreeNode(
    nodeList: List<TreeNode<T, I>>,
    parent: T? = null
): List<TreeNode<T, I>> {
    val map = nodeList.associateBy { it.id }
    val resultList = mutableListOf<TreeNode<T, I>>()
    for (node in nodeList) {
        if (node.parent == parent) {
            resultList.add(node)
        } else {
            val parentNode = map[node.parent]
            parentNode?.child?.add(node)
        }
    }
    return resultList.toList()
}

/**
 * 转换数据为树结构
 * @param dataList 数据
 * @param idKey id转换函数
 * @param nameKey 名称转换函数
 * @param parentKey 父节点转换函数
 */
fun <T, V> transformTree(
    dataList: List<T>,
    idKey: (T) -> V,
    nameKey: (T) -> String,
    parentKey: (T) -> V
): List<TreeNode<V, T>> {
    return dataList.map { TreeNode(idKey(it), nameKey(it), parentKey(it),it) }
}

data class TreeNode<T, I>(
    val id: T,
    val name: String,
    val parent: T?,
    val sourceData: I?,
    val child: MutableList<TreeNode<T, I>> = mutableListOf()
)

