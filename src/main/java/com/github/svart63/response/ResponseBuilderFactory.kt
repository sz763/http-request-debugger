package com.github.svart63.response

interface ResponseBuilderFactory {
    fun builderOf(type: String): ResponseBuilder<Any>
}