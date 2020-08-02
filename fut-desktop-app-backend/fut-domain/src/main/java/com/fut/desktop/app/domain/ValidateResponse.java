package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class ValidateResponse {
    private String debug;

    private String string;

    private String reason;

    private int code;

    private String token;
}
