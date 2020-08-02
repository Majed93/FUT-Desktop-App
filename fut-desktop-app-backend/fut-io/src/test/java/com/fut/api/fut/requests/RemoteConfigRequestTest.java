package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Future;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class RemoteConfigRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private RemoteConfigRequest remoteConfigRequest;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        remoteConfigRequest.setBaseAppVersion(AppVersion.WebApp);
        remoteConfigRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        String mockResponse = "remoteResponse";
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(eq(remoteConfigRequest.baseResources.RemoteConfig))).thenReturn(message);

        String result = remoteConfigRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).GetAsync(eq(remoteConfigRequest.baseResources.RemoteConfig));
        Assert.assertNotNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnGet() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        String mockResponse = "remoteResponse";
        String content = MockResponseUtils.objectToString(mockResponse);

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).GetAsync(anyString());

        String result = remoteConfigRequest.PerformRequestAsync().get();

        // Verify calls
        verifyNoMoreInteractions(httpClient);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnGet() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        String mockResponseFail = "remoteResponse";
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        String result = remoteConfigRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).GetAsync(anyString());
        Assert.assertNull(result);
    }
}
