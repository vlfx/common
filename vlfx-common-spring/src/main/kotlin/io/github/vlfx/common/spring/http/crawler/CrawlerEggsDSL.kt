package io.github.vlfx.common.spring.http.crawler

import io.github.vlfx.common.spring.http.RequestMetadata
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestClient


@DslMarker
annotation class CrawlerEggsMarker

/**
 * 以dsl方式构建
 *
 *     crawlerEggs<ClassA.Params, ClassA>(REST_CLIENT, ClassA::class.java) {
 *         uri("https://")
 *         headers {
 *             add("Accept", "application/json, text/plain")
 *             add("User-Agent", SPECIAL_BROWSER)
 *         }
 *     }
 *
 *
 * 直接实例化方式
 *
 *     CrawlerEggs<ClassA.Params, ClassA>(
 *         RequestMetadata(
 *             "https://",
 *             headersConsumer = {
 *                 it.add("Accept", "application/json, text/plain")
 *                 it.add("User-Agent", SPECIAL_BROWSER)
 *             }
 *         ),
 *         REST_CLIENT,
 *         ClassA::class.java,
 *     )
 *
 *
 * @author vLfx
 * @date 2026/4/20 09:40
 */
@CrawlerEggsMarker
class CrawlerEggsBuilder<in PARAMS : Any, RESPONSE> {
    private var uri: String? = null
    private var method: HttpMethod = HttpMethod.GET
    private var params: Map<String, Any?> = emptyMap()
    private var body: Any? = null
    private var headersConsumer: (HttpHeaders) -> Unit = {}
    private var restClient: RestClient? = null
    private var responseClass: Class<RESPONSE>? = null
    private var resultTransform: ((String?) -> RESPONSE?)? = null

    fun uri(value: String) {
        this.uri = value
    }

    fun method(value: HttpMethod) {
        this.method = value
    }

    fun params(value: Map<String, Any?>) {
        this.params = value
    }

    fun body(value: Any?) {
        this.body = value
    }

    fun headers(block: HttpHeaders.() -> Unit) {
        this.headersConsumer = block
    }

    fun restClient(value: RestClient) {
        this.restClient = value
    }

    fun responseClass(value: Class<RESPONSE>) {
        this.responseClass = value
    }

    fun resultTransform(block: (String?) -> RESPONSE?) {
        this.resultTransform = block
    }

    fun build(): CrawlerEggs<PARAMS, RESPONSE> {
        val requestMetadata = RequestMetadata(
            uri = uri ?: throw IllegalArgumentException("uri must be set"),
            method = method,
            params = params,
            body = body,
            headersConsumer = headersConsumer
        )
        val crawlerEggs = CrawlerEggs<PARAMS, RESPONSE>(
            requestMetadata = requestMetadata,
            restClient = restClient ?: throw IllegalArgumentException("restClient must be set"),
            responseClass = responseClass ?: throw IllegalArgumentException("responseClass must be set")
        )
        resultTransform?.let {
            crawlerEggs.setupResultTransform(it)
        }
        return crawlerEggs
    }
}

fun <PARAMS : Any, RESPONSE> crawlerEggs(
    restClient: RestClient,
    responseClass: Class<RESPONSE>,
    block: CrawlerEggsBuilder<PARAMS, RESPONSE>.() -> Unit
): CrawlerEggs<PARAMS, RESPONSE> {
    return CrawlerEggsBuilder<PARAMS, RESPONSE>().apply {
        this.restClient(restClient)
        this.responseClass(responseClass)
    }.apply(block).build()
}
