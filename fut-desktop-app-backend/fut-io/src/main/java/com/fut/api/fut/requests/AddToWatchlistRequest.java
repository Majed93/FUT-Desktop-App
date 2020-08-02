package com.fut.api.fut.requests;

import com.fut.desktop.app.domain.AuctionInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 * NOTE: Currently not implemented
 */
@Slf4j
public class AddToWatchlistRequest extends FutRequestBase implements IFutRequest<Byte> {

    private Collection<AuctionInfo> auctionInfos;

    public AddToWatchlistRequest(Collection<AuctionInfo> auctionInfos) {
        this.auctionInfos = auctionInfos;
    }

    @Override
    public Future<Byte> PerformRequestAsync() {
        throw new UnsupportedOperationException();
        //Should only be one auction info because we're only adding one.
        /*String tradeIds = auctionInfos.stream().map(a -> String.valueOf(a.getTradeId())).collect(Collectors.joining("%2C"));

        // an object..?
        String content = "{{\"auctionInfo\":[{{\"id\":" + tradeIds + "}}]}}";
        URI uriString = URI.create(baseResources.FutHome + Resources.Watchlist + "?tradeId=" + tradeIds + "");

        if (getBaseAppVersion() == AppVersion.WebApp) {
            addCommonHeaders(HttpMethod.POST);
////////////////////////////CHECK HOW HEADERS GET SET CAUSE IDK!
            // HttpEntity<String> entity = new HttpEntity<>(content, headers);
            //httpClient.PostAsync(uriString, entity);
        } else if (getBaseAppVersion() == AppVersion.CompanionApp) {

        } else {
            log.error("App version not set in " + this.getClass());
        }

        return null;*/
    }
}
