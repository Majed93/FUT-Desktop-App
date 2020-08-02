package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
@AllArgsConstructor
public class PileItem {
    private long id;
    private String pile;
    private Boolean success;
    private int errorCode;
    private String reason;
}
