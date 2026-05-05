@file:Suppress("unused")

package io.github.vlfx.common.spring

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.github.vlfx.common.spring.JacksonUtils.fromJsonString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * json工具类
 * @author vLfx
 * @date 2025/9/28 11:59
 */
object JacksonUtils {

    /**
     * 传统的json序列化与反序列化 增加了一些自定义功能
     */
    val JSON by lazy {
        ObjectMapper().setCustomJsonConfig()
    }

    /**
     * 并不是完整的json5支持 并且增加了一些自定义功能
     *
     * 不支持的 JSON5 特性
     *
     * 尽管 Jackson 提供了许多灵活的解析选项，但仍有一些 JSON5 特性无法支持：
     * 十六进制数字：Jackson 不支持解析十六进制格式的数字。
     * 数字尾随小数点：例如 123. 的格式无法被解析。
     * 数字前的加号：例如 +123 的格式不被支持。
     * 额外的空白字符：Jackson 无法处理 JSON5 中允许的多余空格。
     * 替代方案
     *
     * 如果需要完整支持 JSON5，可以考虑使用专门的 JSON5 库，例如 json5（适用于 JavaScript 环境）或其他 JVM 上的 JSON5 解析库，如 Jankson。Jankson 是一个支持 JSON5 和 HJSON 的解析器，能够保留注释和字段顺序，非常适合需要完整 JSON5 支持的场景。
     */
    val JSON5 by lazy {
        val jsonFactory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES) // 支持未加引号的字段名
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA) // 支持尾随逗号
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES) // 支持单引号字符串
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER) // 支持转义字符
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS) // 支持非数字值（如 NaN、Infinity）
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS) // 支持单行和多行注释
            .enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS) // 支持数字前导小数点
            .build()
        ObjectMapper(jsonFactory).setCustomJsonConfig()
    }

    private fun ObjectMapper.setCustomJsonConfig(): ObjectMapper {
        findAndRegisterModules()
        //忽略json反序列化时多余的字段
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        //允许无字段的bean序列化 如："body":{}
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        registerModule(object : SimpleModule() {
            init {
                addDeserializer(
                    LocalDateTime::class.java,
                    LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
                addSerializer(
                    LocalDateTime::class.java,
                    LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
//                addDeserializer(String::class.java,FlexibleStringDeserializer())
            }
        })
        return this
    }

//    /**
//     * 自定义反序列化器，处理字段可能是字符串、空字符串或空对象的情况
//     */
//    class FlexibleStringDeserializer : JsonDeserializer<String>() {
//        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
//            val node = p.codec.readTree<JsonNode>(p)
//            return when {
//                node.isTextual -> node.asText()
//                node.isObject -> "" // 空对象 {} 返回空字符串
//                node.isNull -> ""
//                else -> ""
//            }
//        }
//    }


    /**
     * 对象转json字符串
     */
    fun <T> T.toJsonString(): String {
        return JSON.writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 忽略值为null的字段
     */
    fun <T> T.toJsonStringNonNull(): String {
        return JSON.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 以漂亮的格式输出
     */
    fun <T> T.toJsonStringPretty(): String {
        return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 忽略值为null的字段
     * 以漂亮的格式输出
     */
    fun <T> T.toJsonStringNonNullPretty(): String {
        return JSON.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter()
            .writeValueAsString(this)
    }

    /**
     * json字符串转换为对象
     * 如下4种用法：
     *         val objA = JacksonUtils.fromJsonString<Data>(str)
     *         val objB = str.jsonStringToObj<Data>()
     *         val objC: Data = str.jsonStringToObj()
     *         val objD: Data = JacksonUtils.fromJsonString(str)
     */
    inline fun <reified T> fromJsonString(json: String): T {
        return JSON.readValue(json, T::class.java)
    }

    fun <T> fromJsonString(json: String, clazz: Class<T>): T {
        return JSON.readValue(json, clazz)
    }

    /**
     * 同fromJsonString(json: String)
     * @see fromJsonString
     */
    inline fun <reified T> String.jsonStringToObj(): T {
        return JSON.readValue(this, T::class.java)
    }
}