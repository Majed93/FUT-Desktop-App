package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.domain.LoginAuctionWrapper;
import com.fut.desktop.app.exceptions.FutErrorException;

/**
 * Contract for login service.
 */
public interface LoginService {

    /**
     * Perform login
     *
     * @param id         Account id.
     * @param appVersion App version to access.
     * @return Login auction wrapper with login data & trade pile response.
     */
    LoginAuctionWrapper login(Integer id, String appVersion) throws FutErrorException;
}
