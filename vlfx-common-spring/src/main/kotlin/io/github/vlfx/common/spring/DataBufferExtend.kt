@file:Suppress("unused")
package io.github.vlfx.common.spring

import io.github.vlfx.common.annotation.CustomExtend
import org.springframework.core.io.buffer.DataBuffer
import java.nio.charset.Charset

/**
 * @author vLfx
 * @date 2025/10/16 20:12
 */

/**
 * 获取可读的数据并转换成字符串
 */
@CustomExtend
fun DataBuffer.getDataStr(charset: Charset = Charsets.UTF_8): String {
    return String(getByteArray(), charset)
}

/**
 * 获取可读的字节数组
 */
@CustomExtend
fun DataBuffer.getByteArray(): ByteArray {
    val readableByteCount = readableByteCount()
    val readPosition = readPosition()
    val bytes = ByteArray(readableByteCount)
    for (index in 0 until readableByteCount) {
        bytes[index] = getByte(index + readPosition)
    }
    return bytes
}