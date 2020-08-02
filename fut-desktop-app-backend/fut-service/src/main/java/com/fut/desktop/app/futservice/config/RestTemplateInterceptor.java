package com.fut.desktop.app.futservice.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Intercept all outgoing requests.
 */
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final static String LOCALHOST = "localhost";

    private Integer reqCount = 0;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Here check if it's login path or something and the reset the count?
        //  Only if it's going to localhost
        if (request.getURI().toString().contains(LOCALHOST)) {
            HttpHeaders headers = request.getHeaders();

            headers.add("FUT-DA", "random: " + reqCount);
        }
        reqCount++;
        return execution.execute(request, body);
    }
}
