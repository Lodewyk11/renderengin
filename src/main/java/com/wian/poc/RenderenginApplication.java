package com.wian.poc;

import com.wian.poc.services.WianSeEinde;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wian.poc.services")
public class RenderenginApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenderenginApplication.class, args);
	}
}
