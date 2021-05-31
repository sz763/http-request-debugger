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
internal class JsonResponseBuilderTest {
    private val responseBuilder = JsonResponseBuilder()

    @Mock
    private lateinit var request: HttpServletRequest

    @Test
    internal fun testGetHeadersOfRequest() {
        val firstHeaderValues = arrayOf("header1")
        val secondHeaderValues = arrayOf("header2.1", "header2.2")
        val firstEntry = Pair("key1", TestEnumeration(firstHeaderValues.iterator()))
        val secondEntry = Pair("key2", TestEnumeration(secondHeaderValues.iterator()))
        val headers = mapOf(firstEntry, secondEntry)
        val headersKey = TestEnumeration(headers.keys.iterator())
        doReturn(headersKey).`when`(request).headerNames
        `when`(request.getHeaders(anyString())).thenAnswer { a -> headers[a.arguments.first()] }
        val actual = responseBuilder.headersOf(request)
        assertEquals(firstHeaderValues.toList(), actual[firstEntry.first])
        assertEquals(secondHeaderValues.toList(), actual[secondEntry.first])
    }

    @Test
    internal fun testGetRequestBody() {
        val expected = "body"
        val mockedStream = mock(ServletInputStream::class.java)
        doReturn(mockedStream).`when`(request).inputStream
        var filled = false
        `when`(mockedStream.read(any(), anyInt(), anyInt())).thenAnswer { inv ->
            val bytes = inv.arguments[0] as ByteArray
            expected.toByteArray().copyInto(bytes)
            if (filled) {
                -1
            } else {
                filled = true
                expected.length
            }
        }
        val actual = responseBuilder.bodyOf(request)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetParameters() {
        val expected = mapOf(Pair("key1", "val1"), Pair("key2", "val2"))
        doReturn(expected).`when`(request).parameterMap
        val actual = responseBuilder.parametersOf(request)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetMultipart() {
        val fe = "json"
        val se = "form-data"
        val firstPart = multipart("form-data-json", toInputStream(fe), "json", fe.length.toLong())
        val secondPart = multipart(null, toInputStream(se), "form-data", se.length.toLong())
        val parts = listOf(firstPart, secondPart)
        doReturn(parts).`when`(request).parts
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

    private fun toInputStream(firstExpected: String) = ByteArrayInputStream(firstExpected.toByteArray())

    private fun multipart(type: String?, body: InputStream, name: String, size: Long): Part {
        val part = mock(Part::class.java)
        doReturn(body).`when`(part).inputStream
        doReturn(size).`when`(part).size
        doReturn(name).`when`(part).name
        doReturn(type).`when`(part).contentType
        return part
    }
}

class TestEnumeration(private val iterator: Iterator<String>) : Enumeration<String> {
    override fun hasMoreElements(): Boolean = iterator.hasNext()
    override fun nextElement(): String = iterator.next()
}
