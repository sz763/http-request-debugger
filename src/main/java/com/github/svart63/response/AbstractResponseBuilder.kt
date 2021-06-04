package com.github.svart63.response

import java.io.InputStream
import javax.servlet.http.Part

abstract class AbstractResponseBuilder {
    internal fun partBody(part: Part?): String? {
        return part?.let { p -> readAndTrimTo50(p.inputStream) }
    }

    internal fun readAndTrimTo50(stream: InputStream): String {
        return stream.bufferedReader()
            .use { it.readText().replace(Regex("(.{50}?)([\\s\\S]+)"), "$1..") }
    }
}