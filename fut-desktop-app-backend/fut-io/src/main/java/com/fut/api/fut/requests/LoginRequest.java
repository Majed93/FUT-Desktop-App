package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.service.HasherImpl;
import com.fut.api.fut.utils.HttpUtils;
import com.fut.desktop.app.constants.*;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.services.base.IHasher;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import com.fut.desktop.app.utils.SleepUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.AsyncResult;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class LoginRequest extends FutRequestBase implements IFutRequest<LoginResponse> {

    private LoginDetails loginDetails;
    private ITwoFactorCodeProvider iTwoFactorCodeProvider;
    private IHasher iHasher;
    private String personaId;
    private PinService pinService;
    private String futSimEndpoint;

    public LoginRequest(LoginDetails loginDetails, ITwoFactorCodeProvider iTwoFactorCodeProvider,
                        PinService pinService) {

        if (loginDetails == null) {
            throw new NullPointerException("Login details null");
        }

        iHasher = new HasherImpl();
        this.loginDetails = loginDetails;
        this.iTwoFactorCodeProvider = iTwoFactorCodeProvider;
        this.pinService = pinService;

        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("application.properties")));
            futSimEndpoint = config.getString("fut.simulator.endpoint");
        } catch (ConfigurationException e) {
            log.error("Error loading properties {}", e.getMessage());
        }
    }

    public void SetCookies(BasicCookieStore cookies) {
        httpClient.setCookies(cookies);
    }

    @Override
    public Future<LoginResponse> PerformRequestAsync() throws FutException {
        try {
            // If it's the web app then get the main page.
            if (getBaseAppVersion() == AppVersion.WebApp) {
                //get main page
                getMainPage().get();
                SleepUtil.sleep(500, 600);
            }

            if (getBaseAppVersion() == AppVersion.CompanionApp) {
                return mobileLogin();
            } else if (getBaseAppVersion() == AppVersion.WebApp) {
                return webAppLogin();
            } else {
                log.error("Invalid app version: {}", getBaseAppVersion());
                throw new IllegalArgumentException("Invalid app version.");
            }
        } catch (Exception e) {
            log.error("Unable to login: " + baseAppVersion + ": ", e.getMessage());
            log.error("Try again or check on the web/companion app that your account works.");

            throw new FutErrorException(new FutError("Unable to login: " + e.getMessage(), FutErrorCode.InternalServerError,
                    "Unable to login: " + e.getMessage(), e.getCause().toString(), ""), e);
        }
    }

    /**
     * Do mobile login
     *
     * @return The login response for mobile app.
     * @throws Exception .
     */
    private Future<LoginResponse> mobileLogin() throws Exception {
        // Logged in before so no need to go through ENTIRE process.
        // And we've used the same app - web or mobile, otherwise we need a fresh login.
        if (getBaseMobileAuthBearerToken() != null
                && !getBaseMobileAuthBearerToken().isEmpty()) {
            String authBearerToken = getBaseMobileAuthBearerToken();
            //Get nucleus and dob
            sendPinEventWebAppLoad();

            // Code isn't actually used.. just cause it's what EA do.
            HttpMessage message = mobileAuthCodePreviouslyLoggedIn().get();
            JsonObject object = new JsonParser().parse(message.getContent()).getAsJsonObject();

            if (object.get("code") == null && object.get("error") != null) {
                log.error("Error code: {}", object.get("error").getAsString());
                throw new FutErrorException(new FutError("Error:  " + object.get("error").getAsString(),
                        FutErrorCode.InternalServerError, "Error:  " + object.get("error").getAsString(), "", ""));
            }
            String authCode = object.get("code").getAsString();

            Future<String[]> nucleusIdFuture = getNucleusId(authBearerToken);
            String[] nucleusDob;
            String refreshToken = getBaseRefreshToken();

            if (nucleusIdFuture == null) {
                // 403 With current auth token so we need to get another one.
                HttpMessage authBearerTokenMsg = getMobileAuthToken(authCode, getBaseRefreshToken()).get();
                AuthToken authBearerTokenObj = (AuthToken) deserializeAsync(authBearerTokenMsg, AuthToken.class).get();
                authBearerToken = authBearerTokenObj.getAccess_token();
                refreshToken = authBearerTokenObj.getRefresh_token();
                nucleusIdFuture = getNucleusId(authBearerToken);

                // If still null throw error.
                if (nucleusIdFuture == null) {
                    throw new FutException("Unable to login. Can't get nucleus Id");
                }
                nucleusDob = nucleusIdFuture.get();
            } else {
                // It's authed so all good
                nucleusDob = nucleusIdFuture.get();
            }
            return new AsyncResult<>(performAccountDataRequest(authBearerToken, nucleusDob, refreshToken));
        } else {
            // Very first one.
            // Explicit pin event
            pinService.setProperties(PinService.EMPTY_PID, "", "",
                    PinEventId.WebApp_Load, "", loginDetails.getPlatform());
            pinService.sendPinEvent(PinEventId.WebApp_Load).get();

            // Send https://accounts.ea.com/connect/auth?prompt=login&accessToken=&client_id=FIFA-19-MOBILE-COMPANION&response_type=code&display=web2/login&locale=en_US&machineProfileKey=e0d19aa1115b1e34&release_type=prod&scope=basic.identity+offline+signin
            // Will result in 302 and redirect to sign in page.

            String authBearerTokenUrl = doFreshLoginAndGetBearerToken(); // Mobile

            // Get the auth bearer access token
            String authCode = generateBearerToken(authBearerTokenUrl);
            HttpMessage authBearerTokenMsg = getMobileAuthToken(authCode, null).get();
            AuthToken authBearerTokenObj = (AuthToken) deserializeAsync(authBearerTokenMsg, AuthToken.class).get();
            String authBearerToken = authBearerTokenObj.getAccess_token();

            String[] nucleusDob = getNucleusDob(authBearerToken);

            return new AsyncResult<>(performAccountDataRequest(authBearerToken, nucleusDob, authBearerTokenObj.getRefresh_token()));
        }
    }

    /**
     * Perform web app login
     *
     * @return The login response for web app
     * @throws Exception .
     */
    private Future<LoginResponse> webAppLogin() throws Exception {
        // Logged in before so no need to go through ENTIRE process.
        // And we've used the same app - web or mobile, otherwise we need a fresh login.
        if (getBaseAuthBearerToken() != null
                && !getBaseAuthBearerToken().isEmpty()) {
            String authBearerToken = getBaseAuthBearerToken();
            //Get nucleus and dob
            sendPinEventWebAppLoad();

            // Process secondary authorization bearer token
            boolean result = getSecondaryAuthBearTokenAndGatewaySubc();
            if (!result) {
                // Error
                log.info("Looks like fresh login required.");
                String authBearerTokenUrl = doFreshLoginAndGetBearerToken();
                //Get the auth bearer access token
                authBearerToken = generateBearerToken(authBearerTokenUrl);

                //Get nucleus and dob
                sendPinEventWebAppLoad();

                // Process secondary authorization bearer token
                getSecondaryAuthBearTokenAndGatewaySubc();

                String[] nucleusDob = getNucleusDob(authBearerToken);

                return new AsyncResult<>(performAccountDataRequest(authBearerToken, nucleusDob, getBaseRefreshToken()));
            }

            Future<String[]> nucleusIdFuture = getNucleusId(authBearerToken);
            String[] nucleusDob;

            if (nucleusIdFuture == null) {
                // 403 With current auth token so we need to get another one.
                BasicCookieStore temp = new BasicCookieStore();

                // Add all to temp.
                temp.addCookies(httpClient.getCookies().getCookies().toArray(new Cookie[0]));

                // Clear cookies and set them again.
                httpClient.setCookies(new BasicCookieStore());
                httpClient.setFollowRedirect();
                httpClient.getContext().setCookieStore(new BasicCookieStore());

                isLoggedIn().get();

                HttpMessage message;

                httpClient.setCookies(temp);
                httpClient.setFollowRedirect();
                httpClient.getContext().setCookieStore(temp);

                // Do redirect
                message = handleRedirect(authBearerToken).get();
                try {
                    authBearerToken = message.getResponse().getHeaders(org.apache.http.HttpHeaders.LOCATION)[0].getValue();
                    authBearerToken = generateBearerToken(authBearerToken);
                } catch (Exception ex) {
                    log.error("Error getting location header. {}", ex.getMessage());
                    authBearerToken = doFreshLoginAndGetBearerToken(); // This will return a url, just reusing instead of creating a new variable
                    authBearerToken = generateBearerToken(authBearerToken);
                }


                // https://accounts.ea.com/connect/auth? + current bearer token
                sendPinEventWebAppLoad();

                nucleusIdFuture = getNucleusId(authBearerToken);

                // If still null throw error.
                if (nucleusIdFuture == null) {
                    throw new FutException("Unable to login. Can't get nucleus Id");
                }
                nucleusDob = nucleusIdFuture.get();
            } else {
                nucleusDob = nucleusIdFuture.get();
            }

            setBaseDob(nucleusDob[1]);
            return new AsyncResult<>(performAccountDataRequest(authBearerToken, nucleusDob, getBaseRefreshToken()));
        } else {
            // Very first one.
            // Explicit pin event
            pinService.setProperties(PinService.EMPTY_PID, "", "",
                    PinEventId.WebApp_Load, "", loginDetails.getPlatform());
            pinService.sendPinEvent(PinEventId.WebApp_Load).get();
            pinService.resetPinCount();
            // End pin event.

            // Send https://accounts.ea.com/connect/auth?response_type=token&redirect_uri=nucleus%3Arest&prompt=none&client_id=ORIGIN_JS_SDK to check same logic as below

            //make request to check if login required
            //https://accounts.ea.com/connect/auth?client_id=FIFA-19-WEBCLIENT&response_type=token&display=web2/login&locale=en_US&redirect_uri=nucleus:rest&prompt=none&scope=basic.identity+offline+signin
            // this should send an error back

            HttpMessage secondaryAuthTokenCheck = tryGetSecondaryAuthBearerToken().get();
            HttpMessage loginRequestCheck = isLoggedIn().get();

            JsonObject sATCJsonObject = new JsonParser().parse(secondaryAuthTokenCheck.getContent()).getAsJsonObject();
            String sATCError = sATCJsonObject.get("error").getAsString();
            JsonObject jsonObject = new JsonParser().parse(loginRequestCheck.getContent()).getAsJsonObject();
            String error = jsonObject.get("error").getAsString();

            String loginReqdStr = "login_required";

            // Both responses should contain the above string because it's a fresh login
            if (sATCError.contains(loginReqdStr) && error.contains(loginReqdStr)) {
                //at this stage i should be at the sign in page?
                //now request this
                //https://accounts.ea.com/connect/auth?prompt=login&accessToken=null&client_id=FIFA-19-WEBCLIENT&response_type=token&display=web2/login&locale=en_US&redirect_uri=https://www.easports.com/uk/fifa/ultimate-team/web-app/auth.html&scope=basic.identity+offline+signin
                // this should do a 302 and redirect somewhere like signin.ea.com
                // Process login form
                String authBearerTokenUrl = doFreshLoginAndGetBearerToken();
                //Get the auth bearer access token
                String authBearerToken = generateBearerToken(authBearerTokenUrl);

                //Get nucleus and dob
                sendPinEventWebAppLoad();

                // Process secondary authorization bearer token
                getSecondaryAuthBearTokenAndGatewaySubc();

                String[] nucleusDob = getNucleusDob(authBearerToken);

                return new AsyncResult<>(performAccountDataRequest(authBearerToken, nucleusDob, getBaseRefreshToken()));
            } else {
                log.error("Responses not as expected: Secondary Auth Bearer Token: {} | Logging Check: {}", sATCJsonObject, jsonObject);
            }
            // If it gets here then i assume it means we don't need to login?
            System.out.println("Shouldn't get here for login request..");
            return null;
        }
    }

    private String[] getNucleusDob(String authBearerToken) throws Exception {
        Future<String[]> nucleusIdFuture = getNucleusId(authBearerToken);
        String[] nucleusDob;
        if (nucleusIdFuture != null) {
            nucleusDob = nucleusIdFuture.get();
            setBaseDob(nucleusDob[1]);
            return nucleusDob;
        } else {
            throw new FutException("Unable to login. Can't get nucleus Id");
        }
    }

    /**
     * Tries to get the secondary authorization bearer token and sends request for gateway/subscription/pid.
     *
     * @throws Exception .
     */
    private boolean getSecondaryAuthBearTokenAndGatewaySubc() throws Exception {

        // Need to try and get the secondary auth bearer token.
        HttpMessage secondaryBearerTokenResp = tryGetSecondaryAuthBearerToken().get();

        // Check if it contains an error here
        JsonObject sATCJsonObject = new JsonParser().parse(secondaryBearerTokenResp.getContent()).getAsJsonObject();
        if(sATCJsonObject != null && sATCJsonObject.get("error") != null) {
            String sATCError = sATCJsonObject.get("error").getAsString();

            String loginReqdStr = "login_required";

            // Both responses should contain the above string because it's a fresh login
            if (sATCError != null && sATCError.contains(loginReqdStr)) {
                return false;
            }
        }
        AuthToken secondaryAuthToken = (AuthToken) deserializeAsync(secondaryBearerTokenResp, AuthToken.class).get();
        Future<String[]> nucleusIdFutureWithSecondary = getNucleusId(secondaryAuthToken.getAccess_token());

        // Shouldn't actually get here because by this stage the first request to get the secondary auth token
        // Is done
        if (nucleusIdFutureWithSecondary == null) {
            log.error("Error getting secondary auth bearer token");
            throw new FutErrorException(new FutError("Unable to get secondary auth bearer token on fresh login.", FutErrorCode.InternalServerError, "Unable to get secondary auth bearer token", "", ""));
        }

        gatewaySubscriptionsRequest(secondaryAuthToken.getAccess_token(), nucleusIdFutureWithSecondary.get()[0]);
        return true;
    }

    /**
     * Send request for https://gateway.ea.com/proxy/subscription/pids/<NUCLEUS_ID>/subscriptionsv2/groups/Origin%20Membership?state=ENABLED,PENDINGEXPIRED
     *
     * @param access_token The access token to use.
     * @param nucleusId    The nucleus id to use.
     */
    private void gatewaySubscriptionsRequest(String access_token, String nucleusId) throws Exception {
        addGenericHeaders();
        httpClient.addRequestHeader("Host", "gateway.ea.com");
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization");
        httpClient.removeRequestHeader("Cookies");
        //Do options request first
        String url = Resources.GatewaySubscriptionPid.replaceAll(Resources.GATEWAY_SUBSCRIPTION_REPLACE, nucleusId);

        HttpMessage optionsResp = httpClient.OptionAsync(url).get();

        int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
            handleExceptionAndStatusCode("Error sending option request for:" + url, optionCode);
        }

        removeAccessControlHeaders();
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        httpClient.removeRequestHeader(HttpHeaders.Accept);
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        addAcceptHeader("application/json; charset=utf-8; */*; q=0.01");
        httpClient.removeRequestHeader("Cookies");

        //Add bearer stuff now
        httpClient.addRequestHeader("Authorization", "Bearer " + access_token);

        HttpMessage httpResponse = httpClient.GetAsync(url).get();

        int statusCode = httpResponse.getResponse().getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.FORBIDDEN.value()) {
            throw new FutErrorException(new FutError("Error sending " + url, FutErrorCode.InternalServerError, "Error sending " + url, "", ""));
        }

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Error getting nucleus id: " + statusCode, statusCode);
        }
    }

    /**
     * Process the sign in page. May or may not need 2FA Code here.
     *
     * @return URL with bearer token to be parsed out
     * @throws Exception Handle errors.
     */
    private String doFreshLoginAndGetBearerToken() throws Exception {
        // should have the cookie here but shouldn't have to worry about that.
        HttpMessage signInPage = getSignInPage().get();
        HttpMessage loginResp = login(loginDetails, signInPage).get();
        HttpMessage redirectResp = loginRedirect(loginResp).get(); //This is now at the point where i need to select which auth method to use.
        SleepUtil.sleep(325, 500);

        if (redirectResp.getContent().contains("App Authenticator") && redirectResp.getContent().contains("Use my")) {
            HttpMessage selectedAuthMethod = selectAuthMethod(redirectResp).get(); // Now we can enter the code.
            HttpMessage enterCode = setTwoFactorCodeAsync(selectedAuthMethod).get();
            return enterCode.getRequest().getURI().toString();
        } else {
            return redirectResp.getRequest().getURI().toString();
        }
    }

    /**
     * Parse out the bearer token from the url
     *
     * @param authBearerToken String with token to be prcoessed
     * @return Parsed out string.
     */
    private String generateBearerToken(String authBearerToken) {
        // the uri can be parsed to get the bearer token.

        if (getBaseAppVersion() == AppVersion.WebApp) {
            authBearerToken = authBearerToken.substring(authBearerToken.indexOf("=") + 1);
            return authBearerToken.substring(0, authBearerToken.indexOf('&'));
        } else {
            return authBearerToken.substring(authBearerToken.indexOf('=') + 1);
        }
    }

    /**
     * Perform request to build up account data
     *
     * @param authBearerToken the auth token.
     * @param nucleusDob      Nucleus id[0] DOB[1]
     * @throws Exception Handle any exceptions
     */
    private LoginResponse performAccountDataRequest(String authBearerToken, String[] nucleusDob, String refreshToken) throws Exception {
        // Get the shards
        Shards shards = getShards(nucleusDob[0]).get();

        //Get the user accounts
        UserAccounts userAccounts = getUserAccounts(loginDetails.getPlatform(), nucleusDob[0]).get();

        //Need to get an auth code
        String idAuthCode = getIdAuthCode(authBearerToken).get();

        //Get session id now.
        SessionAuthResponse sessionId = getSessionId(userAccounts, idAuthCode, loginDetails.getPlatform()).get();

        // Send login pin event
        sendLoginPinEvent(nucleusDob[0], sessionId.getSid());

        //Phishing token NOTE: Doesn't exist anymore
//        String phishingToken = validate(loginDetails.getSecretAnswer(), sessionId.getSid(), nucleusDob[0], loginDetails.getPlatform()).get();

        //Login response
        if (getBaseAppVersion() == AppVersion.WebApp) {
            return new LoginResponse(nucleusDob[0], shards, userAccounts, sessionId.getSid(),
                    sessionId.getPhishingToken(), personaId, nucleusDob[1], authBearerToken, pageSize, DateTimeExtensions.ToUnixTime(), getBaseMobileAuthBearerToken(), getBaseMobileSessionId(), getBaseRefreshToken());
        } else if (getBaseAppVersion() == AppVersion.CompanionApp) {
            return new LoginResponse(nucleusDob[0], shards, userAccounts, getBaseSessionId(),
                    sessionId.getPhishingToken(), personaId, nucleusDob[1], getBaseAuthBearerToken(), pageSize, DateTimeExtensions.ToUnixTime(), authBearerToken, sessionId.getSid(), refreshToken);
        } else {
            throw new IllegalArgumentException("Incorrect app version provided");
        }
    }

    /**
     * Do redirect but don't follow it.
     *
     * @param authBearerToken old auth token
     * @return Redirected response future object
     * @throws Exception Handle exceptions
     */
    private Future<HttpMessage> handleRedirect(String authBearerToken) throws Exception {
        httpClient.clearRequestHeaders();
        addAcceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
        addUserAgent();

        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        httpClient.addRequestHeader(NonStandardHttpHeaders.Upgrade_Insecure_Requests, "1");
        httpClient.addRequestHeader("Host", "accounts.ea.com");
        // https://accounts.ea.com/connect/auth?accessToken=QVQxOjEuMDozLjA6NTk6UE1rcnkyUGtKWWhpVzUyc3JuZm5ORzZXcDFCVG02UklBNHg6OTI0MzA6bzRzb20
        // &client_id=FIFA-19-WEBCLIENT&response_type=token&display=web2/
        // login&locale=en_US&redirect_uri=https://www.easports.com/uk/fifa/ultimate-team/web-app/auth.html&scope=basic.identity+offline+signin
        String uri = "https://accounts.ea.com/connect/auth?accessToken=" +
                authBearerToken +
                "&client_id=FIFA-" + Resources.FUT_YEAR + "-WEBCLIENT&response_type=token&display=web2/" +
                "login&locale=en_US&redirect_uri=https://www.easports.com/uk/fifa/ultimate-team/web-app/auth.html&scope=basic.identity+offline+signin";

        httpClient.setDoNotFollowRedirect();
        HttpMessage httpResponse = httpClient.GetAsync(uri).get();
        httpClient.setFollowRedirect();
        return new AsyncResult<>(httpResponse);
    }


    private Future<String> getIdAuthCode(String authBearerToken) throws Exception {
        // https://accounts.ea.com/connect/auth?client_id=FOS-SERVER&redirect_uri=nucleus:rest&response_type=code&access_token=
        // QVQxOjEuMDozLjA6NjA6MU9ETUd4dmZScDRCMkVLaVFMYXRHdk42eGd2emtSNlgxY2Y6ODE4NzA6b2k2dWM&release_type=prod

        String uri = Resources.AccountsAuthCode + authBearerToken + Resources.AccountsAuthCodePrefix;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            addGenericHeaders();
            addReferrerHeader(baseResources.Home); // Add the referrer header
            httpClient.addRequestHeader("Host", "accounts.ea.com");
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type");


            //Do options request first
            HttpMessage optionsResp = httpClient.OptionAsync(uri).get();

            int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
                handleExceptionAndStatusCode("Error sending option request for:" + uri, optionCode);
            }

            // Now send get request
            removeAccessControlHeaders();
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            httpClient.removeRequestHeader("Cookies");
        } else {
            // Mobile
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader("Host", "accounts.ea.com");

            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addMobileUserAgent();
            addRequestWithHeader();
        }
        HttpMessage httpResponse = httpClient.GetAsync(uri).get();

        int statusCode = httpResponse.getResponse().getStatusLine().getStatusCode();

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Error getting : " + uri + " | " + statusCode, statusCode);
        }

        JsonObject object = new JsonParser().parse(httpResponse.getContent()).getAsJsonObject();

        return new AsyncResult<>(object.get("code").getAsString());
    }

    private Future<HttpMessage> selectAuthMethod(HttpMessage redirectResp) throws Exception {
        HttpEntity httpEntity = new StringEntity("codeType=APP&_eventId=submit");
        String uri = redirectResp.getRequest().getURI().toString();

        Future<HttpMessage> selectAuthMethod = httpClient.PostAsync(uri, httpEntity);

        return new AsyncResult<>(selectAuthMethod.get());
    }

    /**
     * This looks like a redirect.
     *
     * @return Sign in page
     * @throws Exception Handle any errors.
     */
    private Future<HttpMessage> getSignInPage() throws Exception {
        httpClient.clearRequestHeaders();
        addSecurityUpgradedHeader();

        if (getBaseAppVersion() == AppVersion.WebApp) {
            addUserAgent();
            addAcceptEncodingHeader();
            addAcceptLanguageHeader();
            addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        } else {
            addMobileUserAgent();
            addRequestWithHeader();
            addMobileAcceptEncodingHeader();
            addMobileAcceptLanguageHeader();
        }

        addAcceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpClient.addRequestHeader(NonStandardHttpHeaders.Upgrade_Insecure_Requests, "1");

        Future<HttpMessage> entityFuture = httpClient.GetAsync(getBaseResources().AuthCode);

        return new AsyncResult<>(entityFuture.get());
    }

    @Deprecated
    private Future<String> getAnswerPage(String sessionId, String nucleusId, Platform platform, String token) throws Exception {
        //Send options request first
        addGenericHeaders();
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");

        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);


        httpClient.addRequestHeader("Host", "utas.external." + (platform == Platform.XboxOne || platform == Platform.Xbox360 ? "s3" : "s2") + ".fut.ea.com");

        if (token != null) {
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-phishing-token,x-ut-sid");
        } else {
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-sid");
        }

        switch (platform) {
            case Xbox360:
            case XboxOne:
                break;
            default:
                baseResources.Question = baseResources.Validate.replaceAll("s3", "s2");
                break;
        }


        long timestamp = DateTimeExtensions.ToUnixTime();

        String uri = baseResources.Question + timestamp;

        HttpMessage optionsResp = httpClient.OptionAsync(uri).get();

        int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
            handleExceptionAndStatusCode("Error sending option request for:" + baseResources.Question, optionCode);
        }

        //Send get request
        removeAccessControlHeaders();
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        httpClient.removeRequestHeader(HttpHeaders.Accept);
        addAcceptHeader("text/plain, */*; q=0.01");
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");

        if (token != null) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.PhishingToken, token);
        }

        httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, sessionId);
        httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, nucleusId);

        HttpMessage getResp = httpClient.GetAsync(uri).get();

        int getCode = getResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(getCode)) {
            handleExceptionAndStatusCode("Error sending option request for:" + baseResources.Question, getCode);
        }

        // This might happen if we've logged in before but cookie has expired.
        if (token != null) {
            try {
                ValidateResponse validateResponse = (ValidateResponse) deserializeAsync(getResp, ValidateResponse.class).get();

                return new AsyncResult<>(validateResponse.getToken());
            } catch (Exception ex) {
                log.error("Can't deserialize validateResponse", ex.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * NOTE: Depcrecated as of FUT 19 28/12/18.
     * Validate the secret answer
     *
     * @param answer    .
     * @param sessionId .
     * @return validation token
     * @throws Exception .
     */
    @Deprecated
    private Future<String> validate(String answer, String sessionId, String nucleusId, Platform platform) throws Exception {
        Future<String> answerPage = getAnswerPage(sessionId, nucleusId, platform, getBasePhishingToken());

        if (answerPage != null) {
            return new AsyncResult<>(answerPage.get());
        }

        // Explicit pin event
        pinService.setProperties(Long.parseLong(nucleusId), personaId, sessionId,
                PinEventId.WebApp_Login, baseDob, loginDetails.getPlatform());
        pinService.sendPinEvent(PinEventId.WebApp_Login).get();
        // End pin event.

        //Send options request first
        httpClient.clearRequestHeaders();
        addUserAgent();
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
        addAcceptHeader("*/*");
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        addAccessControlHeaders(HttpMethod.POST);
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-sid");
        httpClient.addRequestHeader(NonStandardHttpHeaders.Origin, "https://www.easports.com");
        httpClient.addRequestHeader("Host", "utas.external." + (platform == Platform.XboxOne || platform == Platform.Xbox360 ? "s3" : "s2") + ".fut.ea.com");

        switch (platform) {
            case Xbox360:
            case XboxOne:
                break;
            default:
                baseResources.Validate = baseResources.Validate.replaceAll("s3", "s2");
                break;
        }


        String hashed = iHasher.hash(answer);

        String uri = baseResources.Validate + hashed;

        HttpMessage optionsResp = httpClient.OptionAsync(uri).get();

        int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
            handleExceptionAndStatusCode("Error sending option request for:" + baseResources.Validate, optionCode);
        }

        //Send post request
        removeAccessControlHeaders();
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        httpClient.removeRequestHeader(HttpHeaders.Accept);
        addAcceptHeader("text/plain, */*; q=0.01");
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");


        httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, sessionId);
        httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, nucleusId);

        HttpEntity httpEntity = new StringEntity(hashed);
        HttpMessage message = httpClient.PostAsync(baseResources.Validate + hashed, httpEntity).get();

        int postCode = message.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(postCode)) {
            handleExceptionAndStatusCode("Error answering question:" + baseResources.Validate, postCode);
        }

        ValidateResponse validateResponse = (ValidateResponse) deserializeAsync(message, ValidateResponse.class).get();

        //Does it again for some reason.
        Future<String> newTokenJSONFuture = getAnswerPage(sessionId, nucleusId, platform, validateResponse.getToken());

        if (newTokenJSONFuture != null) {
            String newTokenJSON = newTokenJSONFuture.get();
            return new AsyncResult<>(newTokenJSON);
        } else {
            throw new FutException("Can't validate security question.");
        }
    }

    /**
     * Get session id
     *
     * @param userAccounts .
     * @param platform     .
     * @return session id
     * @throws Exception .
     */
    private Future<SessionAuthResponse> getSessionId(UserAccounts userAccounts, String idAuthCode, Platform platform) throws Exception {
        Collection<Persona> personas = userAccounts.getUserAccountInfo().getPersonas();
        Persona persona = null;

        for (Persona p : personas) {
            for (UserClub club : p.getUserClubList()) {
                if (club.getPlatform().equals(getNucleusPersonaPlatform(platform))) {
                    persona = p;
                }
            }
        }

        if (persona == null) {
            throw new FutException("Couldn't find a persona matching the selected platform");
        }

        personaId = String.valueOf(persona.getPersonaId());

        String sku = getBaseAppVersion() == AppVersion.WebApp ? "FUT19WEB" : "FUT19AND"; // Web or Android
        String clientVersion = getBaseAppVersion() == AppVersion.WebApp ? "1" : "5"; // Client version

        // TODO: Could make a POJO and let spring handle json conversion
        HttpEntity httpEntity = new StringEntity("{" +
                "\"isReadOnly\":false," +
                "\"sku\":\"" + sku + "\"," +
                "\"clientVersion\":" + clientVersion + "," +
                "\"locale\":\"en-US\"," +
                "\"method\":\"authcode\"," +
                "\"priorityLevel\":4," +
                "\"identification\":{\"authCode\":\"" + idAuthCode + "\",\"redirectUrl\":\"nucleus:rest\"}," +
                "\"nucleusPersonaId\":" + persona.getPersonaId() + "," +
                "\"gameSku\":\"" + getGameSku(platform) + "\"" +
                "}");

        switch (platform) {
            case Xbox360:
            case XboxOne:
                break;
            default:
                baseResources.Auth = baseResources.Auth.replaceAll("s3", "s2");
                break;
        }

        if (getBaseAppVersion() == AppVersion.WebApp) {
            //Send options request first
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-cache");

            addUserAgent();
            addAcceptEncodingHeader();
            addAcceptLanguageHeader();
            addAcceptHeader("*/*");
            addReferrerHeader(baseResources.Home);
            addAccessControlHeaders(HttpMethod.POST);
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);

            if (getBasePhishingToken() != null) {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,x-ut-phishing-token");
            } else {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type");
            }

            httpClient.addRequestHeader(NonStandardHttpHeaders.Origin, "https://www.easports.com");
            httpClient.addRequestHeader("Host", "utas.external." + (platform == Platform.XboxOne || platform == Platform.Xbox360 ? "s3" : "s2") + ".fut.ea.com");

            HttpMessage optionsResp = httpClient.OptionAsync(baseResources.Auth).get();

            int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
                handleExceptionAndStatusCode("Error sending option request for:" + baseResources.Auth, optionCode);
            }

            removeAccessControlHeaders();
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        } else {
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-cache");
            httpClient.addRequestHeader("Host", "utas.external." + (platform == Platform.XboxOne || platform == Platform.Xbox360 ? "s3" : "s2") + ".fut.ea.com");
            httpClient.addRequestHeader(NonStandardHttpHeaders.Origin, "file://");
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addMobileUserAgent();
            addRequestWithHeader();
        }

        //Send get request

        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");

        if (getBasePhishingToken() != null) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.PhishingToken, getBasePhishingToken());
        }

        HttpMessage message = httpClient.PostAsync(baseResources.Auth, httpEntity).get();

        int statusCode = message.getResponse().getStatusLine().getStatusCode();

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Error in getting session id: " + statusCode + " | " + baseResources.Auth, statusCode);
        }

        SessionAuthResponse sessionAuthResponse = (SessionAuthResponse) deserializeAsync(message, SessionAuthResponse.class).get();
        return new AsyncResult<>(sessionAuthResponse);
    }

    /**
     * Get SKU
     *
     * @param platform .
     * @return SKU
     * @throws Exception .
     */
    private String getGameSku(Platform platform) throws Exception {
        switch (platform) {
            case Ps3:
                return "FFA" + Resources.FUT_YEAR + "PS3";
            case Ps4:
                return "FFA" + Resources.FUT_YEAR + "PS4";
            case Xbox360:
                return "FFA" + Resources.FUT_YEAR + "XBX";
            case XboxOne:
                return "FFA" + Resources.FUT_YEAR + "XBO";
            case Pc:
                return "FFA" + Resources.FUT_YEAR + "PCC";
            default:
                throw new Exception("Platform out of range: " + platform);
        }
    }

    /**
     * Get nucleus platform
     *
     * @param platform .
     * @return Nucleus platform.
     * @throws Exception .
     */
    private String getNucleusPersonaPlatform(Platform platform) throws Exception {
        switch (platform) {
            case Ps3:
            case Ps4:
                return "ps3";
            case Xbox360:
            case XboxOne:
                return "360";
            case Pc:
                return "pc";
            default:
                throw new Exception("Platform out of range: " + platform);
        }

    }

    /**
     * Get userAccounts
     *
     * @param platform .
     * @return UserAccounts
     * @throws Exception .
     */
    private Future<UserAccounts> getUserAccounts(Platform platform, String nucleusId) throws Exception {
        //All options request in a row then all get requests in a row. Only thing that's diff is the host.


        // Do s2 first - both OPTIONS and GET.

        // s2
        httpClient.removeRequestHeader("Host");
        httpClient.addRequestHeader("Host", "utas.external.s2.fut.ea.com");

        if (getBaseAppVersion() == AppVersion.WebApp) {
            prepareUtasHeaders();

            if (getBasePhishingToken() != null) {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-phishing-token");
            } else {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id");
            }

            HttpMessage optionsRespUtasS2 = httpClient.OptionAsync(Resources.UTAS_S2 + baseResources.AccountInfo).get();

            int optionCodeUtasS2 = optionsRespUtasS2.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCodeUtasS2)) {
                handleExceptionAndStatusCode("Error sending option request for:" + baseResources.AccountInfo + " s2 utas", optionCodeUtasS2);
            }

            // Need to set the correct headers first.
            removeAccessControlHeaders();
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            httpClient.removeRequestHeader(HttpHeaders.Accept);
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("text/plain, */*; q=0.01");
        } else {
            // Mobile
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addMobileUserAgent();
            addRequestWithHeader();
        }


        // s2
        httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, nucleusId);
        addReferrerHeader(baseResources.Home);
        httpClient.removeRequestHeader("Cookies");

        httpClient.removeRequestHeader("Host");
        httpClient.addRequestHeader("Host", "utas.external.s2.fut.ea.com");
        HttpMessage getRespUtasS2 = httpClient.GetAsync(Resources.UTAS_S2 + baseResources.AccountInfo).get();


        if (getBaseAppVersion() == AppVersion.WebApp) {
            // Re-add the required headers
            prepareUtasHeaders();

            //Do options request first

            //utas
            HttpMessage optionsRespUtas = httpClient.OptionAsync(Resources.UTAS + baseResources.AccountInfo).get();

            int optionCodeUtas = optionsRespUtas.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCodeUtas)) {
                handleExceptionAndStatusCode("Error sending option request for:" + baseResources.AccountInfo + " normal utas", optionCodeUtas);
            }

            // s3
            httpClient.removeRequestHeader("Host");
            httpClient.addRequestHeader("Host", "utas.external.s3.fut.ea.com");

            HttpMessage optionsRespUtasS3 = httpClient.OptionAsync(Resources.UTAS_S3 + baseResources.AccountInfo).get();

            int optionCodeUtasS3 = optionsRespUtasS3.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCodeUtasS3)) {
                handleExceptionAndStatusCode("Error sending option request for:" + baseResources.AccountInfo + " s3 utas", optionCodeUtasS3);
            }

            // Need to set the correct headers first.
            removeAccessControlHeaders();
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            httpClient.removeRequestHeader(HttpHeaders.Accept);
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
        }

        httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, nucleusId);
        addReferrerHeader(baseResources.Home);
        httpClient.removeRequestHeader("Cookies");

        // Now do the get requests
        // utas

        httpClient.removeRequestHeader("Host");
        httpClient.addRequestHeader("Host", "utas.external.fut.ea.com");
        HttpMessage getRespUtas = httpClient.GetAsync(Resources.UTAS + baseResources.AccountInfo).get();

        // s3
        httpClient.removeRequestHeader("Host");
        httpClient.addRequestHeader("Host", "utas.external.s3.fut.ea.com");
        HttpMessage getRespUtasS3 = httpClient.GetAsync(Resources.UTAS_S3 + baseResources.AccountInfo).get();


        HttpMessage message;

        // if xbox then s3 otherwise s2.
        if (platform == Platform.XboxOne || platform == Platform.Xbox360) {
            message = getRespUtasS3;

            int getCodeUtasS3 = getRespUtasS3.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(getCodeUtasS3)) {
                handleExceptionAndStatusCode("Error sending get request for:" + baseResources.AccountInfo + " s3 utas", getCodeUtasS3);
            }
        } else if (platform == Platform.Pc) {
            message = getRespUtasS2;

            int getCodeUtasS2 = getRespUtasS2.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(getCodeUtasS2)) {
                handleExceptionAndStatusCode("Error sending get request for:" + baseResources.AccountInfo + " s2 utas", getCodeUtasS2);
            }
        } else {
            message = getRespUtas;

            int getCodeUtas = getRespUtas.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(getCodeUtas)) {
                handleExceptionAndStatusCode("Error sending get request for:" + baseResources.AccountInfo + " utas", getCodeUtas);
            }
        }

        return new AsyncResult<>((UserAccounts) deserializeAsync(message, UserAccounts.class).get());
    }

    /**
     * Get shards
     *
     * @param nucleusId .
     * @return Shards
     * @throws Exception .
     */
    private Future<Shards> getShards(String nucleusId) throws Exception {
        String uri = baseResources.Shards;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            //Need to send the options request first.

            addGenericHeaders();
            httpClient.addRequestHeader("Host", "utas.mob.v1.fut.ea.com");
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type");
            httpClient.removeRequestHeader("Cookies");
            addReferrerHeader(baseResources.Home);

            if (getBasePhishingToken() != null) {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-phishing-token,x-ut-sid");
            } else {
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type");
            }
            //Do options request first
            HttpMessage optionsResp = httpClient.OptionAsync(uri).get();

            int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
                handleExceptionAndStatusCode("Error sending option request for:" + uri, optionCode);
            }

            removeAccessControlHeaders();
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            httpClient.removeRequestHeader(HttpHeaders.Accept);
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
            httpClient.removeRequestHeader("Cookies");
        } else {
            // Mobile
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader("Host", "utas.mob.v1.fut.ea.com");

            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("*/*");
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addMobileUserAgent();
            addRequestWithHeader();
        }

        // Possibly not needed as of FUT 19
