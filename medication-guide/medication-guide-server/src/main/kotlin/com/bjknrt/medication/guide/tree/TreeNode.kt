package com.bjknrt.medication.guide.tree

import java.util.*

/**
 * 实现树结构
 *
 */
class TreeNode<T : Any>(data: T) : Iterable<TreeNode<T>?> {
    /**
     * 树节点
     */
    var data: T?

    /**
     * 父节点，根没有父节点
     */
    var parent: TreeNode<T>? = null

    /**
     * 子节点，叶子节点没有子节点
     */
    var children: MutableList<TreeNode<T>>

    /**
     * 保存了当前节点及其所有子节点，方便查询
     */
    private val elementsIndex: MutableList<TreeNode<T>>

    /**
     * 构造函数
     *
     */
    init {
        this.data = data
        children = LinkedList()
        elementsIndex = LinkedList()
        elementsIndex.add(this)
    }

    /**
     * 判断是否为根：根没有父节点
     *
     * @return
     */
    private val isRoot: Boolean
        get() = parent == null

    /**
     * 判断是否为叶子节点：子节点没有子节点
     *
     * @return
     */
    val isLeaf: Boolean
        get() = children.size == 0

    /**
     * 添加一个子节点
     *
     * @param child
     * @return
     */
    fun addChild(child: T): TreeNode<T> {
        val childNode = TreeNode(child)
        childNode.parent = this
        children.add(childNode)
        registerChildForSearch(childNode)
        return childNode
    }

    /**
     * 获取当前节点的层
     *
     * @return
     */
    val level: Int
        get() = if (isRoot) {
            0
        } else {
            parent!!.level + 1
        }

    /**
     * 递归为当前节点以及当前节点的所有父节点增加新的节点
     *
     * @param node
     */
    private fun registerChildForSearch(node: TreeNode<T>) {
        elementsIndex.add(node)
        if (parent != null) {
            parent!!.registerChildForSearch(node)
        }
    }

    /**
     * 从当前节点及其所有子节点中搜索某节点
     *
     * @param cmp
     * @return
     */
    fun findChildrenTreeNode(cmp: Comparable<T>): TreeNode<T>? {
        for (element in elementsIndex) {
            val elData: T? = element.data
            if (elData?.let { cmp.compareTo(it) } == 0) return element
        }
        return null
    }

    /**
     * 获取当前节点的迭代器
     *
     * @return
     */
    override fun iterator(): Iterator<TreeNode<T>> {
        return TreeNodeIterator(this)
    }

    override fun toString(): String {
        return if (data != null) data.toString() else "[tree data null]"
    }

    /**
     * 查询自身和父节点数据转换成集合
     */
    fun findCurrentAndParentNodeList(): MutableList<T?> {
        val list = mutableListOf<T?>()
        this.findCurrentAndParentNodeList(list, this)
        return list
    }

    private fun findCurrentAndParentNodeList(list: MutableList<T?>, currentNode: TreeNode<T>) {
        if (currentNode.parent != null) {
            list.add(currentNode.data)
            findCurrentAndParentNodeList(list, currentNode.parent!!)
        }
    }
}
