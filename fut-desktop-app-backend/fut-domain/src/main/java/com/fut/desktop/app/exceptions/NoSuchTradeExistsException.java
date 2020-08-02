package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class NoSuchTradeExistsException extends FutErrorException {
    public NoSuchTradeExistsException(FutError futError, Exception e) {
        super(futError, e);
    }
}
