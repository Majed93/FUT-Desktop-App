package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class CaptchaTriggeredException extends FutErrorException {
    public CaptchaTriggeredException(FutError futError, Exception e) {
        super(futError, e);
    }
}
