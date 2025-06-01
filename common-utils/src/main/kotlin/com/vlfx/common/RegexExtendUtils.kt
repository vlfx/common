@file:Suppress("unused")

package com.vlfx.common

import com.vlfx.common.annotation.DiyExtend

/**
 * 正则表达式扩展方法
 * @author vLfx
 * @date 2024/8/13 18:23
 */


/**
 * 字符串匹配正则表达式取回第group分组的值
 * @param regexStr 正则表达式
 * @param group 匹配到的第几个分组
 * @param startIndex 开始匹配的位置
 * @param default 匹配失败返回的默认值
 * @return 匹配到的值
 */
@DiyExtend
fun String.findMatch(regexStr: String, group: Int = 0, startIndex: Int = 0, default: String? = null):String? {
    val regex = Regex(regexStr)
    val matchResult = regex.find(this, startIndex)
    if (matchResult != null) {
//        println(matchResult.groups)
        if (group == 0) {
            return matchResult.value
        } else {
            if (matchResult.groups.size <= group) {
                return default
            }
            return matchResult.groups[group]?.value
        }
    } else {
        return default
    }
}
