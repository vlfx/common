package io.github.vlfx.common.spring.config

import io.github.vlfx.common.spring.SpringUtils
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Import

/**
 * @author vLfx
 * @date 2026/1/12 20:12
 */
@Import(SpringUtils.LoadApplicationContextAware::class)
@AutoConfiguration
class CommonSpringAutoConfiguration