package io.github.vlfx.common.spring.http

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
)

inline fun <reified T> RequestMetadata.sync(restClient: RestClient, params: Map<String, Any?> = emptyMap()): T? {
    return restClient
        .method(method) // method
        .uri(uri, this.params + params) // uri & params
        .headers(headersConsumer).apply { // headers
            if (body != null) {
                body(body) // body
            }
        }.retrieve().body()
}

inline fun <reified T, R> RequestMetadata.sync(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    resultTransform: (T?) -> R
): R {
    return resultTransform(sync(restClient, params))
}

inline fun <reified T> RequestMetadata.async(
    restClient: RestClient,
    params: Map<String, Any?> = emptyMap(),
    exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>
) {
    restClient.method(method)
        .uri(uri, this.params + params)
        .headers(headersConsumer).apply {
            if (body != null) {
                body(body)
            }
        }.exchange<T>(exchangeFunction)
}
