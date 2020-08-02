package com.fut.api.fut.requests;

import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.Future;

public class RemoveFromWatchlistRequest extends FutRequestBase implements IFutRequest<Boolean> {

    private Collection<Long> tradeIds;

    public RemoveFromWatchlistRequest(Collection<Long> tradeIds) {
        this.tradeIds = tradeIds;
    }

    @Override
    public Future<Boolean> PerformRequestAsync() throws FutException {
        String uri = baseResources.getFutHome() + Resources.WatchlistRemoveItem + StringUtils.collectionToCommaDelimitedString(tradeIds);
        Platform platform = getBasePlatform();

        if (getBaseAppVersion() == AppVersion.WebApp) {

            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.DELETE, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in remove item from watch list" + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the DELETE request now.
            sendDeleteRequest(uri, false);
            return new AsyncResult<>(true);
        } catch (Exception ex) {
            throw new FutException("Error sending DELETE request for remove item from watch list", ex);
        }
    }
}
