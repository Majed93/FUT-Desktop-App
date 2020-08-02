package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonFormat
@ToString
public class AuthToken {
    private String access_token;

    private String expires_in;

    private String token_type;

    private String refresh_token;

    private String id_token;
}
