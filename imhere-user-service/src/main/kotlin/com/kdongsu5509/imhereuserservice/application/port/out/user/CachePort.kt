package com.kdongsu5509.imhereuserservice.application.port.out.user

import java.time.Duration

interface CachePort {
    fun save(key: String, data: Any, duration: Duration)
    fun find(key: String): Any?
}