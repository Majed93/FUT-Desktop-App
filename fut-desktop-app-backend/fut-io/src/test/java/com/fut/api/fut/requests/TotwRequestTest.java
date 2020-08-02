package com.fut.api.fut.requests;

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
import org.springframework.http.HttpMethod;

import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class TotwRequestTest extends FutRequestBaseTest{

    @InjectMocks
    private TotwRequest totwRequest;

    @Mock
    private Future<HttpMessage> message;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        totwRequest.setBaseAppVersion(AppVersion.WebApp);
        totwRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockTOTW();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        Boolean result = totwRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertTrue("Result not true. ", result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        when(mockResources.getFutHome()).thenReturn("futHome");

        doThrow(FutException.class).when(futRequestBase).sendGenericUtasOptions(any(Platform.class), anyString(), any(HttpMethod.class), anyBoolean());

        Boolean result = totwRequest.PerformRequestAsync().get();

        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyNoMoreInteractions(httpClient);
        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnGet() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        Object mockResponse = MockResponseUtils.generateMockTOTW();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).GetAsync(anyString());

        Boolean result = totwRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnGet() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        Object mockResponseFail = MockResponseUtils.generateMockTOTW();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        Boolean result = totwRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        Assert.assertNull(result);
    }
}
