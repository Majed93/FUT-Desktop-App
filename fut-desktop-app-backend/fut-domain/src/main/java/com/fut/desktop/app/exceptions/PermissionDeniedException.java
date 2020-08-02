package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class PermissionDeniedException extends FutErrorException{
    public PermissionDeniedException(FutError futError, Exception e) {
        super(futError, e);
    }
}
