package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class PackExt {
    private PackExtPrice finalPrice;

    private PackExtPrice originalPrice;
}