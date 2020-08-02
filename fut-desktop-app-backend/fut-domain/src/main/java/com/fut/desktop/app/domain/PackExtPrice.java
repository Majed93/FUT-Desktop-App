package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class PackExtPrice {
    private long apple;

    private String externalEntitlement;

    private long externalPriceId;

    private String  groupName;

    private long MS;

    private long PC;

    private String productId;

    private long sony;
}
