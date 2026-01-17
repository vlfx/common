package io.github.vlfx.common.spring


import io.github.vlfx.common.annotation.CustomExtend
import io.github.vlfx.common.annotation.GlobalMarker
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.HttpHost
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import kotlin.apply
import kotlin.apply as kotlinApply

/**
 * restClient和http的一些工具集和扩展
 * 使用者没有指定或者没有特殊说明的方法默认使用
 * {@linkplain org.springframework.http.client.HttpComponentsClientHttpRequestFactory Apache Http Client}
 *
 * @author vLfx
 * @date 2026/1/17 10:40
 */
@Suppress("unused")
@GlobalMarker
object RestClientUtils {

    /**
     * 替代 RestClient.requestFactory 用于设置http代理
     *
     * @param httpProxyConfig http代理配置信息
     * @param clientHttpRequestFactoryBuild 用于构建请求工厂ClientHttpRequestFactory，默认使用
     * {@linkplain org.springframework.http.client.HttpComponentsClientHttpRequestFactory Apache Http Client}
     * 详情见 RestClient.requestFactory
     *
     * @return this builder
     */
    @CustomExtend
    fun RestClient.Builder.requestFactoryOfHttpProxy(
        httpProxyConfig: HttpProxyConfig,
        clientHttpRequestFactoryBuild: ((HttpClient) -> ClientHttpRequestFactory) = {
            HttpComponentsClientHttpRequestFactory(
                it
            )
        },
    ): RestClient.Builder {
        this.requestFactory(clientHttpRequestFactoryBuild(httpProxyClientBuild(httpProxyConfig)))
        return this
    }


    /**
     * 如果proxyUsername或proxyPassword为null将不进行认证设置
     */
    data class HttpProxyConfig(
        val proxyHost: String,
        val proxyPort: Int,
        val proxyUsername: String? = null,
//        val proxyPassword: String? = null
        val proxyPassword: CharArray? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as HttpProxyConfig

            if (proxyPort != other.proxyPort) return false
            if (proxyHost != other.proxyHost) return false
            if (proxyUsername != other.proxyUsername) return false
            if (!proxyPassword.contentEquals(other.proxyPassword)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = proxyPort
            result = 31 * result + proxyHost.hashCode()
            result = 31 * result + (proxyUsername?.hashCode() ?: 0)
            result = 31 * result + (proxyPassword?.contentHashCode() ?: 0)
            return result
        }
    }

    /**
     * 构建基于http代理的httpClient
     */
    fun httpProxyClientBuild(
        httpProxyConfig: HttpProxyConfig
    ): HttpClient {
        with(httpProxyConfig) {
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
                                proxyPassword
                            )
                        )
                    }
                    // 设置代理认证信息
                    setDefaultCredentialsProvider(credentialsProvider)
                }
                // 设置http代理
                setProxy(proxy)
            }.build()
            return httpClient
        }
    }

    /**
     * 构建基于http代理的restClient
     */
    fun httpProxyRestClientBuild(
        httpProxyConfig: HttpProxyConfig
    ): RestClient {
        val restClient = RestClient.builder().kotlinApply {
            requestFactory(HttpComponentsClientHttpRequestFactory(httpProxyClientBuild(httpProxyConfig)))
        }.build()
        return restClient
    }
}