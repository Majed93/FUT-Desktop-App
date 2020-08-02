package com.fut.api.fut.utils;

import org.springframework.stereotype.Component;

@Component
public final class HttpUtils {

    /**
     * Check if 2xx status code.
     *
     * @param statusCode Status code to check
     * @return If between 200 and 398
     */
    public static boolean ensureSuccessStatusCode(int statusCode) {
        return statusCode >= 199 && statusCode <= 399;
    }
}
