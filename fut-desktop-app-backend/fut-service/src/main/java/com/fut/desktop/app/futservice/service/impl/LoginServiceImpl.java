package com.fut.desktop.app.futservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.constants.Pile;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.exceptions.FutErrorDto;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.futservice.service.base.LoginService;
import com.fut.desktop.app.futservice.service.base.PinService;
import com.fut.desktop.app.restObjects.Account;
import com.fut.desktop.app.restObjects.LoginWrapper;
import com.fut.desktop.app.utils.EncryptUtil;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Implementation of {@link LoginService}
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    private RestTemplate rest = new RestTemplate();

    private final FileUtils fileUtils;

    private final AccountService accountService;

    private final PinService pinService;

    public LoginServiceImpl(FileUtils fileUtils, AccountService accountService, PinService pinService) {
        this.fileUtils = fileUtils;
        this.accountService = accountService;
        this.pinService = pinService;
    }

    @Override
    public LoginAuctionWrapper login(Integer id, String appVersion) throws FutErrorException {
        Account account;

        try {
            account = accountService.findOne(id);
        } catch (Exception e) {
            log.error("No account setup. Add one first.");
            throw new FutErrorException(new FutError("No account", FutErrorCode.NotFound, "No account found", "", ""));
        }

        Platform platform = account.getPlatform();
        String username = account.getEmail();
        String password = account.getPassword();
        String secretAnswer = account.getAnswer();
        String deviceId = account.getDeviceId();
        AppVersion lastAppVersion = account.getLastLoggedInPlatform() == null ? AppVersion.WebApp : account.getLastLoggedInPlatform();

        LoginDetails loginDetails = new LoginDetails(username, password, secretAnswer, platform, AppVersion.valueOf(appVersion), deviceId, lastAppVersion);

        LoginResponse loginResp = (LoginResponse) fileUtils.readFile(id, FileUtils.dataExt);
        ResponseEntity<LoginAuctionWrapper> loginAuctionWrapperResp;

        HttpHeaders headers = new HttpHeaders();

        LoginWrapper loginWrapper = new LoginWrapper(id, loginDetails, loginResp, account);

        HttpEntity<LoginWrapper> entity = new HttpEntity<>(loginWrapper, headers);

        try {
            loginAuctionWrapperResp = rest.exchange(URI.create(futServiceEndpoint + "/login"), HttpMethod.POST, entity, LoginAuctionWrapper.class);
        } catch (Exception ex) {
            if (ex instanceof HttpServerErrorException) {
                ObjectMapper objectMapper = new ObjectMapper();
                HttpServerErrorException hseEx = (HttpServerErrorException) ex;
                FutErrorDto futErrorDto;
                try {
                    futErrorDto = objectMapper.readValue(hseEx.getResponseBodyAsString(), FutErrorDto.class);
                } catch (IOException e) {
                    throw new FutErrorException(new FutError("Error logging in. Unable to parse the error message. Contact admin",
                            FutErrorCode.PermissionDenied,
                            "Error logging in: " + e.getMessage(), "", ""));
                }
                throw new FutErrorException(new FutError(futErrorDto.getMessage(),
                        FutErrorCode.InternalServerError, "", "", ""));
            } else {
                throw new FutErrorException(new FutError("Error logging in.", FutErrorCode.PermissionDenied,
                        "Error logging in: " + loginResp, "", ""));
            }

        }
        if (loginAuctionWrapperResp.getBody() != null) {
            loginResp = loginAuctionWrapperResp.getBody().getLoginResponse();
            HomeWrapper homeWrapper = loginAuctionWrapperResp.getBody().getHomeWrapper();
            fileUtils.writeFile(loginResp, id, FileUtils.dataExt);
            ResponseEntity<String> cookieResponse = rest.postForEntity(URI.create(futServiceEndpoint + "/login/cookies"), id, String.class);

            if (cookieResponse.getBody() != null) {
                log.error("Unable to write cookies.");
            }

            // Set the pin count
            account.setPinCount(pinService.getPinCount());

            if (homeWrapper != null && homeWrapper.getTradePile() != null) {
                account.setCoins(homeWrapper.getTradePile().getCredits());
            }

            account.setLastLogin(DateTimeExtensions.ToUnixTime());
            List<PileSize> pileSizes = loginAuctionWrapperResp.getBody().getPileSizes();

            if (pileSizes != null) {
                for (PileSize pile : pileSizes) {
                    if (pile.getKey().equals(Pile.TradePile)) {
                        account.setPile_tradePileSize(pile.getValue());
                    } else if (pile.getKey().equals(Pile.WatchList)) {
                        account.setPile_watchListSize(pile.getValue());
                    } else if (pile.getKey().equals(Pile.UnknownPile3)) {
                        account.setPile_unknownPile3(pile.getValue());
                    } else if (pile.getKey().equals(Pile.UnknownPile6)) {
                        account.setPile_unknownPile6(pile.getValue());
                    } else if (pile.getKey().equals(Pile.UnknownPile11)) {
                        account.setPile_unknownPile11(pile.getValue());
                    } else if (pile.getKey().equals(Pile.UnassignedPileSize)) {
                        account.setPile_unassigned(5); // NOTE: explicitly using 5 here.
                    }
                }
            }

            // Save unassigned pile stuff.
            if (homeWrapper != null && homeWrapper.getUnassignedItems() != null
                    && homeWrapper.getUnassignedItems().getAuctionInfo() != null) {
                account.setUnassignedPileCount(loginAuctionWrapperResp.getBody().getHomeWrapper().getUnassignedItems().getAuctionInfo().size());
            }

            account.setLastLoggedInPlatform(loginDetails.getAppVersion());
            accountService.update(account);

            return loginAuctionWrapperResp.getBody();
        }
        throw new FutErrorException(new FutError("Error logging in.", FutErrorCode.PermissionDenied,
                "Error logging in: " + loginResp, "", ""));
    }
}
