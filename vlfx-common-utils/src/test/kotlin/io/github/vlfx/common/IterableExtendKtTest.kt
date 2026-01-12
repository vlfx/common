package io.github.vlfx.common


import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author vLfx
 * @date 2025/5/29 12:53
 */
class IterableExtendKtTest {

    @Test
    fun nullOrAllFalse() {

        val list1: List<Boolean> = listOf(false, false, false)
        val list2: List<Boolean> = listOf(false, false, false, true, false)
        val list3: List<Boolean>? = null

        assertEquals(true, list1.allFalseOrNull())
        assertEquals(false, list2.allFalseOrNull())
        assertEquals(true, list3.allFalseOrNull())
    }

    @Test
    fun toTree() {

        val list = listOf(
            Pair(1, 0),
            Pair(2, 1),
            Pair(3, 0),
            Pair(4, 2),
            Pair(5, 1),
        )

        val tree = list.toTree({ it.first }, { it.second }, 0)
        val ro = mutableListOf(
            Node(
                1 to 0,
                1,
                0,
                mutableListOf(
                    Node(
                        2 to 1,
                        2,
                        1,
                        mutableListOf(Node(4 to 2, 4, 2, mutableListOf()))
                    ),
                    Node(
                        5 to 1,
                        5,
                        1,
                    ),
                )
            ),
            Node(
                3 to 0,
                3,
                0,
            )
        )
        assertEquals(ro, tree)
    }

    @Test
    fun handleTree() {
        val list = listOf(
            Pair(1, 0),
            Pair(2, 1),
            Pair(3, 0),
            Pair(4, 2),
            Pair(5, 1),
        )

        val tree = list.toTree({ it.first }, { it.second }, 0)
        tree.handleTree { level, node ->
//            println("level: $level, node: $node")
            if (level == 2) {
                assertEquals(4, node.id)
            }
            if (level == 1 && node.id == 2) {
                assertEquals(1, node.children.size)
                assertEquals(4, node.children[0].id)
            }
        }
    }

    @Test
    fun toLevelMap() {

        val list = listOf(
            Pair(1, 0),
            Pair(2, 1),
            Pair(3, 0),
            Pair(4, 2),
            Pair(5, 1),
        )

        val tree = list.toTree({ it.first }, { it.second }, 0)
        val map = tree.toLevelMap()
        assertEquals(2, map[0]?.size)
        assertEquals(2, map[1]?.size)
        assertEquals(1, map[2]?.size)
        assertEquals(5, map[1]?.get(1)?.id)
    }
}