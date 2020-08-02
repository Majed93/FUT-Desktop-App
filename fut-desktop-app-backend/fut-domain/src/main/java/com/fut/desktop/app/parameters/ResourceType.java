package com.fut.desktop.app.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ResourceType {
    @JsonProperty("Player")
    Player,
    @JsonProperty("Staff")
    Staff,
    @JsonProperty("ClubInfo")
    ClubInfo,
    @JsonProperty("Training")
    Training,
    @JsonProperty("Development")
    Development,
    @JsonProperty("Stadium")
    Stadium,
    @JsonProperty("Ball")
    Ball
}
