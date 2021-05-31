package com.github.svart63.response

import org.springframework.stereotype.Component
import java.io.IOException
import java.util.Arrays
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

@Component("plain")
class PlainTextResponseBuilder : ResponseBuilder<String> {
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
        builder.append("===========body===============\n")
        try {
            request.inputStream.bufferedReader().use { builder.append(it.readText()) }
        } catch (e: IOException) {
            builder.append("failed reading body... ").append(e.message)
        }
        builder.append("===========body===============\n")
    }

    internal fun appendParameters(request: HttpServletRequest, builder: StringBuilder) {
        val map = request.parameterMap
        builder.append("===========parameters===============\n")
        map.entries.stream().map { (key, value) -> key + '=' + Arrays.toString(value) }
            .forEach { e: String? -> builder.append(e).append('\n') }
        builder.append("===========parameters===============\n")
    }

    internal fun appendHeaders(request: HttpServletRequest, builder: StringBuilder) {
        val headerNames = request.headerNames
        builder.append("===========headers===============\n")
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            builder.append(headerName).append('=').append(request.getHeader(headerName)).append('\n')
        }
        builder.append("===========headers============\n")
    }

    internal fun appendMultiParts(request: HttpServletRequest, builder: StringBuilder) {
        builder.append("===========multi parts============\n")
        try {
            request.parts.forEach { part: Part ->
                builder
                    .append("name: ").append(part.name).append(',').append(' ')
                    .append("size: ").append(part.size).append(',').append(' ')
                    .append("type: ").append(part.contentType).append(',').append(' ')
                    .append("body: ").append(getSource(part))
                    .append('\n')
            }
        } catch (e: ServletException) {
            builder.append("failed getting parts: ").append(e.message).append('\n')
        }
        builder.append("===========multi parts============\n")
    }

    internal fun getSource(part: Part): String {
        try {
            part.inputStream.bufferedReader().use { stream ->
                val text = stream.readText()
                return if (text.length > 50) text.substring(0, 49) + "..." else text
            }
        } catch (e: IOException) {
            return "Failed reading part content: " + part.name
        }
    }

    override fun type(): String = "plain"
}