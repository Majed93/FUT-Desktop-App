package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class NotFoundException extends FutErrorException {
    public NotFoundException(FutError futError, Exception e) {
        super(futError, e);
    }
}
