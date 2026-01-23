@file:Suppress("unused")

package io.github.vlfx.common.spring.http

import io.github.vlfx.common.reflectionPropertiesMap
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

/**
 * http请求元数据，简化基础性的http请求
 * @author vLfx
 * @date 2026/1/21 09:53
 */
data class RequestMetadata(
    val uri: String,
    val method: HttpMethod = HttpMethod.GET,
    val params: Map<String, Any?> = emptyMap(),
    val body: Any? = null,
    val headersConsumer: (HttpHeaders) -> Unit = {},
) {
    constructor(
        uri: String,
        method: HttpMethod = HttpMethod.GET,
        params: Any,
        body: Any? = null,
        headersConsumer: (HttpHeaders) -> Unit = {}
    ) : this(uri, method, params.reflectionPropertiesMap(), body, headersConsumer)
}

fun <T> RequestMetadata.sync(
    resultBodyType: Class<T>,
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null
): T? {
    val bodyContent = body ?: this.body
    return restClient
        .method(method) // method
        .uri(uri, this.params + params) // uri & params
        .headers(headersConsumer).apply { // headers
            if (bodyContent != null) {
                body(bodyContent) // body
            }
        }.retrieve().body(resultBodyType)
}

fun <T> RequestMetadata.sync(
    resultBodyType: Class<T>,
    restClient: RestClient,
    params: Any,
    body: Any? = null
): T? = sync(resultBodyType, restClient, params.reflectionPropertiesMap(), body)

inline fun <reified T> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null
): T? {
    val bodyContent = body ?: this.body
    return restClient
        .method(method) // method
        .uri(uri, this.params + params) // uri & params
        .headers(headersConsumer).apply { // headers
            if (bodyContent != null) {
                body(bodyContent) // body
            }
        }.retrieve().body()
}

inline fun <reified T> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null
): T? = sync(restClient, params.reflectionPropertiesMap(), body)

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    resultTransform: (T?) -> R
): R {
    return resultTransform(sync(restClient, params, body))
}

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    resultTransform: (T?) -> R
): R = sync(restClient, params.reflectionPropertiesMap(), body, resultTransform)

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    resultTransform: ResultTransform<T, R>
): R {
    return resultTransform.transform(sync(restClient, params, body))
}

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    resultTransform: ResultTransform<T, R>
): R = sync(restClient, params.reflectionPropertiesMap(), body, resultTransform)

/******* async *******/

inline fun <reified T> RequestMetadata.async(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>
) {
    val bodyContent = body ?: this.body
    restClient.method(method)
        .uri(uri, this.params + params)
        .headers(headersConsumer).apply {
            if (bodyContent != null) {
                body(bodyContent)
            }
        }.exchange<T>(exchangeFunction)
}

inline fun <reified T> RequestMetadata.async(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>
) = async(restClient, params.reflectionPropertiesMap(), body, exchangeFunction)
