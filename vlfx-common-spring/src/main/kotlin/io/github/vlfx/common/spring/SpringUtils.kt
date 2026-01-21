@file:Suppress("unused")
package io.github.vlfx.common.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEvent

/**
 * Spring工具类
 * 主要功能：
 *      1、给静态方法使用applicationContext，静态方法获取bean
 *      2、运行时动态获取bean，可以不局限于实例化时注入
 * 实现方式：使用kotlin的委托方式，官方库已经实现了kotlin扩展方法，以下只是列出了部分扩展
 *
 * @see org.springframework.beans.factory.getBean
 * @see org.springframework.beans.factory.getBeansOfType
 * @see org.springframework.beans.factory.getBeanNamesForType
 * @see org.springframework.beans.factory.getBeanNamesForAnnotation
 * @see org.springframework.beans.factory.getBeansWithAnnotation
 *
 * @author vLfx
 * @date 2025/9/11 10:57
 */
lateinit var springUtils: SpringUtils // 全局属性，此实例的类委托于ApplicationContext，可以使用ApplicationContext的所有方法

// 不需要在其他地方实例化
class SpringUtils private constructor(val applicationContext: ApplicationContext) :
    ApplicationContext by applicationContext {
    //class SpringUtils(val applicationContext: ApplicationContext) : ApplicationContext by applicationContext {
    override fun publishEvent(event: ApplicationEvent) {
        applicationContext.publishEvent(event)
    }

    class LoadApplicationContextAware : ApplicationContextAware {
        override fun setApplicationContext(applicationContext: ApplicationContext) {
            springUtils = SpringUtils(applicationContext)
        }
    }

    /**
     * 按类型判断spring上下文中是否有实例
     */
    inline fun <reified T> containsBeanOfType(
        includeNonSingletons: Boolean = true,
        allowEagerInit: Boolean = true
    ): Boolean {
        try {
            this.getBean(T::class.java)
            return true
        } catch (_: Exception) {
            val beans = this.getBeansOfType(T::class.java, includeNonSingletons, allowEagerInit)
            return beans.isNotEmpty()
        }
    }
}
