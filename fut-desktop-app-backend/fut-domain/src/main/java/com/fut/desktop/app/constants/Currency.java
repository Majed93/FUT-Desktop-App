package com.fut.desktop.app.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum Currency {
    @JsonProperty("Coins")
    COINS(1),
    @JsonProperty("MTX")
    MTX(2);

    private long currency;
}
