package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class SessionAuthResponse {
    private String protocol;

    private String ipPort;

    private String serverTime;

    private String lastOnlineTime;

    private String sid;

    private String phishingToken;
}
