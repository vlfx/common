@file:Suppress("unused")

package io.github.vlfx.common.spring.http.crawler

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

    fun sync(params: Map<String, Any?> = emptyMap(), body: Any? = null): RESPONSE? {
        return if (resultTransform == null) {
            requestMetadata.sync(responseClass, restClient, params, body)
        } else {
            resultTransform?.transform(requestMetadata.sync(restClient, params, body))
        }
    }

    fun sync(params: PARAMS, body: Any? = null): RESPONSE? {
        return if (resultTransform == null) {
            requestMetadata.sync(responseClass, restClient, params, body)
        } else {
            resultTransform?.transform(requestMetadata.sync(restClient, params, body))
        }
    }
}