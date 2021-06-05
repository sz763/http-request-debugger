package com.github.svart63.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.svart63.response.RequestProjection
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
internal class SimpleControllerTest(@Autowired val mockMvc: MockMvc) {
    private val objectMapper = ObjectMapper()

    @Test
    internal fun testGetRequestAsText() {
        mockMvc.get("/test/get").andExpect {
            status { isOk() }
            content { StringContains.containsString("/test/get") }
        }
    }

    @Test
    internal fun testGetRequestAsProjection() {
        val projection = RequestProjection(emptyMap(), "", emptyMap(), emptyList())
        val projectionAsJson = objectMapper.writeValueAsString(projection)
        mockMvc.get("/test?type=json").andExpect {
            status { isOk() }
            content { json(projectionAsJson) }
        }
    }

    @Test
    internal fun testGetRequestAsProjectionJsonPath() {
        val projection = RequestProjection(emptyMap(), "", emptyMap(), emptyList())
        val projectionAsJson = objectMapper.writeValueAsString(projection)
        mockMvc.get("/test/json").andExpect {
            status { isOk() }
            content { json(projectionAsJson) }
        }
    }
}