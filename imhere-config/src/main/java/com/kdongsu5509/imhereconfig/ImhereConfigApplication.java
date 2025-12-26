package com.kdongsu5509.imhereconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ImhereConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImhereConfigApplication.class, args);
	}

}
