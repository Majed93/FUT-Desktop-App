package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionDetails;
import com.fut.desktop.app.domain.AuctionDuration;
import com.fut.desktop.app.domain.ListAuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.dto.ListAuctionDto;
import com.fut.desktop.app.dto.ListAuctionItemDataDto;
import com.fut.desktop.app.exceptions.FutException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

@AllArgsConstructor
public class ListAuctionRequest extends FutRequestBase implements IFutRequest<ListAuctionResponse> {

    private final AuctionDetails auctionDetails;

    private final Boolean sendOptions;

    @Override
    public Future<ListAuctionResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() + Resources.Auctionhouse;
        Platform platform = getBasePlatform();
        ListAuctionResponse listAuctionResponse;

        // Create content
        ObjectMapper mapper = new ObjectMapper();

        ListAuctionItemDataDto itemData = new ListAuctionItemDataDto(auctionDetails.getItemDataId());
        itemData.setId(auctionDetails.getItemDataId());

        // If it's null then set to one hour by default
        if (auctionDetails.getAuctionDuration() == null) {
            auctionDetails.setAuctionDuration(AuctionDuration.OneHour);
        }

        ListAuctionDto listAuctionDto = new ListAuctionDto(itemData, auctionDetails.getStartingBid(), auctionDetails.getAuctionDuration().getDuration(),
                auctionDetails.getBuyNowPrice());
        String content;

        if (getBaseAppVersion() == AppVersion.WebApp) {

            if (sendOptions) {
                // Send options
                try {
                    sendGenericUtasOptions(platform, uri, HttpMethod.POST, false);
                } catch (Exception e) {
                    throw new FutException("Error sending options in list auction: " + e.getMessage());
                }
            } else {
                addCommonHeaders(platform);
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Send post request
        try {
            content = mapper.writeValueAsString(listAuctionDto);
            HttpMessage postResp = sendPostRequest(uri, content, false, false);
            listAuctionResponse = (ListAuctionResponse) deserializeAsync(postResp, ListAuctionResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending POST request for listing item", ex);
        }

        return new AsyncResult<>(listAuctionResponse);
    }
}
