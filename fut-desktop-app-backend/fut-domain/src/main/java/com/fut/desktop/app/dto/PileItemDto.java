package com.fut.desktop.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class PileItemDto {
    private long id;
    private String pile;
    private String tradeId;
}
