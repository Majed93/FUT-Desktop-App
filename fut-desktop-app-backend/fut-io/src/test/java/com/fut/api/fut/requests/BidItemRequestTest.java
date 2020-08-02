package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.apache.http.entity.StringEntity;
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

import java.util.concurrent.Future;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class BidItemRequestTest extends FutRequestBaseTest {

    // Only mock because it has constructors.
    @InjectMocks
    private BidItemRequest bidItemRequest = new BidItemRequest(123456789L, 500L);

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // For some reason base platform on this is null
        bidItemRequest.setBaseAppVersion(AppVersion.WebApp);
        bidItemRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockBidRequest();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock put request
        when(httpClient.PutAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), AuctionResponse.class)).thenReturn(mockResponse);

        AuctionResponse result = bidItemRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).PutAsync(anyString(), any(StringEntity.class));
        Assert.assertNotNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        AuctionResponse result = bidItemRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnPut() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockBidRequest();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).PostAsync(anyString(), any(StringEntity.class));

        AuctionResponse result = bidItemRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnPut() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        AuctionResponse mockResponseFail = MockResponseUtils.generateMockBidRequest();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);

        AuctionResponse result = bidItemRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test
    public void testSetTradeId() {
        long test = 12345L;
        bidItemRequest.setTradeId(test);

        Assert.assertEquals(test, bidItemRequest.getTradeId());
    }

    @Test
    public void testSetBidAmount() {
        Long test = 12345L;
        bidItemRequest.setBidAmount(test);

        Assert.assertEquals(test, bidItemRequest.getBidAmount());
    }
}
