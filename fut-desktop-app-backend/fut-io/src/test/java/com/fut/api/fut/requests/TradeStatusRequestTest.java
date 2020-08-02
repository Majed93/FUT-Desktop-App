package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.AuctionResponse;
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
public class TradeStatusRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private TradeStatusRequest tradeStatusRequest;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // For some reason base platform on this is null
        tradeStatusRequest.setBaseAppVersion(AppVersion.WebApp);
        tradeStatusRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void performRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockTradeStatus();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), AuctionResponse.class)).thenReturn(mockResponse);

        AuctionResponse result = tradeStatusRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertNotNull(result);
    }


    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        performRequestAsyncExceptionOnOptionOnGet();
        AuctionResponse result = tradeStatusRequest.PerformRequestAsync().get();

        performRequestAsyncExceptionOnOptionOnGetVerify();

        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnGet() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockTradeStatus();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).GetAsync(anyString());

        AuctionResponse result = tradeStatusRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnGet() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        AuctionResponse mockResponseFail = MockResponseUtils.generateMockTradeStatus();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        AuctionResponse result = tradeStatusRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }
}
