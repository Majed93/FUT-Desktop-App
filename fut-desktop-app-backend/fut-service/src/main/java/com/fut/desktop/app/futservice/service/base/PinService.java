package com.fut.desktop.app.futservice.service.base;

/**
 * Contract for PinService
 */
public interface PinService {

    /**
     * Retrieve the pin count of the current session
     *
     * @return The current pin count.
     */
    Integer getPinCount();
}
