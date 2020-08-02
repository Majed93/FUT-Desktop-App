package com.fut.api.fut.requests;

import com.fut.desktop.app.domain.CreditsResponse;
import com.fut.desktop.app.exceptions.FutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * Currently not used
 */
@Getter
@Setter
@Slf4j
public class CreditsRequest extends FutRequestBase implements IFutRequest<CreditsResponse> {

    @Override
    public Future<CreditsResponse> PerformRequestAsync() throws FutException {
/*        String uri = baseResources.FutHome + Resources.Credits;

        if (baseAppVersion == AppVersion.WebApp) {
            addCommonHeaders(HttpMethod.GET);
        } else {
            addCommonMobileHeaders();
            uri += "?_=" + DateTimeExtensions.ToUnixTime(LocalDateTime.now());
        }

        try {
            HttpMessage creditsMsg = httpClient.GetAsync(uri).get();

            return new AsyncResult<>((CreditsResponse) deserializeAsync(creditsMsg, CreditsResponse.class).get());

        } catch (Exception e) {
            log.error("Unable to get credits! " + e.getMessage());
            return null;
        }*/
        throw new UnsupportedOperationException();
    }
}
