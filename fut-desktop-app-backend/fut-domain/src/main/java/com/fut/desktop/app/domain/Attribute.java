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
public class Attribute {

    private long index;

    private long value;
}
