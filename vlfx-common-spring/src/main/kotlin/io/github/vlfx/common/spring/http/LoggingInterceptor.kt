package io.github.vlfx.common.spring.http

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * http请求日志拦截器
 * @author vLfx
 * @date 2026/1/21 10:09
 */
open class LoggingInterceptor(private val printer: Printer) : ClientHttpRequestInterceptor {
    /**
     *
     * @param printFun 打印函数，默认为系统println
     *  其他如：org.slf4j.Logger::info
     *
     */
    constructor(printFun: (String?) -> Unit = ::println) : this(object : Printer {
        override fun println(message: Any?) {
            printFun(message.toString())
        }
    })

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        // 打印请求信息
        printer.println("=== REQUEST ===")
        printer.println("URI: ${request.uri}")
        printer.println("Method: ${request.method.name()}")
//            printer.println("Attributes:")
//            request.attributes.forEach { (string, any) ->
//                printer.println("  $string: $any")
//            }
        printer.println("Headers:")
        request.headers.forEach { (name, values) ->
            printer.println("  $name: ${values.joinToString()}")
        }

        // 执行请求
        val response = execution.execute(request, body)

        // 打印响应信息
        printer.println("=== RESPONSE ===")
        printer.println("Status: ${response.statusCode}")
        printer.println("Headers: ${response.headers.size}")
        response.headers.forEach { (name, values) ->
            printer.println("  $name: ${values.joinToString()}")
        }

        return response
    }

    interface Printer {
        fun println(message: Any?)
    }
}

