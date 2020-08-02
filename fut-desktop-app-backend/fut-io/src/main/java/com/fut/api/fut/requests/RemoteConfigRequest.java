package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.HttpHeaders;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

public class RemoteConfigRequest extends FutRequestBase implements IFutRequest<String> {

    @Override
    public Future<String> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().RemoteConfig;

        httpClient.clearRequestHeaders();
        httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");
        addAcceptHeader("*/*");

        if (getBaseAppVersion() == AppVersion.WebApp) {
            addAcceptEncodingHeader();
            addAcceptLanguageHeader();
            addUserAgent();
            addReferrerHeader(Resources.Referer);
            httpClient.addRequestHeader("Host", "www.easports.com");
        } else {
            addMobileUserAgent();
            addMobileAcceptLanguageHeader();
            addMobileAcceptEncodingHeader();
            addRequestWithHeader();
            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-cache");
            httpClient.addRequestHeader("Host", "fifa19.content.easports.com");
        }

        String response;
        try {
            //Send the GET request now.
            HttpMessage getResp = sendGetRequest(uri);
            response = getResp.getContent();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for remote config", ex);
        }

        return new AsyncResult<>(response);
    }
}
