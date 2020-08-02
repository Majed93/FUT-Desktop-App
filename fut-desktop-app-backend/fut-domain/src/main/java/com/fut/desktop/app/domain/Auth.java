package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class Auth {

    private String ipPort;

    private String lastOnlineTime;

    private String protocol;

    private String serverTime;

    private String sid;

    private String expiresOn;
}
