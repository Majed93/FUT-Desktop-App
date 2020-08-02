package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;

public class PurchasedItemsFullException extends FutErrorException {
    public PurchasedItemsFullException(FutError futError, Exception e) {
        super(futError, e);
    }
}
