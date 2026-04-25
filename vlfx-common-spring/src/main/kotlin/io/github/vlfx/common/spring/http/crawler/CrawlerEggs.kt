@file:Suppress("unused")

package io.github.vlfx.common.spring.http.crawler

import io.github.vlfx.common.reflectionPropertiesMap
import io.github.vlfx.common.spring.JacksonUtils
import io.github.vlfx.common.spring.http.RequestMetadata
import io.github.vlfx.common.spring.http.ResultTransform
import io.github.vlfx.common.spring.http.sync
import org.springframework.web.client.RestClient

/**
 * 爬虫卵(或者叫爬虫节点，爬虫系统中每个节点的请求信息)，用于封装固定的请求和结果转换逻辑
 * 一次定义多次使用
 * @author vLfx
 * @date 2026/4/18 13:58
 */
open class CrawlerEggs<in PARAMS : Any, RESPONSE>(
    val requestMetadata: RequestMetadata,
    val restClient: RestClient,
    val responseClass: Class<RESPONSE>,
) {
    var resultTransform: ResultTransform<String, RESPONSE>? = null

    fun setupResultTransform(resultTransform: ResultTransform<String, RESPONSE>): CrawlerEggs<PARAMS, RESPONSE> {
        this.resultTransform = resultTransform
        return this
    }

    fun setupResultTransform(resultTransformFun: ((String?) -> RESPONSE?)): CrawlerEggs<PARAMS, RESPONSE> {
        this.resultTransform = object : ResultTransform<String, RESPONSE> {
            override fun transform(result: String?): RESPONSE? {
                return resultTransformFun.invoke(result)
            }
        }
        return this
    }

    fun sync(
        params: Map<String, Any?> = emptyMap(),
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): RESPONSE? {
        if (params.isEmpty() && requestMetadata.uri.contains("{") && requestMetadata.uri.contains("}")) {
            // 如果uri中包含{}插值模版，则params不能为空。(暂时这么处理，还没想出更好的方法)
            throw IllegalArgumentException("Params must not be empty")
        }
        return if (resultTransform == null) {
            requestMetadata.sync(responseClass, restClient, params, body, headers)
        } else {
            resultTransform?.transform(requestMetadata.sync(restClient, params, body, headers))
        }
    }

    fun sync(params: PARAMS, body: Any? = null, headers: Map<String, String> = emptyMap()): RESPONSE? {
        return if (resultTransform == null) {
            requestMetadata.sync(responseClass, restClient, params, body, headers)
        } else {
            resultTransform?.transform(requestMetadata.sync(restClient, params, body, headers))
        }
    }

    /**
     * 请求时观察响应字符串
     * 此时请求的decode并没有交给restClient，手动 json to bean 使用的 JacksonUtils.fromJsonString
     */
    fun requestAndObserveResponseString(
        params: Map<String, Any?> = emptyMap(),
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        observer: (String?) -> Unit
    ): RESPONSE? {
        val result: String? = requestMetadata.sync(restClient, params, body, headers)
        observer(result)
        if (result == null) {
            return null
        }
        return if (resultTransform == null) {
            JacksonUtils.fromJsonString(result, responseClass)
        } else {
            resultTransform?.transform(result)
        }
    }

    fun requestAndObserveResponseString(
        params: PARAMS,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        observer: (String?) -> Unit
    ): RESPONSE? {
        return requestAndObserveResponseString(params.reflectionPropertiesMap(), body, headers, observer)
    }
}