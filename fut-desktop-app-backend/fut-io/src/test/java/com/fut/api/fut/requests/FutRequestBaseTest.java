package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;

import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

// For casting Future<HttpMessage> as mock
@SuppressWarnings("unchecked")
public abstract class FutRequestBaseTest {

    @Mock
    FutRequestBase futRequestBase;

    @Mock
    HttpClientImpl httpClient;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    public static Resources mockResources = new Resources(AppVersion.WebApp);

    void setupMocks() {
        futRequestBase.setBaseResources(mockResources);
        futRequestBase.setBasePhishingToken("12345");
        futRequestBase.setBaseSessionId("ABCD");
        futRequestBase.setBaseNucleusId("12345-ABCD");
        futRequestBase.setBaseAppVersion(AppVersion.WebApp);
        futRequestBase.setBaseDob("1990-01");
        futRequestBase.setBasePlatform(Platform.XboxOne);
        futRequestBase.setHttpClient(httpClient);

    }

    /**
     * Helper function to test options
     *
     * @throws Exception Handle any exception
     */
    void testOptions() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        when(mockResources.getFutHome()).thenReturn("futHome");

        HttpClientTestUtils.mockSuccessfulResponseForOptions(message, mockMessage);
        when(httpClient.OptionAsync(anyString())).thenReturn(message);
    }

    /**
     * Helper function to perform getting request
     */
    Future<HttpMessage> performGet(Object mockResponse, String uri, boolean isMatcher) throws Exception {
        Future<HttpMessage> message = (Future<HttpMessage>) mock(Future.class);
        HttpMessage mockMessage = mock(HttpMessage.class);
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        if (isMatcher) {
            when(httpClient.GetAsync(anyString())).thenReturn(message);
        } else {
            when(httpClient.GetAsync(contains(uri))).thenReturn(message);
        }

        when(message.get()).thenReturn(mockMessage);
        return message;
    }

    /**
     * Perform mock calls on exception for OPTION request prior to GET request.
     */
    void performRequestAsyncExceptionOnOptionOnGet() {
        when(mockResources.getFutHome()).thenReturn("futHome");
    }

    /**
     * Verify calls on exception for OPTION request prior to GET request.
     */
    void performRequestAsyncExceptionOnOptionOnGetVerify() {
        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
    }

    /**
     * Helper function to test DELETE requests.
     *
     * @throws Exception Handle error
     */
    void performRequestAsyncDelete() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, "");

        // Mock delete request
        when(httpClient.DeleteAsync(anyString())).thenReturn(message);
    }

    /**
     * Verify calls for DELETE request.
     */
    void performRequestAsyncDeleteVerify() {
        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).DeleteAsync(anyString());
    }

    /**
     * Perform mocks for exception on OPTIONS request.
     *
     * @throws Exception Handle errors.
     */
    void performRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());
    }

    /**
     * Verify calls on exception on OPTIONS.
     */
    void performRequestAsyncExceptionOnOptionVerify() {
        verifyZeroInteractions(httpClient.DeleteAsync(anyString()));
        verifyNoMoreInteractions(httpClient);
    }

    /**
     * Perform mock calls for exception on DELETE request.
     *
     * @throws Exception Handle errors.
     */
    void performRequestAsyncExceptionOnDelete() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, "");

        doThrow(Exception.class).when(httpClient).DeleteAsync(anyString());
    }

    /**
     * Verify calls for exception on DELETE request.
     */
    void performRequestAsyncExceptionOnDeleteVerify() {
        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
    }

    /**
     * Perform mock calls for NOT success code on DELETE request.
     *
     * @throws Exception Handle errors.
     */
    void performRequestAsyncNotSuccessCodeOnDelete() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        String contentFail = "false";

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.DeleteAsync(anyString())).thenReturn(message);
    }

    /**
     * Helper function to verify calls on not success on DELETE request
     */
    void performRequestAsyncNotSuccessCodeOnDeleteVerify() {
        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.DeleteAsync(anyString()));
    }

    void testDeserializeAsync(Object object, Class<?> type) throws Exception {
        ObjectMapper mapper = mock(ObjectMapper.class);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }
}
