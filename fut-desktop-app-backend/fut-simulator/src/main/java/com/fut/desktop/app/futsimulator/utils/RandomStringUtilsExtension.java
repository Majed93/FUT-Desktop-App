package com.fut.desktop.app.futsimulator.utils;

import org.apache.commons.lang.RandomStringUtils;

public final class RandomStringUtilsExtension {

    /**
     * Ensure the first character is not 0
     *
     * @param str String to test
     * @return string without 0
     */
    public static String ensureDoesNotStartWithZero(String str) {
        if (str.charAt(0) == '0') {
            StringBuilder stringBuilder = new StringBuilder(str);
            stringBuilder.setCharAt(0,
                    ensureDoesNotStartWithZero(RandomStringUtils.random(1, false, true)).charAt(0));
            str = stringBuilder.toString();
        }
        return str;
    }
}
