package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class InvalidDeckException extends FutErrorException {
    public InvalidDeckException(FutError futError, Exception e) {
        super(futError, e);
    }
}
