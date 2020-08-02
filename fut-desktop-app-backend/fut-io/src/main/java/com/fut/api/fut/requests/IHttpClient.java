package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

import java.net.URI;
import java.util.concurrent.Future;

public interface IHttpClient {

    HttpClientContext getContext();

    BasicCookieStore getCookies();

    void setDoNotFollowRedirect();

    void setFollowRedirect();

    void setCookies(BasicCookieStore cookies);

    void clearRequestHeaders();

    void addRequestHeader(String name, String value);

    void removeRequestHeader(String name);

    void addConnectionKeepAliveHeader();

    void setReferrerUri(String value);

    Future<HttpMessage> GetAsync(String requestUri);

    Future<HttpMessage> PostAsync(String requestUri, HttpEntity requestEntity);

    Future<HttpMessage> PostAsync(URI requestUri, UrlEncodedFormEntity requestEntity);

    Future<HttpMessage> PutAsync(String requestUri, HttpEntity httpEntity);

    Future<HttpMessage> DeleteAsync(String requestUri);

    Future<HttpMessage> OptionAsync(String requestUri);

    /**
     * Used for images.
     *
     * @param requestUri endpoint
     * @return image in bytes
     */
    Future<HttpMessage> GetByteArrayAsync(String requestUri);

    void closeAll(HttpUriRequest request);
}
