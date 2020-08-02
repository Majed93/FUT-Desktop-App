package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.RelistResponse;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Handle re-list operations
 */
public class ReListRequest extends FutRequestBase implements IFutRequest<RelistResponse> {
    @Override
    public Future<RelistResponse> PerformRequestAsync() throws FutException {
        String uri = getBaseResources().getFutHome() + Resources.Auctionhouse + Resources.ReList;
        Platform platform = getBasePlatform();
        RelistResponse relistResponse;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            // Send the options request
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.PUT, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in relist: " + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the PUT request now.
            HttpMessage putResp = sendPutRequest(uri, " ", false, true);

            relistResponse = (RelistResponse) deserializeAsync(putResp, RelistResponse.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending PUT request for re-listing ALL", ex);
        }

        return new AsyncResult<>(relistResponse);
    }
}
