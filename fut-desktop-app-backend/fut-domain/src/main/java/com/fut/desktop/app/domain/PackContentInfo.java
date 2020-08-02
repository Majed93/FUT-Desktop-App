package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class PackContentInfo {

    private byte bronzeQuantity;

    private byte goldQuantity;

    private byte itemQuantity;

    private byte rareQuantity;

    private byte silverQuantity;
}
