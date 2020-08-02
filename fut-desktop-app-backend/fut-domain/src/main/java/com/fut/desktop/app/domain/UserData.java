package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class UserData {
    private String count;

    private List<UserDataInfo> data;

    private String offset;

    private String totalRecords;
}
