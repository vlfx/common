@file:Suppress("unused")
package io.github.vlfx.common

import io.github.vlfx.common.annotation.CustomExtend
import kotlin.collections.get
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 反射方法扩展
 * @author vLfx
 * @date 2025/9/4 12:25
 */


/**
 * 将一个对象转换为另一个类的对象，如果是两个对象互相复制可以直接用spring的BeanUtils
 * 这里不用spring的BeanUtils是因为kotlin的特殊性，比如data class如果其中有一个不可为空且无默认值的属性比如"var name: String"，这个时候是没有无参构造方法的，我试过BeanUtils.copyProperties必须得用实例化对象，了解了一下MapStruct好像生成的代码也是先用无参构造方法实例化一个对象然后给属性set值，这种情况应该怎么办？
 * 注：后面可以考虑模仿spring增加缓存，暂时不考虑性能问题而增加不必要的工作量
 *
 * @param T 目标类
 * @param nullable 是否将本对象为null的属性复制给目标对象
 */
@CustomExtend
inline fun <reified T : Any> Any.copyTo(nullable: Boolean = true): T {
    val targetClass: KClass<T> = T::class
    val sourceProperties = this::class.memberProperties.associateBy { it.name }
    val constructor = targetClass.constructors.first()

//    val args = constructor.parameters.associateWith { param ->
//        sourceProperties[param.name]?.getter?.call(this)
//    }.filter { it.value != null } // 忽略掉null值的属性,需保证接收者构造方法该参数有默认值或者可为null

    var args = constructor.parameters.associateWith { param ->
        sourceProperties[param.name]?.getter?.call(this)
    }
    if (!nullable) {
        args = args.filter { it.value != null } // 忽略掉null值的属性,需保证接收者构造方法该参数有默认值或者可为null
    }

    return constructor.callBy(args)
}

/**
 * 同上
 *
 * @param T 目标类
 * @param replaceParams 本对象this没有的属性可以在replaceParams中指定
 * @param ignoreParams 忽略掉本对象的某些属性复制到目标类，也就是指定不想复制给目标的属性
 */
@CustomExtend
inline fun <reified T : Any> Any.copyTo(
    replaceParams: Map<String, Any?> = emptyMap(),
    ignoreParams: List<String> = emptyList()
): T {
    val targetClass: KClass<T> = T::class
    val sourceProperties = this::class.memberProperties.associateBy { it.name }.filter { it.key !in ignoreParams }
    val constructor = targetClass.constructors.first()
    val args = mutableMapOf<KParameter, Any?>()
    constructor.parameters.forEach { param ->
        if (replaceParams.containsKey(param.name)) {
            args[param] = replaceParams[param.name] // replaceParams无论值是否为null都赋值给目标对象
        } else {
            val v = sourceProperties[param.name]?.getter?.call(this)
            if (v != null) {
                args[param] = v
            } // 忽略自己为null的属性
        }
    }

    return constructor.callBy(args)
}

/**
 * 通过属性名获取属性的值 包括私有属性
 */
@CustomExtend
inline fun <reified T> Any.reflectionProperty(propertyName: String): T? {
    val ps = this::class.memberProperties.associateBy { it.name }
    return ps[propertyName]?.getter?.let {
        it.isAccessible = true
        return@let it.call(this) as T
    }
}
