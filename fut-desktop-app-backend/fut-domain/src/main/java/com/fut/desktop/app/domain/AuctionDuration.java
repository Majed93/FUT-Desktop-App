package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum AuctionDuration {
    OneHour(3600),
    ThreeHours(10800),
    SixHours(21600),
    TwelveHours(43200),
    OneDay(86400),
    ThreeDays(259200);

    private int duration;

    @JsonCreator
    public static AuctionDuration fromValue(int duration) {
        for (AuctionDuration a : values()) {
            if (duration == a.duration) {
                return a;
            }
        }

        return null;
    }

    @JsonValue
    public int getDuration() {
        return duration;
    }
}
