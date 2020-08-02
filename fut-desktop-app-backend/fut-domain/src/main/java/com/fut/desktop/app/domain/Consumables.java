package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class Consumables {
    private ConsumablesCategory type;

    private int contextId;

    private int contextValue;

    private int typeValue;
}
