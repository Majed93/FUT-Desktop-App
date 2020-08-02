package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.constants.AppVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
public class LoginDetails {

    private String username;

    private String password;

    private String secretAnswer;

    private Platform platform;

    private AppVersion appVersion;

    private String deviceId; //Might not need this.

    /**
     * used to track the last app version the user logged in to
     */
    private AppVersion lastUsedAppVersion;

}
