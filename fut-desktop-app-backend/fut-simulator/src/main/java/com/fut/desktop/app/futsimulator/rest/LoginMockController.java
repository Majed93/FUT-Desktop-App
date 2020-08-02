package com.fut.desktop.app.futsimulator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuthToken;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.RandomStringUtilsExtension;
import com.fut.desktop.app.futsimulator.utils.ResponseReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handle login request.
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class LoginMockController {

    private final String ENDPOINT = RestConstants.MOCK_IO;

    private final Environment environment;

    private String localhost;

    private boolean isSecondaryAuthTokenReady = false;

    private final ObjectMapper objectMapper;

    private boolean isLoginSuccess = true;

    @Autowired
    public LoginMockController(Environment environment, ObjectMapper objectMapper) {
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    /**
     * Train the login to success or not.
     *
     * @param success If true the login will be trained to succeed othewise false.
     */
    @GetMapping("/isLoginSuccess/{success}")
    @ResponseStatus(HttpStatus.OK)
    public void setAnswer(@PathVariable("success") Boolean success) {
        this.isLoginSuccess = success;
    }

    /**
     * Train the secondary authorization bearer token to be ready.
     *
     * @param isReady Boolean, true or false.
     */
    @GetMapping("/setSecondaryAuthTokenReady/{isReady}")
    @ResponseStatus(HttpStatus.OK)
    public void setSecondaryAuthTokenReady(@PathVariable Boolean isReady) {
        isSecondaryAuthTokenReady = isReady;
    }

    /**
     * @return error for requiring login or redirect
     */
    @GetMapping(value = "/accounts.ea.com/connect/auth")
    // {client_id}{response_type}{display}{locale}{redirect_uri}{prompt}{release_type}{scope}",
    // params = {"client_id", "response_type", "display", "locale", "redirect_uri", "prompt", "release_type", "scope"})
    public String checkLoggedInGet(@RequestHeader HttpHeaders requestHeaders,
                                   @RequestParam("client_id") String clientId,
                                   @RequestParam("response_type") String responseType, @RequestParam(value = "display", required = false) String display,
                                   @RequestParam(value = "locale", required = false) String locale, @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                   @RequestParam(value = "prompt", required = false) String prompt, @RequestParam(value = "release_type", required = false) String releaseType,
                                   @RequestParam(value = "scope", required = false) String scope, @RequestParam(value = "access_token", required = false) String accessToken,
                                   HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);

        if (clientId.equals("ORIGIN_JS_SDK")) {
            if (this.isSecondaryAuthTokenReady) {
                String bearerToken = RandomStringUtils.random(83, true, true);

                AuthToken authToken = new AuthToken();
                authToken.setAccess_token(bearerToken);
                authToken.setToken_type("Bearer");
                authToken.setExpires_in("3600");

                // Reset it.
                isSecondaryAuthTokenReady = false;
                return objectMapper.writeValueAsString(authToken);
            } else {
                return "{\"error\":\"login_required\"}";
            }
        } else if (clientId.equals("FIFA-" + Resources.FUT_YEAR + "-MOBILE-COMPANION")) {
            // Mobile
            if(prompt != null && prompt.equals("login")) {
                // Fresh login
                response.setStatus(HttpStatus.FOUND.value());
                this.localhost = "http://localhost:" + environment.getProperty("local.server.port");
                String fid = RandomStringUtils.random(56, true, true);
                response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?fid=" + fid);
                return " ";
            }else {
                // When user has logged in before
                String token = RandomStringUtils.random(42, true, true);
                return "{\"code\":\"" + token + "\"}";
            }
        } else {
            if (prompt == null && accessToken != null) {
                String token = RandomStringUtils.random(42, true, true);
                return "{\"code\":\"" + token + "\"}";
            } else if (prompt != null && prompt.equals("login")) {
                response.setStatus(HttpStatus.FOUND.value());
                this.localhost = "http://localhost:" + environment.getProperty("local.server.port");
                if (request.getQueryString().contains("fid")) {
                    String bearerToken = RandomStringUtils.random(83, true, true);
                    response.sendRedirect(localhost + ENDPOINT + "/www.easports.com/uk/fifa/ultimate-team/web-app/auth.html#access_token=" + bearerToken + "&token_type=Bearer&expires_in=3600");
                    isSecondaryAuthTokenReady = true;
                } else {
                    String fid = RandomStringUtils.random(56, true, true);
                    response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?fid=" + fid);
                }
            }

            return "{\"error\":\"login_required\"}";
        }
    }

    @PostMapping("/accounts.ea.com/connect/token")
    public String mobileAuthBearerToken(@RequestHeader HttpHeaders requestHeaders,
                                        @RequestParam("grant_type") String grantType,
                                        @RequestParam("code") String code,
                                        @RequestParam("client_id") String clientId,
                                        @RequestParam("client_secret") String clientSecret,
                                        @RequestParam("release_type") String releaseType) throws JsonProcessingException {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        Assert.assertNotNull("Auth code in param is null", code);

        String bearerToken = RandomStringUtils.random(83, true, true);
        String refreshToken = RandomStringUtils.random(110, true, true);

        AuthToken authToken = new AuthToken();
        authToken.setAccess_token(bearerToken);
        authToken.setRefresh_token(refreshToken);
        authToken.setToken_type("Bearer");
        authToken.setExpires_in("3600");
        authToken.setId_token(null);

        return objectMapper.writeValueAsString(authToken);
    }

    /**
     * Sign in request with 'fid'
     */
    @GetMapping(value = "/signin.ea.com/p/web2/login{fid}", params = "fid")
    @ResponseStatus(HttpStatus.FOUND)
    public void signinFid(@RequestHeader HttpHeaders requestHeaders, @RequestParam("fid") String fid, HttpServletResponse response) throws IOException {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        String execution = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(10, false, true));
        response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?execution=e" + execution + "s1&initref=https://signin.ea.com/p/web2/login?execution=e1432587834s1&initref=https%3A%2F%2Faccounts.ea.com%3A443%2Fconnect%2Fauth%3Fprompt%3Dlogin%26accessToken%3Dnull%26client_id%3DFIFA-"
                + Resources.FUT_YEAR +
                "-WEBCLIENT%26response_type%3Dtoken%26display%3Dweb2%252Flogin%26locale%3Den_US%26redirect_uri%3Dhttps%253A%252F%252Fwww.easports.com%252Fuk%252Ffifa%252Fultimate-team%252Fweb-app%252Fauth.html%26scope%3Dbasic.identity%2Boffline%2Bsignin");
    }

    /**
     * Main sign in redirection method
     *
     * @param execution Note of this parameter. the 's' at the end sometimes gets incremeneted..
     */
    @GetMapping(value = "/signin.ea.com/p/web2/login{execution}{initref}", params = {"execution", "initref"})
    @ResponseStatus(HttpStatus.OK)
    public String signInPage(@RequestHeader HttpHeaders requestHeaders, @RequestParam("execution") String execution,
                             @RequestParam("initref") List<String> initref, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        String referrer = requestHeaders.get(HttpHeaders.REFERER) != null ? requestHeaders.get(HttpHeaders.REFERER).get(0) : "";

        if (!isLoginSuccess) {
            response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?execution=" + replaceExecution(execution) + "&initref=" + initref.get(initref.size() - 1));
        }

        if (request.getQueryString().contains("_eventId=end")) {
            this.localhost = "http://localhost:" + environment.getProperty("local.server.port");
            response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?execution=" + replaceExecution(execution) + "&initref=" + initref);
        }

        if (referrer.contains("www.easports.com/fifa/ultimate-team/web-app")) {
            return ResponseReaderUtil.generateStringOfFile("responses/html/loginPage.html");
        } else if ((referrer.contains("s1&"))) {
            return ResponseReaderUtil.generateStringOfFile("responses/html/script.html");
        } else if (referrer.contains("s2&") && referrer.contains("signin.ea.com/p/web2/login?execution")) {
            return ResponseReaderUtil.generateStringOfFile("responses/html/chooseAuth.html");
        } else if (referrer.contains("s3&") && referrer.contains("signin.ea.com/p/web2/login?execution")) {
            return ResponseReaderUtil.generateStringOfFile("responses/html/sendCode.html");
        } else {
            return null;
        }
    }

    /**
     * Post method to login. mainly used when logging in, selecting auth or entering code.
     */
    @PostMapping(value = "/signin.ea.com/p/web2/login{execution}{initref}", params = {"execution", "initref"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public void enterLoginDetails(@RequestHeader HttpHeaders requestHeaders, @RequestParam("execution") String execution,
                                  @RequestParam("initref") String initref, @RequestBody MultiValueMap<String, String> paramMap,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        this.localhost = "http://localhost:" + environment.getProperty("local.server.port");

        // If signing in
        if (paramMap.get("email") != null) {
            response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?execution=" + replaceExecution(execution) + "&initref=" + initref);
        }

        // If Selecting auth method
        if (paramMap.get("codeType") != null) {
            response.sendRedirect(localhost + ENDPOINT + "/signin.ea.com/p/web2/login?execution=" + replaceExecution(execution) + "&initref=" + initref);
        }

        // If entering code
        if (paramMap.get("oneTimeCode") != null) {
            String fid = RandomStringUtils.random(56, true, true);
            response.sendRedirect(localhost + ENDPOINT + "/accounts.ea.com/connect/auth?prompt=login&accessToken=null&client_id=FIFA-"
                    + Resources.FUT_YEAR + "-WEBCLIENT&response_type=token&display=web2%2Flogin&locale=en_US&redirect_uri=https%3A%2F%2Fwww.easports.com%2Fuk%2Ffifa%2Fultimate-team%2Fweb-app%2Fauth.html&scope=basic.identity+offline+signin&fid=" + fid);
        }
    }

    /**
     * Get the auth page which page contain errors.
     */
    @GetMapping("/www.easports.com/uk/fifa/ultimate-team/web-app/auth.html")
    public String getAuthPageHtml(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);

        return ResponseReaderUtil.generateStringOfFile("responses/html/authBearer.html");
    }

    /**
     * Increment the 's' count on the execution parameter
     *
     * @param execution Execution string to replace
     * @return replaced string
     */
    private String replaceExecution(String execution) {
        for (int i = 1; i < 6; i++) {
            if (execution.contains("s" + i)) {
                return execution.replaceAll("s" + i, "s" + (i + 1));
            }
        }

        return execution;
    }
}
