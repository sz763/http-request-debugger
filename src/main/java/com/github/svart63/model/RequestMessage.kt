package com.github.svart63.model

import java.time.LocalDateTime

class RequestMessage(val message: Any) {
    private val time: LocalDateTime = LocalDateTime.now()
    fun getTime(): String {
        return time.toString()
    }
}