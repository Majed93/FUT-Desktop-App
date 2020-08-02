package com.fut.api.fut.httpWrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Wrapper to contain request and response
 */
@Getter
@Setter
@AllArgsConstructor
public class HttpMessage {

    private HttpUriRequest request;

    private HttpResponse response;

    private String content;
}
