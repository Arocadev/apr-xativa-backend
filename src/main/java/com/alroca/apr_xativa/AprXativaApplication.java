package com.alroca.apr_xativa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AprXativaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AprXativaApplication.class, args);
    }
}