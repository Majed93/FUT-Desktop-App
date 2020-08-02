package com.fut.desktop.app.utils;

import org.springframework.core.env.Environment;

public final class UrlUtils {

    /**
     * Returns the base url for your rest interface
     *
     * @return url with port.
     */
    public static String getBaseUrl(Environment environment) {
        return "http://localhost:" + environment.getProperty("local.server.port");
    }
}
