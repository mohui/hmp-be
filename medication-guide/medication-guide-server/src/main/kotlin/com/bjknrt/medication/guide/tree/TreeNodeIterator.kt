package com.bjknrt.medication.guide.tree

/**
 *
 * 迭代器
 *
 */
class TreeNodeIterator<T : Any>(private val treeNode: TreeNode<T>) : Iterator<TreeNode<T>> {
    internal enum class ProcessStages {
        ProcessParent, ProcessChildCurNode, ProcessChildSubNode
    }

    private var doNext: ProcessStages?
    private var next: TreeNode<T>? = null
    private val childrenCurNodeIter: Iterator<TreeNode<T>>
    private var childrenSubNodeIter: Iterator<TreeNode<T>>? = null

    init {
        doNext = ProcessStages.ProcessParent
        childrenCurNodeIter = treeNode.children.iterator()
    }

    override fun hasNext(): Boolean {
        if (doNext == ProcessStages.ProcessParent) {
            next = treeNode
            doNext = ProcessStages.ProcessChildCurNode
            return true
        }
        if (doNext == ProcessStages.ProcessChildCurNode) {
            return if (childrenCurNodeIter.hasNext()) {
                val childDirect = childrenCurNodeIter.next()
                childrenSubNodeIter = childDirect.iterator()
                doNext = ProcessStages.ProcessChildSubNode
                hasNext()
            } else {
                doNext = null
                false
            }
        }
        return if (doNext == ProcessStages.ProcessChildSubNode) {
            if (childrenSubNodeIter!!.hasNext()) {
                next = childrenSubNodeIter!!.next()
                true
            } else {
                next = null
                doNext = ProcessStages.ProcessChildCurNode
                hasNext()
            }
        } else false
    }

    override fun next(): TreeNode<T> {
        return next!!
    }

}