package com.github.svart63.response

import org.mockito.Mock
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Enumeration
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

abstract class AbstractResponseBuilderTest {
    @Mock
    protected lateinit var request: HttpServletRequest

    protected fun mockHeaders(
        firstHeader: Pair<String, Array<String>>,
        secondHeader: Pair<String, Array<String>>
    ): Pair<Pair<String, TestEnumeration>, Pair<String, TestEnumeration>> {
        val firstEntry = Pair(firstHeader.first, TestEnumeration(firstHeader.second.iterator()))
        val secondEntry = Pair(secondHeader.first, TestEnumeration(secondHeader.second.iterator()))
        val headers = mapOf(firstEntry, secondEntry)
        val headersKey = TestEnumeration(headers.keys.iterator())
        Mockito.doReturn(headersKey).`when`(request).headerNames
        Mockito.`when`(request.getHeaders(Mockito.anyString())).thenAnswer { a -> headers[a.arguments.first()] }
        return Pair(firstEntry, secondEntry)
    }

    protected fun mockMultipart(fe: String, se: String) {
        val firstPart = multipart("form-data-json", toInputStream(fe), "json", fe.length.toLong())
        val secondPart = multipart(null, toInputStream(se), "form-data", se.length.toLong())
        val parts = listOf(firstPart, secondPart)
        Mockito.doReturn(parts).`when`(request).parts
        Mockito.doReturn("multipart").`when`(request).contentType
    }

    protected fun mockMultipartWithoutHeaderMultipart(contentType: String?) {
        Mockito.doReturn(contentType).`when`(request).contentType
    }

    protected fun mockMultipartThrowsError() {
        Mockito.doReturn("multipart").`when`(request).contentType
        Mockito.doThrow(IOException("mocked")).`when`(request).parts
    }

    protected fun toInputStream(firstExpected: String) = ByteArrayInputStream(firstExpected.toByteArray())

    protected fun multipart(type: String?, body: InputStream, name: String, size: Long): Part {
        val part = Mockito.mock(Part::class.java)
        Mockito.doReturn(body).`when`(part).inputStream
        Mockito.doReturn(size).`when`(part).size
        Mockito.doReturn(name).`when`(part).name
        Mockito.doReturn(type).`when`(part).contentType
        return part
    }

    protected fun mockBody(expected: String) {
        val mockedStream = Mockito.mock(ServletInputStream::class.java)
        Mockito.doReturn(mockedStream).`when`(request).inputStream
        var filled = false
        Mockito.`when`(mockedStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer { inv ->
            val bytes = inv.arguments[0] as ByteArray
            expected.toByteArray().copyInto(bytes)
            if (filled) {
                -1
            } else {
                filled = true
                expected.length
            }
        }
    }

    protected fun mockParameters(): Map<String, Array<String>> {
        val expected = mapOf(Pair("key1", arrayOf("val1")), Pair("key2", arrayOf("val2")))
        Mockito.doReturn(expected).`when`(request).parameterMap
        return expected
    }
}

class TestEnumeration(private val iterator: Iterator<String>) : Enumeration<String> {
    override fun hasMoreElements(): Boolean = iterator.hasNext()
    override fun nextElement(): String = iterator.next()
}