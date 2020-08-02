package com.fut.api.fut.service;

import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TwoFactorCodeProviderImpl implements ITwoFactorCodeProvider {

    private static String _secretKey;

    public TwoFactorCodeProviderImpl(String secretKey) {
        _secretKey = secretKey;
    }

    @Override
    public String getTwoFactorCodeAsync() throws FutException {
        for (int i = 0; i < 5; i++) {

            String code = tryGetCode();

            if (code != null) {
                return code;
            }
            //wait a bit then do again.
            try {
                log.error("Can't authorize code, trying again.. ");

                Thread.sleep(12);

                code = tryGetCode();

                if (code != null) {
                    return code;
                }

                Thread.sleep(6345);
            } catch (InterruptedException e) {
                log.error("Error in getting auth code: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        throw new FutException("Unable to get two-factor authentication code.");
    }

    private String tryGetCode() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        Integer code = gAuth.getTotpPassword(_secretKey);

        if (gAuth.authorize(_secretKey, code)) {
            //return here
            return String.valueOf(code);
        }
        return null;
    }
}
