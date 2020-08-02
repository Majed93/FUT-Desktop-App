package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.SendToPile;
import com.fut.desktop.app.dto.PileItemDto;
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
public class SendToPileRequestTest extends FutRequestBaseTest {

    @Mock
    private PileItemDto pileItemDto = new PileItemDto(1234567890L, "trade", "1234567890");

    @InjectMocks
    private SendToPileRequest sendToPileRequest = new SendToPileRequest(pileItemDto, "trade", true);

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sendToPileRequest.setBaseAppVersion(AppVersion.WebApp);
        sendToPileRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void performRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        // Generate a list item thing
        SendToPile mockResponse = MockResponseUtils.generateMockMoveToTradeRequest();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock post request
        when(httpClient.PutAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), SendToPile.class)).thenReturn(mockResponse);

        SendToPile result = sendToPileRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).PutAsync(anyString(), any(StringEntity.class));
        verify(httpClient, atLeast(10)).addRequestHeader(anyString(), anyString());
        Assert.assertNotNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        SendToPile result = sendToPileRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnPut() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        SendToPile mockResponse = MockResponseUtils.generateMockMoveToTradeRequest();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).PostAsync(anyString(), any(StringEntity.class));

        SendToPile result = sendToPileRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnPut() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        SendToPile mockResponseFail = MockResponseUtils.generateMockMoveToTradeRequest();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);

        SendToPile result = sendToPileRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }
}
