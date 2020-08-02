package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpUtils;
import com.fut.desktop.app.constants.*;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.fut.desktop.app.domain.Platform.Xbox360;
import static com.fut.desktop.app.domain.Platform.XboxOne;

@Getter
@Setter
@Slf4j
public abstract class FutRequestBase {

    private String basePhishingToken;

    private String baseSessionId;

    private String baseMobileSessionId;

    private String baseNucleusId;

    private String basePersonaId;

    public AppVersion baseAppVersion;

    public Resources baseResources;

    public String baseDob;

    public Platform basePlatform;

    public IHttpClient httpClient;

    public long timerFactor;

    private String baseAuthBearerToken;

    private String baseMobileAuthBearerToken;

    private String baseRefreshToken;

    public int pageSize;

    public void addSecurityUpgradedHeader() {
        httpClient.addRequestHeader(NonStandardHttpHeaders.Upgrade_Insecure_Requests, "1");
    }

    /**
     * Add common headers
     *
     * @param platform Platform
     */
    void addCommonHeaders(Platform platform) {
        httpClient.clearRequestHeaders();
        addUserAgent();
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
        addReferrerHeader("https://www.easports.com/fifa/ultimate-team/web-app/");
        httpClient.addRequestHeader(NonStandardHttpHeaders.Origin, "https://www.easports.com");
        httpClient.addRequestHeader("Host", "utas.external." + (platform == XboxOne || platform == Xbox360 ? "s3" : "s2") + ".fut.ea.com");
    }

    /**
     * Add common mobile headers.
     *
     * @param platform Platform
     */
    protected void addCommonMobileHeaders(Platform platform) {
        httpClient.clearRequestHeaders();
        addMobileUserAgent();
        addMobileAcceptLanguageHeader();
        addMobileAcceptEncodingHeader();
        httpClient.addRequestHeader("Host", "utas.external." + (platform == XboxOne || platform == Xbox360 ? "s3" : "s2") + ".fut.ea.com");
        addRequestWithHeader();
    }
//
//    protected void addMobileLoginHeaders() {
//        httpClient.clearRequestHeaders();
//        httpClient.addRequestHeader(NonStandardHttpHeaders.CSP, "active");
//        addAcceptHeader("*/*");
//        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
//        addAcceptEncodingHeader();
//        addAcceptMobileLanguageHeader();
//        addMobileUserAgent();
//    }

    protected void addPinHeaders() {
        httpClient.clearRequestHeaders();
        httpClient.addRequestHeader("Origin", "https://www.easports.com");
        httpClient.addRequestHeader("Host", "pin-river.data.ea.com");
        httpClient.addRequestHeader("x-ea-taxv", "1.1");
        httpClient.addRequestHeader("x-ea-game-id-type", "easku");
        addUserAgent();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        httpClient.addRequestHeader("x-ea-game-id", Resources.FUT_WEB);
        addAcceptHeader("text/plain, */*; q=0.01");
        addReferrerHeader(
                "https://www.easports.com/uk/fifa/ultimate-team/web-app/");
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
    }

    protected void addPinHeadersMobile() {
        httpClient.clearRequestHeaders();
        addAcceptHeader("*/*");
        httpClient.addRequestHeader(HttpHeaders.AcceptEncoding, "gzip, deflate");
        httpClient.addRequestHeader(HttpHeaders.AcceptLanguage, "en-GB,en-US;q=0.9,en;q=0.8");
        addMobileUserAgent();
        addRequestWithHeader();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        httpClient.addRequestHeader("x-ea-game-id", "874218"); // TODO: CHECK WHAT THIS IS, still the same..
        httpClient.addRequestHeader("x-ea-game-id-type", "easku");
        httpClient.addRequestHeader("x-ea-taxv", "1.1");

        httpClient.addRequestHeader("Origin", "file://");
    }

    void addRequestWithHeader() {
        httpClient.addRequestHeader(NonStandardHttpHeaders.RequestedWith, "com.ea.gp.fifaultimate");
    }

    protected void addAnonymousHeader() {
        addUserAgent();
        addAcceptHeader("*/*");
        addReferrerHeader(Resources.BaseShowoff);
        //httpClient.addRequestHeader("X-Requested-With", "ShockwaveFlash/21.0.0.182");
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
    }

    protected void addAnonymousMobileHeader() {
        addMobileUserAgent();
        addAcceptHeader("*/*");
        httpClient.addRequestHeader("CSP", "active");
        addAcceptEncodingHeader();
        addAcceptLanguageHeader();
    }

    protected void addContentHeader(String contentType) {
        httpClient.addRequestHeader(HttpHeaders.ContentType, contentType);
    }

    protected void addEncodingHeader(String encodingType) {
        httpClient.addRequestHeader(HttpHeaders.AcceptEncoding, encodingType);
    }

