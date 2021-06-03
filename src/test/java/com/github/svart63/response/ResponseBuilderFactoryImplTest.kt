package com.github.svart63.response

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class ResponseBuilderFactoryImplTest {
    @Test
    internal fun testBuilderOf() {
        val builder = mock(ResponseBuilder::class.java)
        doReturn("mock").`when`(builder).type()
        val factory = ResponseBuilderFactoryImpl(listOf(builder))
        val responseBuilder = factory.builderOf("mock")
        assertEquals(builder, responseBuilder)
    }
}