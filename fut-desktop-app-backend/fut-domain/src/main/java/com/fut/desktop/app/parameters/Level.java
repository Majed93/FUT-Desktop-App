package com.fut.desktop.app.parameters;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum Level {
    Any,
    Bronze,
    Silver,
    Gold,
    Special
}
