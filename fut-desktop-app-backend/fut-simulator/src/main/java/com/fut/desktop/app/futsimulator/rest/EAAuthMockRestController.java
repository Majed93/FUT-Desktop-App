package com.fut.desktop.app.futsimulator.rest;

import com.fut.desktop.app.constants.EAHttpStatusCodes;
import com.fut.desktop.app.constants.NonStandardHttpHeaders;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.RandomStringUtilsExtension;
import com.fut.desktop.app.futsimulator.utils.ResponseReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

/**
 * Handle authentication after logging in. see {@link LoginMockController} for logging in.
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class EAAuthMockRestController {

    /**
     * Flag for if the question is answer correctly
     */
    private boolean isAnswerCorrect = true;

    /**
     * Get method for gateway pid
     */
    @GetMapping("/gateway.ea.com/proxy/identity/pids/me")
    public String gatewayPid(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.chechAuthBearerToken(requestHeaders);

        String pidJson = ResponseReaderUtil.generateStringOfFile("responses/json/gatewayPid.json");

        String randomPid = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(13, false, true));
        pidJson = pidJson.replaceAll("9999999999999", randomPid);
        pidJson = pidJson.replaceAll("REPLACE_EMAIL", "test@test.com");
        return RandomStringUtilsExtension.ensureDoesNotStartWithZero(pidJson);
    }

    /**
     * https://gateway.ea.com/proxy/subscription/pids/2359081870/subscriptionsv2/groups/Origin%20Membership?state=ENABLED,PENDINGEXPIRED
     */
    @GetMapping("/gateway.ea.com/proxy/subscription/pids/{nucleusId}/subscriptionsv2/groups/{OriginMembership}{state}")
    public String gatewaySubscription(@RequestHeader HttpHeaders requestHeaders, @PathVariable("nucleusId") String nucleusId,
                                      @RequestParam("state") Object state, @PathVariable("OriginMembership") String OriginMembership) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.chechAuthBearerToken(requestHeaders);
        Assert.assertNotNull("Nucleus Id not present", nucleusId);
        Assert.assertNotNull("state param not present", state);
        return "{ }";
    }

    /**
     * Get request for utas shards
     */
    @GetMapping(value = "/utas.mob.v1.fut.ea.com/ut/shards/v2")
    public String shardsGet(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        return ResponseReaderUtil.generateStringOfFile("responses/json/shards.json");
    }

    /**
     * Options request for user accounts. Wildcard used for s2 and s3.
     */
    @GetMapping(value = "/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/user/accountinfo" +
            "{filterConsoleLogin}{sku}{returningUserGameYear}")

    public String userAccountsGet(@RequestHeader HttpHeaders requestHeaders,
                                  HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam("filterConsoleLogin") String filterConsoleLogin,
                                  @RequestParam("sku") String sku,
                                  @RequestParam("returningUserGameYear") String returningUserGameYear) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkEASessionDataNucleusId(requestHeaders);
        Assert.assertNotNull("Filter console login is null!", filterConsoleLogin);
        Assert.assertNotNull("sku is null!", sku);
        Assert.assertNotNull("returningUserGameYear is null!", returningUserGameYear);

        // if it's not s3 that means it's not an Xbox account
        if (request.getRequestURI().contains("s2")) {
            response.setStatus(EAHttpStatusCodes.NO_PERMISSION);
            return "";
        }

        String userAccountsJson = ResponseReaderUtil.generateStringOfFile("responses/json/userAccounts.json");
        String personaId = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(10, false, true));
        return userAccountsJson.replaceAll("9999999999", personaId);
    }

    /**
     * Post and return the session token
     */
    @PostMapping("/utas.external.*fut.ea.com/ut/auth{sku_c}")
    public String sessionId(@RequestHeader HttpHeaders requestHeaders,
                            @RequestParam("sku_c") String sku_c,
                            @RequestBody Object reqBody) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);

        String sku = ((LinkedHashMap) reqBody).get("sku").toString();
        String isReadOnly = ((LinkedHashMap) reqBody).get("isReadOnly").toString();
        String clientVersion = ((LinkedHashMap) reqBody).get("clientVersion").toString();
        String locale = ((LinkedHashMap) reqBody).get("locale").toString();
        String authcode = ((LinkedHashMap) reqBody).get("method").toString();
        String priorityLevel = ((LinkedHashMap) reqBody).get("priorityLevel").toString();
        String nucleusPersonaId = ((LinkedHashMap) reqBody).get("nucleusPersonaId").toString();
        String gameSku = ((LinkedHashMap) reqBody).get("gameSku").toString();

        Object identification = ((LinkedHashMap) reqBody).get("identification");
        String authCode = ((LinkedHashMap) identification).get("authCode").toString();
        String redirectUrl = ((LinkedHashMap) identification).get("redirectUrl").toString();

        Assert.assertNotNull(sku_c);
        Assert.assertNotNull(sku);
        Assert.assertNotNull(isReadOnly);
        Assert.assertNotNull(clientVersion);
        Assert.assertNotNull(locale);
        Assert.assertNotNull(authcode);
        Assert.assertNotNull(priorityLevel);
        Assert.assertNotNull(nucleusPersonaId);
        Assert.assertNotNull(gameSku);
        Assert.assertNotNull(identification);
        Assert.assertNotNull(authCode);
        Assert.assertNotNull(redirectUrl);

        Assert.assertTrue("Wrong sku year.." + sku, sku.contains("FUT19WEB") || sku.contains("FUT19AND"));

        String sessionResp = ResponseReaderUtil.generateStringOfFile("responses/json/sessionToken.json");

        // Generate phishing token
        // REPLACE_ME_PHISHING_TOKEN
        String phishingToken = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(19, false, true));
        sessionResp = sessionResp.replaceAll("REPLACE_ME_PHISHING_TOKEN", phishingToken);

        // 3f427a3c-c050-41e4-8928-7a803e557f49
        return sessionResp.replaceAll("REPLACE_ME", generateSessionToken());
    }

    /**
     * Get how many attempts of the question we have. Usually this is the first time we're entering a question
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/phishing/question{_}")
    @Deprecated
    public String answerQuestion(@RequestHeader HttpHeaders requestHeaders,
                                 @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkEASessionDataNucleusId(requestHeaders);
        HttpHeaderCheckerUtil.checkXUTSID(requestHeaders);

        if (requestHeaders.get(NonStandardHttpHeaders.PhishingToken) != null) {
            String resp = ResponseReaderUtil.generateStringOfFile("responses/json/questionAnswered.json");
            String token = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(19, false, true));
            return resp.replaceAll("999999", token);
        }

        return ResponseReaderUtil.generateStringOfFile("responses/json/questionTime.json");
    }

    /**
     * Post the answer of the security question.
     * Training will take place here to fail if needed.
     */
    @PostMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/phishing/validate{answer}")
    @Deprecated
    public String isAnswerCorrect(@RequestHeader HttpHeaders requestHeaders,
                                  @RequestParam("answer") String answer,
                                  @RequestBody String token) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkEASessionDataNucleusId(requestHeaders);
        HttpHeaderCheckerUtil.checkXUTSID(requestHeaders);
        Assert.assertNotNull(token);

        if (isAnswerCorrect) {
            String newToken = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(19, false, true));
            return "{\"debug\":\"Answer is correct.\",\"string\":\"OK\",\"code\":\"200\",\"reason\":\"Answer is correct.\",\"token\":\"" + newToken + "\"}";
        } else {
            return "{\"debug\":\"Answers do not match for nucleus id 2359081870\",\"string\":\"Permission Denied\",\"code\":\"461\",\"reason\":\"Answers do not match for nucleus id 2359081870\",\"token\":null}";
        }
    }

    /**
     * Train the answer to be true or false.
     */
    @GetMapping("/setAnswer/{answer}")
    @Deprecated
    public void setAnswer(@PathVariable("answer") Boolean answer) {
        this.isAnswerCorrect = answer;
    }

    /**
     * Generate a token in a format.
     *
     * @return generated token.
     */
    private String generateSessionToken() {
        Integer[] partLengths = {8, 4, 4, 4, 12};
        char seperator = '-';
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < partLengths.length; i++) {
            String randomToken = RandomStringUtils.random(partLengths[i], true, true);
            token.append(i == 0 ? randomToken : seperator + randomToken);
        }

        return token.toString().toLowerCase();
    }
}
