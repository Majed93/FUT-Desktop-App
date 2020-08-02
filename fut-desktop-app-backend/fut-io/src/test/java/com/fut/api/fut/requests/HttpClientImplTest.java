package com.fut.api.fut.requests;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class HttpClientImplTest {

    @Mock
    private CloseableHttpClient client;

    @Mock
    private HttpUriRequest request;

    @Mock
    private BasicCookieStore cookieStore;

    @InjectMocks
    private HttpClientImpl httpClient = new HttpClientImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        httpClient.setFollowRedirect();
        request = httpClient.getRequest();
    }

    @Test
    public void testClearRequestHeaders() throws Exception {
        Header[] headers = new Header[1];
        headers[0] = mock(Header.class);

        when(request.getAllHeaders()).thenReturn(headers);
//        doNothing().when(request).removeHeaders(headers[0].getName());
        httpClient.clearRequestHeaders();
    }

    @Test
    public void testAddRequestHeader() throws Exception {

    }

    @Test
    public void testRemoveRequestHeader() throws Exception {
    }

    @Test
    public void testAddConnectionKeepAliveHeader() throws Exception {
    }

    @Test
    public void testSetReferrerUri() throws Exception {
    }

    @Test
    public void testGetAsync() throws Exception {
    }

    @Test
    public void testPostAsyncString() throws Exception {
    }

    @Test
    public void testPostAsyncURI() throws Exception {

    }

    @Test
    public void testPutAsync() throws Exception {
    }

    @Test
    public void testDeleteAsync() throws Exception {
    }

    @Test
    public void testGetByteArrayAsync() throws Exception {
    }

}