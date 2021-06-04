package com.github.svart63.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Enumeration
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
internal class PlainTextResponseBuilderTest : AbstractResponseBuilderTest() {
    private val responseBuilder = PlainTextResponseBuilder()

    private val builder = StringBuilder()

    @Test
    internal fun testTypeOfBuilder() {
        assertEquals("plain", responseBuilder.type())
    }

    @Test
    internal fun testWrapperLine() {
        val actual = responseBuilder.wrapperLine("test")
        assertEquals("===========test============\n", actual)
    }

    @Test
    internal fun testGetHeadersOfRequest() {
        val firstHeaderValues = Pair("key1", arrayOf("header1"))
        val secondHeaderValues = Pair("key2", arrayOf("header2.1", "header2.2"))
        mockHeaders(firstHeaderValues, secondHeaderValues)
        responseBuilder.appendHeaders(request, builder)
        val actual = builder.toString()
        assertContains(actual, "headers")
        assertContains(actual, "key1")
        assertContains(actual, "header2.2")
    }

    @Test
    internal fun testGetRequestBody() {
        val expected = "body"
        mockBody(expected)
        responseBuilder.appendRequestBody(request, builder)
        val wrapperLine = responseBuilder.wrapperLine("body")
        assertEquals(wrapperLine + expected + '\n' + wrapperLine, builder.toString())
    }

    @Test
    internal fun testGetParameters() {
        mockParameters()
        responseBuilder.appendParameters(request, builder)
        val actual = builder.toString()
        assertContains(actual, "key1")
        assertContains(actual, "val2")
    }

    @Test
    internal fun testGetMultipart() {
        val fe = "json"
        val se = "form-data"
        mockMultipart(fe, se)
        responseBuilder.appendMultiParts(request, builder)
        val actual = builder.toString()
        assertContains(actual, responseBuilder.wrapperLine("multi parts"))
        assertContains(actual, "json")
        assertContains(actual, "form-data")
    }

//    @Test
//    internal fun testBuildResponse() {
//        val headers = mapOf(Pair("key", TestEnumeration(listOf("val").iterator())))
//        val parameters = mapOf(Pair("key1", "val1"))
//        val body = "http-request-body"
//        val fe = "json"
//        val multipart = multipart("form-data-json", toInputStream(fe), "json", fe.length.toLong())
//        val parts = listOf(multipart)
//        val headersKey = TestEnumeration(headers.keys.iterator())
//
//        doReturn(headersKey).`when`(request).headerNames
//        `when`(request.getHeaders(anyString())).thenAnswer { a -> headers[a.arguments.first()] }
//        doReturn(parameters).`when`(request).parameterMap
//        doReturn(parts).`when`(request).parts
//        doReturn("multipart").`when`(request).contentType
//        mockBody(body)
//
//        val responseText = responseBuilder.buildResponse(request)
//        assertEquals(body, requestProjection.body)
//        assertEquals(mapOf(Pair("key", listOf("val"))), requestProjection.headers)
//        assertEquals(parameters, requestProjection.parameters)
//        val projection = MultipartProjection("form-data-json", "json", fe.length.toLong(), fe)
//        assertEquals(listOf(projection), requestProjection.multipart)
//    }
}


