package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class GiftMessage {

    private String message;

    private int id;

    private String type;

    private String time;

    private String startTime;

    private String rewardValue;

    private String rewardType;

    private String read;
    
}
