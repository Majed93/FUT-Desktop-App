package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class ConflictException extends FutErrorException {
    public ConflictException(FutError futError, Exception e) {
        super(futError, e);
    }
}
