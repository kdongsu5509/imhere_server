package com.kdongsu5509.imhereapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ImhereApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImhereApiGatewayApplication.class, args);
    }

}
