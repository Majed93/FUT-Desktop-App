package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.RelistResponse;
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
public class ReListRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private ReListRequest reListRequest;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reListRequest.setBaseAppVersion(AppVersion.WebApp);
        reListRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        RelistResponse mockResponse = MockResponseUtils.generateMockRelist();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.PutAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), RelistResponse.class)).thenReturn(mockResponse);

        RelistResponse result = reListRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).PutAsync(anyString(), any(StringEntity.class));
        Assert.assertNotNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        RelistResponse result = reListRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnPut() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        RelistResponse mockResponse = MockResponseUtils.generateMockRelist();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).PostAsync(anyString(), any(StringEntity.class));

        RelistResponse result = reListRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnPut() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        RelistResponse mockResponseFail = MockResponseUtils.generateMockRelist();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);

        RelistResponse result = reListRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.PostAsync(anyString(), any(StringEntity.class)));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

}
