package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum CardType {
    @JsonProperty("Gold")
    Gold,
    @JsonProperty("Silver")
    Silver,
    @JsonProperty("Bronze")
    Bronze
}
