package com.newproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.newproject.mapper")
@SpringBootApplication
public class NewprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewprojectApplication.class, args);
	}

}
