package com.kdongsu5509.imhereeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ImhereEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImhereEurekaApplication.class, args);
    }

}
