package com.fut.api.fut.requests;

import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

public class RemoveSoldRequest extends FutRequestBase implements IFutRequest<Boolean> {

    @Override
    public Future<Boolean> PerformRequestAsync() throws FutException {
        String uri = baseResources.getFutHome() + Resources.RemoveFromTradePile + "sold";
        Platform platform = getBasePlatform();

        if (getBaseAppVersion() == AppVersion.WebApp) {

            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.DELETE, true);
            } catch (Exception e) {
                throw new FutException("Error sending options in remove sold" + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        // Now send actual request
        try {
            sendDeleteRequest(uri, true);
            return new AsyncResult<>(true);

        } catch (Exception ex) {
            throw new FutException("Error sending DELETE request for getting remove sold", ex);
        }
    }
}