    void addUserAgent() {
        httpClient.addRequestHeader(HttpHeaders.UserAgent,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    }

    void addMobileUserAgent() {
        httpClient.addRequestHeader(HttpHeaders.UserAgent,
                "Mozilla/5.0 (Linux; U; Android 2.3.4; en-us; Kindle Fire Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Safari/533.1");
    }

    /**
     * Remove any existing accept header then add it again.
     *
     * @param value Value of the accept header.
     */
    void addAcceptHeader(String value) {
        httpClient.removeRequestHeader(HttpHeaders.Accept);
        httpClient.addRequestHeader(HttpHeaders.Accept, value);
    }

    void addReferrerHeader(String value) {
        httpClient.setReferrerUri(value);
    }

    void addAcceptEncodingHeader() {
        httpClient.addRequestHeader(HttpHeaders.AcceptEncoding, "gzip, deflate, br");
    }

    void addAcceptLanguageHeader() {
        httpClient.addRequestHeader(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9");
    }

    void addMobileAcceptEncodingHeader() {
        httpClient.addRequestHeader(HttpHeaders.AcceptEncoding, "gzip, deflate");
    }

    void addMobileAcceptLanguageHeader() {
        httpClient.addRequestHeader(HttpHeaders.AcceptLanguage, "en-GB,en-US;q=0.9,en;q=0.8");
    }

    void addMethodOverrideHeader(HttpMethod httpMethod) {
        httpClient.addRequestHeader(NonStandardHttpHeaders.MethodOverride, httpMethod.toString());
    }

    void addAccessControlHeaders(HttpMethod method) {
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "content-type");
        httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, method.toString());
    }

    void removeAccessControlHeaders() {
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
    }

    protected void addAuthorizationHeader(String authCode) {
        httpClient.addRequestHeader("Authorization", "Bearer " + authCode);
    }

    /**
     * Senda generic utas options request.
     * It will clear all headers at start and remove Access control & Accept header.
     *
     * @param platform          Console
     * @param uri               URL to request
     * @param method            Method to send.
     * @param requiresNucleusId If true the request requires nucleus Id.
     * @throws Exception Handle exception.
     */
    void sendGenericUtasOptions(Platform platform, String uri, HttpMethod method, Boolean requiresNucleusId) throws Exception {
        addCommonHeaders(platform);
        addAcceptHeader("*/*");

        addAccessControlHeaders(method);
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);

        if (requiresNucleusId) {
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,easw-session-data-nucleus-id,x-ut-sid");
        } else {
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,x-ut-sid");
        }

