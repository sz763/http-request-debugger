package com.github.svart63.response

import javax.servlet.http.HttpServletRequest

interface ResponseBuilder<T> {
    fun buildResponse(request: HttpServletRequest): T
    fun type(): String
}