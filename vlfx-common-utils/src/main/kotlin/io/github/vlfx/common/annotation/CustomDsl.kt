package io.github.vlfx.common.annotation

/**
 * 标记为自定义DSL，只是为了方便使用IDE来查找所有引用的地方
 * @author vLfx
 * @date 2023/9/8 11:25
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.SOURCE)
annotation class CustomDsl
