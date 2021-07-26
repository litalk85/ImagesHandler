package com.litalk.shieldtask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.litalk.init.Initializer;

@SpringBootApplication(scanBasePackages = { "com.litalk" })
@EnableJpaRepositories("com.acme.repositories")
@EntityScan("com.litalk.model")
public class ShieldtaskApplication {
	@Autowired
	Initializer init;
	
	public static void main(String[] args) {
		SpringApplication.run(ShieldtaskApplication.class, args);	
	}

}
