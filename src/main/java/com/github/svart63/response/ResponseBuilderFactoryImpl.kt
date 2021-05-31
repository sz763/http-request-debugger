package com.github.svart63.response

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ResponseBuilderFactoryImpl(@Autowired builders: List<ResponseBuilder<*>>) : ResponseBuilderFactory {
    private val builderMap = mutableMapOf<String, ResponseBuilder<*>>()

    init {
        builders.map { builderMap[it.type().lowercase()] = it }
    }

    override fun builderOf(type: String): ResponseBuilder<Any> {
        val builder = builderMap[type.lowercase()]
        if (builder == null) {
            throw IllegalArgumentException("No such response builder by type: $type")
        } else {
            return builder as ResponseBuilder<Any>
        }
    }
}