package io.github.vlfx.common.spring


import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.HttpHost
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import kotlin.apply
import kotlin.apply as kotlinApply

/**
 * @author vLfx
 * @date 2026/1/17 10:40
 */
@Suppress("unused")
object RestClientUtils {

    /**
     * 构建基于http代理的restClient
     * 如果proxyUsername或proxyPassword为null将不进行认证设置
     */
    fun httpProxyRestClientBuild(
        proxyHost: String,
        proxyPort: Int,
        proxyUsername: String? = null,
        proxyPassword: String? = null
    ): RestClient {
        // 创建代理对象
        val proxy = HttpHost(proxyHost, proxyPort)
        // 创建自定义http客户端(使用http代理)
        val httpClient = HttpClients.custom().apply {
            if (proxyUsername != null && proxyPassword != null) {
                // 创建凭证提供者，设置代理认证信息
                val credentialsProvider = BasicCredentialsProvider().apply {
                    setCredentials(
                        AuthScope(proxy), UsernamePasswordCredentials(
                            proxyUsername,
                            proxyPassword.toCharArray()
                        )
                    )
                }
                // 设置代理认证信息
                setDefaultCredentialsProvider(credentialsProvider)
            }
            // 设置http代理
            setProxy(proxy)
        }.build()

        val restClient = RestClient.builder().kotlinApply {
            requestFactory(HttpComponentsClientHttpRequestFactory(httpClient))
        }.build()
        return restClient
    }
}