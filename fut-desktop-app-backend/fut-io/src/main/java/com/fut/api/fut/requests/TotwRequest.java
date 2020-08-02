package com.fut.api.fut.requests;

import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Will get TOTW.
 */
public class TotwRequest extends FutRequestBase implements IFutRequest<Boolean> {

    @Override
    public Future<Boolean> PerformRequestAsync() throws FutException {
        Platform platform = getBasePlatform();
        String uri = getBaseResources().getFutHome() + Resources.TOTW_URL + DateTimeExtensions.ToUnixTime();

        if (getBaseAppVersion() == AppVersion.WebApp) {
            // Options for TOTW
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, true);
            } catch (Exception e) {
                throw new FutException("Error sending options in totw." + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            // Send the GET for totw
            sendGetRequest(uri, true, false);
            return new AsyncResult<>(true);
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for totw, ex");
        }
    }
}
