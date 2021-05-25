package com.github.svart63.controller;

import com.github.svart63.model.RequestMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
public class WsController {
    @MessageMapping("/request")
    public List<RequestMessage> messages(String requestId) {
        System.out.println("Request: " + requestId);
        return Collections.emptyList();
    }
}
