package com.office;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OASystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OASystemApplication.class, args);
    }
} 