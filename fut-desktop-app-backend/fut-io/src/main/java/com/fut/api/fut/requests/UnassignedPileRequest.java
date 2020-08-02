package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.PurchasedItemsResponse;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Handle unassigned pile request operation
 */
public class UnassignedPileRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {
    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() + Resources.PurchasedItems;
        Platform platform = getBasePlatform();
        AuctionResponse unassignedPile;

        // Send the options request
        if (getBaseAppVersion() == AppVersion.WebApp) {
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in unassigned pile: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            HttpMessage getMessage = sendGetRequest(uri, false, true);
            PurchasedItemsResponse purchasedItemsResponse = (PurchasedItemsResponse) deserializeAsync(getMessage, PurchasedItemsResponse.class).get();

            List<AuctionInfo> auctionInfos = new ArrayList<>();

            purchasedItemsResponse.getItemData().forEach(itemData -> {
                AuctionInfo auctionInfo = new AuctionInfo();
                auctionInfo.setItemData(itemData);
                auctionInfos.add(auctionInfo);
            });

            unassignedPile = new AuctionResponse();
            unassignedPile.setAuctionInfo(auctionInfos);

        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting unassigned pile in request object.", ex);
        }

        return new AsyncResult<>(unassignedPile);
    }
}
