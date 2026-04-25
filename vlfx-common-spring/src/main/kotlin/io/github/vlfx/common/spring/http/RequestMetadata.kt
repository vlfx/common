@file:Suppress("unused")

package io.github.vlfx.common.spring.http

import io.github.vlfx.common.reflectionPropertiesMap
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient

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
    body: Any? = null,
    headers: Map<String, String> = emptyMap(), // 请求时最后的headers修正
): T? {
    val bodyContent = body ?: this.body
    return restClient
        .method(method) // method
        .uri(uri, this.params + params) // uri & params
        .headers(headersConsumer).apply { // headers metadata
            // 请求时最后的headers修正
            headers.forEach { (key, value) ->
                header(key, value)
            }
            if (bodyContent != null) {
                body(bodyContent) // body
            }
        }.retrieve().body(resultBodyType)
}

fun <T> RequestMetadata.sync(
    resultBodyType: Class<T>,
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
): T? = sync(resultBodyType, restClient, params.reflectionPropertiesMap(), body, headers)

inline fun <reified T> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
): T? = sync(T::class.java, restClient, params, body, headers)

inline fun <reified T> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
): T? = sync(restClient, params.reflectionPropertiesMap(), body, headers)

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    resultTransform: (T?) -> R?
): R? {
    return resultTransform(sync(restClient, params, body, headers))
}

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    resultTransform: (T?) -> R?
): R? = sync(restClient, params.reflectionPropertiesMap(), body, headers, resultTransform)

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    resultTransform: ResultTransform<T, R>
): R? {
    return resultTransform.transform(sync(restClient, params, body, headers))
}

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    resultTransform: ResultTransform<T, R>
): R? = sync(restClient, params.reflectionPropertiesMap(), body, headers, resultTransform)

/******* toEntity *******/

fun <T> RequestMetadata.toEntity(
    resultBodyType: Class<T>,
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(), // 请求时最后的headers修正
): ResponseEntity<T> {
    val bodyContent = body ?: this.body
    return restClient
        .method(method)
        .uri(uri, this.params + params)
        .headers(headersConsumer).apply {
            // 请求时最后的headers修正
            headers.forEach { (key, value) ->
                header(key, value)
            }
            if (bodyContent != null) {
                body(bodyContent)
            }
        }.retrieve().toEntity(resultBodyType)
}

inline fun <reified T> RequestMetadata.toEntity(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
): ResponseEntity<T> = toEntity(T::class.java, restClient, params, body, headers)

inline fun <reified T> RequestMetadata.toEntity(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
): ResponseEntity<T> = toEntity(restClient, params.reflectionPropertiesMap(), body, headers)

/******* exchange *******/

inline fun <reified T> RequestMetadata.exchange(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>
) {
    val bodyContent = body ?: this.body
    restClient.method(method)
        .uri(uri, this.params + params)
        .headers(headersConsumer).apply {
            // 请求时最后的headers修正
            headers.forEach { (key, value) ->
                header(key, value)
            }
            if (bodyContent != null) {
                body(bodyContent)
            }
        }.exchange<T>(exchangeFunction)
}

inline fun <reified T> RequestMetadata.exchange(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>
) = exchange(restClient, params.reflectionPropertiesMap(), body, headers, exchangeFunction)

inline fun <reified T> RequestMetadata.exchange(
    restClient: RestClient,
    params: Any,
    body: Any? = null,
    headers: Map<String, String> = emptyMap(),
    crossinline exchangeLambda: (req: HttpRequest, resp: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse) -> T
) = exchange(
    restClient, params.reflectionPropertiesMap(), body, headers,
    RestClient.RequestHeadersSpec.ExchangeFunction<T> { request, response -> exchangeLambda(request, response) })