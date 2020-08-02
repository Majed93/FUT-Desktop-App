package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.AuctionDetails;
import com.fut.desktop.app.domain.AuctionDuration;
import com.fut.desktop.app.domain.ListAuctionResponse;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class ListAuctionRequestTest extends FutRequestBaseTest {
    @Mock
    private AuctionDetails auctionDetails = new AuctionDetails(1234L, AuctionDuration.OneHour, 150, 10000L);

    @Mock
    private AuctionDetails auctionDetailsNullDuration = new AuctionDetails(1234L, null, 150, 10000L);

    @InjectMocks
    private ListAuctionRequest listAuctionRequest = new ListAuctionRequest(auctionDetails, true);

    @InjectMocks
    private ListAuctionRequest listAuctionRequestNoOption = new ListAuctionRequest(auctionDetailsNullDuration, false);

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    public ListAuctionRequestTest() throws Exception {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listAuctionRequest.setBaseAppVersion(AppVersion.WebApp);
        listAuctionRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        // Generate a list item thing
        ListAuctionResponse mockResponse = MockResponseUtils.generateMockListItem();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock post request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), ListAuctionResponse.class)).thenReturn(mockResponse);


        ListAuctionResponse result = listAuctionRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));
        verify(httpClient, atLeast(10)).addRequestHeader(anyString(), anyString());
        Assert.assertNotNull(result);
    }

    @Test
    public void testPerformRequestAsyncWithNoOptionsAndNullDuration() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        // Generate a list item thing
        ListAuctionResponse mockResponse = MockResponseUtils.generateMockListItem();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock post request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), ListAuctionResponse.class)).thenReturn(mockResponse);

        // Different mock with sendOptions as false.
        ListAuctionResponse result = listAuctionRequestNoOption.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));
        verify(httpClient, atLeast(5)).addRequestHeader(anyString(), anyString());
        Assert.assertNotNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        ListAuctionResponse result = listAuctionRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnPost() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        ListAuctionResponse mockResponse = MockResponseUtils.generateMockListItem();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).PostAsync(anyString(), any(StringEntity.class));

        ListAuctionResponse result = listAuctionRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnPost() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        ListAuctionResponse mockResponseFail = MockResponseUtils.generateMockListItem();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);

        ListAuctionResponse result = listAuctionRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }
}
