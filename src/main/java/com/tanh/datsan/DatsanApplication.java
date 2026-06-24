package com.tanh.datsan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatsanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatsanApplication.class, args);
    }

}
