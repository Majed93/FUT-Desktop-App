package com.fut.desktop.app.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum FutErrorCode {
    ExpiredSession(401),
    NotFound(404),
    Conflict(409),
    UpgradeRequired(426),
    TooManyRequests(429),
    CaptchaTriggered(458),
    BadRequest(460),
    PermissionDenied(461),
    NotEnoughCredit(470),
    DestinationFull(473),
    InvalidDeck(477),
    NoSuchTradeExists(478),
    InternalServerError(500),
    ServiceUnavailable(503),
    PurchasedItemsFull(471);

    private int futErrorCode;

    @JsonCreator
    public static FutErrorCode fromValue(int futErrorCode) {
        for (FutErrorCode f : values()) {
            if (futErrorCode == f.futErrorCode) {
                return f;
            }
        }

        return FutErrorCode.InternalServerError;
    }

    @JsonValue
    public int getFutErrorCode() {
        return futErrorCode;
    }
}
