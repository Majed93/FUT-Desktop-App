package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
public class PinResponse {
    private String status;
}
