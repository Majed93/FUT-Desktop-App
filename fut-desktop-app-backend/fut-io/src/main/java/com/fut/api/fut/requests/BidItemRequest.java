package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
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
public class BidItemRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {

    private long tradeId;

    private Long bidAmount;

    public BidItemRequest(long tradeId, Long bidAmount) {
        this.tradeId = tradeId;
        this.bidAmount = bidAmount;
    }

    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() +
                String.format(Resources.Bid, tradeId) + Resources.CLIENT_COMP_QUESTION;
        Platform platform = getBasePlatform();
        AuctionResponse auctionResponse;
        String content = "{\"bid\":" + bidAmount + "}";

        if (getBaseAppVersion() == AppVersion.WebApp) {
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.PUT, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in bid item: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            HttpMessage putResp = sendPutRequest(uri, content, false, true);
            auctionResponse = (AuctionResponse) deserializeAsync(putResp, AuctionResponse.class).get();
        } catch (Exception ex) {
            log.error("Error placing bid: " + ex.getMessage());
            throw new FutException("Error sending PUT request for putting bid item: ", ex);
        }

        return new AsyncResult<>(auctionResponse);
    }
}
