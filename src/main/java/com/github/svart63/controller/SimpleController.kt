package com.github.svart63.controller

import com.github.svart63.model.RequestMessage
import com.github.svart63.response.ResponseBuilderFactory
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/test/**")
class SimpleController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val responseBuilderFactory: ResponseBuilderFactory
) {

    //Http method didn't specified to receive all requests
    @RequestMapping
    fun handleRequest(
        @RequestParam(required = false, defaultValue = "plain") type: String,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        val builder = responseBuilderFactory.builderOf(type)
        val response = builder.buildResponse(request)
        messagingTemplate.convertAndSend("/messages", RequestMessage(response))
        return ResponseEntity.ok(response)
    }

    @RequestMapping(path = ["/json"])
    fun handleRequestJson(request: HttpServletRequest): ResponseEntity<Any> {
        val builder = responseBuilderFactory.builderOf("json")
        val response = builder.buildResponse(request)
        messagingTemplate.convertAndSend("/messages", RequestMessage(response))
        return ResponseEntity.ok(response)
    }

}