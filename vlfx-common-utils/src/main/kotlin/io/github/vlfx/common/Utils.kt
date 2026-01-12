package io.github.vlfx.common

import io.github.vlfx.common.annotation.GlobalMarker
import java.lang.reflect.Field
import java.util.*
import java.util.regex.Pattern

/**
 * @author vLfx
 * @date 2025/5/29 12:24
 */
@Suppress("unused")
@GlobalMarker
object Utils {


    /**
     * 字符串长度不够len左边补s(默认0)
     */
    fun strLeftCompletion(str: String, len: Int, s: String = "0"): String {
        if (len <= 0 || str.length >= len) {
            return str
        }
        var v = str
        repeat(len - str.length) {
            v = s + v
        }
        return v
    }

    /**
     * 获取某类型的字段，且向父类查找
     */
    fun getFieldForAll(clazz: Class<*>, fieldName: String): Field? {
        var targetClass: Class<*>? = clazz
        do {
            val field: Field? = try {
                targetClass?.getDeclaredField(fieldName)
            } catch (e: Exception) {
                null
            }
            if (field != null) {
                return field
            }
            targetClass = targetClass?.superclass
        } while (targetClass != null)
        return null
    }

    /**
     * 是否是手机号
     */
    fun isMobile(mobile: String): Boolean {
        return Pattern.compile("1\\d{10}").matcher(mobile).matches()
    }

    /**
     * 简单uuid，去掉了中间的横杠，长度32位
     */
    fun simpleUuid() = UUID.randomUUID().toString().replace("-", "")

    /**
     * 手机号脱敏
     */
    fun maskPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("(\\d{3})\\d{4}(\\d{4})"), "$1****$2")
    }

}