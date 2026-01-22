package io.github.vlfx.common.spring.http

import org.springframework.web.client.RestClient

/**
 * 用来封装一个固定的请求者实例，方便进行后续的多次调用
 * @author vLfx
 * @date 2026/1/22 12:31
 */
class ResultTransformRequester<T, R>(
    private val requestMetadata: RequestMetadata,
    private val restClient: RestClient,
    private val resultTransform: ResultTransform<T?, R>
) {

    fun sync(params: Map<String, Any?> = emptyMap(), body: Any? = null): R {
        return requestMetadata.sync(restClient, params, body, resultTransform)
    }
}