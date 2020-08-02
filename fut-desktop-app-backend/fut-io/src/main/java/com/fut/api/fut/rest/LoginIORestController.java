package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.api.fut.service.TwoFactorCodeProviderImpl;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.LoginAuctionWrapper;
import com.fut.desktop.app.domain.LoginDetails;
import com.fut.desktop.app.domain.LoginResponse;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.restObjects.Account;
import com.fut.desktop.app.restObjects.LoginWrapper;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Handle login request.
 */
@RequestMapping("/io/login")
@RestController
@Slf4j
public class LoginIORestController {

    private final IFutClient futClient;

    private final FileUtils fileUtils;

    /**
     * The session limit in minutes
     */
    private static final Integer SESSION_LIMIT = 15;

    @Autowired
    public LoginIORestController(FileUtils fileUtils, IFutClient futClient) {
        this.fileUtils = fileUtils;
        this.futClient = futClient;
    }

    /**
     * All params are encoded.
     *
     * @param loginWrapper Wrapper login info.
     * @return LoginResponse.
     */
    @PostMapping
    @ResponseBody
    public LoginAuctionWrapper login(@RequestBody LoginWrapper loginWrapper) throws Exception {

        LoginDetails loginDetails = loginWrapper.getLoginDetails();
        LoginResponse loginResponse = loginWrapper.getLoginResponse();
        Account account = loginWrapper.getAccount();
        String secretKey = account.getSecretKey();

        BasicCookieStore cookieStore = (BasicCookieStore) fileUtils.readFile(loginWrapper.getId(), FileUtils.cookieExt);

        ITwoFactorCodeProvider codeProvider = new TwoFactorCodeProviderImpl(secretKey);

        futClient.newInstance(cookieStore);

        LoginAuctionWrapper loginAuctionWrapper;

        // Flag for if fresh login needed depending on web or mobile login.
        boolean newLoginNeeded = false;

        if (loginResponse != null) {
            if (loginDetails.getAppVersion() == AppVersion.WebApp) { // First time logging in to web app ever
                if (loginDetails.getLastUsedAppVersion() != AppVersion.WebApp &&
                        StringUtils.isEmpty(loginResponse.getAuthBearerToken()) &&
                        StringUtils.isEmpty(loginResponse.getSessionId())) {
                    newLoginNeeded = true;
                }
            } else {
                // Mobile. // First time logging in to companion app
                if (loginDetails.getLastUsedAppVersion() != AppVersion.CompanionApp &&
                        StringUtils.isEmpty(loginResponse.getMobileAuthBearerToken()) &&
                        StringUtils.isEmpty(loginResponse.getMobileSessionId())) {
                    newLoginNeeded = true;
                }
            }
        }

        // New login
        if (loginResponse == null || newLoginNeeded) {
            //Login
            loginAuctionWrapper = futClient.login(loginDetails, codeProvider).get();
            return loginAuctionWrapper;
        } else {// not new login
            // Set the tokens needed by the login.
            futClient.getFutRequestFactories().setAuthBearerToken(loginResponse.getAuthBearerToken());
            futClient.getFutRequestFactories().setMobileAuthToken(loginResponse.getMobileAuthBearerToken());
            futClient.getFutRequestFactories().setRefreshToken(loginResponse.getRefreshToken());

            // This will be the last time we logged in via EA requests.
            LocalDateTime modified = DateTimeExtensions.FromUnixTime(String.valueOf(loginResponse.getLastLogin()));
            futClient.getFutRequestFactories().setPhishingToken(loginResponse.getPhishingToken());
            if (!DateTimeExtensions.isTimeWithAddedDurationBeforeNow(SESSION_LIMIT, modified, TimeUnit.MINUTES)) {
                // Less than 10 mins - custom use same session id etc.
                //Set login to default stuff.
                // Need to set the page size.
                // Set the resources from the app version
                futClient.getFutRequestFactories().setAppVersion(loginDetails.getAppVersion());
                futClient.getFutRequestFactories().setBaseResources(loginDetails.getAppVersion());

                futClient.getFutRequestFactories().loginRequest(loginDetails, codeProvider);
                futClient.remoteConfig();
                futClient.getFutRequestFactories().setPhishingToken(loginResponse.getPhishingToken());
                futClient.getFutRequestFactories().setSessionId(loginResponse.getSessionId());
                futClient.getFutRequestFactories().setMobileSessionId(loginResponse.getMobileSessionId());
                futClient.getFutRequestFactories().setNucleusId(loginResponse.getNucleusId());
                futClient.getFutRequestFactories().setPersonaId(loginResponse.getPersonaId());
                futClient.getFutRequestFactories().getPinService().setPinRequestCount(account.getPinCount());

                futClient.getFutRequestFactories().setAppVersion(loginDetails.getAppVersion());
                futClient.getFutRequestFactories().getPinService().setBaseAppVersion(loginDetails.getAppVersion());
                futClient.getFutRequestFactories().setPlatform(loginDetails.getPlatform());
                futClient.getFutRequestFactories().setDob(loginResponse.getDob());

                loginAuctionWrapper = new LoginAuctionWrapper(loginResponse, null, null);
                return loginAuctionWrapper;
            } else {
                // Greater than 60 minutes. Can be both. FUT Client will handle if new session needed.
                loginAuctionWrapper = futClient.login(loginDetails, codeProvider).get();
                return loginAuctionWrapper;
            }
        }
    }

    @PostMapping(value = "/cookies")
    @ResponseBody
    public String cookies(@RequestBody Integer id) {
        BasicCookieStore cookies = futClient.getFutRequestFactories().getHttpClient().getCookies();

        Boolean fileWritten = fileUtils.writeFile(cookies, id, FileUtils.cookieExt);
        log.info("Cookie written: " + fileWritten);
        if (!fileWritten) {
            log.error("Unable to save cookies.");
        }

        return fileWritten ? null : "Error occurred saving cookie.";
    }
}
