package com.fut.desktop.app.domain.pinEvents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonFormat
@JsonPropertyOrder({"taxv", "tidt", "tid", "rel", "v", "ts_post", "sid", "gid", "plat", "et", "loc", "is_sess", "custom", "events"})
public class PinEvent {
    private double taxv;
    private String tidt;
    private String tid;
    private String rel;
    private String v;
    private String ts_post;
    private String sid;
    private Integer gid;//Only mobile
    private String plat;
    private String et;
    private String loc;
    private boolean is_sess;
    private Custom custom;
    private List<Event> events;

    @JsonProperty(value = "is_sess")
    public boolean is_sess() {
        return is_sess;
    }

    public void set_sess(boolean sess) {
        this.is_sess = sess;
    }

}
