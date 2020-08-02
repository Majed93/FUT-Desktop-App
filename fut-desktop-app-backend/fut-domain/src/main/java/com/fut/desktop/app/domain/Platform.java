package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@JsonFormat
@AllArgsConstructor
public enum Platform {
    Ps3("PS3"),
    Ps4("PS4"),
    Xbox360("Xbox 360"),
    XboxOne("Xbox One"),
    Pc("PC");

    private String platform;

    public static Platform getPlatform(String name) {
        return Arrays.stream(values()).filter(p -> p.getPlatform().equals(name)).collect(Collectors.toList()).get(0);
    }
}
