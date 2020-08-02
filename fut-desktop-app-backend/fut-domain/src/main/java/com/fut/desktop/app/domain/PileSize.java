package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.constants.Pile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
@AllArgsConstructor
public class PileSize {

    private Pile key;

    private int value;
}
