package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.PileSize;
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
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class PileSizeRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private PileSizeRequest pileSizeRequest;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pileSizeRequest.setBaseAppVersion(AppVersion.WebApp);
        pileSizeRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockPileSize();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        List<PileSize> result = pileSizeRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 1);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        List<PileSize> result = pileSizeRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnGet() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockPileSize();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).GetAsync(anyString());

        List<PileSize> result = pileSizeRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnGet() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        Object mockResponseFail = MockResponseUtils.generateMockPileSize();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        List<PileSize> result = pileSizeRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test
    public void testPerformRequestAsyncNoClientData() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockPileSizeNoClientData();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        Future<List<PileSize>> result = pileSizeRequest.PerformRequestAsync();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertNull(result);
    }

    @Test
    public void testPerformRequestAsyncNoEntries() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockPileSizeNoEntries();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        Future<List<PileSize>> result = pileSizeRequest.PerformRequestAsync();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertNull(result);
    }
}
