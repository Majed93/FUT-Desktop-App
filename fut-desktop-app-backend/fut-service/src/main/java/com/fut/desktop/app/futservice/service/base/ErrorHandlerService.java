package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.domain.FutError;

/**
 * Contract to handle FUT-IO REST errors.
 */
public interface ErrorHandlerService {

    /**
     * Handle the futError from the FUT-IO rest call.
     * Depending on the error, if required, this method will return true which will tell the service to stop
     * all actions and notify the user to login to the webapp manually to ensure every thing is ok.
     *
     * @param futError The fut error to process
     * @param endpoint The endpoint to send the message to
     */
    boolean isMajorError(FutError futError, String endpoint);
}
