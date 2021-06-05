package com.github.svart63.response

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AbstractResponseBuilderTest {
    private val responseBuilder = ResponseBuilderImpl()

    @Test
    internal fun testGetPartIfNull() {
        kotlin.test.assertNull(responseBuilder.partBody(null))
    }

    @Test
    internal fun testReadAndTrimTo50() {
        val builder = StringBuilder(51)
        for (index in 0..51) {
            builder.append((48 + index).toChar())
        }
        val body = builder.toString()
        val actual = responseBuilder.readAndTrimTo50(body.byteInputStream())
        assertNotNull(actual, "Body must not be null")
        Assertions.assertEquals(52, actual.length)
        assertTrue(actual.endsWith(".."), "body must end with '..', actual: $actual")
    }
}


class ResponseBuilderImpl : AbstractResponseBuilder<String>() {
    override fun buildResponse(request: HttpServletRequest): String = ""

    override fun type(): String = ""
}