package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

public class WatchListRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {

    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() + Resources.Watchlist + DateTimeExtensions.ToUnixTime();
        Platform platform = getBasePlatform();

        AuctionResponse auctionResponse;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in watch list" + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the GET request now.
            HttpMessage getResp = sendGetRequest(uri, false, true);
            auctionResponse = (AuctionResponse) deserializeAsync(getResp, AuctionResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting watch list", ex);
        }


        return new AsyncResult<>(auctionResponse);
    }
}
