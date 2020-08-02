package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class DestinationFullException extends FutErrorException {
    public DestinationFullException(FutError futError, Exception e) {
        super(futError, e);
    }
}
