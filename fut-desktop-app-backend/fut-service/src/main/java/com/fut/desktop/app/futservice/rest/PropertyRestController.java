package com.fut.desktop.app.futservice.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get property.
 */
@RequestMapping("/property")
@RestController
@Slf4j
public class PropertyRestController {

    private final Environment environment;

    @Autowired
    public PropertyRestController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping(value = "/port")
    public String getPort() {
        return environment.getProperty("server.port");
    }
}
