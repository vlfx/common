package io.github.vlfx.common.spring

import io.github.vlfx.common.spring.JacksonUtils.JSON5
import io.github.vlfx.common.spring.JacksonUtils.jsonStringToObj
import io.github.vlfx.common.spring.JacksonUtils.toJsonString
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author vLfx
 * @date 2026/5/5 23:07
 */
class JacksonUtilsTest {


    @Test
    fun `JSON test`() {
        val json = "{}"
        val obj: Any? = json.jsonStringToObj()
        assert(obj != null)
    }

    @Test
    fun `JSON5 test`() {
        val json = """
            {
                id: 123,
                name: '张三', // 姓名
                sex: "男",
            }
        """.trimIndent()
        val obj: Any? = JSON5.readTree(json)
        assert(obj != null)
        assertEquals(obj.toJsonString(), """
            {"id":123,"name":"张三","sex":"男"}
        """.trimIndent())
    }
}