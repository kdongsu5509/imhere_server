package com.kdongsu5509.user

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class ImhereUserServiceApplication

@PostConstruct
fun started() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
}

fun main(args: Array<String>) {
    runApplication<ImhereUserServiceApplication>(*args)
}
