@file:Suppress("unused")

package com.vlfx.common.spring

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.vlfx.common.annotation.DiyExtend
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



/**
 * @author vLfx
 * @date 2025/6/2 10:17
 */


//val deserializer = object : JsonDeserializer<LocalDateTime>() {
//    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime? {
//        val str = p.valueAsString // Date(1565089204000-0000)
////        "在这里把你的字符串解析成 LocalDateTime"


//        return Utils.convert(str)
//    }
//}
@DiyExtend
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

//DIY
@DiyExtend
fun <T> T.toJsonString(): String {

    return JSON.writeValueAsString(this)
}

/**
 * 美化输出json字符串
 */
@DiyExtend
fun <T> T.toJsonStringPretty(): String {
    return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}

@DiyExtend
fun <T> T.toJsonStringNonNullPretty(): String {
    return JSON.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter().writeValueAsString(this)
}
