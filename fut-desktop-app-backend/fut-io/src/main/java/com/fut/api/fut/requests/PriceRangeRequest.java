package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.PriceRange;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Handle price range request operations
 */
public class PriceRangeRequest extends FutRequestBase implements IFutRequest<PriceRange[]> {

    private Long itemId;

    public PriceRangeRequest(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public Future<PriceRange[]> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() + Resources.PriceRange + itemId;
        Platform platform = getBasePlatform();
        PriceRange[] priceRange;

        if (getBaseAppVersion() == AppVersion.WebApp) {

            // Send the options request
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in price range: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            HttpMessage getMessage = sendGetRequest(uri, false, true);
            priceRange = (PriceRange[]) deserializeAsync(getMessage, PriceRange[].class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for getting price range", ex);
        }

        return new AsyncResult<>(priceRange);
    }
}