/*
        if (getBasePhishingToken() != null) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.PhishingToken, getBasePhishingToken());
        }


        httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, nucleusId);
        httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, "");
        */


        HttpMessage shardResp = httpClient.GetAsync(uri).get();

        return new AsyncResult<>((Shards) deserializeAsync(shardResp, Shards.class).get());
    }

    /**
     * Send WebApp_Load Pin event.
     */
    private void sendPinEventWebAppLoad() throws Exception {
        // Explicit pin event
        pinService.setProperties(PinService.EMPTY_PID, "", "",
                PinEventId.WebApp_Load, "", loginDetails.getPlatform());
        pinService.sendPinEvent(PinEventId.WebApp_Load).get();
        // End pin event.
    }

    /**
     * Send login pin event
     *
     * @param nucleusId Nucleus Id.
     * @param sessionId Session Id.
     * @throws Exception .
     */
    private void sendLoginPinEvent(String nucleusId, String sessionId) throws Exception {
        // Explicit pin event
        pinService.setProperties(Long.parseLong(nucleusId), personaId, sessionId,
                PinEventId.WebApp_Login, baseDob, loginDetails.getPlatform());
        pinService.sendPinEvent(PinEventId.WebApp_Login).get();
        // End pin event.
    }

    /**
     * Get nucleus id.
     *
     * @param authBearerToken auth access token/bearer token.
     * @return Nucleus id
     * @throws Exception .
     */
    private Future<String[]> getNucleusId(String authBearerToken) throws Exception {
        if (getBaseAppVersion() == AppVersion.WebApp) {
            addGenericHeaders();
            httpClient.addRequestHeader("Host", "gateway.ea.com");
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization");
            httpClient.removeRequestHeader("Cookies");
            //Do options request first
            HttpMessage optionsResp = httpClient.OptionAsync(Resources.Pid).get();

            int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
                handleExceptionAndStatusCode("Error sending option request for:" + Resources.Pid, optionCode);
            }

            removeAccessControlHeaders();
            addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
            httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            httpClient.removeRequestHeader(HttpHeaders.Accept);
            httpClient.addRequestHeader(HttpHeaders.ContentType, "text/plain;charset=UTF-8");
            addAcceptHeader("application/json");
            httpClient.removeRequestHeader("Cookies");
        } else {
            // Mobile request.
            httpClient.clearRequestHeaders();
            httpClient.addRequestHeader("Host", "gateway.ea.com");

            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
            addAcceptHeader("application/json; charset=utf-8; */*; q=0.01");
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addMobileUserAgent();
            addRequestWithHeader();
        }

        //Add bearer stuff now
        httpClient.addRequestHeader("Authorization", "Bearer " + authBearerToken);

        HttpMessage httpResponse = httpClient.GetAsync(Resources.Pid).get();

        int statusCode = httpResponse.getResponse().getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.FORBIDDEN.value()) {
            return null;
        }

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Error getting nucleus id: " + statusCode, statusCode);
        }

        String message = httpResponse.getContent();

        JsonObject object = new JsonParser().parse(message).getAsJsonObject();

        String nucleusId = object.get("pid").getAsJsonObject().get("pidId").getAsString();
        String dob = object.get("pid").getAsJsonObject().get("dob").getAsString();

        //need to parse dob so it's yyyy-mm
        String[] splitDob = dob.split("-");
        dob = splitDob[0] + "-" + splitDob[1];

        String[] nuc_dob = new String[]{nucleusId, dob};

        return new AsyncResult<>(nuc_dob);
    }

    /**
     * Handle login redirect.
     *
     * @param loginResp .
     * @return Redirected response
     * @throws Exception .
     */
    private Future<HttpMessage> loginRedirect(HttpMessage loginResp) throws Exception {
        String url;
        String jsPageContent = loginResp.getContent();
        int iHttpPos = jsPageContent.indexOf("https");
        int iEndOfRowPos = jsPageContent.indexOf("';", iHttpPos);

        url = jsPageContent.substring(iHttpPos, iEndOfRowPos);
        url += "&_eventId=end";

        if (url.contains(":443")) {
            url = url.replaceAll(":443", "");
        }

        addReferrerHeader(loginResp.getRequest().getURI().toString());
        HttpMessage loginRedirect = httpClient.GetAsync(url).get();

        int statusCode = loginRedirect.getResponse().getStatusLine().getStatusCode();

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Not successful login redirect: " + statusCode, statusCode);
        }

        return new AsyncResult<>(loginRedirect);
    }

    /**
     * First request to login.
     *
     * @param loginDetails .
     * @param signPageResp .
     * @return Login response and request message.
     * @throws Exception .
     */
    private Future<HttpMessage> login(LoginDetails loginDetails, HttpMessage signPageResp) throws Exception {
        // Below uri should be something along the lines of 'signin.ea.com/p/..'
        URI uri = new URI(signPageResp.getRequest().getURI().toString());
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.CACHE_CONTROL, "max-age=0");
        addContentHeader("application/x-www-form-urlencoded");
        addReferrerHeader(signPageResp.getRequest().getURI().toString());
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        // email=testEmail%40live.co.uk&
        // password=PASSWORD1&
        // country=GB
        // &phoneNumber=
        // &passwordForPhone=&
        // _rememberMe=on&
        // rememberMe=on&
        // _eventId=submit&
        // gCaptchaResponse=&
        // isPhoneNumberLogin=false&
        // isIncompletePhone=

        nameValuePairs.add(new BasicNameValuePair("email", loginDetails.getUsername()));
        nameValuePairs.add(new BasicNameValuePair("password", loginDetails.getPassword()));
        nameValuePairs.add(new BasicNameValuePair("country", "GB"));
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", ""));
        nameValuePairs.add(new BasicNameValuePair("passwordForPhone", ""));
        nameValuePairs.add(new BasicNameValuePair("_rememberMe", "on"));
        nameValuePairs.add(new BasicNameValuePair("rememberMe", "on"));
        nameValuePairs.add(new BasicNameValuePair("_eventId", "submit"));
        nameValuePairs.add(new BasicNameValuePair("gCaptchaResponse", "on"));
        nameValuePairs.add(new BasicNameValuePair("isPhoneNumberLogin", "false"));
        nameValuePairs.add(new BasicNameValuePair("isIncompletePhone", ""));

        Future<HttpMessage> future = httpClient.PostAsync(uri, new UrlEncodedFormEntity(nameValuePairs));
        HttpMessage message = future.get();

        int statusCode = message.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Not successful login: " + statusCode, statusCode);
        }

        return new AsyncResult<>(message);
    }

    /**
     * Try and get the secondary authorization bearer token.
     * <p>
     * https://accounts.ea.com/connect/auth?response_type=token&redirect_uri=nucleus%3Arest&prompt=none&client_id=ORIGIN_JS_SDK
     * </p
     *
     * @return The httpMessage wrapper with request/response
     * @throws Exception .
     */
    private Future<HttpMessage> tryGetSecondaryAuthBearerToken() throws Exception {
        addGenericHeaders();
        httpClient.addRequestHeader("Host", "accounts.ea.com");

        removeAccessControlHeaders();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");

        Future<HttpMessage> authBearerTokenResp = httpClient.GetAsync(Resources.SecondaryAuthBearerToken);
        return new AsyncResult<>(authBearerTokenResp.get());
    }

    /**
     * Get hte auth token for mobile
     *
     * @param authCode     The auth code to put in the url
     * @param refreshToken The refresh token. This is needed when the user has logged into the device before.
     * @return The mobile auth token message
     * @throws Exception .
     */
    private Future<HttpMessage> getMobileAuthToken(String authCode, String refreshToken) throws Exception {
        httpClient.clearRequestHeaders();
        addAcceptHeader("*/*");
        addMobileAcceptEncodingHeader();
        addMobileAcceptLanguageHeader();
        addMobileUserAgent();
        addRequestWithHeader();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/x-www-form-urlencoded");
        httpClient.addRequestHeader("Origin", "file://");

        String url;

        if (refreshToken == null) {
            url = String.format(Resources.MobileAuthBearerToken, authCode);
        } else {
            url = String.format(Resources.MobileAuthBearerTokenRefresh, refreshToken);
        }

        Future<HttpMessage> authBearerTokenResp = httpClient.PostAsync(url, new StringEntity(" "));
        return new AsyncResult<>(authBearerTokenResp.get());
    }

    /**
     * Checks if the user is logged in. Need to send options and get request.
     *
     * @return Is user logged in.
     * @throws Exception .
     */
    private Future<HttpMessage> isLoggedIn() throws Exception {
        addGenericHeaders();
        httpClient.addRequestHeader("Host", "accounts.ea.com");

        Future<HttpMessage> options = httpClient.OptionAsync(Resources.CheckLoggedIn);
        HttpMessage optionsResp = options.get();

        int statusCode = optionsResp.getResponse().getStatusLine().getStatusCode();

        if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
            handleExceptionAndStatusCode("Main page error code: " + statusCode, statusCode);
        }

        removeAccessControlHeaders();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");

        Future<HttpMessage> future = httpClient.GetAsync(Resources.CheckLoggedIn);
        HttpMessage response = future.get();

        return new AsyncResult<>(response);
    }

    /**
     * Get the auth code for when the user has previously logged in via mobile
     *
     * @return
     * @throws Exception
     */
    private Future<HttpMessage> mobileAuthCodePreviouslyLoggedIn() throws Exception {
        httpClient.clearRequestHeaders();

        addAcceptHeader("*/*");
        addMobileAcceptLanguageHeader();
        addMobileAcceptEncodingHeader();
        addMobileUserAgent();
        addRequestWithHeader();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        httpClient.addRequestHeader("Host", "accounts.ea.com");

        Future<HttpMessage> future = httpClient.GetAsync(Resources.TryGetCodeMobile);
        HttpMessage response = future.get();

        return new AsyncResult<>(response);
    }

    /**
     * Get the main page.
     *
     * @return Main page response and request.
     */
    private Future<HttpMessage> getMainPage() {
        httpClient.clearRequestHeaders();
        addSecurityUpgradedHeader();
        addUserAgent();
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
        addAcceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

        Future<HttpMessage> entityFuture = httpClient.GetAsync(baseResources.Home);

        //Remove the upgraded insecure header.
        httpClient.removeRequestHeader(NonStandardHttpHeaders.Upgrade_Insecure_Requests);

        HttpMessage message = null;

        try {
            message = entityFuture.get();

            int statusCode = message.getResponse().getStatusLine().getStatusCode();

            if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
                handleExceptionAndStatusCode("Main page error code: " + statusCode, statusCode);
            }
        } catch (Exception e) {
            log.error("Error getting main login page: " + e.getMessage());
        }

        return new AsyncResult<>(message);
    }

    /**
     * Enter 2FA code.
     *
     * @param message .
     * @return 2FA response and request.
     * @throws Exception .
     */
    private Future<HttpMessage> setTwoFactorCodeAsync(HttpMessage message) throws Exception {
        try {
            String tfCode = iTwoFactorCodeProvider.getTwoFactorCodeAsync();
            log.info("Code is " + tfCode);

            addReferrerHeader(message.getRequest().getURI().toString());

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("oneTimeCode", tfCode));
            nameValuePairs.add(new BasicNameValuePair("_eventId", "submit"));
            nameValuePairs.add(new BasicNameValuePair("_trustThisDevice", "on"));
            nameValuePairs.add(new BasicNameValuePair("trustThisDevice", "on"));

            Future<HttpMessage> futureCodeResponse = httpClient.PostAsync(message.getRequest().getURI(),
                    new UrlEncodedFormEntity(nameValuePairs));

            HttpMessage codeResp = futureCodeResponse.get();

            int statusCode = codeResp.getResponse().getStatusLine().getStatusCode();

            if (!HttpUtils.ensureSuccessStatusCode(statusCode)) {
                handleExceptionAndStatusCode("Main page error code: " + statusCode, statusCode);
                return null; // should never get here because the above method throws an exception.
            } else {
                String codeContent = codeResp.getContent();

                if (codeContent.contains("Incorrect code entered")) {
                    log.error("Incorrect 2FA Code, trying again after 23450ms pause " + LocalTime.now());
                    Thread.sleep(SleepUtil.random(20000, 30000));
                    log.info("Trying now " + LocalTime.now());
                    return new AsyncResult<>(setTwoFactorCodeAsync(message)).get();
                }

                return new AsyncResult<>(codeResp);
            }
        } catch (Exception e) {
            log.error("Error getting 2FA code: " + e.getMessage());
            throw new Exception("Error getting 2FA code: " + e.getMessage());
        }
    }

    /**
     * Add generic headers.
     */
    private void addGenericHeaders() {
        httpClient.clearRequestHeaders();
        addUserAgent();
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
        addAcceptHeader("*/*");
        addAccessControlHeaders(HttpMethod.GET);
        httpClient.addRequestHeader(NonStandardHttpHeaders.Origin, "https://www.easports.com");
    }

    /**
     * Prepare headers for UTAS user accounts request
     */
    private void prepareUtasHeaders() {
        addGenericHeaders();
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        httpClient.addRequestHeader("Host", "utas.external.fut.ea.com");
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id");
    }
}


