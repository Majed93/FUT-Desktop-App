package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class UserDataInfo {
    private String displayName;

    private String lastLogin;

    private String lastLogout;

    private String nucPersId;

    private String nucUserId;

    private List<UserDataInfoSettings> settings;

    private String sku;
}
