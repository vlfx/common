package io.github.vlfx.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * @author vLfx
 * @date 2026/5/15
 */
class ReflectionExtendKtTest {

    // 测试数据类
    data class SourceClass(
        val name: String,
        val age: Int,
        val email: String? = null,
        val address: String? = null
    )

//    data class TargetClass(
//        val name: String,
//        val age: Int,
//        val email: String? = null,
//        val phone: String? = null
//    )
    data class TargetClass(
        val name: String,
    ){
        var age: Int = 0
        var email: String? = null
        var phone: String? = null
    }

    data class TargetClassWithVar(
        val name: String,
        val age: Int,
        var extra: String? = null
    )

    data class TargetClassNoDefault(
        val name: String,
        val age: Int
    )

    // 一个没有合适构造函数的测试类
    class NoConstructorClass

    @Test
    fun testCopyToBasic() {
        val source = SourceClass("张三", 25, "zhangsan@example.com", "北京市")
        val target = source.copyTo<TargetClass>()

        assertEquals("张三", target.name)
        assertEquals(25, target.age)
        assertEquals("zhangsan@example.com", target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithNullableTrue() {
        val source = SourceClass("李四", 30, null, null)
        val target = source.copyTo<TargetClass>(nullable = true)

        assertEquals("李四", target.name)
        assertEquals(30, target.age)
        assertNull(target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithNullableFalse() {
        val source = SourceClass("王五", 35, "wangwu@example.com", null)
        val target = source.copyTo<TargetClass>(nullable = false)

        assertEquals("王五", target.name)
        assertEquals(35, target.age)
        assertEquals("wangwu@example.com", target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithReplaceParams() {
        val source = SourceClass("赵六", 40, "zhaoliu@example.com", "上海市")
        val target = source.copyTo<TargetClass>(
            replaceParams = mapOf("phone" to "13800138000")
        )

        assertEquals("赵六", target.name)
        assertEquals(40, target.age)
        assertEquals("zhaoliu@example.com", target.email)
        assertEquals("13800138000", target.phone)
    }

    @Test
    fun testCopyToWithReplaceParamsOverride() {
        val source = SourceClass("孙七", 45, "sunqi@example.com", "广州市")
        val target = source.copyTo<TargetClass>(
            replaceParams = mapOf("name" to "新名字", "age" to 50)
        )

        assertEquals("新名字", target.name)
        assertEquals(50, target.age)
        assertEquals("sunqi@example.com", target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithIgnoreParams() {
        val source = SourceClass("周八", 50, "zhouba@example.com", "深圳市")
        val target = source.copyTo<TargetClass>(
            ignoreParams = listOf("email", "address")
        )

        assertEquals("周八", target.name)
        assertEquals(50, target.age)
        assertNull(target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithReplaceParamsAndIgnoreParams() {
        val source = SourceClass("吴九", 55, "wujiu@example.com", "杭州市")
        val target = source.copyTo<TargetClass>(
            replaceParams = mapOf("phone" to "13900139000"),
            ignoreParams = listOf("address")
        )

        assertEquals("吴九", target.name)
        assertEquals(55, target.age)
        assertEquals("wujiu@example.com", target.email)
        assertEquals("13900139000", target.phone)
    }

    @Test
    fun testCopyToWithNullableAndReplaceParams() {
        val source = SourceClass("郑十", 60, null, "成都市")
        val target = source.copyTo<TargetClass>(
            replaceParams = mapOf("phone" to "13700137000"),
            nullable = false
        )

        assertEquals("郑十", target.name)
        assertEquals(60, target.age)
        assertNull(target.email)
        assertEquals("13700137000", target.phone)
    }

    @Test
    fun testCopyToWithVarProperty() {
        val source = SourceClass("钱十一", 65, "qianshiyi@example.com", "武汉市")
        val target = source.copyTo<TargetClassWithVar>()

        assertEquals("钱十一", target.name)
        assertEquals(65, target.age)
        assertNull(target.extra)
    }

    @Test
    fun testCopyToWithVarPropertyAndReplace() {
        val source = SourceClass("孙十二", 70, "sunshier@example.com", "西安市")
        val target = source.copyTo<TargetClassWithVar>(
            replaceParams = mapOf("extra" to "额外信息")
        )

        assertEquals("孙十二", target.name)
        assertEquals(70, target.age)
        assertEquals("额外信息", target.extra)
    }

//    @Test
//    fun testCopyToNoConstructor() {
//        val source = SourceClass("测试", 25, "test@example.com", "测试地址")
//
//        // 测试没有构造函数的情况
//        assertFailsWith<IllegalArgumentException> {
//            source.copyTo<NoConstructorClass>()
//        }
//    }

    @Test
    fun testCopyToWithNullValuesAndNullableFalse() {
        val source = SourceClass("测试用户", 30, null, null)
        
        // 当 nullable 为 false 时，null 值不会被复制
        // 目标类需要有默认值或者可为 null
        val target = source.copyTo<TargetClass>(nullable = false)
        
        assertEquals("测试用户", target.name)
        assertEquals(30, target.age)
        assertNull(target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToWithAllNullValues() {
        val source = SourceClass("全空测试", 35, null, null)
        val target = source.copyTo<TargetClass>(nullable = true)

        assertEquals("全空测试", target.name)
        assertEquals(35, target.age)
        assertNull(target.email)
        assertNull(target.phone)
    }

    @Test
    fun testCopyToReplaceParamsWithNull() {
        val source = SourceClass("替换null测试", 40, "original@example.com", "原始地址")
        val target = source.copyTo<TargetClass>(
            replaceParams = mapOf("email" to null)
        )

        assertEquals("替换null测试", target.name)
        assertEquals(40, target.age)
        assertNull(target.email)
        assertNull(target.phone)
    }
}
