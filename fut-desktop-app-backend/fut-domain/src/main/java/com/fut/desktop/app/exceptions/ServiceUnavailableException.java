package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class ServiceUnavailableException extends FutErrorException {
    public ServiceUnavailableException(FutError futError, Exception e) {
        super(futError, e);
    }
}
