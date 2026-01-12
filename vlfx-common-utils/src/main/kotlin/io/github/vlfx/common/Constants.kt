package io.github.vlfx.common

import io.github.vlfx.common.annotation.GlobalMarker
import java.time.format.DateTimeFormatter

/**
 * @author vLfx
 * @date 2025/5/29 11:53
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@GlobalMarker
object Constants {

    const val DATE_FORMAT_DEFAULT_STR = "yyyy-MM-dd HH:mm:ss"
    val DATE_FORMAT_DEFAULT: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern(DATE_FORMAT_DEFAULT_STR) }

//    /**
//     * 中文排序工具
//     */
//    val COLLATOR_CHINA by lazy {
//        Collator.getInstance(Locale.CHINA)
//    }
}