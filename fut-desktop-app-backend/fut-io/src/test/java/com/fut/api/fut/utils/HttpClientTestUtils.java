package com.fut.api.fut.utils;

import com.fut.api.fut.httpWrappers.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class HttpClientTestUtils {

    /**
     * Helper function to return Successful responses (HttpStatus.OK) for option request.
     *
     * @param message     A mock of the future message object.
     * @param mockMessage A mock of the message
     * @throws Exception Handle any exception
     */
    public static void mockSuccessfulResponseForOptions(Future<HttpMessage> message, HttpMessage mockMessage) throws Exception {
        when(message.get()).thenReturn(mockMessage);
        when(message.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(message.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(message.get().getResponse().getStatusLine().getStatusCode()).thenReturn(200);
    }

    /**
     * Helper function to return Successful responses (HttpStatus.OK)
     *
     * @param message     A mock of the future message object.
     * @param mockMessage A mock of the message
     * @throws Exception Handle any exception
     */
    public static void mockSuccessfulResponse(Future<HttpMessage> message, HttpMessage mockMessage, String content) throws Exception {
        when(message.get()).thenReturn(mockMessage);
        when(message.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(message.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(message.get().getResponse().getStatusLine().getStatusCode()).thenReturn(200);
        when(message.get().getContent()).thenReturn(content);
    }

    /**
     * Helper function to return failed responses
     *
     * @param message     A mock of the future message object.
     * @param mockMessage A mock of the message
     * @throws Exception Handle any exception
     */
    public static void mockFailedResponse(Future<HttpMessage> message, HttpMessage mockMessage, String content) throws Exception {
        when(message.get()).thenReturn(mockMessage);
        when(message.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(message.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(message.get().getResponse().getStatusLine().getStatusCode()).thenReturn(500);
        when(message.get().getContent()).thenReturn(content);
    }

    /**
     * Helper function to return 403 responses
     *
     * @param message     A mock of the future message object.
     * @param mockMessage A mock of the message
     * @throws Exception Handle any exception
     */
    public static void mock403Response(Future<HttpMessage> message, HttpMessage mockMessage, String content) throws Exception {
        when(message.get()).thenReturn(mockMessage);
        when(message.get().getResponse()).thenReturn(mock(HttpResponse.class));
        when(message.get().getResponse().getStatusLine()).thenReturn(mock(StatusLine.class));
        when(message.get().getResponse().getStatusLine().getStatusCode()).thenReturn(200, 403, 200);
        when(message.get().getContent()).thenReturn(content);
    }
}
