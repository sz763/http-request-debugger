package com.github.svart63.controller

import com.github.svart63.model.RequestMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class WsController {
    @MessageMapping("/request")
    fun messages(requestId: String): List<RequestMessage> {
        println("Request: $requestId")
        return emptyList()
    }
}