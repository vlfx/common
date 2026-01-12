package io.github.vlfx.common.annotation

/**
 * 标记全局属性，只是为了方便使用IDE来查找所有引用的地方
 * @author vLfx
 * @date 2025/9/29 00:19
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.SOURCE)
annotation class GlobalMarker
