package com.github.svart63.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpMethod
import java.io.IOException
import kotlin.test.assertContains

@ExtendWith(MockitoExtension::class)
internal class PlainTextResponseBuilderTest : ResponseBuilderTest() {
    private val responseBuilder = PlainTextResponseBuilder()

    private val builder = StringBuilder()

    @Test
    internal fun testTypeOfBuilder() {
        assertEquals("plain", responseBuilder.type())
    }

    @Test
    internal fun testAppendMetadata() {
        doReturn("RequestURI").`when`(request).requestURI
        doReturn("PUT").`when`(request).method
        responseBuilder.appendMetadata(request, builder)
        val metaData = builder.toString()
        assertContains(metaData, "RequestURI")
        assertContains(metaData, "Request: PUT")
    }

    @Test
    internal fun testWrapperLine() {
        val actual = responseBuilder.wrapperLine("test")
        assertEquals("===========test============\n", actual)
    }

    @Test
    internal fun testBuildResponse() {
        val firstHeaderValues = Pair("key1", arrayOf("header1"))
        val secondHeaderValues = Pair("key2", arrayOf("header2.1", "header2.2"))
        mockHeaders(firstHeaderValues, secondHeaderValues)
        mockParameters()
        mockBody("JsonBody")
        mockMultipart("Multipart1", "Multipart2")
        val response = responseBuilder.buildResponse(request)
        assertContains(response, "JsonBody")
        assertContains(response, "Multipart2")
        assertContains(response, "header2.2")
        assertContains(response, "key2")
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
        assertContains(actual, responseBuilder.wrapperLine("multipart"))
        assertContains(actual, "json")
        assertContains(actual, "form-data")
    }

    @Test
    internal fun testGetMultipartWithoutContentType() {
        mockMultipartWithoutHeaderMultipart(null)
        responseBuilder.appendMultiParts(request, builder)
        verify(request, times(0)).parts
    }

    @Test
    internal fun testGetMultipartWithoutMultipartHeader() {
        mockMultipartWithoutHeaderMultipart("application/json")
        responseBuilder.appendMultiParts(request, builder)
        verify(request, times(0)).parts
    }

    @Test
    internal fun testGetMultipartThrowsError() {
        mockMultipartThrowsError()
        responseBuilder.appendMultiParts(request, builder)
        val actual = builder.toString()
        assertContains(actual, "failed getting parts")
    }

    @Test
    internal fun testAppendRequestBodyThrowsError() {
        val exMessage = "mocked"
        doThrow(IOException(exMessage)).`when`(request).inputStream
        responseBuilder.appendRequestBody(request, builder)
        assertContains(builder.toString(), "failed reading body... $exMessage")
    }
}


