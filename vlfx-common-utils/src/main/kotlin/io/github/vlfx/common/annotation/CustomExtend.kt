package io.github.vlfx.common.annotation

/**
 * 标记为自定义扩展，只是为了方便使用IDE来查找所有引用的地方
 * @author vLfx
 * @date 2023/9/8 11:25
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.SOURCE)
annotation class CustomExtend
