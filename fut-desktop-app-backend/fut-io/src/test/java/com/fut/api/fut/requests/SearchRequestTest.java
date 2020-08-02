package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.parameters.Level;
import com.fut.desktop.app.parameters.PlayerSearchParameters;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class SearchRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private SearchRequest searchRequest = new SearchRequest(setParams(1));

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        searchRequest.setBaseAppVersion(AppVersion.WebApp);
        searchRequest.setBasePlatform(Platform.XboxOne);
        searchRequest.setPageSize(35);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockSearchResult();
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock options request
        testOptions();

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock get request
        when(httpClient.GetAsync(anyString())).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), AuctionResponse.class)).thenReturn(mockResponse);

        AuctionResponse result = searchRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verify(httpClient).GetAsync(anyString());
        Assert.assertNotNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        performRequestAsyncExceptionOnOptionOnGet();

        AuctionResponse result = searchRequest.PerformRequestAsync().get();

        performRequestAsyncExceptionOnOptionOnGetVerify();

        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnGet() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        AuctionResponse mockResponse = MockResponseUtils.generateMockSearchResult();
        String content = MockResponseUtils.objectToString(mockResponse);

        testOptions();

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).GetAsync(anyString());

        AuctionResponse result = searchRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnGet() throws Exception {
        HttpMessage mockMessageFail = mock(HttpMessage.class);
        AuctionResponse mockResponseFail = MockResponseUtils.generateMockSearchResult();
        String contentFail = MockResponseUtils.objectToString(mockResponseFail);

        testOptions();

        HttpClientTestUtils.mockFailedResponse(message, mockMessageFail, contentFail);
        when(httpClient.GetAsync(anyString())).thenReturn(message);

        AuctionResponse result = searchRequest.PerformRequestAsync().get();

        // Verify calls
        verify(httpClient).OptionAsync(anyString());
        verifyZeroInteractions(httpClient.GetAsync(anyString()));
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncOnPageNotSet() throws Exception {
        searchRequest.setPageSize(0);

        AuctionResponse result = searchRequest.PerformRequestAsync().get();

        verifyNoMoreInteractions(searchRequest.getPageSize());
        Assert.assertNull(result);
    }

    @Test
    public void testPageNumber() {
        // First page
        Integer expected = 0;
        Assert.assertEquals(expected, searchRequest.pageStart());

        //Second page
        expected = 25;
        searchRequest.setParameters(setParams(2));
        Assert.assertEquals(expected, searchRequest.pageStart());

        //Third page
        expected = 50;
        searchRequest.setParameters(setParams(3));
        Assert.assertEquals(expected, searchRequest.pageStart());

        // Automate to confirm it works for 20 pages?
        for (int i = 4; i < 50; i++) {
            // 50 = 2 * 26 - 2
            int pageStartNormalizer = i - 1;
            expected = pageStartNormalizer * 26 - pageStartNormalizer;
            searchRequest.setParameters(setParams(i));
            Assert.assertEquals(expected, searchRequest.pageStart());
        }
    }

    @Test
    public void testSetParameters() {
        searchRequest.setParameters(setParams(1));

        Assert.assertNotNull(searchRequest.getParameters());
        Assert.assertTrue(searchRequest.getParameters() instanceof PlayerSearchParameters);
    }

    private PlayerSearchParameters setParams(Integer pageNo) {
        PlayerSearchParameters params = new PlayerSearchParameters();
        params.setResourceId(123456);
        params.setMaxBuy(AuctionInfo.roundToNearest(5000));
        params.setPageSize((byte) 26);
        params.setLevel(Level.Gold);
        params.setPage(pageNo);
        return params;
    }
}
