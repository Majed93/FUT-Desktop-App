package com.fut.desktop.app.exceptions;

public class FutException extends RuntimeException {

    private static final long serialVersionUID = 3119901352000951168L;

    public FutException(String message) {
        super(message);
    }

    public FutException(String message, Exception e) {
        super(message, e);
    }
}
