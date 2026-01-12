@file:Suppress("unused")
package io.github.vlfx.common.spring

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.github.vlfx.common.annotation.CustomExtend
import io.github.vlfx.common.annotation.GlobalMarker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * json工具类
 * @author vLfx
 * @date 2025/9/28 11:59
 */
@GlobalMarker
object JacksonUtils {

    val JSON = ObjectMapper().apply {
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
            }
        })
    }


    /**
     * 对象转json字符串
     */
    @CustomExtend
    fun <T> T.toJsonString(): String {
        return JSON.writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 忽略值为null的字段
     */
    @CustomExtend
    fun <T> T.toJsonStringNonNull(): String {
        return JSON.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 以漂亮的格式输出
     */
    @CustomExtend
    fun <T> T.toJsonStringPretty(): String {
        return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }

    /**
     * 对象转json字符串
     * 忽略值为null的字段
     * 以漂亮的格式输出
     */
    @CustomExtend
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
    @CustomExtend
    inline fun <reified T> fromJsonString(json: String): T {
        return JSON.readValue(json, T::class.java)
    }

    /**
     * 同fromJsonString(json: String)
     * @see fromJsonString
     */
    @CustomExtend
    inline fun <reified T> String.jsonStringToObj(): T {
        return JSON.readValue(this, T::class.java)
    }
}