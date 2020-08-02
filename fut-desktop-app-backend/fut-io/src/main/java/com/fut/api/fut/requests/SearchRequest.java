package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.parameters.SearchParameters;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class SearchRequest extends FutRequestBase implements IFutRequest<AuctionResponse> {

    private SearchParameters parameters;

    public SearchRequest(SearchParameters params) {
        this.parameters = params;
    }

    @Override
    public Future<AuctionResponse> PerformRequestAsync() throws FutException {
        if (pageSize < 1) {
            log.error("Page size is: " + getPageSize());
            throw new FutException("Page size not set");
        }

        parameters.setPageSize((byte) (this.getPageSize() + 1)); // this value is dynamic
        log.debug("Page size is: " + parameters.getPageSize());

        AuctionResponse auctionResponse;
        Platform platform = getBasePlatform();
        //page is 26 because it seems to have changed. NOTE: this number is dynamic and loaded in as above.
        // https://utas.external.s3.fut.ea.com/ut/game/fifa19/transfermarket?start=0&num=26&type=player&_=1509478297829
        String uri = Resources.FutHomeXbox + Resources.TransferMarket + "?start=" + pageStart() +
                "&num=" + parameters.getPageSize();
        uri = parameters.BuildUriString(uri);
        uri += "&_=" + DateTimeExtensions.ToUnixTime();

        if (getBaseAppVersion() == AppVersion.WebApp) {
            //Send options
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, true);
            } catch (Exception e) {
                throw new FutException("Error in sending options from market search: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the GET request now.
            HttpMessage getResp = sendGetRequest(uri, true, false);

            auctionResponse = (AuctionResponse) deserializeAsync(getResp, AuctionResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting market search", ex);
        }

        return new AsyncResult<>(auctionResponse);
    }

    /**
     * Get the page start number.
     *
     * @return Page start parameter.
     */
    public Integer pageStart() {
        if (parameters.getPage() < 2) {
            return 0;
        } else {
            return Math.toIntExact(((parameters.getPage() - 1) * parameters.getPageSize()) - (parameters.getPage() - 1));
        }
    }
}
