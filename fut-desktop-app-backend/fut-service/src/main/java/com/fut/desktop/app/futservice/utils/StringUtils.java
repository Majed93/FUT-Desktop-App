package com.fut.desktop.app.futservice.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StringUtils {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Check validity of email
     *
     * @param email email to check
     * @return true if valid, otherwise false
     */
    public boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return (pattern.matcher(email).matches());
    }
}
