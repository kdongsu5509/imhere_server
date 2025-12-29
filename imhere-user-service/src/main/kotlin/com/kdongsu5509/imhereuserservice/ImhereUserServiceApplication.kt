package com.kdongsu5509.imhereuserservice

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import java.util.*

@SpringBootApplication
@EnableDiscoveryClient
class ImhereUserServiceApplication

@PostConstruct
fun started() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
}

fun main(args: Array<String>) {
    runApplication<ImhereUserServiceApplication>(*args)
}
