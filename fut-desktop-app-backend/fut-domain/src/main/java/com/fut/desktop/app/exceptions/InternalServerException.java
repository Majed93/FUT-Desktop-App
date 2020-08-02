package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class InternalServerException extends FutErrorException {
    public InternalServerException(FutError futError, Exception e) {
        super(futError, e);
    }
}
