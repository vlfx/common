@file:Suppress("unused")

package io.github.vlfx.common

import io.github.vlfx.common.annotation.CustomExtend
import kotlin.collections.get
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 反射方法扩展
 * @author vLfx
 * @date 2025/9/4 12:25
 */


/**
 * 注意：目前只对kotlin的“data class”有用，java的class明确有问题，后面有空再修复 TODO
 *
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
    
    // 获取主构造函数
    val constructor = targetClass.constructors.firstOrNull()
        ?: throw IllegalArgumentException("目标类 ${targetClass.simpleName} 没有可用的构造函数")

    // 构建构造函数参数映射
    val constructorArgs = mutableMapOf<KParameter, Any?>()
    constructor.parameters.forEach { param ->
        val sourceValue = sourceProperties[param.name]?.getter?.call(this)
        if (nullable || sourceValue != null) {
            constructorArgs[param] = sourceValue
        }
    }

    // 通过构造函数创建实例
    val targetInstance = constructor.callBy(constructorArgs)

    // 处理可变属性（var）：如果构造函数中没有对应的参数，尝试通过反射设置
    val targetProperties = targetClass.memberProperties.associateBy { it.name }
    sourceProperties.forEach { (name, sourceProp) ->
        // 如果属性不在构造函数参数中，或者是可变属性，尝试设置值
        val paramInConstructor = constructor.parameters.any { it.name == name }
        val targetProp = targetProperties[name]
        
        if (!paramInConstructor && targetProp != null) {
            val sourceValue = sourceProp.getter.call(this)
            if (nullable || sourceValue != null) {
                try {
                    // 尝试设置为可变属性
                    val mutableProp = targetProp as? kotlin.reflect.KMutableProperty1<T, *>
                    mutableProp?.let {
                        it.isAccessible = true
                        @Suppress("UNCHECKED_CAST")
                        (it as kotlin.reflect.KMutableProperty1<T, Any?>).set(targetInstance, sourceValue)
                    }
                } catch (e: Exception) {
                    // 忽略设置失败的情况（可能是不可变属性或类型不兼容）
                }
            }
        }
    }

    return targetInstance
}

/**
 * 注意：目前只对kotlin的“data class”有用，java的class明确有问题，后面有空再修复 TODO
 *
 * 同上
 *
 * @param T 目标类
 * @param replaceParams 本对象this没有的属性可以在replaceParams中指定
 * @param ignoreParams 忽略掉本对象的某些属性复制到目标类，也就是指定不想复制给目标的属性
 */
@CustomExtend
inline fun <reified T : Any> Any.copyTo(
    replaceParams: Map<String, Any?> = emptyMap(),
    ignoreParams: List<String> = emptyList(),
    nullable: Boolean = true
): T {
    val targetClass: KClass<T> = T::class
    val sourceProperties = this::class.memberProperties
        .associateBy { it.name }
        .filter { it.key !in ignoreParams }
    
    // 获取主构造函数
    val constructor = targetClass.constructors.firstOrNull()
        ?: throw IllegalArgumentException("目标类 ${targetClass.simpleName} 没有可用的构造函数")

    // 构建构造函数参数映射
    val constructorArgs = mutableMapOf<KParameter, Any?>()
    constructor.parameters.forEach { param ->
        if (replaceParams.containsKey(param.name)) {
            // replaceParams 中的值无论是否为 null 都赋值
            constructorArgs[param] = replaceParams[param.name]
        } else {
            val sourceValue = sourceProperties[param.name]?.getter?.call(this)
            if (nullable || sourceValue != null) {
                constructorArgs[param] = sourceValue
            }
        }
    }

    // 通过构造函数创建实例
    val targetInstance = constructor.callBy(constructorArgs)

    // 处理可变属性（var）：如果构造函数中没有对应的参数，尝试通过反射设置
    val targetProperties = targetClass.memberProperties.associateBy { it.name }
    sourceProperties.forEach { (name, sourceProp) ->
        // 跳过在 ignoreParams 中的属性
        if (name in ignoreParams) return@forEach
        
        // 如果属性在 replaceParams 中，使用 replaceParams 的值
        val valueToSet = if (replaceParams.containsKey(name)) {
            replaceParams[name]
        } else {
            sourceProp.getter.call(this)
        }
        
        // 检查是否需要设置值
        if (!nullable && valueToSet == null && !replaceParams.containsKey(name)) {
            return@forEach
        }
        
        // 如果属性不在构造函数参数中，或者是可变属性，尝试设置值
        val paramInConstructor = constructor.parameters.any { it.name == name }
        val targetProp = targetProperties[name]
        
        if (!paramInConstructor && targetProp != null) {
            try {
                // 尝试设置为可变属性
                val mutableProp = targetProp as? kotlin.reflect.KMutableProperty1<T, *>
                mutableProp?.let {
                    it.isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    (it as kotlin.reflect.KMutableProperty1<T, Any?>).set(targetInstance, valueToSet)
                }
            } catch (e: Exception) {
                // 忽略设置失败的情况（可能是不可变属性或类型不兼容）
            }
        }
    }

    return targetInstance
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

/**
 * 将对象的属性转换成 <属性名,值> 的 map
 * @param accessPrivate 是否也获取私有属性 (目前是 非PUBLIC)
 */
@CustomExtend
fun Any.reflectionPropertiesMap(accessPrivate: Boolean = false): Map<String, Any?> {
    return this::class.memberProperties.filter {
        if (accessPrivate) {
            true
        } else {
            it.getter.visibility == KVisibility.PUBLIC
        }
    }.associate {
        if (accessPrivate && it.getter.visibility != KVisibility.PUBLIC) {
            it.getter.isAccessible = true
        }
        it.name to it.getter.call(this)
    }
}
