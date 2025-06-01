@file:Suppress("unused")

package com.vlfx.com.vlfx.common

import com.vlfx.common.annotation.DiyExtend

/**
 * @author vLfx
 * @date 2025/5/29 12:19
 */

object ArraysUtils {
    /**
     * 列表转树形结构
     */
    fun <T, ID> list2tree(list: Iterable<T>, idKey: (T) -> ID, pidKey: (T) -> ID, rootId: ID? = null): List<RecursionObject<T, ID>> {
        val nodes = list.map { RecursionObject(it, idKey(it), pidKey(it)) }
        val map = nodes.associateBy { it.id }
        val rootNodes = mutableListOf<RecursionObject<T, ID>>()
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
        list: Iterable<RecursionObject<T, ID>>,
        level: Int,
        handler: (level: Int, node: RecursionObject<T, ID>) -> Unit
    ) {
        list.forEach {
            handler(level, it)
            recursionHandleTree(it.children, level + 1, handler)
        }
    }

    /**
     * 递归处理树形结构，返回每层节点集合
     */
    fun <T, ID> recursionToLevelMap(list: Iterable<RecursionObject<T, ID>>): Map<Int, List<RecursionObject<T, ID>>> {
        val map = mutableMapOf<Int, List<RecursionObject<T, ID>>>()
        recursionHandleTree(list, 0) { level, node ->
            map[level] = map[level]?.let { it + node } ?: listOf(node)
        }
        return map
    }
}

/**
 * 列表转树形结构DTO
 * 辅助类
 */
data class RecursionObject<T, ID>(
    val data: T,
    val id: ID,
    val pid: ID,
    val children: MutableList<RecursionObject<T, ID>> = mutableListOf(),
)


/**
 * Boolean列表是否全部为false
 */
@DiyExtend
fun Iterable<Boolean>?.allFalseOrNull(): Boolean = this?.all { !it } ?: true

/**
 * 列表转树形结构
 *
 */
@DiyExtend
fun <T, ID> Iterable<T>.toTree(idKey: (T) -> ID, pidKey: (T) -> ID, rootId: ID? = null): List<RecursionObject<T, ID>> =
    ArraysUtils.list2tree(this, idKey, pidKey, rootId)

/**
 * 树形结构处理
 * @param level 定义根层级起始index
 */
@DiyExtend
fun <T, ID> Iterable<RecursionObject<T, ID>>.handleTree(
    level: Int = 0,
    handler: (level: Int, node: RecursionObject<T, ID>) -> Unit
) = ArraysUtils.recursionHandleTree(this, level, handler)


/**
 * 树形结构转Map,返回每层节点集合
 */
@DiyExtend
fun <T, ID> Iterable<RecursionObject<T, ID>>.toLevelMap() = ArraysUtils.recursionToLevelMap(this)

