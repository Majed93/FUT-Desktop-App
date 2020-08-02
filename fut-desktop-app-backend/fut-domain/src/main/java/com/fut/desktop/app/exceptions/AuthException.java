package com.fut.desktop.app.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthException extends Exception {

    String error;

    public AuthException(String error, Exception ex) {
        super(error, ex);
        this.error = error;
    }
}
