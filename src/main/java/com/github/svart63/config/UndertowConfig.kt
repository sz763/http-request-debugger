package com.github.svart63.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.UndertowServletWebServerFactoryCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.streams.toList

@Configuration
open class UndertowConfig {
    @Bean
    open fun undertowServletWebServerFactory(
        deploymentInfoCustomizers: ObjectProvider<UndertowDeploymentInfoCustomizer>,
        builderCustomizers: ObjectProvider<UndertowBuilderCustomizer>
    ): UndertowServletWebServerFactory? {
        val factory = UndertowServletWebServerFactory()
        factory.deploymentInfoCustomizers.addAll(deploymentInfoCustomizers.orderedStream().toList())
        factory.builderCustomizers.addAll(builderCustomizers.orderedStream().toList())
        return factory
    }

    @Bean
    open fun undertowServletWebServerFactoryCustomizer(
        serverProperties: ServerProperties
    ): UndertowServletWebServerFactoryCustomizer {
        return UndertowServletWebServerFactoryCustomizer(serverProperties)
    }

}