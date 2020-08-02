package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class TradePileRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {

    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        String uri = baseResources.getFutHome() + baseResources.TradePile;
        Platform platform = getBasePlatform();
        AuctionResponse auctionResponse;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            // Send the options request
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in trade pile: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            HttpMessage getMessage = sendGetRequest(uri, false, true);
            auctionResponse = (AuctionResponse) deserializeAsync(getMessage, AuctionResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting trade pile", ex);
        }

        return new AsyncResult<>(auctionResponse);
    }
}
