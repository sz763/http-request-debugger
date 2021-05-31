package com.github.svart63

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class SpringAppMain

fun main(args: Array<String>) {
    runApplication<SpringAppMain>(*args)
}
