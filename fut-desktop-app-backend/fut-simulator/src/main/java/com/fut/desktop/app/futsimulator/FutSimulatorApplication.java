package com.fut.desktop.app.futsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.fut.desktop.app")
@SpringBootApplication
public class FutSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutSimulatorApplication.class, args);
    }
}
