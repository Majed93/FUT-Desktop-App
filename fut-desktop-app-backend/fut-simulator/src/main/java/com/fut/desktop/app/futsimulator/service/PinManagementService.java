package com.fut.desktop.app.futsimulator.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("singleton")
public class PinManagementService {

    private Integer pinCount = 0;

    /**
     * Reset the pin count
     */
    public void resetPin() {
        pinCount = 0;
    }

    /**
     * Increment the pin count
     */
    public void increment() {
        pinCount++;
    }
}
