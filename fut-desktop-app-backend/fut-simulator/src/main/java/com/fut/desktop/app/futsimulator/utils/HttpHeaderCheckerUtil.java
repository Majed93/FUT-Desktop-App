package com.fut.desktop.app.futsimulator.utils;

import com.fut.desktop.app.constants.NonStandardHttpHeaders;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;

public final class HttpHeaderCheckerUtil {

    /**
     * Asserts the following:
     * - the request is not null
     * - the user agent string is present.
     * - only one keep-alive header
     *
     * @param requestHeaders Headers to check.
     */
    public static void checkHeaders(HttpHeaders requestHeaders) {
        Assert.assertNotNull("Request headers null", requestHeaders);

        Assert.assertTrue("More than one or two Connection header!", requestHeaders.getConnection().size() < 3);
        Assert.assertNotNull("User agent header empty", requestHeaders.get(com.fut.desktop.app.constants.HttpHeaders.UserAgent.toLowerCase()));
    }

    /**
     * Ensure the authorization bearer token is present
     *
     * @param requestHeaders headers to check.
     */
    public static void chechAuthBearerToken(HttpHeaders requestHeaders) {
        Assert.assertNotNull("Authorization bearer token is missing!", requestHeaders.get("Authorization"));
    }

    /**
     * Ensure 'Easw-Session-Data-Nucleus-Id' is present in headers
     *
     * @param requestHeaders headers to check
     */
    public static void checkEASessionDataNucleusId(HttpHeaders requestHeaders) {
        Assert.assertNotNull("Easw-Session-Data-Nucleus-Id is null", requestHeaders.get(NonStandardHttpHeaders.NucleusId));
    }

    /**
     * Ensure the 'X-UT-SID' header is present.
     *
     * @param requestHeaders Headers to check
     */
    public static void checkXUTSID(HttpHeaders requestHeaders) {
        Assert.assertNotNull("X-UT-SID is null", requestHeaders.get(NonStandardHttpHeaders.SessionId));
    }

    /**
     * Ensure the 'X-UT-PHISHING-TOKEN' header is present.
     *
     * @param requestHeaders Headers to check
     */
    public static void checkPhishingToken(HttpHeaders requestHeaders) {
        Assert.assertNotNull("X-UT-PHISHING-TOKEN is null", requestHeaders.get(NonStandardHttpHeaders.PhishingToken));

    }

    /**
     * Check authorization headers
     *
     * @param requestHeaders Headers to check
     */
    public static void checkAuthHeaders(HttpHeaders requestHeaders, boolean isNucleusRequired) {
        if (isNucleusRequired) {
            checkEASessionDataNucleusId(requestHeaders);
        }
        checkXUTSID(requestHeaders);
    }
}
