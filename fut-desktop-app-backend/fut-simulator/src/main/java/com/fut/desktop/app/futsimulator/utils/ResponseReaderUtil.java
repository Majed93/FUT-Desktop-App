package com.fut.desktop.app.futsimulator.utils;

import org.springframework.util.Assert;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Util to read file and return string
 */
public final class ResponseReaderUtil {

    /**
     * Return raw string of file
     *
     * @param file File to read
     * @return string of file
     * @throws Exception Handle exception
     */
    public static String generateStringOfFile(String file) throws Exception {
        URL resource = ResponseReaderUtil.class.getClassLoader().getResource(file);
        Assert.notNull(resource, "File " + file + " is null");

        return new String(Files.readAllBytes(Paths.get(resource.toURI())), Charset.forName("UTF-8")).
                replace(System.getProperty("line.separator"), "").
                replace("\n", "").
                replace("\r", "").
                replace(" :", ":").
                replace(" ,", ",").
                replace(", ", ",").
                replace("{ ", "{").
                replace(" }", "}").
                replace(": ", ":").trim();
    }

    /**
     * Private constructor.
     */
    private ResponseReaderUtil() {
        // NOOP
    }
}
