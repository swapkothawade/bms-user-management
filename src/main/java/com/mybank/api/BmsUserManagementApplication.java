package com.mybank.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan({"com.mybank.api", "com.mybank.api.config","com.mybank.api.dao"})
public class BmsUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BmsUserManagementApplication.class, args);
	}
}
