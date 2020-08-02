package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class ExpiredSessionException extends FutErrorException {
    public ExpiredSessionException(FutError futError, Exception e) {
        super(futError, e);
    }
}
