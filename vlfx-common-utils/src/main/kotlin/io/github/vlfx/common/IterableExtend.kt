@file:Suppress("unused")

package io.github.vlfx.common

import io.github.vlfx.common.annotation.CustomExtend
import io.github.vlfx.common.annotation.GlobalMarker

/**
 * @author vLfx
 * @date 2025/5/29 12:19
 */
@GlobalMarker
object IterableUtils {
    /**
     * 列表转树形结构
     */
    fun <T, ID> iterable2tree(iterable: Iterable<T>, idKey: (T) -> ID, pidKey: (T) -> ID, rootId: ID? = null): List<Node<T, ID>> {
        val nodes = iterable.map { Node(it, idKey(it), pidKey(it)) }
        val map = nodes.associateBy { it.id }
        val rootNodes = mutableListOf<Node<T, ID>>()
        nodes.forEach { node ->
            if (node.pid == rootId) {
                rootNodes.add(node)
            } else {
                map[node.pid]?.children?.add(node)
            }
        }
        return rootNodes
    }

    /**
     * 递归处理树形结构
     */
    fun <T, ID> recursionHandleTree(
        iterable: Iterable<Node<T, ID>>,
        level: Int,
        handler: (level: Int, node: Node<T, ID>) -> Unit
    ) {
        iterable.forEach {
            handler(level, it)
            recursionHandleTree(it.children, level + 1, handler)
        }
    }

    /**
     * 递归处理树形结构，返回每层节点集合
     */
    fun <T, ID> recursionToLevelMap(iterable: Iterable<Node<T, ID>>): Map<Int, List<Node<T, ID>>> {
        val map = mutableMapOf<Int, List<Node<T, ID>>>()
        recursionHandleTree(iterable, 0) { level, node ->
            map[level] = map[level]?.let { it + node } ?: listOf(node)
        }
        return map
    }
}

/**
 * 列表转树形结构DTO
 * 辅助类
 */
data class Node<T, ID>(
    val data: T,
    val id: ID,
    val pid: ID,
    val children: MutableList<Node<T, ID>> = mutableListOf(),
)


/**
 * Boolean列表是否全部为false
 */
@CustomExtend
fun Iterable<Boolean>?.allFalseOrNull(): Boolean = this?.all { !it } ?: true

/**
 * 列表转树形结构
 *
 */
@CustomExtend
fun <T, ID> Iterable<T>.toTree(idKey: (T) -> ID, pidKey: (T) -> ID, rootId: ID? = null): List<Node<T, ID>> =
    IterableUtils.iterable2tree(this, idKey, pidKey, rootId)

/**
 * 树形结构处理
 * @param level 定义根层级起始index
 */
@CustomExtend
fun <T, ID> Iterable<Node<T, ID>>.handleTree(
    level: Int = 0,
    handler: (level: Int, node: Node<T, ID>) -> Unit
) = IterableUtils.recursionHandleTree(this, level, handler)


/**
 * 树形结构转Map,返回每层节点集合
 */
@CustomExtend
fun <T, ID> Iterable<Node<T, ID>>.toLevelMap() = IterableUtils.recursionToLevelMap(this)

