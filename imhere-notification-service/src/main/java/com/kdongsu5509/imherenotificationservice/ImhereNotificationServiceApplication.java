package com.kdongsu5509.imherenotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ImhereNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImhereNotificationServiceApplication.class, args);
    }

}