        sendOptionsRequest(platform, uri);
    }

    void sendOptionsRequest(Platform platform, String uri) throws Exception {
        //TODO:
        //platform check
        switch (platform) {
            case Xbox360:
            case XboxOne:
                break;
            default:
                uri = uri.replaceAll("s3", "s2");
                break;
        }
        try {
            //Send OPTIONS request first
            HttpMessage optionsResp = httpClient.OptionAsync(uri).get();

            int optionCode = optionsResp.getResponse().getStatusLine().getStatusCode();
            if (!HttpUtils.ensureSuccessStatusCode(optionCode)) {
                handleExceptionAndStatusCode("Error sending option request for:" + uri, optionCode);
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new FutException("Error sending OPTIONS request for: " + uri, ex);
        }

        removeAccessControlHeaders();
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.CONNECTION);
        httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        httpClient.removeRequestHeader(HttpHeaders.Accept);
    }

    /**
     * Helper method to send usual GET request.
     * NOTE: Header may very. these should be added before.
     *
     * @param uri                   Uri to request.
     * @param requiresNucleusId     If true the request requires nucleus Id.
     * @param shortenedAcceptHeader If true then the accept header added '* / *'
     * @return Response
     * @throws Exception handle any exception
     */
    HttpMessage sendGetRequest(String uri, Boolean requiresNucleusId, Boolean shortenedAcceptHeader) throws Exception {
        prepareHeaders(requiresNucleusId, shortenedAcceptHeader);
        //Send the GET request now.
        return sendGetRequest(uri);
    }

    HttpMessage sendPostRequest(String uri, String content, Boolean requiresNucleusId, Boolean shortenedAcceptHeader) throws Exception {
        // Now send actual request
        prepareHeaders(requiresNucleusId, shortenedAcceptHeader);
        //Send the POST request now.
        HttpMessage postResp = httpClient.PostAsync(uri, new StringEntity(content)).get();

        int postCode = postResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(postCode)) {
            handleExceptionAndStatusCode("Error sending post request for:" + uri, postCode);
        }

        return postResp;
    }

    /**
     * Helper method to send usual PUT request.
     *
     * @param uri                   Uri to request.
     * @param content               Content to send.
     * @param requiresNucleusId     If true the request requires nucleus Id.
     * @param shortenedAcceptHeader If true then the accept header added '* / *'
     * @return Response
     * @throws Exception Handle any errors.
     */
    HttpMessage sendPutRequest(String uri, String content, Boolean requiresNucleusId, Boolean shortenedAcceptHeader) throws Exception {
        prepareHeaders(requiresNucleusId, shortenedAcceptHeader);

        //Send the PUT request now.
        HttpMessage putResp = httpClient.PutAsync(uri, new StringEntity(content)).get();

        int putCode = putResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(putCode)) {
            log.error("Error bidding. Code is: " + putCode);
            handleExceptionAndStatusCode("Error sending put request for:" + uri, putCode);
        }

        return putResp;
    }

    /**
     * Helper method to send usual DELETE request.
     *
     * @param uri               Uri to request
     * @param requiresNucleusId If true then request requires nucleus Id.
     * @throws Exception .
     */
    void sendDeleteRequest(String uri, Boolean requiresNucleusId) throws Exception {
        removeAccessControlHeaders();
        addAcceptHeader("*/*");
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");

        httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, getBaseSessionId());

        if (requiresNucleusId) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, getBaseNucleusId());
        }
        //Send the DELETE request now.
        HttpMessage deleteResp = httpClient.DeleteAsync(uri).get();

        int deleteCode = deleteResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(deleteCode)) {
            handleExceptionAndStatusCode("Error sending delete for " + uri, deleteCode);
        }
    }

    static Future<?> deserializeAsync(HttpMessage message, Class<?> type) throws HttpException, FutException {
        if (!HttpUtils.ensureSuccessStatusCode(message.getResponse().getStatusLine().getStatusCode())) {
            throw new HttpException("Error code! " + message.getResponse().getStatusLine().getStatusCode());
        }

        Object body = message.getContent();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        Object value = null;

        try {
            value = mapper.readValue(body.toString(), type);
        } catch (IOException e) {
            try {
                FutError futError = mapper.readValue(body.toString(), FutError.class);
                MapAndThrowException(e, futError);
            } catch (IOException ex) {
                log.error("URL is: {} ", message.getRequest().getURI());
                log.error("Original message is: {}", body);
                log.error("Error deserializing error code: " + ex.getMessage());
            }
        }

        return new AsyncResult<>(value);
    }

    /**
     * Prepare the http headers for a generic request
     *
     * @param requiresNucleusId  If true the request requires nucleus Id.
     * @param longerAcceptHeader If true then the accept header added '* / *'
     */
    private void prepareHeaders(Boolean requiresNucleusId, Boolean longerAcceptHeader) {
        removeAccessControlHeaders();

        if (longerAcceptHeader) {
            addAcceptHeader("text/plain, */*; q=0.01");
        } else {
            addAcceptHeader("*/*");
        }

        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");

        if (getBaseAppVersion() == AppVersion.WebApp) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, getBaseSessionId());
        } else {
            // Mobile
            httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, getBaseMobileSessionId());
        }
        if (requiresNucleusId) {
            httpClient.addRequestHeader(NonStandardHttpHeaders.NucleusId, getBaseNucleusId());
        }
    }

    /**
     * Send the get request with all the headers now having been set.
     *
     * @param uri The uri the request
     * @return The response
     * @throws Exception .
     */
    HttpMessage sendGetRequest(String uri) throws Exception {
        HttpMessage getResp = httpClient.GetAsync(uri).get();

        int getCode = getResp.getResponse().getStatusLine().getStatusCode();
        if (!HttpUtils.ensureSuccessStatusCode(getCode)) {
            handleExceptionAndStatusCode("Error sending get request for:" + uri, getCode);
        }
        return getResp;
    }

    /**
     * @param msg        The message to show in the exception
     * @param statusCode The status code
     * @throws FutException Exception thrown.
     */
    void handleExceptionAndStatusCode(String msg, Integer statusCode) throws FutException {
        log.error("Error msg is {}, status code is {}", msg, statusCode);

        // Check if it's the HTTP code for account needing captcha.
        if (statusCode.equals(FutErrorCode.CaptchaTriggered.getFutErrorCode())) {
            throw new FutErrorException(new FutError(msg, FutErrorCode.CaptchaTriggered, msg, msg, msg));
        }

        throw new FutException(msg + " | HTTP Status code is: " + statusCode);
    }

    private static void MapAndThrowException(Exception exception, FutError futError) throws FutException {
        switch (futError.getCode()) {
            case ExpiredSession:
                throw new ExpiredSessionException(futError, exception);
            case NotFound:
                throw new NotFoundException(futError, exception);
            case Conflict:
                throw new ConflictException(futError, exception);
            case BadRequest:
                throw new BadRequestException(futError, exception);
            case PermissionDenied:
                throw new PermissionDeniedException(futError, exception);
            case NotEnoughCredit:
                throw new NotEnoughCreditException(futError, exception);
            case NoSuchTradeExists:
                throw new NoSuchTradeExistsException(futError, exception);
            case InternalServerError:
                throw new InternalServerException(futError, exception);
            case ServiceUnavailable:
                throw new ServiceUnavailableException(futError, exception);
            case InvalidDeck:
                throw new InvalidDeckException(futError, exception);
            case DestinationFull:
                throw new DestinationFullException(futError, exception);
            case CaptchaTriggered:
                throw new CaptchaTriggeredException(futError, exception);
            case PurchasedItemsFull:
                throw new PurchasedItemsFullException(futError, exception);
            default:
                FutException newException = new FutErrorException(futError, exception);
                throw new FutException(
                        String.format("Unknown EA error, please contact supplier - %s", newException.getMessage()));
        }
    }
}
