package com.fut.desktop.app.futservice.service.impl;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.futservice.repository.AccountRepository;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.restObjects.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findOne(Integer id) {
        return accountRepository.findOne(id);
    }

    @Override
    public Account findByEmailAndPlatform(String email, Platform platform) {
        return accountRepository.findByEmailAndPlatform(email, platform);
    }

    @Override
    public Account add(Account account) throws FutErrorException {
        if (findByEmailAndPlatform(account.getEmail(), account.getPlatform()) != null) {
            throw new FutErrorException(new FutError("Account already exists.", FutErrorCode.InternalServerError,
                    "Account already exists", "", ""));
        }

        long seed = UUID.nameUUIDFromBytes((account.getEmail() + "|" + account.getPlatform().name()).getBytes()).getMostSignificantBits();
        Random random = new Random(seed);
        Integer id = random.nextInt(Integer.MAX_VALUE) + 1;

        account.setId(id);
        account.setCoins(0);
        account.setTimeFinish(String.valueOf(DateTimeExtensions.ToUnixTime()));
        account.setTotalSession(0);
        account.setWatchListCount(0);
        account.setTradePileCount(0);
        account.setUnassignedPileCount(0);
        account.setLastLogin(DateTimeExtensions.ToUnixTime());
        account.setDeviceId("0");
        account.setPinCount(0);

        // Setting the pile sizes to default of 0.
        account.setPile_tradePileSize(0);
        account.setPile_watchListSize(0);
        account.setPile_unknownPile3(0);
        account.setPile_unknownPile6(0);
        account.setPile_unknownPile11(0);
        account.setPile_unassigned(0);

        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) throws FutErrorException {
        Account checkAccount = findByEmailAndPlatform(account.getEmail(), account.getPlatform());

        if (checkAccount != null && !Objects.equals(checkAccount.getId(), account.getId())) {
            throw new FutErrorException(new FutError("Account already exists.", FutErrorCode.InternalServerError,
                    "Account already exists", "", ""));
        }

        return accountRepository.save(account);
    }

    @Override
    public void delete(Integer id) throws FutErrorException {
        accountRepository.delete(id);
    }

    @Override
    public boolean isSpaceInWatchList(Integer id){
        Account account = findOne(id);

        // 95 < 100 - 1
        return account.getWatchListCount() < account.getPile_watchListSize() - 1;
    }

    @Override
    public boolean isSpaceInTradePile(Integer id){
        Account account = findOne(id);

        return account.getTradePileCount() < account.getPile_tradePileSize() - 1;
    }
}
