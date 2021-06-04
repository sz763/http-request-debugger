package com.github.svart63.response

import org.springframework.stereotype.Component
import java.io.IOException
import java.util.Arrays
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

@Component("plain")
class PlainTextResponseBuilder : AbstractResponseBuilder(), ResponseBuilder<String> {
    override fun buildResponse(request: HttpServletRequest): String {
        val builder = StringBuilder()
        appendMetadata(builder, request)
        appendHeaders(request, builder)
        appendMultiParts(request, builder)
        appendParameters(request, builder)
        appendRequestBody(request, builder)
        return builder.toString()
    }

    internal fun appendMetadata(builder: StringBuilder, request: HttpServletRequest) {
        builder.append("Request: ").append(request.method).append(": ").append(request.requestURI).append('\n')
    }

    internal fun appendRequestBody(request: HttpServletRequest, builder: StringBuilder) {
        val wrapperLine = wrapperLine("body")
        builder.append(wrapperLine)
        try {
            builder.append(readAndTrimTo50(request.inputStream)).append('\n')
        } catch (e: IOException) {
            builder.append("failed reading body... ").append(e.message)
        }
        builder.append(wrapperLine)
    }

    internal fun appendParameters(request: HttpServletRequest, builder: StringBuilder) {
        val wrapperLine = wrapperLine("parameters")
        val map = request.parameterMap
        builder.append(wrapperLine)
        map.entries.stream().map { (key, value) -> key + '=' + Arrays.toString(value) }
            .forEach { e: String? -> builder.append(e).append('\n') }
        builder.append(wrapperLine)
    }

    internal fun appendHeaders(request: HttpServletRequest, builder: StringBuilder) {
        val wrapperLine = wrapperLine("headers")
        val headerNames = request.headerNames
        builder.append(wrapperLine)
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            builder.append(headerName).append('=').append(request.getHeaders(headerName).toList()).append('\n')
        }
        builder.append(wrapperLine)
    }


    internal fun appendMultiParts(request: HttpServletRequest, builder: StringBuilder) {
        val wrapperLine = wrapperLine("multi parts")
        builder.append(wrapperLine)
        if (request.contentType != null && request.contentType.contains("multipart")) {
            try {
                request.parts.forEach { part: Part ->
                    builder
                        .append("name: ").append(part.name).append(',').append(' ')
                        .append("size: ").append(part.size).append(',').append(' ')
                        .append("type: ").append(part.contentType).append(',').append(' ')
                        .append("body: ").append(partBody(part))
                        .append('\n')
                }
            } catch (e: ServletException) {
                builder.append("failed getting parts: ").append(e.message).append('\n')
            }
        }
        builder.append(wrapperLine)
    }

    internal fun wrapperLine(key: String) = "===========$key============\n"

    override fun type(): String = "plain"
}