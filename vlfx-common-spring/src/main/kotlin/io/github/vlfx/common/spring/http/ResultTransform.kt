package io.github.vlfx.common.spring.http

/**
 * @author vLfx
 * @date 2026/1/22 12:18
 */
interface ResultTransform<T, R> {
    fun transform(result: T): R
}