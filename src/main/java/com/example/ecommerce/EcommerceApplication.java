package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// extends SpringBootServletInitializer when generating war file to deployed in tomcat
@SpringBootApplication
public class EcommerceApplication extends SpringBootServletInitializer {

	// Add this method in override way when generating war file to deployed in tomcat
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(EcommerceApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext(){
		return new SpringApplicationContext();
	}

}
