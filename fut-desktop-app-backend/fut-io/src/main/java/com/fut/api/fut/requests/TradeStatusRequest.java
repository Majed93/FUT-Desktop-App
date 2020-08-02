package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.HttpHeaders;
import com.fut.desktop.app.constants.NonStandardHttpHeaders;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.Future;

@AllArgsConstructor
public class TradeStatusRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {

    private final Collection<Long> tradeIds;

    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() +
                Resources.TradeStatus +
                StringUtils.collectionToCommaDelimitedString(tradeIds) +
                Resources.CLIENT_COMP_AMPS;
        Platform platform = getBasePlatform();
        AuctionResponse auctionResponse;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            try {
                addCommonHeaders(platform);
                addAcceptHeader("*/*");

                addAccessControlHeaders(HttpMethod.GET);
                httpClient.removeRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                httpClient.addRequestHeader(org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "cache-control,content-type,x-ut-sid");

                sendOptionsRequest(platform, uri);
            } catch (Exception e) {
                throw new FutException("Error sending options in trade status" + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            //Send the GET request now.
            removeAccessControlHeaders();

            addAcceptHeader("*/*");
            httpClient.addRequestHeader(HttpHeaders.ContentType, "application/json");

            if (getBaseAppVersion() == AppVersion.WebApp) {
                httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, getBaseSessionId());
            } else {
                // Mobile
                httpClient.addRequestHeader(NonStandardHttpHeaders.SessionId, getBaseMobileSessionId());
            }

            httpClient.addRequestHeader(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-cache");
            //Send the GET request now.
            HttpMessage getResp = sendGetRequest(uri);
            auctionResponse = (AuctionResponse) deserializeAsync(getResp, AuctionResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting trade status", ex);
        }


        return new AsyncResult<>(auctionResponse);
    }
}
