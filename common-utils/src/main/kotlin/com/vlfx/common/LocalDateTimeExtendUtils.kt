@file:Suppress("unused")

package com.vlfx.common

import com.vlfx.com.vlfx.common.Constants
import com.vlfx.common.annotation.DiyExtend
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * @author vLfx
 * @date 2025/5/29 11:51
 */



/**
 * +运算符扩展
 * LocalDateTime + Duration => LocalDateTime
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline operator fun LocalDateTime.plus(other: Duration): LocalDateTime = this.plus(other.toJavaDuration())

/**
 * -运算符扩展
 * LocalDateTime - Duration => LocalDateTime
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline operator fun LocalDateTime.minus(other: Duration): LocalDateTime = this.minus(other.toJavaDuration())

/**
 * 获取下一天时间
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline fun LocalDateTime.nextDay(): LocalDateTime = this.plusDays(1)

/**
 * 获取前一天时间
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline fun LocalDateTime.agoDay(): LocalDateTime = this.minusDays(1)

/**
 * 获取下一天日期
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline fun LocalDate.nextDay(): LocalDate = this.plusDays(1)

/**
 * 获取前一天日期
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline fun LocalDate.agoDay(): LocalDate = this.minusDays(1)

/**
 * 以常用格式化成字符串
 * 默认格式：yyyy-MM-dd HH:mm:ss
 */
@DiyExtend
@Suppress("NOTHING_TO_INLINE")
inline fun LocalDateTime.toDefFmtStr(formatter: DateTimeFormatter = Constants.DATE_FORMAT_DEFAULT): String = this.format(formatter)

/**
 * 转换为时间戳
 */
@DiyExtend
fun LocalDateTime.toTimeMillis(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

/**
 * 将时间戳转换为LocalDateTime
 */
@DiyExtend
fun Long?.toLocalDateTime(): LocalDateTime? = if (this == null) null else LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), ZoneId.systemDefault())
