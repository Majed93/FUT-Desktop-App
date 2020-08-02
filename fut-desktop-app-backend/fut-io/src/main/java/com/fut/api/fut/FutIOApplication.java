package com.fut.api.fut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.fut.api.fut","com.fut.desktop.app"})
@SpringBootApplication
public class FutIOApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutIOApplication.class, args);
    }
}
