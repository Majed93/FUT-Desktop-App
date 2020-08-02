package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class ConsumablesDetails {
    private long count;

    private Integer discardValue;

    private long resourceId;

    private long untradeableCount;
}
