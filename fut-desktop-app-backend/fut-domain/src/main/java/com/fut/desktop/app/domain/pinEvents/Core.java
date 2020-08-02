package com.fut.desktop.app.domain.pinEvents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@JsonPropertyOrder({"s", "pidt", "pid", "pidm", "didm", "ts_event", "en", "dob"})
public class Core {
    private Integer s; //Pin count
    private String pidt;
    private Long pid;
    private Pidm pidm;
    private Didm didm;
    private String ts_event;
    private String en;
    private String dob;
}
