package com.fut.desktop.app.domain.pinEvents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@JsonPropertyOrder({"type", "status", "source", "custom", "msg_Id", "pgid", "toid", "userId", "core"})
public class Event {
    private String type;
    private String status;
    private String source;
    private String custom;
    private Long userId;
    private Core core;
    private Long msg_Id;
    private String pgid;
    private String toid;
}
