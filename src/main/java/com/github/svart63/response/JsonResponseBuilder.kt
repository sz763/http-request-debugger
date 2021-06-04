package com.github.svart63.response

import org.springframework.stereotype.Component
import java.io.InputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

@Component
class JsonResponseBuilder : AbstractResponseBuilder(), ResponseBuilder<RequestProjection> {

    override fun buildResponse(request: HttpServletRequest): RequestProjection {
        val headers = headersOf(request)
        val body: String = bodyOf(request)
        val parameters = parametersOf(request)
        val multipart = multiPartOf(request)
        return RequestProjection(headers, body, parameters, multipart)
    }

    internal fun multiPartOf(request: HttpServletRequest): List<MultipartProjection> {
        return if (request.contentType != null && request.contentType.contains("multipart")) {
            return try {
                request.parts.map { part ->
                    MultipartProjection(part.contentType, part.name, part.size, partBody(part))
                }
            } catch (ex: Exception) {
                listOf(MultipartProjection("error", "error", 4, "failed getting multipart: ${ex.message}"))
            }
        } else {
            emptyList()
        }
    }

    internal fun parametersOf(request: HttpServletRequest) = request.parameterMap

    internal fun bodyOf(request: HttpServletRequest): String {
        return readAndTrimTo50(request.inputStream)
    }

    internal fun headersOf(request: HttpServletRequest) = request.headerNames.asSequence()
        .map { Pair(it, request.getHeaders(it).asSequence().toList()) }
        .toMap()

    override fun type(): String = "json"
}

data class RequestProjection(
    val headers: Map<String, Collection<String>>,
    val body: String?,
    val parameters: Map<String, Array<String>>,
    val multipart: List<MultipartProjection>
)

data class MultipartProjection(val contentType: String?, val name: String, val size: Long, val body: String?)
