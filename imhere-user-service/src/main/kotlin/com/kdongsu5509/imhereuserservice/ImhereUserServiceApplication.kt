package com.kdongsu5509.imhereuserservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class ImhereUserServiceApplication

fun main(args: Array<String>) {
	runApplication<ImhereUserServiceApplication>(*args)
}
