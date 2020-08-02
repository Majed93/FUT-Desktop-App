package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class MobileToken {
    private String additionalAccessTokens;
    private String code;
    private String Expires_In;
    private String Id_Token;
    private String originAccessToken;
    private String originIdToken;
    private String originRefreshToken;
    private Pid pid;
    private String Refresh_Token;
    private String Token_Type;

    private String Access_Token;
}
