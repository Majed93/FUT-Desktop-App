package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.services.base.IHasher;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import com.fut.desktop.app.utils.SleepUtil;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// For casting Future<HttpMessage> as mock
@SuppressWarnings({"unchecked", "JavadocReference"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateTimeExtensions.class)
public class LoginRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private LoginRequest loginRequest;

    @Mock
    private Future<PinResponse> mockPinResponse;

    @Mock
    private ITwoFactorCodeProvider iTwoFactorCodeProvider;

    @Mock
    private PinService pinService;

    @Mock
    private LoginDetails loginDetails;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IHasher iHasher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SleepUtil.setTimeoutFactor(0);

        loginRequest.setBaseAppVersion(AppVersion.WebApp);
        loginDetails.setAppVersion(AppVersion.WebApp);
        loginRequest.setBasePlatform(Platform.XboxOne);
        Resources resources = new Resources(AppVersion.WebApp);
        resources.FutHome = Resources.FutHomeXbox;
        resources.Home = Resources.Default_Home;
        loginRequest.setBaseResources(resources);
    }

    @Test
    public void testPerformRequestAsyncFullLogin() throws Exception {
        when(loginDetails.getPlatform()).thenReturn(loginRequest.getBasePlatform());

        testGetMainPage();
        testPinService();

        testSecondaryAuthToken();
        testIsLoggedIn();

        HttpMessage signMessage = testGetSignInPage();
        HttpMessage loginMessage = testLogin(signMessage);
        HttpMessage redirectResp = testLoginRedirect(loginMessage);
        HttpMessage selectedAuthResp = testSelectAuthMethod(redirectResp);
        testSetTwoFactorCodeAsync(selectedAuthResp);
        testGetNucleusId(false);

        testGatewaySub();
        testGetShards();
        testGetUserAccounts();
        testGetIdAuthCode();
        testGetSessionId();

        //Execute the test
        LoginResponse loginResponse = loginRequest.PerformRequestAsync().get();

        // Verify calls were made
        verify(iTwoFactorCodeProvider).getTwoFactorCodeAsync();
        verify(pinService, times(3)).sendPinEvent(any());

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.getNucleusId());
        Assert.assertNotNull(loginResponse.getShards());
        Assert.assertNotNull(loginResponse.getUserAccounts());
        Assert.assertNotNull(loginResponse.getSessionId());
        Assert.assertNotNull(loginResponse.getPhishingToken());
        Assert.assertNotNull(loginResponse.getPersonaId());
        Assert.assertNotNull(loginResponse.getDob());
        Assert.assertNotNull(loginResponse.getAuthBearerToken());
        Assert.assertNotNull(loginResponse.getLastLogin());
    }


    @Test
    public void testPerformRequestAsyncLoggedInBeforeOneHour() throws Exception {
        loginRequest.setBaseAuthBearerToken("QVQxOjEuMDozLjA6NjA6alY4MWd6TkJFaTdiRnRYbjRaeXpPZmJjaVJCT0ZXRTl6aEo6NzE1NzQ6bzRzazY");
        when(loginDetails.getPlatform()).thenReturn(loginRequest.getBasePlatform());

        testGetMainPage();

        testSecondaryAuthTokenOnlyCode();

        testGetNucleusId(false);
        testGatewaySub();
        testGetShards();
        testGetUserAccounts();
        testGetIdAuthCode();
        testGetSessionId();

        testPinService();


        //Execute the test
        LoginResponse loginResponse = loginRequest.PerformRequestAsync().get();

        // Verify calls were made
        verify(pinService, times(2)).sendPinEvent(any());

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.getNucleusId());
        Assert.assertNotNull(loginResponse.getShards());
        Assert.assertNotNull(loginResponse.getUserAccounts());
        Assert.assertNotNull(loginResponse.getSessionId());
        Assert.assertNotNull(loginResponse.getPhishingToken());
        Assert.assertNotNull(loginResponse.getPersonaId());
        Assert.assertNotNull(loginResponse.getDob());
        Assert.assertNotNull(loginResponse.getAuthBearerToken());
        Assert.assertNotNull(loginResponse.getLastLogin());
    }

    @Test
    public void testPerformRequestAsyncLoggedInAfterOneHour() throws Exception {
        loginRequest.setBaseAuthBearerToken("QVQxOjEuMDozLjA6NjA6alY4MWd6TkJFaTdiRnRYbjRaeXpPZmJjaVJCT0ZXRTl6aEo6NzE1NzQ6bzRzazY");
        when(loginDetails.getPlatform()).thenReturn(loginRequest.getBasePlatform());

        testGetMainPage();

        testSecondaryAuthTokenOnlyCode();

        testGetNucleusId(true);
        testIsLoggedIn();

        // mock cookies.
        BasicCookieStore mockBasicCookieStore = mock(BasicCookieStore.class);
        List<Cookie> cookies = new ArrayList<>();
        when(loginRequest.httpClient.getCookies()).thenReturn(mockBasicCookieStore);
        when(mockBasicCookieStore.getCookies()).thenReturn(cookies);
        when(httpClient.getContext()).thenReturn(mock(HttpClientContext.class));
        testHandleRedirect();
        testGatewaySub();
        testGetShards();
        testGetUserAccounts();
        testGetIdAuthCode();
        testGetSessionId();

        testPinService();

        //Execute the test
        LoginResponse loginResponse = loginRequest.PerformRequestAsync().get();

        // Verify calls were made
        verify(pinService, times(3)).sendPinEvent(any());

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.getNucleusId());
        Assert.assertNotNull(loginResponse.getShards());
        Assert.assertNotNull(loginResponse.getUserAccounts());
        Assert.assertNotNull(loginResponse.getSessionId());
        Assert.assertNotNull(loginResponse.getPhishingToken());
        Assert.assertNotNull(loginResponse.getPersonaId());
        Assert.assertNotNull(loginResponse.getDob());
        Assert.assertNotNull(loginResponse.getAuthBearerToken());
        Assert.assertNotNull(loginResponse.getLastLogin());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLoginDetails() {
        loginRequest = new LoginRequest(null, null, null);
    }

    @Test
    public void testSetCookies() {
        loginRequest.SetCookies(mock(BasicCookieStore.class));
    }

    @Test
    public void testGetSetLoginDetails() {
        loginRequest.setLoginDetails(loginDetails);
        Assert.assertNotNull(loginRequest.getLoginDetails());
    }

    @Test
    public void testGetSetITwoFactorCodeProvider() {
        loginRequest.setITwoFactorCodeProvider(iTwoFactorCodeProvider);
        Assert.assertNotNull(loginRequest.getITwoFactorCodeProvider());
    }

    @Test
    public void testGetSetIHasher() {
        loginRequest.setIHasher(iHasher);
        Assert.assertNotNull(loginRequest.getIHasher());
    }

    @Test
    public void testGetSetPersonaId() {
        loginRequest.setPersonaId("personaId");
        Assert.assertNotNull(loginRequest.getPersonaId());
    }

    @Test
    public void testGetSetPinService() {
        loginRequest.setPinService(pinService);
        Assert.assertNotNull(loginRequest.getPinService());
    }

    /**
     * Mock {@link LoginRequest#tryGetSecondaryAuthBearerToken()}
     * Because we're calling the same URL twice we need to handle this in a different way, when mocking need to return an error message first then a good message
     */
    private void testSecondaryAuthToken() throws Exception {
        String mockResponse = MockResponseUtils.generateMockIsLoggedIn();
        AuthToken mockResponseGood = MockResponseUtils.generateMockSecondaryAuthResponse();

        //Mock get request
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        when(message.get()).thenReturn(mockMessage);
        when(message.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(message.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(message.get().getResponse().getStatusLine().getStatusCode()).thenReturn(200);
        when(message.get().getContent()).thenReturn(content);

        Future<HttpMessage> messageGood = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessageGood = mock(HttpMessage.class);
        String contentGood = MockResponseUtils.objectToString(mockResponseGood);

        // Mock successful response.
        when(messageGood.get()).thenReturn(mockMessageGood);
        when(messageGood.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(messageGood.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(messageGood.get().getResponse().getStatusLine().getStatusCode()).thenReturn(200);
        when(messageGood.get().getContent()).thenReturn(contentGood);

        // Mock get request
        when(httpClient.GetAsync(contains(Resources.SecondaryAuthBearerToken))).thenReturn(message, messageGood);

        when(message.get()).thenReturn(mockMessage);

        when(mockMessage.getContent()).thenReturn(mockResponse);
        when(objectMapper.readValue(MockResponseUtils.objectToString(mockResponseGood), AuthToken.class)).thenReturn(mockResponseGood);
    }

    /**
     * Mock out secondary auth token request but only return the code
     */
    private void testSecondaryAuthTokenOnlyCode() throws Exception {
        AuthToken mockResponse = MockResponseUtils.generateMockSecondaryAuthResponse();

        //Mock options request
        HttpMessage messageFuture = performGet(mockResponse, Resources.SecondaryAuthBearerToken, false).get();

        when(messageFuture.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));
        when(objectMapper.readValue(MockResponseUtils.objectToString(mockResponse), AuthToken.class)).thenReturn(mockResponse);
    }

    /**
     * Gateway subscription response
     */
    private void testGatewaySub() throws Exception {
        String mockResponse = "{ }";

        //Mock options request
        String url = Resources.GatewaySubscriptionPid.split(Resources.GATEWAY_SUBSCRIPTION_REPLACE)[0];

        HttpMessage messageFuture = performGet(mockResponse, url, false).get();

        when(messageFuture.getContent()).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#getMainPage()}
     *
     * @throws Exception Handle any exceptions
     */
    private void testGetMainPage() throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);

        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockHomePage();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, MockResponseUtils.objectToString(mockResponse));

        message = performGet(mockResponse, loginRequest.baseResources.Home, false);
        when(message.get()).thenReturn(mockMessage);
    }

    /**
     * Mock {@link PinService}
     */
    private void testPinService() {
        when(pinService.sendPinEvent(any(PinEventId.class))).thenReturn(mockPinResponse);
    }

    /**
     * Mock {@link LoginRequest#isLoggedIn()}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testIsLoggedIn() throws Exception {
        String mockResponse = MockResponseUtils.generateMockIsLoggedIn();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = performGet(mockResponse, Resources.CheckLoggedIn, false).get();

        when(messageFuture.getContent()).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#setTwoFactorCodeAsync(HttpMessage)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testSetTwoFactorCodeAsync(HttpMessage selectedAuthResp) throws Exception {
        when(iTwoFactorCodeProvider.getTwoFactorCodeAsync()).thenReturn("123456");
        when(selectedAuthResp.getRequest()).thenReturn(mock(HttpUriRequest.class));
        when(selectedAuthResp.getRequest().getURI()).thenReturn(new URI("testUri"));
    }

    /**
     * Mock {@link LoginRequest#login(LoginDetails, HttpMessage)}
     *
     * @throws Exception Handle any exceptions.
     */
    private HttpMessage testLogin(HttpMessage signMessage) throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);

        Object mockResponse = MockResponseUtils.generateMockLoginHtmlBeforeRedirect();

        when(signMessage.getRequest()).thenReturn(mock(HttpUriRequest.class));
        when(signMessage.getRequest().getURI()).thenReturn(new URI("testUri"));

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, MockResponseUtils.objectToString(mockResponse));

        // Mock http client call
        when(httpClient.PostAsync(eq(signMessage.getRequest().getURI()), any(UrlEncodedFormEntity.class))).thenReturn(message);
        when(message.get()).thenReturn(mockMessage);
        return mockMessage;
    }

    /**
     * Mock {@link LoginRequest#loginRedirect(HttpMessage)}
     *
     * @throws Exception Handle any exceptions.
     */
    private HttpMessage testLoginRedirect(HttpMessage loginMessage) throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessageRedirect = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockEnterCodePage();

        // Html from the mock html file.
        String testUri = "https://signin.ea.com/p/web2/" +
                "login?execution=e1596340872s2&initref=https%3A%2F%2F" +
                "accounts.ea.com%3A443%2Fconnect%2Fauth%3Fprompt%3Dlogin%26accessToken%3D" +
                "null%26client_id%3DFIFA-19-WEBCLIENT%26response_type%3Dtoken%26display" +
                "%3Dweb2%252Flogin%26locale%3Den_US%26redirect_uri%3Dhttps%253A%252F%252F" +
                "www.easports.com%252Fuk%252Ffifa%252Fultimate-team%252Fweb-app%252Fauth.html%26release_type" +
                "%3Dprod%26scope%3Dbasic.identity%2Boffline%2Bsignin&_eventId=end";

        when(loginMessage.getRequest()).thenReturn(mock(HttpUriRequest.class));
        when(loginMessage.getRequest().getURI()).thenReturn(new URI(testUri));

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessageRedirect, MockResponseUtils.objectToString(mockResponse));

        message = performGet(mockResponse, testUri, false);
        when(message.get()).thenReturn(mockMessageRedirect);

        return mockMessageRedirect;
    }

    /**
     * Mock {@link LoginRequest#getNucleusId(String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetNucleusId(boolean forceNull) throws Exception {
        String mockResponse = MockResponseUtils.generateMockGatewayPid();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = mock(HttpMessage.class);
        if (forceNull) {
            Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
            String content = MockResponseUtils.objectToString(mockResponse);

            // Mock successful THEN 403 response.
            HttpClientTestUtils.mock403Response(message, messageFuture, content);

            // Mock get request
            when(httpClient.GetAsync(contains(Resources.Pid))).thenReturn(message);
            when(message.get()).thenReturn(messageFuture);
        } else {
            messageFuture = performGet(mockResponse, Resources.Pid, false).get();
        }

        when(messageFuture.getContent()).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#getShards(String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetShards() throws Exception {
        Shards mockResponse = MockResponseUtils.generateMockShards();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = performGet(mockResponse, loginRequest.baseResources.Shards, false).get();

        when(messageFuture.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));

        ObjectMapper mapper = mock(ObjectMapper.class);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        when(mapper.readValue(MockResponseUtils.objectToString(mockResponse), Shards.class)).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#getUserAccounts(Platform, String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetUserAccounts() throws Exception {
        UserAccounts mockResponse = MockResponseUtils.generateMockUserAccounts();

        mockTime();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = performGet(mockResponse, Resources.UTAS + loginRequest.baseResources.AccountInfo, false).get();
        HttpMessage messageFuture_s2 = performGet(mockResponse, Resources.UTAS_S2 + loginRequest.baseResources.AccountInfo, false).get();
        HttpMessage messageFuture_s3 = performGet(mockResponse, Resources.UTAS_S3 + loginRequest.baseResources.AccountInfo, false).get();


        switch (loginRequest.getBasePlatform()) {
            case XboxOne:
            case Xbox360:
                when(messageFuture_s3.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));
            case Pc:
                when(messageFuture.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));
            default:
                when(messageFuture_s2.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));
        }

        when(objectMapper.readValue(messageFuture.getContent(), UserAccounts.class)).thenReturn(mockResponse);
        when(objectMapper.readValue(messageFuture_s2.getContent(), UserAccounts.class)).thenReturn(mockResponse);
        when(objectMapper.readValue(messageFuture_s3.getContent(), UserAccounts.class)).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#getSessionId(UserAccounts, String, Platform)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetSessionId() throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);
        String mockResponse = MockResponseUtils.generateMockSessionId();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, mockResponse);

        // Mock http client call
        when(httpClient.PostAsync(contains(loginRequest.baseResources.Auth), any(StringEntity.class))).thenReturn(message);
        when(message.get()).thenReturn(mockMessage);
        when(mockMessage.getContent()).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#validate(String, String, String, Platform)}
     *
     * @throws Exception Handle any exceptions.
     */
    @Deprecated // Not used any more since phishing token is retrieved another way.
    private void testValidate() throws Exception {
        testGetAnswerPageNotAnswered();

        ValidateResponse mockResponse = MockResponseUtils.generateMockAnswerCorrect();
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);

        // Mock the hasher
        when(loginDetails.getSecretAnswer()).thenReturn("answer");
        when(iHasher.hash(anyString())).thenReturn("testhash");

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, MockResponseUtils.objectToString(mockResponse));

        // Mock http client call
        when(httpClient.PostAsync(contains(loginRequest.baseResources.Validate), any(StringEntity.class))).thenReturn(message);
        when(message.get()).thenReturn(mockMessage);

        testGetAnswerPage();
    }

    /**
     * Mock {@link LoginRequest#getAnswerPage(String, String, Platform, String)} not answering
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetAnswerPageNotAnswered() throws Exception {
        Object mockResponse = MockResponseUtils.generateMockNotAnsweredYet();

        //Mock options request
        testOptions();

        performGet(mockResponse, loginRequest.baseResources.Question, false).get();
    }

    /**
     * Mock {@link LoginRequest#getAnswerPage(String, String, Platform, String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetAnswerPage() throws Exception {
        ValidateResponse mockResponse = MockResponseUtils.generateMockAlreadyAnswered();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = performGet(mockResponse, loginRequest.baseResources.Question, false).get();

        when(messageFuture.getContent()).thenReturn(MockResponseUtils.objectToString(mockResponse));
    }

    /**
     * Mock {@link LoginRequest#getSignInPage()}
     *
     * @throws Exception Handle any exceptions.
     */
    private HttpMessage testGetSignInPage() throws Exception {
        Object mockResponse = MockResponseUtils.generateMockSignIn();
        Future<HttpMessage> messageFuture = performGet(mockResponse, loginRequest.baseResources.AuthCode, false);
        HttpMessage mockMessage = mock(HttpMessage.class);
        when(messageFuture.get()).thenReturn(mockMessage);
        return mockMessage;
    }

    /**
     * Mock {@link LoginRequest#selectAuthMethod(HttpMessage)}
     *
     * @throws Exception Handle any exceptions.
     */
    private HttpMessage testSelectAuthMethod(HttpMessage redirectResp) throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);

        when(redirectResp.getRequest()).thenReturn(mock(HttpUriRequest.class));
        when(redirectResp.getRequest().getURI()).thenReturn(new URI("testUri"));

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, "");

        // Mock http client call
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(message.get()).thenReturn(mockMessage);

        return mockMessage;
    }

    /**
     * Mock {@link LoginRequest#getIdAuthCode(String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testGetIdAuthCode() throws Exception {
        String mockResponse = MockResponseUtils.generateMockIDAuthCode();

        //Mock options request
        testOptions();

        HttpMessage messageFuture = performGet(mockResponse, Resources.AccountsAuthCode, false).get();

        when(messageFuture.getContent()).thenReturn(mockResponse);
    }

    /**
     * Mock {@link LoginRequest#handleRedirect(String)}
     *
     * @throws Exception Handle any exceptions.
     */
    private void testHandleRedirect() throws Exception {
        String uri = "https://accounts.ea.com/connect/auth?accessToken=" + loginRequest.getBaseAuthBearerToken();
        HttpResponse httpResponse = mock(HttpResponse.class);

        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);
        Header[] mockHeaders = new Header[1];
        String mockHeaderValue = "https://www.easports.com/uk/fifa/ultimate-team/web-app/auth.html#access_token=QVQxOjEuMDozLjA6NTk6Ulp1U3g3YUw5cjR2U1d2TzlwS0FDNFh3RkZSTEw5QmRiMEU6OTI0MzA6bzRzdms&token_type=Bearer&expires_in=3599";
        mockHeaders[0] = new BasicHeader(HttpHeaders.LOCATION, mockHeaderValue);
        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, "");

        // Mock get request
        when(httpClient.GetAsync(contains(uri))).thenReturn(message);

        when(message.get()).thenReturn(mockMessage);

        when(mockMessage.getResponse()).thenReturn(httpResponse);
        when(httpResponse.getHeaders(contains(HttpHeaders.LOCATION))).thenReturn(mockHeaders);
    }

    /**
     * Mock time
     */
    private void mockTime() {
        PowerMockito.mockStatic(DateTimeExtensions.class);
        when(DateTimeExtensions.ToUnixTime()).thenReturn(0L);
    }
}
