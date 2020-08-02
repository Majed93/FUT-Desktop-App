package com.fut.desktop.app.services.base;

import com.fut.desktop.app.exceptions.FutException;

public interface ITwoFactorCodeProvider {

    String getTwoFactorCodeAsync() throws FutException;
}
