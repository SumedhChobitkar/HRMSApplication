package com.example.HRMS.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(HrmsApplication.class, args);
		System.out.println("Swagger Link:");
		System.out.println("http://localhost:8080/swagger-ui/index.html");
	}

}

