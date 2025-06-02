package com.vlfx.common.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * @author vLfx
 * @date 2025/6/1 14:14
 */
@Suppress("unused")
class SpringUtils : ApplicationContextAware
//,BeanFactoryPostProcessor
{


    companion object {
//        private lateinit var beanFactory: ConfigurableListableBeanFactory
        private lateinit var applicationContext: ApplicationContext


        fun getApplicationContext(): ApplicationContext {
            return applicationContext
        }

        fun <T> getBeanByName(name: String): T {
            @Suppress("UNCHECKED_CAST")
            return applicationContext.getBean(name) as T
        }

        inline fun <reified T> getBean(): T {
            return getApplicationContext().getBean(T::class.java)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringUtils.applicationContext = applicationContext

    }
}