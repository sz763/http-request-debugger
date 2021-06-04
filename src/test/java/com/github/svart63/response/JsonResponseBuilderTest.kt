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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
internal class JsonResponseBuilderTest : AbstractResponseBuilderTest() {
    private val responseBuilder = JsonResponseBuilder()

    @Test
    internal fun testGetHeadersOfRequest() {
        val firstHeaderValues = Pair("key1", arrayOf("header1"))
        val secondHeaderValues = Pair("key2", arrayOf("header2.1", "header2.2"))
        val (firstEntry, secondEntry) = mockHeaders(firstHeaderValues, secondHeaderValues)
        val actual = responseBuilder.headersOf(request)
        assertEquals(firstHeaderValues.second.toList(), actual[firstEntry.first])
        assertEquals(secondHeaderValues.second.toList(), actual[secondEntry.first])
    }

    @Test
    internal fun testGetRequestBody() {
        val expected = "body"
        mockBody(expected)
        val actual = responseBuilder.bodyOf(request)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetParameters() {
        val expected = mockParameters()
        val actual = responseBuilder.parametersOf(request)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetMultipart() {
        val fe = "json"
        val se = "form-data"
        mockMultipart(fe, se)
        val actual = responseBuilder.multiPartOf(request)
        val fistProjection = actual.component1()
        val secondProjection = actual.component2()
        assertEquals(fe, fistProjection.body)
        assertEquals(se, secondProjection.body)
    }

    @Test
    internal fun testReadLongBodyFromStream() {
        val builder = StringBuilder(51)
        for (index in 0..51) {
            builder.append((48 + index).toChar())
        }
        val body = builder.toString()
        val actual = responseBuilder.readAndTrimTo50(body.byteInputStream())
        assertNotNull(actual, "Body must not be null")
        assertEquals(52, actual.length)
        assertTrue(actual.endsWith(".."), "body must end with '..', actual: $actual")
    }

    @Test
    internal fun testBuildResponse() {
        val headers = mapOf(Pair("key", TestEnumeration(listOf("val").iterator())))
        val parameters = mapOf(Pair("key1", "val1"))
        val body = "http-request-body"
        val fe = "json"
        val multipart = multipart("form-data-json", toInputStream(fe), "json", fe.length.toLong())
        val parts = listOf(multipart)
        val headersKey = TestEnumeration(headers.keys.iterator())

        doReturn(headersKey).`when`(request).headerNames
        `when`(request.getHeaders(anyString())).thenAnswer { a -> headers[a.arguments.first()] }
        doReturn(parameters).`when`(request).parameterMap
        doReturn(parts).`when`(request).parts
        doReturn("multipart").`when`(request).contentType
        mockBody(body)

        val requestProjection = responseBuilder.buildResponse(request)
        assertEquals(body, requestProjection.body)
        assertEquals(mapOf(Pair("key", listOf("val"))), requestProjection.headers)
        assertEquals(parameters, requestProjection.parameters)
        val projection = MultipartProjection("form-data-json", "json", fe.length.toLong(), fe)
        assertEquals(listOf(projection), requestProjection.multipart)
    }
}


