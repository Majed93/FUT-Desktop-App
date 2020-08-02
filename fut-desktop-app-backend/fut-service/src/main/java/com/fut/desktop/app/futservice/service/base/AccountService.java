package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.restObjects.Account;

import java.util.List;

/**
 * Contract for Account business logic.
 */
public interface AccountService {

    List<Account> findAll();

    Account findOne(Integer id);

    Account findByEmailAndPlatform(String email, Platform platform);

    Account add(Account account) throws FutErrorException;

    Account update(Account account) throws FutErrorException;

    void delete(Integer id) throws FutErrorException;

    /**
     * Check if account has space in watch list.
     *
     * @param id Id of account.
     * @return true if there is space in watch list - 1.
     * NOTE: -1 to be safe.
     */
    boolean isSpaceInWatchList(Integer id);

    /**
     * Check if account has space in trade pile.
     *
     * @param id Id of account.
     * @return true if there is space in trade pile - 1.
     * NOTE: -1 to be safe.
     */
    boolean isSpaceInTradePile(Integer id);
}
