package io.github.vlfx.common.spring.http

import org.springframework.web.client.RestClient
import kotlin.reflect.jvm.javaType

/**
 * 用来封装一个固定的请求者实例，方便进行后续的多次调用
 * @author vLfx
 * @date 2026/1/22 12:31
 */
class ResultTransformRequester<T, R>(
    private val requestMetadata: RequestMetadata,
    private val restClient: RestClient,
    private val resultTransform: ResultTransform<T, R>,
//    val bodyClass: Class<T> // 传递bodyClass用于判定T的实际类型 plan 2
) {

// plan 1
//    // Cannot use 'T' as reified type parameter. Use a class instead.
//    fun sync(params: Map<String, Any?> = emptyMap(), body: Any? = null): R {
//        return requestMetadata.sync(restClient, params, body, resultTransform)
//    }

// plan 2
//    // 传递bodyClass用于判定T的实际类型
//    fun sync(params: Map<String, Any?> = emptyMap(), body: Any? = null): R {
//        val resultBody = requestMetadata.sync(bodyClass,restClient, params, body)
//        return resultTransform.transform(resultBody)
//    }

// plan 3
    fun sync(params: Map<String, Any?> = emptyMap(), body: Any? = null): R {
        @Suppress("UNCHECKED_CAST") val resultBodyClass: Class<T> =
            resultTransform.javaClass.kotlin.supertypes[0].arguments[0].type!!.javaType as Class<T>

        val resultBody = requestMetadata.sync(resultBodyClass, restClient, params, body)
        return resultTransform.transform(resultBody)
    }

}