package com.fut.desktop.app.futservice.service.impl;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.futservice.service.base.ErrorHandlerService;
import com.fut.desktop.app.futservice.service.base.StatusMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ErrorHandlerService}
 */
@Service
public class ErrorHandlerServiceImpl implements ErrorHandlerService {

    /**
     * Handle to {@link StatusMessagingService}
     */
    private final StatusMessagingService statusMessagingService;

    /**
     * Captcha needed message
     */
    public final static String CAPTCHA_NEEDED_MSG = "Captcha Verification is required. Please login to the webapp to manually resolve this.";

    /**
     * Soft ban message
     */
    public final static String SOFT_BAN_MSG = "Oops, looks like you've made too many requests. You've been soft banned. Please try again in about 10/15 minutes.";

    /**
     * Token expired message
     */
    public final static String TOKEN_EXPIRED_MSG = "Token expired. Please login to your account again";

    /**
     * Constructor.
     *
     * @param statusMessagingService Handle to {@link StatusMessagingService}
     */
    @Autowired
    public ErrorHandlerServiceImpl(StatusMessagingService statusMessagingService) {
        this.statusMessagingService = statusMessagingService;
    }

    @Override
    public boolean isMajorError(FutError futError, String endpoint) {
        if (futError == null || futError.getCode() == null) {
            return false;
        }

        FutErrorCode code = futError.getCode();
        boolean isError = false;
        switch (code) {
            case CaptchaTriggered:
                statusMessagingService.sendStatus(CAPTCHA_NEEDED_MSG, endpoint);
                isError = true;
                break;
            case UpgradeRequired:
                statusMessagingService.sendStatus(SOFT_BAN_MSG, endpoint);
                isError = true;
                break;
            case TooManyRequests:
                statusMessagingService.sendStatus(TOKEN_EXPIRED_MSG, endpoint);
                isError = true;
                break;
            default:
                break;
        }

        if (isError) {
            this.statusMessagingService.sendActionNeeded(code.getFutErrorCode());
        }

        return isError;
    }
}
