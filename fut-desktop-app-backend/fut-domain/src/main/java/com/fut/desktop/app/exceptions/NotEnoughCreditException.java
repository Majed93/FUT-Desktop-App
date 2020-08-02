package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class NotEnoughCreditException extends FutErrorException {
    public NotEnoughCreditException(FutError futError, Exception e) {
        super(futError, e);
    }
}
