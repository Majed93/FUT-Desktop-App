package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.scheduling.annotation.AsyncResult;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class HttpClientImpl implements IHttpClient {

    private CloseableHttpClient httpClient;

    private HttpClientContext context;

    private HttpUriRequest request;

    private BasicCookieStore cookies;

    public HttpClientImpl() {
        this.context = HttpClientContext.create();
        this.request = new HttpGet();
        if (cookies != null) {
            cookies.clear();
        }
    }

    @Override
    public void setDoNotFollowRedirect() {
        this.httpClient = HttpClients.custom().
                setDefaultCookieStore(cookies != null ? cookies : new BasicCookieStore()).
                setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build()).
                disableRedirectHandling().
                setProxy(new HttpHost("127.0.0.1", 8888)). //NOTE: If using fiddler
                build();
    }

    @Override
    public void setFollowRedirect() {
        this.httpClient = HttpClients.custom().
                setDefaultCookieStore(cookies != null ? cookies : new BasicCookieStore()).
                setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build()).
                setRedirectStrategy(new LaxRedirectStrategy()).
                setProxy(new HttpHost("127.0.0.1", 8888)). //NOTE: If using fiddler
                build();
    }

    @Override
    public void clearRequestHeaders() {
        Arrays.stream(request.getAllHeaders()).forEachOrdered(h -> request.removeHeaders(h.getName()));
    }


    @Override
    public void addRequestHeader(String name, String value) {
        request.addHeader(new BasicHeader(name, value));
    }

    @Override
    public void removeRequestHeader(String name) {
        Arrays.stream(request.getHeaders(name)).forEachOrdered(h -> request.removeHeaders(h.getName()));
    }

    /**
     * Remove duplicates headers.
     */
    private void removeDuplicateHeaders() {
        Set<Header> headers = new HashSet<>(Arrays.asList(request.getAllHeaders()));

        for (Header h : headers) {
            if (request.getHeaders(h.getName()).length > 1) {
                request.removeHeader(h);
            }
        }
    }

    @Override
    public void addConnectionKeepAliveHeader() {
        // NOTE: *************************************************************************************************
        // NOTE: This could actually be commented out because it seems like this header is automatically add. ****
        // NOTE: *************************************************************************************************
        request.addHeader(new BasicHeader(HttpHeaders.CONNECTION, HTTP.CONN_KEEP_ALIVE));
    }

    @Override
    public void setReferrerUri(String value) {
        removeRequestHeader(HttpHeaders.REFERER);
        request.addHeader(new BasicHeader(HttpHeaders.REFERER, value));
    }

    @Override
    public Future<HttpMessage> GetAsync(String requestUri) {
        HttpGet httpGet = new HttpGet(URI.create(requestUri));

        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpGet.setHeaders(request.getAllHeaders());

        HttpMessage message = null;
        try {
            message = execute(httpGet).get();
        } catch (Exception e) {
            log.error("Error getting: " + e.getMessage());
        }

        setCookies((BasicCookieStore) context.getCookieStore());
        return new AsyncResult<>(message);
    }


    @Override
    public Future<HttpMessage> PostAsync(String requestUri, HttpEntity requestEntity) {
        HttpPost httpPost = new HttpPost(URI.create(requestUri));
        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpPost.setEntity(requestEntity);
        httpPost.setHeaders(request.getAllHeaders());

        HttpMessage message;
        try {
            message = execute(httpPost).get();
        } catch (Exception e) {
            log.error("Error posting: " + e.getMessage());
            return null;
        }

        setCookies((BasicCookieStore) context.getCookieStore());
        return new AsyncResult<>(message);
    }

    @Override
    public Future<HttpMessage> PostAsync(URI requestUri, UrlEncodedFormEntity requestEntity) {
        HttpPost httpPost = new HttpPost(requestUri);
        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpPost.setEntity(requestEntity);
        httpPost.setHeaders(request.getAllHeaders());

        HttpMessage message;
        try {
            message = execute(httpPost).get();
        } catch (Exception e) {
            log.error("Error posting: " + e.getMessage());
            return null;
        }

        setCookies((BasicCookieStore) context.getCookieStore());
        return new AsyncResult<>(message);
    }

    @Override
    public Future<HttpMessage> PutAsync(String requestUri, HttpEntity httpEntity) {
        HttpPut httpPut = new HttpPut(URI.create(requestUri));
        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpPut.setEntity(httpEntity);
        httpPut.setHeaders(request.getAllHeaders());

        HttpMessage message;
        try {
            message = execute(httpPut).get();
        } catch (Exception e) {
            log.error("Error putting : " + e.getMessage());
            return null;
        }

        setCookies((BasicCookieStore) context.getCookieStore());
        return new AsyncResult<>(message);
    }

    @Override
    public Future<HttpMessage> DeleteAsync(String requestUri) {
        HttpDelete httpDelete = new HttpDelete(URI.create(requestUri));

        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpDelete.setHeaders(request.getAllHeaders());

        HttpMessage message;
        try {
            message = execute(httpDelete).get();
        } catch (Exception e) {
            log.error("Error deleting : " + e.getMessage());
            return null;
        }

        setCookies((BasicCookieStore) context.getCookieStore());
        return new AsyncResult<>(message);
    }

    @Override
    public Future<HttpMessage> OptionAsync(String requestUri) {
        HttpOptions httpOptions = new HttpOptions(URI.create(requestUri));
        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        request.removeHeaders("Cookie");
        httpOptions.setHeaders(request.getAllHeaders());

        HttpMessage message;
        try {
            message = execute(httpOptions).get();
        } catch (Exception e) {
            log.error("Error getting: " + e.getMessage());
            return null;
        }

        setCookies((BasicCookieStore) context.getCookieStore());

        return new AsyncResult<>(message);
    }

    @Override
    public Future<HttpMessage> GetByteArrayAsync(String requestUri) {
        HttpGet httpGet = new HttpGet(URI.create(requestUri));
        addConnectionKeepAliveHeader();
        removeDuplicateHeaders();

        httpGet.setHeaders(request.getAllHeaders());

        HttpMessage message = null;
        try {
            HttpResponse httpResponse = httpClient.execute(request, context);
            List<URI> redirects = context.getRedirectLocations();

            if (redirects != null) {
                httpGet.setURI(redirects.get(redirects.size() - 1));
            }

            message = new HttpMessage(request, httpResponse, Arrays.toString(IOUtils.toByteArray(httpResponse.getEntity().getContent())));
            closeAll(request);
        } catch (Exception e) {
            log.error("Error getting image? : " + e.getMessage());
        }
        log.info("If put doesn't work then look in HttpClientImpl.java: GetByteArray");

        return new AsyncResult<>(message);
    }

    @Override
    public void closeAll(HttpUriRequest req) {
        if (req instanceof HttpPost) {
            ((HttpPost) req).releaseConnection();
        }

        if (req instanceof HttpGet) {
            ((HttpGet) req).releaseConnection();
        }

        if (req instanceof HttpPut) {
            ((HttpPut) req).releaseConnection();
        }

        if (req instanceof HttpDelete) {
            ((HttpDelete) req).releaseConnection();
        }

        if (req instanceof HttpOptions) {
            ((HttpOptions) req).releaseConnection();
        }
    }


    /**
     * Helper method to handle request & response.
     *
     * @param request Request to execute. HttpGet or HttpPost
     * @return HttpMessage with response, request and content
     * @throws Exception errors.
     */
    private Future<HttpMessage> execute(HttpRequestBase request) throws Exception {
        // Ensure only one connection header

        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("application.properties")));

        String futSimEndpoint = config.getString("fut.simulator.endpoint");
        if (futSimEndpoint != null && !futSimEndpoint.isEmpty()) {
            // **************************************
            // ***** WIll request FUT Simulator *****
            // **************************************
            String currentUrl = request.getURI().toString();
            String delimiter = "https://";
            // https://utas.fut.external... replace with http://{fumsimurl}
            URI simUri = URI.create(currentUrl.replaceAll(delimiter, futSimEndpoint + "/"));
            request.setURI(simUri);
        }

        if (!request.getURI().toString().contains(Objects.requireNonNull(futSimEndpoint))) {
            log.error("shouldn't get here");
        }
        HttpResponse httpResponse = httpClient.execute(request, context);
        List<URI> redirects = context.getRedirectLocations();

        if (redirects != null && !redirects.isEmpty()) {
            if (request instanceof HttpOptions) {
                HttpOptions httpOptions = (HttpOptions) request;
                httpOptions.setURI(redirects.get(redirects.size() - 1));
                request = httpOptions;
            } else if (request instanceof HttpGet) {
                HttpGet httpGet = (HttpGet) request;
                httpGet.setURI(redirects.get(redirects.size() - 1));
                request = httpGet;
            } else if (request instanceof HttpPost) {
                HttpPost httpPost = (HttpPost) request;
                httpPost.setURI(redirects.get(redirects.size() - 1));
                request = httpPost;
            } else if (request instanceof HttpPut) {
                HttpPut httpPut = (HttpPut) request;
                httpPut.setURI(redirects.get(redirects.size() - 1));
                request = httpPut;
            } else if (request instanceof HttpDelete) {
                HttpDelete httpDelete = (HttpDelete) request;
                httpDelete.setURI(redirects.get(redirects.size() - 1));
                request = httpDelete;
            } else {
                throw new UnsupportedOperationException("Http request not supported..?");
            }
        }

        HttpMessage message = new HttpMessage(request, httpResponse, IOUtils.toString(httpResponse.getEntity().getContent(), CharEncoding.UTF_8));
        closeAll(request);

        return new AsyncResult<>(message);
    }
}
